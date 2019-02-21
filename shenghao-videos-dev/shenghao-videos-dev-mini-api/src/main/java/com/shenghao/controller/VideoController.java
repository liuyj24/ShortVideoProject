package com.shenghao.controller;

import com.shenghao.enums.VideoStatusEnum;
import com.shenghao.pojo.Bgm;
import com.shenghao.pojo.Comments;
import com.shenghao.pojo.Videos;
import com.shenghao.service.BgmService;
import com.shenghao.service.UserService;
import com.shenghao.service.VideoService;
import com.shenghao.utils.FetchVideoCover;
import com.shenghao.utils.IMoocJSONResult;
import com.shenghao.utils.MergeVideoMp3;
import com.shenghao.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@RestController
@Api(value = "视频相关业务的接口", tags = {"视频相关业务的controller"})
@RequestMapping("/video")
public class VideoController extends BasicController {

    @Autowired
    private UserService userService;

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "用户上传视频", notes = "用户上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "bgmId", value = "背景音乐Id", required = false, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "String", paramType = "form"),
            @ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form")
    })
    @PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
    @ResponseBody
    public IMoocJSONResult upload(String userId,
                                  String bgmId, double videoSeconds, int videoWidth, int videoHeight, String desc,
                                  @ApiParam(value = "短视频", required = true) MultipartFile file) throws Exception {//给上传的文件定义变量名file

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }
        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        String finalVideoPath = "";//文件的最终保存路径
        try {
            if (file != null) {
                //先拿到用户上传的文件名, 并判断文件的名字是否为空
                String fileName = file.getOriginalFilename();
                //截取视频的名字, 作为封面图片的名字
                String fileNamePrefix = fileName.split("\\.")[0];//注意要对点进行转义
                coverPathDB += ("/" + fileNamePrefix + ".jpg");

                if (StringUtils.isNotBlank(fileName)) {
                    //拼接文件的最终保存路径, 绝对路径
                    finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    //为新文件创建文件夹, 如果已经有就不创建了
                    File outFile = new File(finalVideoPath);//在磁盘上创建文件对象, 这个时候文件是空的
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    //把用户上传的文件拷贝到准备好的文件对象中
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                return IMoocJSONResult.errorMsg("上传出错...");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        //判断bgmId是否为空, 如果不为空,
        // 那就查询bgm的信息, 并且合成视频, 产生新的视频.
        if (StringUtils.isNotBlank(bgmId)) {
            Bgm bgm = bgmService.queryBgmById(bgmId);//拿到bgm的详细信息
            String mp3InputPath = FILE_SPACE + bgm.getPath();//Tomcat虚拟目录路径 + 相对路径 = 绝对路径
            MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);//合成 视频的工具

            String videoInputPath = finalVideoPath;
            String videoOutputName = UUID.randomUUID().toString() + ".mp4";//为合成的新视频起个名字
            uploadPathDB = "/" + userId + "/video/" + videoOutputName;//拼接新视频保存在数据库的相对路径
            finalVideoPath = FILE_SPACE + uploadPathDB;//最终合成的视频的绝对路径, (带上文件名)

            tool.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);//合并视频
        }

        //对视频进行截图
        FetchVideoCover fvc = new FetchVideoCover(FFMPEG_EXE);
        fvc.convertor(finalVideoPath, FILE_SPACE + coverPathDB);


        //保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);//设置背景音乐的id
        video.setUserId(userId);//设置发布者的id
        video.setVideoSeconds((float) videoSeconds);//设置视频的长度
        video.setVideoHeight(videoHeight);//设置视频的高
        video.setVideoWidth(videoWidth);//设置视频的宽
        video.setCoverPath(coverPathDB);
        video.setVideoDesc(desc);//设置视频的描述信息
        video.setVideoPath(uploadPathDB);//保存到数据库中的相对路径
        video.setStatus(VideoStatusEnum.SUCCESS.value);//设置视频的状态, 1.发布成功 2.禁止播放 3.管理员操作
        video.setCreateTime(new Date());//设置视频的生成时间

        String vedioId = videoService.saveVideo(video);

        return IMoocJSONResult.ok(vedioId);
    }

    /**
     * 分页和搜索查询视频列表
     * @param video
     * @param isSaveRecord 1-需要保存 0或为空-不需要保存
     * @param page
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/showAll")
    public IMoocJSONResult showAll(@RequestBody Videos video, Integer isSaveRecord, Integer page, Integer pageSize) throws Exception {

        if (page == null){
            page = 1;
        }

        if (pageSize == null){
            pageSize = PAGE_SIZE;
        }

        PagedResult result = videoService.getAllVideos(video, isSaveRecord, page, pageSize);
        return IMoocJSONResult.ok(result);
    }

    /**
     * @Description: 我关注的人发的视频
     */
    @PostMapping("/showMyFollow")
    @ResponseBody
    public IMoocJSONResult showMyFollow(String userId, Integer page) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }

        int pageSize = 6;

        PagedResult videosList = videoService.queryMyFollowVideos(userId, page, pageSize);

        return IMoocJSONResult.ok(videosList);
    }

    /**
     * 查询我点赞过的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     */
    @PostMapping("/showMyLike")
    @ResponseBody
    public IMoocJSONResult showMyLike(String userId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.errorMsg("用户id不能为空");
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 6;
        }

        PagedResult videosList = videoService.queryMyLikeVideos(userId, page, pageSize);

        return IMoocJSONResult.ok(videosList);
    }

    /**
     * 查询热搜词的接口
     * @return
     */
    @PostMapping(value = "/hot")
    public IMoocJSONResult hot(){
        return IMoocJSONResult.ok(videoService.getHotWords());
    }

    /**
     * 用户点赞
     * @param userId
     * @param videoId
     * @param videoCreaterId
     * @return
     */
    @PostMapping(value = "/userLike")
    public IMoocJSONResult userLike(String userId, String videoId, String videoCreaterId){
        videoService.userLikeVideo(userId, videoId, videoCreaterId);
        return IMoocJSONResult.ok();
    }

    /**
     * 用户取消点赞
     * @param userId
     * @param videoId
     * @param videoCreaterId
     * @return
     */
    @PostMapping(value = "/userUnLike")
    public IMoocJSONResult userUnLike(String userId, String videoId, String videoCreaterId){
        videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
        return IMoocJSONResult.ok();
    }

    /**
     * 保存留言
     * @param comment
     * @return
     */
    @PostMapping("/saveComment")
    public IMoocJSONResult saveComment(@RequestBody Comments comment, String fatherCommentId, String toUserId){

        if (fatherCommentId != null){
            comment.setFatherCommentId(fatherCommentId);
        }
        if (toUserId != null){
            comment.setToUserId(toUserId);
        }
        videoService.saveComment(comment);
        return IMoocJSONResult.ok();
    }
    @PostMapping("/getVideoComments")
    public IMoocJSONResult getVideoComments(String videoId, Integer page, Integer pageSize){
        if (StringUtils.isBlank(videoId)){
            return IMoocJSONResult.errorMsg("没有视频id");
        }
        //分页查询视频列表, 时间倒序排序
        if (page == null){
            page = 1;
        }
        if (pageSize == null){
            pageSize = 10;
        }
        PagedResult list = videoService.getAllComments(videoId, page, pageSize);
        return IMoocJSONResult.ok(list);
    }
}

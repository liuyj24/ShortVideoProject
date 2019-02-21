package com.shenghao.controller;

import com.shenghao.pojo.Users;
import com.shenghao.pojo.UsersReport;
import com.shenghao.pojo.vo.PublisherVideo;
import com.shenghao.pojo.vo.UsersVO;
import com.shenghao.service.UserService;
import com.shenghao.utils.IMoocJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@Api(value = "用户相关业务的接口", tags = {"用户相关业务的controller"})
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query")
    @PostMapping("/uploadFace")
    @ResponseBody
    public IMoocJSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[] files) throws Exception {//给上传的文件定义变量名file

        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }
        //文件保存的命名空间
        String fileSpace = "C:/workspace_wxxcx/shenghao_videos_dev";
        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/face";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;

        try {
            if (files != null && files.length > 0){
                //先拿到用户上传的文件名, 并判断文件的名字是否为空
                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)){
                    //拼接文件的最终保存路径, 绝对路径
                    String finalFacePath = fileSpace + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    //为新文件创建文件夹, 如果已经有就不创建了
                    File outFile = new File(finalFacePath);//在磁盘上创建文件对象, 这个时候文件是空的
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()){
                        //创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    //把用户上传的文件拷贝到准备好的文件对象中
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            }else{
                return IMoocJSONResult.errorMsg("上传出错...");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return IMoocJSONResult.errorMsg("上传出错...");
        }finally{
            if (fileOutputStream != null){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        //图片上传成功
        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);
        userService.updateUserInfo(user);

        return IMoocJSONResult.ok(uploadPathDB);
    }

    /**
     * 查询登陆用户的信息
     * @param userId
     * @return
     */
    @ApiOperation(value = "查询用户信息", notes = "查询用户信息的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "query")
    @PostMapping("/query")
    @ResponseBody
    public IMoocJSONResult query(String userId, String fanId) {
        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户id不能为空...");
        }
        Users userInfo = userService.queryUserInfo(userId);
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userInfo, userVO);

        userVO.setFollow(userService.queryIsFollow(userId, fanId));

        return IMoocJSONResult.ok(userVO);
    }

    @PostMapping("/queryPublisher")
    @ResponseBody
    public IMoocJSONResult queryPublisher(String loginUserId, String videoId, String publishUserId){
        if (StringUtils.isBlank(publishUserId)){
            return IMoocJSONResult.errorMsg("");
        }

        //1. 查询视频发布者的信息
        Users userInfo = userService.queryUserInfo(publishUserId);
        UsersVO publisher = new UsersVO();
        if (userInfo != null){
            BeanUtils.copyProperties(userInfo, publisher);
        }

        //2. 查询当前登陆者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);

        //3. 组合成返回的数据
        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);

        return IMoocJSONResult.ok(bean);
    }

    @PostMapping("/beYourFans")
    @ResponseBody
    public IMoocJSONResult beYourFans(String userId, String fanId){
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }
        userService.saveUserFanRelation(userId, fanId);

        return IMoocJSONResult.ok("关注成功...");
    }

    @PostMapping("/dontBeYourFans")
    @ResponseBody
    public IMoocJSONResult dontBeYourFans(String userId, String fanId){
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)){
            return IMoocJSONResult.errorMsg("");
        }
        userService.deleteUserFanRelation(userId, fanId);

        return IMoocJSONResult.ok("取消关注成功...");
    }
    @PostMapping("/reportUser")
    @ResponseBody
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport){
        //保存举报信息
        userService.reportUser(usersReport);

        return IMoocJSONResult.ok("举报成功, 有你的平台变得更美好...");
    }
}

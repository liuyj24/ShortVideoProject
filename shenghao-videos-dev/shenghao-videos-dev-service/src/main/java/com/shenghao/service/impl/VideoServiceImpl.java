package com.shenghao.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shenghao.mapper.*;
import com.shenghao.pojo.Comments;
import com.shenghao.pojo.SearchRecords;
import com.shenghao.pojo.UsersLikeVideos;
import com.shenghao.pojo.Videos;
import com.shenghao.pojo.vo.CommentsVO;
import com.shenghao.pojo.vo.VideosVO;
import com.shenghao.service.VideoService;
import com.shenghao.utils.PagedResult;
import com.shenghao.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired
    private Sid sid;

    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private VideosMapperCustom videosMapperCustom;

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private CommentsMapper commentsMapper;

    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Transactional(propagation = Propagation.REQUIRED)//如果当前没有事务就创建一个事务, 如果有事务就加入到事务中
    @Override
    public String saveVideo(Videos video) {
        String id = sid.nextShort();
        video.setId(id);
        videosMapper.insertSelective(video);//这个insert方法, 空的字段会使用数据库表中设置的默认值
        return video.getId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String coverPath) {
        Videos video = new Videos();
        video.setId(videoId);//设置主键
        video.setCoverPath(coverPath);//设置视频封面路径
        videosMapper.updateByPrimaryKeySelective(video);//根据主键去更新不为null的值
    }

    /**
     * 根据页数, 页的大小获得内容
     * @param video
     * @param isSaveRecord
     * @param page
     * @param pageSize
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)//要进行写操作
    @Override
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {

        //保存热搜词
        String desc = video.getVideoDesc();
        if(isSaveRecord != null && isSaveRecord == 1){
            SearchRecords records = new SearchRecords();//创建SearchRecords对象
            String recordId = sid.nextShort();//使用sid获得record的id
            records.setId(recordId);
            records.setContent(video.getVideoDesc());//设置内容为视频的描述
            searchRecordsMapper.insert(records);//保存
        }

        PageHelper.startPage(page, pageSize);//要分多少页, 每页大小
        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc);//查询数据库获得整张表
        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);//当前页数
        pagedResult.setTotal(pageList.getPages());//总页数
        pagedResult.setRows(list);//查询出来的每行内容
        pagedResult.setRecords(pageList.getTotal());//总记录数
        return pagedResult;
    }

    /**
     * 查询热搜词列表
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotWords();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
        //1. 保存用户和视频的喜欢点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        usersLikeVideosMapper.insert(ulv);

        //2. 视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(videoId);

        //3. 用户受喜欢数量累加
        usersMapper.addReceiveLikeCount(videoCreaterId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {
        //1. 删除用户和视频的喜欢点赞关联关系表
        Example example = new Example(UsersLikeVideos.class);
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);//第一个参数对应的是pojo类的属性, 而不是数据库中的字段名
        criteria.andEqualTo("videoId", videoId);
        usersLikeVideosMapper.deleteByExample(example);

        //2. 视频喜欢数量累减
        videosMapperCustom.reduceVideoLikeCount(videoId);

        //3. 用户受喜欢数量累减
        usersMapper.reduceReceiveLikeCount(videoCreaterId);
    }

    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);


        for (VideosVO vv : list){
            System.out.println(vv.getAudioId());
            System.out.println(vv.getUserId());
        }


        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveComment(Comments comment) {
        String id = sid.nextShort();
        comment.setId(id);
        comment.setCreateTime(new Date());
        commentsMapper.insert(comment);

    }
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);

        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);

        for (CommentsVO c : list){
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }
        PageInfo<CommentsVO> pageList = new PageInfo<>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());
        return grid;
    }
}

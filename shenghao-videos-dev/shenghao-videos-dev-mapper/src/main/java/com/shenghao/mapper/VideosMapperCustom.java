package com.shenghao.mapper;

import com.shenghao.pojo.Videos;
import com.shenghao.pojo.vo.VideosVO;
import com.shenghao.utils.MyMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideosMapperCustom extends MyMapper<Videos> {
    /**
     * 根据搜索内容查询视频列表(也可以没有搜索内容, 查询全部)
     * @param videoDesc
     * @return
     */
    public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc);

    /**
     * 对视频得到的点赞数进行累加
     * @param videoId
     */
    public void addVideoLikeCount(String videoId);

    /**
     * 对视频得到的点赞数进行累减
     * @param videoId
     */
    public void reduceVideoLikeCount(String videoId);

    /**
     * 查询我关注的视频
     * @param userId
     * @return
     */
    List<VideosVO> queryMyFollowVideos(String userId);

    /**
     * 查询点赞的视频
     * @param userId
     * @return
     */
    public List<VideosVO> queryMyLikeVideos(String userId);
}
package com.shenghao.service;

import com.shenghao.pojo.Comments;
import com.shenghao.pojo.Videos;
import com.shenghao.utils.PagedResult;

import java.util.List;

public interface VideoService {
    /**
     * 保存视频
     * @param video
     */
    public String saveVideo(Videos video);

    /**
     * 更新视频信息, 保存视频封面
     * @param videoId
     * @param coverPath
     * @return
     */
    public void updateVideo(String videoId, String coverPath);

    /**
     * 分页查询视频列表
     *
     * @param video
     * @param isSaveRecord
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize);

    /**
     * 获取热搜词列表
     * @return
     */
    public List<String> getHotWords();

    /**
     * 用户对视频点赞
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    public void userLikeVideo(String userId, String videoId, String videoCreaterId);

    /**
     * 用户对视频取消点赞
     * @param userId
     * @param videoId
     * @param videoCreaterId
     */
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId);

    /**
     * 查询我关注的视频
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult queryMyFollowVideos(String userId, Integer page, int pageSize);

    /**
     * 查询我喜欢的视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

    /**
     * 保存留言的接口
     * @param comment
     */
    void saveComment(Comments comment);

    /**
     * 按视频id查询所有的评论
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     */
    PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
}
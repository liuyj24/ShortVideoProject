package com.shenghao.service;

import com.shenghao.pojo.Bgm;
import com.shenghao.utils.PagedResult;

public interface VideoService {
    /**
     * 增加背景音乐
     * @param bgm
     */
    public void addBgm(Bgm bgm);

    /**
     * 查询bgm列表
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryBgmList(Integer page, Integer pageSize);

    /**
     * 删除bgm
     * @param id
     */
    public void deleteBgm(String id);

    /**
     * 查询举报视频列表
     * @param page
     * @param i
     * @return
     */
    PagedResult queryReportList(Integer page, int i);

    /**
     * 改变视频的状态, 是否禁播
     * @param videoId
     * @param value
     */
    void updateVideoStatus(String videoId, int value);
}

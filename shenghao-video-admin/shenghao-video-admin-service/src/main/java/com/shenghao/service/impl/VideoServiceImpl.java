package com.shenghao.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.shenghao.enums.BGMOperatorTypeEnum;
import com.shenghao.mapper.BgmMapper;
import com.shenghao.mapper.UsersReportMapperCustom;
import com.shenghao.mapper.VideosMapper;
import com.shenghao.pojo.Bgm;
import com.shenghao.pojo.BgmExample;
import com.shenghao.pojo.Videos;
import com.shenghao.pojo.vo.Reports;
import com.shenghao.service.VideoService;
import com.shenghao.service.util.ZKCurator;
import com.shenghao.utils.JsonUtils;
import com.shenghao.utils.PagedResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private BgmMapper bgmMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private ZKCurator zkCurator;

    @Autowired
    private UsersReportMapperCustom usersReportMapperCustom;

    @Autowired
    private VideosMapper videosMapper;

    @Override
    public PagedResult queryBgmList(Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);
        BgmExample example = new BgmExample();
        List<Bgm> list = bgmMapper.selectByExample(example);
        PageInfo<Bgm> pageList = new PageInfo<>(list);

        PagedResult result = new PagedResult();
        result.setTotal(pageList.getPages());
        result.setRows(list);
        result.setPage(page);
        result.setRecords(pageList.getTotal());

        return result;
    }

    /**
     * 添加bgm
     * @param bgm
     */
    @Override
    public void addBgm(Bgm bgm) {

        String bgmId = sid.nextShort();
        bgm.setId(bgmId);
        bgmMapper.insert(bgm);

        //准备把添加到节点的数据封住到json字符串中, 先使用map搞定数据
        Map<String, String> map = new HashMap<>();
        map.put("operType", BGMOperatorTypeEnum.ADD.type);//标识增加或删除
        map.put("path", bgm.getPath());//bgm的路径, 用于小程序后端删除

        zkCurator.sendBgmOperator(bgmId, JsonUtils.objectToJson(map));//zk监听添加
    }

    @Override
    public void deleteBgm(String id) {
        Bgm bgm = bgmMapper.selectByPrimaryKey(id);
        bgmMapper.deleteByPrimaryKey(id);

        //准备把添加到节点的数据封住到json字符串中, 先使用map搞定数据
        Map<String, String> map = new HashMap<>();
        map.put("operType", BGMOperatorTypeEnum.DELETE.type);//标识增加或删除
        map.put("path", bgm.getPath());//bgm的路径, 用于小程序后端删除

        zkCurator.sendBgmOperator(id, JsonUtils.objectToJson(map));//zk监听删除
    }

    @Override
    public PagedResult queryReportList(Integer page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Reports> reportsList = usersReportMapperCustom.selectAllVideoReport();
        PageInfo<Reports> pageList = new PageInfo<Reports>(reportsList);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(reportsList);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());
        return grid;
    }

    @Override
    public void updateVideoStatus(String videoId, int status) {
        Videos video = new Videos();
        video.setId(videoId);
        video.setStatus(status);
        videosMapper.updateByPrimaryKeySelective(video);
    }
}

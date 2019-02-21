package com.shenghao.mapper;

import com.shenghao.pojo.Comments;
import com.shenghao.pojo.vo.CommentsVO;
import com.shenghao.utils.MyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsMapperCustom extends MyMapper<Comments> {

    public List<CommentsVO> queryComments(String videoId);
}

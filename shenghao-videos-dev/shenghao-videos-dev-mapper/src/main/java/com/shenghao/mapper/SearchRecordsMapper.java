package com.shenghao.mapper;

import com.shenghao.pojo.SearchRecords;
import com.shenghao.utils.MyMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRecordsMapper extends MyMapper<SearchRecords> {

    public List<String> getHotWords();
}
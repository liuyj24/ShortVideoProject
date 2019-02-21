package com.shenghao.controller;

import com.shenghao.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

    @Autowired
    public RedisOperator redis;

    //用户登陆时保存在redis中的key的前缀
    public static final String USER_REDIS_SESSION = "user-redis-session";

    //文件保存的命名空间
    public static final String FILE_SPACE = "C:/workspace_wxxcx/shenghao_videos_dev";

    //ffepeg.exe工具所在的地址
    public static final String FFMPEG_EXE = "C:/ffmpeg/bin/ffmpeg.exe";

    //每页分页的记录数
    public static final Integer PAGE_SIZE = 5;
}

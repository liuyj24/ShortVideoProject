package com.shenghao;

import com.shenghao.config.ResourceConfig;
import com.shenghao.enums.BGMOperatorTypeEnum;
import com.shenghao.service.BgmService;
import com.shenghao.utils.JsonUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

@Component//被springboot扫描
public class ZKCuratorClient {
    @Autowired
    private ResourceConfig config;

    //zk客户端
    private CuratorFramework client = null;
    final static Logger log = LoggerFactory.getLogger(ZKCuratorClient.class);

    @Autowired
    private BgmService bgmService;

//    public static final String ZOOKEEPER_SERVER = "192.168.117.129:2181"; //使用了配置文件代替

    public void init(){
        if (client != null){
            return;
        }
        //重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);

        //创建zk客户端
        client = CuratorFrameworkFactory.builder().connectString(config.getZookeeperServer())
                .sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin").build();

        //启动客户端
        client.start();

        //测试, 看是否启动成功,能否获得数据
        try {
//            String testNodeData = new String(client.getData().forPath("/bgm/190124FCH948YRP0"));
//            log.info("测试的节点数据为{}", testNodeData);
              addChildWatch("/bgm");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addChildWatch(String nodePath) throws Exception{

        final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
        cache.start();
        //添加监听器
        cache.getListenable().addListener(new PathChildrenCacheListener() {//添加监听器
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){//判断事件的类型是不是添加节点

                    //1. 从数据库查询bgm对象, 获取路径path
                    //1. update 修改为从zk获取
                    String path = event.getData().getPath();//拿到节点的路径, admin管理后台
                    String operatorObjStr = new String(event.getData().getData(), "UTF-8");//拿到节点里面的值
                    Map<String, String> map = JsonUtils.jsonToPojo(operatorObjStr, Map.class);
                    String operatorType = map.get("operType");
                    String songPath = map.get("path");//bgm在管理后台的相对路径

//                    String[] arr = path.split("/");
//                    String bgmId = arr[arr.length - 1];//通过切割路径得到bgm的id

//                    Bgm bgm = bgmService.queryBgmById(bgmId);//根据bgmId到数据库中查询, 两个后端共用一个数据库
//                    if (bgm == null){
//                        return;
//                    }
//                    //1.1 bgm所在管理后台的相对路径
//                    String songPath = bgm.getPath();

                    //2. 定义保存到本地的bgm路径
//                    String filePath = "C:\\workspace_wxxcx\\shenghao_videos_dev" + songPath;//保存到小程序后端的地址
                    String filePath = config.getFileSpace() + songPath;//保存到小程序后端的地址

                    //3. 定义下载的路径(播放url)
//                    String[] arrPath = songPath.split("\\\\");//按照\\进行分割, 写四个\\才代表\\, 两个是转义, windows
                    String[] arrPath = songPath.split("/");//linux
                    String finalPath = "";
                    //3.1 处理url的斜杆以及编码
                    for (int i = 0; i < arrPath.length; i++){
                        if (StringUtils.isNotBlank(arrPath[i])){
                            finalPath += "/";//从admin的服务器拿数据, admin配置的是linux服务器
                            finalPath += URLEncoder.encode(arrPath[i], "UTF-8");//防止中文出错, 进行编码
                        }
                    }
//                    String bgmUrl = "http://192.168.164.1:8080/mvc" + finalPath;//最终下载bgm的路径, 指的是admin服务器中bgm的路径
                    String bgmUrl = config.getBgmServer() + finalPath;//最终下载bgm的路径, 指的是admin服务器中bgm的路径

                    if (operatorType.equals(BGMOperatorTypeEnum.ADD.type)){
                        //小程序后端进行添加
                        //下载bgm到springboot到springboot服务器
                        URL url = new URL(bgmUrl);//因为设置了编码, 所以转换为Url类, 指向的是小程序后端保存bgm的地址, 这个url在浏览器因该能直接播放
                        File file = new File(filePath);//小程序后端保存bgm的地址
                        FileUtils.copyURLToFile(url, file);//使用apache的common.io将bgm(filePath)下载到本地
                        //把zk上的节点删除
                        client.delete().forPath(path);
                    }else if (operatorType.equals(BGMOperatorTypeEnum.DELETE.type)){
                        //小程序后端进行删除
                        File file = new File(filePath);
                        FileUtils.forceDelete(file);
                        //把zk上的节点删除
                        client.delete().forPath(path);
                    }
                }
            }
        });

    }

}

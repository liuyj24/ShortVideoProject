package com.shenghao.service.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 操作zookeeper的工具类
 */
public class ZKCurator {

    //zk客户端
    private CuratorFramework client = null;

    //日志
    final static Logger log = LoggerFactory.getLogger(ZKCurator.class);

    public ZKCurator(CuratorFramework client){
        this.client = client;
    }

    /**
     * 初始化操作
     */
    public void init(){
        client = client.usingNamespace("admin");//设定命名空间, 因为zookeeper有可能是多个项目在用, 用命名空间方便管理

        try {
            // 判断在admin命名空间下是否有bgm节点, 所在路径大概是这样子:/admin/bgm
            if(client.checkExists().forPath("/bgm") == null){
                /**
                 * 如果节点为空就创建节点
                 * 对于zk有两种类型的节点
                 *   持久节点: 创建一个节点后, 节点就永远存在, 除非手动删除
                 *   临时节点: 创建一个节点后, 当会话断开, 节点会自动删除, 也可以手动删除
                 */
                client.create().creatingParentsIfNeeded() //递归创建
                        .withMode(CreateMode.PERSISTENT)    //节点类型: 持久节点
                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)//acl: 默认权限, 匿名权限
                        .forPath("/bgm");
                log.info("zookeeper客户端初始化成功...");

                log.info("zookeeper服务器状态: {}", client.isStarted());
            }
        } catch (Exception e) {
            log.error("zookeeper客户端连接初始化错误...");
            e.printStackTrace();
        }
    }


    /**
     * 增加或者删除bgm, 向zk-server创建子节点, 供小程序后端监听
     * @param bgmId
     * @param operObj
     */
    public void sendBgmOperator(String bgmId, String operObj){
        try {
            client.create().creatingParentsIfNeeded() //递归创建
                    .withMode(CreateMode.PERSISTENT)    //节点类型: 持久节点
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)//acl: 默认权限, 匿名权限
                    .forPath("/bgm/" + bgmId, operObj.getBytes());//bgm节点下面拼接上bgmId, 作为新子节点的路径
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

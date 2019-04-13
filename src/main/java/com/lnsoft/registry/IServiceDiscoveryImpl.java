package com.lnsoft.registry;

import com.lnsoft.loadbalance.LoadBalance;
import com.lnsoft.loadbalance.RandomLoadBalance;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.ArrayList;
import java.util.List;

/**
 * （2）订阅/选择/发现功能的测试
 * <p>
 * Created By Chr on 2019/4/12/0012.
 */
public class IServiceDiscoveryImpl implements IServiceDiscovery {

    List<String> repos = new ArrayList<>();

    private CuratorFramework curatorFramework;

    //构造方法连接
    public IServiceDiscoveryImpl() {
        //根据ZkConfig中的字符串初始化curatorFramework
        curatorFramework = CuratorFrameworkFactory.builder()
                .connectString(ZkConfig.CONNECTION_STR)
                .sessionTimeoutMs(4000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 10)).build();
        curatorFramework.start();
    }

    @Override
    public String doSubscribe(String serviceName) {
        //  /registrys/com.lnsoft.IChrHello
        String path = ZkConfig.ZK_REGISTER_PATH + "/" + serviceName;
        try {
            //  /registrys/com.lnsoft.IChrHello--->List   urls --->
            //url地址有多个（集群），需要监听和选择多个地址
            //有两个功能，选择那个地址进行调用（选择功能，选择哪一个，负载均衡），看urls地址是否变化，是否上下线（监听功能，监听urls）
            repos = curatorFramework.getChildren().forPath(path);  //订阅 监听的功能     服务  urls
        } catch (Exception e) {
            e.printStackTrace();
        }

        //订阅的实现：监听功能
        lookUp(path);

        //选择功能：负载均衡算法，客户端的负载均衡  url
        LoadBalance loadBalance = new RandomLoadBalance();

        return loadBalance.select(repos);
    }

    //监听功能：监听urls是否变化，就是活动是否上线/下线的监听
    private void lookUp(final String path) {
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, path, true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                //监听urls，如果repos有更新（新增或删除），urls就重新赋值
                repos = curatorFramework.getChildren().forPath(path);
            }
        };

        //监听器添加到getListenable中
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            //开启
            childrenCache.start();
        } catch (Exception e) {
            throw new RuntimeException(" 注册PathChild Watcher 异常 " + e);
        }
    }
}

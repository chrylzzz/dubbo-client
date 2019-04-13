package com.lnsoft.registry;

/**
 * Created By Chr on 2019/4/12/0012.
 */
public interface IServiceDiscovery {

    /**
     * 源码：包含两个功能lookup和doSubscribe
     * lookup()：监听
     * doSubscribe()：订阅
     *
     * @param serviceName
     * @return
     */
    //根据服务名称com.lnsoft.XXX   ---url地址 127.0.0.1:8080  Socket Netty
    String doSubscribe(String serviceName);
}

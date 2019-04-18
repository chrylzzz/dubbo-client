package com.lnsoft;


import com.lnsoft.proxy.RpcClientProxy;
import com.lnsoft.registry.IServiceDiscovery;
import com.lnsoft.registry.IServiceDiscoveryImpl;

/**
 * 测试客户端去调用rpc远程接口，是否能实现
 * <p>
 * Created By Chr on 2019/4/11/0011.
 */
public class ClientTest {

    public static void main(String args[]) {
        IServiceDiscovery serviceDiscovery = new IServiceDiscoveryImpl();

        //服务发现 放在动态代理中进行 Netty
        RpcClientProxy rpcClientProxy = new RpcClientProxy(serviceDiscovery);

        IChrHello iChrHello = rpcClientProxy.create(IChrHello.class);//远程通信，服务发现，netty走的url

        //本地调用方法，测试能否调用服务端的方法
        System.out.println(iChrHello.sayHello("Chr"));//远程调用，此时是服务端
    }
}

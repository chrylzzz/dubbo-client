package com.lnsoft;

import com.lnsoft.registry.IServiceDiscovery;
import com.lnsoft.registry.IServiceDiscoveryImpl;

/**
 * 测试注册功能是否成功，测试发现功能是否成功
 * 第二步：（2）选择/发现功能的测试
 * 第三步：（3）通知功能
 * Created By Chr on 2019/4/12/0012.
 */
public class Test {
    public static void main(String args[]) {
        IServiceDiscovery iServiceDiscovery = new IServiceDiscoveryImpl();
        System.out.println(iServiceDiscovery.doSubscribe("com.lnsoft.IChr"));
    }
}

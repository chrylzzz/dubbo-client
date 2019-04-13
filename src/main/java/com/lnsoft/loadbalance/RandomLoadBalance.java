package com.lnsoft.loadbalance;

import java.util.List;
import java.util.Random;

/**
 * 模拟负载均衡
 * <p>
 * Created By Chr on 2019/4/12/0012.
 */
public class RandomLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> repos) {
        int len = repos.size();

        Random random = new Random();
        return repos.get(random.nextInt(len));
    }
}

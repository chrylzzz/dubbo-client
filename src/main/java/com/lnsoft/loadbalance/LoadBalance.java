package com.lnsoft.loadbalance;

import java.util.List;

/**
 * Created By Chr on 2019/4/12/0012.
 */
public interface LoadBalance {

    //list--->choose one

    String select(List<String> repos);
}

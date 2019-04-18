package com.lnsoft.bean;

import java.io.Serializable;

/**
 * 发送和接口的数据包装:用来放rpc远程调用的参数，放客户端远程调用的方法的参数
 * <p>
 * Created By Chr on 2019/4/12/0012.
 */
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -5964079676431390729L;

    private String className;
    private String methodName;
    private Class<?>[] types;
    private Object[] params;


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getTypes() {
        return types;
    }

    public void setTypes(Class<?>[] types) {
        this.types = types;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}

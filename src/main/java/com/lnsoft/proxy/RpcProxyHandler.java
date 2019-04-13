package com.lnsoft.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 与服务端交互：Netty需要的handler
 * 拿到服务端，传递到客户端的内容
 * <p>
 * Created By Chr on 2019/4/12/0012.
 */
public class RpcProxyHandler extends ChannelInboundHandlerAdapter {

    private Object response;

    public Object getResponse() {
        return response;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //msg：拿到服务端写过来的内容
        response = msg;

    }
}

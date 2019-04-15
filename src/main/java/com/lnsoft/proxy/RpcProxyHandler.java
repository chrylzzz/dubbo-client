package com.lnsoft.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * （4）调用：获取服务端传过来的信息：
 * 客户端自定义的handler：客户端与服务端进行交互，io交互，继承ChannelInboundHandlerAdapter
 * 覆写channelRead。
 * <p>
 * 该类是客户端往服务端传送数据的--发送类
 * <p>
 * Created By Chr on 2019/4/12/0012.
 */
public class RpcProxyHandler extends ChannelInboundHandlerAdapter {

    private Object response;

    public Object getResponse() {
        return response;
    }

    /**
     * @param ctx 发送数据
     * @param msg 接收数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        //msg：拿到服务端写过来的内容
        response = msg;

    }
}

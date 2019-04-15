package com.lnsoft.proxy;

import com.lnsoft.bean.RpcRequest;
import com.lnsoft.registry.IServiceDiscovery;
import com.lnsoft.registry.IServiceDiscoveryImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.Proxy;

/**
 * （4）调用
 * 与服务端交互：
 * 客户端往服务端连接并且发送数据:
 * <p>
 * Created By Chr on 2019/4/12/0012.
 */
public class RpcClientProxy {
    //百度
    IServiceDiscovery serviceDiscovery = new IServiceDiscoveryImpl();

    public RpcClientProxy(IServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    //一定要是通用的  interfaceClass IChrHello.class
    public <T> T create(final Class<T> interfaceClass) {
        //这里诗级上市封装RpcRequest请求对象，然后通过Netty发送给服务端
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),//
                new Class<?>[]{interfaceClass}, (proxy, method, args) -> {
                    //封装RpcRuqest
                    RpcRequest rpcRequest = new RpcRequest();
                    rpcRequest.setClassName(method.getDeclaringClass().getName());
                    rpcRequest.setMethodName(method.getName());
                    rpcRequest.setTypes(method.getParameterTypes());
                    rpcRequest.setParams(args);

                    //服务发现，因为接下来需要进行通信了，IChrService
                    String serviceName = interfaceClass.getName();
                    //url地址
                    String serviceAddress = serviceDiscovery.doSubscribe(serviceName);

                    //解析host和ip
                    String[] arrs = serviceAddress.split(":");

                    String host = arrs[0];
                    int port = Integer.parseInt(arrs[1]);
                    //Socket  Netty 进行连接  Socket(ip,port)---->Netty

                    final RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();
                    //通过netty方式进行连接和发送
                    EventLoopGroup group = new NioEventLoopGroup();
                    try {
                        Bootstrap bootstrap = new Bootstrap();
                        bootstrap.group(group).channel(NioSocketChannel.class)
                                .option(ChannelOption.TCP_NODELAY, true)//百度是true还是false
                                .handler(new ChannelInitializer<SocketChannel>() {
                                    @Override
                                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                                        ChannelPipeline pipeline = socketChannel.pipeline();

//                            pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,0,));
//                            pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
//                            pipeline.addLast("encoder",new ObjectEncoder());
//                            pipeline.addLast("decoder",new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.));

                                        //百度,第三个参数开始：百度的4,0,4
                                        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                                        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                                        pipeline.addLast("encoder", new ObjectEncoder());
                                        //百度,第2个参数开始：百度的.cacheDisabled(null)
                                        pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                                        /**
                                         * 通过handler进行数据io交互
                                         */
                                        pipeline.addLast(rpcProxyHandler);//单例，保持同一个channel
                                    }
                                });
                        /**
                         * 连接服务 地址host+port
                         */
                        ChannelFuture future = bootstrap.connect(host, port).sync();
                        //将封装好的rpcRequest 对象写过去-》服务端数据的返回
                        //用netty方式将数据写回到服务端
                        future.channel().writeAndFlush(rpcRequest);
                        future.channel().closeFuture().sync();
                    } catch (Exception e) {
                        //视频，这里没有捕获异常
                        e.printStackTrace();
                    } finally {
                        //关流
                        group.shutdownGracefully();
                    }

                    //数据返回给动态代理的调用者
                    return rpcProxyHandler.getResponse();
                });
    }


//    new InvocationHandler() {
//        @Override
//        public Object invoke (Object proxy, Method method, Object[]args) throws Throwable {
//            return null;
//        }
//    }
    //###############################//
//    (proxy, method, args) -> {
//
//    }
}

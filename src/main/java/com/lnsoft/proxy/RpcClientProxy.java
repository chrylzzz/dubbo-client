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

    //动态代理调用方法
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
//                                        可知当添加到不同管线的是不同的实例时候，不同连接在检查时候h.added总是返回的false，所以不会抛出异常。
//                                        当添加到不同管线的是同一个实例时候，由于是单例，所以第一个连接会把单例的对象的added设置为了true，所以其他连接检查时候发现没有添加@Sharable注解并且当前added为true则会抛出异常。
//                                        正常情况下同一个ChannelHandler,的不同的实例会被添加到不同的Channel管理的管线里面的，但是如果你需要全局统计一些信息，比如所有连接报错次数（exceptionCaught）等，
//                                        这时候你可能需要使用单例的ChannelHandler，需要注意的是这时候ChannelHandler上需要添加@Sharable注解

                                        pipeline.addLast(rpcProxyHandler);
                                    }
                                });
                        /**
                         * 连接服务 地址host+port
                         */
                        ChannelFuture future = bootstrap.connect(host, port).sync();
                        //异步处理的返回
//                        通过 isDone 方法来判断当前操作是否完成。
//                        通过 isSuccess 方法来判断已完成的当前操作是否成功。
//                        通过 getCause 方法来获取已完成的当前操作失败的原因。
//                        通过 isCancelled 方法来判断已完成的当前操作是否被取消。
//                        通过 addListener 方法来注册监听器，当操作已完成(isDone 方法返回完成)，将会通知指定的监听器；如果 Future 对象已完成，则理解通知指定的监听器。
//                        if (future.isSuccess()){
//                            System.out.println("绑定端口成功:"+port);
//                        }

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

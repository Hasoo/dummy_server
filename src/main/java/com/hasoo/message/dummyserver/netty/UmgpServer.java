package com.hasoo.message.dummyserver.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.LineSeparator;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class UmgpServer {
  private int port;

  public UmgpServer(int port) {
    this.port = port;
  }

  public void run() throws Exception {
    EventLoopGroup serverGroup = new NioEventLoopGroup();
    EventLoopGroup childGroup = new NioEventLoopGroup();

    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(serverGroup, childGroup).channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new LineBasedFrameDecoder(4096)).addLast(new UmgpServerHandler())
                .addLast(new ReadTimeoutHandler(10))
                .addLast(new LineEncoder(LineSeparator.WINDOWS));
          }
        }).childOption(ChannelOption.TCP_NODELAY, true);

    ChannelFuture f = serverBootstrap.bind(port).sync();
    f.channel().closeFuture().sync();
  }

}

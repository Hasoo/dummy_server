package com.hasoo.message.dummyserver.netty;

import java.util.concurrent.TimeUnit;
import com.hasoo.message.dummyserver.umgp.ReportDeliverThread;
import com.hasoo.message.dummyserver.umgp.TapeDeliveryRepository;
import com.hasoo.message.dummyserver.umgp.UmgpWorker;
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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UmgpServer {
  private int port;

  public UmgpServer(int port) {
    this.port = port;
  }

  public void run() throws Exception {

    UmgpWorker umgpWorker = new UmgpWorker(TapeDeliveryRepository.getInstance());

    ReportDeliverThread reportDeliverThread = new ReportDeliverThread(umgpWorker);
    reportDeliverThread.start();

    EventLoopGroup serverGroup = new NioEventLoopGroup();
    EventLoopGroup childGroup = new NioEventLoopGroup();

    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(serverGroup, childGroup).channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
        /* @formatter:off */
            ch.pipeline()
                .addLast(new ReadTimeoutHandler(60, TimeUnit.SECONDS))
                .addLast(new LineEncoder(LineSeparator.WINDOWS))
                .addLast(new LineBasedFrameDecoder(4096))
                .addLast(new UmgpTimeoutHandler(umgpWorker))
                .addLast(new UmgpServerHandler(umgpWorker))
                ;
        /* @formatter:on */
          }
        }).childOption(ChannelOption.TCP_NODELAY, true);

    ChannelFuture f = serverBootstrap.bind(port).sync();
    log.debug("complete binding");
    f.channel().closeFuture().sync();
    log.debug("server exited");
  }
}

package com.hasoo.message.dummyserver.netty;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UmgpServerHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    super.channelUnregistered(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf byteBuf = (ByteBuf) msg;
    String line = byteBuf.toString(Charset.defaultCharset());
    line = line.trim();
    log.info(line);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    InetSocketAddress clientAddress = (InetSocketAddress) ctx.channel().remoteAddress();
    log.info("connection reset by peer {}:{}", clientAddress.getHostName(),
        clientAddress.getPort());
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    if (cause instanceof ReadTimeoutException) {
      log.info(cause.getMessage());
    } else {
      super.exceptionCaught(ctx, cause);
    }
  }

}

package io.github.hasoo.netty;

import io.github.hasoo.umgp.UmgpWorker;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class UmgpServerHandler extends ChannelInboundHandlerAdapter {

    private final UmgpWorker umgpWorker;

    public UmgpServerHandler(UmgpWorker umgpWorker) {
        this.umgpWorker = umgpWorker;
    }

  @Override
  public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
    log.info("connected->{}", ctx.toString());
    umgpWorker.connected(ctx.channel());
    super.channelRegistered(ctx);
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    log.debug(ctx.toString());
    umgpWorker.disconnected(ctx.channel());
    super.channelUnregistered(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf byteBuf = (ByteBuf) msg;
    try {
      if (byteBuf.isReadable()) {
        String line = byteBuf.toString(Charset.defaultCharset());
        line = line.trim();
        // log.debug(Util.dump(line));
        umgpWorker.receive(ctx.channel(), line);
      }
    } finally {
      byteBuf.release();
    }
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("connection closed {}", umgpWorker.who(ctx.channel()));
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.error(cause.getMessage());
  }

}

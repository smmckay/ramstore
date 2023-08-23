package us.abbies.b.ramstore.server.redis;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.redis.InlineCommandRedisMessage;

import java.util.List;

public class InlineCommandDecoder extends MessageToMessageDecoder<InlineCommandRedisMessage> {
    @Override
    protected void decode(ChannelHandlerContext ctx, InlineCommandRedisMessage msg, List<Object> out) throws Exception {

    }
}

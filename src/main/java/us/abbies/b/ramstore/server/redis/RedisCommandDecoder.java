package us.abbies.b.ramstore.server.redis;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.redis.ArrayRedisMessage;

import java.util.List;

public class RedisCommandDecoder extends MessageToMessageDecoder<ArrayRedisMessage> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ArrayRedisMessage msg, List<Object> out) throws Exception {

    }
}

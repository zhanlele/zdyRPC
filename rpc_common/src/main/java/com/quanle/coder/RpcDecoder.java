package com.quanle.coder;

import com.quanle.seriail.Serializer;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @author quanle
 * @date 2020/4/28 9:14 PM
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> clazz;

    private Serializer serializer;


    public RpcDecoder(Class<?> clazz, Serializer serializer) {

        this.clazz = clazz;

        this.serializer = serializer;

    }

    @Override
    protected void decode(
            ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int length = byteBuf.readableBytes();
        byte[] content = new byte[length];
        byteBuf.getBytes(4, content, 0, length);
        Object o = serializer.deserialize(clazz, content);
        list.add(o);
    }
}

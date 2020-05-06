package com.quanle.common.client;

import com.alibaba.fastjson.JSON;

import java.util.concurrent.Callable;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author quanle
 * @date 2020/4/28 9:27 PM
 */
public class UserClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    private ChannelHandlerContext context;
    private String result;
    private Object para;


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        context = ctx;
    }

    /**
     * 收到服务端数据，唤醒等待线程
     */

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) {
        result = JSON.toJSONString(msg);
        notify();
    }

    /**
     * 写出数据，开始等待唤醒
     */

    @Override
    public synchronized Object call() throws InterruptedException {
        context.writeAndFlush(para);
        wait();
        return result;
    }

    /**
     * 设置参数
     */
    void setPara(Object para) {
        this.para = para;
    }
}

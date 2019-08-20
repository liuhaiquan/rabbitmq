package com.kavin.rabbitmq.api.utils;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 获取channel（通道）的工具类
 * Connection 相当于一根电缆，而channel相当于电缆中的每根光纤线
 */
public class ChannelUtil {


    /**
     * 获取通道channel
     * @return
     */
    public static Channel getChannel(){
        Channel channel= null;
        try {
            Connection connection = new ConnectionUtil().getConnection();
            channel = connection.createChannel();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }


    public static void  close(Channel channel) throws Exception {
        channel.close();
    }

}

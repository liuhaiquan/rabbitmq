package com.kavin.rabbitmq.api.exchange.fanout;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.kavin.rabbitmq.api.utils.ConnectionUtil;
import com.rabbitmq.client.Channel;
/**
 *
 * 生产者发送消息
 * fanout类型交换器
 * 把消息路由到与该交换器绑定的所有队列中，与routingKey无关
 *
 * 关于下面方法参数的注释请参考 com.kavin.rabbitmq.api.consumer.Producer类
 */

public class Producer4FanoutExchange {

	
	public static void main(String[] args) throws Exception {
		
		//3 创建Channel
		Channel channel = ChannelUtil.getChannel();
		//4 声明
		String exchangeName = "fanout_exchange";
		//5 发送
		for(int i = 0; i < 10; i ++) {
			String msg = "Hello World RabbitMQ 4 FANOUT Exchange Message ...";
			//此处路由键为空，但是交换器类型为fanout。则不使用路由键，而是发送到所有绑定该交换器的队列上。
			channel.basicPublish(exchangeName, "", null , msg.getBytes()); 			
		}
		ChannelUtil.close(channel);
        ConnectionUtil.close();
	}
	
}

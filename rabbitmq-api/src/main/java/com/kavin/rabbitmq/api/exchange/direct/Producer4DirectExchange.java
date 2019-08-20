package com.kavin.rabbitmq.api.exchange.direct;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;
/**
 * direct类型交换器
 * 把消息路由到那些 BindingKey和RoutingKey完全匹配的队列中。
 *
 * 关于下面方法参数的注释请参考 com.kavin.rabbitmq.api.consumer.Producer类
 *
 */
public class Producer4DirectExchange {

	
	public static void main(String[] args) throws Exception {

		//创建Channel
		Channel channel = ChannelUtil.getChannel();
		//声明
		String exchangeName = "direct_exchange";
		String routingKey = "direct_key";
		//发送消息。通过routingKey从交换机找到相应队列（或是交换机），保存到队列中。
		String msg = "Hello World RabbitMQ 4  Direct Exchange Message 111 ... ";
		channel.basicPublish(exchangeName, routingKey , null , msg.getBytes()); 		
		
	}
	
}

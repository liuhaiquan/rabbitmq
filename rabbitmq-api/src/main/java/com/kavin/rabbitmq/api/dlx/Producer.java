package com.kavin.rabbitmq.api.dlx;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 死信队列
 * 以下几种情况会进入私信队列
 * 1.消息被拒绝（basic.reject/ basic.nack）并且requeue=false
 * 2.消息TTL过期（参考：RabbitMQ之TTL（Time-To-Live 过期时间））
 * 3.队列达到最大长度
 *
 *
 * 关于下面方法参数的注释请参考 com.kavin.rabbitmq.api.consumer.Producer类
 */
public class Producer {


	public static void main(String[] args) throws Exception {
		

		Channel channel = ChannelUtil.getChannel();
		
		String exchange = "dlx_exchange";
		String routingKey = "dlx.save";
		
		String msg = "Hello RabbitMQ DLX Message";
		
		for(int i =0; i<1; i ++){


		    // 这里设置了消息的超时时间  10秒之内消息没有被消费掉就会进入到死信队列
			AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
					.deliveryMode(2) // 设置消息是否持久化，1： 非持久化 2：持久化
					.contentEncoding("UTF-8") //设置消息字符集
					.expiration("10000") // 设置消息超时时间
					.build();
			channel.basicPublish(exchange, routingKey, true, properties, msg.getBytes());
		}
		
	}
}

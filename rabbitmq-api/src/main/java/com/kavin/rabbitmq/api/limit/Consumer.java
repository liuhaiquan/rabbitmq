package com.kavin.rabbitmq.api.limit;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;


/**
 * 消息限流
 *  下面一些方法的详细解释请参考com.kavin.rabbitmq.api.consumer.Consumer类
 */
public class Consumer {

	
	public static void main(String[] args) throws Exception {
		
		

		Channel channel = ChannelUtil.getChannel();
		
		
		String exchangeName = "qos_exchange";
		String queueName = "qos_queue";
		String routingKey = "qos.#";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		channel.queueDeclare(queueName, true, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		//限流方式  第一件事就是autoAck设置为false.需要在消费端自动确认。
        // 因为生产者这边接受到消息确认之后才会再次发送prefetchCount个消息到消费端。否则设置自动确认就失去了限流的意义。
        //1.prefetchSize 限制消息的大小  0是不限制
        //2.prefetchCount 消费者端单次能处理多少消息
        //3.global 限流是在什么级别上应用的  true：channel    false:在消费端限制
		channel.basicQos(0, 1, false);

		// 关闭消息 自动确认 autoAck
		channel.basicConsume(queueName, false, new MyConsumer(channel));
		
		
	}
}

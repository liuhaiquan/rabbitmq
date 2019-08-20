package com.kavin.rabbitmq.api.ack;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 *  手工确认消息 ack
 * @author kavin
 * 下面一些方法的详细解释参考 com.kavin.rabbitmq.api.consumer.Consumer 类
 */
public class Consumer {

	
	public static void main(String[] args) throws Exception {

		Channel channel = ChannelUtil.getChannel();
		
		//声明
		String exchangeName = "ack_exchange";
		String queueName = "ack_queue";
		String routingKey = "ack.#";

		//声明topic类型交换器
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		//声明队列
		channel.queueDeclare(queueName, true, false, false, null);
		//将交换机和队列进行绑定
		channel.queueBind(queueName, exchangeName, routingKey);
		
		// 手工签收 必须要关闭 autoAck = false,否则手工签收无效
		channel.basicConsume(queueName, false, new MyConsumer(channel));
		
		
	}
}

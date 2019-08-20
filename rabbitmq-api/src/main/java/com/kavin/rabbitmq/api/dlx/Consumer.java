package com.kavin.rabbitmq.api.dlx;

import java.util.HashMap;
import java.util.Map;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;

/**
 * 死信队列  消费者不进行消息的消费，等待消息超时进入到死信队列
 *  * 以下几种情况会进入私信队列
 * 1.消息被拒绝（basic.reject/ basic.nack）并且requeue=false
 * 2.消息TTL过期（参考：RabbitMQ之TTL（Time-To-Live 过期时间））
 * 3.队列达到最大长度
 *
 * 关于下面方法参数的注释请参考 com.kavin.rabbitmq.api.consumer.consumer类
 */
public class Consumer {



    //到rabbitmq 面板查看消息是否从 dlx_queue 10秒后进入到 dlx.queue 队列中
	public static void main(String[] args) throws Exception {

		Channel channel = ChannelUtil.getChannel();
		
		// 这就是一个普通的交换机 和 队列 以及路由
		String exchangeName = "dlx_exchange";
		String routingKey = "dlx.#";
		String queueName = "dlx_queue";
		
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);
		
		Map<String, Object> agruments = new HashMap<String, Object>();
		//设置死信队列 指定死信队列对应的交换机
		agruments.put("x-dead-letter-exchange", "dlx.exchange");
		//这个agruments属性，要设置到声明队列上
		channel.queueDeclare(queueName, true, false, false, agruments);
		channel.queueBind(queueName, exchangeName, routingKey);
		
		//声明死信队列
		channel.exchangeDeclare("dlx.exchange", "topic", true, false, null);
		channel.queueDeclare("dlx.queue", true, false, false, null);
		channel.queueBind("dlx.queue", "dlx.exchange", "#");




		
	}
}

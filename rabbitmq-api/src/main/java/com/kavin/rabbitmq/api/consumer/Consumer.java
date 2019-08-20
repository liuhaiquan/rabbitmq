package com.kavin.rabbitmq.api.consumer;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;


/**
 * 消费消息
 */
public class Consumer {

	
	public static void main(String[] args) throws Exception {
		
		
		//获取channel通道
		Channel channel = ChannelUtil.getChannel();
		
		
		String exchangeName = "consumer_exchange";

		//当交换器类型是topic类型时，routingKey进行模糊匹配。#代表匹配一个或多个单词， *匹配一个单词
		String routingKey = "consumer.#";
		String queueName = "consumer_queue";
		//声明交换器
		//1.exchange：交换器名称
		//2.type：交换器的类型，常见的如 fanout direct topic
		//3.durable:设置是否持久化
		//4.autoDelete 设置是否自动删除。前提是至少有一个交换器或者队列与此交换器绑定，之后所有与这个交换器绑定的队列或者交换器都与之解绑。
		//5.argument:其他一些结构化参数
		channel.exchangeDeclare(exchangeName, "topic", true, false, null);

		//声明队列
		//1.quene:队列名称
		//2.durable:设置是否持久化
		//3.exclusive 设置是否排他。
		//4.autoDelete 设置是否自动删除。前提是至少一个消费者连接到此队列，之后所有与此队列连接的消费者都断开时。
		//5.argurnents: 设置队列的其他一些参数
		channel.queueDeclare(queueName, true, false, false, null);


		//队列和交换机通过routingKey进行绑定
        //此处交换器类型是topic类型，routingKey进行模糊匹配。#代表匹配一个或多个单词， *匹配一个单词
        //所以只要是exchangeName交换机路由过来的消息的routingKey是consumer.开头的消息都会进入此队列
		channel.queueBind(queueName, exchangeName, routingKey);

		//消费消息。将信道置为接收模式
		//1.queue 队列的名称:
		//2.autoAck 设置是否自动确认。建议设成 false ，即不自动确认:
		//3.设置消费者的回调函数。用来处理 RabbitMQ推送过来的消息，比如DefaultConsumer 使用时需要客户端重写 (override) 其中的方法。
		channel.basicConsume(queueName, true, new MyConsumer(channel));
		
		
	}
}

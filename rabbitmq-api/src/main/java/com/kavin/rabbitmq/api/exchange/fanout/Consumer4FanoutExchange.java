package com.kavin.rabbitmq.api.exchange.fanout;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.QueueingConsumer.Delivery;

/**
 *
 * 消费者消费消息
 * fanout类型交换器
 * 把消息路由到与该交换器绑定的所有队列中，与routingKey无关
 * 关于下面方法参数的注释请参考 com.kavin.rabbitmq.api.consumer.consumer类
 */
public class Consumer4FanoutExchange {

	public static void main(String[] args) throws Exception {


        Channel channel = ChannelUtil.getChannel();
		//4 声明
		String exchangeName = "fanout_exchange";
		String exchangeType = "fanout";
		String queueName = "fanout_queue";
		String routingKey = "";	//不设置路由键
		channel.exchangeDeclare(exchangeName, exchangeType, true, false, false, null);
		channel.queueDeclare(queueName, false, false, false, null);
		channel.queueBind(queueName, exchangeName, routingKey);

        //实际开发中不建议使用QueueingConsumer，此处只是为了方便代码演示。
        // 这个方法已经被弃用，建议使用DefaultConsumer类并重写handleDelivery方法
        QueueingConsumer consumer = new QueueingConsumer(channel);
        //参数：队列名称、是否自动ACK、Consumer
        channel.basicConsume(queueName, true, consumer); 
        //循环获取消息  
        while(true){  
            //获取消息，如果没有消息，这一步将会一直阻塞  
            Delivery delivery = consumer.nextDelivery();  
            String msg = new String(delivery.getBody());    
            System.out.println("收到消息：" + msg);  
        } 
	}
}

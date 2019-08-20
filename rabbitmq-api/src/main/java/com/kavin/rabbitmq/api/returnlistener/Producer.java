package com.kavin.rabbitmq.api.returnlistener;

import java.io.IOException;

import com.kavin.rabbitmq.api.utils.ChannelUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ReturnListener;

/**
 *
 * Teturn 消息机制
 *  * 关于下面方法参数的注释请参考 com.kavin.rabbitmq.api.consumer.Producer类
 */
public class Producer {

	
	public static void main(String[] args) throws Exception {
		
		

		Channel channel = ChannelUtil.getChannel();
		
		String exchange = "return_exchange";
		//设置一个找不到队列的路由键
		String routingKeyError = "abc.save";
		
		String msg = "Hello RabbitMQ Return Message";
		
		//通过路由键找不到交换机。所以调用Basic.Return返回给生产者。这里监听了return事件
		channel.addReturnListener(new ReturnListener() {
			@Override
            //1.错误码
            //2.错误原因
            //3.交换机
            //4.路由键
            //5.发送消息携带的properties
            //6.消息体
			public void handleReturn(int replyCode, String replyText, String exchange,
					String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
				
				System.err.println("---------handle  return----------");
				System.err.println("replyCode: " + replyCode);
				System.err.println("replyText: " + replyText);
				System.err.println("exchange: " + exchange);
				System.err.println("routingKey: " + routingKey);
				System.err.println("properties: " + properties);
				System.err.println("body: " + new String(body));
			}
		});
		
		//使用return消息机制发送消息时候必须设置mandatory为true、这样才会调用Basic.Return 命令将消息返回给生产者   false 为直接删除
		channel.basicPublish(exchange, routingKeyError, true, null, msg.getBytes());
		

		
		
		
	}
}

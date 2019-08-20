package com.kavin.spring;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import com.kavin.spring.entity.Order;
import com.kavin.spring.entity.Packaged;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Test
	public void contextLoads() {
	}
	
	@Autowired
	private RabbitAdmin rabbitAdmin;
	
	@Test
	public void testAdmin() throws Exception {
	    //声明
		rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));
		
		rabbitAdmin.declareExchange(new TopicExchange("test.topic", false, false));
		
		rabbitAdmin.declareExchange(new FanoutExchange("test.fanout", false, false));
		
		rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
		
		rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
		
		rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));
		
		rabbitAdmin.declareBinding(new Binding("test.direct.queue",
				Binding.DestinationType.QUEUE,
				"test.direct", "direct", new HashMap<>()));
		
		rabbitAdmin.declareBinding(
				BindingBuilder
				.bind(new Queue("test.topic.queue", false))		//直接创建队列
				.to(new TopicExchange("test.topic", false, false))	//直接创建交换机 建立关联关系
				.with("user.#"));	//指定路由Key
		
		
		rabbitAdmin.declareBinding(
				BindingBuilder
				.bind(new Queue("test.fanout.queue", false))		
				.to(new FanoutExchange("test.fanout", false, false)));
		
		//清空队列数据
		rabbitAdmin.purgeQueue("test.topic.queue", false);
	}
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	
	@Test
	public void testSendMessage() throws Exception {
		//1 创建消息  默认的content-type是application/octet-stream 类型的
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.getHeaders().put("desc", "信息描述..");
		messageProperties.getHeaders().put("type", "自定义消息类型..");
		//因为该消息的content-type是application/octet-stream类型的。在rabbitMQConfig中 没有为全局convert设置
        //转换器。通过判断消息体类型来调用重载的委托（本消息调用 consumeMessage(byte[] messageBody)）方法执行
        Message message = new Message("Hello RabbitMQ".getBytes(), messageProperties);
		//MessagePostProcessor:消息前置后置处理器
		rabbitTemplate.convertAndSend("topic001", "spring.amqp", message, new MessagePostProcessor() {
		    //消息发送完成之后对消息在做一些设置
			@Override
			public Message postProcessMessage(Message message) throws AmqpException {
				System.err.println("------添加额外的设置---------");
				message.getMessageProperties().getHeaders().put("desc", "额外修改的信息描述");
				message.getMessageProperties().getHeaders().put("attr", "额外新加的属性");
				return message;
			}
		});
	}
	
	@Test
	public void testSendMessage2() throws Exception {
		//1 创建消息
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("text/plain");
		Message message = new Message("mq 消息1234".getBytes(), messageProperties);
		//可以发送message对象。也可以是一句话
		rabbitTemplate.send("topic001", "spring.abc", message);
		
		rabbitTemplate.convertAndSend("topic001", "spring.amqp", "hello object message send!");
		rabbitTemplate.convertAndSend("topic002", "rabbit.abc", "hello object message send!");
	}

    /**
     * 此消息的contentType 是text/plain。消息都会被转换器转换成String类型，（本消息调用 consumeMessage(String messageBody)）
     * @throws Exception
     */
	@Test
	public void testSendMessage4Text() throws Exception {
		//1 创建消息
		MessageProperties messageProperties = new MessageProperties();
		messageProperties.setContentType("text/plain");
		Message message = new Message("mq 消息1234".getBytes(), messageProperties);
		
		rabbitTemplate.send("topic001", "spring.abc", message);
		rabbitTemplate.send("topic002", "rabbit.abc", message);
	}

    /**
     * 使用Jackson2JsonMessageConverter处理器，客户端发送JSON类型数据，但是没有指定消息的contentType类型，那么Jackson2JsonMessageConverter就会将消息转换成byte[]类型的消息进行消费。
     * 如果指定了contentType为application/json，那么消费端就会将消息转换成Map类型的消息进行消费。
     * 如果指定了contentType为application/json，并且生产端是List类型的JSON格式，那么消费端就会将消息转换成List类型的消息进行消费。
     * 此消息的contentType 是application/json。消息都会被转换器转换成Map类型，（本消息调用 consumeMessage(Map messageBody)）
     * 此消息的contentType 是application/json。消息都会被转换器转换成Map类型，（本消息调用 consumeMessage(Map messageBody)）
     * @throws Exception
     */
	@Test
	public void testSendJsonMessage() throws Exception {
		
		Order order = new Order();
		order.setId("001");
		order.setName("消息订单");
		order.setContent("描述信息");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json);
		
		MessageProperties messageProperties = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties.setContentType("application/json");
		Message message = new Message(json.getBytes(), messageProperties);
		
		rabbitTemplate.send("topic001", "spring.order", message);
	}

    /**
     * 发送一个java对象    放开RabbitMQConfig类中的1.2的代码
     * @throws Exception
     */
	@Test
	public void testSendJavaMessage() throws Exception {
		
		Order order = new Order();
		order.setId("001");
		order.setName("订单消息");
		order.setContent("订单描述信息");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json);
		
		MessageProperties messageProperties = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties.setContentType("application/json");
        /**
         * 发送消息的时候,TypeId的值可以是java对象全称，也可以是映射的key
         * 当消费者有配置映射key的时候，生产者既可以指定java对象全称，又可以是映射的key。
         * 如果消费者没有配置映射key，则只能指定java对象全称
         */
		//指定的__TypeId__属性值必须是消费端的Order的全类名，如果不匹配则会报错。那发送的消息封装到order对象中
		messageProperties.getHeaders().put("__TypeId__", "Order");
		Message message = new Message(json.getBytes(), messageProperties);
		
		rabbitTemplate.send("topic001", "spring.order", message);
	}

    /**
     * 通过映射关系发送java对象    运行此代码须放开RabbitMQConfig类中的1.3的代码
     * @throws Exception
     */
	@Test
	public void testSendMappingMessage() throws Exception {
		
		ObjectMapper mapper = new ObjectMapper();
		
		Order order = new Order();
		order.setId("001");
		order.setName("订单消息");
		order.setContent("订单描述信息");
		
		String json1 = mapper.writeValueAsString(order);
		System.err.println("order 4 json: " + json1);
		
		MessageProperties messageProperties1 = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties1.setContentType("application/json");
		// order 这个可以在RabbitMQConfig 1.3的代码中配置了映射关系  order 对应的是  Order.class这个类。 将结果转成order对象
		messageProperties1.getHeaders().put("__TypeId__", "order");
		Message message1 = new Message(json1.getBytes(), messageProperties1);
		rabbitTemplate.send("topic001", "spring.order", message1);
		
		Packaged pack = new Packaged();
		pack.setId("002");
		pack.setName("包裹消息");
		pack.setDescription("包裹描述信息");
		
		String json2 = mapper.writeValueAsString(pack);
		System.err.println("pack 4 json: " + json2);

		MessageProperties messageProperties2 = new MessageProperties();
		//这里注意一定要修改contentType为 application/json
		messageProperties2.setContentType("application/json");
		// 和上面order同理
		messageProperties2.getHeaders().put("__TypeId__", "packaged");
		Message message2 = new Message(json2.getBytes(), messageProperties2);
		rabbitTemplate.send("topic001", "spring.pack", message2);
	}

    /**
     * 发送图片或者pdf消息进行处理。返回File对象调用响应的委托类中的重载方法
     * @throws Exception
     */
	@Test
	public void testSendExtConverterMessage() throws Exception {
//			byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "picture.png"));
//			MessageProperties messageProperties = new MessageProperties();
//			messageProperties.setContentType("image/png");
//			messageProperties.getHeaders().put("extName", "png");
//			Message message = new Message(body, messageProperties);
//			rabbitTemplate.send("", "image_queue", message);
		
			byte[] body = Files.readAllBytes(Paths.get("d:/002_books", "mysql.pdf"));
			MessageProperties messageProperties = new MessageProperties();
			messageProperties.setContentType("application/pdf");
			Message message = new Message(body, messageProperties);
			rabbitTemplate.send("", "pdf_queue", message);
	}
	

}

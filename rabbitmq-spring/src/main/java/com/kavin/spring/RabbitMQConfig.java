package com.kavin.spring;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.kavin.spring.adapter.MessageDelegate;
import com.kavin.spring.convert.ImageMessageConverter;
import com.kavin.spring.convert.PDFMessageConverter;
import com.kavin.spring.convert.TextMessageConverter;
import com.kavin.spring.entity.Order;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.ConsumerTagStrategy;
import org.springframework.amqp.support.converter.ContentTypeDelegatingMessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration //设置该类为一个配置类
@ComponentScan({"com.kavin.spring.*"})  // 扫描com.bfxy.spring包下的类到容器中
public class RabbitMQConfig {

	@Bean  // 相当于xml中配置的<bean id="connectionFactory"></bean> id就是方法名称
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses("127.0.0.1:5672");
		connectionFactory.setUsername("guest");
		connectionFactory.setPassword("guest");
		connectionFactory.setVirtualHost("/");
		return connectionFactory;
	}

    /**
     * (1)RabbitAdmin类可以很好的操作RabbitMQ，在Spring中直接进行注入即可
     * (2)autoStartup必须设置true，否则Spring容器不会加载RabbitAdmin类
     * (3)RabbitAdmin底层实现就是从Spring容器中获取Exchagge,Bingding,RoutingKey以及Queue的@Bean声明
     * (4)然后使用RabbitTemplate的execute方法执行对应声明，修改，删除等操作
     *
     */
	@Bean
	public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
		RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
		rabbitAdmin.setAutoStartup(true);
		return rabbitAdmin;
	}
	
    /**  
     * 针对消费者配置  
     * 1. 设置交换机类型  
     * 2. 将队列绑定到交换机  
        FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念  
        HeadersExchange ：通过添加属性key-value匹配  
        DirectExchange:按照routingkey分发到指定队列  
        TopicExchange:多关键字匹配  
     */

    @Bean  //创建Topic类型交换器 topic001
    public TopicExchange exchange001() {  

        return new TopicExchange("topic001", true, false);
    }  

    @Bean  //创建队列 queue001
    public Queue queue001() {

        return new Queue("queue001", true); //队列持久
    }  
    
    @Bean //将topic001交换器 与 queue001进行绑定通过 spring.* 这个routingKey
    public Binding binding001() {  

        return BindingBuilder.bind(queue001()).to(exchange001()).with("spring.*");
    }  
    
    @Bean  
    public TopicExchange exchange002() {  

        return new TopicExchange("topic002", true, false);
    }  
    
    @Bean  
    public Queue queue002() {  

        return new Queue("queue002", true); //队列持久
    }
    
    @Bean  
    public Binding binding002() {  

        return BindingBuilder.bind(queue002()).to(exchange002()).with("rabbit.*");
    } 
    
    @Bean  
    public Queue queue003() {  

        return new Queue("queue003", true); //队列持久
    }
    
    @Bean  
    public Binding binding003() {  

        return BindingBuilder.bind(queue003()).to(exchange001()).with("mq.*");
    } 
    
    @Bean  
    public Queue queue_image() {  
        return new Queue("image_queue", true); //队列持久
    }
    
    @Bean  
    public Queue queue_pdf() {  

        return new Queue("pdf_queue", true); //队列持久
    }
    
    //创建消息模板,简化 RabbitMQ 发送和接收消息操作
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    	RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    	return rabbitTemplate;
    }

    /**
     * (1)简单消息监听容器：这个类非常的强大，我们可以对他进行很多设置，对于消费者的配置项，这个类都可以满足
     * (2)设置事务特性，事务管理器，事务属性，事务容量，事务开启等
     * (3)设置消息确认和自动确认模式，是否重回队列，异常捕获handler函数
     * (4)设置消费者标签生成策略，是否独占模式，消费者属性等
     * (5)simpleMessageListenerContailer可以进行动态设置，比如在运行中的应用可以动态的修改其消费者数量的大小，接收消息的模式等

     */
    @Bean
    public SimpleMessageListenerContainer messageContainer(ConnectionFactory connectionFactory) {

    	SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        //可同时监控多个队列
    	container.setQueues(queue001(), queue002(), queue003(), queue_image(), queue_pdf());
    	//当前的消费者数量
    	container.setConcurrentConsumers(1);
        //最多多少个消费者
    	container.setMaxConcurrentConsumers(5);
    	//是否重回队列
    	container.setDefaultRequeueRejected(false);
    	//自动签收
    	container.setAcknowledgeMode(AcknowledgeMode.AUTO);
        //消费端生成标签策略
    	container.setConsumerTagStrategy(new ConsumerTagStrategy() {
			@Override
			public String createConsumerTag(String queue) {

			    return queue + "_" + UUID.randomUUID().toString();
			}
		});



        /* //具体监听
    	container.setMessageListener(new ChannelAwareMessageListener() {
			@Override
			public void onMessage(Message message, Channel utils) throws Exception {
				String msg = new String(message.getBody());
				System.err.println("----------消费者: " + msg);
			}
		});*/
    	
    	/**
    	 * 1 适配器方式. 默认是有自己的方法名字的：handleMessage
    		// 可以自己指定一个方法的名字: consumeMessage
    		// 也可以添加一个转换器: 从字节数组转换为String
    	MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
    	adapter.setDefaultListenerMethod("consumeMessage");
    	adapter.setMessageConverter(new TextMessageConverter());
    	container.setMessageListener(adapter);
    	*/
    	
    	/**
    	 * 2 适配器方式: 我们的队列名称 和 方法名称 也可以进行一一的匹配
    	 * 
    	MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
    	adapter.setMessageConverter(new TextMessageConverter());
    	Map<String, String> queueOrTagToMethodName = new HashMap<>();
    	queueOrTagToMethodName.put("queue001", "method1");
    	queueOrTagToMethodName.put("queue002", "method2");
    	adapter.setQueueOrTagToMethodName(queueOrTagToMethodName);
    	container.setMessageListener(adapter);    	
    	*/
    	
        // 1.1 支持json格式的转换器
        /**
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");
        
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        adapter.setMessageConverter(jackson2JsonMessageConverter);
        
        container.setMessageListener(adapter);
        */
    	
        
        //支持java对象转换
        // 1.2 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter
        /**
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");
        
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        
        adapter.setMessageConverter(jackson2JsonMessageConverter);
        container.setMessageListener(adapter);
        */
        
        //支持java对象多映射转换
        //1.3 DefaultJackson2JavaTypeMapper & Jackson2JsonMessageConverter

/*      MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());
        adapter.setDefaultListenerMethod("consumeMessage");
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper javaTypeMapper = new DefaultJackson2JavaTypeMapper();
        
        Map<String, Class<?>> idClassMapping = new HashMap<String, Class<?>>();
		idClassMapping.put("order", com.kavin.spring.entity.Order.class);
		idClassMapping.put("packaged", com.kavin.spring.entity.Packaged.class);
		
		javaTypeMapper.setIdClassMapping(idClassMapping);
		
		jackson2JsonMessageConverter.setJavaTypeMapper(javaTypeMapper);
        adapter.setMessageConverter(jackson2JsonMessageConverter);
        container.setMessageListener(adapter);*/

        
        //1.4 ext convert  消息监听适配器    适配自己创建的MessageDelegate类进行消息委托
        
        MessageListenerAdapter adapter = new MessageListenerAdapter(new MessageDelegate());

        //设置消息委托者的默认处理方法名，该方法重载，根据消息的类型调用不同的参数的重载方法。
        adapter.setDefaultListenerMethod("consumeMessage");
        
        //全局的转换器:
		ContentTypeDelegatingMessageConverter convert = new ContentTypeDelegatingMessageConverter();


		//根据不同的contentType 选择不同的消息转换器  下面没有的contentType不进行转换
        TextMessageConverter textConvert = new TextMessageConverter();
		convert.addDelegate("text", textConvert);
		convert.addDelegate("html/text", textConvert);
		convert.addDelegate("xml/text", textConvert);
		convert.addDelegate("text/plain", textConvert);


		Jackson2JsonMessageConverter jsonConvert = new Jackson2JsonMessageConverter();
		convert.addDelegate("json", jsonConvert);
		convert.addDelegate("application/json", jsonConvert);


		ImageMessageConverter imageConverter = new ImageMessageConverter();
		convert.addDelegate("image/png", imageConverter);
		convert.addDelegate("image", imageConverter);
		
		PDFMessageConverter pdfConverter = new PDFMessageConverter();
		convert.addDelegate("application/pdf", pdfConverter);
        
		//设置这个适配器的消息转换器为convert全局转换器
		adapter.setMessageConverter(convert);
		//在容器中设置消息监听器为adapter这个适配器
		container.setMessageListener(adapter);
		
    	return container;
    	
    }
    
    

    
    
    
    
    
    
    
    
	
	
	
}

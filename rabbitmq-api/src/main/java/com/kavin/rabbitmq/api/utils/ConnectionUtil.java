package com.kavin.rabbitmq.api.utils;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;


public class ConnectionUtil {


    //与rabbitmq的连接
    private static Connection connection;


    /**
     * 此处连接的ip 是虚拟机通过keepalived 暴露出来的虚拟ip，采用Haproxy进行负载均衡rabbitmq 镜像集群
     */
    private String host = "192.168.140.200";

    private int port = 5672;

    private String userName = "guest";

    private String passWord = "guest";




    public Connection getConnection() {
        return connection;
    }

    /**
     * 使用有参构造方法创建connection
     * @return
     */
    public  ConnectionUtil(){
        if(connection == null)
            connection = createConnection();
    }

    /**
     * 获取连接工厂
     * @return
     */
    public ConnectionFactory getConnectionFactory(){
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(passWord);
        connectionFactory.setVirtualHost("/");
        return connectionFactory;
    }


    //获取连接
    private Connection createConnection(){
        try {
            ConnectionFactory connectionFactory = getConnectionFactory();
            connection = connectionFactory.newConnection();
        }catch(Exception e) {
            e.printStackTrace();
        }

        return connection;
    }


    public static void close() throws IOException {
        connection.close();
    }



}

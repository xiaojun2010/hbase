package com.atguigu.phoenix.thin;

import java.sql.*;

/**
 * JDBC编码步骤: 注册驱动  获取连接  编写SQL  预编译  设置参数  执行SQL  封装结果  关闭连接
 */
public class ThinClientDemo {

    public static void main(String[] args) throws SQLException {

        //1. 获取连接
        String url = "jdbc:phoenix:thin:url=http://hadoop102:8765;serialization=PROTOBUF";
        Connection connection = DriverManager.getConnection(url);

        //2. 编写SQL
        String sql = "select id,name,addr from student" ;

        //3. 预编译
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        //4. 执行sql
        ResultSet resultSet = preparedStatement.executeQuery();

        //5. 封装结果
        while(resultSet.next()){
            String line = resultSet.getString("id") + " : " +
                    resultSet.getString("name") + " : " +
                    resultSet.getString("addr") ;

            System.out.println(line);
        }

        //6. 关闭连接
        resultSet.close();
        preparedStatement.close();
        connection.close();

    }
}

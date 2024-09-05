package com.atguigu.phoenix.thick;

import java.sql.*;
import java.util.Properties;

public class ThickClientDemo {
    public static void main(String[] args) throws SQLException {

        //1. 获取连接
        String url = "jdbc:phoenix:hadoop102,hadoop103,hadoop104:2181";

        Properties props = new Properties();
        props.put("phoenix.schema.isNamespaceMappingEnabled","true");

        Connection connection = DriverManager.getConnection(url,props);

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

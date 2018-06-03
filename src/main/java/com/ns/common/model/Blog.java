package com.ns.common.model;

import com.ns.common.model.base.BaseBlog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * 本 ns 仅表达最为粗浅的 jfinal 用法，更为有价值的实用的企业级用法
 * 详见 JFinal 俱乐部: http://jfinal.com/club
 * 
 * Blog model.
 * 数据库字段名建议使用驼峰命名规则，便于与 java 代码保持一致，如字段名： userId
 */
@SuppressWarnings("serial")
public class Blog extends BaseBlog<Blog> {
    public static void main(String[] args) {
        try {

            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = (Connection) DriverManager
                    .getConnection("jdbc:mysql://39.107.92.124:3306/ns?user=ns&password=hd67y12&useUnicode=true&characterEncoding=UTF8");
                            Statement s =  conn.createStatement();
            System.out.println(conn.isClosed());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

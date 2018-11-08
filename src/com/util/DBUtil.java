package com.util;

import java.sql.*;
import java.util.ArrayList;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class DBUtil {
//    mysql://yxt:pwdasdwx@172.17.128.172:3306/skyeye?charset=utf8"

        // JDBC 驱动名及数据库 URL
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
        static final String DB_URL = "jdbc:mysql://172.17.128.172:3306/skyeye?charset=utf8";

        // 数据库的用户名与密码，需要根据自己的设置
        static final String USER = "yxt";
        static final String PASS = "pwdasdwx";

        public static ArrayList query(String fields, String table, String condition){
            ArrayList<HashMap<String,String>> return_result = new ArrayList<>();
            Connection conn = null;
            Statement stmt = null;
            try{
                // 注册 JDBC 驱动
                Class.forName(JDBC_DRIVER);

                // 打开链接
                System.out.println("连接数据库...");
                conn = DriverManager.getConnection(DB_URL,USER,PASS);

                // 执行查询
                System.out.println(" 实例化Statement对象...");
                stmt = conn.createStatement();
                String sql;
                sql = "select "+fields+" from  "+table;
                if(condition!=null){
                    sql = sql + " where " + condition;
                }
//                sql = sql + " LIMIT 10";
                ResultSet rs = stmt.executeQuery(sql);

                // 展开结果集数据库

                while(rs.next()){
                    // 通过字段检索
//                    String id  = rs.getString("id");
//                    String orgid = rs.getString("orgid");
//                    String distributesourcetype = rs.getString("distributesourcetype");
                    String[] fieldArr = fields.split(",");
                    HashMap<String,String> hm = new HashMap<>();

                    for(int i=0;i<fieldArr.length;i++){
                        hm.put(fieldArr[i],rs.getString(fieldArr[i]));
                    }
                    return_result.add(hm);
//                    System.out.println(return_result);
                    // 输出数据
//                    System.out.print("ID: " + id);
//                    System.out.print(", 站点名称: " + orgid);
//                    System.out.print(", 站点 URL: " + distributesourcetype);
//                    System.out.print("\n");
                }
                // 完成后关闭
                rs.close();
                stmt.close();
                conn.close();
            }catch(SQLException se){
                // 处理 JDBC 错误
                se.printStackTrace();
            }catch(Exception e){
                // 处理 Class.forName 错误
                e.printStackTrace();
            }finally{
                // 关闭资源
                try{
                    if(stmt!=null) stmt.close();
                }catch(SQLException se2){
                }// 什么都不做
                try{
                    if(conn!=null) conn.close();
                }catch(SQLException se){
                    se.printStackTrace();
                }
            }
            System.out.println("Goodbye!");
//            System.out.println(return_result);
            return return_result;
        }

        public static void main(String[] args) {
//            query("id,orgid,distributesourcetype","core_knowledge","distributesourcetype=2");
            ArrayList res = query("pid,bucketName,bucketKey","gw_knowledge","  kngType='VideoKnowledge' ");
            System.out.println(res);
            System.out.println(res.size());
//            System.out.println(res);
//            ArrayList res = query("id","core_organizationprofile","isofficialcustomer=1");
//            System.out.println(res.size());
//            System.out.println(res);
        }
    }

package com.linshu.exe;

import com.linshu.exe.mapper.CustomerMapper;
import com.linshu.exe.model.Customer;
import com.linshu.exe.session.SqlSession;
import com.linshu.exe.session.SqlSessionFactory;
import com.linshu.exe.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

public class App {


    public static void main(String[] args) {

        /**
         * 读取mybatis核心配置文件
         */
        InputStream resourceAsStream = App.class.getClassLoader().getResourceAsStream("mybatis/mybatis-config.xml");

        /**
         * 1.解析mybatis配置文件,封装到Configuration中
         *      Configuration---|Environment信息,如连接数据库的配置信息
         *                      |Map<String,MapperStatement>
         *
         * 2.SqlSessionFactoryBuilder通过封装后的Configuration,生成SqlSessionFactory
         */
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);


        /**
         * 打开session回话
         */
        SqlSession sqlSession = sessionFactory.openSession();

//
        CustomerMapper mapper = sqlSession.getMapper(CustomerMapper.class);
//
        for(int i = 0; i < 50; i++){
            Customer customer = mapper.selectById(1L);

            System.out.println(customer.getName());
        }
    }
}

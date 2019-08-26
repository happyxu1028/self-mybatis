package com.linshu.exe.session;

import com.linshu.exe.mapping.Configuration;
import com.linshu.exe.parsing.XMLConfigBuilder;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream inputStream){

        // mybatis 配置对象,就是mybatis-config的配置信息
         Configuration configuration  = new XMLConfigBuilder(inputStream).parse();
        return new SqlSessionFactory(configuration);
    }
}

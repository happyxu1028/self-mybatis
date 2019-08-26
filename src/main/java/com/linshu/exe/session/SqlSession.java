package com.linshu.exe.session;

import com.linshu.exe.executor.CustomExecutor;
import com.linshu.exe.mapper.CustomerMapper;
import com.linshu.exe.mapping.Configuration;
import com.linshu.exe.mapping.MapperStatement;
import com.linshu.exe.proxy.MapperProxy;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * 作为MyBatis工作的主要顶层API，表示和数据库交互时的会话，完成必要数据库增删改查功能。
 * @author linshu
 */
public class SqlSession {

    private Configuration configuration;

    private CustomExecutor customExecutor;

    public SqlSession(Configuration configuration, CustomExecutor customExecutor) {
        this.configuration = configuration;
        this.customExecutor = customExecutor;
    }

    public <T> T   getMapper(Class<T> clazz) {

        return (T)Proxy.newProxyInstance(clazz.getClassLoader(),
                                        new Class<?>[]{clazz},
                                        new MapperProxy(this));
    }

    public <T> List<T> selectList(String statementKey, Object arg) {
        MapperStatement mapperStatement = configuration.getStatementMap().get(statementKey);
        return customExecutor.doQueryList(mapperStatement,arg);
    }
}

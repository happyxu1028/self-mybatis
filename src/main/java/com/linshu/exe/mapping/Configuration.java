package com.linshu.exe.mapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MyBatis所有的配置信息都保存在Configuration对象之中，配置文件中的大部分配置都会存储到该类中
 * @author linshu
 */
public class Configuration {

    //mybatis-config.xml
    private Environment environment;

    //xxMapper.xml
    private Map<String, MapperStatement> statementMap = new ConcurrentHashMap<String, MapperStatement>();

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Map<String, MapperStatement> getStatementMap() {
        return statementMap;
    }

    public void setStatementMap(Map<String, MapperStatement> statementMap) {
        this.statementMap = statementMap;
    }
}

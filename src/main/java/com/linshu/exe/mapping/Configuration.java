package com.linshu.exe.mapping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

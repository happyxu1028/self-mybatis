package com.linshu.exe.session;

import com.linshu.exe.executor.CustomExecutor;
import com.linshu.exe.mapping.Configuration;

public class SqlSessionFactory {

    private Configuration configuration;

    public SqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    public SqlSession openSession(){

        CustomExecutor executor = new CustomExecutor(configuration);
        return new SqlSession(configuration,executor);
    }


}

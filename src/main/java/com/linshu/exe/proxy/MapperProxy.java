package com.linshu.exe.proxy;

import com.linshu.exe.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapperProxy implements InvocationHandler {

    private SqlSession sqlSession;

    public MapperProxy(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        String statementKey = method.getDeclaringClass().getName()+"."+method.getName();

        Class<?> returnType = method.getReturnType();
        if(Collections.class.isAssignableFrom(returnType)){
            // 如果返回是集合类型及子类->查询多条数据,返回List
            //TODO
        }else if(Map.class.isAssignableFrom(returnType)){
            // 返回Map类型
            //TODO
        }else{
            // 返回对象
            List<Object> dataList = sqlSession.selectList(statementKey, null == args ? null : args[0]);
            if(null == dataList){
                return null;
            }
            if(dataList.size() > 1){
                throw new RuntimeException("there has more than 1 result");
            }
            return dataList.get(0);
        }

        return null;
    }
}

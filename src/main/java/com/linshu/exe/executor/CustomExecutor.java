package com.linshu.exe.executor;

import com.linshu.exe.mapping.Configuration;
import com.linshu.exe.mapping.MapperStatement;
import com.linshu.exe.parsing.GenericTokenParser;
import com.linshu.exe.pool.MyDataSource;
import com.linshu.exe.reflection.ReflectionUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomExecutor {


    private MyDataSource dataSource;


    public CustomExecutor(Configuration configuration) {
        dataSource = new MyDataSource(configuration.getEnvironment());
    }


    public <T> List<T> doQueryList(MapperStatement mapperStatement, Object arg) {
        List<T> resultList = new ArrayList<>();

        Connection connection = null;

        PreparedStatement preparedStatement = null;

        ResultSet resultSet = null;

        try {
            connection = dataSource.getConnection();
            String parseSql = GenericTokenParser.parse(mapperStatement.getSql());
            preparedStatement = connection.prepareStatement(parseSql);

            if(arg instanceof Integer){
                preparedStatement.setInt(1,(Integer) arg);
            }else  if(arg instanceof Long){
                preparedStatement.setLong(1,(Long) arg);
            }else  if(arg instanceof Double){
                preparedStatement.setDouble(1,(Double) arg);
            }else  if(arg instanceof String){
                preparedStatement.setString(1,(String) arg);
            }

            resultSet = preparedStatement.executeQuery();
            // 处理查询后的结果
            handleResultSet(mapperStatement,resultList,resultSet);

            return resultList;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(null != resultSet){
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(null != preparedStatement){
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            try {
                dataSource.freeConnection(connection);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 处理结果集
     * @param mapperStatement
     * @param resultList
     * @param resultSet
     * @param <T>
     */
    private <T> void handleResultSet(MapperStatement mapperStatement, List<T> resultList, ResultSet resultSet) {
        Class<T> clazz = null;
        try {
            clazz = (Class<T>) Class.forName(mapperStatement.getResultType());
            while(resultSet.next()){
                T entity = clazz.newInstance();

                //把从数据库中查询到的结果集字段映射到entity中去
                ReflectionUtil.setProToBeanFromResult(entity,resultSet);

                resultList.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.linshu.exe.reflection;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ReflectionUtil {

    /**
     * 结果集中的数据,映射到javaBean中
     * @param target
     * @param resultSet
     * @throws SQLException
     */
    public  static void setProToBeanFromResult(Object target, ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();

        int  count = metaData.getColumnCount();
        Field[] declaredFields = target.getClass().getDeclaredFields();
        for(int i = 0; i < count; i++){
            String columnName = metaData.getColumnName(i+1).replace("_","").toUpperCase();
            for(int j = 0; j < declaredFields.length; j++){
                String filedName = declaredFields[j].getName().toUpperCase();
                if(columnName.equalsIgnoreCase(filedName)){
                    if(declaredFields[j].getType().getSimpleName().equals("Integer")){
                        setProToBean(target,declaredFields[j].getName(),resultSet.getInt(metaData.getColumnName(i+1)));
                    } else if (declaredFields[j].getType().getSimpleName().equals("Long")){
                        setProToBean(target,declaredFields[j].getName(),resultSet.getLong(metaData.getColumnName(i+1)));
                    }else if (declaredFields[j].getType().getSimpleName().equals("Double")){
                        setProToBean(target,declaredFields[j].getName(),resultSet.getDouble(metaData.getColumnName(i+1)));
                    }else if (declaredFields[j].getType().getSimpleName().equals("Float")){
                        setProToBean(target,declaredFields[j].getName(),resultSet.getFloat(metaData.getColumnName(i+1)));
                    }else if (declaredFields[j].getType().getSimpleName().equals("Date")){
                        setProToBean(target,declaredFields[j].getName(),resultSet.getDate(metaData.getColumnName(i+1)));
                    }else if (declaredFields[j].getType().getSimpleName().equals("Boolean")){
                        setProToBean(target,declaredFields[j].getName(),resultSet.getBoolean(metaData.getColumnName(i+1)));
                    }else if (declaredFields[j].getType().getSimpleName().equals("BigDecimal")){
                        setProToBean(target,declaredFields[j].getName(),resultSet.getBigDecimal(metaData.getColumnName(i+1)));
                    }else if (declaredFields[j].getType().getSimpleName().equals("String")){
                        setProToBean(target,declaredFields[j].getName(),resultSet.getString(metaData.getColumnName(i+1)));
                    }
                    break;
                }
            }
        }
    }

    private static void setProToBean(Object target, String name, Object value) {
        try {
            Field declaredField = target.getClass().getDeclaredField(name);
            declaredField.setAccessible(true);
            declaredField.set(target,value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

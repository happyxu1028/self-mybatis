//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.linshu.exe.parsing;

import java.util.HashMap;

import com.linshu.exe.mapping.MapperStatement;

import com.linshu.exe.mapping.Configuration;
import com.linshu.exe.mapping.Environment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;

public class XMLConfigBuilder {

    private XPathParser parser;


    public XMLConfigBuilder(InputStream inputStream) {
        this.parser = new XPathParser(inputStream);
    }


    public Configuration parse() {
        Node dataSourceNode = parser.xnode("/configuration/environments/environment/dataSource");
        Properties properties = new Properties();
        NodeList propertyNodeList = dataSourceNode.getChildNodes();

        // 读取DataSource的属性
        for (int i = 0; i < propertyNodeList.getLength(); i++) {
            Node item = propertyNodeList.item(i);
            if (Node.ELEMENT_NODE == item.getNodeType()) {
                properties.setProperty(item.getAttributes().getNamedItem("name").getNodeValue(), item.getAttributes().getNamedItem("value").getNodeValue());
            }
        }

        // mapper文件解析
        Map<String, MapperStatement> statementMap = new ConcurrentHashMap<String, MapperStatement>();
        Node mapperNodeList = parser.xnode("/configuration/mappers");
        NodeList nodeListChildNodes = mapperNodeList.getChildNodes();
        for (int i = 0; i < nodeListChildNodes.getLength(); i++) {
            Node mapperNode = nodeListChildNodes.item(i);
            if (Node.ELEMENT_NODE == mapperNode.getNodeType()) {

                String resource = mapperNode.getAttributes().getNamedItem("resource").getNodeValue();

                //解析mapper文件->流中
                InputStream mapperResource = this.getClass().getClassLoader().getResourceAsStream(resource);

                this.parser = new XPathParser(mapperResource);
                Element element = parser.getDocument().getDocumentElement();
                String namespace = element.getAttribute("namespace");
                NodeList sqlNodeList = element.getChildNodes();

                for (int sqlIndex = 0; sqlIndex < sqlNodeList.getLength(); sqlIndex++) {
                    Node sqlNode = sqlNodeList.item(sqlIndex);
                    if (Node.ELEMENT_NODE == sqlNode.getNodeType()) {

                        String id = "";
                        String resultType = "";
                        String parameterType = "";

                        // mapper 方法  id 解析
                        Node idNode = sqlNode.getAttributes().getNamedItem("id");
                        if (null == idNode) {
                            throw new RuntimeException("sql id is null");
                        } else {
                            id = sqlNode.getAttributes().getNamedItem("id").getNodeValue();
                        }

                        // mapper 方法  resultType 解析
                        Node resultTypeNode = sqlNode.getAttributes().getNamedItem("resultType");
                        if (null == resultTypeNode) {
                            throw new RuntimeException("sql resultType is null");
                        } else {
                            resultType = resultTypeNode.getNodeValue();
                        }

                        // mapper 方法  parameterType 解析
                        Node paramNode = sqlNode.getAttributes().getNamedItem("parameterType");
                        if (null == paramNode) {
                            throw new RuntimeException("sql parameterType is null");
                        } else {
                            parameterType = paramNode.getNodeValue();
                        }

                        String sql = sqlNode.getTextContent();

                        MapperStatement mapperStatement = new MapperStatement();
                        mapperStatement.setNamespace(namespace);
                        mapperStatement.setId(id);
                        mapperStatement.setResultType(resultType);
                        mapperStatement.setParameterType(parameterType);
                        mapperStatement.setSql(sql);
                        statementMap.put(namespace + "." + id, mapperStatement);

                    }
                }

            }

        }


        Configuration configuration = new Configuration();

        // 环境配置
        Environment environment = new Environment();
        environment.setUsername(properties.getProperty("username"));
        environment.setPassword(properties.getProperty("password"));
        environment.setUrl(properties.getProperty("url"));
        environment.setDriver(properties.getProperty("driver"));

        configuration.setEnvironment(environment);
        configuration.setStatementMap(statementMap);
        return configuration;

    }


}

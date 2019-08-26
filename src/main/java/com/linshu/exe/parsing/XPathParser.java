//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.linshu.exe.parsing;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

public class XPathParser {
    private final Document document;
//    private boolean validation;
//    private EntityResolver entityResolver;
//    private Properties variables;
    private XPath xpath;



    public XPathParser(InputStream inputStream) {
        this.xpath = createXpath();
        this.document = this.createDocument(new InputSource(inputStream));
    }

    private XPath createXpath() {
        XPathFactory factory = XPathFactory.newInstance();
        return factory.newXPath();
    }

    public Document getDocument() {
        return document;
    }

    public XPath getXpath() {
        return xpath;
    }

    public void setXpath(XPath xpath) {
        this.xpath = xpath;
    }

    private Document createDocument(InputSource inputSource) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(false);
            factory.setCoalescing(false);
            factory.setExpandEntityReferences(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
                public void error(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                public void warning(SAXParseException exception) throws SAXException {
                }
            });
            return builder.parse(inputSource);
        } catch (Exception var4) {
            throw new RuntimeException("Error creating document instance.  Cause: " + var4, var4);
        }
    }

    /**
     * 根据表达式解析xml节点
     * @param expression
     * @return
     */
    public Node xnode(String expression) {
        Node node = null;
        try {
            node =  (Node)this.xpath.evaluate(expression, document, XPathConstants.NODE);
        } catch (Exception var5) {
            throw new RuntimeException("Error evaluating XPath.  Cause: " + var5, var5);
        }
        return node;
    }
}

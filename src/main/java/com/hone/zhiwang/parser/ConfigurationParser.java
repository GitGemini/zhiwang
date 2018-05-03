package com.hone.zhiwang.parser;

import com.hone.zhiwang.application.ApplicationConfiguration;
import com.hone.zhiwang.db.AbstractDBHelper;
import com.hone.zhiwang.db.DBHelper;
import com.hone.zhiwang.db.MysqlHelper;
import com.hone.zhiwang.keywords.FileAdapter;
import com.hone.zhiwang.keywords.StoreAdapter;
import com.hone.zhiwang.download.DefaultDownloadHandlerFactory;
import com.hone.zhiwang.download.DownloadHandlerFactory;
import com.hone.zhiwang.strategy.DownloadStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.List;

public class ConfigurationParser {
    private static Logger logger = LoggerFactory.getLogger(ConfigurationParser.class);

    private static Document document = null;

    private ApplicationConfiguration configuration = null;

    public ApplicationConfiguration parser() throws Exception {
        logger.info("解析配置文件...");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbFactory.newDocumentBuilder();
        configuration = new ApplicationConfiguration();
        document = db.parse(this.getClass().getResourceAsStream("/config.xml"));
        parserConfiguration();
        parserStrategies();
        parserStoreAdapter();
        parserDBHelper();
        parserDownloadHandlerFactory();
        return configuration;
    }

    private void parserConfiguration() throws Exception {
        NodeList nodeList = document.getElementsByTagName("configuration");
        Node item = nodeList.item(0);
        NodeList childNodes = item.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            switch (childNode.getNodeName()) {
            case "path":
                if (childNode.getTextContent() == null) {
                    configuration.setPath("D:/test");
                } else {
                    configuration.setPath(childNode.getTextContent());
                }
                break;
            case "fileMinSize":
                if (childNode.getTextContent() == null) {
                    configuration.setFileMinSize(10000);
                } else {
                    configuration.setFileMinSize(Long.parseLong(childNode.getTextContent()));
                }
                break;
            case "scope":
                if (childNode.getTextContent() == null) {
                    configuration.setScope("篇名");
                } else {
                    configuration.setScope(childNode.getTextContent());
                }
                break;
            case "keyIndex":
                if (childNode.getTextContent() == null) {
                    configuration.setKeyIndex(1);
                } else {
                    configuration.setKeyIndex(Integer.parseInt(childNode.getTextContent()));
                }
                break;
            case "pageIndex":
                if (childNode.getTextContent() == null) {
                    configuration.setPageIndex(1);
                } else {
                    configuration.setPageIndex(Integer.parseInt(childNode.getTextContent()));
                }
                break;
            default:
                break;
            }
        }
    }

    private void parserStrategies() throws Exception {
        NodeList nodeList = document.getElementsByTagName("strategies");
        Node item = nodeList.item(0);
        NodeList childNodes = item.getChildNodes();
        List<DownloadStrategy> strategies = new ArrayList<>();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            switch (childNode.getNodeName()) {
            case "strategy":
                NamedNodeMap attributes = childNode.getAttributes();
                if (attributes.getNamedItem("class").getTextContent() != null) {
                    Class<?> c1 = Class.forName(attributes.getNamedItem("class").getTextContent());
                    Object instance = c1.newInstance();
                    if (instance instanceof DownloadStrategy) {
                        strategies.add((DownloadStrategy) instance);
                    }
                }
                break;
            default:
                break;
            }
        }
        configuration.setStrategies(strategies);
    }

    private void parserStoreAdapter() throws Exception{
        NodeList nodeList = document.getElementsByTagName("storeAdapter");
        Node item = nodeList.item(0);
        NamedNodeMap attributes = item.getAttributes();
        if (attributes.getNamedItem("class").getTextContent() != null) {
            Class<?> c1 = Class.forName(attributes.getNamedItem("class").getTextContent());
            Object instance = c1.newInstance();
            if (instance instanceof StoreAdapter) {
                configuration.setStoreAdapter((StoreAdapter) instance);
            }
        }
        if (configuration.getStoreAdapter() == null) {
            configuration.setStoreAdapter(new FileAdapter());
        }
    }

    public void parserDBHelper() throws Exception{
        NodeList nodeList = document.getElementsByTagName("dataSource");
        Node item = nodeList.item(0);
        NamedNodeMap attributes = item.getAttributes();
        if (attributes.getNamedItem("class").getTextContent() != null) {
            Class<?> c1 = Class.forName(attributes.getNamedItem("class").getTextContent());
            Object instance = c1.newInstance();
            if (instance instanceof DBHelper) {
                configuration.setDbHelper((DBHelper) instance);
            }
        }
        if (configuration.getStoreAdapter() == null) {
            configuration.setDbHelper(new MysqlHelper());
        }
        AbstractDBHelper dbHelper = (AbstractDBHelper) configuration.getDbHelper();
        NodeList childNodes = item.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            switch (childNode.getNodeName()) {
            case "url":
                if (childNode.getTextContent() == null) {
                    dbHelper.setUrl("jdbc:mysql://localhost:3306/pachong");
                } else {
                    dbHelper.setUrl(childNode.getTextContent());
                }
                break;
            case "name":
                if (childNode.getTextContent() == null) {
                    dbHelper.setName("com.mysql.jdbc.Driver");
                } else {
                    dbHelper.setName(childNode.getTextContent());
                }
                break;
            case "user":
                if (childNode.getTextContent() == null) {
                    dbHelper.setUser("root");
                } else {
                    dbHelper.setUser(childNode.getTextContent());
                }
                break;
            case "password":
                if (childNode.getTextContent() == null) {
                    dbHelper.setPassword("123");
                } else {
                    dbHelper.setPassword(childNode.getTextContent());
                }
                break;
            default:
                break;
            }
        }
    }

    private void parserDownloadHandlerFactory() throws Exception{
        NodeList nodeList = document.getElementsByTagName("downloadHandlerFactory");
        Node item = nodeList.item(0);
        NamedNodeMap attributes = item.getAttributes();
        if (attributes.getNamedItem("class").getTextContent() != null) {
            Class<?> c1 = Class.forName(attributes.getNamedItem("class").getTextContent());
            Object instance = c1.newInstance();
            if (instance instanceof DownloadHandlerFactory) {
                configuration.setDownloadHandlerFactory((DownloadHandlerFactory) instance);
            }
        }
        if (configuration.getDownloadHandlerFactory() == null) {
            configuration.setDownloadHandlerFactory(new DefaultDownloadHandlerFactory());
        }
    }
}

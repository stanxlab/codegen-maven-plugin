package io.github.stanxlab.codegen.util;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class PomPluginsMerger {

    /**
     * 将 originalPomFile 文件中的 plugin部分合并到 mergedPomFile 中
     *
     * @param originalPomFile
     * @param mergedPomFile
     */
    public static void merge(File originalPomFile, File mergedPomFile) {
        try {
            // 文件路径请根据实际情况调整
//            File originalPomFile = new File("path/to/originalPomFile.xml");
//            File mergedPomFile = new File("path/to/mergedPomFile.xml");

            // 解析两个pom文件
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc1 = dBuilder.parse(originalPomFile);
            Document doc2 = dBuilder.parse(mergedPomFile);

            // 获取pom1的plugins节点
            NodeList pluginsFromPom1 = doc1.getElementsByTagName("plugins");
            if (pluginsFromPom1.getLength() > 0) {
                Node pluginsNodeFromPom1 = pluginsFromPom1.item(0);

                // 查找或创建pom2中的<build><plugins>
                Node buildNodeInPom2 = getOrCreateChildElement(doc2.getDocumentElement(), "build");
                Node pluginsNodeInPom2 = getOrCreateChildElement(buildNodeInPom2, "plugins");

                for (int i = 0; i < pluginsNodeFromPom1.getChildNodes().getLength(); i++) {
                    Node pluginNode = pluginsNodeFromPom1.getChildNodes().item(i);
                    if (pluginNode.getNodeType() == Node.ELEMENT_NODE) {
                        Node importedPlugin = doc2.importNode(pluginNode, true); // 导入节点
                        pluginsNodeInPom2.appendChild(importedPlugin);
                    }
                }

                // 写入合并后的结果到pom2.xml
                writeDocumentToFile(doc2, mergedPomFile);
                System.out.println("Plugins from originalPomFile.xml have been merged into mergedPomFile.xml.");
            } else {
                System.out.println("No <plugins> found in originalPomFile.xml.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Node getOrCreateChildElement(Node parentNode, String childElementName) {
        NodeList nodeList = parentNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(childElementName)) {
                return node;
            }
        }
        // 如果找不到，则创建并返回新节点
        Element newElement = parentNode.getOwnerDocument().createElement(childElementName);
        parentNode.appendChild(newElement);
        return newElement;
    }

    private static void writeDocumentToFile(Document doc, File file) throws TransformerException, IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }
}

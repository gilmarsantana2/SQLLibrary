package sqlibrary.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sqlibrary.connection.DBSettings;
import sqlibrary.connection.DBType;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class Store {

    public static DBSettings readFile() {
        File xmlFile = new File("database.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList node = doc.getElementsByTagName("settings");
            // now XML is loaded as Document in memory, lets convert it to Object List
            return getSettings(node.item(0));
        } catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    private static DBSettings getSettings(Node node) {
        // XMLReaderDOM domReader = new XMLReaderDOM();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            var tipo = getTagValue("type", element);
            DBType type = switch (tipo){
                case "SQLITE" -> DBType.SQLITE;
                case "MYSQL" -> DBType.MYSQL;
                case "MARIA_DB" -> DBType.MARIA_DB;
                case "HSQLDB" -> DBType.HSQLDB;
                default -> DBType.SQLITE;
            };
            var host = getTagValue("host", element);
            var porta = Integer.parseInt(getTagValue("porta", element));
            var database = getTagValue("database", element);
            var user = getTagValue("user", element);
            var password = getTagValue("password", element);
            return new DBSettings(type, host, porta, database, user, password);
        }
        return null;
    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public static void createFile(DBSettings set) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            // add elements to Document
            Element rootElement = doc.createElement("DBSettings");
            // append root element to document
            doc.appendChild(rootElement);

            // append first child element to root element
            rootElement.appendChild(createUserElement(doc, set));


            // for output to file, console
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            // for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            // write to console or file
            //StreamResult console = new StreamResult(System.out);
            StreamResult file = new StreamResult(new File("database.xml"));

            // write data
            //transformer.transform(source, console);
            transformer.transform(source, file);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Node createUserElement(Document doc, DBSettings set) {
        Element settings = doc.createElement("settings");

        settings.appendChild(createUserElements(doc, "type", set.getType().name()));
        settings.appendChild(createUserElements(doc, "host", set.getHost()));
        settings.appendChild(createUserElements(doc,"porta", set.getPorta().toString()));
        settings.appendChild(createUserElements(doc, "database", set.getDataBase()));
        settings.appendChild(createUserElements(doc, "user", set.getUser()));
        settings.appendChild(createUserElements(doc, "password", set.getPassword()));

        return settings;
    }

    // utility method to create text node
    private static Node createUserElements(Document doc, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }

    private static void modeify(String[] args) {
        String filePath = "users.xml";
        File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();

            // parse xml file and load into document
            Document doc = dBuilder.parse(xmlFile);

            doc.getDocumentElement().normalize();

            // update Element value
            updateElementValue(doc);

            // delete element
            deleteElement(doc);

            // add new element
            addElement(doc);

            // write the updated document to file or console
            writeXMLFile(doc);

        } catch (SAXException | ParserConfigurationException | IOException | TransformerException e1) {
            e1.printStackTrace();
        }
    }

    private static void writeXMLFile(Document doc)
            throws TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
        doc.getDocumentElement().normalize();
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File("users_updated.xml"));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        System.out.println("XML file updated successfully");
    }

    /**
     * Add a new element salary to user element.
     * @param doc
     */
    private static void addElement(Document doc) {
        NodeList users = doc.getElementsByTagName("User");
        Element emp = null;

        // loop for each user
        for (int i = 0; i < users.getLength(); i++) {
            emp = (Element) users.item(i);
            Element salaryElement = doc.createElement("salary");
            salaryElement.appendChild(doc.createTextNode("10000"));
            emp.appendChild(salaryElement);
        }
    }

    /**
     * Delete gender element from User element
     * @param doc
     */
    private static void deleteElement(Document doc) {
        NodeList users = doc.getElementsByTagName("User");
        Element user = null;
        // loop for each user
        for (int i = 0; i < users.getLength(); i++) {
            user = (Element) users.item(i);
            Node genderNode = user.getElementsByTagName("gender").item(0);
            user.removeChild(genderNode);
        }

    }

    /**
     * Update firstName element value to Upper case.
     * @param doc
     */
    private static void updateElementValue(Document doc) {
        NodeList users = doc.getElementsByTagName("User");
        Element user = null;
        // loop for each user
        for (int i = 0; i < users.getLength(); i++) {
            user = (Element) users.item(i);
            Node name = user.getElementsByTagName("firstName").item(0).getFirstChild();
            name.setNodeValue(name.getNodeValue().toUpperCase());
        }
    }
}

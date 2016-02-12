
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Alberto
 */
public class XmlParser
{
    public XmlParser()
    {
        
    }
    
    public String createUserInfo(String name, String surname)
    {
        try
        {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("userInfo");
            doc.appendChild(rootElement);
            
            Element type_node = doc.createElement("name");
            type_node.setTextContent(name);
            rootElement.appendChild(type_node);
            
            type_node = doc.createElement("surname");
            type_node.setTextContent(surname);
            rootElement.appendChild(type_node);
            
            type_node = doc.createElement("following");
            rootElement.appendChild(type_node);
            
            
            String res = getXmlString(doc);
            
            System.out.println(res);
            
            return res;
        } catch (ParserConfigurationException ex)
        {
            Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public String addElement(String xml, String root_name , String element_name, String value)
    {
        Document doc = getDocument(xml);
        Node type_node = doc.getElementsByTagName(root_name).item(0);
        Element new_node = doc.createElement(element_name);
        new_node.setTextContent(value);
        type_node.appendChild(new_node);
        return getXmlString(doc);
    }
    
    public String[] getElements(String xml, String element_name)
    {
        
        Document doc = getDocument(xml);
        
        NodeList nodes = doc.getElementsByTagName(element_name);
        String[] res = new String[nodes.getLength()];
        
        for (int i = 0; i < nodes.getLength(); i++)
            res[i] = nodes.item(i).getTextContent();
        
        return res;
    }
    
    public String removeElement(String xml, String element_name, String value)
    {
        Document doc = getDocument(xml);
         
        NodeList nodes = doc.getElementsByTagName(element_name);
        ArrayList<Node> exiles = new ArrayList<>(); 
        
        //find all nodes with specified name and value
        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node node = nodes.item(i);
            if (value.equals(node.getTextContent()))
                exiles.add(node);
        }
        
        //find all the white spaces
        for (int i = 0; i < exiles.size(); i++)
            for (Node whitespace = exiles.get(i).getNextSibling();
                whitespace != null && whitespace.getNodeType() == Node.TEXT_NODE && whitespace.getTextContent().matches("\\s*");
                whitespace = whitespace.getNextSibling())
                    exiles.add(whitespace);
        
        //remove all finded nodes
        for (Node exile : exiles)
            exile.getParentNode().removeChild(exile);
        
        return getXmlString(doc);
    }
    
    private Document getDocument(String xml)
    {
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            return builder.parse(is);
        } catch (ParserConfigurationException | SAXException | IOException ex)
        {
            Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private String getXmlString(Document doc)
    {
        try
        {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(domSource, result);
            return writer.toString();
            
        } catch (TransformerException ex)
        {
            Logger.getLogger(XmlParser.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
            
}

package com.benz.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.benz.CustomerOuterClass.Customer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XMLUtils {

	public final static String DIR = "/c/tmp/merc-benz/customers/xml/";

	// instantiate the factory
	private static DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	private static TransformerFactory tf = TransformerFactory.newInstance();

	/**
	 * creates filename with username of customer and writes details as XML
	 * 
	 * @param customer
	 */
	public static void writeToXML(Customer customer) {
		final String fileName = DIR + customer.getUsername() + ".xml";
		try {
			Document doc = dbf.newDocumentBuilder().newDocument();

			Element root = doc.createElement("customer");
			doc.appendChild(root);

			Element username = doc.createElement("username");
			root.appendChild(username);
			username.appendChild(doc.createTextNode(customer.getUsername()));

			Element firstname = doc.createElement("firstname");
			root.appendChild(firstname);
			firstname.appendChild(doc.createTextNode(customer.getFirstname()));

			Element lastname = doc.createElement("lastname");
			root.appendChild(lastname);
			lastname.appendChild(doc.createTextNode(customer.getLastname()));

			Element age = doc.createElement("age");
			root.appendChild(age);
			age.appendChild(doc.createTextNode("" + customer.getAge()));

			Transformer transformer = tf.newTransformer();
			transformer.transform(new DOMSource(doc), new StreamResult(new File(fileName)));

		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
	}

	private static final XmlMapper xmlMapper = new XmlMapper();
	private static final ObjectMapper objMapper = new ObjectMapper();

	/**
	 * 
	 * @param username
	 * @return
	 */
	public static String readFromXML(String username) {
		final String fileName = DIR + username + ".xml";

		try {
			JsonNode t = xmlMapper.readTree(new File(fileName));
			ObjectNode node = JsonNodeFactory.instance.objectNode();
			node.put("username", t.get("username").asText());
			node.put("firstname", t.get("firstname").asText());
			node.put("lastname", t.get("lastname").asText());
			node.put("age", t.get("age").asInt());
			return node.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

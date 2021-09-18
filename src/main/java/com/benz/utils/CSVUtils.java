package com.benz.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Retryable;

import com.benz.CustomerOuterClass.Customer;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CSVUtils {

	private static final Logger LOG = LoggerFactory.getLogger(CSVUtils.class);

	public final static String DIR = "/c/tmp/merc-benz/customers/csv/";

	/**
	 * creates filename with username of customer and writes details as CSV
	 * 
	 * @param customer
	 */
	public static void writeToCSV(final Customer customer) {
		String csv = String.join(",", customer.getUsername(), customer.getFirstname(), customer.getLastname(),
				"" + customer.getAge());
		writeFile(DIR + customer.getUsername() + ".csv", csv);
	}

	/**
	 * 
	 * @param username
	 * @return
	 */
	public static String readFromCSV(final String username) {
		final String fileName = DIR + username + ".csv";
		try {
			String[] tmp = (new String(Files.readAllBytes(Paths.get(fileName)))).split(",");
			ObjectNode node = JsonNodeFactory.instance.objectNode();
			node.put("username", tmp[0]);
			node.put("firstname", tmp[1]);
			node.put("lastname", tmp[2]);
			node.put("age", Integer.parseInt(tmp[3]));
			return node.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * writes data to file given
	 * 
	 * @param fileName
	 * @param data
	 */
	@Retryable
	private static void writeFile(final String fileName, final String data) {
		try {
			Files.write(Paths.get(fileName), data.getBytes());
		} catch (IOException e) {
			LOG.error("Error writing file: " + fileName, e);
		}
	}
}

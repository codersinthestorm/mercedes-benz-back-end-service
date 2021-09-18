package com.benz;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.benz.CustomerOuterClass.Customer;
import com.benz.aes.AES;
import com.benz.utils.CSVUtils;
import com.benz.utils.XMLUtils;

@RestController
@SpringBootApplication
@RequestMapping("/api/v1/benz/customer")
public class BackEndServiceApplication {

	private static final Logger LOG = LoggerFactory.getLogger(BackEndServiceApplication.class);

	public static void main(String[] args) {
		createDirIfNeeded(XMLUtils.DIR);
		createDirIfNeeded(CSVUtils.DIR);
		SpringApplication.run(BackEndServiceApplication.class, args);
	}

	/**
	 * creates xml/csv dirs if needed
	 * 
	 * @param dirName
	 */
	@Retryable
	private static void createDirIfNeeded(final String dirName) {
		try {
			Files.createDirectories(Paths.get(dirName));
		} catch (IOException e) {
			LOG.error("Unable to create directory: " + dirName + "; " + e.getMessage());
		}
	}

	@GetMapping(produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public byte[] read(@RequestParam(name = "username", required = true) String username) {
		String file = null;
		if (Files.exists(Paths.get(XMLUtils.DIR + username + ".xml"))) {
			file = XMLUtils.readFromXML(username);
		} else if (Files.exists(Paths.get(CSVUtils.DIR + username + ".csv"))) {
			file = CSVUtils.readFromCSV(username);
		}
		if (file != null) {
			return AES.encrypt(file.getBytes(), AES.SECRET, AES.SALT);
		}
		return null;
	}

	/**
	 * kafka message listener
	 * 
	 * @param customer
	 * @param partition
	 * @param offset
	 * @param fileType
	 */
	@Retryable
	@KafkaListener(topics = "merc-benz-topic")
	public void listen(@Payload final Customer customer,
			@Header(KafkaHeaders.RECEIVED_PARTITION_ID) final int partition,
			@Header(KafkaHeaders.OFFSET) final long offset, @Header("fileType") final String fileType) {

		LOG.info("Received message with partition {} and offset {}; customer details: {}, fileType: {}", partition,
				offset, customer.toString(), fileType);

		if (fileType.equalsIgnoreCase("xml")) {
			XMLUtils.writeToXML(customer);
		} else if (fileType.equalsIgnoreCase("csv")) {
			CSVUtils.writeToCSV(customer);
		}
	}
}

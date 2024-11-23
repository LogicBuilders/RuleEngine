package com.codewarriors.service;

import com.codewarriors.dto.ResultTransactionDTO;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriterUtil {
	public static void writeCSV(List<ResultTransactionDTO> transactions, String fileName) throws IOException {
		// Create a FileWriter and CSVWriter to write to a CSV file
		FileWriter fileWriter = new FileWriter(fileName);
		CSVWriter csvWriter = new CSVWriter(fileWriter);

		// Write header to the CSV
		String[] header = { "transaction_id", "sender_account", "sender_routing_number", "sender_name",
				"sender_address", "receiver_account", "receiver_routing_number", "receiver_name", "receiver_address",
				"transaction_amount" };
		csvWriter.writeNext(header);

		// Write data to the CSV
		for (ResultTransactionDTO transaction : transactions) {
			String[] record = { transaction.getTransactionId(), transaction.getSenderAccount(),
					transaction.getSenderRoutingNumber(), transaction.getSenderName(), transaction.getSenderAddress(),
					transaction.getReceiverAccount(), transaction.getReceiverRoutingNumber(),
					transaction.getReceiverName(), transaction.getReceiverAddress(),
					String.valueOf(transaction.getTransactionAmount()) };
			csvWriter.writeNext(record);
		}

		// Close the CSVWriter
		csvWriter.close();
	}

}
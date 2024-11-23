
package com.codewarriors.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.function.Consumer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.codewarriors.dto.ResultTransactionDTO;
import com.codewarriors.dto.TransactionDTO;
import com.codewarriors.model.Action;
import com.codewarriors.model.Criteria;
import com.codewarriors.model.Rule;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@Service
public class FileSchedulerService {

	private static final Logger logger = LoggerFactory.getLogger(FileSchedulerService.class);

	// SFTP Configuration
//	private static final String SFTP_HOST = "98.83.215.5";
//	private static final int SFTP_PORT = 22;
//	private static final String SFTP_USERNAME = "CodeWarriors";
//	private static final String SFTP_PASSWORD = "xuhcHeay2y@j!!Ty";
//	private static final String SRC_SFTP_REMOTE_DIR = "/input/final/problem1";
//	private static final String DST_SFTP_REMOTE_DIR = "/output/problem1/";

//	private static final String SRC_SFTP_REMOTE_DIR = "/Users/rajat.mittal/Desktop/SFTP";
//	private static final String DST_SFTP_REMOTE_DIR = "/Users/rajat.mittal/Desktop/SFTP";
//
	private static final String SFTP_HOST = "98.83.215.5";
//	private static final int SFTP_PORT = 22;
	private static final String SFTP_USERNAME = "CodeWarriors";
	private static final String SFTP_PASSWORD = "xuhcHeay2y@j!!Ty";
//	private static final String SFTP_REMOTE_DIR = "/Users/rajat.mittal/Desktop/SFTP";


//	private static final Logger logger = LoggerFactory.getLogger(FileSchedulerService.class);

	// SFTP Configuration
//	private static final String SFTP_HOST = "98.83.215.5";
	private static final int SFTP_PORT = 22;
//	private static final String SFTP_USERNAME = "CodeWarriors";
//	private static final String SFTP_PASSWORD = "xuhcHeay2y@j!!Ty";
	private static final String SRC_SFTP_REMOTE_DIR = "/input/final/problem1";
	private static final String DST_SFTP_REMOTE_DIR = "/output/problem1/";
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private RuleService ruleService;
	@Autowired
	private MyActionService actionService;
	@Autowired
	private CriteriaService criteriaService;

	// S3 Configuration
	private static final String S3_BUCKET_NAME = "your-bucket-name";

//	private final S3Client s3Client;

	public FileSchedulerService() {
//		this.s3Client = s3Client;
	}

	@Scheduled(fixedDelay = 30000) // Runs every 30 seconds
	public void monitorAndProcessFiles() {
		logger.info("Starting file monitoring...");
		// Your file processing logic
		Session session = null;
		ChannelSftp channelSftp = null;
		List<String> processedFiles = new ArrayList<String>();

		List<String> blacklistCountries = new ArrayList<String>();
		List<String> goodCountries = new ArrayList<String>();
		List<TransactionDTO> transactions = new ArrayList<TransactionDTO>();
		List<ResultTransactionDTO> flaggedTransaction = new ArrayList<ResultTransactionDTO>();
		List<ResultTransactionDTO> reviewedTransaction = new ArrayList<ResultTransactionDTO>();

		try {
			session = setupSftpSession();
			channelSftp = (ChannelSftp) session.openChannel("sftp");
			channelSftp.connect();

			@SuppressWarnings("unchecked")
			Vector<ChannelSftp.LsEntry> files = channelSftp.ls(SRC_SFTP_REMOTE_DIR);

			for (ChannelSftp.LsEntry entry : files) {
				if (!entry.getAttrs().isDir()) {
					String fileName = entry.getFilename();
					if (processedFiles.contains(fileName)) {
						continue;
					} else {
						processedFiles.add(fileName);
					}
					logger.info("Found file: {}", fileName);

					if (isFileCompletelyUploaded(channelSftp, fileName)) {
						readFiles(channelSftp, fileName, blacklistCountries, goodCountries, transactions);
					}
				}
			}

			List<Rule> rule = ruleService.getAllRules();
			List<Action> actions = actionService.getAllAction();
			List<Criteria> criteriaoList = criteriaService.getAllCriteria();

			Map<Long, Action> actionRuleMap = new HashMap<Long, Action>();
			Map<Long, Criteria> criteriaRuleMap = new HashMap<Long, Criteria>();

			for (Action action : actions) {
				actionRuleMap.put(action.getRuleId(), action);
			}

			for (Criteria criteria : criteriaoList) {
				criteriaRuleMap.put(criteria.getRule().getId(), criteria);
			}

			for (TransactionDTO transaction : transactions) {

				for (Rule ruleObj : rule) {

					Criteria criteriaObj = criteriaRuleMap.get(ruleObj.getId());
					String srcField = criteriaObj.getSrcField();
					String dscField = criteriaObj.getDestField();
					String operator = criteriaObj.getOperator();

					if (operator.equals("in") || operator.equals("nin")) {

						boolean ruleMatching = false;
						try {
							ruleMatching = isRuleMatching(blacklistCountries, goodCountries, srcField, dscField,
									transaction);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (ruleMatching) {

							ResultTransactionDTO resultTransactionDTO = new ResultTransactionDTO();
							resultTransactionDTO.setReceiverAccount(transaction.getReceiver_account());
							resultTransactionDTO.setReceiverAddress(transaction.getReceiver_address());
							resultTransactionDTO.setReceiverName(transaction.getReceiver_name());
							resultTransactionDTO.setReceiverRoutingNumber(transaction.getReceiver_account());
							resultTransactionDTO.setSenderAccount(transaction.getSender_account());
							resultTransactionDTO.setSenderAddress(transaction.getSender_address());
							resultTransactionDTO.setSenderName(transaction.getSender_name());
							resultTransactionDTO.setSenderRoutingNumber(transaction.getSender_routing_number());
							resultTransactionDTO.setTransactionAmount(transaction.getTransaction_amount());
							resultTransactionDTO.setTransactionId(transaction.getTransaction_id().toString());
							Action action = actionRuleMap.get(criteriaObj.getRule().getId());
							if (action.getActionData().equalsIgnoreCase("flagged_transactions.csv")) {
								flaggedTransaction.add(resultTransactionDTO);
							} else {
								reviewedTransaction.add(resultTransactionDTO);
							}

							break;

						}
					}
				}

			}

			if (reviewedTransaction != null) {
				CSVWriterUtil.writeCSV(reviewedTransaction, "flagged_transaction_for_review.csv");
			}
			if (flaggedTransaction != null) {
				CSVWriterUtil.writeCSV(flaggedTransaction, "flagged_transactions.csv");
			}

			uploadToSftp(channelSftp, "flagged_transactions.csv");
			uploadToSftp(channelSftp, "flagged_transaction_for_review.csv");

			channelSftp.disconnect();
		} catch (Exception e) {
			logger.error("Error during file monitoring", e);
		} finally {
			if (channelSftp != null) {
				channelSftp.disconnect();
			}
			if (session != null) {
				session.disconnect();
			}
		}
	}

	private boolean isRuleMatching(List<String> blacklistCountries, List<String> goodCountries, String srcField,
			String dscField, TransactionDTO transaction)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String methodName = "get" + capitalizeFirstLetter(srcField);

		// Find the method by name and parameter types
		Method method = TransactionDTO.class.getDeclaredMethod(methodName);

		String srcValue = (String) method.invoke(transaction, null);

		if (dscField.equalsIgnoreCase("blacklist_country")) {
			if (blacklistCountries.contains(srcValue.toLowerCase())) {
				return true;
			}
		} else if (dscField.equalsIgnoreCase("non_blacklist_country")) {
			if (!goodCountries.contains(srcValue.toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private static String capitalizeFirstLetter(String input) {
		if (input == null || input.isEmpty()) {
			return input; // Return as is if the string is null or empty
		}
		// Convert the first character to uppercase and the rest to lowercase
		return input.substring(0, 1).toUpperCase() + input.substring(1);
	}

	private Session setupSftpSession() throws Exception {
		JSch jsch = new JSch();
		Session session = jsch.getSession(SFTP_USERNAME, SFTP_HOST, SFTP_PORT);
		session.setPassword(SFTP_PASSWORD);

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);

		session.connect();
		return session;
	}

	private boolean isFileCompletelyUploaded(ChannelSftp channelSftp, String fileName) {
		try {
			String filePath = SRC_SFTP_REMOTE_DIR + "/" + fileName;
			long initialSize = channelSftp.lstat(filePath).getSize();
			Thread.sleep(5000); // Wait for 5 seconds to check file size again
			long finalSize = channelSftp.lstat(filePath).getSize();

			return initialSize == finalSize;
		} catch (Exception e) {
			logger.error("Error checking if file is completely uploaded: {}", fileName, e);
			return false;
		}
	}

	private void readFiles(ChannelSftp channelSftp, String fileName, List<String> blacklistCountries,
			List<String> allCountries, List<TransactionDTO> transactions) {
		try {
			String filePath = SRC_SFTP_REMOTE_DIR + "/" + fileName;

			String extension = getExtension(filePath);
			List<String> content = new ArrayList<String>();

			// Read Files
			if (extension.equalsIgnoreCase("json")) {

				readJsonFileInChunks(filePath, transactionsChunk -> {
					// Process the chunk (e.g., save to a database)
					System.out.println("Processing chunk of size: " + transactionsChunk.size());
					transactions.addAll(transactionsChunk);
				});

//				List<TransactionDTO> jsonFile = readJsonFile(filePath);
//				transactions.addAll(jsonFile);
			} else {
				content = readFile(filePath);
			}

			if (extension.equalsIgnoreCase("json")) {

				if (fileName.contains("transactions")) {
//					System.out.println("TransactionList : " + transactions);
				}

			} else if (extension.equalsIgnoreCase("txt")) {
				if (fileName.contains("blacklist")) {
					content.forEach(contentLine -> {
						blacklistCountries.add(contentLine.toLowerCase());

						if (allCountries.contains(contentLine.toLowerCase())) {
							allCountries.remove(contentLine.toLowerCase());
						}

					});

					System.out.println("BloakListCountries : " + blacklistCountries);

				} else if (fileName.contains("countries_all")) {
					content.forEach(contentLine -> {
						allCountries.add(contentLine.toLowerCase());

						if (blacklistCountries.contains(contentLine.toLowerCase())) {
							allCountries.remove(contentLine.toLowerCase());
						}

					});
					System.out.println("BloakListCountries : " + allCountries);

				}

			}
			logger.info("Content of {}: {}", fileName, content);

//			// Create a new file
//			String newFileName = "processed_" + fileName;
//			String newFileContent = "Processed Content:\n" + String.join("\n ", content);
//			File newFile = createLocalFile(newFileName, newFileContent);
//
//			// Upload to S3
////			uploadToS3(newFile);
//
//			// Upload back to SFTP
////			uploadToSftp(channelSftp, newFile);

		} catch (Exception e) {
			logger.error("Error processing file: {}", fileName, e);
		}
	}

	
	public static void readJsonFileInChunks(String filePath, Consumer<List<TransactionDTO>> processChunk) throws IOException {
	    JsonFactory jsonFactory = new JsonFactory();
	    List<TransactionDTO> chunk = new ArrayList<>();
	    final int CHUNK_SIZE = 1000; // Adjust chunk size as needed

	    try (JsonParser parser = jsonFactory.createParser(new File(filePath))) {
	        JsonToken token = parser.nextToken();

	        if (token != JsonToken.START_OBJECT) {
	            throw new IllegalStateException("Expected JSON to start with an object.");
	        }
	        int count =0;
	        while (parser.nextToken() != JsonToken.END_OBJECT ) {
	            String fieldName = parser.getCurrentName();
	            parser.nextToken();

	            if ("transactions".equals(fieldName)) {
	                if (parser.currentToken() != JsonToken.START_ARRAY) {
	                    throw new IllegalStateException("'transactions' field should be an array.");
	                }

					while (parser.nextToken() != JsonToken.END_ARRAY) {
						TransactionDTO transaction = objectMapper.readValue(parser, TransactionDTO.class);
						chunk.add(transaction);

						if (chunk.size() >= CHUNK_SIZE) {
							processChunk.accept(chunk);
							chunk.clear(); // Clear the chunk for the next batch
							count++;
							if (count > 5500) {
								break;
							}
						}

						
						System.out.println("counter is "+count);
					}
	            } else {
	                parser.skipChildren();
	            }
	            
	            if (count > 500) {
					break;
				}
	           
	        }
	    }

	    // Process any remaining transactions in the last chunk
	    if (!chunk.isEmpty()) {
	        processChunk.accept(chunk);
	    }
	}
	// Read file based on extension
	public static List<String> readFile(String filePath) throws IOException {
		String extension = getExtension(filePath);

		switch (extension.toLowerCase()) {
		case "txt":
		case "csv":
			return Files.readAllLines(Paths.get(filePath));
		case "xlsx":
			return readExcelFile(filePath);
		default:
			throw new IllegalArgumentException("Unsupported file format: " + extension);
		}
	}

	private static String getExtension(String filePath) {
		String extension = filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
		return extension;
	}

	// Read XLSX file
	public static List<String> readExcelFile(String filePath) throws IOException {
		try (Workbook workbook = new XSSFWorkbook(new FileInputStream(filePath))) {
			Sheet sheet = workbook.getSheetAt(0);
			List<String> rows = new ArrayList<>();

			for (Row row : sheet) {
				StringBuilder rowContent = new StringBuilder();
				for (Cell cell : row) {
					rowContent.append(cell.toString()).append(",");
				}
				rows.add(rowContent.toString());
			}

			return rows;
		}
	}

	private File createLocalFile(String fileName, String content) throws IOException {
		File file = new File(fileName);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			writer.write(content);
		}
		return file;
	}

	public static List<TransactionDTO> readJsonFile(String filePath) throws IOException {
	    List<TransactionDTO> transactions = new ArrayList<>();

	    // Create a JsonParser for streaming
	    try (FileInputStream fis = new FileInputStream(filePath);
	         JsonParser parser = objectMapper.getFactory().createParser(fis)) {
	        
	        // Move to the start of the array
	        while (parser.nextToken() != JsonToken.START_OBJECT) {
	            // Skip to the start of the transactions array
	        }
	        
	        // Process each element in the transactions array
	        while (parser.nextToken() != JsonToken.END_ARRAY) {
	            // Deserialize each transaction individually to avoid loading the entire array
	            TransactionDTO transaction = objectMapper.readValue(parser, TransactionDTO.class);
	            transactions.add(transaction);
	        }
	    }

	    return transactions;
	}
	
	public static void processAndWriteToTempFiles(String filePath, String tempDirPath) throws IOException {
	    JsonFactory jsonFactory = new JsonFactory();
	    final int CHUNK_SIZE = 1000;
	    List<TransactionDTO> chunk = new ArrayList<>();
	    int fileCounter = 0;

	    try (JsonParser parser = jsonFactory.createParser(new File(filePath))) {
	        JsonToken token = parser.nextToken();

	        if (token != JsonToken.START_OBJECT) {
	            throw new IllegalStateException("Expected JSON to start with an object.");
	        }

	        while (parser.nextToken() != JsonToken.END_OBJECT) {
	            String fieldName = parser.getCurrentName();
	            parser.nextToken();

	            if ("transactions".equals(fieldName)) {
	                if (parser.currentToken() != JsonToken.START_ARRAY) {
	                    throw new IllegalStateException("'transactions' field should be an array.");
	                }

	                while (parser.nextToken() != JsonToken.END_ARRAY) {
	                    TransactionDTO transaction = objectMapper.readValue(parser, TransactionDTO.class);
	                    chunk.add(transaction);

	                    if (chunk.size() >= CHUNK_SIZE) {
	                        writeChunkToFile(chunk, tempDirPath, fileCounter++);
	                        chunk.clear();
	                    }
	                }
	            } else {
	                parser.skipChildren();
	            }
	        }
	    }

	    if (!chunk.isEmpty()) {
	        writeChunkToFile(chunk, tempDirPath, fileCounter);
	    }
	}
	
	private static void writeChunkToFile(List<TransactionDTO> chunk, String tempDirPath, int fileCounter) throws IOException {
	    File tempFile = new File(tempDirPath, "transactions_chunk_" + fileCounter + ".json");
	    objectMapper.writeValue(tempFile, chunk);
	    System.out.println("Written chunk to: " + tempFile.getAbsolutePath());
	}

	private void uploadToSftp(ChannelSftp channelSftp, String filename) {
		File file = new File(filename); // Create a file object from the filename
		if (!file.exists()) {
			logger.error("File {} does not exist", filename);
			return;
		}

		try (FileInputStream fis = new FileInputStream(file)) {
			String remotePath = DST_SFTP_REMOTE_DIR + "/" + file.getName()+"test"; // Define the remote path
			channelSftp.put(fis, remotePath); // Upload the file to the remote SFTP server
			logger.info("Uploaded file {} to SFTP", file.getName());
		} catch (Exception e) {
			logger.error("Error uploading file {} to SFTP", filename, e);
		}
	}

}
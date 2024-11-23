package com.codewarriors.dto;

import java.math.BigDecimal;

public class ResultTransactionDTO {
	private String transactionId;
	private String senderAccount;
	private String senderRoutingNumber;
	private String senderName;
	private String senderAddress;
	private String receiverAccount;
	private String receiverRoutingNumber;
	private String receiverName;
	private String receiverAddress;
	private BigDecimal transactionAmount;

	// Getters and setters
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getSenderAccount() {
		return senderAccount;
	}

	public void setSenderAccount(String senderAccount) {
		this.senderAccount = senderAccount;
	}

	public String getSenderRoutingNumber() {
		return senderRoutingNumber;
	}

	public void setSenderRoutingNumber(String senderRoutingNumber) {
		this.senderRoutingNumber = senderRoutingNumber;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}

	public String getReceiverAccount() {
		return receiverAccount;
	}

	public void setReceiverAccount(String receiverAccount) {
		this.receiverAccount = receiverAccount;
	}

	public String getReceiverRoutingNumber() {
		return receiverRoutingNumber;
	}

	public void setReceiverRoutingNumber(String receiverRoutingNumber) {
		this.receiverRoutingNumber = receiverRoutingNumber;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}
}
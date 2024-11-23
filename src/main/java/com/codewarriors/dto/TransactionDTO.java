package com.codewarriors.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransactionDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	Long transaction_id;
	String sender_account;
	String sender_routing_number;
	String sender_name;
	String sender_address;
	String sendor_address_country;

	String receiver_account;
	String receiver_routing_number;
	String receiver_name;
	String receiver_address;
	String receiver_address_country;

	public String getSendor_address_country() {
		return sendor_address_country;
	}

	public String getReceiver_address_country() {
		return receiver_address_country;
	}

	BigDecimal transaction_amount;

	public Long getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(Long transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getSender_account() {
		return sender_account;
	}

	public void setSender_account(String sender_account) {
		this.sender_account = sender_account;
	}

	public String getSender_routing_number() {
		return sender_routing_number;
	}

	public void setSender_routing_number(String sender_routing_number) {
		this.sender_routing_number = sender_routing_number;
	}

	public String getSender_name() {
		return sender_name;
	}

	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}

	public String getSender_address() {
		return sender_address;
	}

	public void setSender_address(String sender_address) {
		this.sender_address = sender_address;
		int lastCommaIndex = sender_address.lastIndexOf(",");

		this.sendor_address_country = sender_address.substring(lastCommaIndex + 1).trim();
	}

	public String getReceiver_account() {
		return receiver_account;
	}

	public void setReceiver_account(String receiver_account) {
		this.receiver_account = receiver_account;
	}

	public String getReceiver_routing_number() {
		return receiver_routing_number;
	}

	public void setReceiver_routing_number(String receiver_routing_number) {
		this.receiver_routing_number = receiver_routing_number;
	}

	public String getReceiver_name() {
		return receiver_name;
	}

	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}

	public String getReceiver_address() {
		return receiver_address;
	}

	public void setReceiver_address(String receiver_address) {
		this.receiver_address = receiver_address;

		int lastCommaIndex = receiver_address.lastIndexOf(",");

		this.receiver_address_country = receiver_address.substring(lastCommaIndex + 1).trim();

	}

	public BigDecimal getTransaction_amount() {
		return transaction_amount;
	}

	public void setTransaction_amount(BigDecimal transaction_amount) {
		this.transaction_amount = transaction_amount;
	}

}

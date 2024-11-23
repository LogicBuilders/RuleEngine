package com.codewarriors.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "action") // Consider naming this table with snake_case if your DB convention follows
						// that.
public class Action {

	private static final Logger logger = LoggerFactory.getLogger(Action.class);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "rule_id")
	private Long ruleId;

	@Column(name = "action_type")
	private String actionType;

	@Column(name = "action_data")
	private String actionData;

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getRuleId() {
		return ruleId;
	}

	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getActionData() {
		return actionData;
	}

	public void setActionData(String actionData) {
		this.actionData = actionData;
	}

	// Execute method with logging instead of System.out.println
	public void execute() {
		logger.info("Executing action: {} with data: {}", actionType, actionData);
		// Add logic for executing specific actions
	}

	// Override toString method
	@Override
	public String toString() {
		return "Action{id=" + id + ", ruleId=" + ruleId + ", actionType='" + actionType + "', actionData='" + actionData
				+ "'}";
	}
}
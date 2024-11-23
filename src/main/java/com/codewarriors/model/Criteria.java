package com.codewarriors.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Criteria")
public class Criteria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
//	@Column(name = "rule_id")
//	private Integer ruleId;
	@Column(name = "src_field")
	private String srcField;
	@Column(name = "dest_field")
	private String destField;
	@Column(name = "operator")
	private String operator; // Value to compare against

    @OneToOne
    @JoinColumn(name = "rule_id")
    private Rule rule;

	public Rule getRule() {
		return rule;
	}
	public void setRule(Rule rule) {
		this.rule = rule;
	}
	// Getters and Setters
	public Criteria() {
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

//	public Integer getRuleId() {
//		return ruleId;
//	}
//
//	public void setRuleId(Integer ruleId) {
//		this.ruleId = ruleId;
//	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getSrcField() {
		return srcField;
	}

	public void setSrcField(String srcField) {
		this.srcField = srcField;
	}

	public String getDestField() {
		return destField;
	}

	public void setDestField(String destField) {
		this.destField = destField;
	}

}
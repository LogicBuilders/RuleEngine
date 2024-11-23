package com.codewarriors.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codewarriors.model.Rule;
import com.codewarriors.repository.RuleRepository;

@Service
public class RuleService {

	private final RuleRepository ruleRepository;

	@Autowired
	public RuleService(RuleRepository actionRepository) {
		this.ruleRepository = actionRepository;
	}

	public Rule getRuleById(Long id) {
		return ruleRepository.findById(id).orElse(null); // findById() is provided by JpaRepository
	}

	public Rule saveRule(Rule rule) {
		return ruleRepository.save(rule); // save() is also provided by JpaRepository
	}

	// Method to get all rules
	public List<Rule> getAllRules() {
		return ruleRepository.findAll(); // findAll() is provided by JpaRepository
	}
}
package com.codewarriors.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codewarriors.model.Action;
import com.codewarriors.model.Criteria;
import com.codewarriors.repository.CriteriaRepository;

@Service
public class CriteriaService {

	private final CriteriaRepository criteriaRepository;

	@Autowired
	public CriteriaService(CriteriaRepository criteriaRepository) {
		this.criteriaRepository = criteriaRepository;
	}

	public Criteria getCriteriaById(Long id) {
		return criteriaRepository.findById(id).orElse(null); // findById() is provided by JpaRepository
	}

	public Criteria saveCriteria(Criteria criteria) {
		return criteriaRepository.save(criteria); // save() is also provided by JpaRepository
	}

	// Get rules by criteria ID
	public List<Criteria> getCriteriaByRuleId(Long ruleId) {
		return criteriaRepository.findByRuleId(ruleId); // Use the method from the repository
	}

	// Method to get all rules
	public List<Criteria> getAllCriteria() {
		return criteriaRepository.findAll(); // findAll() is provided by JpaRepository
	}
}
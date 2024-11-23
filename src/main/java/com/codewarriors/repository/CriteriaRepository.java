package com.codewarriors.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codewarriors.model.Criteria;

@Repository
public interface CriteriaRepository extends JpaRepository<Criteria, Long> {

	Optional<Criteria> findById(Long criteri);
	// Custom queries can be added here if necessary

	// Find rules by the associated criteria ID
	List<Criteria> findByRuleId(Long ruleId);

}
package com.codewarriors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codewarriors.model.Rule;

@Repository
public interface RuleRepository extends JpaRepository<Rule, Long> {
	// Custom queries can be added here if necessary
}
package com.codewarriors.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codewarriors.model.Action;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
	// Custom queries can be added here if necessary
}
package com.codewarriors.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codewarriors.model.Action;
import com.codewarriors.model.Rule;
import com.codewarriors.repository.ActionRepository;

@Service
public class MyActionService {

	private final ActionRepository actionRepository;

	@Autowired
	public MyActionService(ActionRepository actionRepository) {
		this.actionRepository = actionRepository;
	}

	public Action getActionById(Long id) {
		return actionRepository.findById(id).orElse(null); // findById() is provided by JpaRepository
	}

	public Action saveAction(Action action) {
		return actionRepository.save(action); // save() is also provided by JpaRepository
	}

	// Method to get all rules
	public List<Action> getAllAction() {
		return actionRepository.findAll(); // findAll() is provided by JpaRepository
	}
}
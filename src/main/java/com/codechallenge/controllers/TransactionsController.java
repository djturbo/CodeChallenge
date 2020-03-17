package com.codechallenge.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.codechallenge.domains.Transaction;
import com.codechallenge.models.SearchRequest;
import com.codechallenge.models.StatusRequest;
import com.codechallenge.models.StatusResponse;
import com.codechallenge.models.TransactionRequest;
import com.codechallenge.services.TransactionService;

@RestController
public class TransactionsController {

	@Autowired
	TransactionService transactionService;

	@PostMapping("/create")
	public Map<String, Object> create(@RequestBody final TransactionRequest transactionRequest) {

		final Transaction transaction = transactionRequest.getTransaction();
		if (transaction.getReference() == null || transaction.getReference().equals("")) {
			transaction.setReference(String.valueOf(java.lang.System.currentTimeMillis()));
		}

		return this.transactionService.create(transaction);
	}

	@PostMapping("/search")
	public List<Transaction> getTransactions(@RequestBody(required = false) final SearchRequest searchRequest) {
		return this.transactionService.getTransactions(searchRequest);
	}

	@PostMapping("/status")
	public StatusResponse getStatus(@RequestBody final StatusRequest statusRequest) {
		return this.transactionService.getStatus(statusRequest.getReference(), statusRequest.getChannel());
	}

}

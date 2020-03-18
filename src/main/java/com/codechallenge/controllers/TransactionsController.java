package com.codechallenge.controllers;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.codechallenge.domains.Transaction;
import com.codechallenge.exceptions.NoAccountException;
import com.codechallenge.exceptions.NoFundsException;
import com.codechallenge.models.SearchRequest;
import com.codechallenge.models.StatusRequest;
import com.codechallenge.models.StatusResponse;
import com.codechallenge.models.TransactionRequest;
import com.codechallenge.models.TransactionResponse;
import com.codechallenge.services.TransactionService;

@RestController
public class TransactionsController {

	@Autowired
	TransactionService transactionService;

	@Transactional
	@PostMapping("/create")
	public TransactionResponse create(@RequestBody final TransactionRequest transactionRequest)
			throws NoAccountException, NoFundsException {
		return this.transactionService.create(transactionRequest);
	}

	@PostMapping("/search")
	public List<Transaction> getTransactions(@RequestBody(required = false) final SearchRequest searchRequest) {
		return this.transactionService.findTransaction(searchRequest);
	}

	@PostMapping("/status")
	public StatusResponse getStatus(@RequestBody final StatusRequest statusRequest) {
		return this.transactionService.getStatus(statusRequest.getReference(), statusRequest.getChannel());
	}

}

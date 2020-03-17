package com.codechallenge.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.codechallenge.domains.Transaction;
import com.codechallenge.pojos.SearchRequest;
import com.codechallenge.pojos.StatusRequest;
import com.codechallenge.pojos.TransactionRequest;
import com.codechallenge.services.TransactionService;

@RestController
public class TransactionsController {

	@Autowired
	TransactionService transactionService;

	@PostMapping("/create")
	public Map<String, Object> create(@RequestBody final TransactionRequest transactionForm) {

		final Transaction transaction = transactionForm.getTransaction();
		if (transaction.getReference() == null || transaction.getReference().equals("")) {
			transaction.setReference(String.valueOf(java.lang.System.currentTimeMillis()));
		}

		return this.transactionService.create(transaction);
	}

	@PostMapping("/search")
	public List<Transaction> getTransactions(@RequestBody final SearchRequest searchForm) {
		return this.transactionService.getTransactionsByIBAN(searchForm.getIban(), searchForm.getSort());
	}

	@PostMapping("/status")
	public Map<String, Object> getStatus(@RequestBody final StatusRequest statusForm) {
		return this.transactionService.getStatus(statusForm.getReference(), statusForm.getChannel());
	}

}

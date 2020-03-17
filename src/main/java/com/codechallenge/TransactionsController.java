package com.codechallenge;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionsController {

	@Autowired
	TransactionService transactionService;

	@PostMapping("/create")
	public Map<String, Object> create(@RequestBody final TransactionForm transactionForm) {

		final Transaction transaction = transactionForm.getTransaction();
		if (transaction.getReference() == null || transaction.getReference().equals("")) {
			transaction.setReference(String.valueOf(java.lang.System.currentTimeMillis()));
		}

		return this.transactionService.create(transaction);
	}

	@PostMapping("/search")
	public List<Transaction> getTransactions(@RequestBody final SearchForm searchForm) {
		return this.transactionService.getTransactionsByIBAN(searchForm.getIban(), searchForm.getSort());
	}

	@PostMapping("/status")
	public Map<String, Object> getStatus(@RequestBody final StatusForm statusForm) {
		return this.transactionService.getStatus(statusForm.getReference(), statusForm.getChannel());
	}

}

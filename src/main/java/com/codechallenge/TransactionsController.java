package com.codechallenge;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionsController {
	
	@Autowired
	TransactionService transactionService;
	
    @PostMapping("/create")
    private Map<String, Object> create(@RequestBody TransactionForm transactionForm) {
    	
        Transaction transaction = transactionForm.getTransaction();
        if (transaction.getReference() == null ||
        		transaction.getReference().equals("")) {
        	transaction.setReference(String.valueOf(java.lang.System.currentTimeMillis()));
        }
    	
        return transactionService.create(transaction);
    }
    
	@PostMapping("/search")
    private List<Transaction> getTransactions(@RequestBody SearchForm searchForm) {
        return transactionService.getTransactionsByIBAN(searchForm.getIban(), searchForm.getSort());
    }
    
    @PostMapping("/status")
    private Map<String, Object> getStatus(@RequestBody StatusForm statusForm) {
        return transactionService.getStatus(statusForm.getReference(), statusForm.getChannel());
    }
    
}

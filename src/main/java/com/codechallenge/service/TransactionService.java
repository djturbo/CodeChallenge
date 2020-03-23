package com.codechallenge.service;

import java.util.List;

import com.codechallenge.domain.Transaction;
import com.codechallenge.model.SearchRequest;
import com.codechallenge.model.StatusRequest;
import com.codechallenge.model.StatusResponse;
import com.codechallenge.model.TransactionRequest;
import com.codechallenge.model.TransactionResponse;

public interface TransactionService {
	static final String DEFAULT_SORT_PROPERTY = "amount";
	static final int REFERENCE_LENGTH = 6;

	/**
	 * Create a new Transaction
	 * @param transactionRequest
	 * @return TransactionResponse
	 */
	TransactionResponse createTransaction(final TransactionRequest transactionRequest);
	/**
	 * Find Transactions according the searchRequest filter
	 * @param searchRequest
	 * @return List<Transaction>
	 */
	List<Transaction> findTransactions(final SearchRequest searchRequest);
	/**
	 * Find Transaction Status given statusRequest filter
	 * @param statusRequest
	 * @return StatusResponse
	 */
	StatusResponse findTransactionStatus(final StatusRequest statusRequest);

}

package com.codechallenge.controllers;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.codechallenge.domains.Transaction;
import com.codechallenge.models.SearchRequest;
import com.codechallenge.models.StatusRequest;
import com.codechallenge.models.StatusResponse;
import com.codechallenge.models.TransactionRequest;
import com.codechallenge.models.TransactionResponse;
import com.codechallenge.services.TransactionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(value = "Transactions", tags = { "Description", "Api for transactions management" })
public class TransactionsController {

	@Autowired
	TransactionService transactionService;

	@Transactional
	@PostMapping("/")
	@ApiOperation(value = "Creates a new transaction")
	@ApiResponses({ @ApiResponse(code = 200, message = "Transaction created") })
	public TransactionResponse createTransaction(
			@ApiParam(value = "The transaction to be created") @RequestBody final TransactionRequest transactionRequest) {
		return this.transactionService.createTransaction(transactionRequest);
	}

	@PostMapping("/search")
	@ApiOperation(value = "List transactions with filter")
	@ApiResponses({ @ApiResponse(code = 200, message = "List of transactions sent") })
	public List<Transaction> findTransactions(
			@ApiParam(value = "Transaction search params") @RequestBody(required = false) final SearchRequest searchRequest) {
		return this.transactionService.findTransactions(searchRequest);
	}

	@PostMapping("/status")
	@ApiOperation(value = "Get a transaction status")
	@ApiResponses({ @ApiResponse(code = 200, message = "Status of a transaction sent") })
	public StatusResponse findTransactionStatus(
			@ApiParam(value = "Transaction status search params") @RequestBody final StatusRequest statusRequest) {
		return this.transactionService.findTransactionStatus(statusRequest);
	}

}

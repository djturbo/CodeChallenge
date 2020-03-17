package com.codechallenge.services;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.codechallenge.domains.Transaction;
import com.codechallenge.models.SearchRequest;
import com.codechallenge.models.StatusResponse;
import com.codechallenge.models.enums.TransactionChannel;
import com.codechallenge.models.enums.TransactionStatus;
import com.codechallenge.repositories.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	TransactionRepository transactionRepository;

	DecimalFormatSymbols decimalSymbols = DecimalFormatSymbols.getInstance();
	DecimalFormat df = new DecimalFormat("0.00");

	public List<Transaction> getTransactions(final SearchRequest searchRequest) {
		if (searchRequest == null) {
			return this.transactionRepository.findAll(Sort.by(Direction.ASC, "amount"));
		}

		final Direction direction = searchRequest.getSort() == null ? Direction.ASC : searchRequest.getSort();
		final Sort sort = Sort.by(direction, "amount");

		return searchRequest.getIban() == null ? this.transactionRepository.findAll(sort)
				: this.transactionRepository.findByIban(searchRequest.getIban(), sort);
	}

	public Map<String, Object> create(final Transaction transaction) {
		this.transactionRepository.save(transaction);
		final Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("reference", transaction.getReference());
		return returnMap;

	}

	public StatusResponse getStatus(final String reference, final TransactionChannel channel) {
		final Transaction transaction = this.transactionRepository.findByReference(reference);

		final StatusResponse statusResponse = new StatusResponse(reference);
		statusResponse.setStatus(this.transactionStatusFactory(transaction));
		statusResponse.setAmount(this.amountFactory(transaction, channel));
		statusResponse.setFee(this.feeFactory(transaction, channel));

		return statusResponse;
	}

	private TransactionStatus transactionStatusFactory(final Transaction transaction) {
		if (transaction == null) {
			return TransactionStatus.INVALID;
		}

		final LocalDate localDateTransaction = transaction.getDate().toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();
		final LocalDate actualLocalDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		if (localDateTransaction.isBefore(actualLocalDate)) {
			return TransactionStatus.SETTLED;
		} else if (localDateTransaction.isEqual(actualLocalDate)) {
			return TransactionStatus.PENDING;
		} else {
			return TransactionStatus.FUTURE;
		}
	}

	private Double amountFactory(final Transaction transaction, final TransactionChannel channel) {
		if (transaction == null) {
			return null;
		}

		if (channel.equals(TransactionChannel.CLIENT) || channel.equals(TransactionChannel.ATM)) {
			return Math.round((transaction.getAmount() - transaction.getFee()) * 100.0) / 100.0;
		} else {
			return transaction.getAmount();
		}
	}

	private Double feeFactory(final Transaction transaction, final TransactionChannel channel) {
		if (transaction == null || !channel.equals(TransactionChannel.INTERNAL)) {
			return null;
		}

		return transaction.getFee();
	}
}

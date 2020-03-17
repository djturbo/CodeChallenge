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
import org.springframework.stereotype.Service;

import com.codechallenge.domains.Transaction;
import com.codechallenge.repositories.TransactionRepository;

@Service
public class TransactionService {

	@Autowired
	TransactionRepository transactionRepository;

	DecimalFormatSymbols decimalSymbols = DecimalFormatSymbols.getInstance();
	private final String SETTLED = "SETTLED";
	private final String FUTURE = "FUTURE";
	private final String PENDING = "PENDING";
	private final String CLIENT = "CLIENT";
	private final String ATM = "ATM";
	private final String INVALID = "INVALID";

	DecimalFormat df = new DecimalFormat("0.00");

	public List<Transaction> getTransactionsByIBAN(final String iban, final String sort) {
		Sort sorting = null;
		if (sort.equals("asc")) {
			sorting = Sort.by("amount").ascending();
		} else {
			sorting = Sort.by("amount").descending();
		}
		final List<Transaction> listTransactions = this.transactionRepository.findByIban(iban, sorting);

		return listTransactions;
	}

	public Map<String, Object> create(final Transaction transaction) {
		this.transactionRepository.save(transaction);
		final Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("reference", transaction.getReference());
		return returnMap;

	}

	public Map<String, Object> getStatus(final String reference, final String channel) {

		final Map<String, Object> returnMap = new HashMap<>();
		returnMap.put("reference", reference);
		final Transaction transaction = this.transactionRepository.findByReference(reference);
		if (transaction == null) {
			returnMap.put("status", this.INVALID);
			return returnMap;
		}

		final LocalDate localDateTransaction = transaction.getDate().toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();
		final LocalDate actualLocalDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		if (localDateTransaction.isBefore(actualLocalDate)) {
			returnMap.put("status", this.SETTLED);
		} else if (localDateTransaction.isEqual(actualLocalDate)) {
			returnMap.put("status", this.PENDING);
		} else if (localDateTransaction.isAfter(actualLocalDate)) {
			returnMap.put("status", this.FUTURE);
		}

		this.decimalSymbols.setDecimalSeparator('.');

		this.df.setDecimalFormatSymbols(this.decimalSymbols);

		if (channel.equals(this.CLIENT) || channel.equals(this.ATM)) {
			returnMap.put("amount", this.df.format(transaction.getAmount() - transaction.getFee()));
		} else {
			returnMap.put("amount", this.df.format(transaction.getAmount()));
			returnMap.put("fee", this.df.format(transaction.getFee()));
		}

		return returnMap;
	}
}

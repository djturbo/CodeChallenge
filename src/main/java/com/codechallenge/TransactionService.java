package com.codechallenge;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

	@Autowired
	TransactionRepository transactionRepository;

	private static DecimalFormat df = new DecimalFormat("0.00");
	private final String SETTLED = "SETTLED";
	private final String FUTURE = "FUTURE";
	private final String PENDING = "PENDING";
	private final String CLIENT = "CLIENT";
	private final String ATM= "ATM";
	private final String INVALID ="INVALID";
	
	public List<Transaction> getTransactionsByIBAN(String iban, String sort) {
		Sort sorting = null;
		if (sort.equals("asc")) {
			sorting = Sort.by("amount").ascending();
		} else {
			sorting = Sort.by("amount").descending();
		}
		List<Transaction> listTransactions = transactionRepository.findByIban(iban, sorting);
		
		return listTransactions;
	}

	public Map<String, Object> create(Transaction transaction) {
		transactionRepository.save(transaction);
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("reference", transaction.getReference());
		return returnMap;
		// TODO Auto-generated method stub

	}

	public Map<String, Object> getStatus(String reference, String channel) {
		
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("reference", reference);
		Transaction transaction = transactionRepository.findByReference(reference);
		if (transaction == null) {
			returnMap.put("status", INVALID);
			return returnMap;
		}

		LocalDate localDateTransaction = transaction.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate actualLocalDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		if (localDateTransaction.isBefore(actualLocalDate)) {
			returnMap.put("status", SETTLED);
		} else if (localDateTransaction.isEqual(actualLocalDate)) {
			returnMap.put("status", PENDING);
		} else if (localDateTransaction.isAfter(actualLocalDate)) {
			returnMap.put("status", FUTURE);
		}

		if (channel.equals(CLIENT) || channel.equals(ATM)) {
			returnMap.put("amount", df.format(transaction.getAmount() - transaction.getFee()));
		} else {
			returnMap.put("amount", transaction.getAmount());
			returnMap.put("fee", transaction.getFee());
		}
		return returnMap;
	}
}

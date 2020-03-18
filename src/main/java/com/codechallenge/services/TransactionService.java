package com.codechallenge.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.codechallenge.domains.Account;
import com.codechallenge.domains.Transaction;
import com.codechallenge.exceptions.NoAccountException;
import com.codechallenge.exceptions.NoFundsException;
import com.codechallenge.models.SearchRequest;
import com.codechallenge.models.StatusRequest;
import com.codechallenge.models.StatusResponse;
import com.codechallenge.models.TransactionRequest;
import com.codechallenge.models.TransactionResponse;
import com.codechallenge.models.enums.TransactionChannel;
import com.codechallenge.models.enums.TransactionStatus;
import com.codechallenge.repositories.AccountRepository;
import com.codechallenge.repositories.TransactionRepository;

@Service
public class TransactionService {

	private static final String DEFAULT_SORT_PROPERTY = "amount";
	private static final int REFERENCE_LENGTH = 6;

	@Autowired
	TransactionRepository transactionRepository;

	@Autowired
	AccountRepository accountRepository;

	@Transactional
	public TransactionResponse createTransaction(final TransactionRequest transactionRequest) {
		final BigDecimal total = this.getTotal(transactionRequest);
		final Account account = this.accountRepository.findByIban(transactionRequest.getIban());

		this.checkAccountExists(account, transactionRequest);
		this.checkHasFunds(account, total);

		final Transaction transaction = new Transaction(transactionRequest.getIban(), transactionRequest.getAmount());
		transaction.setReference(this.createReferenceFactory(transactionRequest));
		transaction.setDate(this.createDateFactory(transactionRequest));
		transaction.setFee(this.createFeeFactory(transactionRequest));
		transaction.setDescription(transactionRequest.getDescription());

		this.transactionRepository.save(transaction);

		account.setAmount(this.addTotal(account, total));
		this.accountRepository.save(account);

		return new TransactionResponse(transaction.getReference());
	}

	private BigDecimal getTotal(final TransactionRequest transaction) {
		return transaction.getAmount().subtract(this.createFeeFactory(transaction));
	}

	private void checkAccountExists(final Account account, final TransactionRequest transactionRequest) {
		if (account == null) {
			throw new NoAccountException("Account " + transactionRequest.getIban() + " is not available");
		}
	}

	private void checkHasFunds(final Account account, final BigDecimal total) {
		if (this.addTotal(account, total).compareTo(BigDecimal.ZERO) < 0) {
			throw new NoFundsException(
					"Cannot substract " + total.abs().toString() + " from " + account.getAmount().toString());
		}
	}

	private BigDecimal addTotal(final Account account, final BigDecimal total) {
		return account.getAmount().add(total);
	}

	private String createReferenceFactory(final TransactionRequest transaction) {
		if (transaction.getReference() == null || transaction.getReference().trim().equals("")) {
			return RandomStringUtils.randomAlphanumeric(TransactionService.REFERENCE_LENGTH);
		}

		return transaction.getReference();
	}

	private ZonedDateTime createDateFactory(final TransactionRequest transaction) {
		return transaction.getDate() == null ? LocalDateTime.now().atZone(ZoneId.systemDefault())
				: transaction.getDate();
	}

	private BigDecimal createFeeFactory(final TransactionRequest transaction) {
		return transaction.getFee() == null ? BigDecimal.ZERO : transaction.getFee();
	}

	public List<Transaction> findTransactions(final SearchRequest searchRequest) {
		if (searchRequest == null) {
			return this.transactionRepository.findAll(Sort.by(Direction.ASC, TransactionService.DEFAULT_SORT_PROPERTY));
		}

		final Direction direction = searchRequest.getSort() == null ? Direction.ASC : searchRequest.getSort();
		final Sort sort = Sort.by(direction, TransactionService.DEFAULT_SORT_PROPERTY);

		return searchRequest.getIban() == null ? this.transactionRepository.findAll(sort)
				: this.transactionRepository.findByIban(searchRequest.getIban(), sort);
	}

	public StatusResponse findTransactionStatus(final StatusRequest statusRequest) {
		final String reference = statusRequest.getReference();
		final TransactionChannel channel = this.transactionChannelFactory(statusRequest);
		final Transaction transaction = this.transactionRepository.findByReference(reference);

		final StatusResponse statusResponse = new StatusResponse(reference);
		statusResponse.setStatus(this.transactionStatusFactory(transaction));
		statusResponse.setAmount(this.statusAmountFactory(transaction, channel));
		statusResponse.setFee(this.statusFeeFactory(transaction, channel));

		return statusResponse;
	}

	private TransactionChannel transactionChannelFactory(final StatusRequest statusRequest) {
		return statusRequest.getChannel() == null ? TransactionChannel.CLIENT : statusRequest.getChannel();
	}

	private TransactionStatus transactionStatusFactory(final Transaction transaction) {
		if (transaction == null) {
			return TransactionStatus.INVALID;
		}

		final LocalDate transactionLocalDate = transaction.getDate().toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate();
		final LocalDate actualLocalDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		if (transactionLocalDate.isBefore(actualLocalDate)) {
			return TransactionStatus.SETTLED;
		} else if (transactionLocalDate.isEqual(actualLocalDate)) {
			return TransactionStatus.PENDING;
		} else {
			return TransactionStatus.FUTURE;
		}
	}

	private BigDecimal statusAmountFactory(final Transaction transaction, final TransactionChannel channel) {
		if (transaction == null) {
			return null;
		}

		if (channel.equals(TransactionChannel.CLIENT) || channel.equals(TransactionChannel.ATM)) {
			return transaction.getAmount().subtract(transaction.getFee());
		} else {
			return transaction.getAmount();
		}
	}

	private BigDecimal statusFeeFactory(final Transaction transaction, final TransactionChannel channel) {
		if (transaction == null || !channel.equals(TransactionChannel.INTERNAL)) {
			return null;
		}

		return transaction.getFee();
	}
}

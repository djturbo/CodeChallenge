package com.codechallenge.unitary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.codechallenge.domains.Account;
import com.codechallenge.domains.Transaction;
import com.codechallenge.models.SearchRequest;
import com.codechallenge.models.StatusRequest;
import com.codechallenge.models.StatusResponse;
import com.codechallenge.models.TransactionRequest;
import com.codechallenge.models.TransactionResponse;
import com.codechallenge.models.enums.TransactionChannel;
import com.codechallenge.models.enums.TransactionStatus;
import com.codechallenge.repositories.AccountRepository;
import com.codechallenge.repositories.TransactionRepository;
import com.codechallenge.services.TransactionService;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
	/* DEFAULT ACCOUNT */
	private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(666);
	private static final String DEFAULT_IBAN = "ES66-0000-1111-2222-3333";
	private static final Account DEFAULT_ACCOUNT = new Account(DEFAULT_IBAN, DEFAULT_AMOUNT);
	
	/* DEFAULT TRANSACTION */
	private static final String NEW_REFERENCE = "1111A";
	private static final String NEW_DESCRIPTION = "new transaction";
	private static final BigDecimal NEW_FEE = BigDecimal.valueOf(0.1f);
	private static final BigDecimal NEW_AMOUNT = new BigDecimal(111);
	private static final ZonedDateTime NEW_DATE = ZonedDateTime.now();
	
	private static final Transaction DEFAULT_TRANSACTION = new Transaction(NEW_REFERENCE, DEFAULT_IBAN, NEW_DATE, NEW_AMOUNT, NEW_FEE, NEW_DESCRIPTION);
	private static final List<Transaction>DEFAULT_TRANSACTION_LIST = new LinkedList<Transaction>() {/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{add(DEFAULT_TRANSACTION);}};
	
	@Mock
	private TransactionRepository transactionRepository;
	@Mock
	private AccountRepository accountRepository;
	
	@InjectMocks
	private TransactionService service;
	
	@BeforeEach
	public void setup() {
		this.service = new TransactionService(transactionRepository, accountRepository);
	}
	
	/* Test the creation transaction code */
	@Test
	public void createTransactionTest() {
		/* Creates new Transaction Object for Mock the transaction repository */
		Transaction transaction = DEFAULT_TRANSACTION;
		/* Create Transaction Request */
		TransactionRequest transactionRequest = new TransactionRequest(NEW_REFERENCE, DEFAULT_IBAN, NEW_DATE, NEW_AMOUNT, NEW_FEE, NEW_DESCRIPTION);
		/* Mock result for the findByIban accountRepository Method to return the TEST DEFAULT_ACCOUNT */
		when(this.accountRepository.findByIban(transactionRequest.getIban())).thenReturn(DEFAULT_ACCOUNT);
		
		when(this.transactionRepository.save(transaction)).thenReturn(transaction);
		
		when(this.accountRepository.save(DEFAULT_ACCOUNT)).thenReturn(DEFAULT_ACCOUNT);

		TransactionResponse response = service.createTransaction(transactionRequest);
		
		assertThat(response).isNotNull();
		assertThat(response.getReference()).isEqualTo(NEW_REFERENCE);
		
	}
	
	/* Test the find all transaction code */
	@Test
	public void findAllTransactions() {
		final SearchRequest searchRequest = new SearchRequest();

		final Direction direction = searchRequest.getSort() == null ? Direction.ASC : searchRequest.getSort();
		final Sort sort = Sort.by(direction, TransactionService.DEFAULT_SORT_PROPERTY);
		
		when(this.transactionRepository.findAll(sort)).thenReturn(DEFAULT_TRANSACTION_LIST);
		List<Transaction> result = service.findTransactions(searchRequest);
		
		assertThat(result).isNotNull();
		assertThat(result).asList();
		assertThat(result.size()).isEqualTo(DEFAULT_TRANSACTION_LIST.size());
		assertThat(result.get(0).getAmount()).isEqualTo(NEW_AMOUNT);
		assertThat(result.get(0).getDate()).isEqualTo(NEW_DATE);
		assertThat(result.get(0).getDescription()).isEqualTo(NEW_DESCRIPTION);
		assertThat(result.get(0).getFee()).isEqualTo(NEW_FEE);
		assertThat(result.get(0).getIban()).isEqualTo(DEFAULT_IBAN);
		assertThat(result.get(0).getReference()).isEqualTo(NEW_REFERENCE);
	}
	/* test the find transaction by iban filter */
	@Test
	public void findTransactionsByIban() {
		
		final SearchRequest searchRequest = new SearchRequest();
		searchRequest.setIban(DEFAULT_IBAN);
		final Direction direction = searchRequest.getSort() == null ? Direction.ASC : searchRequest.getSort();
		final Sort sort = Sort.by(direction, TransactionService.DEFAULT_SORT_PROPERTY);
		
		when(this.transactionRepository.findByIban(searchRequest.getIban(), sort)).thenReturn(DEFAULT_TRANSACTION_LIST);
		List<Transaction> result = service.findTransactions(searchRequest);

		assertThat(result).isNotNull();
		assertThat(result).asList();
		assertThat(result.size()).isEqualTo(DEFAULT_TRANSACTION_LIST.size());
		assertThat(result.get(0).getAmount()).isEqualTo(NEW_AMOUNT);
		assertThat(result.get(0).getDate()).isEqualTo(NEW_DATE);
		assertThat(result.get(0).getDescription()).isEqualTo(NEW_DESCRIPTION);
		assertThat(result.get(0).getFee()).isEqualTo(NEW_FEE);
		assertThat(result.get(0).getIban()).isEqualTo(DEFAULT_IBAN);
		assertThat(result.get(0).getReference()).isEqualTo(NEW_REFERENCE);
	}
	/* Test the result when trying to search with null request */
	@Test
	public void findTransactionsWithNullRequest() {
		
		final SearchRequest searchRequest = null;
		
		when(this.transactionRepository.findAll(Sort.by(Direction.ASC, TransactionService.DEFAULT_SORT_PROPERTY))).thenReturn(DEFAULT_TRANSACTION_LIST);
		List<Transaction> result = service.findTransactions(searchRequest);

		assertThat(result).isNotNull();
		assertThat(result).asList();
		assertThat(result.size()).isEqualTo(DEFAULT_TRANSACTION_LIST.size());
		assertThat(result.get(0).getAmount()).isEqualTo(NEW_AMOUNT);
		assertThat(result.get(0).getDate()).isEqualTo(NEW_DATE);
		assertThat(result.get(0).getDescription()).isEqualTo(NEW_DESCRIPTION);
		assertThat(result.get(0).getFee()).isEqualTo(NEW_FEE);
		assertThat(result.get(0).getIban()).isEqualTo(DEFAULT_IBAN);
		assertThat(result.get(0).getReference()).isEqualTo(NEW_REFERENCE);
	}
	/* Test that returns a Pending statatus when the transaction was created not before and not after now */
	@Test
	public void findTransactionStatusPendingTest() {
		
		final StatusRequest statusRequest = new StatusRequest(NEW_REFERENCE, TransactionChannel.CLIENT);
		
		when(this.transactionRepository.findByReference(NEW_REFERENCE)).thenReturn(DEFAULT_TRANSACTION);
		
		StatusResponse result = service.findTransactionStatus(statusRequest);
		
		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(TransactionStatus.PENDING);
	}
	/* Test the return status when the transaction was created before now */
	@Test
	public void findTransactionStatusSettledTest() {
		
		final StatusRequest statusRequest = new StatusRequest(NEW_REFERENCE, TransactionChannel.CLIENT);
		Transaction transaction = cloneDefaultTransaction();
		transaction.setDate(ZonedDateTime.now().minusDays(1));
		when(this.transactionRepository.findByReference(NEW_REFERENCE)).thenReturn(transaction);
		
		StatusResponse result = service.findTransactionStatus(statusRequest);
		
		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(TransactionStatus.SETTLED);
	}
	
	/* Test the status code if the transaction will create after now */
	@Test
	public void findTransactionStatusFutureTest() {
		
		final StatusRequest statusRequest = new StatusRequest(NEW_REFERENCE, TransactionChannel.CLIENT);
		Transaction transaction = cloneDefaultTransaction();
		transaction.setDate(ZonedDateTime.now().plusDays(11));
		when(this.transactionRepository.findByReference(NEW_REFERENCE)).thenReturn(transaction);
		
		StatusResponse result = service.findTransactionStatus(statusRequest);
		
		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(TransactionStatus.FUTURE);
	}
	
	/* Test the code that return INVALID transaction status. */
	@Test
	public void findTransactionStatusInvalidTest() {
		
		final StatusRequest statusRequest = new StatusRequest(NEW_REFERENCE, TransactionChannel.CLIENT);
		when(this.transactionRepository.findByReference(NEW_REFERENCE)).thenReturn(null);
		
		StatusResponse result = service.findTransactionStatus(statusRequest);
		
		assertThat(result).isNotNull();
		assertThat(result.getStatus()).isEqualTo(TransactionStatus.INVALID);
	}
	/* Util method */
	private Transaction cloneDefaultTransaction() {
		Transaction trans = new Transaction();
		trans.setAmount(NEW_AMOUNT);
		trans.setDate(ZonedDateTime.now());
		trans.setDescription(NEW_DESCRIPTION);
		trans.setFee(NEW_FEE);
		trans.setIban(DEFAULT_IBAN);
		trans.setReference(NEW_REFERENCE);
		return trans;
	}

}

package com.codechallenge.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import com.codechallenge.TestUtil;
import com.codechallenge.TransactionsApplication;
import com.codechallenge.controller.TransactionsController;
import com.codechallenge.domain.Transaction;
import com.codechallenge.model.StatusRequest;
import com.codechallenge.model.StatusResponse;
import com.codechallenge.model.TransactionRequest;
import com.codechallenge.models.enums.TransactionChannel;
import com.codechallenge.models.enums.TransactionStatus;
import com.codechallenge.repository.TransactionRepository;
import com.codechallenge.service.TransactionService;

@SpringBootTest(classes = { TransactionsApplication.class })
public class TransactionsControllerITest {
	private static final String DEFAULT_DESCRIPTION = "new transaction";

	private static final BigDecimal DEFAULT_FEE = BigDecimal.valueOf(10f);

	private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(666);

	private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.now();

	private static final String NEW_IBAN = "ES660000111122223333";

	private static final String DEFAULT_IBAN = "ES4131891613999502318057";
	
	private static final String DEFAULT_REFERENCE = "1111A";

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private TransactionRepository transactionRepository;
	
	@Autowired
	private Validator validator;
	@Autowired
	private MappingJackson2HttpMessageConverter jacksonMessageConverter;

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		final TransactionsController transactionsController = new TransactionsController(transactionService);
		this.mockMvc = MockMvcBuilders.standaloneSetup(transactionsController)
				.setMessageConverters(jacksonMessageConverter).setValidator(validator).build();
	}
	/* When a transaction is created with a non existing IBAN, throws a NoAccountException */
	@Test
	public void createTransactionNotAccountExceptionTest() {
		/* Given argument for create new transaction */
		TransactionRequest transactionRequest = new TransactionRequest(DEFAULT_REFERENCE, NEW_IBAN, DEFAULT_DATE,
				DEFAULT_AMOUNT, DEFAULT_FEE, DEFAULT_DESCRIPTION);

		try {
			mockMvc.perform(MockMvcRequestBuilders.post("/").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionRequest)))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()));
		}catch(Exception ex) {
			assertThat(ex.getMessage()).contains("NoAccountException");
		}
	}

	/* Ensures that the creation the transaction is correct */
	@Test
	public void createTransactionTest() throws Exception {
		Long beforeTransactions = this.transactionRepository.count();
		/* Given argument for create new transaction */
		TransactionRequest transactionRequest = new TransactionRequest(DEFAULT_REFERENCE, DEFAULT_IBAN, DEFAULT_DATE,
				DEFAULT_AMOUNT, DEFAULT_FEE, DEFAULT_DESCRIPTION);

		mockMvc.perform(MockMvcRequestBuilders.post("/").contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionRequest)))
				.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
				.andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE));
		Long afterTransactions = this.transactionRepository.count();
		/* Ensure that the new transaction was created */
		assertThat(beforeTransactions).isEqualTo(afterTransactions -1);
	}
	
	/*  Given: A transaction that is not stored in our system
	    When: I check the status from any channel
	    Then: The system returns the status 'INVALID' */
	@Test
	public void expectInvalidTransactionStatusTest() throws IOException, Exception {
		String transactionToCheckReference = "XXXXXX";
		StatusRequest statusRequest = new StatusRequest(transactionToCheckReference, TransactionChannel.CLIENT);
		
		StatusResponse expectedResponse = new StatusResponse(transactionToCheckReference);
		expectedResponse.setStatus(TransactionStatus.INVALID);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/status").contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusRequest)))
			.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.reference").value(expectedResponse.getReference()))
			.andExpect(jsonPath("$.status").value(expectedResponse.getStatus().toString()));
	}
	
	/*  
		Given: A transaction that is stored in our system
		When: I check the status from CLIENT or ATM channel
		And the transaction date is before today
		Then: The system returns the status 'SETTLED'
		And the amount substracting the fee */
	@Test
	public void expectSettledTransactionStatusTest() throws IOException, Exception {
		
		String transactionToCheckReference = "456781";
		StatusRequest statusRequest = new StatusRequest(transactionToCheckReference, TransactionChannel.CLIENT);
		statusRequest.setChannel(TransactionChannel.CLIENT);
		/* get transaction from database for compare the result */
		Transaction forTransaction = this.transactionRepository.findById(transactionToCheckReference).get();
		
		StatusResponse expectedResponse = new StatusResponse(transactionToCheckReference);
		expectedResponse.setStatus(TransactionStatus.SETTLED);
		
		
		mockMvc.perform(MockMvcRequestBuilders.post("/status").contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusRequest)))
			.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.reference").value(expectedResponse.getReference()))
			.andExpect(jsonPath("$.status").value(expectedResponse.getStatus().toString()))
			.andExpect(jsonPath("$.amount").value(forTransaction.getAmount().subtract(forTransaction.getFee()).toString()));
	}
	
	/*  
		Given: A transaction that is stored in our system
		When: I check the status from INTERNAL channel And the transaction date is before today
		Then: The system returns the status 'SETTLED'
		And the amount will be the same without subtract fee
		And the fee will be same that original transaction */
	@Test
	public void expectSettledTransactionStatusForInternalChannelTest() throws IOException, Exception {
		
		String transactionToCheckReference = "456781";
		StatusRequest statusRequest = new StatusRequest(transactionToCheckReference, TransactionChannel.CLIENT);
		statusRequest.setChannel(TransactionChannel.INTERNAL);
		/* get transaction from database for compare the result */
		Transaction forTransaction = this.transactionRepository.findById(transactionToCheckReference).get();
		
		StatusResponse expectedResponse = new StatusResponse(transactionToCheckReference);
		expectedResponse.setStatus(TransactionStatus.SETTLED);
		
		
		mockMvc.perform(MockMvcRequestBuilders.post("/status").contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusRequest)))
			.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.reference").value(expectedResponse.getReference()))
			.andExpect(jsonPath("$.status").value(expectedResponse.getStatus().toString()))
			.andExpect(jsonPath("$.amount").value(forTransaction.getAmount().toString()))
			.andExpect(jsonPath("$.fee").value(forTransaction.getFee().toString()));
	}
	/*  
		Given: A transaction that is stored in our system
		When: I check the status from CLIENT or ATM channel
		And the transaction date is equals to today
		Then: The system returns the status 'PENDING'
		And the amount substracting the fee */
	@Test
	public void expectPendingTransactionStatusChannelTest() throws IOException, Exception {
		/* reference of the registered transaction that it date is set to now */
		String transactionToCheckReference = "456782"; 
		StatusRequest statusRequest = new StatusRequest(transactionToCheckReference, TransactionChannel.CLIENT);
		/* set channel ATM or CLIENT for test the correct behavior */
		statusRequest.setChannel(TransactionChannel.ATM);
		/* get transaction from database for compare the result */
		Transaction forTransaction = this.transactionRepository.findById(transactionToCheckReference).get();
		
		StatusResponse expectedResponse = new StatusResponse(transactionToCheckReference);
		expectedResponse.setStatus(TransactionStatus.PENDING);
		
		
		mockMvc.perform(MockMvcRequestBuilders.post("/status").contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusRequest)))
			.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.reference").value(expectedResponse.getReference()))
			.andExpect(jsonPath("$.status").value(expectedResponse.getStatus().toString()))
			.andExpect(jsonPath("$.amount").value(forTransaction.getAmount().subtract(forTransaction.getFee()).toString()));
	}
	
	/*  
		Given: A transaction that is stored in our system
		When: I check the status from INTERNAL channel
		And the transaction date is equals to today
		Then: The system returns the status 'PENDING'
		And the amount
		And the fee */
	@Test
	public void expectPendingTransactionStatusChannelWhenInternalChannelTest() throws IOException, Exception {
		/* reference of the registered transaction that it date is set to now */
		String transactionToCheckReference = "456782"; 
		StatusRequest statusRequest = new StatusRequest(transactionToCheckReference, TransactionChannel.CLIENT);
		/* set channel internal for test the correct behavior */
		statusRequest.setChannel(TransactionChannel.INTERNAL);
		/* get transaction from database for compare the result */
		Transaction forTransaction = this.transactionRepository.findById(transactionToCheckReference).get();
		
		StatusResponse expectedResponse = new StatusResponse(transactionToCheckReference);
		expectedResponse.setStatus(TransactionStatus.PENDING);
		
		
		mockMvc.perform(MockMvcRequestBuilders.post("/status").contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusRequest)))
			.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.reference").value(expectedResponse.getReference()))
			.andExpect(jsonPath("$.status").value(expectedResponse.getStatus().toString()))
			.andExpect(jsonPath("$.amount").value(forTransaction.getAmount().toString()))
			.andExpect(jsonPath("$.fee").value(forTransaction.getFee().toString()));
	}
	/*  
		Given: A transaction that is stored in our system
		When: I check the status from CLIENT channel
		And the transaction date is greater than today
		Then: The system returns the status 'FUTURE'
		And the amount substracting the fe */
	@Test
	public void expectFutureTransactionStatusTest() throws IOException, Exception {
		/* Transaction with date greater than today at this moment is 2020-23-03 */
		String transactionToCheckReference = "456783";
		StatusRequest statusRequest = new StatusRequest(transactionToCheckReference, TransactionChannel.CLIENT);
		statusRequest.setChannel(TransactionChannel.CLIENT);
		/* get transaction from database for compare the result */
		Transaction forTransaction = this.transactionRepository.findById(transactionToCheckReference).get();
		
		StatusResponse expectedResponse = new StatusResponse(transactionToCheckReference);
		expectedResponse.setStatus(TransactionStatus.FUTURE);
		
		
		mockMvc.perform(MockMvcRequestBuilders.post("/status").contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusRequest)))
			.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.reference").value(expectedResponse.getReference()))
			.andExpect(jsonPath("$.status").value(expectedResponse.getStatus().toString()))
			.andExpect(jsonPath("$.amount").value(forTransaction.getAmount().subtract(forTransaction.getFee()).toString()));
	}
	/*  
		Given: A transaction that is stored in our system
		When: I check the status from ATM channel
		And the transaction date is greater than today
		Then: The system returns the status 'PENDING'
		And the amount substracting the fee */
	@Test
	public void expectPendingTransactionStatusWhenDateGreaterAndChannelATMTest() throws IOException, Exception {
		/* Transaction with date greater than today at this moment is 2020-23-03 */
		String transactionToCheckReference = "456783";
		StatusRequest statusRequest = new StatusRequest(transactionToCheckReference, TransactionChannel.CLIENT);
		statusRequest.setChannel(TransactionChannel.ATM);
		/* get transaction from database for compare the result */
		Transaction forTransaction = this.transactionRepository.findById(transactionToCheckReference).get();
		
		StatusResponse expectedResponse = new StatusResponse(transactionToCheckReference);
		expectedResponse.setStatus(TransactionStatus.PENDING);
		
		
		mockMvc.perform(MockMvcRequestBuilders.post("/status").contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusRequest)))
			.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.reference").value(expectedResponse.getReference()))
			.andExpect(jsonPath("$.status").value(expectedResponse.getStatus().toString()))
			.andExpect(jsonPath("$.amount").value(forTransaction.getAmount().subtract(forTransaction.getFee()).toString()));
	}
	/* 	  
		Given: A transaction that is stored in our system
		When: I check the status from INTERNAL channel
		And the transaction date is greater than today
		Then: The system returns the status 'FUTURE'
		And the amount
		And the fee */
	@Test
	public void expectFutureTransactionStatusWhenDateGreaterAndChannelInternalTest() throws IOException, Exception {
		/* Transaction with date greater than today at this moment is 2020-23-03 */
		String transactionToCheckReference = "456783";
		StatusRequest statusRequest = new StatusRequest(transactionToCheckReference, TransactionChannel.CLIENT);
		statusRequest.setChannel(TransactionChannel.INTERNAL);
		/* get transaction from database for compare the result */
		Transaction forTransaction = this.transactionRepository.findById(transactionToCheckReference).get();
		
		StatusResponse expectedResponse = new StatusResponse(transactionToCheckReference);
		expectedResponse.setStatus(TransactionStatus.FUTURE);
		
		
		mockMvc.perform(MockMvcRequestBuilders.post("/status").contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(statusRequest)))
			.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
			.andExpect(jsonPath("$.reference").value(expectedResponse.getReference()))
			.andExpect(jsonPath("$.status").value(expectedResponse.getStatus().toString()))
			.andExpect(jsonPath("$.amount").value(forTransaction.getAmount().toString()))
			.andExpect(jsonPath("$.fee").value(forTransaction.getFee().toString()));
	}
}

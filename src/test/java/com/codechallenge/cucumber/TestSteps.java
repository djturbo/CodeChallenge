package com.codechallenge.cucumber;

import cucumber.api.PendingException;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import com.codechallenge.TransactionsApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.HashMap;

@SpringBootTest(classes = TransactionsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
public class TestSteps {

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private TestRestTemplate restTemplate2;

	private ResponseEntity<HashMap> response;
	private ResponseEntity<String> responseEntity;
	private String reference = null;
	private String date = null;
	private String amount = null;
	private String fee = null;
	private String description = null;
	private String account_iban = null;
	private String sort = null;
	
	String transactionResultAsJsonStr = null;
	
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAccount_iban() {
		return account_iban;
	}

	public void setAccount_iban(String account_iban) {
		this.account_iban = account_iban;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getFee() {
		return fee;
	}

	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private ObjectMapper objectMapper = new ObjectMapper();
	JsonNode root;
	
	@Given("^A transaction with reference (.*)")
	public void aTransNotStored(String reference) {
		
		this.reference = reference;
	}

	@When("^I check the status from (.*)")
	public void whenICheckStatus(String channel) throws JSONException, JsonMappingException, JsonProcessingException {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject transactionJsonObject = new JSONObject();
		transactionJsonObject.put("reference", reference);
		transactionJsonObject.put("channel", channel);

		HttpEntity<String> request = new HttpEntity<String>(transactionJsonObject.toString(), headers);

		transactionResultAsJsonStr = restTemplate.postForObject("/status", request, String.class);
		root = objectMapper.readTree(transactionResultAsJsonStr);

	}

	@Then("^The system returns the status (.*)")
	public void thSystemReturnsStatus(String status) throws Throwable {

		Assert.assertEquals(status, root.path("status").asText());
	}
	
	@And("^The system returns amount (.*)")
	public void thSystemReturnsAmount(String amount) throws Throwable {

		Assert.assertEquals(amount, root.path("amount").asText());
	}
	
	@And("^The system returns fee (.*)")
	public void thSystemReturnsFee(String fee) throws Throwable {
		Assert.assertEquals(fee, root.path("fee").asText());
	}
	
	// Create
	
	@Given("^reference (.*), account iban (.*), date (.*), amount (.*), fee (.*) and description (.*)$")
	public void reference_account_iban_ES_date_amount_fee_and_description(String reference, String account_iban, String date, String amount, String fee, String description) throws Throwable {
	    
		setReference(reference);
	    setAccount_iban(account_iban);
	    setDate(date);
	    setAmount(amount);
	    setFee(fee);
	    setDescription(description);
	   
	}

	@When("^I call create$")
	public void i_call_create() throws Throwable {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject transactionJsonObject = new JSONObject();
		
		transactionJsonObject.put("reference", getReference());
		transactionJsonObject.put("account_iban", getAccount_iban());
		transactionJsonObject.put("date", getDate());
		transactionJsonObject.put("amount", getAmount());
		transactionJsonObject.put("fee", getFee());
		transactionJsonObject.put("description", getDescription());

		HttpEntity<String> request = new HttpEntity<String>(transactionJsonObject.toString(), headers);

		transactionResultAsJsonStr = restTemplate2.postForObject("/create", request, String.class);
		root = objectMapper.readTree(transactionResultAsJsonStr);
	}

	@Then("^The system returns the reference of the newly created transaction$")
	public void the_system_returns_the_reference_of_the_newly_created_transaction() throws Throwable {
		setReference(root.path("reference").asText());
		Assert.assertTrue(reference != null && !reference.equals(""));
	    
	}
	
	// Search
	
	@Given("^An IBAN (.*) and sort (.*)$")
	public void an_IBAN_ES_and_sort_asc(String iban, String sort) throws Throwable {
	    
		setAccount_iban(iban);
		setSort(sort);
	    
	}

	@When("^I search$")
	public void i_search() throws Throwable {
	    
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		JSONObject transactionJsonObject = new JSONObject();
		
		transactionJsonObject.put("iban", getAccount_iban());
		transactionJsonObject.put("sort", getSort());
		
		HttpEntity<String> request = new HttpEntity<String>(transactionJsonObject.toString(), headers);

		transactionResultAsJsonStr = restTemplate2.postForObject("/search", request, String.class);
		//root = objectMapper.readTree(transactionResultAsJsonStr);
	}

	@Then("^I receive a list of transactions for that IBAN$")
	public void i_receive_a_list_of_transactions_for_that_IBAN() throws Throwable {

		JSONArray transactionsList = new JSONArray(transactionResultAsJsonStr);
		
		Assert.assertTrue(transactionsList.length() > 0);
	}
}

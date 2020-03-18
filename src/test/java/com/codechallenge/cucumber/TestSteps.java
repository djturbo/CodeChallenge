package com.codechallenge.cucumber;

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
import org.springframework.test.context.ContextConfiguration;

import com.codechallenge.TransactionsApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ContextConfiguration
@FieldDefaults(level = AccessLevel.PRIVATE)
@SpringBootTest(classes = TransactionsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestSteps {

	@Autowired
	TestRestTemplate restTemplate;

	@Autowired
	TestRestTemplate restTemplate2;

	String reference = null;
	String date = null;
	String amount = null;
	String fee = null;
	String description = null;
	String iban = null;
	String sort = null;

	String transactionResultAsJsonStr = null;

	private final ObjectMapper objectMapper = new ObjectMapper();
	JsonNode root;

	@Given("^A transaction with reference (.*)")
	public void aTransNotStored(final String reference) {
		this.reference = reference;
	}

	@When("^I check the status from (.*)")
	public void whenICheckStatus(final String channel)
			throws JSONException, JsonMappingException, JsonProcessingException {

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		final JSONObject transactionJsonObject = new JSONObject();
		transactionJsonObject.put("reference", this.reference);
		transactionJsonObject.put("channel", channel);

		final HttpEntity<String> request = new HttpEntity<>(transactionJsonObject.toString(), headers);

		this.transactionResultAsJsonStr = this.restTemplate.postForObject("/status", request, String.class);
		this.root = this.objectMapper.readTree(this.transactionResultAsJsonStr);

	}

	@Then("^The system returns the status (.*)")
	public void thSystemReturnsStatus(final String status) throws Throwable {

		Assert.assertEquals(status, this.root.path("status").asText());
	}

	@And("^The system returns amount (.*)")
	public void thSystemReturnsAmount(final String amount) throws Throwable {

		Assert.assertEquals(amount, this.root.path("amount").asText());
	}

	@And("^The system returns fee (.*)")
	public void thSystemReturnsFee(final String fee) throws Throwable {
		Assert.assertEquals(fee, this.root.path("fee").asText());
	}

	// Create

	@Given("^reference (.*), account iban (.*), date (.*), amount (.*), fee (.*) and description (.*)$")
	public void reference_iban_ES_date_amount_fee_and_description(final String reference, final String iban,
			final String date, final String amount, final String fee, final String description) throws Throwable {

		this.setReference(reference);
		this.setIban(iban);
		this.setDate(date);
		this.setAmount(amount);
		this.setFee(fee);
		this.setDescription(description);

	}

	@When("^I call create$")
	public void i_call_create() throws Throwable {
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		final JSONObject transactionJsonObject = new JSONObject();

		transactionJsonObject.put("reference", this.getReference());
		transactionJsonObject.put("iban", this.getIban());
		transactionJsonObject.put("date", this.getDate());
		transactionJsonObject.put("amount", this.getAmount());
		transactionJsonObject.put("fee", this.getFee());
		transactionJsonObject.put("description", this.getDescription());

		final HttpEntity<String> request = new HttpEntity<>(transactionJsonObject.toString(), headers);

		this.transactionResultAsJsonStr = this.restTemplate2.postForObject("/", request, String.class);
		this.root = this.objectMapper.readTree(this.transactionResultAsJsonStr);
	}

	@Then("^The system returns the reference of the newly created transaction$")
	public void the_system_returns_the_reference_of_the_newly_created_transaction() throws Throwable {
		this.setReference(this.root.path("reference").asText());
		Assert.assertTrue(this.reference != null && !this.reference.equals(""));
	}

	// Search

	@Given("^An IBAN (.*) and sort (.*)$")
	public void an_IBAN_ES_and_sort_asc(final String iban, final String sort) throws Throwable {

		this.setIban(iban);
		this.setSort(sort);

	}

	@When("^I search$")
	public void i_search() throws Throwable {

		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		final JSONObject transactionJsonObject = new JSONObject();

		transactionJsonObject.put("iban", this.getIban());
		transactionJsonObject.put("sort", this.getSort());

		final HttpEntity<String> request = new HttpEntity<>(transactionJsonObject.toString(), headers);

		this.transactionResultAsJsonStr = this.restTemplate2.postForObject("/search", request, String.class);
		// root = objectMapper.readTree(transactionResultAsJsonStr);
	}

	@Then("^I receive a list of transactions for that IBAN$")
	public void i_receive_a_list_of_transactions_for_that_IBAN() throws Throwable {

		final JSONArray transactionsList = new JSONArray(this.transactionResultAsJsonStr);

		Assert.assertTrue(transactionsList.length() > 0);
	}
}

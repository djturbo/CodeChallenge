# codechallenge

## General rules assumptions

- The microservice is self-contained. H2 is used as in-memory database, but it is assumed that the actual database could be a NoSQL, so table relationships are avoided.
- The Account entity should be managed by another microservice, but setted here for testing and functionality purposes.
- JUnit Jupiter and Mockito, has been used for unitary tests and SpringBootTest for integration Tests.
- All endpoints use the HTTP POST method, denormalizing standard REST methods to ensure correct body encryption and secure transactions over HTTPS
- To help with CD and CI databases are versioned using Liquibase
- Swagger is used to document the API
- The response of the endpoints will be a standar REST JSON object, or array of JSON objects in the case of the search.
- `BigDecimal` is used to store and manage amount and fee to avoid `double` operations round errors.
- Some custom REST errors handlers have been created, but more could be needed by functional requirements.
- CORS config is added for security reasons, but may be assumed by microservices proxy system.

## Create transaction assumptions

- `reference` is created randomly if it's not present in the request, assuming there could be another service to get valid company references.
- `account_iban` is checked to exists on Account entity before persist, else throw an error.
- `date` is generated as the current zoned datetime if it's not present in the request.
- `fee` is setted to 0 by default if it's not present in the request to avoid calculations errors.
- Before creating the transaction, the system verify that the account has sufficient funds, else throw an error.

## Search transaction assumptions

- If there is no params in the request, or even no payload, will display all transactions ordered by amount ascending.
- `account_iban` param is optional and filter the transactions by IBAN.
- If `account_iban` do not exists return and empty array.
- `sort` param is optional and defaults to transaction `amount` ascending. Always sorts by `amount`.
- The search response is not paged, but could be easily implemented.

## Transaction status assumptions

- Follow all business rules set out in the provided Gherkin steps. 
- `channel` defaults to `CLIENT` if not setted.
- If a non supported channel is provided throws and error.

## Endpoins

- API documentation: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console (default options)
 
## TODO

- Unit testing
- More cucumber tests for assumptions
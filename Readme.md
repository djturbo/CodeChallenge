# codechallenge
Code challenge

Assumptions

- I have used H2 as in-memory database.
- Cucumber and its ordinary language parser Gherkin have been used for tests.
- Given that the transaction's reference can come included in the call, I first check that it is not empty.
  If it is, I give it a random alphanumeric string.
- For testing that a status of INVALID comes whenever you send a non existent reference,
  I send two invented references for the three kinds of available channels.
- When you send channel ATM or CLIENT, the instructions say that the fee should be substracted from
  the amount. I took for granted that if the amount is negative, the fee will be likewise substrated from 
  that amount. That is to say, the result will be a bigger negative amount. For example:
  amount: -40
  fee: 2
  result: -42
- The search function returns a json array, which is very convenient to be understood via JSONArray class.
- The Account managemet is done by another microservice.

## Endpoins

- API documentation: http://localhost:8080/swagger-ui.html
 
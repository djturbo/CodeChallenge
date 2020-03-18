Feature: The system responds with the right status, amount and fee,
    according to the give reference and channel when status is called

  Scenario Outline: System reply with status <status>, 
 		amount <amount> and fee <fee>, when called with reference <reference> and channel <channel>
    Given A transaction with reference <reference>
	When I check the status from <channel>
	Then The system returns the status <status>
	And The system returns amount <amount>
	And The system returns fee <fee>
	
	Examples:
	
	| reference | channel | status 	| amount | fee  |
	| xxxxxx 	| ATM     | INVALID |        |      |
	| xxxxxx 	| CLIENT  | INVALID |        |      |
	| xxxxxx 	| INTERNAL| INVALID |        |      |
	| 000000 	| ATM     | INVALID |		 |      |
	| 000000 	| CLIENT  | INVALID |		 |      |
	| 000000	| INTERNAL| INVALID |		 |      |
	| 456781 | ATM     | SETTLED | 326.6 | 	    |
	| 456782	| ATM  	  | PENDING | 22.6	 |      |
	| 456783 | ATM	  | FUTURE 	| 22.6  |      |
	| 456781 | CLIENT  | SETTLED | 326.6 | 	    |
	| 456782	| CLIENT  | PENDING | 22.6	 |      |
	| 456783 | CLIENT  | FUTURE 	| 22.6  |      |	
	| 456781 | INTERNAL| SETTLED | 330.2 | 3.6 |
	| 456782	| INTERNAL| PENDING | 25.4	 | 2.8 |
	| 456783 | INTERNAL| FUTURE 	| 25.4  | 2.8 |	
	

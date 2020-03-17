Feature: The system creates a transaction, 
	returning the actual reference for that transaction

  Scenario Outline: The system is given all data needed
 	to create a new transaction, returning the reference
 	for that transaction
 
 	Given reference <reference>, account iban <iban>, date <date>, amount <amount>, fee <fee> and description <description>
	When I call create
	Then The system returns the reference of the newly created transaction
	
	Examples:
	
	| reference | iban 			   | date 				 | amount  | fee | description |
	| 123456111 | ES9121000418450200051332 | 2021-01-16T20:47:41.000Z |	100	   | 2.3 | Gas		   |
	| 123456112 | ES3120713285602792521178 | 2018-02-16T13:47:41.000Z |	25	   | 2.3 | Books	   |
	| 123456113 | ES8520865224136640176203 | 2020-03-10T09:47:41.000Z |	12	   | 1.2 | Internet	   |
	
	

# Bank Account Kata

This kata is about the implementation of a simple bank account application that allows the user to deposit or withdraw money, as well as view the history of successful transactions.

## User Stories



```bash
'US1'

In order to save money
As a bank client
I want to make a deposit in my account

'US 2':

In order to retrieve some or all of my savings
As a bank client
I want to make a withdrawal from my account

'US 3':

In order to check my operations
As a bank client
I want to see the history (operation, date, amount, balance) of my operations
```

## Project Structure


```bash
.
    ├── src
       ├── main
          ├── java
             ├── com.bank
                ├── config
                ├── controller
                ├── dto
                ├── exception
                ├── model
                   ├── enumeration
                ├── service
                   ├── impl
                ├── utils
          ├── resources
             ├── messages
       ├── test
          ├── java
             ├── com.bank.controller
             ├── com.bank.service
                ├── stepdefinitions
          ├── resources
    
    ├── pom.xml
    └── README.md
```
- The directory ***src/test/resources*** contains our BDD features (deposit feature, withdrawal feature and show history feature) each with the different possible scenarios.
- The directory ***src/test/java/com/bank/service/stepdefinitions*** contains step definition files. 
- The directory ***src/test/java/com/bank/controller*** contains unit test and integration test for our controller layer.
- The directory ***src/test/java/com/bank/service*** contains unit test for our service layer.

#### Note : 
Since Martin Fowler considers not having any method within objects except getters and setters as an anti-model and calls it anemic domain model, we have tried in this kata to find the balance between anemic object and rich object by moving the logic in the domain model and leaving the checks and calls to these methods in the service.

## Setup

You can also run Bank Account locally:

1. Clone or download the project.
2. Download **these plugins** in you IDE :
   - Cucumber
   - Lombok
3. Run the build command :
   ```
   mvn clean install
   ```
4. Run the application


## Documentation
To see the documentation for the exposed APIs, go to http://localhost:8080/swagger-ui/#/

![Alt Text](https://github.com/NajlaAb/Bank-Account-Kata/blob/main/demo/demo.PNG)

**/account/deposit** : allows you to deposit money. 
- ***accountId*** is required.
- The ***amount*** should be positive, otherwise an exception will be thrown.
- If the given ***accountId*** is not found, a new Account will be created with this ID.

**/account/withdraw** : allows you to withdraw money. 
- ***accountId*** is required.
- The ***amount*** should be positive and you must have sufficient balance, otherwise an exception will be thrown.
- If the given ***accountId*** is not found, an exception will be thrown.

**/account/statement** : allows you to get account statement by Year, Month or by a Beginning Date and an Ending Date.
- ***accountId*** is required.
- If the given ***accountId*** is not found, an empty list will be returned.
- If ***periodType*** is not provided, all statement will be returned regardless of the date.
- If ***periodType*** is provided and is equal to "YEAR", then ***year*** will be required. All other fields (month, startDate and endDate) will have no impact.
- If ***periodType*** is provided and is equal to "MONTH", then ***month*** will be required. All other fields (year, startDate and endDate) will have no impact.
- If ***periodType*** is provided and is equal to "CUSTOM", then ***startDate*** and ***endDate*** will be required. All other fields (month, and year) will have no impact.
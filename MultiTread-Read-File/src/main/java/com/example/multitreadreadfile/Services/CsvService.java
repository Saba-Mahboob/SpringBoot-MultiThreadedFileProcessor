package com.example.multitreadreadfile.Services;
import com.example.multitreadreadfile.DTO.AccountDTO;
import com.example.multitreadreadfile.DTO.CustomerDTO;
import com.example.multitreadreadfile.Entities.Account;
import com.example.multitreadreadfile.Entities.Customer;
import com.example.multitreadreadfile.Error.ErrorResponse;
import com.example.multitreadreadfile.Repositories.AccountRepository;
import com.example.multitreadreadfile.Repositories.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class CsvService {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    AccountRepository accountRepository;

    private final Validator validator;
    public CsvService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }


    private static final Logger logger = LoggerFactory.getLogger(CsvService.class);

    @Async
    public CompletableFuture<List<CustomerDTO>> processCustomers(InputStream inputStream,List<ErrorResponse> errorResponses) {
        long startTime = System.nanoTime();
        logger.info("Thread: {} - Starting to process customers", Thread.currentThread().getName());

        List<CustomerDTO> customers = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                CustomerDTO dto = new CustomerDTO();
                try {
                    dto.setRecordNumber(fields[0]);
                    dto.setId(fields[1]);
                    dto.setName(fields[2]);
                    dto.setLastName(fields[3]);
                    dto.setAddress(fields[4]);
                    dto.setZipCode(fields[5]);
                    dto.setNationalId(fields[6]);
                    dto.setBirthDate(fields[7]);
                }
                catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("exception in customer : "+e.getMessage());

                }

                // Validate DTO
                Set<ConstraintViolation<CustomerDTO>> violations = validator.validate(dto);
                if (violations.isEmpty()) {
                    customers.add(dto);
                    Customer customer = new Customer(
                            dto.getRecordNumber(),
                            dto.getId(),
                            dto.getName(),
                            dto.getLastName(),
                            dto.getAddress(),
                            dto.getZipCode(),
                            dto.getNationalId(),
                            dto.getBirthDate());
                    customerRepository.save(customer);
                } else {
                    System.out.println("not valid customer");
                    for (ConstraintViolation<CustomerDTO> violation : violations) {
                        ErrorResponse errorResponse=new ErrorResponse();
                        // Fill out error object

                        errorResponse.setFileName("Customer");
                        errorResponse.setRecordNumber(dto.getRecordNumber());
                        errorResponse.setCode(ErrorResponse.counter);
                        errorResponse.setErrorCategory(violation.getPropertyPath().toString());
                        errorResponse.setDescription(violation.getMessage());
                        errorResponse.setRecordNumber(dto.getRecordNumber());
                        errorResponse.setErrorDate(new Date());
                        errorResponses.add(errorResponse);
                    }
                };
            }
        } catch (Exception e) {
            System.err.println("Error reading costumerFile: " + e.getMessage());
            e.printStackTrace();
        }
        long duration = System.nanoTime() - startTime;
        logger.info("Thread: {} - Finished processing customers in {} ms", Thread.currentThread().getName(), duration / 1_000_000);
        return CompletableFuture.completedFuture(customers);
    }

    @Async
    public CompletableFuture<List<AccountDTO>> processAccounts(InputStream inputStream, Set<String> validCustomerIds,List<ErrorResponse> errorResponses) {
        long startTime = System.nanoTime();
        logger.info("Thread: {} - Starting to process Accounts", Thread.currentThread().getName());
        List<AccountDTO> accounts = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");
                AccountDTO dto = new AccountDTO();
                try {
                    dto.setRecordNumber(fields[0]);
                    dto.setAccountNumber(fields[1]);
                    dto.setType(fields[2]);
                    dto.setCustomerId(fields[3]);
                    dto.setLimit(fields[4].replace("$", ""));
                    dto.setOpenDate(fields[5]);
                    dto.setBalance(fields[6].replace("$", ""));
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("exception in accounts : " + e.getMessage());

                }

                // Validate DTO
                Set<ConstraintViolation<AccountDTO>> violations = validator.validate(dto);
                if (violations.isEmpty() && validCustomerIds.contains(dto.getCustomerId())) {
                    accounts.add(dto);
                    Account account = new Account();

                    account.setRecordNumber(dto.getRecordNumber());
                    account.setAccountNumber(dto.getAccountNumber());
                    account.setLimitation(dto.getLimit());
                    account.setOpenDate(dto.getOpenDate());
                    account.setBalance(dto.getBalance());
                    account.setType(dto.getType());

                    // Fetch the existing customer from the repository
                    Customer customer = customerRepository.findById(dto.getCustomerId()).orElse(null);
                    if (customer != null) {
                        account.setCustomer(customer);
                        accountRepository.save(account);
                    }
                } else {
                     System.out.println("not valid account");
                    for (ConstraintViolation<AccountDTO> violation : violations) {
                        //System.out.println("we are in the suspicious loop!!!!!!!!!!!");
                        ErrorResponse errorResponse = new ErrorResponse();
                        errorResponse.setFileName("Account");
                        errorResponse.setRecordNumber(dto.getRecordNumber());
                        errorResponse.setCode(ErrorResponse.counter);
                        errorResponse.setErrorCategory(violation.getPropertyPath().toString());
                        errorResponse.setDescription(violation.getMessage());
                        errorResponse.setErrorDate(new Date());
                        errorResponses.add(errorResponse);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading accountsFile: " + e.getMessage());
            e.printStackTrace();
        }
        long duration = System.nanoTime() - startTime;
        logger.info("Thread: {} - Finished processing Accounts in {} ms", Thread.currentThread().getName(), duration / 1_000_000);
        return CompletableFuture.completedFuture(accounts);
    }










}

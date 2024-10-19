package com.example.multitreadreadfile.Conrollers;

import com.example.multitreadreadfile.DTO.CustomerDTO;
import com.example.multitreadreadfile.Error.ErrorResponse;
import com.example.multitreadreadfile.Services.CsvService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class Controller {
    @Autowired
    CsvService csvService;

    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<String>> uploadFiles(@RequestParam("customers") MultipartFile customersFile,
                                                                 @RequestParam("accounts") MultipartFile accountsFile) throws IOException {
        List<ErrorResponse> errorResponses = new ArrayList<>();

        // Process customers
        return csvService.processCustomers(customersFile.getInputStream(), errorResponses)
                .thenCompose(customers -> {
                    Set<String> validCustomerIds = customers.stream()
                            .map(CustomerDTO::getId)
                            .collect(Collectors.toSet());

                    // Process accounts using the valid customer IDs
                    try {
                        return csvService.processAccounts(accountsFile.getInputStream(), validCustomerIds, errorResponses)
                                .thenApply(accounts -> {
                                    // Write errors to JSON if any
                                    if (!errorResponses.isEmpty()) {
                                        writeErrorsToJsonFileAsync(errorResponses, "Errors.json");
                                    }

                                    return ResponseEntity.ok("Files processed successfully!");
                                })
                                .exceptionally(e -> {
                                    return (ResponseEntity<String>) handleError(e, "Error processing accounts");
                                });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .exceptionally(e -> {
                    return (ResponseEntity<String>) handleError(e, "Error processing customers");
                });
    }
    private static final Logger logger = LoggerFactory.getLogger(Controller.class);
    @Async
    public CompletableFuture<Void> writeErrorsToJsonFileAsync(List<ErrorResponse> errorResponses, String fileName) {
        long startTime = System.nanoTime();
        logger.info("Thread: {} - Starting to write to a JSON file", Thread.currentThread().getName());

        return CompletableFuture.runAsync(() -> {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            try {
                objectMapper.writeValue(new File(fileName), errorResponses);
                long duration = System.nanoTime() - startTime;
                logger.info("Errors written to {} in {} ms", fileName, duration / 1_000_000);
            } catch (IOException e) {
                logger.error("Failed to write errors to file: {}", fileName, e);
                throw new CompletionException(e); // propagate exception to the CompletableFuture
            }
        });
    }



    private ResponseEntity<?> handleError(Throwable e, String message) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(message + ": " + e.getMessage());
    }

}


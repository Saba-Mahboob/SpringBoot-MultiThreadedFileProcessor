package com.example.multitreadreadfile.DTO;
import com.example.multitreadreadfile.Entities.Account;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    @NotNull
    @NotEmpty
    private String recordNumber;
    @NotNull
    @NotEmpty
    private String id;
    @Size(min = 3, max = 25)
    private String name;
    @Size(min = 3, max = 25)
    private String lastName;
    @NotNull
    @NotEmpty
    private String address;
    @NotNull
    @NotEmpty
    private String zipCode;
    @Size(min = 10, max = 10, message = "National ID must be exactly 10 characters long")
    private String nationalId;
    @NotNull
    @NotEmpty
    private String birthDate;



}


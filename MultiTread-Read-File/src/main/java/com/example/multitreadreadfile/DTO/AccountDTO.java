package com.example.multitreadreadfile.DTO;

import com.example.multitreadreadfile.Entities.Customer;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;

@Data
public class AccountDTO {
    @NotNull
    @NotEmpty
    private String recordNumber;
    //@Pattern(regexp = "^0\\d{21}$|^(?!0{22})\\d{22}$", message = "Account number must be 22 digits, starting with at least one '0', but not all zeros.")
    @NotNull
    @NotEmpty
    private String accountNumber;
    @NotNull
    private String type;
    @NotNull
    @NotEmpty
    private String customerId;
    @NotNull
    @Range(min = 500,max = 1000)
    private String limit;
    @NotNull
    @NotEmpty
    private String openDate;
    @NotNull
    private String balance;
    @AssertTrue(message = "Balance must be less than or equal to the limit")
    public boolean isBalanceLessThanOrEqualToLimit() {
        try {
            double balanceValue = Double.parseDouble(balance);
            double limitValue = Double.parseDouble(limit);
            return balanceValue <= limitValue;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}


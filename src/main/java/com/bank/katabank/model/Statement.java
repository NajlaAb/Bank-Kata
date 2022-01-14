package com.bank.katabank.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Statement {
    /**
     * Transaction Date
     */
    private String date;

    /**
     * Amount To Deposit Or To Withdraw
     */
    private int amount;

    /**
     * Balance
     */
    private int balance;
}

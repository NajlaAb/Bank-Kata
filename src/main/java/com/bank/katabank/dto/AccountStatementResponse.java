package com.bank.katabank.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementResponse {
    /**
     * Date of transaction
     */
    private String date;
    /**
     * Amount In
     */
    private int credit;
    /**
     * Amount Out
     */
    private int debit;
    /**
     * Current Balance
     */
    private int currentBalance;
}

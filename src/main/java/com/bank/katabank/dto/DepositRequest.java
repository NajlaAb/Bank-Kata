package com.bank.katabank.dto;

import com.bank.katabank.utils.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositRequest {
    /**
     * Account ID
     */
    @NotNull(message = MessageUtils.ACCOUNT_ID_NOT_PROVIDED)
    private Integer accountId;
    /**
     * Amount to Deposit
     */
    @NotNull(message = MessageUtils.AMOUNT_NOT_PROVIDED)
    private Integer amount;
}

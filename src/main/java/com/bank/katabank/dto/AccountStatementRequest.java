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
public class AccountStatementRequest {
    /**
     * Account ID
     */
    @NotNull(message = MessageUtils.ACCOUNT_ID_NOT_PROVIDED)
    private Integer accountId;
    /**
     * Period Type : Month, Year or Custom Period
     */
    private String periodType;
    /**
     * Year when PeriodType is Year
     */
    private int year;
    /**
     * Month when PeriodType is month
     */
    private String month;
    /**
     * Start Date when PeriodType is Custom
     */
    private String startDate;
    /**
     * End Date when PeriodType is Custom
     */
    private String endDate;
}

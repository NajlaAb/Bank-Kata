package com.bank.katabank.controller;

import com.bank.katabank.dto.AccountStatementRequest;
import com.bank.katabank.dto.AccountStatementResponse;
import com.bank.katabank.dto.DepositRequest;
import com.bank.katabank.dto.WithdrawRequest;
import com.bank.katabank.model.enumeratin.PeriodTypeEnum;
import com.bank.katabank.service.AccountService;
import com.bank.katabank.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AccountController.class)
public class AccountControllerUnitTest {

    @MockBean
    AccountService accountService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void shouldDepositMoney() throws Exception {
        DepositRequest request = DepositRequest.builder().accountId(1).amount(1200).build();

        Mockito.doNothing().when(accountService).deposit(any(DepositRequest.class));

        mockMvc.perform(post(MessageUtils.ACCOUNT + MessageUtils.DEPOSIT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldWithdrawMoney() throws Exception {
        WithdrawRequest request = WithdrawRequest.builder().accountId(1).amount(1200).build();

        Mockito.doNothing().when(accountService).withdraw(any(WithdrawRequest.class));

        mockMvc.perform(post(MessageUtils.ACCOUNT + MessageUtils.WITHDRAW)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetAccountStatement() throws Exception {
        List<AccountStatementResponse> response = new ArrayList<>();
        response.add(new AccountStatementResponse("13/11/2021", 1000, 0, 1000));
        response.add(new AccountStatementResponse("14/11/2021", 250, 0, 1250));
        response.add(new AccountStatementResponse("24/11/2021", 0, 500, 750));

        AccountStatementRequest request = AccountStatementRequest.builder()
                .accountId(1)
                .periodType(PeriodTypeEnum.YEAR.name())
                .year(2021)
                .build();

        when(accountService.getAccountStatement(any(AccountStatementRequest.class))).thenReturn(response);

        mockMvc.perform(post(MessageUtils.ACCOUNT + MessageUtils.STATEMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].currentBalance").value("750"));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

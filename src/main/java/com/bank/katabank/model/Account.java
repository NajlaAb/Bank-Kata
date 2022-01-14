package com.bank.katabank.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.bank.katabank.utils.MessageUtils.BEGINNING_VERTICAL_BAR;
import static com.bank.katabank.utils.MessageUtils.DATE_PATTERN;
import static com.bank.katabank.utils.MessageUtils.EMPTY_VALUE;
import static com.bank.katabank.utils.MessageUtils.ENDING_VERTICAL_BAR;
import static com.bank.katabank.utils.MessageUtils.STATEMENT_HEADER;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account {
    /**
     * Account ID
     */
    @Id
    private int id;
    /**
     * Current Balance
     */
    private int currentBalance;
    /**
     * Successful Transactions
     */
    private List<Statement> statements;

    public void deposit(int amount) {
        this.currentBalance += amount;
        this.statements.add(new Statement(
                new SimpleDateFormat(DATE_PATTERN).format(new Date()), amount, this.currentBalance
        ));
    }

    public void withdraw(int amount) {
        this.currentBalance -= amount;
        this.statements.add(new Statement(
                new SimpleDateFormat(DATE_PATTERN).format(new Date()), -amount, this.currentBalance
        ));
    }

    public void printStatements() {
        System.out.println(STATEMENT_HEADER);
        StringBuilder builder = new StringBuilder();
        for (Statement statement : this.statements) {
            builder.append(statement.getDate());
            builder.append(ENDING_VERTICAL_BAR);
            addAmount(statement, builder);
            builder.append(BEGINNING_VERTICAL_BAR).append(StringUtils.rightPad(String.valueOf(statement.getBalance()),
                    10, ' ')).append(" ");
            builder.append("\r\n");
        }
        System.out.println(builder.toString());
    }

    /**
     * Add amount to output statement
     * @param statement Statement to add amount to
     * @param builder   output result
     */
    private void addAmount(Statement statement, StringBuilder builder) {
        if (statement.getAmount() > 0) {
            builder.append(" ")
                    .append(StringUtils.rightPad(String.valueOf(statement.getAmount()),10, ' '))
                    .append(ENDING_VERTICAL_BAR).append(EMPTY_VALUE);
        } else {
            builder.append(EMPTY_VALUE).append(BEGINNING_VERTICAL_BAR)
                    .append(StringUtils.rightPad(String.valueOf(Math.abs(statement.getAmount())), 11, ' '));
        }
    }
}

package com.bank.katabank;

import com.bank.katabank.controller.AccountController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class KataBankApplicationTests {

	@Autowired
	private AccountController accountController;

	@Test
	void contextLoads() {
		Assertions.assertThat(accountController).isNotNull();
	}

}

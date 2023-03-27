package com.example.springBatchTutorial.job.DbDataReadWrite;

import com.example.springBatchTutorial.SpringBatchTestConfig;
import com.example.springBatchTutorial.job.core.domain.account.AccountsRepository;
import com.example.springBatchTutorial.job.core.domain.order.Orders;
import com.example.springBatchTutorial.job.core.domain.order.OrdersRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;


@RunWith(SpringRunner.class)
@SpringBatchTest
@SpringBootTest(classes = {SpringBatchTestConfig.class, TrMigrationConfig.class})
class TrMigrationConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @BeforeEach
    public void cleanUpBeforeEach() {
        ordersRepository.deleteAll();
        ordersRepository.deleteAll();
    }

    @Test()
    public void success_noData() throws Exception {

        JobExecution jobExecution = jobLauncherTestUtils.launchJob();

        Assertions.assertEquals(jobExecution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(0, accountsRepository.count());

    }

    @Test()
    public void success_existData() throws Exception {
        Orders orders1 = new Orders(null, "kakao gift", 15000, new Date());
        Orders orders2 = new Orders(null, "naver gift", 10000, new Date());

        ordersRepository.save(orders1);
        ordersRepository.save(orders2);

        JobExecution execution = jobLauncherTestUtils.launchJob();

        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
        Assertions.assertEquals(2, accountsRepository.count());
    }

}

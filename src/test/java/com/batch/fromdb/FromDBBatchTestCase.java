package com.batch.fromdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.StopWatch;

@SpringBatchTest
@SpringJUnitConfig(locations = "classpath:com/batch/fromdb/contextFromDB.xml")
public class FromDBBatchTestCase {

	private static final Logger logger = LoggerFactory
			.getLogger(FromDBBatchTestCase.class);

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	public void seedData() {
		jdbcTemplate.update("DELETE FROM ledger");
		var date = Date.valueOf(LocalDate.of(2009, 2, 22));
		jdbcTemplate.update(
				"INSERT INTO ledger (rcv_dt, mbr_nm, chk_nbr, chk_dt, pymt_typ, dpst_amt, pymt_amt, comments) VALUES (?,?,?,?,?,?,?,?)",
				date, "Person1", "1432", date, "Offertery", new BigDecimal("50.00"), BigDecimal.ZERO, "test comment");
		jdbcTemplate.update(
				"INSERT INTO ledger (rcv_dt, mbr_nm, chk_nbr, chk_dt, pymt_typ, dpst_amt, pymt_amt, comments) VALUES (?,?,?,?,?,?,?,?)",
				date, "Person2", "900", date, "Membership", new BigDecimal("800.00"), BigDecimal.ZERO, null);
	}

	@Test
	public void testLaunchJob() throws Exception {
		var sw = new StopWatch();
		sw.start();
		var execution = jobLauncherTestUtils.launchJob();
		sw.stop();
		logger.info(">>> TIME ELAPSED:{}", sw.prettyPrint());

		assertEquals(BatchStatus.COMPLETED, execution.getStatus());

		var stepExecution = execution.getStepExecutions().iterator().next();
		assertEquals(2, stepExecution.getReadCount(), "Should have read the 2 seeded rows");
		assertEquals(2, stepExecution.getWriteCount(), "Should have written 2 rows to file");

		var outputFile = new File("target/ledgers-output.txt");
		assertTrue(outputFile.exists(), "Output file should exist");
		var lines = Files.readAllLines(outputFile.toPath());
		assertEquals(2, lines.size(), "Output file should have 2 lines");
		assertTrue(lines.get(0).contains("Person1"), "First line should contain Person1");
		assertTrue(lines.get(1).contains("Person2"), "Second line should contain Person2");
	}
}

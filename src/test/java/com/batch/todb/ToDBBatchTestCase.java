package com.batch.todb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

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
@SpringJUnitConfig(locations = "classpath:com/batch/todb/contextToDB.xml")
public class ToDBBatchTestCase {

	private static final Logger logger = LoggerFactory
			.getLogger(ToDBBatchTestCase.class);

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@BeforeEach
	public void cleanTable() {
		jdbcTemplate.update("DELETE FROM ledger");
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
		assertTrue(stepExecution.getReadCount() > 0, "Should have read at least one item");
		assertEquals(stepExecution.getReadCount(), stepExecution.getWriteCount(), "Read and write counts should match");

		// Verify data actually persisted to the database
		var rowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ledger", Long.class);
		assertEquals(stepExecution.getWriteCount(), (long) rowCount, "DB row count should match write count");

		// Verify a sample row has expected values
		var firstMember = jdbcTemplate.queryForObject(
				"SELECT mbr_nm FROM ledger ORDER BY id LIMIT 1", String.class);
		assertEquals("Person1", firstMember, "First row should be Person1");

		var depositAmt = jdbcTemplate.queryForObject(
				"SELECT dpst_amt FROM ledger ORDER BY id LIMIT 1", BigDecimal.class);
		assertEquals(new BigDecimal("50.00"), depositAmt, "First row deposit should be $50.00");
	}
}

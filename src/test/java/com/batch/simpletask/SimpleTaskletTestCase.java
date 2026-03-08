package com.batch.simpletask;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.StopWatch;

@SpringBatchTest
@SpringJUnitConfig(locations = "classpath:com/batch/simpletask/simpletaskletcontext.xml")
public class SimpleTaskletTestCase {

	private static final Logger logger = LoggerFactory
			.getLogger(SimpleTaskletTestCase.class);

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Test
	public void testLaunchJob() throws Exception {
		var sw = new StopWatch();
		sw.start();
		var execution = jobLauncherTestUtils.launchJob();
		sw.stop();
		logger.info(">>> TIME ELAPSED:{}", sw.prettyPrint());

		assertEquals(BatchStatus.COMPLETED, execution.getStatus());
		assertEquals(2, execution.getStepExecutions().size());
	}
}

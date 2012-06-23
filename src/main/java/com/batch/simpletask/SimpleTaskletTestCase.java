package com.batch.simpletask;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StopWatch;

@ContextConfiguration(locations = "classpath:com/batch/simpletask/simpletaskletcontext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class SimpleTaskletTestCase {

	private final static Logger logger = Logger
			.getLogger(SimpleTaskletTestCase.class);

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	private JobParameters jobParameters = new JobParameters();

	@Test
	public void testLaunchJob() throws Exception {
		StopWatch sw = new StopWatch();
		sw.start();
		jobLauncher.run(job, jobParameters);
		sw.stop();
		logger.info(">>> TIME ELAPSED:" + sw.prettyPrint());
	}
}

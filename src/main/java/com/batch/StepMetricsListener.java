package com.batch;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component("stepMetricsListener")
public class StepMetricsListener implements StepExecutionListener {

	private static final Logger logger = LoggerFactory.getLogger(StepMetricsListener.class);

	@Override
	public void beforeStep(StepExecution stepExecution) {
		MDC.put("stepName", stepExecution.getStepName());
		logger.info("Step [{}] starting", stepExecution.getStepName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		var duration = Duration.between(stepExecution.getStartTime(), stepExecution.getEndTime());
		var millis = duration.toMillis();

		logger.info("Step [{}] completed in {}ms - Read: {}, Processed: {}, Written: {}, " +
						"Skipped: {}, Filtered: {}, Commits: {}, Rollbacks: {}",
				stepExecution.getStepName(),
				millis,
				stepExecution.getReadCount(),
				stepExecution.getReadCount() - stepExecution.getFilterCount(),
				stepExecution.getWriteCount(),
				stepExecution.getSkipCount(),
				stepExecution.getFilterCount(),
				stepExecution.getCommitCount(),
				stepExecution.getRollbackCount());

		MDC.remove("stepName");
		return stepExecution.getExitStatus();
	}
}

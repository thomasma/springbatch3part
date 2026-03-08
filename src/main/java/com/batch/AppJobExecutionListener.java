package com.batch;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component("appJobExecutionListener")
public class AppJobExecutionListener implements JobExecutionListener {
	private static final Logger logger = LoggerFactory
			.getLogger(AppJobExecutionListener.class);

	private final BatchMetricsLogger batchMetricsLogger;

	public AppJobExecutionListener(BatchMetricsLogger batchMetricsLogger) {
		this.batchMetricsLogger = batchMetricsLogger;
	}

	@Override
	public void beforeJob(JobExecution jobExecution) {
		MDC.put("jobName", jobExecution.getJobInstance().getJobName());
		MDC.put("jobInstanceId", String.valueOf(jobExecution.getJobInstance().getInstanceId()));
		logger.info("Job starting (id={})", jobExecution.getJobId());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		var duration = Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime());
		var exitDescription = jobExecution.getExitStatus().getExitDescription();

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			logger.info("Job completed (id={}) in {}{}", jobExecution.getJobId(), formatDuration(duration),
					exitDescription.isEmpty() ? "" : " - " + exitDescription);
		} else if (jobExecution.getStatus() == BatchStatus.FAILED) {
			logger.error("Job failed (id={}) after {}{}", jobExecution.getJobId(), formatDuration(duration),
					exitDescription.isEmpty() ? "" : " - " + exitDescription);
			for (var ex : jobExecution.getAllFailureExceptions()) {
				logger.error("  Failure cause: {}", ex.getMessage());
			}
		}

		batchMetricsLogger.recordJobMetrics(jobExecution);

		MDC.remove("jobName");
		MDC.remove("jobInstanceId");
	}

	private String formatDuration(Duration duration) {
		var seconds = duration.toSeconds();
		var millis = duration.toMillisPart();
		if (seconds > 0) {
			return seconds + "." + String.format("%03d", millis) + "s";
		}
		return millis + "ms";
	}
}

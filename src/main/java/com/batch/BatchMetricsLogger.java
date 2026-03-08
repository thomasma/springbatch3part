package com.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component("batchMetricsLogger")
public class BatchMetricsLogger {

	private static final Logger logger = LoggerFactory.getLogger(BatchMetricsLogger.class);
	private final MeterRegistry meterRegistry;

	public BatchMetricsLogger(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
	}

	public void recordJobMetrics(JobExecution jobExecution) {
		var jobName = jobExecution.getJobInstance().getJobName();
		var status = jobExecution.getStatus().toString();
		var duration = Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime());

		Timer.builder("batch.job.duration")
				.tag("job", jobName)
				.tag("status", status)
				.register(meterRegistry)
				.record(duration.toMillis(), TimeUnit.MILLISECONDS);

		meterRegistry.counter("batch.job.count", "job", jobName, "status", status).increment();

		for (var stepExecution : jobExecution.getStepExecutions()) {
			recordStepMetrics(jobName, stepExecution);
		}

		logMetricsSummary(jobName);
	}

	private void recordStepMetrics(String jobName, StepExecution stepExecution) {
		var stepName = stepExecution.getStepName();
		var tags = new String[]{"job", jobName, "step", stepName};

		meterRegistry.gauge("batch.step.read.count", java.util.List.of(
				io.micrometer.core.instrument.Tag.of("job", jobName),
				io.micrometer.core.instrument.Tag.of("step", stepName)),
				stepExecution.getReadCount());

		meterRegistry.gauge("batch.step.write.count", java.util.List.of(
				io.micrometer.core.instrument.Tag.of("job", jobName),
				io.micrometer.core.instrument.Tag.of("step", stepName)),
				stepExecution.getWriteCount());

		meterRegistry.counter("batch.step.skip.count", tags).increment(stepExecution.getSkipCount());
		meterRegistry.counter("batch.step.commit.count", tags).increment(stepExecution.getCommitCount());
		meterRegistry.counter("batch.step.rollback.count", tags).increment(stepExecution.getRollbackCount());
	}

	private void logMetricsSummary(String jobName) {
		logger.info("=== Metrics Summary for job [{}] ===", jobName);
		meterRegistry.getMeters().stream()
				.filter(meter -> meter.getId().getTag("job") != null
						&& meter.getId().getTag("job").equals(jobName))
				.forEach(meter -> logger.info("  {} {} = {}", meter.getId().getType(),
						meter.getId().getName(), meter.measure()));
		logger.info("=== End Metrics Summary ===");
	}
}

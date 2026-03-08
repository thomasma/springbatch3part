package com.batch.simpletask;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class TimeTask implements Tasklet {
	private static final Logger logger = LoggerFactory.getLogger(TimeTask.class);

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		logger.info("{}", LocalDateTime.now());
		return RepeatStatus.FINISHED;
	}
}

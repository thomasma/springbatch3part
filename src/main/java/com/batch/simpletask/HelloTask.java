package com.batch.simpletask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class HelloTask implements Tasklet {
	private static final Logger logger = LoggerFactory.getLogger(HelloTask.class);

	private String taskStartMessage;

	public void setTaskStartMessage(String taskStartMessage) {
		this.taskStartMessage = taskStartMessage;
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
		logger.info(taskStartMessage);
		return RepeatStatus.FINISHED;
	}
}

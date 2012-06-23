package com.batch.simpletask;

import java.util.Calendar;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class TimeTask implements Tasklet {
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1)
			throws Exception {
		System.out.println(Calendar.getInstance().getTime());
		return RepeatStatus.FINISHED;
	}
}

package com.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Component;

@Component("itemFailureLoggerListener")
public class ItemFailureLoggerListener implements ItemReadListener<Object>, ItemWriteListener<Object> {
	private static final Logger logger = LoggerFactory
			.getLogger(ItemFailureLoggerListener.class);

	@Override
	public void onReadError(Exception ex) {
		logger.error("Encountered error on read", ex);
	}

	@Override
	public void onWriteError(Exception ex, Chunk<?> items) {
		logger.error("Encountered error on write", ex);
	}
}

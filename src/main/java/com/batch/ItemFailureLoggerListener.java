package com.batch;

import org.apache.log4j.Logger;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.stereotype.Component;

@Component("itemFailureLoggerListener")
public class ItemFailureLoggerListener extends ItemListenerSupport {
	private final static Logger logger = Logger
			.getLogger(ItemFailureLoggerListener.class);

	public void onReadError(Exception ex) {
		logger.error("Encountered error on read", ex);
	}

	public void onWriteError(Exception ex, Object item) {
		logger.error("Encountered error on write", ex);
	}

}

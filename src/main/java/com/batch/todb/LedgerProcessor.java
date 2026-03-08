package com.batch.todb;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component("ledgerProcessor")
public class LedgerProcessor implements ItemProcessor<Ledger, Ledger> {

	private static final Logger logger = LoggerFactory.getLogger(LedgerProcessor.class);

	@Override
	public Ledger process(Ledger ledger) {
		if (ledger.depositAmount().compareTo(BigDecimal.ZERO) < 0
				|| ledger.paymentAmount().compareTo(BigDecimal.ZERO) < 0) {
			logger.warn("Filtering out ledger with negative amount: {}", ledger.memberName());
			return null;
		}

		var normalizedType = ledger.paymentType().trim().toUpperCase();
		if (normalizedType.equals(ledger.paymentType())) {
			return ledger;
		}

		return new Ledger(
				ledger.id(),
				ledger.receiptDate(),
				ledger.memberName(),
				ledger.checkNumber(),
				ledger.checkDate(),
				normalizedType,
				ledger.depositAmount(),
				ledger.paymentAmount(),
				ledger.comments());
	}
}

package com.batch.todb;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Ledger(
		int id,
		LocalDate receiptDate,
		String memberName,
		String checkNumber,
		LocalDate checkDate,
		String paymentType,
		BigDecimal depositAmount,
		BigDecimal paymentAmount,
		String comments) {
}

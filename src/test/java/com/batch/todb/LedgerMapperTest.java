package com.batch.todb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.file.transform.DefaultFieldSet;

public class LedgerMapperTest {

	private final LedgerMapper mapper = new LedgerMapper();

	@Test
	public void testMapFieldSet() {
		var tokens = new String[]{"02/22/09", "John Doe", "1432", "02/22/09", "Offertery", "$50.00", "$0.00"};
		var names = new String[]{"receiptDate", "memberName", "checkNumber", "checkDate", "paymentType", "depositAmount", "paymentAmount"};
		var fieldSet = new DefaultFieldSet(tokens, names);

		var ledger = mapper.mapFieldSet(fieldSet);

		assertEquals(0, ledger.id());
		assertEquals(LocalDate.of(2009, 1, 22), ledger.receiptDate());
		assertEquals("John Doe", ledger.memberName());
		assertEquals("1432", ledger.checkNumber());
		assertEquals(LocalDate.of(2009, 1, 22), ledger.checkDate());
		assertEquals("Offertery", ledger.paymentType());
		assertEquals(BigDecimal.valueOf(50.0), ledger.depositAmount());
		assertEquals(BigDecimal.valueOf(0.0), ledger.paymentAmount());
	}

	@Test
	public void testLargeDollarAmountWithCommas() {
		var tokens = new String[]{"02/22/09", "Jane Smith", "900", "02/22/09", "Membership", "$1,234.56", "$0.00"};
		var names = new String[]{"receiptDate", "memberName", "checkNumber", "checkDate", "paymentType", "depositAmount", "paymentAmount"};
		var fieldSet = new DefaultFieldSet(tokens, names);

		var ledger = mapper.mapFieldSet(fieldSet);

		assertEquals(BigDecimal.valueOf(1234.56), ledger.depositAmount());
	}

	@Test
	public void testInvalidDollarAmountReturnsZero() {
		var tokens = new String[]{"02/22/09", "Test User", "100", "02/22/09", "Check", "invalid", "$10.00"};
		var names = new String[]{"receiptDate", "memberName", "checkNumber", "checkDate", "paymentType", "depositAmount", "paymentAmount"};
		var fieldSet = new DefaultFieldSet(tokens, names);

		var ledger = mapper.mapFieldSet(fieldSet);

		assertEquals(BigDecimal.ZERO, ledger.depositAmount());
		assertEquals(BigDecimal.valueOf(10.0), ledger.paymentAmount());
	}

	@Test
	public void testCommentsIsNull() {
		var tokens = new String[]{"02/22/09", "Test User", "100", "02/22/09", "Check", "$5.00", "$0.00"};
		var names = new String[]{"receiptDate", "memberName", "checkNumber", "checkDate", "paymentType", "depositAmount", "paymentAmount"};
		var fieldSet = new DefaultFieldSet(tokens, names);

		var ledger = mapper.mapFieldSet(fieldSet);

		assertEquals(null, ledger.comments());
	}
}

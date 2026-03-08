package com.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.batch.todb.Ledger;

public class AppTest {

	@Test
	public void testAllAccessors() {
		var date = LocalDate.of(2024, 1, 15);
		var ledger = new Ledger(1, date, "John Doe", "1432", date, "Check",
				new BigDecimal("100.50"), new BigDecimal("25.00"), "test comment");

		assertEquals(1, ledger.id());
		assertEquals(date, ledger.receiptDate());
		assertEquals("John Doe", ledger.memberName());
		assertEquals("1432", ledger.checkNumber());
		assertEquals(date, ledger.checkDate());
		assertEquals("Check", ledger.paymentType());
		assertEquals(new BigDecimal("100.50"), ledger.depositAmount());
		assertEquals(new BigDecimal("25.00"), ledger.paymentAmount());
		assertEquals("test comment", ledger.comments());
	}

	@Test
	public void testEquality() {
		var date = LocalDate.of(2024, 1, 15);
		var ledger1 = new Ledger(1, date, "John Doe", "1432", date, "Check",
				new BigDecimal("100.00"), BigDecimal.ZERO, null);
		var ledger2 = new Ledger(1, date, "John Doe", "1432", date, "Check",
				new BigDecimal("100.00"), BigDecimal.ZERO, null);
		var ledger3 = new Ledger(2, date, "Jane Doe", "999", date, "Cash",
				new BigDecimal("50.00"), BigDecimal.ZERO, null);

		assertEquals(ledger1, ledger2);
		assertEquals(ledger1.hashCode(), ledger2.hashCode());
		assertNotEquals(ledger1, ledger3);
	}

	@Test
	public void testToStringContainsFields() {
		var ledger = new Ledger(1, LocalDate.of(2024, 1, 15), "John Doe", "1432",
				LocalDate.of(2024, 1, 15), "Check", new BigDecimal("100.00"), BigDecimal.ZERO, "notes");
		var str = ledger.toString();

		assertTrue(str.contains("John Doe"), "toString should contain memberName");
		assertTrue(str.contains("1432"), "toString should contain checkNumber");
		assertTrue(str.contains("100.00"), "toString should contain depositAmount");
	}

	@Test
	public void testNullComments() {
		var ledger = new Ledger(0, LocalDate.now(), "Test", "100", LocalDate.now(),
				"Check", BigDecimal.ZERO, BigDecimal.ZERO, null);
		assertEquals(null, ledger.comments());
	}
}

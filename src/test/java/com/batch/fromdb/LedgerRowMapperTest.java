package com.batch.fromdb;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(locations = "classpath:com/batch/fromdb/contextFromDB.xml")
public class LedgerRowMapperTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private final LedgerRowMapper rowMapper = new LedgerRowMapper();

	@BeforeEach
	public void setUp() {
		jdbcTemplate.update("DELETE FROM ledger");
	}

	@Test
	public void testMapAllFields() {
		var rcvDate = LocalDate.of(2024, 3, 10);
		var chkDate = LocalDate.of(2024, 3, 5);
		jdbcTemplate.update(
				"INSERT INTO ledger (rcv_dt, mbr_nm, chk_nbr, chk_dt, pymt_typ, dpst_amt, pymt_amt, comments) VALUES (?,?,?,?,?,?,?,?)",
				Date.valueOf(rcvDate), "John Doe", "1432", Date.valueOf(chkDate), "Offertery",
				new BigDecimal("250.75"), new BigDecimal("10.00"), "test comment");

		var ledger = jdbcTemplate.queryForObject("SELECT * FROM ledger WHERE chk_nbr = '1432'", rowMapper);

		assertEquals(rcvDate, ledger.receiptDate());
		assertEquals("John Doe", ledger.memberName());
		assertEquals("1432", ledger.checkNumber());
		assertEquals(chkDate, ledger.checkDate());
		assertEquals("Offertery", ledger.paymentType());
		assertEquals(new BigDecimal("250.75"), ledger.depositAmount());
		assertEquals(new BigDecimal("10.00"), ledger.paymentAmount());
		assertEquals("test comment", ledger.comments());
	}

	@Test
	public void testBigDecimalScale() {
		var date = Date.valueOf(LocalDate.of(2024, 1, 1));
		jdbcTemplate.update(
				"INSERT INTO ledger (rcv_dt, mbr_nm, chk_nbr, chk_dt, pymt_typ, dpst_amt, pymt_amt, comments) VALUES (?,?,?,?,?,?,?,?)",
				date, "Scale Test", "999", date, "Check",
				new BigDecimal("1234.56"), new BigDecimal("0.00"), null);

		var ledger = jdbcTemplate.queryForObject("SELECT * FROM ledger WHERE chk_nbr = '999'", rowMapper);

		assertEquals(new BigDecimal("1234.56"), ledger.depositAmount());
		assertEquals(new BigDecimal("0.00"), ledger.paymentAmount());
	}

	@Test
	public void testNullComments() {
		var date = Date.valueOf(LocalDate.of(2024, 1, 1));
		jdbcTemplate.update(
				"INSERT INTO ledger (rcv_dt, mbr_nm, chk_nbr, chk_dt, pymt_typ, dpst_amt, pymt_amt, comments) VALUES (?,?,?,?,?,?,?,?)",
				date, "Null Test", "888", date, "Cash",
				BigDecimal.ZERO, BigDecimal.ZERO, null);

		var ledger = jdbcTemplate.queryForObject("SELECT * FROM ledger WHERE chk_nbr = '888'", rowMapper);

		assertEquals(null, ledger.comments());
	}
}

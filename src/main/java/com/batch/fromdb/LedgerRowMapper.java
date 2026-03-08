package com.batch.fromdb;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.batch.todb.Ledger;

@Component("ledgerRowMapper")
public class LedgerRowMapper implements RowMapper<Ledger> {
	@Override
	public Ledger mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Ledger(
				rs.getInt("id"),
				rs.getDate("rcv_dt").toLocalDate(),
				rs.getString("mbr_nm"),
				rs.getString("chk_nbr"),
				rs.getDate("chk_dt").toLocalDate(),
				rs.getString("pymt_typ"),
				rs.getBigDecimal("dpst_amt"),
				rs.getBigDecimal("pymt_amt"),
				rs.getString("comments"));
	}
}

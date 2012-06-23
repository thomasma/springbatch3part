package com.batch.todb;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LedgerDAOImpl implements LedgerDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional(propagation = Propagation.REQUIRED)
	public void save(final Ledger item) {
		jdbcTemplate
				.update("insert into ledger (rcv_dt, mbr_nm, chk_nbr, chk_dt, pymt_typ, dpst_amt, pymt_amt, comments) values(?,?,?,?,?,?,?,?)",
						new PreparedStatementSetter() {
							public void setValues(PreparedStatement stmt)
									throws SQLException {
								stmt.setDate(1, new java.sql.Date(item
										.getReceiptDate().getTime()));
								stmt.setString(2, item.getMemberName());
								stmt.setString(3, item.getCheckNumber());
								stmt.setDate(4, new java.sql.Date(item
										.getCheckDate().getTime()));
								stmt.setString(5, item.getPaymentType());
								stmt.setDouble(6, item.getDepositAmount());
								stmt.setDouble(7, item.getPaymentAmount());
								stmt.setString(8, item.getComments());
							}
						});
	}
}

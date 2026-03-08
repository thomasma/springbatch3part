package com.batch.todb;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.ZoneId;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;

@Component("ledgerMapper")
public class LedgerMapper implements FieldSetMapper<Ledger> {
	private static final String DATE_PATTERN = "mm/DD/yy";
	private final DecimalFormat dollarFormat = new DecimalFormat("$###,###.###");

	@Override
	public Ledger mapFieldSet(FieldSet fs) {
		int idx = 0;
		return new Ledger(
				0,
				fs.readDate(idx++, DATE_PATTERN).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				fs.readString(idx++),
				fs.readString(idx++),
				fs.readDate(idx++, DATE_PATTERN).toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
				fs.readString(idx++),
				parseDollarAmount(fs.readString(idx++)),
				parseDollarAmount(fs.readString(idx++)),
				null);
	}

	private BigDecimal parseDollarAmount(String value) {
		try {
			return BigDecimal.valueOf(dollarFormat.parse(value).doubleValue());
		} catch (ParseException e) {
			return BigDecimal.ZERO;
		}
	}
}

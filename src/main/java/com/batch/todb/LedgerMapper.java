package com.batch.todb;

import java.text.DecimalFormat;
import java.text.ParseException;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component("ledgerMapper")
public class LedgerMapper implements FieldSetMapper {
	private final static String DATE_PATTERN = "mm/DD/yy";
	private final static String DOLLAR_PATTERN = "$###,###.###";

	public Object mapFieldSet(FieldSet fs) {
		Ledger item = new Ledger();
		int idx = 0;
		item.setReceiptDate(fs.readDate(idx++, DATE_PATTERN));
		item.setMemberName(fs.readString(idx++));
		item.setCheckNumber(fs.readString(idx++));
		item.setCheckDate(fs.readDate(idx++, DATE_PATTERN));
		item.setPaymentType(fs.readString(idx++));

		// deposit amount
		try {
			DecimalFormat fmttr = new DecimalFormat(DOLLAR_PATTERN);
			Number number = fmttr.parse(fs.readString(idx++));
			item.setDepositAmount(number.doubleValue());
		} catch (ParseException e) {
			item.setDepositAmount(0);
		}

		// payment amount
		try {
			DecimalFormat fmttr = new DecimalFormat(DOLLAR_PATTERN);
			Number number = fmttr.parse(fs.readString(idx++));
			item.setPaymentAmount(number.doubleValue());
		} catch (ParseException e) {
			item.setPaymentAmount(0);
		}

		//
		return item;
	}
}

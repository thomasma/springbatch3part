package com.batch.todb;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("itemWriter")
public class LedgerWriter implements ItemWriter {

	@Autowired
	private LedgerDAO itemDAO;

	public void write(List items) throws Exception {
		for (Iterator<Ledger> iterator = items.iterator(); iterator.hasNext();) {
			Ledger item = iterator.next();
			itemDAO.save(item);
		}
	}
}
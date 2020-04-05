package de.deadlocker8.budgetmaster.transactions;

import de.deadlocker8.budgetmaster.accounts.Account;
import de.deadlocker8.budgetmaster.categories.Category;
import de.deadlocker8.budgetmaster.tags.Tag;

import java.util.List;
import java.util.Optional;

public interface TransactionBase
{
	Category getCategory();

	List<Tag> getTags();

	Account getAccount();

	void setAccount(Account account);

	Account getTransferAccount();

	void setTransferAccount(Account account);
}
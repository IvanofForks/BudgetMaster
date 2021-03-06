package de.deadlocker8.budgetmaster.accounts;

import de.deadlocker8.budgetmaster.controller.BaseController;
import de.deadlocker8.budgetmaster.settings.SettingsService;
import de.deadlocker8.budgetmaster.utils.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


@Controller
public class AccountController extends BaseController
{
	private final AccountRepository accountRepository;
	private final AccountService accountService;
	private final SettingsService settingsService;

	@Autowired
	public AccountController(AccountRepository accountRepository, AccountService accountService, SettingsService settingsService)
	{
		this.accountRepository = accountRepository;
		this.accountService = accountService;
		this.settingsService = settingsService;
	}

	@GetMapping(value = "/accounts/{ID}/select")
	public String selectAccount(HttpServletRequest request, @PathVariable("ID") Integer ID)
	{
		accountService.selectAccount(ID);

		String referer = request.getHeader("Referer");
		if(referer.contains("database/import"))
		{
			return "redirect:/settings";
		}
		return "redirect:" + referer;
	}

	@GetMapping(value = "/accounts/{ID}/setAsDefault")
	public String setAsDefault(HttpServletRequest request, @PathVariable("ID") Integer ID)
	{
		accountService.setAsDefaultAccount(ID);

		String referer = request.getHeader("Referer");
		if(referer.contains("database/import"))
		{
			return "redirect:/settings";
		}
		return "redirect:" + referer;
	}

	@GetMapping("/accounts")
	public String accounts(Model model)
	{
		model.addAttribute("accounts", accountService.getAllAccountsAsc());
		model.addAttribute("settings", settingsService.getSettings());
		return "accounts/accounts";
	}

	@GetMapping("/accounts/{ID}/requestDelete")
	public String requestDeleteAccount(Model model, @PathVariable("ID") Integer ID)
	{
		model.addAttribute("accounts", accountService.getAllAccountsAsc());
		model.addAttribute("currentAccount", accountRepository.getOne(ID));
		model.addAttribute("settings", settingsService.getSettings());
		return "accounts/accounts";
	}

	@GetMapping("/accounts/{ID}/delete")
	public String deleteAccountAndReferringTransactions(Model model, @PathVariable("ID") Integer ID)
	{
		if(accountRepository.findAllByType(AccountType.CUSTOM).size() > 1)
		{
			accountService.deleteAccount(ID);
			return "redirect:/accounts";
		}

		model.addAttribute("accounts", accountService.getAllAccountsAsc());
		model.addAttribute("currentAccount", accountRepository.getOne(ID));
		model.addAttribute("accountNotDeletable", true);
		model.addAttribute("settings", settingsService.getSettings());
		return "accounts/accounts";
	}

	@GetMapping("/accounts/newAccount")
	public String newAccount(Model model)
	{
		Account emptyAccount = new Account();
		model.addAttribute("account", emptyAccount);
		model.addAttribute("settings", settingsService.getSettings());
		return "accounts/newAccount";
	}

	@GetMapping("/accounts/{ID}/edit")
	public String editAccount(Model model, @PathVariable("ID") Integer ID)
	{
		Optional<Account> accountOptional = accountRepository.findById(ID);
		if(!accountOptional.isPresent())
		{
			throw new ResourceNotFoundException();
		}

		model.addAttribute("account", accountOptional.get());
		model.addAttribute("settings", settingsService.getSettings());
		return "accounts/newAccount";
	}

	@PostMapping(value = "/accounts/newAccount")
	public String post(HttpServletRequest request, Model model,
					   @ModelAttribute("NewAccount") Account account,
					   BindingResult bindingResult)
	{
		AccountValidator accountValidator = new AccountValidator();
		accountValidator.validate(account, bindingResult);

		if(accountRepository.findByName(account.getName()) != null)
		{
			bindingResult.addError(new FieldError("NewAccount", "name", "", false, new String[]{"warning.duplicate.account.name"}, null, null));
		}

		if(bindingResult.hasErrors())
		{
			model.addAttribute("error", bindingResult);
			model.addAttribute("account", account);
			model.addAttribute("settings", settingsService.getSettings());
			return "accounts/newAccount";
		}
		else
		{
			account.setType(AccountType.CUSTOM);
			if(account.getID() == null)
			{
				// new account
				accountRepository.save(account);
			}
			else
			{
				// edit existing account
				Optional<Account> existingAccountOptional = accountRepository.findById(account.getID());
				if(existingAccountOptional.isPresent())
				{
					Account existingAccount = existingAccountOptional.get();
					existingAccount.setName(account.getName());
					accountRepository.save(existingAccount);
				}
			}
		}

		if(request.getSession().getAttribute("database") != null)
		{
			return "redirect:/settings/database/accountMatcher";
		}

		return "redirect:/accounts";
	}
}
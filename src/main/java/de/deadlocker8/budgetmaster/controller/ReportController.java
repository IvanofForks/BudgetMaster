package de.deadlocker8.budgetmaster.controller;

import com.itextpdf.text.DocumentException;
import de.deadlocker8.budgetmaster.entities.Transaction;
import de.deadlocker8.budgetmaster.entities.account.Account;
import de.deadlocker8.budgetmaster.entities.report.ReportColumn;
import de.deadlocker8.budgetmaster.entities.report.ReportSettings;
import de.deadlocker8.budgetmaster.reports.Budget;
import de.deadlocker8.budgetmaster.reports.ReportConfiguration;
import de.deadlocker8.budgetmaster.reports.ReportConfigurationBuilder;
import de.deadlocker8.budgetmaster.services.HelpersService;
import de.deadlocker8.budgetmaster.services.SettingsService;
import de.deadlocker8.budgetmaster.services.TransactionService;
import de.deadlocker8.budgetmaster.services.report.ReportColumnService;
import de.deadlocker8.budgetmaster.services.report.ReportGeneratorService;
import de.deadlocker8.budgetmaster.services.report.ReportSettingsService;
import de.thecodelabs.utils.util.Localization;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Controller
public class ReportController extends BaseController
{
	@Autowired
	private SettingsService settingsService;

	@Autowired
	private ReportSettingsService reportSettingsService;

	@Autowired
	private ReportColumnService reportColumnService;

	@Autowired
	private ReportGeneratorService reportGeneratorService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private HelpersService helpers;

	@RequestMapping("/reports")
	public String reports(Model model, @CookieValue(value = "currentDate", required = false) String cookieDate)
	{
		DateTime date = helpers.getDateTimeFromCookie(cookieDate);

		model.addAttribute("reportSettings", reportSettingsService.getReportSettings());
		model.addAttribute("currentDate", date);
		return "reports/reports";
	}

	@RequestMapping(value = "/reports/generate", method = RequestMethod.POST)
	public String post(HttpServletResponse response,
					   @ModelAttribute("NewReportSettings") ReportSettings reportSettings)
	{
		reportSettingsService.getRepository().delete(0);
		for(ReportColumn reportColumn : reportSettings.getColumns())
		{
			reportColumnService.getRepository().save(reportColumn);
		}
		reportSettingsService.getRepository().save(reportSettings);


		LOGGER.debug("Exporting month report...");

		//TODO handle all accounts
		Account account = helpers.getCurrentAccount();
		List<Transaction> transactions = transactionService.getTransactionsForMonthAndYear(account, reportSettings.getDate().getMonthOfYear(), reportSettings.getDate().getYear(), settingsService.getSettings().isRestActivated());
		Budget budget = new Budget(helpers.getIncomeSumForTransactionList(transactions), helpers.getExpenditureSumForTransactionList(transactions));

		ReportConfiguration reportConfiguration = new ReportConfigurationBuilder()
				.setBudget(budget)
				.setCategoryBudgets(new ArrayList<>())
				.setReportSettings(reportSettings)
				.setReportItems(new ArrayList<>())
				.createReportConfiguration();

		try
		{
			byte[] dataBytes = reportGeneratorService.generate(reportConfiguration);
			String fileName = Localization.getString("report.initial.filename", reportSettings.getDate().toString("MM"), reportSettings.getDate().toString("YYYY"));
			response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

			response.setContentType("application/pdf; charset=UTF-8");
			response.setContentLength(dataBytes.length);
			response.setCharacterEncoding("UTF-8");

			try(ServletOutputStream out = response.getOutputStream())
			{
				out.write(dataBytes);
				out.flush();
				LOGGER.debug("Exporting month report DONE");
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		catch(DocumentException e)
		{
			e.printStackTrace();
		}

		return "redirect:/reports";
	}
}
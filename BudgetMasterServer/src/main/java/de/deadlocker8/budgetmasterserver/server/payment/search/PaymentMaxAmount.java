package de.deadlocker8.budgetmasterserver.server.payment.search;

import static spark.Spark.halt;

import com.google.gson.Gson;

import de.deadlocker8.budgetmasterserver.logic.database.DatabaseHandler;
import spark.Request;
import spark.Response;
import spark.Route;

public class PaymentMaxAmount implements Route
{
	private DatabaseHandler handler;
	private Gson gson;

	public PaymentMaxAmount(DatabaseHandler handler, Gson gson)
	{
		this.handler = handler;
		this.gson = gson;
	}

	@Override
	public Object handle(Request req, Response res) throws Exception
	{
		try
		{
			int maxNormal = handler.getNormalPaymentMaxAmount();
			int maxRepeating = handler.getRepeatingPaymentMaxAmount();
			
			int max = maxNormal;
			if(maxRepeating > max)
			{
				max = maxRepeating;
			}
			//plus 1 to allow all amounts up to maxNormal.99 €
			return gson.toJson((max+1)/100);
		}
		catch(IllegalStateException ex)
		{
			halt(500, "Internal Server Error");
		}

		return null;
	}
}
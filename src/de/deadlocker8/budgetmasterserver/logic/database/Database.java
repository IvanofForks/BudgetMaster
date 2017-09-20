package de.deadlocker8.budgetmasterserver.logic.database;

import java.util.ArrayList;

import de.deadlocker8.budgetmaster.logic.category.Category;
import de.deadlocker8.budgetmaster.logic.payment.NormalPayment;
import de.deadlocker8.budgetmaster.logic.payment.RepeatingPayment;
import de.deadlocker8.budgetmaster.logic.tag.Tag;
import de.deadlocker8.budgetmaster.logic.tag.TagMatch;

public class Database
{
	/*
	 * VERSIONS 
	 * 
	 * --> 1
	 * initial
	 * 
	 * --> 2
	 * added tags and tag matches (additional tables)
	 */	
	
	@SuppressWarnings("unused")
	private final String TYPE = "BUDGETMASTER_DATABASE";
	private final int VERSION = 2;
	private ArrayList<Category> categoriesX;
	private ArrayList<NormalPayment> normalPayments;
	private ArrayList<RepeatingPayment> repeatingPayments;
	private ArrayList<Tag> tags;
	private ArrayList<TagMatch> tagMatches;
	
	public Database()
	{
	    
	}
	
	public Database(ArrayList<Category> categories, ArrayList<NormalPayment> normalPayments, ArrayList<RepeatingPayment> repeatingPayments, ArrayList<Tag> tags, ArrayList<TagMatch> tagMatches)
	{	
		this.categoriesX = categories;
		this.normalPayments = normalPayments;
		this.repeatingPayments = repeatingPayments;
		this.tags = tags;
		this.tagMatches = tagMatches;
	}

	public ArrayList<Category> getCategories()
	{
	    return categoriesX;
	}
	
	public ArrayList<NormalPayment> getNormalPayments()
    {
        return normalPayments;
    }
	
	public ArrayList<RepeatingPayment> getRepeatingPayments()
    {
        return repeatingPayments;
    }

	public int getVERSION()
	{
		return VERSION;
	}

	public ArrayList<Tag> getTags()
	{
		return tags;
	}

	public ArrayList<TagMatch> getTagMatches()
	{
		return tagMatches;
	}

	@Override
	public String toString()
	{
		return "Database [VERSION=" + VERSION + ", categories=" + categoriesX + ", normalPayments=" + normalPayments + ", repeatingPayments=" + repeatingPayments + ", tags=" + tags + ", tagMatches=" + tagMatches + "]";
	}
}
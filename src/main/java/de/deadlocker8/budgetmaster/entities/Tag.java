package de.deadlocker8.budgetmaster.entities;

import com.google.gson.annotations.Expose;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Tag
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Expose
	private Integer ID;

	@NotNull
	@Size(min=1)
	@Column(unique=true)
	@Expose
	private String name;

	@ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
	private transient List<Payment> referringPayments;

	public Tag()
	{
	}

	public Tag(String name)
	{
		this.name = name;
	}

	public Integer getID()
	{
		return ID;
	}

	public void setID(Integer ID)
	{
		this.ID = ID;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<Payment> getReferringPayments()
	{
		return referringPayments;
	}

	@Override
	public String toString()
	{
		return "Tag{ID=" + ID +	", name='" + name + '}';
	}
}
package ca.mcgill.cs.comp303.capone.loaders.op.stubs;

public class JBill
{
	String home_chamber;
	boolean law;
	JTextFrenchEnglish name;
	String[] vote_urls;
	String text_url;
	String number;
	JTextFrenchEnglish short_title;
	String url;
	String[] otherSessionUrls;
	String session;
	Related related;
	String legisinfo_url;
	boolean private_member_bill;
	String introduced;
	int legisinfo_id;
	String sponsor_politician_ur;
	String sponsor_politician_membership_url;

	public static class Related
	{
		String bills_url;
	}

	public String getHome_chamber()
	{
		return home_chamber;
	}

	public boolean isLaw()
	{
		return law;
	}

	public JTextFrenchEnglish getName()
	{
		return name;
	}

	public String[] getVote_urls()
	{
		return vote_urls;
	}

	public String getText_url()
	{
		return text_url;
	}

	public String getNumber()
	{
		return number;
	}

	public JTextFrenchEnglish getShort_title()
	{
		return short_title;
	}

	public String getUrl()
	{
		return url;
	}

	public String[] getOtherSessionUrls()
	{
		return otherSessionUrls;
	}

	public String getSession()
	{
		return session;
	}

	public Related getRelated()
	{
		return related;
	}

	public String getLegisinfo_url()
	{
		return legisinfo_url;
	}

	public boolean isPrivate_member_bill()
	{
		return private_member_bill;
	}

	public String getIntroduced()
	{
		return introduced;
	}

	public int getLegisinfo_id()
	{
		return legisinfo_id;
	}

	public String getSponsor_politician_ur()
	{
		return sponsor_politician_ur;
	}

	public String getSponsor_politician_membership_url()
	{
		return sponsor_politician_membership_url;
	}

}

package ca.mcgill.cs.comp303.capone.loaders.op.stubs;

import java.util.List;

public class JMP
{
	String fax;
	List<Link> links;
	String image;
	Other_info other_info;
	Related related;
	String family_name;
	String name;
	String url;
	String gender;
	List<JMembership> memberships;
	String given_name;
	String voice;
	String email;

	public static class Link
	{
		String url;
		String note;
	}

	public static class Other_info
	{
		String[] wikipedia_id;
		String[] favourite_word;
		String[] anagram;
		String[] wordcloud;
		String[] parlinfo_id;
		String[] parl_id;
		String[] twitter;
		String[] twitter_id;
		String[] freebase_id;
		String[] constituency_offices;
		String[] alternate_name;
	}

	public static class Related
	{
		String speeches_url;
		String ballots_url;
		String sponsored_bills_url;

		public String getSpeeches_url()
		{
			return speeches_url;
		}

		public String getBallots_url()
		{
			return ballots_url;
		}

		public String getSponsored_bills_url()
		{
			return sponsored_bills_url;
		}
	}

	public String getFax()
	{
		return fax;
	}

	public List<Link> getLinks()
	{
		return links;
	}

	public String getImage()
	{
		return image;
	}

	public Related getRelated()
	{
		return related;
	}

	public String getFamily_name()
	{
		return family_name;
	}

	public String getName()
	{
		return name;
	}

	public String getUrl()
	{
		return url;
	}

	public String getGender()
	{
		return gender;
	}

	public List<JMembership> getMemberships()
	{
		return memberships;
	}

	public String getGiven_name()
	{
		return given_name;
	}

	public String getVoice()
	{
		return voice;
	}

	public String getEmail()
	{
		return email;
	}
}

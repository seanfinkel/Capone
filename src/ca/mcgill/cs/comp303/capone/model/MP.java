package ca.mcgill.cs.comp303.capone.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import ca.mcgill.cs.comp303.capone.model.stubs.Utilities;

/**
 * This class represents a Member of Parliament, the representative of the voters to the Parliament of Canada, the
 * federal legislative branch of Canada. [Reference: Wikipedia (http://en.wikipedia.org/wiki/Member_of_Parliament)]
 * 
 * This class directly maps to a single politician resource from the openparliament.ca API. For an example, check out
 * the data format of an MP: api.openparliament.ca/politicians/tom-lukiwski/
 * 
 * The minimum set of data to capture in this class is indicated through the getter methods.
 * 
 * This class should not be immutable. It will be a dynamic entity in the final application.
 */
public class MP
{
	private String aEmail;
	private String aFamilyName;
	private String aGivenName;
	private String aName;
	private String aPhoneNumber;
	private String aImageURL;
	private ArrayList<Membership> aMemberships = new ArrayList<Membership>();
	private ArrayList<Speech> aSpeeches = new ArrayList<Speech>();

	/**
	 * Constructs a new MP object.
	 * 
	 * @param pEmail
	 *            MP's email address
	 * @param pFamilyName
	 *            MP's last name
	 * @param pGivenName
	 *            MP's first name
	 * @param pName
	 *            MP's full name
	 * @param pPhoneNumber
	 *            MP's phone number
	 * @param pImageURL
	 *            MP's image URL
	 * @param pMemberships
	 *            MP's party memberships
	 */
	public MP(String pEmail, String pFamilyName, String pGivenName, String pName, String pPhoneNumber,
			String pImageURL, ArrayList<Membership> pMemberships)
	{
		aEmail = pEmail;
		aFamilyName = pFamilyName;
		aGivenName = pGivenName;
		aName = pName;
		aPhoneNumber = pPhoneNumber;
		aImageURL = pImageURL;
		aMemberships = pMemberships;
	}

	/**
	 * @return A primary key (unique identifier) for this object. We will use an MP's email as primary key.
	 */
	public String getPrimaryKey()
	{
		return aEmail;
	}

	/**
	 * @return The family name(s) of the MP
	 */
	public String getFamilyName()
	{
		return aFamilyName;
	}

	/**
	 * @return The given name(s) of the MP
	 */
	public String getGivenName()
	{
		return aGivenName;
	}

	/**
	 * @return The given and family name(s), separated by a white space.
	 */
	public String getName()
	{
		return aName;
	}

	/**
	 * @return The email address of the MP. This is used as the primary key.
	 */
	public String getEmail()
	{
		return aEmail;
	}

	/**
	 * @return The phone number of the MP
	 */
	public String getPhoneNumber()
	{
		return aPhoneNumber;
	}

	/**
	 * @return The image url of the MP
	 */
	public String getImageURL()
	{
		return aImageURL;
	}

	/**
	 * @return This MP's official RSS feed URL, or null if no ImageUrl exists on file.
	 * @precondition aImageURL != null
	 */
	public String getRSSFeedURI()
	{
		if (aImageURL == null) // return null if no Image url exists from data
		{
			return null;
		}
		Pattern p1 = Pattern.compile("/media/polpics/");
		Pattern p2 = Pattern.compile("(_\\d)*.jpg");
		String input = this.aImageURL;
		String firstSplit = p1.split(input)[1];
		String[] secondSplit = p2.split(firstSplit);
		return secondSplit[0];
	}

	/**
	 * @return The total number of memberships for this MP, including the current one.
	 */
	public int getNumberOfMemberships()
	{
		return aMemberships.size();
	}

	/**
	 * @return The number of distinct memberships of an MP
	 */
	public int getNumberOfDistinctmemberships()
	{
		Set<String> partyNames = new HashSet<String>();
		for (Membership membership : aMemberships)
		{
			partyNames.add(membership.getParty().getName());
		}
		return partyNames.size();
	}

	/**
	 * @return The total number of days the MP has served in Parliament
	 */
	public int calculateTimeInParliament()
	{
		int timeInParliament = 0;
		for (Membership membership : aMemberships)
		{
			try
			{
				timeInParliament += membership.calculateMembershipLength();
			}
			catch (ParseException e)
			{
				System.out.println("Parsing error when calculating membership length");
				e.printStackTrace();
			}
		}
		return timeInParliament;
	}

	/**
	 * @return The current MP membership. Null if no current membership is found.
	 */
	public Membership getCurrentMembership()
	{
		Membership currMemb = null;
		for (Membership memb : aMemberships)
		{
			if (memb.getEndDate() == null)
			{
				currMemb = memb;
			}
		}
		return currMemb;
	}

	/**
	 * @return The most recent membership (not necessarily current)
	 * @precondition aMemberships.size()>0
	 */
	public Membership getLatestMembership()
	{
		Collections.sort(aMemberships);
		return aMemberships.get(0);
	}

	/**
	 * @param pMembership
	 *            The membership to add to this MP
	 */
	public void addMembership(Membership pMembership)
	{
		aMemberships.add(pMembership);
	}

	/**
	 * @return A clone of the list of memberships;
	 */
	public ArrayList<Membership> getMembershipList()
	{
		ArrayList<Membership> list = new ArrayList<Membership>();
		for (Membership memb : aMemberships)
		{
			list.add(memb);
		}
		return list;
	}

	/**
	 * Adds a Speech object to the MP's speech array.
	 * 
	 * @param pSpeech
	 *            the speech to add
	 */
	public void loadSpeech(Speech pSpeech)
	{
		aSpeeches.add(pSpeech);

	}

	/**
	 * @return a list of speech object that are connect to this particular MP
	 */
	public ArrayList<Speech> returnSpeeches()
	{
		ArrayList<Speech> returnArray = new ArrayList<Speech>();
		// For proper encapsulation, we copy all the speeches into a new array.
		for (Speech i : aSpeeches)
		{
			returnArray.add(i);
		}
		return returnArray;
	}

	/**
	 * Verifies if an MP has changed contents, except speeches.
	 * 
	 * @param pNewMP
	 *            The new version of the MP
	 * @param pOldMP
	 *            The old version of the MP
	 * @return True if newMP and oldMP have different contents
	 */
	public static boolean mpChanged(MP pNewMP, MP pOldMP)
	{
		boolean modified = false;
		if (!Utilities.stringsAreEqual(pNewMP.getFamilyName(), pOldMP.getFamilyName()))
		{
			modified = true;
		}
		if (!Utilities.stringsAreEqual(pNewMP.getGivenName(), pOldMP.getGivenName()))
		{
			modified = true;
		}
		if (!Utilities.stringsAreEqual(pNewMP.getName(), pOldMP.getName()))
		{
			modified = true;
		}
		if (!Utilities.stringsAreEqual(pNewMP.getPhoneNumber(), pOldMP.getPhoneNumber()))
		{
			modified = true;
		}
		if (!Utilities.stringsAreEqual(pNewMP.getImageURL(), pOldMP.getImageURL()))
		{
			modified = true;
		}

		// The membership is seen as changed if the latest membership start and end dates have changed
		if (!Membership.isSamePeriod(pNewMP.getLatestMembership(), pOldMP.getLatestMembership()))
		{
			modified = true;
		}

		return modified;
	}

	/*
	 * Two MPs objects are equals if they represent the same physical MP.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object pMP)
	{
		if (pMP instanceof MP)
		{
			return this.getPrimaryKey().equals(((MP) pMP).getPrimaryKey());
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		// use the RSS URI as the hashcode for functions
		// That way, two equal MPs will have the same hashcode.
		String hashCode = this.getRSSFeedURI();
		return Integer.parseInt(hashCode);
	}

}

package ca.mcgill.cs.comp303.capone.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.mcgill.cs.comp303.capone.model.stubs.Utilities;

/**
 * This class maps to a single politician membership resource from the openparliament.ca API. See an example of the data
 * format from this example: api.openparliament.ca/politicians/memberships/1534/ Memberships should be naturally
 * sortable in decreasing chronological order. Not all data must be captured by this class. The minimum set is
 * represented by the methods. The class should be immutable.
 * 
 * @see http://api.openparliament.ca/politicians/memberships/
 */
public class Membership implements Comparable<Membership>
{

	private final Party aParty;
	private final Riding aRiding;
	private final String aStartDate;
	private final String aEndDate;

	/**
	 * @param pParty
	 *            The party that corresponds to this membership
	 * @param pRiding
	 *            The riding that corresponds to this membership
	 * @param pStartDate
	 *            The start date of this membership
	 * @param pEndDate
	 *            The end date of this membership
	 * @precondition if (pEndDate!=null) then pStartDate < pEndDate
	 */
	public Membership(Party pParty, Riding pRiding, String pStartDate, String pEndDate)
	{
		aParty = pParty;
		aRiding = pRiding;
		aStartDate = pStartDate;
		aEndDate = pEndDate;
	}

	/**
	 * @return the party
	 */
	public Party getParty()
	{
		return aParty;
	}

	/**
	 * @return the riding
	 */
	public Riding getRiding()
	{
		return aRiding;
	}

	/**
	 * @return the start date
	 */
	public String getStartDate()
	{
		return aStartDate;
	}

	/**
	 * 
	 * @return If this is a past membership, it returns the end date of that membership. If this is a current
	 *         membership, return null;
	 */
	public String getEndDate()
	{
		return aEndDate;
	}

	/**
	 * @param pM1
	 *            The first membership to be compared
	 * @param pM2
	 *            The second membership to be compared
	 * @return True if the end and start dates of both memberships are the same
	 * @precondition pM1!=null, pM2!=null
	 */
	public static boolean isSamePeriod(Membership pM1, Membership pM2)
	{
		if (Utilities.stringsAreEqual(pM1.getStartDate(), pM2.getStartDate()) && Utilities.stringsAreEqual(pM1.getEndDate(), pM2.getEndDate()))
		{
			return true;
		}

		return false;
	}

	@Override
	/**
	 * Sorts by the End Dates in descending order
	 * @precondition pObj!=null
	 */
	public int compareTo(Membership pObj)
	{
		// If this object's date is a current date (null), return -1
		if (aEndDate == null)
		{
			return -1;
		}
		// If the other object's date is a current date (null), return 1
		if (pObj.getEndDate() == null)
		{
			return 1;
		}

		// Reverse the value of the comparison in order to provide descending order
		return 0 - aEndDate.compareTo(pObj.getEndDate());
	}

	/**
	 * @return the length of membership in days.
	 * @throws ParseException
	 *             if it cannot parse the date properly
	 */
	public int calculateMembershipLength() throws ParseException
	{
		final int lMILLISECONDSinDAYS = 86400000;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = df.parse(aStartDate);
		Date endDate;
		// If it is the current membership, then set the end date to the current date
		if (aEndDate == null)
		{
			endDate = new Date();
		}
		else
		{
			endDate = df.parse(aEndDate);
		}

		long timeDifferenceMills = endDate.getTime() - startDate.getTime();
		float timeDifferenceDays = timeDifferenceMills / lMILLISECONDSinDAYS;
		return (int) timeDifferenceDays;
	}
}

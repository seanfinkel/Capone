package ca.mcgill.cs.comp303.capone.model;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Observable;

/**
 * An object representing a graph of data about the Canadian parliament, including MPs, speeches, etc.
 */
public final class Parliament extends Observable
{
	// One list of parties and one for ridings used to implement the flyweight design pattern.
	private ArrayList<Party> aParties = new ArrayList<Party>();
	private ArrayList<Riding> aRidings = new ArrayList<Riding>();
	private Hashtable<String, MP> aMPs = new Hashtable<String, MP>();

	/**
	 * Returns the MP with this key, or null if no information is available. Note: the MP returned is mutable.
	 * 
	 * @param pKey
	 *            The key in the hashtable
	 * @return the MP associated with this key
	 */
	public MP getMP(String pKey)
	{
		return aMPs.get(pKey);
	}

	/**
	 * @param pMP
	 *            The MP to add to the Parliament
	 */
	public void addMP(MP pMP)
	{
		if (pMP.getPrimaryKey() != null) // if the MP object has a key, add it to Parliament
		{
			aMPs.put(pMP.getPrimaryKey(), pMP);
			setChanged();
		}
	}

	/**
	 * @param pKey
	 *            The key of the MP to remove from parliament
	 * @precondition pKey is in parliament
	 */
	public void deleteMP(String pKey)
	{
		if (pKey != null)
		{
			aMPs.remove(pKey);
			setChanged();
		}
	}

	/**
	 * @return An ArrayList containing all the MPs in Parliament
	 */
	public ArrayList<MP> getMPList()
	{
		ArrayList<MP> returnArray = new ArrayList<MP>();
		for (MP i : aMPs.values())
		{
			returnArray.add(i);
		}
		return returnArray;
	}

	/**
	 * Checks if an MP is still in parliament.
	 * 
	 * @param pKey
	 *            The primary key of the MP that has to be checked
	 * @return True if the given MP is still in parliament
	 */
	public boolean checkOnMP(String pKey)
	{
		// If the current membership is different than null, then the MP is still in parliament
		if (getMP(pKey) != null && getMP(pKey).getCurrentMembership() != null)
		{

			return true;
		}
		return false;
	}

	/**
	 * @param pParty
	 *            the Party we wish to add to Parliament
	 * @return reference to the party we just added
	 */
	public Party addParty(Party pParty)
	{
		aParties.add(pParty);
		return pParty;
	}

	/**
	 * @param pPartyName
	 *            the name of the party we are looking for
	 * @return reference to party if it already exists; else null
	 */
	public Party findParty(String pPartyName)
	{
		for (Party i : aParties)
		{
			if (i.getName().equals(pPartyName))
			{
				return i;
			}
		}
		return null;
	}

	/**
	 * @param pRiding
	 *            the riding we wish to add
	 * @return reference to the riding we just added
	 */
	public Riding addRiding(Riding pRiding)
	{
		aRidings.add(pRiding);
		return pRiding;
	}

	/**
	 * @param pID
	 *            the ID for the riding we are looking for
	 * @return reference to the riding if it is found; null otherwise
	 */
	public Riding findRiding(int pID)
	{
		for (Riding i : aRidings)
		{
			if (i.getId() == pID)
			{
				return i;
			}
		}
		return null;
	}

	/**
	 * @return list of all parties in parliament
	 */
	public ArrayList<Party> getParties()
	{
		ArrayList<Party> returnArray = new ArrayList<Party>();
		for (int i = 0; i < aParties.size(); i++)
		{
			returnArray.add(aParties.get(i));
		}
		return returnArray;
	}

	/**
	 * Convert's MP's name to email.
	 * 
	 * @param pMPName
	 *            MP's name
	 * @return MP's email
	 */
	public String getMPKey(String pMPName)
	{
		for (MP mp : aMPs.values())
		{
			if (mp.getName().equals(pMPName))
			{
				return mp.getEmail();
			}
		}
		return null;
	}

	/**
	 * "Empties" out the parliament info, while keeping its observers.
	 */
	public void resetParliament()
	{
		aParties = new ArrayList<Party>();
		aRidings = new ArrayList<Riding>();
		aMPs = new Hashtable<String, MP>();
	}
}

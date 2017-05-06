package ca.mcgill.cs.comp303.capone.model;

/**
 * This represents a political party in Canada. Immutable. There should only even one instance of each unique Party
 * object within a given JVM.
 */

public class Party
{
	private final String aName;
	private final String aShortName;

	/**
	 * @param pName
	 *            name from constructor
	 * @param pShortName
	 *            short name from constructor
	 */
	public Party(String pName, String pShortName)
	{
		aName = pName;
		aShortName = pShortName;
	}

	/**
	 * @return party name For example, "Conservative Party of Canada".
	 */
	public String getName()
	{
		return aName;
	}

	/**
	 * @return party short name For example, "Conservatives".
	 */
	public String getShortName()
	{
		return aShortName;
	}

	@Override
	public String toString()
	{
		return aName;
	}
}

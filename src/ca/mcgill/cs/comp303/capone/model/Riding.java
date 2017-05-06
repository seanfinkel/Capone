package ca.mcgill.cs.comp303.capone.model;

/**
 * This class represents an electoral district in Canada, a geographical constituency upon which a Member of Parliament
 * to the Canadian House of Commons represents. [Reference: Wikipedia
 * (http://en.wikipedia.org/wiki/Electoral_district_%28Canada%29)]
 * 
 * Immutable. There should only even one instance of each unique Riding object within a given JVM.
 */
public final class Riding
{

	private final int aId;
	private final String aName;
	private final String aProvince;

	/**
	 * Constructs a new Riding object.
	 * 
	 * @param pId
	 *            Riding id
	 * @param pName
	 *            The Riding name
	 * @param pProvince
	 *            The province of the Riding
	 */
	public Riding(int pId, String pName, String pProvince)
	{
		aId = pId;
		aName = pName;
		aProvince = pProvince;
	}

	/**
	 * The unique ID for this riding, as obtained from OpenParliament. E.g., 4700.
	 * 
	 * @return the Id
	 */
	public int getId()
	{
		return aId;
	}

	/**
	 * The official name of the riding, e.g., "Regina\u2014Lumsden\u2014Lake Centre".
	 * 
	 * @return the Name
	 */
	public String getName()
	{
		return aName;
	}

	/**
	 * The province code, e.g, SK.
	 * 
	 * @return the Province
	 */
	public String getProvince()
	{
		return aProvince;
	}

}

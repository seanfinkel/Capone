package ca.mcgill.cs.comp303.capone.model.stubs;

/**
 * This class contains utility methods that are not related to the model.
 */
public final class Utilities
{
	private Utilities()
	{	
	}
	
	/**
	 * Compares two string, with the possibility of them being null.
	 * 
	 * @param pString1
	 *            The first string to be compared
	 * @param pString2
	 *            The second string to be compared
	 * @return True if the two strings are the same
	 */
	public static boolean stringsAreEqual(String pString1, String pString2)
	{
		if (pString1 == null)
		{
			if (pString2 == null)
			{
				return true;
			}
		}
		else
		{
			if (pString1.equals(pString2))
			{
				return true;
			}
		}
		return false;
	}

}

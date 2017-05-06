package ca.mcgill.cs.comp303.capone.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Capability to import user profile from a binary file.
 */
public final class BinaryImport
{
	private static Logger logger = Logger.getLogger("ca.mcgill.cs.comp303.capone.user.BinaryExport");

	private BinaryImport()
	{
		// not Called
	}

	/**
	 * @param pPath
	 *            The path of the file containing a user profile
	 * @return The User Profile read from the file
	 * @throws IOException
	 *             When the file specified by pPath is not specified
	 */
	public static UserProfile importUserProfile(String pPath) throws IOException
	{
		ObjectInputStream userProfileIn = null;
		UserProfile userProfile = null;
		userProfileIn = new ObjectInputStream(new FileInputStream(new File(pPath)));
		try
		{
			userProfile = (UserProfile) userProfileIn.readObject();
			return userProfile;
		}
		catch (ClassNotFoundException e)
		{
			logger.log(Level.WARNING, "Could not case userProfile file into proper class!");
			throw new IOException();
		}
		finally
		{
			userProfileIn.close();
		}
	}

}

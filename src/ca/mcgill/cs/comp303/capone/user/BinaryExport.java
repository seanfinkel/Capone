package ca.mcgill.cs.comp303.capone.user;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * exports user profile in binary format.
 */
public class BinaryExport implements ExportUserProfile
{
	private static Logger logger = Logger.getLogger("ca.mcgill.cs.comp303.capone.user.BinaryExport");

	@SuppressWarnings("null")
	@Override
	public void export(UserProfile pUserProfile, String pPath)
	{
		ObjectOutputStream userProfileOut = null;
		try
		{
			userProfileOut = new ObjectOutputStream(new FileOutputStream(new File(pPath)));
			userProfileOut.writeObject(pUserProfile);

		}
		catch (IOException e)
		{
			logger.log(Level.WARNING, "I/O Exception!", e);
		}
		finally
		{
			try
			{
				userProfileOut.close();
			}
			catch (IOException e)
			{
				logger.log(Level.WARNING, "I/O Exception!", e);
			}
		}
	}

}

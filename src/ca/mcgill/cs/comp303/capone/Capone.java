package ca.mcgill.cs.comp303.capone;

import ca.mcgill.cs.comp303.capone.model.Parliament; 
import ca.mcgill.cs.comp303.capone.user.Properties;
import ca.mcgill.cs.comp303.capone.user.UserProfile;

/**
 * There scan only be one instance of this class in any given JVM. Uses the singleton design pattern.
 */
public final class Capone
{
	private static final Capone INSTANCE = new Capone();
	private Parliament aParliament = new Parliament();
	private UserProfile aUser = new UserProfile();
	private Properties aProperties = new Properties();

	/**
	 * Support for the singleton design pattern.
	 * 
	 * @return The model instance.
	 */
	public static Capone getInstance()
	{
		return INSTANCE;
	}

	/**
	 * @return The Parliament object
	 */
	public Parliament getParliament()
	{
		aParliament.addObserver(aUser);
		return aParliament;
	}

	/**
	 * @return The User Profile
	 */
	public UserProfile getUserProfile()
	{
		return aUser;
	}

	/**
	 * @return properties file.
	 */
	public Properties getProperties()
	{
		return aProperties;
	}
}

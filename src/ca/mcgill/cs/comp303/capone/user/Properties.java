package ca.mcgill.cs.comp303.capone.user;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handles reading and writing to/from the properties.txt file.
 * 
 */
public class Properties
{
	private String aDataLocation;
	private Boolean aAutoLoad;
	private String aUserProfileLocation;
	private String aLastUsedRecommender;
	private String aJsonPath;

	/**
	 * create a new properties object.
	 */
	public Properties()
	{
		// try to load the properties from file
		try
		{
			loadPropertiesfromFile();
		}
		// if the properties file doesn't exist, write a new one with default settings
		catch (IOException e)
		{
			setDataLocation("null");
			setAutoLoad(false);
			setUserProfileLocation("null");
			setLastUsedRecommender("Content Based");
			setJsonPath("null");
			savePropertiestoFile();
		}
	}

	@SuppressWarnings("null")
	private void savePropertiestoFile()
	{
		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter("properties.txt");
			writer.println(aDataLocation);
			writer.println(aAutoLoad);
			writer.println(aUserProfileLocation);
			writer.println(aLastUsedRecommender);
			writer.println(aJsonPath);
		}
		catch (FileNotFoundException e)
		{
			// TODO log file exception
		}
		finally
		{
			writer.close();
		}
	}

	/**
	 * loads properties from file.
	 * 
	 * @throws IOException
	 */
	private void loadPropertiesfromFile() throws IOException
	{
		FileReader reader = null;

		reader = new FileReader("properties.txt");
		BufferedReader br = new BufferedReader(reader);
		aDataLocation = br.readLine();
		String tempAutoLoad = br.readLine();
		if (tempAutoLoad.equals("true"))
		{
			aAutoLoad = true;
		}
		else
		{
			aAutoLoad = false;
		}
		aUserProfileLocation = br.readLine();
		aLastUsedRecommender = br.readLine();
		aJsonPath = br.readLine();
		br.close();
		reader.close();

	}

	/**
	 * @return data location.
	 */
	public String getDataLocation()
	{
		return aDataLocation;
	}

	/**
	 * @param pDataLocation
	 *            the location where the data is saved.
	 */
	public void setDataLocation(String pDataLocation)
	{
		this.aDataLocation = pDataLocation;
		savePropertiestoFile();
	}

	/**
	 * @return true if autoload is selected. False otherwise.
	 */
	public Boolean getAutoLoad()
	{
		return aAutoLoad;
	}

	/**
	 * @param pAutoLoad
	 *            true if autoload is selected. False otherwise.
	 */
	public void setAutoLoad(Boolean pAutoLoad)
	{
		this.aAutoLoad = pAutoLoad;
		savePropertiestoFile();
	}

	/**
	 * @return location of user profile file.
	 */
	public String getUserProfileLocation()
	{
		return aUserProfileLocation;
	}

	/**
	 * @param pUserProfileLocation
	 *            location to save user profile file.
	 */
	public void setUserProfileLocation(String pUserProfileLocation)
	{
		this.aUserProfileLocation = pUserProfileLocation;
		savePropertiestoFile();
	}

	/**
	 * @return the last used recommender
	 */
	public String getLastUsedRecommender()
	{
		return aLastUsedRecommender;
	}

	/**
	 * @param pLastUsedRecommender
	 *            the last used recommender.
	 */
	public void setLastUsedRecommender(String pLastUsedRecommender)
	{
		this.aLastUsedRecommender = pLastUsedRecommender;
		savePropertiestoFile();
	}

	/**
	 * @return the last path to which the user profile has been exported as Json
	 */
	public String getJsonPath()
	{
		return aJsonPath;
	}

	/**
	 * @param pJsonPath
	 *            the path where to save the user profile as Json
	 */
	public void setJsonPath(String pJsonPath)
	{
		this.aJsonPath = pJsonPath;
		savePropertiestoFile();
	}
}

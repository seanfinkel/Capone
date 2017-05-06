package ca.mcgill.cs.comp303.capone.loaders;

import java.io.FileNotFoundException;
import java.io.IOException;

import ca.mcgill.cs.comp303.capone.model.Parliament;

/**
 * Services for creating model elements.
 */
public interface ParliamentLoader
{
	/**
	 * Loads an MP record into the Parliament object.
	 * 
	 * @param pRelativeLocation An indicator of where to find the MP's record 
	 * 							relative to the context of the concrete loader.
	 * @param pParliament The object to load the data into.
	 * @return The primary key of the MP just loaded.
	 * @throws IOException 
	 */
	String loadMP(String pRelativeLocation, Parliament pParliament) throws IOException;

	/**
	 * Loads recent events related to this MP into the Parliament object. 
	 * For M1 these events are all the speeches.
	 * 
	 * @param pMPKey The primary key identifier of the MP
	 * @param pParliament The object to load the data into.
	 * @throws FileNotFoundException 
	 */
	void loadRecentEvents(String pMPKey, Parliament pParliament) throws FileNotFoundException;

}

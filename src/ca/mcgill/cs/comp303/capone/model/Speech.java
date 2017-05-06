package ca.mcgill.cs.comp303.capone.model;

import java.io.Serializable;
import java.util.Date;

/**
 * This class maps to a single speech (that is part of a house aDebate) resource from the openparliament.ca API.
 * 
 * Here is an example of the data format: http://api.openparliament.ca/debates/2013/6/18/tom-lukiwski-1/
 * 
 * Immutable.
 * 
 */
public class Speech implements Comparable<Speech>, Serializable
{
	private static final long serialVersionUID = 1L;
	private final String aH1;
	private final String aH2;
	private final String aContent;
	private final String aAuthor;
	private Date aTime;
	private boolean aRead;

	/**
	 * @param pH1
	 *            h1 from speech constructor
	 * @param pH2
	 *            h2 from speech constructor
	 * @param pContent
	 *            content from speech constructor
	 * @param pTime
	 *            time from speech constructor
	 * @param pAuthor
	 *            the key of the politician who is the author of the speech
	 */
	public Speech(String pH1, String pH2, String pContent, Date pTime, String pAuthor)
	{
		aH1 = pH1;
		aH2 = pH2;
		aContent = pContent.replaceAll("\\<[^>]*>", "");
		aTime = pTime;
		aAuthor = pAuthor;
		aRead = false;
	}
	
	/**
	 * @return The read status.
	 */
	public boolean getReadStatus()
	{
		return aRead;
	}
	
	/**
	 * @param pBoolean The value to set the read status to.
	 */
	public void setReadStatus(boolean pBoolean)
	{
		aRead = pBoolean;
	}

	/**
	 * @return The main label for this speech. e.g., "Routine Proceedings"
	 */
	public String getHeader1()
	{
		return aH1;
	}

	/**
	 * @return The secondary label for this speech. e.g., "Government Response to Petitions"
	 * @precondition (pJSpeech.getH2()).getEn() != null
	 */
	public String getHeader2()
	{
		return aH2;
	}

	/**
	 * @return The content of the speech.
	 */
	public String getContent()
	{
		// remove tags from content
		return aContent;
	}

	/**
	 * @return The time at which the speech was given.
	 */
	public Date getTime()
	{
		return (Date) aTime.clone();
	}

	/**
	 * @return The author of the speech.
	 */
	public String getAuthor()
	{
		return aAuthor;
	}

	@Override
	/**
	 * Compares to speech object by their time in reverse order.
	 * @precondition pObj!=null
	 */
	public int compareTo(Speech pObj)
	{
		// Reverse the value of the comparison in order to provide descending order
		return 0 - aTime.compareTo(pObj.getTime());
	}
}

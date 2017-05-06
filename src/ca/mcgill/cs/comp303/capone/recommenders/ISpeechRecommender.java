package ca.mcgill.cs.comp303.capone.recommenders;

import java.util.ArrayList;

import ca.mcgill.cs.comp303.capone.model.Speech;

/**
 * Speech recommendation interface implemented using the strategy design pattern.
 * 
 */
public interface ISpeechRecommender
{

	/**
	 * @param pMPs
	 *            the list of MPs a user is interested in.
	 * @param pKeyWords
	 *            the keywords the user is interested in.
	 * @return an ArrayList of the recommended speeches in descending order.
	 */
	ArrayList<Speech> recommendSpeeches(ArrayList<String> pMPs, ArrayList<String> pKeyWords);
}

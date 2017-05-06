package ca.mcgill.cs.comp303.capone.recommenders;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.mcgill.cs.comp303.capone.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Speech;

/**
 * Recommends speeches made by MPs in UserProfile, based on keywords.
 */
public class ContentBasedRecommender extends AbstractRecommender
{

	private static final Logger LOGGER = Logger.getLogger(ContentBasedRecommender.class.getName());

	/**
	 * @precondition Capone.getInstance().getParliament().getMPList()!=null
	 */

	/**
	 * @param pMPsKeys
	 *            the list of MPs we're currently interested in.
	 * @param pKeyWords
	 *            the keywords the user is interested in.
	 * @return an ArrayList of the recommended speeches in descending order, with MPs that are chosen near the top.
	 * @precondition pMPsKeys!=null
	 * @precondition pKeyWords!=null
	 */
	public ArrayList<Speech> recommendSpeeches(ArrayList<String> pMPsKeys, ArrayList<String> pKeyWords)
	{
		ArrayList<MP> pMPs = convertToMPList(pMPsKeys);

		// Add all the speeches by the selected MPs to our result array
		ArrayList<Speech> result = rateMPsSubsection(pMPs, pKeyWords, true);

		// Then add all the speeches by the not selected MPs
		ArrayList<MP> notSelectedMPs = findNotSelectedMPs(pMPs);
		result.addAll(rateMPsSubsection(notSelectedMPs, pKeyWords, false));
		LOGGER.log(Level.INFO, "Generated content-based recommedations!");
		return result;
	}

	private static ArrayList<MP> findNotSelectedMPs(ArrayList<MP> pMPs)
	{
		ArrayList<MP> allMPs = Capone.getInstance().getParliament().getMPList();
		ArrayList<MP> notSelectedMPs = new ArrayList<MP>();
		for (MP mp : allMPs)
		{
			if (!pMPs.contains(mp))
			{
				notSelectedMPs.add(mp);
			}
		}
		return notSelectedMPs;
	}

}
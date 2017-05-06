package ca.mcgill.cs.comp303.capone.recommenders;

import java.util.ArrayList;
import java.util.Collections;

import ca.mcgill.cs.comp303.capone.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Speech;
import ca.mcgill.cs.comp303.capone.recommenders.stubs.RatedSpeech;

/**
 * Implements the basic ranking mechanism for rating speeches.
 * Ranks speeches based on "breadth" - the proportion of keyWords that appear in the speech.
 * and "depth" - the proportion of speech that is made of keywords.
 */
public abstract class AbstractRecommender implements ISpeechRecommender
{
	private static final int BREADTH_MULTIPLIER = 2;
	private static final int H1_MULTIPLIER = 3;
	private static final int H2_MULTIPLIER = 2;
	private static final double SELECTED_MP_MULTIPLIER = 2;
	

	/**
	 * @param pMPs
	 *            the list of MPs a user is interested in.
	 * @param pKeyWords
	 *            the keywords the user is interested in.
	 * @return an ArrayList of the recommended speeches in descending order.
	 */
	public abstract ArrayList<Speech> recommendSpeeches(ArrayList<String> pMPs, ArrayList<String> pKeyWords);
	
	/**
	 * @param pMPs The "subsection" of MPs whose speeches we wish to rate
	 * @param pKeyWords the keywords the user is interested in
	 * @param pSelectedMPs true is the "subsection" is from the userprofile; false otherwise.
	 * @return an list of the speeches, ordered from most likely to be interesting to least likey.
	 */
	protected static ArrayList<Speech> rateMPsSubsection(ArrayList<MP> pMPs, ArrayList<String> pKeyWords,
			boolean pSelectedMPs)
	{
		ArrayList<RatedSpeech> ratedSpeeches = new ArrayList<RatedSpeech>();
		for (MP mp : pMPs)
		{
			for (Speech speech : mp.returnSpeeches())
			{
				ratedSpeeches.add(new RatedSpeech(speech, speechScore(speech, pKeyWords, pSelectedMPs)));
			}
		}
		Collections.sort(ratedSpeeches);
		return convertToSpeechArray(ratedSpeeches);
	}
	
	/**
	 * @param pMPs The "subsection" of MPs whose speeches we wish to rate
	 * @param pKeyWords the keywords the user is interested in
	 * @param pSelectedMPs true is the "subsection" is from the userprofile; false otherwise.
	 * @return an list of the ratedSpeeches, ordered from most likely to be interesting to least likely.
	 */
	protected static ArrayList<RatedSpeech> ratedSpeechesForMPsSubsection(ArrayList<MP> pMPs, ArrayList<String> pKeyWords,
			boolean pSelectedMPs)
	{
		ArrayList<RatedSpeech> ratedSpeeches = new ArrayList<RatedSpeech>();
		for (MP mp : pMPs)
		{
			for (Speech speech : mp.returnSpeeches())
			{
				ratedSpeeches.add(new RatedSpeech(speech, speechScore(speech, pKeyWords, pSelectedMPs)));
			}
		}
		Collections.sort(ratedSpeeches);
		return ratedSpeeches;
	}
	
	
	/**
	 * @param pMPsKeys the list of MP keys
	 * @return a list filled with MPs matching those keys.
	 */
	protected static ArrayList<MP> convertToMPList(ArrayList<String> pMPsKeys)
	{
		ArrayList<MP> pMPs = new ArrayList<MP>();
		for (String key : pMPsKeys)
		{
			pMPs.add(Capone.getInstance().getParliament().getMP(key));
		}
		return pMPs;
	}

	// ranks the speeches given by the MPs in pMPs.
	private static ArrayList<Speech> convertToSpeechArray(ArrayList<RatedSpeech> pRatedSpeeches)
	{
		ArrayList<Speech> speechResultList = new ArrayList<Speech>();
		for (RatedSpeech rs : pRatedSpeeches)
		{
			speechResultList.add(rs.getSpeech());
		}
		return speechResultList;
	}

	// returns an int reflecting how closely a speech matches the words in the pKeyWords list.
	private static double speechScore(Speech pSpeech, ArrayList<String> pKeyWords, boolean pSelectedMP)
	{
		double h1Breadth = keyWordBreadth(pSpeech.getHeader1(), pKeyWords);
		double h1Count = keyWordCount(pSpeech.getHeader1(), pKeyWords);
		double h2Breadth = keyWordBreadth(pSpeech.getHeader2(), pKeyWords);
		double h2Count = keyWordCount(pSpeech.getHeader2(), pKeyWords);
		double contentBreadth = keyWordBreadth(pSpeech.getContent(), pKeyWords);
		double contentCount = keyWordCount(pSpeech.getContent(), pKeyWords);
		double h1Score = (BREADTH_MULTIPLIER * h1Breadth) + h1Count;
		double h2Score = (BREADTH_MULTIPLIER * h2Breadth) + h2Count;
		double contentScore = (BREADTH_MULTIPLIER * contentBreadth) + contentCount;

		double speechScore = (H1_MULTIPLIER * h1Score) + (H2_MULTIPLIER * h2Score) + contentScore;
		if (pSelectedMP)
		{
			speechScore = speechScore * SELECTED_MP_MULTIPLIER; // add extra points if the speech is given by a
																// highlighted MP.
		}
		return speechScore;

	}

	// a double reflecting the proportion of keyWords that appear in the content
	private static double keyWordBreadth(String pContent, ArrayList<String> pKeyWords)
	{
		int count = 0;
		for (String keyWord : pKeyWords)
		{
			String[] s = pContent.split("(?i)" + keyWord);
			if (s.length != 1)
			{
				count++;
			}
		}
		return (double) count / (double) pKeyWords.size();
	}

	// a double reflecting the proportion of speech that is made of keywords
	private static double keyWordCount(String pString, ArrayList<String> pKeyWords)
	{
		int count = 0;
		for (String keyWord : pKeyWords)
		{
			String[] s = pString.split("(?i)" + keyWord);
			if (s.length != 0)
			{
				count = count + (s.length - 1);
			}
			else
			// if the entire string was just the keyword, s.length will be = 0
			{
				count++;
			}
		}
		return (double) count / (double) pKeyWords.size();
	}
}

package ca.mcgill.cs.comp303.capone.recommenders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.mcgill.cs.comp303.capone.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Speech;
import ca.mcgill.cs.comp303.capone.recommenders.stubs.MPVertex;
import ca.mcgill.cs.comp303.capone.recommenders.stubs.RatedMP;
import ca.mcgill.cs.comp303.capone.recommenders.stubs.RatedSpeech;

/**
 * Recommends speeches based on similarity of the MP giving it to the MP the user has shown interest in. There are three
 * factors taken into consideration when labeling an MP "similar" to another: whether they are in the same party,
 * whether they are in the same province, and whether they tend to talk about similar subjects.
 * 
 * The tendency to talk about similar subjects is calculated by creating an mpVertex for every mp, which maps each to
 * the proportion of that mp's headers that is that word. To compare the similarity between 2 MPs, we calculate the the
 * difference between two mpVertex s and calculate the size of the created the vertex. The smaller the difference, the
 * more similar two MPs are.
 */
public class SimilarityBasedRecommender extends AbstractRecommender
{
	static final double SAME_PARTY_MULTIPLIER = 0.8;
	static final double SAME_PROVINCE_MULTIPLIER = 0.9;
	static final double SPEECH_THRESHOLD_MULTIPLIER = .75;

	private static final Logger LOGGER = Logger.getLogger(SimilarityBasedRecommender.class.getName());

	private static HashMap<String, Integer> aWordIndex;

	/**
	 * creates a new SimilarityBaedRecommender instance by initializing the word index hashmap.
	 * 
	 * @precondition Capone.getInstance().getParliament().getMPList()!=null
	 */
	public SimilarityBasedRecommender()
	{
		aWordIndex = generateWordIndex(); // when a new SimilarityBasedRecommender is created, we refresh the wordIndex.
	}

	/**
	 * @param pMPsKeys
	 *            the list of MPs keys a user is interested in.
	 * @param pKeyWords
	 *            the keywords the user is interested in.
	 * @return an ArrayList of the recommended speeches in descending order.
	 * @precondition pMPsKeys!=null
	 * @precondition pKeyWords!=null
	 */
	public ArrayList<Speech> recommendSpeeches(ArrayList<String> pMPsKeys, ArrayList<String> pKeyWords)
	{
		ArrayList<MP> pMPs = super.convertToMPList(pMPsKeys);

		// We have 3 buckets - they will hold 2 "classes" of speeches.
		ArrayList<Speech> relevantSpeeches = new ArrayList<Speech>();
		ArrayList<Speech> notRelevantSpeeches = new ArrayList<Speech>();

		// First add the speeches given by the selected MPs to our temporary list.
		ArrayList<RatedSpeech> classlessRatedSpeeches = ratedSpeechesForMPsSubsection(pMPs, pKeyWords, true);

		// relevantSpeechThreshold will decide what is the minimum score required to enter the relevantSpeeches "class"
		// this value is picked at runtime since the keywords chosen can make the range of speech score vary greatly.
		// then add all the other MPs' speeches, sorted by their similarity to our selected MPs
		classlessRatedSpeeches.addAll(rankedSpeechesbySimilarMPs(pMPs, pKeyWords));

		// We will then sort all the speeches into the 2 "classes"
		for (RatedSpeech sp : classlessRatedSpeeches)
		{
			if (sp.getScore() > 0)
			{
				relevantSpeeches.add(sp.getSpeech());
			}
			else
			{
				notRelevantSpeeches.add(sp.getSpeech());

			}
		}

		// and combined all the classes into one list, which we return.
		relevantSpeeches.addAll(notRelevantSpeeches);
		LOGGER.log(Level.INFO, "Generated similarity-based recommedations!");
		return relevantSpeeches;
	}

	// Returns a list of RatedSpeeches, which are sorted by the similarity of the MP who gave them to the MPs in
	// our SelectedMPs list. Speeches given by those SelectedMPs will not be included in the returned list.
	private static ArrayList<RatedSpeech> rankedSpeechesbySimilarMPs(ArrayList<MP> pSelectedMPs,
			ArrayList<String> pKeyWords)
	{
		ArrayList<MP> tempSingleMPList = new ArrayList<MP>();
		ArrayList<RatedSpeech> result = new ArrayList<RatedSpeech>();
		for (MP mp : rankSimilarMPs(pSelectedMPs))
		{
			tempSingleMPList.add(mp);
			for (RatedSpeech sp : ratedSpeechesForMPsSubsection(tempSingleMPList, pKeyWords, true))
			{
				result.add(sp);
			}
			tempSingleMPList.remove(mp);
		}
		return result;
	}

	// returns an MP list containing all the not-selected MPs, sorted by how similar they are to MPs in
	// the selectedMPs list.
	// The list will not contained MPs in the original pSelectedMPs list.
	private static ArrayList<MP> rankSimilarMPs(ArrayList<MP> pSelectedMPs)

	{
		// HashMap is used to provide an easy way to keep only the highest score for each MP.
		HashMap<String, Double> ratedMPsMap = new HashMap<String, Double>();
		HashMap<String, Double> tempRatedMPs;
		ArrayList<RatedMP> sortedMPsArray = new ArrayList<RatedMP>(); // will be used to sort the MPs by their rating.
		for (MP mp : pSelectedMPs)
		{
			tempRatedMPs = getSimilarMPs(mp);
			for (String key : tempRatedMPs.keySet())
			{
				// if this is the first MP we look at, put all the MPs into the hashmap
				if (!ratedMPsMap.containsKey(key))
				{
					ratedMPsMap.put(key, tempRatedMPs.get(key));
				}
				else
				{
					// otherwise, keep only the lowest (best) similarity score for any MP.
					if (ratedMPsMap.get(key) > tempRatedMPs.get(key))
					{
						ratedMPsMap.put(key, tempRatedMPs.get(key));
					}
				}
			}
		}

		for (String key : ratedMPsMap.keySet())
		{
			// Convert the hashmap into an ArrayList.
			sortedMPsArray.add(new RatedMP(Capone.getInstance().getParliament().getMP(key), ratedMPsMap.get(key)));
		}
		Collections.sort(sortedMPsArray); // sort the MPs by their ratings.
		// return the MPs in sorted order - without the MPs in the original parameter list.
		return removeOverlap(convertToRatedMPList(sortedMPsArray), pSelectedMPs);
	}

	// returns a map relating each MP to his similarity score compared to the parameter MP.
	private static HashMap<String, Double> getSimilarMPs(MP pMP)
	{
		HashMap<String, Double> ratedMPs = new HashMap<String, Double>();
		MPVertex currMPVertex = new MPVertex(pMP.getEmail(), getWordIndex());
		for (MP mp : Capone.getInstance().getParliament().getMPList())
		{
			if (!mp.getEmail().equals(pMP.getEmail()))
			{
				ratedMPs.put(mp.getEmail(), mpSimilarityScore(currMPVertex, pMP, mp));
			}
		}
		return ratedMPs;
	}

	// returns a score that signifies how similar two MPs are. The small the score, the more similar they are.
	// If two MPs are the same, the score will be 0.0.
	private static double mpSimilarityScore(MPVertex pCurrMPVertex, MP pCurrMP, MP pComparedMP)
	{
		double tempSimilarityScore = mpSpeechHeaderSimilarityScore(pCurrMPVertex, pComparedMP);
		if (pCurrMP.getCurrentMembership().getParty().equals(pComparedMP.getCurrentMembership().getParty()))
		{
			tempSimilarityScore = tempSimilarityScore * SAME_PARTY_MULTIPLIER;
		}
		if (pCurrMP.getCurrentMembership().getRiding().getProvince()
				.equals(pComparedMP.getCurrentMembership().getRiding().getProvince()))
		{
			tempSimilarityScore = tempSimilarityScore * SAME_PROVINCE_MULTIPLIER;
		}
		return tempSimilarityScore;
	}

	// returns the Similarity score of pMP2 compared to the pCurrMPVertex's MP, which determines how similar the MPs are
	// based on how similar their speeches are.
	private static double mpSpeechHeaderSimilarityScore(MPVertex pCurrMPVertex, MP pMP2)
	{
		MPVertex tempMPVertex = new MPVertex(pMP2.getEmail(), getWordIndex());
		double similarityScore = pCurrMPVertex.subtract(tempMPVertex).vLength();
		return similarityScore;
	}

	// Generates a map containing (String word,int index) pairs for all the words in all the headers of all the
	// speeches.
	private static HashMap<String, Integer> generateWordIndex()
	{
		HashMap<String, Integer> wordIndex = new HashMap<String, Integer>();
		int currIndex = 0;
		StringBuffer combinedHeaders;
		for (MP mp : Capone.getInstance().getParliament().getMPList())
		{
			for (Speech sp : mp.returnSpeeches())
			{
				combinedHeaders = new StringBuffer();
				combinedHeaders.append(sp.getHeader1());
				combinedHeaders.append(sp.getHeader2());
				for (String str : combinedHeaders.toString().split(" "))
				{
					if (!wordIndex.containsKey(str))
					{
						wordIndex.put(str, currIndex);
						currIndex++;
					}
				}
			}
		}
		return wordIndex;
	}

	// a getter which returns the WordIndex map generated by generateWordIndex.
	private static HashMap<String, Integer> getWordIndex()
	{
		return aWordIndex;
	}

	// removes all elements of pOverLap from pOriginal
	private static ArrayList<MP> removeOverlap(ArrayList<MP> pOriginal, ArrayList<MP> pOverlap)
	{
		for (MP mp : pOverlap)
		{
			pOriginal.remove(mp);
		}
		return pOriginal;
	}

	// converts a RatedMP list to MP list, keeping relative order.
	private static ArrayList<MP> convertToRatedMPList(ArrayList<RatedMP> pRatedMPs)
	{
		ArrayList<MP> result = new ArrayList<MP>(); // will be used to sort the MPs by their rating.
		for (RatedMP mp : pRatedMPs)
		{
			result.add(mp.getMP());
		}
		return result;
	}
}

package ca.mcgill.cs.comp303.capone.recommenders.stubs;

import ca.mcgill.cs.comp303.capone.model.Speech;

/**
 * an object which will hold a speech and its score.
 */
public class RatedSpeech implements Comparable<RatedSpeech>
{
	private Speech aSpeech;
	private double aScore;

	/**
	 * @param pSpeech
	 *            the speech to be added
	 * @param pScore
	 *            the score of the speech added
	 */
	public RatedSpeech(Speech pSpeech, double pScore)
	{
		aSpeech = pSpeech;
		aScore = pScore;
	}

	// returns reverse ordering, in order to be used in max priorityQueue
	@Override
	public int compareTo(RatedSpeech pRatedSpeech)
	{
		if (pRatedSpeech.getScore() > aScore)
		{
			return 1;
		}
		if (pRatedSpeech.getScore() < aScore)
		{
			return -1;
		}
		return 0;
	}

	/**
	 * @return Speech
	 */
	public Speech getSpeech()
	{
		return aSpeech;
	}

	/**
	 * @return score
	 */
	public double getScore()
	{
		return aScore;
	}

}

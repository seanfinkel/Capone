package ca.mcgill.cs.comp303.capone.recommenders.stubs;

import ca.mcgill.cs.comp303.capone.model.MP;

/**
 * an object which will hold an MP and its score.
 */
public class RatedMP implements Comparable<RatedMP>
{
	private MP aMP;
	private double aScore;

	/**
	 * @param pMP
	 *            the mp to be added
	 * @param pScore
	 *            the score of the mp added
	 */
	public RatedMP(MP pMP, double pScore)
	{
		aMP = pMP;
		aScore = pScore;
	}

	// returns reverse ordering, in order to be used in max priorityQueue
	@Override
	public int compareTo(RatedMP pRatedMP)
	{
		if (pRatedMP.getScore() > aScore)
		{
			return -1;
		}
		if (pRatedMP.getScore() < aScore)
		{
			return 1;
		}
		return 0;
	}

	/**
	 * @return mp
	 */
	public MP getMP()
	{
		return aMP;
	}

	/**
	 * @return score
	 */
	public double getScore()
	{
		return aScore;
	}

}

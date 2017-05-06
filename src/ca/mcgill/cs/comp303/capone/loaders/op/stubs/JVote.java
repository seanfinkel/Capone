package ca.mcgill.cs.comp303.capone.loaders.op.stubs;

import java.util.List;

/**
 * 
 * @author aying1
 * 
 */
public class JVote
{
	JTextFrenchEnglish description;
	int nay_total;
	String url;
	List<VotesPerParty> party_votes;
	int yea_total;
	int paired_total;
	int number;
	Related related;
	String session;
	String result;
	String date;
	String context_statement;
	String bill_url;

	public static class VotesPerParty
	{
		String vote;
		JParty party;
		float disagreement;
	}

	public static class Related
	{
		String ballots;
		String votes_url;
	}
}

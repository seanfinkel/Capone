package ca.mcgill.cs.comp303.capone.loaders.op.stubs;

import java.util.List;

/**
 * 
 * @author aying1
 * 
 */
public class JCommittee
{
	JTextFrenchEnglish name;
	JTextFrenchEnglish short_name;
	List<Session> sessions;
	String url;
	Related related;
	String[] subcommittees;
	String parent_url;
	String slug;

	public static class Session
	{
		String acronym;
		String session;
		String source_url;
	}

	public static class Related
	{
		String meetings_url;
		String committees_url;
	}
}

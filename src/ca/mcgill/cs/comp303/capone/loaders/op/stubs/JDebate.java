package ca.mcgill.cs.comp303.capone.loaders.op.stubs;

/**
 * 
 * @author aying1
 * 
 */
public class JDebate
{
	int source_id;
	String document_type;
	String session;
	String url;
	String date;
	JTextEnglishOnly most_frequent_word;
	String number;
	Related related;
	String source_url;

	public static class Related
	{
		String speeches_url;
		String debates_url;
	}
}

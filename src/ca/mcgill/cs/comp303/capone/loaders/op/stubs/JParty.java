package ca.mcgill.cs.comp303.capone.loaders.op.stubs;

public class JParty
{
	private JTextEnglishOnly name;
	private JTextEnglishOnly short_name;

	public String getName()
	{
		return name.getEn();
	}

	public String getShort_name()
	{
		return short_name.getEn();
	}
}

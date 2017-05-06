package ca.mcgill.cs.comp303.capone.loaders.op.stubs;

public class JRiding
{
	private String province;
	private JTextEnglishOnly name;
	private int id;

	public String getProvince()
	{
		return province;
	}

	public String getName()
	{
		return name.getEn();
	}

	public int getId()
	{
		return id;
	}
}

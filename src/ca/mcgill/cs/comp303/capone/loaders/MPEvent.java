package ca.mcgill.cs.comp303.capone.loaders;

/**
 * An event related to the professional life of an MP. Immutable.
 */
public final class MPEvent
{
	private String aTitle = "";
	private String aLink = "";
	private String aDescription = "";
	private Descriptor aType = Descriptor.UNCLASSIFIED;

	/**
	 * Describes what kind of event this is.
	 */
	public enum Descriptor
	{
		UNCLASSIFIED, MEDIA, VOTE, SPEECH
	}

	/**
	 * Constructs a new event.
	 * 
	 * @param pType The type of the event.
	 * @param pTitle The title of the event.
	 * @param pLink The event link.
	 * @param pDescription A description of the event.
	 */
	public MPEvent(Descriptor pType, String pTitle, String pLink, String pDescription)
	{
		aType = pType;
		aTitle = pTitle;
		aLink = pLink;
		aDescription = pDescription;
	}

	/**
	 * @return The type of the event.
	 */
	public Descriptor getType()
	{
		return aType;
	}

	/**
	 * @return The title of the event.
	 */
	public String getTitle()
	{
		return aTitle;
	}

	/**
	 * @return The link of the event.
	 */
	public String getLink()
	{
		return aLink;
	}

	/**
	 * @return The description of the event.
	 */
	public String getDescription()
	{
		return aDescription;
	}

	@Override
	public String toString()
	{
		return "MPEvent [aTitle=" + aTitle + ", aLink=" + aLink + ", aDescription=" + aDescription + "]";
	}
}
package ca.mcgill.cs.comp303.capone.loaders.op;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import ca.mcgill.cs.comp303.capone.loaders.MPEvent;
import ca.mcgill.cs.comp303.capone.loaders.ParliamentLoader;
import ca.mcgill.cs.comp303.capone.loaders.op.stubs.JMP;
import ca.mcgill.cs.comp303.capone.loaders.op.stubs.JMembership;
import ca.mcgill.cs.comp303.capone.loaders.op.stubs.JSpeech;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Membership;
import ca.mcgill.cs.comp303.capone.model.Parliament;
import ca.mcgill.cs.comp303.capone.model.Party;
import ca.mcgill.cs.comp303.capone.model.Riding;
import ca.mcgill.cs.comp303.capone.model.Speech;
import ca.mcgill.cs.comp303.capone.model.stubs.Utilities;

import com.google.gson.Gson;

/**
 * A builder that can build the model from serialized JSON objects stored at specific locations on disk. Objects of this
 * class should store the root of the data tree internally (e.g., C:\workspace...\data). The last branches of the path
 * map directly to the OpenParliament API.
 * 
 * We packaged the data under Capone-M1/data. You can (should) use this as the root path.
 */
public class OpenParliamentFileLoader implements ParliamentLoader
{
	// Some formatters you might find useful.
	@SuppressWarnings("unused")
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	// Relative paths to JSON files
	private static final String CONTEXT_POLITICIANS = "politicians" + File.separator;
	private static final String CONTEXT_SPEECHES = "debates" + File.separator;
	private static final String CONTEXT_RSS = "rss" + File.separator;

	private static final String JSON_SUFFIX = ".json";

	private final String aDataFileSubdir;
	private final String aQueryContext;

	private Parliament aParliament;

	/**
	 * Constructs a new OpenParliamentFileLoader object.
	 * 
	 * @param pParliament
	 *            the Parliament that the loader modifies
	 * @param pPath
	 *            the location of the data the the loader uses
	 * @precondition pParliament != null, pPath != null
	 */
	public OpenParliamentFileLoader(Parliament pParliament, String pPath)
	{
		aParliament = pParliament;
		aDataFileSubdir = pPath + File.separator;
		aQueryContext = new File(aDataFileSubdir).getAbsolutePath() + File.separator;
	}

	/*
	 * The relative location indicates the subpath leading to a specific politician. For the complete list, see:
	 * http://api.openparliament.ca/politicians/ an example of input for pRelativeLocation is: gord-brown
	 * 
	 * @see ca.mcgill.cs.comp303.capone.loaders.ParliamentLoader#loadMP(java.lang.String,
	 * ca.mcgill.cs.comp303.capone.model.Parliament)
	 */
	@Override
	/**
	 * @precondition pRelativeLocation != null, pParliament != null
	 */
	public String loadMP(String pRelativeLocation, Parliament pParliament) throws FileNotFoundException
	{
		// This method produces a complete path pointing to the
		// JSON file for this MP.
		String jsonUri = getMPJsonUri(pRelativeLocation);

		// JSON file can get loaded easily into stub objects
		// using the Google GSON library. See the ...op.stubs
		// package. Do not change any stub.
		JMP jmp;
		try
		{
			jmp = getGson(jsonUri, JMP.class);

			// create the MP object
			String email = jmp.getEmail();
			String familyName = jmp.getFamily_name();
			String givenName = jmp.getGiven_name();
			String name = jmp.getName();
			String phoneNumber = jmp.getVoice();
			String imageURL = jmp.getImage();

			ArrayList<Membership> memberships = new ArrayList<Membership>();
			List<JMembership> jMemberships = jmp.getMemberships();
			for (JMembership jMemb : jMemberships)
			{
				memberships.add(constructMembership(jMemb));
			}
			MP mp = new MP(email, familyName, givenName, name, phoneNumber, imageURL, memberships);

			if (email == null)
			{
				throw new FileNotFoundException("The MP " + name + " is missing email. Not loaded.");
			}

			// If the MP is not currently in parliament, add it
			if (aParliament.getMP(email) == null)
			{
				aParliament.addMP(mp);
			}
			else
			{
				refreshMP(email, familyName, givenName, name, phoneNumber, imageURL, mp);
			}

			// Return the primary key of an MP that was either just added or not modified at all
			return mp.getPrimaryKey();
		}
		catch (FileNotFoundException e)
		{
			throw new FileNotFoundException("Couldn't load " + pRelativeLocation);
		}
	}

	private void refreshMP(String pEmail, String pFamilyName, String pGivenName, String pName, String pPhoneNumber,
			String pImageURL, MP pMp)

	{
		MP oldMP = aParliament.getMP(pEmail);
		if (MP.mpChanged(pMp, oldMP))
		{
			// Compare the old and new membership lists and add all memberships from the new one
			// that are not present in the old one
			ArrayList<Membership> oldMemberships = oldMP.getMembershipList();
			ArrayList<Membership> mergedMemberships = oldMP.getMembershipList();
			ArrayList<Membership> newMemberships = pMp.getMembershipList();

			for (Membership newMemb : newMemberships)
			{
				boolean membershipFound = false;
				for (Membership oldMemb : oldMemberships)
				{
					// A membership is found to be already in on record if the start date is the same
					if (Utilities.stringsAreEqual(newMemb.getStartDate(), oldMemb.getStartDate()))
					{
						membershipFound = true;
						// If the end dates are not the same, update the end date by deleting the old membership
						// and adding the new one
						if (!Utilities.stringsAreEqual(newMemb.getEndDate(), oldMemb.getEndDate()))
						{
							mergedMemberships.remove(oldMemb);
							mergedMemberships.add(newMemb);
						}
					}
				}
				// When a membership is not found at all in the old record, add it
				if (!membershipFound)
				{
					mergedMemberships.add(newMemb);
				}
			}

			// Construct the MP
			MP mergedMP = new MP(pEmail, pFamilyName, pGivenName, pName, pPhoneNumber, pImageURL, mergedMemberships);

			aParliament.deleteMP(pEmail);
			aParliament.addMP(mergedMP);
		}

	}

	/**
	 * @param pJMembership
	 *            The JMembership from which a membership is constructed
	 * @return The membership that was just constructed
	 * @precondition pJMembership != null
	 */
	private Membership constructMembership(JMembership pJMembership)
	{
		Party party;
		Riding riding;
		String startDate;
		String endDate;

		Party partyReference = aParliament.findParty(pJMembership.getParty().getName());
		if (partyReference != null)
		{
			party = partyReference;
		}
		else
		{
			String partyName = pJMembership.getParty().getName();
			String partyShortName = pJMembership.getParty().getShort_name();
			party = aParliament.addParty(new Party(partyName, partyShortName));
		}
		Riding ridingReference = aParliament.findRiding(pJMembership.getRiding().getId());
		if (ridingReference != null)
		{
			riding = ridingReference;
		}
		else
		{
			int ridingId = pJMembership.getRiding().getId();
			String ridingName = pJMembership.getRiding().getName();
			String ridingProvince = pJMembership.getRiding().getProvince();
			riding = aParliament.addRiding(new Riding(ridingId, ridingName, ridingProvince));
		}
		startDate = pJMembership.getStart_date();
		endDate = pJMembership.getEnd_date();
		return new Membership(party, riding, startDate, endDate);

	}

	@Override
	/**
	 * @precondition pParliament != null
	 */
	public void loadRecentEvents(String pMPKey, Parliament pParliament) throws FileNotFoundException
	{
		MP aMP = pParliament.getMP(pMPKey);
		String aRssFeed = aMP.getRSSFeedURI();
		if (aRssFeed == null) // if there is no RSS URL on file, exit the method.
		{
			return;
		}

		InputStream aInRssFeed;
		try
		{
			aInRssFeed = getInputStream(getRssUri(aRssFeed));
			RssSaxParser aRssSaxParser = new RssSaxParser();
			List<MPEvent> aMPEvents = aRssSaxParser.parse(aInRssFeed); // load MPEvent list
			ArrayList<Speech> oldSpeeches = aMP.returnSpeeches();
			for (MPEvent event : aMPEvents)
			{
				if (event.getLink().split("openparliament.ca/debates/").length > 1) // if the event if a debate
				{
					addSingleSpeech(pMPKey, aMP, oldSpeeches, event);
				}
			}
		}
		catch (FileNotFoundException e)
		{
			throw new FileNotFoundException("Couldn't load recent events for: " + pMPKey);
		}
	}

	private void addSingleSpeech(String pMPKey, MP pMP, ArrayList<Speech> pOldSpeeches, MPEvent pEvent)
	{
		try
		{
			// put the speech into a stub
			String speechURL = getSpeechJsonUri(getSpeechContext(pEvent.getLink()));
			JSpeech jspeech = getGson(speechURL, JSpeech.class);

			// create a Speech object from the stub
			String speechH1 = (jspeech.getH1()).getEn();
			String speechH2 = ""; // if we don't have a second headline, report it as an empty string.
			if (jspeech.getH2() != null)
			{
				speechH2 = (jspeech.getH2()).getEn();
			}
			String speechContent = jspeech.getContent().getEn();
			Date speechTime = DATE_TIME_FORMAT.parse(jspeech.getTime());
			Speech newSpeech = new Speech(speechH1, speechH2, speechContent, speechTime, pMPKey);

			// compare the new speech object to all the old speeches
			// add it if it is a new speech
			addIfNewSpeech(pMP, pOldSpeeches, newSpeech);
		}
		catch (ParseException e)
		{
			// ignore the speech if we can't parse the file properly.
		}
		catch (FileNotFoundException e)
		{
			// ignore the speech if we can't find it.
		}
	}

	private static void addIfNewSpeech(MP pMP, ArrayList<Speech> pOldSpeeches, Speech pNewSpeech)
	{
		boolean speechFound = false;
		for (Speech oldSpeech : pOldSpeeches)
		{
			// if the speech already exists, mark it so
			if (oldSpeech.compareTo(pNewSpeech) == 0)
			{
				speechFound = true;
			}
		}
		if (!speechFound)
		{
			// if we couldn't find the speech, add it
			pMP.loadSpeech(pNewSpeech);
		}
	}

	// Below are some potentially useful utility methods. You may need to
	// experiment with them a bit to make sure you understand how they work.

	private static String getSpeechContext(String pRssSpokeInTheHouseLink)
	{
		String httpLink = pRssSpokeInTheHouseLink;
		String speechContextHttp = CONTEXT_SPEECHES.replace(File.separator, "/");
		int start = httpLink.indexOf(speechContextHttp) + speechContextHttp.length();
		String context = httpLink.substring(start, httpLink.length() - 1).replace("/", File.separator);
		return context;
	}

	/**
	 * Uses the google-gson library to convert JSON to Java objects (prefixed those objects with J, in package
	 * ...loaders.op.stubs directory).
	 * 
	 * @param pJsonUri
	 *            Filename of the JSON data
	 * @param ptype
	 *            A Class object from the package ...loaders.op.stubs
	 * @return A GSON stub object, specified by the parameter "type" CSOFF:
	 */
	public static <T> T getGson(String pJsonUri, Class<T> ptype) throws FileNotFoundException
	{
		InputStreamReader json = new InputStreamReader(getInputStream(pJsonUri));
		T gson = new Gson().fromJson(json, ptype);
		return gson;
	}

	// CSON:

	/**
	 * @param pMPRelativeLocation
	 *            Relative location that identifies the MP, returned by getMPRelativeLocations
	 * @return Filename of the JSON data on the given MP
	 */
	public String getMPJsonUri(String pMPRelativeLocation)
	{
		return aQueryContext + CONTEXT_POLITICIANS + pMPRelativeLocation + JSON_SUFFIX;
	}

	/**
	 * @param pSpeechContext
	 *            The speech context obtained from the link from the RSS feed "Spoke in the house" event, e.g.,
	 *            debates/2013/6/17/thomas-mulcair-4/
	 * @return Filename of the JSON data on transcript of the given MP's speeches
	 */
	public String getSpeechJsonUri(String pSpeechContext)
	{
		return aQueryContext + CONTEXT_SPEECHES + pSpeechContext + JSON_SUFFIX;
	}

	/**
	 * @param pMPRelativeLocation
	 *            Relative location that identifies the MP, returned by getMPRelativeLocations
	 * @return Filename of the RSS feed for the specified MP.
	 */
	public String getRssUri(String pMPRelativeLocation)
	{
		return aQueryContext + CONTEXT_RSS + pMPRelativeLocation + ".xml";
	}

	/*
	 * Return a FileInputStream for the given path
	 */
	private static InputStream getInputStream(String pFilePath) throws FileNotFoundException
	{

		File file = new File(pFilePath);
		return new FileInputStream(file);

	}

	/**
	 * @return the relative locations of all the MPs.
	 */
	public Iterator<String> getMPRelativeLocations()
	{
		List<String> result = new ArrayList<String>();
		String dir = aQueryContext + CONTEXT_POLITICIANS;
		File file2 = new File(dir);
		file2.mkdirs();
		Collection<File> files = FileUtils.listFiles(file2, new WildcardFileFilter("*.json"), null);

		for (File file : files)
		{
			String f = file.getName();
			result.add(f.substring(0, f.length() - ".json".length()));
		}
		return result.iterator();
	}

	/**
	 * @return the number of MPs that this loader will load.
	 */
	public float getMPListSize()
	{
		String dir = aQueryContext + CONTEXT_POLITICIANS;
		File file2 = new File(dir);
		file2.mkdirs();
		Collection<File> files = FileUtils.listFiles(file2, new WildcardFileFilter("*.json"), null);
		return files.size();
	}

}

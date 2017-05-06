package ca.mcgill.cs.comp303.capone.loaders.op;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

import ca.mcgill.cs.comp303.capone.loaders.MPEvent;
import ca.mcgill.cs.comp303.capone.loaders.ParliamentLoader;
import ca.mcgill.cs.comp303.capone.loaders.op.stubs.JMP;
import ca.mcgill.cs.comp303.capone.loaders.op.stubs.JMPList;
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
 * A Builder that loads information from the OpenParliament API. It can load both speeches (recent events) and MPs. It
 * saves all the files to disk.
 * 
 */
public class WebParliamentLoader implements ParliamentLoader
{

	private static final int OUTPUT_BUFFER_SIZE = 1024;
	private static final String API_URL_PREFIX = "http://api.";

	private static final String JSON_EXTENSION_SUFFIX = ".json";
	private static final String XML_EXTENSION_SUFFIX = ".xml";

	private static final String API_POLITICANS_URL = "http://api.openparliament.ca/politicians/";
	private static final String RSS_URL_SUFFIX = "/rss/activity/";
	private static final String JSON_URL_SUFFIX = "?format=json";

	private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private static final String CONTEXT_POLITICIANS = "politicians";
	private static final String CONTEXT_RSS = "rss";
	private static final String POLITICIANS_DATA_LOCATION = File.separator + CONTEXT_POLITICIANS + File.separator;
	private static final String RSS_DATA_LOCATION = File.separator + CONTEXT_RSS + File.separator;;

	private static String aDataLocation;
	private Parliament aParliament;
	private int aNumberOfNewSpeeches;
	private Date aMinDate = null;
	private Date aMaxDate = null;

	/**
	 * @param pParliament
	 *            the Parliament to which we will load new objects
	 * @param pSaveToPath
	 *            the location to which we will save the retreieved information
	 */
	public WebParliamentLoader(Parliament pParliament, String pSaveToPath)
	{
		aParliament = pParliament;
		aDataLocation = pSaveToPath;
		aNumberOfNewSpeeches = 0;
	}

	@SuppressWarnings("null")
	@Override
	public String loadMP(String pRelativeLocation, Parliament pParliament) throws FileNotFoundException
	{
		new File(aDataLocation + POLITICIANS_DATA_LOCATION).getAbsolutePath();

		// get the json file
		InputStreamReader json = getJson(API_POLITICANS_URL + pRelativeLocation + File.separator + JSON_URL_SUFFIX);
		StringWriter writer = null;
		FileWriter fileWriter = null;
		try
		{
			// write the JSON to the data-saved location
			writer = new StringWriter();
			IOUtils.copy(json, writer);
			String theString = writer.toString();
			File file = new File(aDataLocation + POLITICIANS_DATA_LOCATION + pRelativeLocation + JSON_EXTENSION_SUFFIX);
			file.getParentFile().mkdirs();
			fileWriter = new FileWriter(file);
			fileWriter.write(theString);

			// reset the json InputStreamReader
			InputStream is = new ByteArrayInputStream(theString.getBytes());
			json = new InputStreamReader(is);
		}
		catch (IOException e)
		{
			throw new FileNotFoundException("coulnd't load the MP: " + pRelativeLocation);
		}
		finally
		{
			try
			{
				fileWriter.close();
				writer.close();
			}
			catch (IOException e)
			{
				// Never occurs.
			}

		}

		// convert the json file to an MP object, add it to parliament, and return the MP key.
		JMP jmp = getGson(json, JMP.class);
		return addJMPtoParliament(jmp);
	}

	@SuppressWarnings("null")
	@Override
	/**
	 * @precondition pParliament != null
	 */
	public void loadRecentEvents(String pMPKey, Parliament pParliament) throws FileNotFoundException
	{
		FileOutputStream outputStream = null;
		InputStream aInRssFeed = null;

		aParliament = pParliament;
		MP aMP = aParliament.getMP(pMPKey);
		String aRssFeed = aMP.getRSSFeedURI();

		try
		{
			aInRssFeed = getRSSInputStream(getRSSUrl(aRssFeed));
			RssSaxParser aRssSaxParser = new RssSaxParser();
			List<MPEvent> aMPEvents = aRssSaxParser.parse(aInRssFeed); // load MPEvent list
			ArrayList<Speech> oldSpeeches = aMP.returnSpeeches();
			for (MPEvent event : aMPEvents)
			{
				if (event.getLink().split("openparliament.ca/debates/").length > 1)
				{
					addSingleSpeech(pMPKey, aMP, oldSpeeches, event);
				}
			}

			// write the RSS feed to file
			aInRssFeed = getRSSInputStream(getRSSUrl(aRssFeed));
			File file = new File(aDataLocation + RSS_DATA_LOCATION + aMP.getRSSFeedURI() + XML_EXTENSION_SUFFIX);
			file.getParentFile().mkdirs();
			outputStream = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[OUTPUT_BUFFER_SIZE];

			while ((read = aInRssFeed.read(bytes)) != -1)
			{
				outputStream.write(bytes, 0, read);
			}
		}
		catch (IOException e)
		{
			// we couldn't open the RSS feed for the current MP
			throw new FileNotFoundException("Couldn't load RSS feed for " + pMPKey);
		}
		finally
		{
			try
			{
				outputStream.close();
				aInRssFeed.close();
			}
			catch (IOException e)
			{
				// Never occurs.
			}
		}

	}

	private void addSingleSpeech(String pMPKey, MP pMP, ArrayList<Speech> pOldSpeeches, MPEvent pEvent)
	{
		try
		{
			// put the speech into a stub
			String speechURL = getSpeechURL(pEvent.getLink());
			InputStreamReader speechJSON = getJson(speechURL);
			JSpeech jspeech = getGson(speechJSON, JSpeech.class);

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
			if (addIfNewSpeech(pMP, pOldSpeeches, newSpeech))
			{
				writeSpeechToFile(speechURL);
				pMP.loadSpeech(newSpeech);

				// update Statistics
				aNumberOfNewSpeeches++;
				if (aMinDate == null)
				{
					aMinDate = newSpeech.getTime();
					aMaxDate = newSpeech.getTime();
				}
				if (newSpeech.getTime().before(aMinDate))
				{
					aMinDate = newSpeech.getTime();
				}
				if (newSpeech.getTime().after(aMaxDate))
				{
					aMaxDate = newSpeech.getTime();
				}
			}

		}
		catch (ParseException e)
		{
			// if we can't parse the time properly, ignore the speech.
		}
		catch (FileNotFoundException e)
		{
			// if we can't load a certain speech, ignore it.
		}
	}

	@SuppressWarnings("null")
	private static void writeSpeechToFile(String pSpeechURL)
	{
		InputStreamReader speechJSON;
		File file;
		FileWriter writer = null;
		StringWriter stringWriter = null;
		try
		{
			speechJSON = getJson(pSpeechURL);

			String filePath = aDataLocation + getSpeechFilePath(pSpeechURL) + JSON_EXTENSION_SUFFIX;
			file = new File(filePath);
			file.getParentFile().mkdirs();
			writer = new FileWriter(file);

			stringWriter = new StringWriter();
			IOUtils.copy(speechJSON, stringWriter);
			String theString = stringWriter.toString();
			writer.write(theString);
		}
		catch (IOException e)
		{
			// ignore speech if we can't write it to file.
		}
		finally
		{
			try
			{
				stringWriter.close();
				writer.close();
			}
			catch (IOException e)
			{
				// do nothing
			}

		}
	}

	private static boolean addIfNewSpeech(MP pMP, ArrayList<Speech> pOldSpeeches, Speech pNewSpeech)
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
			return true;
		}
		return false;
	}

	private String addJMPtoParliament(JMP pJmp) throws FileNotFoundException
	{
		String email = pJmp.getEmail();
		String familyName = pJmp.getFamily_name();
		String givenName = pJmp.getGiven_name();
		String name = pJmp.getName();
		String phoneNumber = pJmp.getVoice();
		String imageURL = pJmp.getImage();

		ArrayList<Membership> memberships = new ArrayList<Membership>();
		List<JMembership> jMemberships = pJmp.getMemberships();
		for (JMembership jMemb : jMemberships)
		{
			memberships.add(constructMembership(jMemb));
		}

		MP mp = new MP(email, familyName, givenName, name, phoneNumber, imageURL, memberships);

		if (email == null)
		{
			throw new FileNotFoundException("null email for " + name);
		}
		// If the MP is not currently in parliament, add it
		if (aParliament.getMP(email) == null)
		{
			aParliament.addMP(mp);
		}

		// If the MP is already in parliament, then only reload the MP if the contents changed
		else
		{
			MP oldMP = aParliament.getMP(email);
			if (MP.mpChanged(mp, oldMP))
			{
				return refreshMP(email, familyName, givenName, name, phoneNumber, imageURL, mp, oldMP);
			}
		}

		// Return the primary key of an MP that was either just added or not modified at all
		return mp.getPrimaryKey();
	}

	private String refreshMP(String pEmail, String pFamilyName, String pGivenName, String pName, String pPhoneNumber,
			String pImageURL, MP pMp, MP pOldMP)
	{
		// Compare the old and new membership lists and add all memberships from the new one
		// that are not present in the old one
		ArrayList<Membership> oldMemberships = pOldMP.getMembershipList();
		ArrayList<Membership> mergedMemberships = pOldMP.getMembershipList();
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
		// Return the primary key of the updated MP
		return mergedMP.getPrimaryKey();

	}

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

	private static InputStreamReader getJson(String pJsonUri) throws FileNotFoundException
	{
		InputStreamReader json = null;
		try
		{
			json = new InputStreamReader(getInputStream(pJsonUri));
			return json;
		}
		catch (IOException e)
		{
			throw new FileNotFoundException(e.getMessage());
		}
	}

	private static <T> T getGson(InputStreamReader pJson, Class<T> pType)
	{
		T gson = null;
		gson = new Gson().fromJson(pJson, pType);
		return gson;
	}

	private static InputStream getInputStream(String pURLString) throws IOException
	{
		InputStream reader = null;
		URL url = new URL(pURLString);
		reader = url.openStream();
		return reader;
	}

	private static InputStream getRSSInputStream(String pURLString) throws IOException
	{
		InputStream reader = null;
		URL url = new URL(pURLString);
		reader = new BufferedInputStream(url.openStream());
		return reader;
	}

	private static String getRSSUrl(String pRSSUri)
	{
		return API_POLITICANS_URL + pRSSUri + RSS_URL_SUFFIX;
	}

	private static String getSpeechURL(String pRssSpokeInTheHouseLink)
	{
		String url = pRssSpokeInTheHouseLink.split("http://")[1];
		return API_URL_PREFIX + url + JSON_URL_SUFFIX;
	}

	private static String getSpeechFilePath(String pURL)
	{
		String extendedPath = pURL.split("http://api.openparliament.ca")[1];
		return extendedPath.split("/\\?format=json")[0];
	}

	/**
	 * @return a list iterator of all the MPs' relative locations.
	 * @throws FileNotFoundException
	 */
	public static Iterator<String> getMPRelativeLocations() throws FileNotFoundException
	{
		List<String> result = new ArrayList<String>();
		String mpURL = API_POLITICANS_URL + JSON_URL_SUFFIX;
		InputStreamReader stream = getJson(mpURL);
		JMPList list = getGson(stream, JMPList.class);
		for (JMP jmp : list.getObjects())
		{
			result.add(jmp.getUrl().split("/politicians/")[1].split("/")[0]);
		}
		return result.iterator();
	}

	/**
	 * @return The size of the list generated by getMPRelativeLocations().
	 * @throws FileNotFoundException
	 *             if file cannot be opened.
	 */
	public static int getMPListSize() throws FileNotFoundException
	{
		String mpURL = API_POLITICANS_URL + JSON_URL_SUFFIX;
		InputStreamReader stream;
		stream = getJson(mpURL);
		JMPList list = getGson(stream, JMPList.class);
		return list.getObjects().size();
	}

	/**
	 * @return number of speeches added.
	 */
	public int getaNumberOfNewSpeeches()
	{
		return aNumberOfNewSpeeches;
	}

	/**
	 * @return the date of the earliest speech added in this instance of the loader.
	 */
	public Date getaMinDate()
	{
		return aMinDate;
	}

	/**
	 * @return the date of the latest speech added in this instance of the loader.
	 */
	public Date getaMaxDate()
	{
		return aMaxDate;
	}

}

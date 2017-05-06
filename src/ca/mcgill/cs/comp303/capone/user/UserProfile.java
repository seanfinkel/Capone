package ca.mcgill.cs.comp303.capone.user;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import ca.mcgill.cs.comp303.capone.Capone;
import ca.mcgill.cs.comp303.capone.model.Speech;
import ca.mcgill.cs.comp303.capone.recommenders.ContentBasedRecommender;
import ca.mcgill.cs.comp303.capone.recommenders.ISpeechRecommender;
import ca.mcgill.cs.comp303.capone.recommenders.SimilarityBasedRecommender;

/**
 * UserProfile will store a list of MPs and a list of keywords it will represent the personal interest of users of the
 * Capone application.
 */
public class UserProfile extends Observable implements Observer, Serializable
{
	private static final long serialVersionUID = -5162130195215343923L;
	private ArrayList<String> aListKeywords;
	private ArrayList<String> aListMPs;
	private ArrayList<Speech> aListRecommendations;
	private ArrayList<Speech> aListFlagSpeech;

	/**
	 * UserProfile constructor.
	 */
	public UserProfile()
	{
		try
		{
			importProfile("UserProfile");
		}
		catch (IOException e)
		{
			aListKeywords = new ArrayList<String>();
			aListMPs = new ArrayList<String>();
			aListRecommendations = new ArrayList<Speech>();
			aListFlagSpeech = new ArrayList<Speech>();
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * 
	 * @param pKey
	 *            of MP to be added to user profile
	 */
	public void addMP(String pKey)
	{
		if (Capone.getInstance().getParliament().checkOnMP(pKey))
		{
			if (!aListMPs.contains(pKey))
			{
				aListMPs.add(pKey);
			}
		}
		buildRecommendations();
		setChanged();
		notifyObservers();
	}

	/**
	 * 
	 * @param pKey
	 *            of MP to be removed from the user profile
	 */
	public void removeMP(String pKey)
	{
		aListMPs.remove(pKey);
		buildRecommendations();
		setChanged();
		notifyObservers();
	}

	/**
	 * 
	 * @param pKeyword
	 *            to be added to user profile
	 */
	public void addKeyword(String pKeyword)
	{
		if (!aListKeywords.contains(pKeyword))
		{
			aListKeywords.add(pKeyword);
		}
		buildRecommendations();
		setChanged();
		notifyObservers();
	}

	/**
	 * 
	 * @param pKeyword
	 *            to be removed from the user profile
	 */
	public void removeKeyword(String pKeyword)
	{
		aListKeywords.remove(pKeyword);
		buildRecommendations();
		setChanged();
		notifyObservers();
	}

	/**
	 * Replaces one keyword with another.
	 * 
	 * @param pOldKeyword
	 *            pOldKeyword The keyword to be replaced
	 * @param pNewKeyword
	 *            The new keyword to be inserted
	 * @return True if the replacement was successful
	 */
	public boolean replaceKeyword(String pOldKeyword, String pNewKeyword)
	{
		if (aListKeywords.contains(pNewKeyword))
		{
			return false;
		}

		int index = aListKeywords.indexOf(pOldKeyword);
		aListKeywords.set(index, pNewKeyword);

		buildRecommendations();
		setChanged();
		notifyObservers();

		return true;
	}

	/**
	 * @return user's list of keywords
	 */
	public ArrayList<String> getListKeywords()
	{
		return aListKeywords;
	}

	/**
	 * @return user's list of MPs
	 */
	public ArrayList<String> getListMPs()
	{
		return aListMPs;
	}

	/**
	 * @param pSelectedRecommender
	 *            a concrete strategy implenetaion of ISpeechRecommender.
	 * @return a list of speeches, sorted from most relevant to least.
	 */
	public ArrayList<Speech> recommend(ISpeechRecommender pSelectedRecommender)
	{
		return pSelectedRecommender.recommendSpeeches(aListMPs, aListKeywords);
	}

	/**
	 * build a list of recommendations based on the LastUsedRecommender.
	 */
	public void buildRecommendations()
	{
		String currentRecommender = Capone.getInstance().getProperties().getLastUsedRecommender();
		if (getListMPs().isEmpty())
		{
			aListRecommendations = Capone.getInstance().getUserProfile().recommend(new ContentBasedRecommender());
		}
		else
		{
			if ("Content Based".equals(currentRecommender))
			{
				aListRecommendations = Capone.getInstance().getUserProfile().recommend(new ContentBasedRecommender());
			}
			if ("Similarity Based".equals(currentRecommender))
			{
				aListRecommendations = Capone.getInstance().getUserProfile()
						.recommend(new SimilarityBasedRecommender());
			}
		}

		Speech[] recArray = new Speech[aListRecommendations.size()];
		for (int i = 0; i < aListRecommendations.size(); i++)
		{
			recArray[i] = aListRecommendations.get(i);
		}
		aListRecommendations = new ArrayList<Speech>();
		for (Speech speech : recArray)
		{
			if (!speech.getReadStatus())
			{
				aListRecommendations.add(speech);
			}
		}
		setChanged();
		notifyObservers();
	}

	/**
	 * @return return a list of recommended speeches.
	 */
	public ArrayList<Speech> getRecommendations()
	{
		return aListRecommendations;
	}

	/**
	 * @param pSpeech
	 *            speech to be marked as read.
	 */
	public void flagSpeech(Speech pSpeech)
	{

		pSpeech.setReadStatus(true);
		aListFlagSpeech.add(pSpeech);
		buildRecommendations();
		setChanged();
		notifyObservers();
	}

	/**
	 * clears the "read" status on all the speeches.
	 */
	public void clearFlagSpeechList()
	{
		for (Speech speech : aListFlagSpeech)
		{
			speech.setReadStatus(false);
		}
		aListFlagSpeech.clear();
		buildRecommendations();
		setChanged();
		notifyObservers();
	}

	/**
	 * @return list of speeches read.
	 */
	public ArrayList<Speech> getFlagSpeechList()
	{
		return aListFlagSpeech;
	}

	/**
	 * @param pExport
	 *            the export type
	 * @param pPath
	 *            the path we wish to export to
	 */
	public void export(ExportUserProfile pExport, String pPath)
	{
		aListRecommendations = new ArrayList<Speech>();
		pExport.export(this, pPath);
	}

	/**
	 * @param pPath
	 *            the path to extract the userprofile from.
	 * @throws IOException
	 *             if the file could not be opened.
	 */
	public void importProfile(String pPath) throws IOException
	{
		UserProfile profile = BinaryImport.importUserProfile(pPath);
		this.aListKeywords = profile.getListKeywords();
		this.aListMPs = profile.getListMPs();
		aListRecommendations = new ArrayList<Speech>();
		aListFlagSpeech = new ArrayList<Speech>();
		if (profile.getFlagSpeechList() != null)
		{
			for (Speech sp : profile.getFlagSpeechList())
			{
				aListFlagSpeech.add(sp);
			}
		}
		setChanged();
		notifyObservers();
		// this.aListFlagSpeech = profile.getFlagSpeechList();
	}

	/**
	 * checks every MP in user profile, and makes sure it's also currently in parliament.
	 */
	public void checkOnMPList()
	{
		int i = 0;
		while (i < aListMPs.size())
		{
			String currMP = aListMPs.get(i);
			if (!Capone.getInstance().getParliament().checkOnMP(currMP))
			{
				aListMPs.remove(i);
			}
			else
			{
				i++;
			}
		}
		setChanged();
		notifyObservers();
	}

	// called whenever parliament changes
	@Override
	public void update(Observable pO, Object pArg)
	{
		int sizeMpList = getListMPs().size();
		for (int i = 0; i < sizeMpList; i++)
		{
			String id = getListMPs().get(i);
			if (!(Capone.getInstance().getParliament().checkOnMP(id)))
			{
				getListMPs().remove(i);
			}
		}
		buildRecommendations();
		setChanged();
		notifyObservers();
	}
}
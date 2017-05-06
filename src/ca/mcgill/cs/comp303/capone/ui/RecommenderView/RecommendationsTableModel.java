package ca.mcgill.cs.comp303.capone.ui.RecommenderView;

import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import ca.mcgill.cs.comp303.capone.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;
import ca.mcgill.cs.comp303.capone.model.Speech;

/**
 * Table model for recommendation table.
 */
public class RecommendationsTableModel extends AbstractTableModel
{
	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("MessageBundle");
	private static final long serialVersionUID = 1L;
	private final String aRankCol = MESSAGES.getString("Capone-M2.src.gui.RecommendationsTableModel.rank");
	private final String aHeadingCol = MESSAGES.getString("Capone-M2.src.gui.RecommendationsTableModel.heading");
	private final String aMpCol = MESSAGES.getString("Capone-M2.src.gui.RecommendationsTableModel.mp");
	private final String aDateCol = MESSAGES.getString("Capone-M2.src.gui.RecommendationsTableModel.date");
	private final String aKeywordsCol = MESSAGES.getString("Capone-M2.src.gui.RecommendationsTableModel.keywords");
	private final String aSynopsisCol = MESSAGES.getString("Capone-M2.src.gui.RecommendationsTableModel.synopsis");

	private String[] aColumnNames = { aRankCol, aHeadingCol, aMpCol, aDateCol, aKeywordsCol, aSynopsisCol };
	private ArrayList<Speech> aList;

	@Override
	public int getColumnCount()
	{
		return aColumnNames.length;
	}

	@Override
	public int getRowCount()
	{
		aList = Capone.getInstance().getUserProfile().getRecommendations();
		if (aList == null)
		{
			return 0;
		}
		return aList.size();
	}

	@Override
	public Object getValueAt(int pRowIndex, int pColumnIndex)
	{
		aList = Capone.getInstance().getUserProfile().getRecommendations();
		Speech currentSpeech = aList.get(pRowIndex);
		String toRet = "";
		if (pColumnIndex == 0) // rank of speech
		{
			toRet = pRowIndex + 1 + ".";
		}
		else if (pColumnIndex == 1) // speech header
		{
			toRet = currentSpeech.getHeader1();
		}
		else if (pColumnIndex == 2) // MP name
		{
			toRet = authorOfSpeech(currentSpeech);
		}
		else if (pColumnIndex == 3) // speech time
		{
			toRet = currentSpeech.getTime().toString();
		}
		else if (pColumnIndex == 4) // keywords in the speech
		{
			toRet = findKeywordsInSpeech(currentSpeech).toString();
		}
		if (pColumnIndex >= 0 && pColumnIndex <= 4)
		{
			return toRet;
		}
		String firstLineCont = currentSpeech.getContent().split("[^r]\\.|<|:")[0];
		return firstLineCont;
	}

	private static String authorOfSpeech(Speech pSpeech)
	{
		String toRet;
		String key = pSpeech.getAuthor();
		MP mp = Capone.getInstance().getParliament().getMP(key);
		String name = mp.getName();
		String riding = mp.getCurrentMembership().getRiding().getName();
		String province = mp.getCurrentMembership().getRiding().getProvince();
		String party = mp.getCurrentMembership().getParty().getShortName();
		toRet = name + " (" + riding + ", " + province + ", " + party + ")";
		return toRet;
	}

	private static ArrayList<String> findKeywordsInSpeech(Speech pSpeech)
	{
		ArrayList<String> matchedKeywords = new ArrayList<String>();

		for (String keyWord : Capone.getInstance().getUserProfile().getListKeywords())
		{
			String[] splitStringH1 = pSpeech.getHeader1().split("(?i)" + keyWord);
			String[] splitStringH2 = pSpeech.getHeader2().split("(?i)" + keyWord);
			String[] splitStringContent = pSpeech.getContent().split("(?i)" + keyWord);
			if (splitStringH1.length > 1 || splitStringH2.length > 1 || splitStringContent.length > 1)
			{
				matchedKeywords.add(keyWord);
			}
		}
		return matchedKeywords;
	}

	@Override
	public String getColumnName(int pCol)
	{
		return aColumnNames[pCol];
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Class getColumnClass(int pC)
	{
		return getValueAt(0, pC).getClass();
	}

	@Override
	public boolean isCellEditable(int pRow, int pCol)
	{
		return false;
	}

}

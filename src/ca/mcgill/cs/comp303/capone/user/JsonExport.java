package ca.mcgill.cs.comp303.capone.user;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import ca.mcgill.cs.comp303.capone.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * exports user profile in json format.
 */
public class JsonExport implements ExportUserProfile
{

	private static Logger logger = Logger.getLogger("ca.mcgill.cs.comp303.capone.user.JsonExport");

	private JsonArray aUserProfileJArray = new JsonArray();
	private JsonArray aListMpJArray = new JsonArray();
	private JsonArray aListKeywordJArray = new JsonArray();

	@Override
	public void export(UserProfile pUserProfile, String pPath)
	{

		for (int i = 0; i < pUserProfile.getListMPs().size(); i++)
		{
			String mpKey = pUserProfile.getListMPs().get(i);
			MP currMP = Capone.getInstance().getParliament().getMP(mpKey);
			JsonObject mp = new JsonObject();
			mp.addProperty("mp_name", currMP.getName());
			mp.addProperty("mp_email", currMP.getEmail());
			mp.addProperty("mp_riding", currMP.getCurrentMembership().getRiding().getName());
			mp.addProperty("mp_party", currMP.getCurrentMembership().getParty().getName());
			aListMpJArray.add(mp);
		}
		aUserProfileJArray.add(aListMpJArray);

		for (int i = 0; i < pUserProfile.getListKeywords().size(); i++)
		{
			JsonObject keywordJson = new JsonObject();
			keywordJson.addProperty("keyword", pUserProfile.getListKeywords().get(i));
			aListKeywordJArray.add(keywordJson);
		}
		aUserProfileJArray.add(aListKeywordJArray);

		String userProfileJson = aUserProfileJArray.toString();
		File jsonFile = new File(pPath + ".json");

		try
		{
			PrintWriter out = new PrintWriter(jsonFile);
			out.println(userProfileJson);
			out.flush();
			out.close();
		}
		catch (FileNotFoundException e)
		{
			logger.log(Level.WARNING, "File Not Found Exception!", e);
		}

	}

}
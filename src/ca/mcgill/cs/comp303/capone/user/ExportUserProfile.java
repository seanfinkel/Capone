package ca.mcgill.cs.comp303.capone.user;

/**
 * will export user profile in either binary of JSON format.
 */
public interface ExportUserProfile
{
	/**
	 * @param pUserProfile
	 *            the userprofile we wish to export.
	 * @param pPath
	 *            the path we wish to export to.
	 */
	void export(UserProfile pUserProfile, String pPath);
}

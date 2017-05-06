package ca.mcgill.cs.comp303.capone.ui.UserProfileView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import ca.mcgill.cs.comp303.capone.Capone;

/**
 * The profile tab of the GUI.
 */
@SuppressWarnings("serial")
public class UserProfileView extends JPanel implements ActionListener
{
	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("MessageBundle");
	private UserProfileMPTable aMPs;
	private UserProfileKeywordTable aKeywords;
	private final String aRemoveMP = MESSAGES.getString("Capone-M2.src.gui.ProfileTab.removeMP");
	private final String aAddKeyword = MESSAGES.getString("Capone-M2.src.gui.ProfileTab.addKeyword");
	private final String aDeleteKeyword = MESSAGES.getString("Capone-M2.src.gui.ProfileTab.deleteKeyword");
	private final String aEditKeyword = MESSAGES.getString("Capone-M2.src.gui.ProfileTab.editKeyword");
	private final String aEnterKeyword = MESSAGES.getString("Capone-M2.src.gui.ProfileTab.enterKeyword");
	private final String aMPColumn = MESSAGES.getString("Capone-M2.src.gui.ProfileMPTableModel.mps");
	private final String aKeywordColumn = MESSAGES.getString("Capone-M2.src.gui.ProfileMPTableModel.keywords");

	/**
	 * Creates a Profile tab.
	 */
	public UserProfileView()
	{
		super();

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		// Create Remove MP button
		JButton removeMP = new JButton(aRemoveMP);
		JPanel mpButtons = new JPanel();
		removeMP.addActionListener(this);
		mpButtons.add(removeMP);
		mpButtons.setLayout(new BoxLayout(mpButtons, BoxLayout.Y_AXIS));

		// Table of MPs
		aMPs = new UserProfileMPTable();
		JScrollPane mpTable = new JScrollPane(aMPs);

		// Create Add Keyword and Remove Keyword Buttons
		JButton addKeyword = new JButton(aAddKeyword);
		JButton deleteKeyword = new JButton(aDeleteKeyword);
		JButton editKeyword = new JButton(aEditKeyword);
		JPanel keywordButtons = new JPanel();
		addKeyword.addActionListener(this);
		deleteKeyword.addActionListener(this);
		editKeyword.addActionListener(this);
		keywordButtons.add(addKeyword);
		keywordButtons.add(deleteKeyword);
		keywordButtons.add(editKeyword);
		keywordButtons.setLayout(new BoxLayout(keywordButtons, BoxLayout.Y_AXIS));

		// Table of Keywords
		aKeywords = new UserProfileKeywordTable();
		JScrollPane keywordTable = new JScrollPane(aKeywords);

		add(mpButtons);
		add(mpTable);
		add(keywordTable);
		add(keywordButtons);

	}

	@SuppressWarnings("unused")
	@Override
	/**
	 * The actionPerformed() method is not part of an anonymous listener
	 * because it needs to modify certain variables.This is impossible
	 * to do inside anonymous classes because Java does not allow 
	 * anonymous classes to access non-final variables.
	 */
	public void actionPerformed(ActionEvent pEvent)
	{
		if (((JButton) pEvent.getSource()).getText().equals(aRemoveMP))
		{
			int row = aMPs.getTable().getSelectedRow();
			if (row != -1)
			{
				String selectedMP = (String) aMPs.getTable().getValueAt(row,
						aMPs.getTable().getColumnModel().getColumnIndex(aMPColumn));
				Capone.getInstance().getUserProfile().removeMP(selectedMP);
			}
		}

		else if (((JButton) pEvent.getSource()).getText().equals(aAddKeyword))
		{
			JFrame frame = new JFrame(aEnterKeyword);
			frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			KeywordAddWindow keywordWindow = new KeywordAddWindow(this, frame);

		}

		else if (((JButton) pEvent.getSource()).getText().equals(aDeleteKeyword))
		{
			int row = aKeywords.getTable().getSelectedRow();
			if (row != -1)
			{
				String selectedKeyword = (String) aKeywords.getTable().getValueAt(row,
						aKeywords.getTable().getColumnModel().getColumnIndex(aKeywordColumn));
				Capone.getInstance().getUserProfile().removeKeyword(selectedKeyword);
			}
		}

		else if (((JButton) pEvent.getSource()).getText().equals(aEditKeyword))
		{
			int row = aKeywords.getTable().getSelectedRow();
			if (row != -1)
			{
				String selectedKeyword = (String) aKeywords.getTable().getValueAt(row,
						aKeywords.getTable().getColumnModel().getColumnIndex(aKeywordColumn));
				JFrame frame = new JFrame(aEnterKeyword);
				frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				KeywordEditWindow keywordWindow = new KeywordEditWindow(this, frame, selectedKeyword);
			}
		}

		revalidate();
		repaint();
	}
}

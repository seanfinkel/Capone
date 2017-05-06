package ca.mcgill.cs.comp303.capone.ui.UserProfileView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ca.mcgill.cs.comp303.capone.Capone;

/**
 * A window that enables a user to edit a keyword for a profile.
 */
public class KeywordEditWindow extends JPanel implements ActionListener
{
	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("MessageBundle");
	private static final long serialVersionUID = 1L;
	private UserProfileView aProfileTab;
	private JFrame aFrame;
	private String aSelectedKeyword;
	private JTextField aTextField;
	private final int aFieldlength = Integer.parseInt(MESSAGES
			.getString("Capone-M2.src.gui.KeywordEditWindow.fieldLength"));
	private final String aErrorPartOne = MESSAGES.getString("Capone-M2.src.gui.KeywordEditWindow.errorPartOne");
	private final String aErrorPartTwo = MESSAGES.getString("Capone-M2.src.gui.KeywordEditWindow.errorPartTwo");

	/**
	 * Constructor for KeywordEditWindow.
	 * 
	 * @param pProfileTab
	 *            The profile tab with which this window is associated
	 * @param pFrame
	 *            The frame that this window is
	 * @param pSelectedKeyword
	 *            The keyword that was selected to be replaced
	 */
	public KeywordEditWindow(UserProfileView pProfileTab, JFrame pFrame, String pSelectedKeyword)
	{
		super();

		aProfileTab = pProfileTab;
		aFrame = pFrame;
		aSelectedKeyword = pSelectedKeyword;

		aTextField = new JTextField(aFieldlength);
		aTextField.addActionListener(this);

		JScrollPane scrollPane = new JScrollPane(aTextField);
		add(scrollPane);

		// Add contents to the window.
		aFrame.add(this);
		// Display the window.
		aFrame.setLocationRelativeTo(null);
		aFrame.pack();
		aFrame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent pE)
	{
		String keyword = aTextField.getText();

		boolean replaceSuccess = Capone.getInstance().getUserProfile().replaceKeyword(aSelectedKeyword, keyword);

		if (!replaceSuccess)
		{
			JOptionPane.showMessageDialog(null, aErrorPartOne + keyword + aErrorPartTwo, "Error",
					JOptionPane.ERROR_MESSAGE);
		}

		else
		{
			// Update the Profile Tab
			aProfileTab.revalidate();
			aProfileTab.repaint();

			// Close the frame
			aFrame.dispose();
		}

	}

}
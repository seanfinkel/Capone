package ca.mcgill.cs.comp303.capone.ui.UserProfileView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ca.mcgill.cs.comp303.capone.Capone;

/**
 * A window that enables a user to enter a new keyword for a profile.
 */
public class KeywordAddWindow extends JPanel implements ActionListener
{
	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("MessageBundle");
	private static final long serialVersionUID = 1L;
	private UserProfileView aProfileTab;
	private JFrame aFrame;
	private JTextField aTextField;
	private final int aFieldlength = Integer.parseInt(MESSAGES
			.getString("Capone-M2.src.gui.KeywordAddWindow.fieldLength"));

	/**
	 * Constructor for KeywordAddWindow.
	 * 
	 * @param pProfileTab
	 *            The profile tab with which this window is associated
	 * @param pFrame
	 *            The frame that this window is
	 */
	public KeywordAddWindow(UserProfileView pProfileTab, JFrame pFrame)
	{
		super();

		aProfileTab = pProfileTab;
		aFrame = pFrame;

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
		// Add keyword to the list of keywords
		String keyword = aTextField.getText();
		Capone.getInstance().getUserProfile().addKeyword(keyword);

		// Update the Profile Tab
		aProfileTab.revalidate();
		aProfileTab.repaint();

		// Close the frame
		aFrame.dispose();
	}

}
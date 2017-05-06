package ca.mcgill.cs.comp303.capone.ui.RecommenderView;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumnModel;

import ca.mcgill.cs.comp303.capone.Capone;
import ca.mcgill.cs.comp303.capone.model.Speech;

/**
 * The recommender tab of the application.
 */
@SuppressWarnings("synthetic-access")
public class RecommenderView extends JPanel implements Observer
{
	private static final long serialVersionUID = 1L;
	private static final int MAX_WIDTH = 40;
	private static final int PREF_WIDTH_1 = 300;
	private static final int PREF_WIDTH_2 = 200;
	private static final int SYNOPSIS_COLUMN = 5;
	private static final int INSETS = 20;

	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("MessageBundle");
	private String aContentBased = MESSAGES.getString("Capone-M2.src.gui.RecommenderView.content");
	private String aSimilarityBased = MESSAGES.getString("Capone-M2.src.gui.RecommenderView.similarity");
	private String aSpeechRecommender = MESSAGES.getString("Capone-M2.src.gui.RecommenderView.speechRecommender");
	private String aDoubleClickInstruc = MESSAGES.getString("Capone-M2.src.gui.RecommenderView.doubleClick");
	private String aButtonLabel = MESSAGES.getString("Capone-M2.src.gui.RecommenderView.button");

	private JLabel aRecommenderLabel;
	private JComboBox aRecommenderComboBox;
	private JTable aTable;
	private JScrollPane aSpeechTable;
	private JTextPane aTextPane;
	private JScrollPane aContentPane;
	private JButton aFlagButton;
	private JPanel aRecommenderPanel; // label + box
	private JPanel aSpeechTablePanel; // table + recommender panel
	private Speech aCurrentSpeech;

	/**
	 * Creates a Recommender View.
	 */
	public RecommenderView()
	{
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// attach as observer to UserProfile and to Parliament
		Capone.getInstance().getUserProfile().addObserver(this);

		// Create the recommended speeches table
		RecommendationsTableModel model = new RecommendationsTableModel();
		aTable = new JTable(model);
		TableColumnModel tcm = aTable.getColumnModel();
		tcm.getColumn(0).setMaxWidth(MAX_WIDTH);
		tcm.getColumn(2).setPreferredWidth(PREF_WIDTH_1);
		tcm.getColumn(SYNOPSIS_COLUMN).setPreferredWidth(PREF_WIDTH_1);
		aTable.setFillsViewportHeight(true);
		aTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// add a double listener - to view a speech
		aTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent pE)
			{
				if (pE.getClickCount() == 2)
				{
					JTable target = (JTable) pE.getSource();
					int row = target.getSelectedRow();
					aCurrentSpeech = Capone.getInstance().getUserProfile().getRecommendations().get(row);
					String content = aCurrentSpeech.getContent();

					aTextPane.setText(content);
					update(null, null);
				}
			}
		});

		aSpeechTable = new JScrollPane(aTable);
		aSpeechTablePanel = new JPanel(new BorderLayout());
		aSpeechTablePanel.add(aSpeechTable, BorderLayout.CENTER);

		// Choose recommender type
		ArrayList<String> recommenders = new ArrayList<String>();
		recommenders.add(aContentBased);
		recommenders.add(aSimilarityBased);
		aRecommenderComboBox = new JComboBox(recommenders.toArray());
		aRecommenderComboBox.setEditable(false);
		aRecommenderComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent pE)
			{
				String aCurrentRecommender = aRecommenderComboBox.getSelectedItem().toString();
				Capone.getInstance().getProperties().setLastUsedRecommender(aCurrentRecommender);
				Capone.getInstance().getUserProfile().buildRecommendations();
				update(null, null);
			}
		});
		aRecommenderLabel = new JLabel(aSpeechRecommender + ":");
		aRecommenderPanel = new JPanel();
		aRecommenderPanel.add(aRecommenderLabel);
		aRecommenderPanel.add(aRecommenderComboBox);

		// Speech content text box
		aTextPane = new JTextPane();
		aTextPane.setEditable(false);
		int aWidth = aTextPane.getWidth();
		aTextPane.setPreferredSize(new Dimension(aWidth, PREF_WIDTH_2));
		aTextPane.setMaximumSize(getPreferredSize());
		aTextPane.setMinimumSize(getPreferredSize());

		aTextPane.setMargin(new Insets(INSETS, INSETS, INSETS, INSETS));
		aTextPane.setText(aDoubleClickInstruc);
		aContentPane = new JScrollPane(aTextPane);

		// Flag as read button
		aFlagButton = new JButton();
		aFlagButton.setText(aButtonLabel);
		aFlagButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent pE)
			{
				Capone.getInstance().getUserProfile().flagSpeech(aCurrentSpeech);
				aTextPane.setText(aDoubleClickInstruc);
				update(null, null);
			}
		});

		// add all components to panel
		add(aRecommenderPanel);
		add(aSpeechTablePanel);
		add(aContentPane);
		add(aFlagButton);
	}

	@Override
	public void update(Observable pO, Object pArg)
	{
		aTable.revalidate();
		aTable.repaint();
	}

}

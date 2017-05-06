package ca.mcgill.cs.comp303.capone.ui.UserProfileView;

import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableRowSorter;

import ca.mcgill.cs.comp303.capone.Capone;

/**
 * Table of Keywords for the Profile tab.
 */
public class UserProfileKeywordTable extends JPanel implements Observer
{
	private static final long serialVersionUID = 1L;
	private JTable aTable;
	private TableRowSorter<UserProfileKeywordTableModel> aSorter;

	/**
	 * Creates a table of Keywords for the Profile tab.
	 */
	public UserProfileKeywordTable()
	{
		super();

		// attach as observer to userprofile
		Capone.getInstance().getUserProfile().addObserver(this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// Create a table with a sorter.
		UserProfileKeywordTableModel model = new UserProfileKeywordTableModel();
		aSorter = new TableRowSorter<UserProfileKeywordTableModel>(model);
		aTable = new JTable(model);
		aTable.setRowSorter(aSorter);
		aTable.setFillsViewportHeight(true);
		aTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(aTable);

		// Add the scroll pane to this panel.
		add(scrollPane);
	}

	/**
	 * @return The JTable associated with the ProfileKeywordTable
	 */
	public JTable getTable()
	{
		return aTable;
	}

	@Override
	public void update(Observable pO, Object pArg)
	{
		aTable.revalidate();
		aTable.repaint();
	}
}

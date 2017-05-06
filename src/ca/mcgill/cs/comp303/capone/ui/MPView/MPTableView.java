package ca.mcgill.cs.comp303.capone.ui.MPView;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SpringLayout;
import javax.swing.table.TableRowSorter;

import ca.mcgill.cs.comp303.capone.Capone;
import ca.mcgill.cs.comp303.capone.model.Party;

/**
 * Creates the MP Table panel, and adds the two filter comboBoxes.
 * 
 */
@SuppressWarnings("synthetic-access")
public class MPTableView extends JPanel implements Observer
{
	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("MessageBundle");
	private static final long serialVersionUID = 1L;
	private JTable aTable;
	private JComboBox aPartyFilterBox;
	private JComboBox aProvinceFilterBox;
	private TableRowSorter<MPTableModelModel> aSorter;
	private String aCurrentlySelectedParty = MESSAGES.getString("Capone-M2.src.gui.MPTableView.all");
	private String aCurrentlySelectedProvince = MESSAGES.getString("Capone-M2.src.gui.MPTableView.all");

	/**
	 * Creates the MP table with Filter panel.
	 */
	public MPTableView()
	{
		super();

		// attach as observer to parliament
		Capone.getInstance().getParliament().addObserver(this);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JLabel instructions = new JLabel(MESSAGES.getString("Capone-M2.src.gui.MPTableView.instructions"));
		JPanel inst = new JPanel();
		inst.add(instructions);
		add(inst);

		// Create a table with a sorter.
		MPTableModelModel model = new MPTableModelModel();
		aSorter = new TableRowSorter<MPTableModelModel>(model);
		aTable = new JTable(model);
		aTable.setRowSorter(aSorter);
		aTable.setFillsViewportHeight(true);
		aTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add MP to userprofile when its row is double-clicked.
		aTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent pE)
			{
				if (pE.getClickCount() == 2)
				{
					JTable target = (JTable) pE.getSource();
					int row = target.getSelectedRow();
					String selectedMP = (String) aTable.getValueAt(
							row,
							aTable.getColumnModel().getColumnIndex(
									MESSAGES.getString("Capone-M2.src.gui.MPTableModel.nameColumn")));
					Capone.getInstance().getUserProfile()
							.addMP(Capone.getInstance().getParliament().getMPKey(selectedMP));
					update(null, null);
				}
			}
		});
		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(aTable);

		// Add the scroll pane to this panel.
		add(scrollPane);

		// Create a separate panel that will hold both buttons
		JPanel form = new JPanel(new SpringLayout());
		form.setLayout(new BorderLayout());

		// Create the sort by party combobox, and add it to the form panel
		ArrayList<String> partyNames = new ArrayList<String>();
		partyNames.add(MESSAGES.getString("Capone-M2.src.gui.MPTableView.all"));
		for (Party party : Capone.getInstance().getParliament().getParties())
		{
			partyNames.add(party.getShortName());
		}
		aPartyFilterBox = new JComboBox(partyNames.toArray());
		aPartyFilterBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent pE)
			{
				aCurrentlySelectedParty = aPartyFilterBox.getSelectedItem().toString();
				mpFilter();
			}
		});
		JPanel partyPanel = new JPanel();
		JLabel partyLabel = new JLabel(MESSAGES.getString("Capone-M2.src.gui.MPTableView.partyLabel"));
		partyPanel.add(partyLabel);
		partyPanel.add(aPartyFilterBox);
		form.add(partyPanel, BorderLayout.EAST);

		// Create the sort by province combobox, and add it to the form panel
		String[] provinceNames = { MESSAGES.getString("Capone-M2.src.gui.MPTableView.all"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.AB"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.BC"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.MB"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.NB"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.NL"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.NS"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.NT"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.NU"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.ON"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.PE"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.QC"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.SK"),
				MESSAGES.getString("Capone-M2.src.gui.MPTableView.YT") };
		aProvinceFilterBox = new JComboBox(provinceNames);
		aProvinceFilterBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent pE)
			{
				aCurrentlySelectedProvince = aProvinceFilterBox.getSelectedItem().toString();
				mpFilter();
			}
		});
		JPanel provincePanel = new JPanel();
		JLabel provinceLabel = new JLabel(MESSAGES.getString("Capone-M2.src.gui.MPTableView.provinceLabel"));
		provincePanel.add(provinceLabel);
		provincePanel.add(aProvinceFilterBox);
		form.add(provincePanel, BorderLayout.WEST);

		// add both comboboxes to our MPTableWithFilters panel
		add(form);

	}

	/**
	 * Filter the table by currently selected party and province.
	 */
	private void mpFilter()
	{
		RowFilter<MPTableModelModel, Object> rf = null;
		RowFilter<MPTableModelModel, Object> partyFilter = null;
		RowFilter<MPTableModelModel, Object> provinceFilter = null;
		ArrayList<RowFilter<MPTableModelModel, Object>> andFilters = new ArrayList<RowFilter<MPTableModelModel, Object>>();

		// If current expression doesn't parse, don't update.
		try
		{
			// Add the currently selected party to the filter
			partyFilter = RowFilter.regexFilter(aCurrentlySelectedParty, 1);
			if (aCurrentlySelectedParty.equals(MESSAGES.getString("Capone-M2.src.gui.MPTableView.all")))
			{
				partyFilter = RowFilter.regexFilter("", 1);
			}
			andFilters.add(partyFilter);

			// Add the currently selected province to the filter
			provinceFilter = RowFilter.regexFilter(
					aCurrentlySelectedProvince,
					aTable.getColumnModel().getColumnIndex(
							MESSAGES.getString("Capone-M2.src.gui.MPTableModel.provinceColumn")));
			if (aCurrentlySelectedProvince.equals(MESSAGES.getString("Capone-M2.src.gui.MPTableView.all")))
			{
				provinceFilter = RowFilter.regexFilter(
						"",
						aTable.getColumnModel().getColumnIndex(
								MESSAGES.getString("Capone-M2.src.gui.MPTableModel.provinceColumn")));
			}
			andFilters.add(provinceFilter);

			// Build the filter
			rf = RowFilter.andFilter(andFilters);
		}
		catch (java.util.regex.PatternSyntaxException e)
		{
			return;
		}
		aSorter.setRowFilter(rf);
	}

	@Override
	public void update(Observable pO, Object pArg)
	{
		for (Party party : Capone.getInstance().getParliament().getParties())
		{
			aPartyFilterBox.addItem(party.getShortName());
		}
		aPartyFilterBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent pE)
			{
				aCurrentlySelectedParty = aPartyFilterBox.getSelectedItem().toString();
				mpFilter();
			}
		});
		aTable.revalidate();
		aTable.repaint();
	}
}

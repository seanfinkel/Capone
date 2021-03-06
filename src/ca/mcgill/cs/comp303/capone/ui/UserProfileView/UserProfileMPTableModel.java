package ca.mcgill.cs.comp303.capone.ui.UserProfileView;

import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import ca.mcgill.cs.comp303.capone.Capone;

/**
 * A table model for a ProfileMPtable.
 */
public class UserProfileMPTableModel extends AbstractTableModel
{
	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("MessageBundle");
	private static final long serialVersionUID = 1L;
	private final String[] aColumnNames = { MESSAGES.getString("Capone-M2.src.gui.ProfileMPTableModel.mps") };

	@Override
	public int getColumnCount()
	{
		return aColumnNames.length;
	}

	@Override
	public int getRowCount()
	{
		return Capone.getInstance().getUserProfile().getListMPs().size();
	}

	@Override
	public Object getValueAt(int pRowIndex, int pColumnIndex)
	{
		return Capone.getInstance().getUserProfile().getListMPs().get(pRowIndex);
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

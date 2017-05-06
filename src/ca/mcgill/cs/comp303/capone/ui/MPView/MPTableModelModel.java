package ca.mcgill.cs.comp303.capone.ui.MPView;

import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import ca.mcgill.cs.comp303.capone.Capone;
import ca.mcgill.cs.comp303.capone.model.MP;

/**
 * A tablemodel for an MPTable.
 * 
 */
public class MPTableModelModel extends AbstractTableModel
{
	private static final ResourceBundle MESSAGES = ResourceBundle.getBundle("MessageBundle");
	private static final long serialVersionUID = 1L;
	private String[] aColumnNames = { MESSAGES.getString("Capone-M2.src.gui.MPTableModel.nameColumn"),
			MESSAGES.getString("Capone-M2.src.gui.MPTableModel.partyColumn"),
			MESSAGES.getString("Capone-M2.src.gui.MPTableModel.ridingColumn"),
			MESSAGES.getString("Capone-M2.src.gui.MPTableModel.provinceColumn"),
			MESSAGES.getString("Capone-M2.src.gui.MPTableModel.userProfileColumn") };

	@Override
	public int getColumnCount()
	{
		return aColumnNames.length;
	}

	@Override
	public int getRowCount()
	{
		return Capone.getInstance().getParliament().getMPList().size();
	}

	@Override
	public Object getValueAt(int pRowIndex, int pColumnIndex)
	{
		MP currMP = Capone.getInstance().getParliament().getMPList().get(pRowIndex);
		String toRet = "";
		if (pColumnIndex == 0)
		{
			toRet = currMP.getName();
		}
		else if (pColumnIndex == 1)
		{
			toRet = currMP.getCurrentMembership().getParty().getShortName();
		}
		else if (pColumnIndex == 2)
		{
			toRet = currMP.getCurrentMembership().getRiding().getName();
		}
		else if (pColumnIndex == 3)
		{
			toRet = currMP.getCurrentMembership().getRiding().getProvince();
		}
		else if (Capone.getInstance().getUserProfile().getListMPs().contains(currMP.getEmail()))
		{
			return MESSAGES.getString("Capone-M2.src.gui.MPTableModel.true");
		}
		if (pColumnIndex >= 0 && pColumnIndex <= 3)
		{
			return toRet;
		}
		return MESSAGES.getString("Capone-M2.src.gui.MPTableModel.false");
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
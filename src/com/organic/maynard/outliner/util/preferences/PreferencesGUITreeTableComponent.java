/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 *  - Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer. 
 * 
 *  - Redistributions in binary form must reproduce the above 
 *    copyright notice, this list of conditions and the following 
 *    disclaimer in the documentation and/or other materials provided 
 *    with the distribution. 
 * 
 *  - Neither the names "Java Outline Editor", "JOE" nor the names of its 
 *    contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS 
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT 
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
 
package com.organic.maynard.outliner.util.preferences;

import com.organic.maynard.outliner.*;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import javax.swing.table.*;

import org.xml.sax.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2004/02/02 10:17:42 $
 */

public class PreferencesGUITreeTableComponent extends AbstractPreferencesGUITreeComponent implements MouseListener {

	// Constants
	public static final String A_COL_LABELS = "col_labels";
	
	public static final String DELIMITER = ",";


	protected RemoveColumnHeaderRenderer removeColumnHeaderRenderer = new RemoveColumnHeaderRenderer();
	
	protected JTable table = new JTable();
	protected SimpleTableModel model = new SimpleTableModel();
	protected TableColumnModel colModel = table.getColumnModel();

	public void startSetup(Attributes atts) {
		table.setModel(model);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);		
		table.setPreferredScrollableViewportSize(new Dimension(100,100));
		
		// Get Column Labels
		ArrayList colLabels = new ArrayList();
		String colLabelsString = atts.getValue(A_COL_LABELS);
		
		colLabels.add("New");
		
		StringTokenizer tokenizer = new StringTokenizer(colLabelsString,DELIMITER);
		while (tokenizer.hasMoreTokens()) {
			colLabels.add(tokenizer.nextToken());
		}
		
		// Setup Table Model
		model.setColumnCount(colLabels.size());
		for (int i = 0; i < colLabels.size(); i++) {
			colModel.getColumn(i).setHeaderValue(colLabels.get(i));
		}

		// Setup Remove Column
		TableColumn removeColumn = colModel.getColumn(0);
		
		removeColumn.setMinWidth(90);
		removeColumn.setMaxWidth(90);
		removeColumn.setResizable(false);

		RemoveCellEditor editor = new RemoveCellEditor(this);
		removeColumn.setCellRenderer(editor);
		removeColumn.setCellEditor(editor);

		removeColumn.setHeaderRenderer(removeColumnHeaderRenderer);

		// Setup Table Header
		table.getTableHeader().addMouseListener(this);   
		table.getTableHeader().setReorderingAllowed(false); 

		// Put it all together		
		JScrollPane component = new JScrollPane(table);
		
		setComponent(component);
		super.startSetup(atts);
		table.addFocusListener(new TableListener(table, getPreference()));
	}

	// MouseListener Interface
	public void mouseClicked(MouseEvent e) {
		int col = table.getTableHeader().columnAtPoint(e.getPoint());
		if (col == 0) {
			// Add new row
			Object[] data = {"Remove","",""};
			model.addRow(data);
			model.fireTableDataChanged();
		}
	}

	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}

class SimpleTableModel extends DefaultTableModel {
    public void setValueAt(Object value, int row, int col) {
    	if (col != 0) {
    		super.setValueAt(value,row,col);
		}
	}
}

class RemoveColumnHeaderRenderer extends JButton implements TableCellRenderer {
	public RemoveColumnHeaderRenderer() {
		super("New");
	}

	public Component getTableCellRendererComponent(
		JTable table, 
		Object value, 
		boolean isSelected, 
		boolean hasFocus, 
		int row, 
		int column
	) {
		return this;
	}
}

class RemoveCellEditor extends ButtonCellEditor {

	private PreferencesGUITreeTableComponent component = null;

	public RemoveCellEditor(PreferencesGUITreeTableComponent component) {
		super(new JCheckBox());
		this.component = component;
	}

	protected void doEditing() {
		if (this.col == 0) {
			component.model.removeRow(this.row);
		}
	}
}
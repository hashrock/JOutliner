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

package com.organic.maynard.outliner;

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.xml.XMLTools;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.tree.*;

public abstract class AbstractAttributesPanel extends JTable {
	
	// Instance Fields
	private RemoveColumnHeaderRenderer removeColumnHeaderRenderer = new RemoveColumnHeaderRenderer();
	protected AttributeTableModel model = null;
	
	
	// The Constructor
	public AbstractAttributesPanel() {
		model = new AttributeTableModel(this);
		setModel(model);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		
		// Setup Remove Column
		TableColumn removeColumn = getColumnModel().getColumn(0);
		
		removeColumn.setMinWidth(80);
		removeColumn.setMaxWidth(80);
		removeColumn.setResizable(false);
		
		AttributesButtonCellEditor editor = new AttributesButtonCellEditor(this);
		removeColumn.setCellRenderer(editor);
		removeColumn.setCellEditor(editor);
		
		removeColumn.setHeaderRenderer(removeColumnHeaderRenderer);
		
		// Setup Editable Column
		TableColumn editableColumn = getColumnModel().getColumn(1);
		
		editableColumn.setMinWidth(OutlineEditableIndicator.TRUE_WIDTH);
		editableColumn.setMaxWidth(OutlineEditableIndicator.TRUE_WIDTH);
		editableColumn.setResizable(false);
		
		AttributesImageIconCellEditor editor2 = new AttributesImageIconCellEditor(this);
		editableColumn.setCellEditor(editor2);
		
		// Setup Table Header
		getTableHeader().addMouseListener(model);
		getTableHeader().setReorderingAllowed(false);
	}
	
	// Data Display
	public abstract void update();
	
	// Data Modification
	public abstract void newAttribute(String key, Object value, boolean isReadOnly, AttributeTableModel model);
	
	// Delete Attribute
	public abstract void deleteAttribute(int row, AttributeTableModel model);
	
	// Delete Attribute
	public abstract void toggleEditability(int row, AttributeTableModel model);
	
	// Set Value
	public abstract void setValueAt(Object value, int row, AttributeTableModel model);
	
	// Misc
	protected abstract boolean isCellEditable();
	protected abstract boolean isCellEditable(int row);
}


class RemoveColumnHeaderRenderer extends JButton implements TableCellRenderer {
	
	// Pseudo Constants
	private static String TEXT_NEW_ELLIPSIS = GUITreeLoader.reg.getText("new_ellipsis");
	
	
	// Constructor
	public RemoveColumnHeaderRenderer() {
		super(TEXT_NEW_ELLIPSIS);
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


class AttributeTableModel extends AbstractTableModel implements MouseListener {
	
	// Pseudo Constants
	private static String TEXT_ATTRIBUTE = GUITreeLoader.reg.getText("attribute");
	private static String TEXT_VALUE = GUITreeLoader.reg.getText("value");
	private static String TEXT_DELETE = GUITreeLoader.reg.getText("delete");
	
	private static NewAttributeDialog dialog = new NewAttributeDialog();
	
	// Instance Fields
	public AbstractAttributesPanel panel = null;
	
	public Vector keys = new Vector();
	public Vector values = new Vector();
	public Vector readOnly = new Vector();
	
	
	// Constructor
	public AttributeTableModel(AbstractAttributesPanel panel) {
		super();
		this.panel = panel;
	}
	
	
	public int getColumnCount() {
		return 4;
	}
	
	public int getRowCount() {
		return keys.size();
	}
	
	public Object getValueAt(int row, int col) {
		if (col == 0) {
			if (panel.isCellEditable(row)) {
				return TEXT_DELETE;
			} else {
				return "";
			}
		} else if (col == 1) {
			boolean isReadOnly = ((Boolean) readOnly.get(row)).booleanValue();
			if (isReadOnly) {
				return OutlineEditableIndicator.ICON_IS_NOT_PROPERTY;
			} else {
				return OutlineEditableIndicator.ICON_IS_PROPERTY;
			}
		} else if (col == 2) {
			return keys.get(row);
		} else {
			return values.get(row);
		}
	}
	
	public Class getColumnClass(int col) {
		try {
			if (col == 0) {
				return Class.forName("java.lang.String");
			} else if (col == 1) {
				return Class.forName("javax.swing.ImageIcon");
			} else if (col == 2) {
				return Class.forName("java.lang.String");
			} else {
				return Class.forName("java.lang.String");
			}
		} catch (java.lang.ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String getColumnName(int col) {
		if (col == 0) {
			return "";
		} else if (col == 1) {
			return "";
		} else if (col == 2) {
			return TEXT_ATTRIBUTE;
		} else {
			return TEXT_VALUE;
		}
	}
	
	public boolean isCellEditable(int row, int col) {
		if (col == 1) {
			if (!panel.isCellEditable()) {
				return false;
			} else {
				return true;
			}
		} else {
			if (!panel.isCellEditable(row)) {
				return false;
			}
			
			if (col == 0 || col == 3) { 
				return true;
			} else {
				return false;
			}
		}
	}
	
	public void setValueAt(Object value, int row, int col) {
		if (col == 3) {
			panel.setValueAt(value, row, this);
			
			fireTableCellUpdated(row, col);
		}
	}
	
	// MouseListener Interface
	public void mouseClicked(MouseEvent e) {
		int col = panel.getTableHeader().columnAtPoint(e.getPoint());
		if (col == 0) {
			if (!panel.isCellEditable()) {
				return;
			}
			
			dialog.show(panel);
		} else if (col == 1) {
			//System.out.println("sort");
		}
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}


class AttributesButtonCellEditor extends ButtonCellEditor {
	
	private AbstractAttributesPanel panel = null;
	
	public AttributesButtonCellEditor(AbstractAttributesPanel panel) {
		super(new JCheckBox());
		this.panel = panel;
	}
	
	protected void doEditing() {
		if (this.col == 0) {
			panel.deleteAttribute(this.row, (AttributeTableModel) panel.getModel());
		}
	}
}

class AttributesImageIconCellEditor extends ImageIconCellEditor {
	
	private AbstractAttributesPanel panel = null;
	
	public AttributesImageIconCellEditor(AbstractAttributesPanel panel) {
		super(new JCheckBox(), OutlineEditableIndicator.ICON_IS_PROPERTY, OutlineEditableIndicator.ICON_IS_NOT_PROPERTY);
		this.panel = panel;
	}
	
	protected void doEditing() {
		if (this.col == 1) {
			panel.toggleEditability(this.row, (AttributeTableModel) panel.getModel());
		}
	}
}

class NewAttributeDialog extends JDialog implements ActionListener {
	
	// Constants
	private static String OK = null;
	private static String CANCEL = null;
	private static String NEW_ATTRIBUTE = null;
	private static String ATTRIBUTE = null;
	private static String VALUE = null;
	
	private static String ERROR_EXISTANCE = null;
	private static String ERROR_UNIQUENESS = null;
	private static String ERROR_ALPHA_NUMERIC = null;
	
	// GUI Elements
	private JButton buttonOK = null;
	private JButton buttonCancel = null;
	private JTextField attributeField = null;
	private JTextField valueField = null;
	private JLabel errorLabel = null;
	
	// Context
	private AbstractAttributesPanel panel = null;
	
	// Constructors
	public NewAttributeDialog() {
		super(Outliner.outliner, NEW_ATTRIBUTE, true);
		
		OK = GUITreeLoader.reg.getText("ok");
		CANCEL = GUITreeLoader.reg.getText("cancel");
		NEW_ATTRIBUTE = GUITreeLoader.reg.getText("new_attribute");
		ATTRIBUTE = GUITreeLoader.reg.getText("attribute");
		VALUE = GUITreeLoader.reg.getText("value");
		ERROR_EXISTANCE = GUITreeLoader.reg.getText("error_att_key_existance");
		ERROR_UNIQUENESS = GUITreeLoader.reg.getText("error_att_key_uniqueness");
		ERROR_ALPHA_NUMERIC = GUITreeLoader.reg.getText("error_att_key_alpha");
		
		buttonOK = new JButton(OK);
		buttonCancel = new JButton(CANCEL);
		attributeField = new JTextField(20);
		valueField = new JTextField(20);
		errorLabel = new JLabel(" ");
		
		// Create the Layout
		setSize(250,180);
		setResizable(false);
		
		// Adding window adapter to fix problem where initial focus won't go to the textfield.
		// Solution found at: http://forums.java.sun.com/thread.jsp?forum=57&thread=124417&start=15&range=15;
		addWindowListener(
			new WindowAdapter() {
				public void windowOpened(WindowEvent e) {
					attributeField.requestFocus();
				}
			}
		);
		
		// Define the Bottom Panel
		JPanel bottomPanel = new JPanel();
		
		bottomPanel.setLayout(new FlowLayout());
		
		buttonOK.addActionListener(this);
		bottomPanel.add(buttonOK);
		
		buttonCancel.addActionListener(this);
		bottomPanel.add(buttonCancel);
		
		getContentPane().add(bottomPanel,BorderLayout.SOUTH);
		
		// Define the Center Panel
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridBagLayout());
		
		AbstractPreferencesPanel.addPreferenceItem(ATTRIBUTE, attributeField, centerPanel);
		AbstractPreferencesPanel.addPreferenceItem(VALUE, valueField, centerPanel);
		AbstractPreferencesPanel.addSingleItemCentered(errorLabel, centerPanel);
		
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		
		// Set the default button
		getRootPane().setDefaultButton(buttonOK);
	}
	
	public void show(AbstractAttributesPanel panel) {
		this.panel = panel;
		
		attributeField.setText("");
		valueField.setText("");
		errorLabel.setText(" ");
		
		attributeField.requestFocus();
		
		Rectangle r = Outliner.outliner.getBounds();
		setLocation((int) (r.getCenterX() - getWidth()/2), (int) (r.getCenterY() - getHeight()/2));
		
		super.show();
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(OK)) {
			ok();
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		}
	}
	
	private void ok() {
		String key = attributeField.getText();
		String value = valueField.getText();
		AttributeTableModel model = (AttributeTableModel) panel.getModel();
		
		// Validate Existence
		if ((key == null) || key.equals("")) {
			errorLabel.setText(ERROR_EXISTANCE);
			return;
		}
		
		// Validate alpha-numeric
		if (!XMLTools.isValidXMLAttributeName(key)) {
			errorLabel.setText(ERROR_ALPHA_NUMERIC);
			return;
		}
		
		// Validate Uniqueness
		for (int i = 0; i < model.keys.size(); i++) {
			String existingKey = (String) model.keys.get(i);
			if (key.equals(existingKey)) {
				errorLabel.setText(ERROR_UNIQUENESS);
				return;
			}
		}
		
		// All is good so lets make the change
		panel.newAttribute(key, value, false, model);
		panel.update(); // [md] Added so that modification date updates.
		
		this.hide();
	}
	
	private void cancel() {
		hide();
	}
}
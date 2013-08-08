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
import com.organic.maynard.outliner.guitree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import org.xml.sax.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.9 $, $Date: 2004/02/23 04:12:50 $
 */

public abstract class AbstractPreferencesPanel extends JPanel implements PreferencesPanel, GUITreeComponent, ActionListener {
	
	// Constants
	public static final String A_TITLE = "title";
	public static final String A_ID = "id";
	
	
	// GUI Components
	private static final Insets INSETS = new Insets(2,2,2,2);
	protected final JButton RESTORE_DEFAULT_EDITOR_BUTTON = new JButton(PreferencesFrame.RESTORE_DEFAULTS);
	
	protected GridBagLayout gridbag = new GridBagLayout();
	protected static GridBagConstraints c = new GridBagConstraints();
	
	
	// The Constructor
	public AbstractPreferencesPanel() {
		c.insets = INSETS;
		setLayout(gridbag);
	}
	
	
	// GUITreeComponent interface
	private String id = null;
        @Override
	public String getGUITreeComponentID() {return this.id;}
        @Override
	public void setGUITreeComponentID(String id) {this.id = id;}
	
        @Override
	public void startSetup(Attributes atts) {
		String title = atts.getValue(A_TITLE);
		String id = atts.getValue(A_ID);
		
		
		// Add this panel to the PreferencesFrame.		
		PreferencesFrame pf = (PreferencesFrame) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES_FRAME);
		
		// Add the preference panel at the appropriate depth.
		int depth = 0;
		while (true) {
			GUITreeComponent c = GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - ++depth);
			if(!(c instanceof PreferencesPanel)) {
				depth--;
				break;
			}
		}
		pf.addPanel(this, title, depth);
		
		// Add this panel to the PreferencesPanel Registry
		Outliner.prefs.addPreferencesPanel(id, this);
		
		// Start setting up box
		addSingleItemCentered(new JLabel(new StringBuffer().append("<html><font size=\"+1\">").append(title).append("</font></html>").toString()), this);
	}
	
        @Override
	public void endSetup(Attributes atts) {
		RESTORE_DEFAULT_EDITOR_BUTTON.addActionListener(this);		
		AbstractPreferencesPanel.addLastItem(RESTORE_DEFAULT_EDITOR_BUTTON, this);
		
		// Update all the prefs.
		setToCurrent();
	}
	
	
	// PreferencesPanel Interface
	private ArrayList containerStack = new ArrayList();
	
        @Override
	public Container getCurrentContainer() {
		if (containerStack.size() > 0) {
			return (Container) containerStack.get(containerStack.size() - 1);
		} else {
			return this;
		}
	}
	
        @Override
	public void startAddSubContainer(Container c) {
		containerStack.add(c);
	}
	
        @Override
	public void endAddSubContainer(Container c) {
		containerStack.remove(containerStack.size() - 1);
		AbstractPreferencesPanel.addSingleItemCentered((JComponent) c, getCurrentContainer());
	}
	
	private ArrayList prefs = new ArrayList();
	
        @Override
	public void addPreference(PreferencesGUITreeComponent pref) {
		prefs.add(pref);
	}
	
        @Override
	public PreferencesGUITreeComponent getPreference(int i) {
		return (PreferencesGUITreeComponent) prefs.get(i);
	}
	
        @Override
	public int getPreferenceListSize() {
		return prefs.size();
	}
	
        @Override
	public void setToCurrent() {
		for (int i = 0, limit = getPreferenceListSize(); i < limit; i++) {
			PreferencesGUITreeComponent comp = getPreference(i);
			Preference pref = comp.getPreference();
			
			if (comp instanceof PreferencesGUITreeTextFieldComponent) {
				JTextField text = (JTextField) comp.getComponent();
				text.setText(pref.getCur());
				
			} else if (comp instanceof PreferencesGUITreeComboBoxComponent) {
				JComboBox comboBox = (JComboBox) comp.getComponent();
				comboBox.setSelectedItem(pref.getCur());
				
			} else if (comp instanceof PreferencesGUITreeCheckBoxComponent) {
				JCheckBox checkBox = (JCheckBox) comp.getComponent();
				checkBox.setSelected(new Boolean(pref.getCur()).booleanValue());
				
			} else if (comp instanceof PreferencesGUITreeColorButtonComponent) {
				JButton button = (JButton) comp.getComponent();
				PreferenceColor prefColor = (PreferenceColor) pref;
				button.setBackground(prefColor.cur);
				
			} else if (comp instanceof PreferencesGUITreeTextAreaComponent) {
				JTextArea list = (JTextArea) ((JScrollPane) comp.getComponent()).getViewport().getView();
				PreferenceStringList prefStringList = (PreferenceStringList) pref;
				
				StringBuffer buf = new StringBuffer();
				for (int j = 0; j < prefStringList.cur.size(); j++) {
					buf.append(prefStringList.cur.get(j)).append("\n");
				}
				list.setText(buf.toString());
			} else if (comp instanceof PreferencesGUITreeTableComponent) {
				JTable table = (JTable) ((JScrollPane) comp.getComponent()).getViewport().getView();
				PreferenceHashMap prefHashMap = (PreferenceHashMap) pref;
				
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				model.setRowCount(0);
				HashMap map = prefHashMap.cur;
				
				Iterator it = map.keySet().iterator();
				
				while (it.hasNext()) {
					String key = (String) it.next();
					String value = (String) map.get(key);
					Object[] data = {"Remove",key,value};
					model.addRow(data);
				}
				
				table.setModel(model);
				model.fireTableDataChanged();
			}
		}
	}
	
	
	// ActionListener Interface
        @Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(PreferencesFrame.RESTORE_DEFAULTS)) {
			try {
			
				for (int i = 0; i < getPreferenceListSize(); i++) {
					PreferencesGUITreeComponent comp = getPreference(i);
					Preference pref = comp.getPreference();
					
					if (comp instanceof PreferencesGUITreeTextFieldComponent) {
						JTextField text = (JTextField) comp.getComponent();
						text.setText(pref.getDef());
						
					} else if (comp instanceof PreferencesGUITreeComboBoxComponent) {
						JComboBox comboBox = (JComboBox) comp.getComponent();
						comboBox.setSelectedItem(pref.getDef());
						
					} else if (comp instanceof PreferencesGUITreeCheckBoxComponent) {
						JCheckBox checkBox = (JCheckBox) comp.getComponent();
						checkBox.setSelected(new Boolean(pref.getDef()).booleanValue());
						
					} else if (comp instanceof PreferencesGUITreeColorButtonComponent) {
						JButton button = (JButton) comp.getComponent();
						PreferenceColor prefColor = (PreferenceColor) pref;
						button.setBackground(prefColor.def);
						
					} else if (comp instanceof PreferencesGUITreeTextAreaComponent) {
						JTextArea list = (JTextArea) ((JScrollPane) comp.getComponent()).getViewport().getView();
						PreferenceStringList prefStringList = (PreferenceStringList) pref;
						
						StringBuffer buf = new StringBuffer();
						for (int j = 0; j < prefStringList.def.size(); j++) {
							buf.append(prefStringList.def.get(j)).append("\n");
						}
						list.setText(buf.toString());
						
					} else if (comp instanceof PreferencesGUITreeTableComponent) {
						JTable table = (JTable) ((JScrollPane) comp.getComponent()).getViewport().getView();
						PreferenceHashMap prefHashMap = (PreferenceHashMap) pref;
						
						DefaultTableModel model = (DefaultTableModel) table.getModel();
						model.setRowCount(0);
						HashMap map = prefHashMap.def;
						
						Iterator it = map.keySet().iterator();
						
						while (it.hasNext()) {
							String key = (String) it.next();
							String value = (String) map.get(key);
							Object[] data = {"Remove",key,value};
							model.addRow(data);
						}
						
						table.setModel(model);
						model.fireTableDataChanged();
					}
					
					pref.restoreTemporaryToDefault();
				}
			} catch (Exception ex) {
				System.out.println("Exception: " + ex);
			}
		}
	}
	
	
	// Static Methods
	private static Dimension prefDim = new Dimension(3,1);
	
	public static void addPreferenceItem(String text, JComponent field, Container container) {
		LayoutManager layout = container.getLayout();
		
		JLabel label = new JLabel(text);
		field.setMaximumSize(field.getPreferredSize());
		
		if (layout instanceof GridBagLayout) {
			GridBagLayout gridbag = (GridBagLayout) layout;
			
			c.weighty = 0;
			
			c.weightx = 0;
			c.fill = GridBagConstraints.NONE;
			c.anchor = GridBagConstraints.EAST;
			c.gridwidth = 1; //reset to the default
			gridbag.setConstraints(label, c);
			container.add(label);
			
			c.weightx = 1;
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.WEST;
			c.gridwidth = GridBagConstraints.REMAINDER; //end row
			gridbag.setConstraints(field, c);
			container.add(field);
		} else {
			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalGlue());
			box.add(label);
			box.add(Box.createRigidArea(prefDim));
			box.add(field);
			container.add(box);
		}
	}
	
	public static void addSingleItemCentered(JComponent component, Container container) {
		LayoutManager layout = container.getLayout();
		
		component.setMaximumSize(component.getPreferredSize());
		
		if (layout instanceof GridBagLayout) {
			GridBagLayout gridbag = (GridBagLayout) layout;
			
			c.weighty = 0;
			c.weightx = 1;
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.CENTER;
			c.gridwidth = GridBagConstraints.REMAINDER; //end row
			gridbag.setConstraints(component, c);
			container.add(component);
		} else {
			Box box = Box.createHorizontalBox();
			box.add(Box.createHorizontalGlue());
			box.add(component);
			box.add(Box.createHorizontalGlue());
			container.add(box);
			container.add(component);
		}
	}
	
	public static void addLastItem(JComponent component, Container container) {
		GridBagLayout gridbag = (GridBagLayout) container.getLayout();
		
		component.setMaximumSize(component.getPreferredSize());
		
		c.weighty = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.NORTH;
		c.gridwidth = GridBagConstraints.REMAINDER; //end row
		gridbag.setConstraints(component, c);
		container.add(component);
	}
}
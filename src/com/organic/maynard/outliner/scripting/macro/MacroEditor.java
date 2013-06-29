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
 
package com.organic.maynard.outliner.scripting.macro;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.guitree.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import org.xml.sax.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.1 $, $Date: 2004/01/20 23:01:42 $
 */

public class MacroEditor extends AbstractGUITreeJDialog implements ActionListener, JoeReturnCodes {
	
	// Constants
	private static final int INITIAL_WIDTH = 450;
	private static final int INITIAL_HEIGHT = 400;
 	private static final int MINIMUM_WIDTH = 350;
	private static final int MINIMUM_HEIGHT = 300;
	
	private static String CREATE = null;
	private static String SAVE = null;
	private static String SAVE_AND_CLOSE = null;
	private static String CANCEL = null;
	private static String DELETE = null;
	
	private static String MACRO_TYPE = null;
	
	private Box createButtonBox = null;
	private Box updateButtonBox = null;
	
	private JLabel macroTypeName = null;
	
	private JButton createButton = null;
	private JButton saveButton = null;
	private JButton saveAndCloseButton = null;
	private JButton deleteButton = null;
	private JButton cancelCreateButton = null;
	private JButton cancelUpdateButton = null;

	private MacroConfig macroConfig = null;
	private MacroManagerFrame frame = null;
	
	// Button Mode
	private static String BUTTON_MODE_CREATE_TITLE = null;
	private static String BUTTON_MODE_UPDATE_TITLE = null;
	
	public static final int BUTTON_MODE_CREATE = 0;
	public static final int BUTTON_MODE_UPDATE = 1;
	
	
	// The Constructor
	public MacroEditor() {
		super(false, false, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		frame = Outliner.macroManager;
		frame.macroEditor = this;
	}
	
	private void initialize() {
		createButtonBox = Box.createHorizontalBox();
		updateButtonBox = Box.createHorizontalBox();
		
		macroTypeName = new JLabel();
		
		BUTTON_MODE_CREATE_TITLE = GUITreeLoader.reg.getText("new_macro");
		BUTTON_MODE_UPDATE_TITLE = GUITreeLoader.reg.getText("update_macro");
		
		CREATE = GUITreeLoader.reg.getText("create");
		SAVE = GUITreeLoader.reg.getText("save");
		SAVE_AND_CLOSE = GUITreeLoader.reg.getText("save_and_close");
		CANCEL = GUITreeLoader.reg.getText("cancel");
		DELETE = GUITreeLoader.reg.getText("delete");
		
		MACRO_TYPE = GUITreeLoader.reg.getText("macro_type");
		
		createButton = new JButton(CREATE);
		saveButton = new JButton(SAVE);
		saveAndCloseButton = new JButton(SAVE_AND_CLOSE);
		deleteButton = new JButton(DELETE);
		cancelCreateButton = new JButton(CANCEL);
		cancelUpdateButton = new JButton(CANCEL);
		
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					MacroEditor me = (MacroEditor) e.getWindow();
					me.cancel();
				}
			}
		);
		
		// Create the layout
		this.getContentPane().setLayout(new BorderLayout());
		
		createButton.addActionListener(this);
		saveButton.addActionListener(this);
		saveAndCloseButton.addActionListener(this);
		deleteButton.addActionListener(this);
		cancelCreateButton.addActionListener(this);
		cancelUpdateButton.addActionListener(this);
		
		createButtonBox.add(createButton);
		createButtonBox.add(Box.createHorizontalStrut(5));
		createButtonBox.add(cancelCreateButton);
		
		updateButtonBox.add(saveAndCloseButton);
		updateButtonBox.add(Box.createHorizontalStrut(5));
		updateButtonBox.add(saveButton);
		updateButtonBox.add(Box.createHorizontalStrut(5));
		updateButtonBox.add(deleteButton);
		updateButtonBox.add(Box.createHorizontalStrut(5));
		updateButtonBox.add(cancelUpdateButton);
		
		// Put it all together
		this.getContentPane().add(macroTypeName,BorderLayout.NORTH);
	}
	
	private boolean initialized = false;
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	public void setMacroConfigAndShow(MacroConfig macroConfig, int buttonMode) {
		// Lazy Instantiation
		if (!initialized) {
			initialize();
			initialized = true;
		}
		
		// Swap in the new MacroConfig Panel
		if (this.macroConfig != null) {
			this.remove((Component) this.macroConfig);
		}
		this.getContentPane().add((Component) macroConfig,BorderLayout.CENTER);
		
		this.macroConfig = macroConfig;
		
		if (buttonMode == BUTTON_MODE_CREATE) {
			this.remove(updateButtonBox);
			this.getContentPane().add(createButtonBox,BorderLayout.SOUTH);
			setTitle(BUTTON_MODE_CREATE_TITLE);
		} else if (buttonMode == BUTTON_MODE_UPDATE) {
			this.remove(createButtonBox);
			this.getContentPane().add(updateButtonBox,BorderLayout.SOUTH);
			setTitle(BUTTON_MODE_UPDATE_TITLE);
		}
		
		// Update the macroTypeName text with the name of the class of the macroConfig.
		this.macroTypeName.setText(MACRO_TYPE + ": " + Outliner.macroManager.getMacroTypeNameFromClassName(macroConfig.getMacro().getClass().getName()));
		
		show();
	}
	
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(CREATE)) {
			create();
		} else if (e.getActionCommand().equals(SAVE)) {
			save();
		} else if (e.getActionCommand().equals(SAVE_AND_CLOSE)) {
			saveAndClose();
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		} else if (e.getActionCommand().equals(DELETE)) {
			delete();
		}
	}
	
	private void create() {
		if (macroConfig.create()) {
			Macro macro = macroConfig.getMacro();
			
			// Add it to the Popup Menu
			int i = Outliner.macroPopup.addMacro(macro);
			
			// Add it to the list in the MacroManager
			if (macro instanceof SortMacro) {
				((DefaultListModel) frame.sortMacroList.getModel()).insertElementAt(macro.getName(),i);
			} else {
				((DefaultListModel) frame.macroList.getModel()).insertElementAt(macro.getName(),i);
			}
			
			// Save it to disk as a serialized object.
			saveMacro(macro);
			
			hide();
		} else {
			JOptionPane.showMessageDialog(this, GUITreeLoader.reg.getText("message_an_error_occurred"));
		}
	}
	
	private void saveAndClose() {
		save();
		hide();
	}
	
	private void save() {
		Macro macro = macroConfig.getMacro();
		String oldName = macro.getFileName();
		
		if (macroConfig.update()) {
			
			// Update the popup menu.
			int oldIndex = Outliner.macroPopup.removeMacro(macro);
			int newIndex = Outliner.macroPopup.addMacro(macro);
			
			// Update the list
			DefaultListModel model = null;
			if (macro instanceof SortMacro) {
				model = (DefaultListModel) frame.sortMacroList.getModel();
			} else {
				model = (DefaultListModel) frame.macroList.getModel();
			}
			
			model.remove(oldIndex);
			model.insertElementAt(macro.getName(), newIndex);
			
			// Save it to disk as a serialized object.
			deleteMacro(new File(Outliner.MACROS_DIR + oldName));
			saveMacro(macro);
		} else {
			JOptionPane.showMessageDialog(this, GUITreeLoader.reg.getText("message_an_error_occurred"));
		}
	}
	
	private void delete() {
		if (USER_ABORTED == promptUser(GUITreeLoader.reg.getText("message_do_you_really_want_to_delete_this_macro"))) {
			return;
		}
		
		if (macroConfig.delete()) {
			Macro macro = macroConfig.getMacro();
			
			// Remove it from the Popup Menu
			int index = Outliner.macroPopup.removeMacro(macro);
			
			// Remove it from the list in the MacroManager
			if (macro instanceof SortMacro) {
				((DefaultListModel) frame.sortMacroList.getModel()).remove(index);
			} else {
				((DefaultListModel) frame.macroList.getModel()).remove(index);
			}
			
			// Remove it from disk
			deleteMacro(new File(Outliner.MACROS_DIR + macro.getFileName()));
			
			hide();
		} else {
			JOptionPane.showMessageDialog(this, GUITreeLoader.reg.getText("message_an_error_occurred"));
		}
	}
	
	private void cancel() {
		if (!macroConfig.cancel()) {
			JOptionPane.showMessageDialog(this, GUITreeLoader.reg.getText("message_uh_oh"));
		}
		
		hide();
	}
	
	
	// Macro Saving and Loading Methods
	private void deleteMacro(File file) {
		file.delete();
		LoadMacroCommand.saveConfigFile(new File(Outliner.MACROS_FILE));
	}
	
	private void saveMacro(Macro macro) {
		macro.save(new File(Outliner.MACROS_DIR + macro.getFileName()));
		LoadMacroCommand.saveConfigFile(new File(Outliner.MACROS_FILE));
	}
	
	
	// Utility Methods
	private static int promptUser(String msg) {
		String yes = GUITreeLoader.reg.getText("yes");
		String no = GUITreeLoader.reg.getText("no");
		String confirm_delete = GUITreeLoader.reg.getText("confirm_delete");
		
		Object[] options = {yes, no};
		int result = JOptionPane.showOptionDialog(Outliner.macroManager.macroEditor,
			msg,
			confirm_delete,
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]
		);
		
		if (result == JOptionPane.NO_OPTION) {
			return USER_ABORTED;
		} else {
			return SUCCESS;
		}
	}
}

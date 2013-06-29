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
import org.xml.sax.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.1 $, $Date: 2004/01/20 23:01:42 $
 */

public class MacroManagerFrame extends AbstractGUITreeJDialog implements ActionListener {
	
	// Constants
	private static final int INITIAL_WIDTH = 275;
	private static final int INITIAL_HEIGHT = 400;
	private static final int MINIMUM_WIDTH = 275;
	private static final int MINIMUM_HEIGHT = 200;
	
	private static String NEW = null;
	
	
	// Define Fields and Buttons
	public MacroEditor macroEditor = null;
	
	protected ArrayList macroNames = new ArrayList();
	protected ArrayList macroClassNames = new ArrayList();
	
	private JButton newButton = null;
	protected JComboBox macroType = new JComboBox();
	
	private JLabel macroLabel = null;
	protected JList macroList = new JList();
	
	private JLabel sortMacroLabel = null;
	protected JList sortMacroList = new JList();
	
	
	// The Constructor
	public MacroManagerFrame() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		Outliner.macroManager = this;
		macroList.setModel(new DefaultListModel());
		sortMacroList.setModel(new DefaultListModel());
	}
	
	private void initialize() {
		NEW = GUITreeLoader.reg.getText("new");
		
		newButton = new JButton(NEW);
		macroLabel = new JLabel(GUITreeLoader.reg.getText("macros"));
		sortMacroLabel = new JLabel(GUITreeLoader.reg.getText("sort_macros"));
		
		// Define New Macro Pulldown area
		newButton.addActionListener(this);
		
		Box newBox = Box.createHorizontalBox();
		newBox.add(macroType);
		newBox.add(Box.createHorizontalStrut(5));
		newBox.add(newButton);
		
		// Define Macro Lists
		Box macroBox = Box.createVerticalBox();
		
		// Define Macro List
		macroBox.add(macroLabel);
		//macroList.setModel(new DefaultListModel());
		macroList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		macroList.addMouseListener(
			new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int index = macroList.locationToIndex(e.getPoint());
						DefaultListModel model = (DefaultListModel) macroList.getModel();
						updateMacro((String) model.get(index));
					}
				}
			}
		);
		
		JScrollPane jsp = new JScrollPane(macroList);
		
		macroBox.add(jsp);
		
		// Define SortMacro List
		macroBox.add(sortMacroLabel);
		//sortMacroList.setModel(new DefaultListModel());
		sortMacroList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		sortMacroList.addMouseListener(
			new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int index = sortMacroList.locationToIndex(e.getPoint());
						DefaultListModel model = (DefaultListModel) sortMacroList.getModel();
						updateMacro((String) model.get(index));
					}
				}
			}
		);
		
		JScrollPane jspSort = new JScrollPane(sortMacroList);
		
		macroBox.add(jspSort);
		
		// Put it all together
		getContentPane().add(newBox, BorderLayout.NORTH);
		getContentPane().add(macroBox, BorderLayout.CENTER);
	}
	
	private boolean initialized = false;
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	public void show() {
		// Lazy Instantiation
		if (!initialized) {
			initialize();
			initialized = true;
		}
		
		super.show();
	}
	
	private void updateMacro(String macroName) {
		Macro macro = Outliner.macroPopup.getMacro(macroName);
		
		if (macro != null) {
			displayMacroEditor(macro, MacroEditor.BUTTON_MODE_UPDATE);
		}
	}
	
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(NEW)) {
			newMacro();
		}
	}
	
	private void newMacro() {
		// Find the ClassName
		String className = getClassNameFromMacroTypeName((String) macroType.getSelectedItem());
		
		// Get the object
		try {
			Class theClass = Class.forName(className);
			Macro macro = (Macro) theClass.newInstance();
			displayMacroEditor(macro, MacroEditor.BUTTON_MODE_CREATE);
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void displayMacroEditor(Macro macro, int buttonMode) {
		MacroConfig macroConfig = (MacroConfig) macro.getConfigurator();
		macroConfig.init(macro);
		macroEditor.setMacroConfigAndShow(macroConfig, buttonMode);
	}
	
	
	// Utility Functions
	public String getClassNameFromMacroTypeName(String macroTypeName) {
		for (int i = 0, limit = macroNames.size(); i < limit; i++) {
			if (((String) macroNames.get(i)).equals(macroTypeName)) {
				return (String) macroClassNames.get(i);
			}
		}
		
		return null;
	}
	
	public String getMacroTypeNameFromClassName(String className) {
		for (int i = 0, limit = macroClassNames.size(); i < limit; i++) {
			if (((String) macroClassNames.get(i)).equals(className)) {
				return (String) macroNames.get(i);
			}
		}
		
		return null;
	}
}
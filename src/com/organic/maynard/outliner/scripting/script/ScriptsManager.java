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
 
package com.organic.maynard.outliner.scripting.script;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.guitree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.xml.sax.*;

public class ScriptsManager extends AbstractGUITreeJDialog implements ActionListener {
	
	// Constants
	private static final int INITIAL_WIDTH = 400;
	private static final int INITIAL_HEIGHT = 400;
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 300;
	
	
	// Pseudo Constants
	private static String NEW = null;
	
	
	// Instance Fields
	private boolean initialized = false;
	public ScriptsManagerModel model = new ScriptsManagerModel();
	public ThreadsTableModel threadsTableModel = new ThreadsTableModel();
	protected ScriptEditor scriptEditor = null;
	
	// Define Fields and Buttons
	protected ArrayList scriptNames = new ArrayList();
	protected ArrayList scriptClassNames = new ArrayList();
	private JButton newButton = null;
	protected JComboBox scriptType = new JComboBox();
	private JLabel scriptLabel = null;
	private JLabel threadLabel = null;
	
	
	// GUI Elements
	
	
	// The Constructors
	public ScriptsManager() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		Outliner.scriptsManager = this;
	}
	
	private void initialize() {
		NEW = GUITreeLoader.reg.getText("new");
		
		newButton = new JButton(NEW);
		scriptLabel = new JLabel(GUITreeLoader.reg.getText("script"));
		threadLabel = new JLabel(GUITreeLoader.reg.getText("thread"));
		
		// Define New Script Pulldown area
		newButton.addActionListener(this);
		
		Box newBox = Box.createHorizontalBox();
		newBox.add(scriptType);
		newBox.add(Box.createHorizontalStrut(5));
		newBox.add(newButton);
		
		// Setup Script Box
		Box scriptBox = Box.createVerticalBox();
		
		scriptBox.add(scriptLabel);
		scriptBox.add(new JScrollPane(new ScriptsTable()));
		
		scriptBox.add(Box.createVerticalStrut(5));
		
		scriptBox.add(threadLabel);
		scriptBox.add(new JScrollPane(new ThreadsTable()));
		
		// Put it all together
		getContentPane().add(newBox, BorderLayout.NORTH);
		getContentPane().add(scriptBox, BorderLayout.CENTER);
	}
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	
	// Methods
	public void show() {
		// Lazy Instantiation
		if (!initialized) {
			initialize();
			initialized = true;
		}
		
		super.show();
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(NEW)) {
			newScript();
		}
	}
	
	private void newScript() {
		// Find the ClassName
		String className = getClassNameFromScriptTypeName((String) scriptType.getSelectedItem());
		
		// Get the object
		try {
			Class theClass = Class.forName(className);
			Script script = (Script) theClass.newInstance();
			displayScriptEditor(script, ScriptEditor.BUTTON_MODE_CREATE);
		} catch (ClassNotFoundException cnfe) {
			System.out.println("Exception: " + className + " " + cnfe);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	protected void displayScriptEditor(Script script, int buttonMode) {
		ScriptConfig scriptConfig = (ScriptConfig) script.getScriptConfigurator();
		scriptConfig.init(script);
		scriptEditor.setScriptConfigAndShow(scriptConfig, buttonMode);
	}
	
	// Utility Functions
	public String getClassNameFromScriptTypeName(String scriptTypeName) {
		for (int i = 0, limit = scriptNames.size(); i < limit; i++) {
			if (((String) scriptNames.get(i)).equals(scriptTypeName)) {
				return (String) scriptClassNames.get(i);
			}
		}
		
		return null;
	}
	
	public String getScriptTypeNameFromClassName(String className) {
		for (int i = 0, limit = scriptClassNames.size(); i < limit; i++) {
			if (((String) scriptClassNames.get(i)).equals(className)) {
				return (String) scriptNames.get(i);
			}
		}
		
		return null;
	}
}

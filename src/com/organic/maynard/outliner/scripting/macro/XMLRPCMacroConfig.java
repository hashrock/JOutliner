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
import com.organic.maynard.outliner.menus.popup.*;
import com.organic.maynard.outliner.guitree.*;
import java.awt.*;
import javax.swing.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.1 $, $Date: 2004/01/20 23:01:42 $
 */

public class XMLRPCMacroConfig extends MacroConfigImpl {
	
	private static String URL = null;
	private static String DO_REPLACEMENT = null;
	private static String CALL = null;
	
	private JLabel nameLabel = null;
	private JLabel urlLabel = null;
	private JLabel doReplacementLabel = null;
	private JLabel callLabel = null;
	
	private JTextField nameField = new JTextField();
	private JTextField urlField = new JTextField();
	private JTextArea callTextArea = new JTextArea();
	
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton yesRadio = null;
	private JRadioButton noRadio = null;
	
	
	// The Constructor
	public XMLRPCMacroConfig() {
		super();
		
		URL = GUITreeLoader.reg.getText("url");
		DO_REPLACEMENT = GUITreeLoader.reg.getText("do_replacement");
		CALL = GUITreeLoader.reg.getText("xmlrpc_call");
		
		nameLabel = new JLabel(NAME);
		urlLabel = new JLabel(URL);
		doReplacementLabel = new JLabel(DO_REPLACEMENT);
		callLabel = new JLabel(CALL);
		
		yesRadio = new JRadioButton(GUITreeLoader.reg.getText("yes"));
		noRadio = new JRadioButton(GUITreeLoader.reg.getText("no"));
		
		
		// Create the layout
		this.setLayout(new BorderLayout());
		
		Insets insets = new Insets(1,3,1,3);
		nameField.setMargin(insets);
		urlField.setMargin(insets);
		
		// Prep the textarea
		callTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		callTextArea.setTabSize(2);
		callTextArea.setMargin(insets);
		JScrollPane callScrollPane = new JScrollPane(callTextArea);
		
		// Setup mainBox
		Box mainBox = Box.createVerticalBox();
		
		mainBox.add(nameLabel);
		mainBox.add(nameField);
		
		mainBox.add(Box.createVerticalStrut(5));
		
		mainBox.add(urlLabel);
		mainBox.add(urlField);
		
		mainBox.add(Box.createVerticalStrut(5));
		
		buttonGroup.add(yesRadio);
		buttonGroup.add(noRadio);
		
		Box radioBox = Box.createHorizontalBox();
		radioBox.add(doReplacementLabel);
		radioBox.add(Box.createHorizontalStrut(5));
		radioBox.add(yesRadio);
		radioBox.add(Box.createHorizontalStrut(5));
		radioBox.add(noRadio);
		
		mainBox.add(radioBox);
		
		mainBox.add(Box.createVerticalStrut(5));
		
		mainBox.add(callLabel);
		
		// Put it all together
		this.add(mainBox,BorderLayout.NORTH);
		this.add(callScrollPane,BorderLayout.CENTER);
	}
	
	
	// MacroConfig Interface
	public void init(Macro xmlrpcMacro) {
		super.init(xmlrpcMacro);
		
		XMLRPCMacro macro = (XMLRPCMacro) getMacro();
		
		nameField.setText(macro.getName());
		urlField.setText(macro.getURL());
		callTextArea.setText(macro.getCall());
		if (macro.isReplacing()) {
			yesRadio.setSelected(true);
		} else {
			noRadio.setSelected(true);
		}
	}
	
	public boolean create() {
		XMLRPCMacro macro = (XMLRPCMacro) getMacro();
		
		String name = nameField.getText();
		String url = urlField.getText();
		
		// Validate URL
		if (url.equals("")) {
			return false;
		}
		
		// Validate Name and do the create
		if (MacroPopupMenu.validateExistence(name) && MacroPopupMenu.validateUniqueness(name) && MacroPopupMenu.validateRestrictedChars(name)) {
			macro.setName(name);
			macro.setURL(url);
			macro.setCall(callTextArea.getText());
			if (yesRadio.isSelected()) {
				macro.setReplacing(true);
			} else {
				macro.setReplacing(false);
			}
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update() {
		XMLRPCMacro macro = (XMLRPCMacro) getMacro();
		
		String name = nameField.getText();
		String url = urlField.getText();
		
		// Validate URL
		if (url.equals("")) {
			return false;
		}
		
		// Validate Name and do the update
		if (MacroPopupMenu.validateExistence(name)) {
			if (name.equals(macro.getName())) {
				macro.setURL(url);
				macro.setCall(callTextArea.getText());
				if (yesRadio.isSelected()) {
					macro.setReplacing(true);
				} else {
					macro.setReplacing(false);
				}
				return true;
			} else if (MacroPopupMenu.validateUniqueness(name) && MacroPopupMenu.validateRestrictedChars(name)) {
				macro.setName(name);
				macro.setURL(url);
				macro.setCall(callTextArea.getText());
				if (yesRadio.isSelected()) {
					macro.setReplacing(true);
				} else {
					macro.setReplacing(false);
				}
				return true;
			}
		}
		return false;
	}
}

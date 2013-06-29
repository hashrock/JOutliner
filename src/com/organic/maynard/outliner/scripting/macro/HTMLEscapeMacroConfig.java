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
 
public class HTMLEscapeMacroConfig extends MacroConfigImpl {
	
	private JLabel nameLabel = null;
	private JTextField nameField = new JTextField();
	private ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton escapeRadio = null;
	private JRadioButton unescapeRadio = null;
	
	
	// The Constructor
	public HTMLEscapeMacroConfig() {
		super();
		
		nameLabel = new JLabel(NAME);
		escapeRadio = new JRadioButton(GUITreeLoader.reg.getText("escape"));
		unescapeRadio = new JRadioButton(GUITreeLoader.reg.getText("unescape"));
		
		// Create the layout
		this.setLayout(new BorderLayout());
		
		buttonGroup.add(escapeRadio);
		buttonGroup.add(unescapeRadio);
		
		Box radioBox = Box.createHorizontalBox();
		radioBox.add(escapeRadio);
		radioBox.add(Box.createHorizontalStrut(5));
		radioBox.add(unescapeRadio);
		
		Box mainBox = Box.createVerticalBox();
		mainBox.add(nameLabel);
		nameField.setMargin(new Insets(1,3,1,3));
		mainBox.add(nameField);
		mainBox.add(Box.createVerticalStrut(10));
		
		// Put it all together
		this.add(mainBox,BorderLayout.NORTH);
		this.add(radioBox,BorderLayout.CENTER);
	}
	
	
	// MacroConfig Interface
	public void init(Macro htmlEscapeMacro) {
		super.init(htmlEscapeMacro);
		
		HTMLEscapeMacro macro = (HTMLEscapeMacro) getMacro();
		
		if (macro.isEscaping()) {
			escapeRadio.setSelected(true);
		} else {
			unescapeRadio.setSelected(true);
		}
		nameField.setText(macro.getName());
	}
	
	public boolean create() {
		HTMLEscapeMacro macro = (HTMLEscapeMacro) getMacro();
		
		String name = nameField.getText();
		
		if (MacroPopupMenu.validateExistence(name) && MacroPopupMenu.validateUniqueness(name) && MacroPopupMenu.validateRestrictedChars(name)) {
			macro.setName(name);
			if (escapeRadio.isSelected()) {
				macro.setEscaping(true);
			} else {
				macro.setEscaping(false);
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update() {
		HTMLEscapeMacro macro = (HTMLEscapeMacro) getMacro();
		
		String name = nameField.getText();
		
		if (MacroPopupMenu.validateExistence(name)) {
			if (name.equals(macro.getName())) {
				if (escapeRadio.isSelected()) {
					macro.setEscaping(true);
				} else {
					macro.setEscaping(false);
				}
				
				return true;
			} else if (MacroPopupMenu.validateUniqueness(name) && MacroPopupMenu.validateRestrictedChars(name)) {
				macro.setName(name);
				if (escapeRadio.isSelected()) {
					macro.setEscaping(true);
				} else {
					macro.setEscaping(false);
				}
				
				return true;
			}
		}
		return false;
	}
}

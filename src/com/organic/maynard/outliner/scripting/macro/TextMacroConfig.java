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
import java.awt.event.*;
import javax.swing.*;
import com.organic.maynard.util.string.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.1 $, $Date: 2004/01/20 23:01:42 $
 */

public class TextMacroConfig extends MacroConfigImpl implements KeyListener {
	
	private static String PATTERN = null;
	
	private JLabel nameLabel = null;
	private JLabel patternLabel = null;
	
	private JTextField nameField = new JTextField();
	private JTextArea patternTextArea = new JTextArea();
	
	
	// The Constructor
	public TextMacroConfig() {
		super();
		
		PATTERN = GUITreeLoader.reg.getText("pattern");
		
		nameLabel = new JLabel(NAME);
		patternLabel = new JLabel(PATTERN);
		
		// Create the layout
		this.setLayout(new BorderLayout());
		
		Box mainBox = Box.createVerticalBox();
		mainBox.add(nameLabel);
		nameField.setMargin(new Insets(1,3,1,3));
		mainBox.add(nameField);
		mainBox.add(Box.createVerticalStrut(10));
		mainBox.add(patternLabel);
		
		// Prep the textarea
		patternTextArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		patternTextArea.setLineWrap(true);
		patternTextArea.setMargin(new Insets(1,3,1,3));
		patternTextArea.addKeyListener(this);
		
		JScrollPane patternScrollPane = new JScrollPane(patternTextArea);
		
		// Put it all together
		this.add(mainBox,BorderLayout.NORTH);
		this.add(patternScrollPane,BorderLayout.CENTER);
	}
	
	
	// MacroConfig Interface
	public void init(Macro macro) {
		super.init(macro);
		
		TextMacro textMacro = (TextMacro) getMacro();
		
		patternTextArea.setText(textMacro.getReplacementPattern());
		nameField.setText(textMacro.getName());
	}
	
	public boolean create() {
		TextMacro textMacro = (TextMacro) getMacro();
		
		String name = nameField.getText();
		
		if (MacroPopupMenu.validateExistence(name) && MacroPopupMenu.validateUniqueness(name) && MacroPopupMenu.validateRestrictedChars(name)) {
			textMacro.setName(name);
			textMacro.setReplacementPattern(patternTextArea.getText());
			return true;
		} else {
			return false;
		}
	}
	
	public boolean update() {
		TextMacro textMacro = (TextMacro) getMacro();
		
		String name = nameField.getText();
		
		if (MacroPopupMenu.validateExistence(name)) {
			if (name.equals(textMacro.getName())) {
				textMacro.setReplacementPattern(patternTextArea.getText());
				return true;
			} else if (MacroPopupMenu.validateUniqueness(name) && MacroPopupMenu.validateRestrictedChars(name)) {
				textMacro.setName(name);
				textMacro.setReplacementPattern(patternTextArea.getText());
				return true;
			}
		}
		return false;
	}
	
	
	// KeyListener Interface
	public void keyPressed(KeyEvent e) {
		if ((e.getKeyChar() == KeyEvent.VK_ENTER) || (e.getKeyChar() == KeyEvent.VK_TAB)) {
			e.consume();
			return;
		}
	}
	
	public void keyTyped(KeyEvent e) {}
	
	public void keyReleased(KeyEvent e) {
		if ((e.getKeyCode() == KeyEvent.VK_V) && e.isControlDown()) {
			String text = patternTextArea.getText();
			text = Replace.replace(text,"\t","");
			text = Replace.replace(text,"\n","");
			patternTextArea.setText(text);
		}
	}
}

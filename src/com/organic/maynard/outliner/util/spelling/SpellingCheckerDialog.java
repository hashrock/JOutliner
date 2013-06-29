/**
 * Copyright (C) 2002,2004 Maynard Demmon, maynard@organic.com
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
 
package com.organic.maynard.outliner.util.spelling;

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.util.undo.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import org.xml.sax.*;
import java.util.*;
import com.swabunga.spell.event.*;
import com.organic.maynard.util.string.Replace;

/**
 * A JDialog which enables a "spell checking session" on a document or selection
 * within a document. The spell checking is performed by a SpellingCheckerWrapper
 * which is spawned in a seperate Thread and which updates the GUI as misspelled
 * words are found.
 * 
 * @author  $Author: maynardd $
 * @version $Revision: 1.5 $, $Date: 2004/02/23 04:12:51 $
 */

public class SpellingCheckerDialog extends AbstractOutlinerJDialog implements ActionListener, JoeXMLConstants {
	
	// Constants
	private static final int MINIMUM_WIDTH = 425;
	private static final int MINIMUM_HEIGHT = 200;
	private static final int INITIAL_WIDTH = 425;
	private static final int INITIAL_HEIGHT = 200;
	
	
	// Instance Fields
	/** The Object through which we access the spell checker. */
	private SpellingCheckerWrapper spellChecker = null;
	
	/** 
	 * A flag which indicates that the spell checking thread should stop. This is
	 * the preferred method for terminating a Thread since it allows the Thread to
	 * finish what it's doing.
	 */
	private boolean stop_thread = false;
	
	/** The index of the current word we're checking. */
	private int word_index = 0;
	
	/** The current node being spell checked. */
	private Node current_node = null;
	
	/** The character offset within the current node's text. */
	private int current_offset_adj = 0;
	
	/** 
	 * A map of misspelled words and their replacements within the context of the
	 * current spell checking session which have had the "replace all" action
	 * performed on them by the user.
	 */
	private HashMap replace_list = null;
	
	/** 
	 * A map of skipped words within the context of the current spell checking 
	 * session which have had the "skip all" action performed on them by the user.
	 */
	private HashMap skip_list = null;
	
	/** 
	 * The undoable which captures all the text updates for the current spell
	 * checking session.
	 */
	private CompoundUndoableEdit undoable = null;
	
	
	// Main Component Containers
	private JScrollPane JSP_CONTEXT = null;
	private JScrollPane JSP_SUGGESTIONS = null;
	
	// Button Text and Other Copy
	private static String SKIP = null;
	private static String SKIP_ALL = null;
	private static String REPLACE = null;
	private static String REPLACE_ALL = null;
	private static String ADD_WORD = null;
	private static String DONE = null;
	private static String CANCEL = null;
	
	// Define Fields and Buttons
	private JLabel WORD = null;
	private JLabel STATUS = null;
	
	private JTextPane CONTEXT = null;
	private JTextField REPLACEMENT = null;
	private JList SUGGESTIONS = null;
	
	private JButton BUTTON_SKIP = null;
	private JButton BUTTON_SKIP_ALL = null;
	private JButton BUTTON_REPLACE = null;
	private JButton BUTTON_REPLACE_ALL = null;
	private JButton BUTTON_ADD_WORD = null;
	private JButton BUTTON_DONE = null;
	private JButton BUTTON_CANCEL = null;
	
	
	// The Constructor
	/**
	 * Constructs a new SpellingCheckerDialog and instantiates all the various GUI
	 * components within it.
	 */
	public SpellingCheckerDialog(SpellingCheckerWrapper spellChecker) {
		super(false, true, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		super.setTitle(GUITreeLoader.reg.getText("spelling_checker"));
		
		this.spellChecker = spellChecker;
		
		// Add a window listener to intercept close operations
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					cancel();
				}
			}
		);
		
		// Button Text and Other Copy
		SKIP = GUITreeLoader.reg.getText("skip");
		SKIP_ALL = GUITreeLoader.reg.getText("skip_all");
		REPLACE = GUITreeLoader.reg.getText("replace");
		REPLACE_ALL = GUITreeLoader.reg.getText("replace_all");
		ADD_WORD = GUITreeLoader.reg.getText("add_word");
		DONE = GUITreeLoader.reg.getText("done");
		CANCEL = GUITreeLoader.reg.getText("cancel");
		
		// Define Fields and Buttons
		STATUS = new JLabel();
		WORD = new JLabel("Misspelt Word: ");
		
		CONTEXT = new JTextPane();
		StyledDocument doc = CONTEXT.getStyledDocument();
		Style style = CONTEXT.addStyle("Red", null);
		StyleConstants.setForeground(style, Color.red);
		StyleConstants.setBold(style, true);
		CONTEXT.setEditable(false);
		CONTEXT.setEnabled(false);
		JSP_CONTEXT = new JScrollPane(CONTEXT);
		CONTEXT.setToolTipText(GUITreeLoader.reg.getText("tooltip_spellcheck_context"));
		
		SUGGESTIONS = new JList();
		SUGGESTIONS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		SUGGESTIONS.addListSelectionListener(
			new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					Object value = SUGGESTIONS.getSelectedValue();
					if (value != null) {
						REPLACEMENT.setText(value.toString());
					}
				}
			}
		);
		JSP_SUGGESTIONS = new JScrollPane(SUGGESTIONS);
		SUGGESTIONS.setToolTipText(GUITreeLoader.reg.getText("tooltip_spellcheck_suggestions"));
		
		REPLACEMENT = new JTextField();
		REPLACEMENT.setToolTipText(GUITreeLoader.reg.getText("tooltip_spellcheck_replacement"));
		
		BUTTON_SKIP = new JButton(SKIP);
		BUTTON_SKIP.addActionListener(this);
		BUTTON_SKIP.setToolTipText(GUITreeLoader.reg.getText("tooltip_spellcheck_skip"));
		
		BUTTON_SKIP_ALL = new JButton(SKIP_ALL);
		BUTTON_SKIP_ALL.addActionListener(this);
		BUTTON_SKIP_ALL.setToolTipText(GUITreeLoader.reg.getText("tooltip_spellcheck_skipall"));
		
		BUTTON_REPLACE = new JButton(REPLACE);
		BUTTON_REPLACE.addActionListener(this);
		BUTTON_REPLACE.setToolTipText(GUITreeLoader.reg.getText("tooltip_spellcheck_replace"));
		
		BUTTON_REPLACE_ALL = new JButton(REPLACE_ALL);
		BUTTON_REPLACE_ALL.addActionListener(this);
		BUTTON_REPLACE_ALL.setToolTipText(GUITreeLoader.reg.getText("tooltip_spellcheck_replaceall"));
		
		BUTTON_ADD_WORD = new JButton(ADD_WORD);
		BUTTON_ADD_WORD.addActionListener(this);
		BUTTON_ADD_WORD.setToolTipText(GUITreeLoader.reg.getText("tooltip_spellcheck_addword"));
		
		BUTTON_DONE = new JButton(DONE);
		BUTTON_DONE.addActionListener(this);
		BUTTON_DONE.setToolTipText(GUITreeLoader.reg.getText("tooltip_spellcheck_done"));
		
		BUTTON_CANCEL = new JButton(CANCEL);
		BUTTON_CANCEL.addActionListener(this);
		BUTTON_CANCEL.setToolTipText(GUITreeLoader.reg.getText("tooltip_spellcheck_cancel"));
		
		getRootPane().setDefaultButton(BUTTON_REPLACE);
		
		// Add Components
			GridBagConstraints c = new GridBagConstraints();
			
			// Main Panel
			JPanel main_panel = new JPanel();
			GridBagLayout main_layout = new GridBagLayout();
			main_panel.setLayout(main_layout);
			
			c.weighty = 0;
			c.weightx = 1;
			c.anchor = GridBagConstraints.WEST;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.NONE;
			main_layout.setConstraints(STATUS, c);
			main_panel.add(STATUS);
			
			c.weighty = 1;
			c.weightx = 1;
			c.anchor = GridBagConstraints.WEST;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.BOTH;
			main_layout.setConstraints(JSP_CONTEXT, c);
			main_panel.add(JSP_CONTEXT);
			
			c.weighty = 0;
			c.weightx = 1;
			c.anchor = GridBagConstraints.WEST;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.NONE;
			main_layout.setConstraints(WORD, c);
			main_panel.add(WORD);
			
			c.weighty = 0;
			c.weightx = 1;
			c.anchor = GridBagConstraints.WEST;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
			main_layout.setConstraints(REPLACEMENT, c);
			main_panel.add(REPLACEMENT);
			
			c.weighty = 1;
			c.weightx = 1;
			c.anchor = GridBagConstraints.WEST;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.BOTH;
			main_layout.setConstraints(JSP_SUGGESTIONS, c);
			main_panel.add(JSP_SUGGESTIONS);
			
			// Button Panel
			JPanel button_panel = new JPanel();
			GridBagLayout button_layout = new GridBagLayout();
			button_panel.setLayout(button_layout);
			
			c.weighty = 0;
			c.weightx = 1;
			c.anchor = GridBagConstraints.WEST;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
			button_layout.setConstraints(BUTTON_SKIP, c);
			button_panel.add(BUTTON_SKIP);
			button_layout.setConstraints(BUTTON_SKIP_ALL, c);
			button_panel.add(BUTTON_SKIP_ALL);
			button_layout.setConstraints(BUTTON_REPLACE, c);
			button_panel.add(BUTTON_REPLACE);
			button_layout.setConstraints(BUTTON_REPLACE_ALL, c);
			button_panel.add(BUTTON_REPLACE_ALL);
			button_layout.setConstraints(BUTTON_ADD_WORD, c);
			button_panel.add(BUTTON_ADD_WORD);
			button_layout.setConstraints(BUTTON_DONE, c);
			button_panel.add(BUTTON_DONE);
			button_layout.setConstraints(BUTTON_CANCEL, c);
			button_panel.add(BUTTON_CANCEL);
			
			// Outer Layout
			JPanel outer_panel = new JPanel();
			GridBagLayout outer_layout = new GridBagLayout();
			outer_panel.setLayout(outer_layout);
			
			c.weighty = 1;
			c.weightx = 1;
			c.anchor = GridBagConstraints.NORTH;
			c.gridwidth = 1;
			c.fill = GridBagConstraints.BOTH;
			outer_layout.setConstraints(main_panel, c);
			outer_panel.add(main_panel);
			
			c.weighty = 0;
			c.weightx = 0;
			c.anchor = GridBagConstraints.NORTH;
			c.gridwidth = GridBagConstraints.REMAINDER;
			c.fill = GridBagConstraints.HORIZONTAL;
			outer_layout.setConstraints(button_panel, c);
			outer_panel.add(button_panel);
			
			getContentPane().add(outer_panel, BorderLayout.CENTER);
			
			pack();
	}
	
	/**
	 * Tests if the spell checking thread has been flagged to stop.
	 */
	public boolean shouldStop() {
		return this.stop_thread;
	}
	
	/**
	 * Causes the spell checking thread to terminate the next time it
	 * checks the stop_thread flag.
	 */
	public void stop() {
		this.stop_thread = true;
	}
	
	/**
	 * Resets this SpellingCheckerDialog so that it can be reused.
	 */
	public void reset() {
		stop_thread = false;
		word_index = 0;
		current_offset_adj = 0;
		skip_list = new HashMap();
		replace_list = new HashMap();
		spellChecker.reset();
		
		OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
		undoable = new CompoundUndoableEdit(doc.tree);
		
		updateGUI();
	}
	
	/**
	 * Starts spell checking the current document as indicated by the 
	 * DocumentRepository. Spawns a spell checking thread which
	 * updates the GUI as it finds misspelled words.
	 */
	public void checkSpellingForDocument() {
		reset();
		Thread t = new Thread(new Runnable() {
			public void run() {
				OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
				Outliner.spellChecker.checkSpellingForDocument(doc);
				Outliner.spellChecker.getDialog().stop();
				Outliner.spellChecker.getDialog().updateButtons();
			}
		});
		t.start();
		show();
	}
	
	/**
	 * Starts spell checking the current selection within the current document as 
	 * indicated by the DocumentRepository. Spawns a spell checking thread which
	 * updates the GUI as it finds misspelled words.
	 */
	public void checkSpellingForSelection() {
		reset();
		Thread t = new Thread(new Runnable() {
			public void run() {
				OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
				Outliner.spellChecker.checkSpellingForSelection(doc);
				Outliner.spellChecker.getDialog().stop();
				Outliner.spellChecker.getDialog().updateButtons();
			}
		});
		t.start();
		show();
	}
	
	
	// ActionListener Interface
	/**
	 * Handles user actions on the various buttons within this Dialog.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(DONE)) {
			done();
		} else if (e.getActionCommand().equals(SKIP)) {
			skip();
		} else if (e.getActionCommand().equals(SKIP_ALL)) {
			skip_all();
		} else if (e.getActionCommand().equals(REPLACE)) {
			replace();
		} else if (e.getActionCommand().equals(REPLACE_ALL)) {
			replace_all();
		} else if (e.getActionCommand().equals(ADD_WORD)) {
			add_word();
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		}
	}
	
	/**
	 * Action triggered by the user clicking on the "add word" button in this Dialog.
	 */
	private void add_word() {
		String word = REPLACEMENT.getText();
		String yes = GUITreeLoader.reg.getText("yes");
		String no = GUITreeLoader.reg.getText("no");
		String confirm_addword = GUITreeLoader.reg.getText("confirm_addword");
		String msg = GUITreeLoader.reg.getText("confirmation_msg_addword");
		msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, word);
		
		Object[] options = {yes, no};
		int result = JOptionPane.showOptionDialog(this,
			msg,
			confirm_addword,
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			options,
			options[0]
		);
		if (result == JOptionPane.YES_OPTION) {
			spellChecker.addWord(word);
		} else if (result == JOptionPane.NO_OPTION) {
			return;
		} else {
			return;
		}
	}
	
	/**
	 * Action triggered by the user clicking on the "cancel" button in this Dialog.
	 * Causes the spell checking thread to stop, discards the undoable which
	 * contains any text changes and hides the Dialog.
	 */
	private void cancel() {
		this.stop();
		
		if (!undoable.isEmpty()) {
			undoable.undo();
		}
		
		hide();
	}
	
	/**
	 * Action triggered by the user clicking on the "done" button in this Dialog.
	 * Causes the spell checking thread to stop, applys the undoable to the current
	 * document and hides the Dialog.
	 */
	private void done() {
		this.stop();
		
		OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
		if (!undoable.isEmpty()) {
			doc.getUndoQueue().add(undoable);
		}
		doc.panel.layout.redraw();
		hide();
	}
	
	/**
	 * Action triggered by the user clicking on the "skip" button in this Dialog.
	 * Skips the current misspelled word.
	 */
	private void skip() {
		this.word_index++;
		
		Node node = this.spellChecker.getMisspeltWordNode(word_index);
		updateDifference(node, 0);
		
		boolean success = updateGUI();
		if (!success) {
			this.word_index--;
			if (!this.stop_thread) {
				updateGUI();
			}
		} else {
			SpellCheckEvent event = this.spellChecker.getMisspeltWord(word_index);
			
			String word = event.getInvalidWord();
			if (skip_list.get(word) != null) {
				skip();
			}
			if (replace_list.get(word) != null) {
				replace();
			}
		}
	}
	
	/**
	 * Action triggered by the user clicking on the "skip all" button in this Dialog.
	 * Skips the current misspelled word and all other instances of it within the
	 * current spell checking session.
	 */
	private void skip_all() {
		SpellCheckEvent event = this.spellChecker.getMisspeltWord(word_index);
		String word = event.getInvalidWord();
		skip_list.put(word,word);
		
		skip();
	}
	
	/**
	 * Action triggered by the user clicking on the "replace" button in this Dialog.
	 * Replaces the current misspelled word with the value selected in the 
	 * suggestions combobox.
	 */
	private void replace() {
		SpellCheckEvent event = this.spellChecker.getMisspeltWord(word_index);
		String word = event.getInvalidWord();
		
		// Look for a value for "replace all"
		String replacement = (String) replace_list.get(word);
		if (replacement == null) {
			replacement = REPLACEMENT.getText();
		}
		
		Node node = this.spellChecker.getMisspeltWordNode(word_index);
		
		int difference = replacement.length() - word.length();
		updateDifference(node, difference);
		
		int offset = this.spellChecker.getMisspeltWordOffset(word_index) + event.getWordContextPosition() + current_offset_adj - difference;
		
		String old_value = node.getValue();
		String new_value = old_value.substring(0,offset) + replacement + old_value.substring(offset + word.length(),old_value.length());
		
		node.setValue(new_value);
		
		undoable.addPrimitive(new PrimitiveUndoableEdit(node, old_value, new_value));
		
		//System.out.println("REPLACEMENT: " + replacement);
		
		skip();
	}
	
	/**
	 * Updates the current offset based on the difference in lengths between the
	 * misspelled word and it's replacement. Also updates the current node if
	 * that has changed.
	 */
	private void updateDifference(Node node, int difference) {
		if (current_node == null) {
			current_node = node;
			current_offset_adj = difference;
		} else if (current_node == node) {
			current_offset_adj += difference;
		} else if (current_node != node) {
			current_node = node;
			current_offset_adj = difference;
		}
	}
	
	/**
	 * Action triggered by the user clicking on the "replace all" button in this Dialog.
	 * Replaces the current misspelled word with the value selected in the 
	 * suggestions combobox and all other instances of the misspelled word within
	 * the current spell checking session.
	 */
	private void replace_all() {
		SpellCheckEvent event = this.spellChecker.getMisspeltWord(word_index);
		String word = event.getInvalidWord();
		String replacement = REPLACEMENT.getText();
		replace_list.put(word,replacement);
		
		replace();
	}
	
	/**
	 * Updates the GUI after each replacement or skip. This method is protected
	 * because the spell checking thread needs to be able to access it.
	 */
	protected boolean updateGUI() {
		SpellCheckEvent event = this.spellChecker.getMisspeltWord(word_index);
		updateButtons();
		if (event == null) {
			// Update Word Display
			WORD.setText("Misspelt Word: ");
			
			// Set Replacement Text
			REPLACEMENT.setText("");
			
			// Update Suggestions List
			SUGGESTIONS.clearSelection();
			Object[] list_data = new Object[0];
			SUGGESTIONS.setListData(list_data);
			
			// Update Context TextArea
			CONTEXT.setText("");
			
			return false;
		} else {
			// Update Word Display
			String word = event.getInvalidWord();
			WORD.setText("Misspelled Word: " + word);
			
			// Set Replacement Text
			REPLACEMENT.setText(word);
			
			// Update Suggestions List
			SUGGESTIONS.clearSelection();
			java.util.List suggestions = event.getSuggestions();
			Object[] list_data = new Object[suggestions.size()];
			for (int i = 0; i < suggestions.size(); i++) {
				list_data[i] = suggestions.get(i);
			}
			SUGGESTIONS.setListData(list_data);
			
			// Update TextArea
			Node node = this.spellChecker.getMisspeltWordNode(word_index);
			int offset = this.spellChecker.getMisspeltWordOffset(word_index);
			CONTEXT.setText(node.getValue());
			//CONTEXT.setText(event.getWordContext());
			//int position = event.getWordContextPosition();
			int position = offset + event.getWordContextPosition() + current_offset_adj;
			
			StyledDocument doc = CONTEXT.getStyledDocument();
			doc.setCharacterAttributes(position, word.length(), CONTEXT.getStyle("Red"), true);
			
			return true;
		}
	}
	
	/**
	 * Updates the buttons and text label based on current word count, 
	 * thread state, etc. This method is protected
	 * because the spell checking thread needs to be able to access it.
	 */
	protected void updateButtons() {
		int current_word_count = spellChecker.getCurrentWordCount();
		StringBuffer text = new StringBuffer();
		if (this.stop_thread) {
			text.append("Done finding misspelled words. ");
		} else {
			text.append("Spellcheck in progress. ");
		}
		
		if (word_index >= current_word_count) {
			STATUS.setText(text.append("Done with spellcheck.").toString());
			BUTTON_SKIP.setEnabled(false);
			BUTTON_SKIP_ALL.setEnabled(false);
			BUTTON_REPLACE.setEnabled(false);
			BUTTON_REPLACE_ALL.setEnabled(false);
			BUTTON_ADD_WORD.setEnabled(false);
		} else {
			STATUS.setText(text.append(word_index + 1).append(" of ").append(current_word_count).append(".").toString());
			BUTTON_SKIP.setEnabled(true);
			BUTTON_SKIP_ALL.setEnabled(true);
			BUTTON_REPLACE.setEnabled(true);
			BUTTON_REPLACE_ALL.setEnabled(true);
			BUTTON_ADD_WORD.setEnabled(true);
		}
	}
}
/**
 * Copyright (C) 2002 Maynard Demmon, maynard@organic.com
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

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.util.preferences.*;
import java.util.*;
import java.io.*;
import com.swabunga.spell.event.*;
import com.swabunga.spell.engine.*;

/**
 * A wrapper around the Jazzy spell checker.
 * 
 * @author  $Author: maynardd $
 * @version $Revision: 1.6 $, $Date: 2004/02/23 04:12:51 $
 */
 
public class SpellingCheckerWrapper implements SpellCheckListener {
	
	// Preference Key Constants
	private static final String IGNOREDIGITWORDS = "ignore_digit_words";
	private static final String IGNOREINTERNETADDRESSES = "ignore_inetadd";
	private static final String IGNOREMIXEDCASE = "ignore_mixed_case";
	private static final String IGNOREMULTIPLEWORDS = "ignore_multiple_words";
	private static final String IGNORESENTENCECAPITALIZATION = "ignore_sentence_cap";
	private static final String IGNOREUPPERCASE = "ignore_upper_case";
	
	/** 
	 * Controls how long we should sleep since we'll be running in our own thread. 
	 */
	private static final int SLEEP_THROTTLE = 25;
	
	// Pseudo Constants
	public static String DICTIONARIES_DIR = new StringBuffer().append(Outliner.PREFS_DIR).append(Outliner.FILE_SEPARATOR).append("dict").append(Outliner.FILE_SEPARATOR).toString();
	
	
	// Instance fields
	private SpellingCheckerDialog dialog = null;
	private SpellChecker spellCheck = null;
	private SpellDictionaryHashMap dictionary = null;
	private boolean is_initialized = false;
	
	private int sleep_count = 0;
	
	private Node current_node = null;
	private int current_offset = 0;
	
	private ArrayList nodes = new ArrayList();
	private ArrayList offsets = new ArrayList();
	private ArrayList misspelt_words = new ArrayList();
	private boolean found_a_misspelling = false;
	
	
	// Constructor
	/**
	 * Constructs a new SpellingCheckerWrapper.
	 */
	public SpellingCheckerWrapper() {
		super();
	};
	
	
	/**
	 * Initializes this SpellingCheckerWrapper since we're doing lazy
	 * instantiation.
	 */
	private boolean init() {
		if (!this.is_initialized) {
			try {
				this.dialog = new SpellingCheckerDialog(this);
				
				reloadDictionaries();
				
				this.is_initialized = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return is_initialized;
	}
	
	/**
	 * Reloads the word dictionaries which the spell checker uses to determine
	 * what is misspelled.
	 */
	private void reloadDictionaries() {
		try {
			File dictonaries_dir = new File(DICTIONARIES_DIR);
			File[] dictionaryFiles = dictonaries_dir.listFiles();
			
			// Load the user specific dictionary. We do this first since it will
			// cause it to be the file where added words are saved to.
			File user_dictionary_file = new File(Outliner.ADDED_WORDS_FILE);
			if (user_dictionary_file.isFile() && user_dictionary_file.canRead()) {
				System.out.println("Loading Dictionary File: " + user_dictionary_file.getName());
				this.dictionary = new SpellDictionaryHashMap(user_dictionary_file);
			}
			
			for (int i = 0; i < dictionaryFiles.length; i++) {
				File dictionary_file = dictionaryFiles[i];
				if (dictionary_file.isFile() && dictionary_file.canRead() && dictionary_file.getName().endsWith(".dict")) {
					System.out.println("Loading Dictionary File: " + dictionary_file.getName());
					this.dictionary.createDictionary(new BufferedReader(new FileReader(dictionary_file)));
				}
			}
			
			this.spellCheck = new SpellChecker(this.dictionary);
			this.spellCheck.addSpellCheckListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Accessors
	/**
	 * Gets the SpellingCheckerDialog which spawned this thread.
	 */
	public SpellingCheckerDialog getDialog() {
		if (init()) {
			return this.dialog;
		} else {
			return null;
		}
	}
	
	// Wrapper Methods
	/**
	 * Checks the spelling on a selection within a document.
	 */
	public void checkSpellingForSelection(OutlinerDocument doc) {
		if (init()) {
			// Configure the spellchecker
			Configuration config = Configuration.getConfiguration();
			config.setBoolean(Configuration.SPELL_IGNOREDIGITWORDS, Preferences.getPreferenceBoolean(IGNOREDIGITWORDS).cur);
			config.setBoolean(Configuration.SPELL_IGNOREINTERNETADDRESSES, Preferences.getPreferenceBoolean(IGNOREINTERNETADDRESSES).cur);
			config.setBoolean(Configuration.SPELL_IGNOREMIXEDCASE, Preferences.getPreferenceBoolean(IGNOREMIXEDCASE).cur);
			config.setBoolean(Configuration.SPELL_IGNOREMULTIPLEWORDS, Preferences.getPreferenceBoolean(IGNOREMULTIPLEWORDS).cur);
			config.setBoolean(Configuration.SPELL_IGNORESENTENCECAPITALIZATION, Preferences.getPreferenceBoolean(IGNORESENTENCECAPITALIZATION).cur);
			config.setBoolean(Configuration.SPELL_IGNOREUPPERCASE, Preferences.getPreferenceBoolean(IGNOREUPPERCASE).cur);
			
			// Do the spellcheck
			TreeContext tree = (TreeContext) doc.getTree();
			if (tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				Node node = tree.getEditingNode();
				int cursor = tree.getCursorPosition();
				int mark = tree.getCursorMarkPosition();
				String text = node.getValue().substring(Math.min(cursor, mark), Math.max(cursor, mark));
				this.current_node = node;
				this.current_offset = Math.min(cursor, mark);
				spellCheck.checkSpelling(new StringWordTokenizer(text));
			} else {
				for (int i = 0; i < tree.getSelectedNodes().size(); i++) {
					if (this.dialog.shouldStop()) {
						return;
					}
					checkSpelling(tree.getSelectedNodes().get(i));
				}
			}
		}
	}
	
	/**
	 * Checks the spelling on an entire document.
	 */
	public void checkSpellingForDocument(OutlinerDocument doc) {
		if (init()) {
			// Configure the spellchecker
			Configuration config = Configuration.getConfiguration();
			config.setBoolean(Configuration.SPELL_IGNOREDIGITWORDS, Preferences.getPreferenceBoolean(IGNOREDIGITWORDS).cur);
			config.setBoolean(Configuration.SPELL_IGNOREINTERNETADDRESSES, Preferences.getPreferenceBoolean(IGNOREINTERNETADDRESSES).cur);
			config.setBoolean(Configuration.SPELL_IGNOREMIXEDCASE, Preferences.getPreferenceBoolean(IGNOREMIXEDCASE).cur);
			config.setBoolean(Configuration.SPELL_IGNOREMULTIPLEWORDS, Preferences.getPreferenceBoolean(IGNOREMULTIPLEWORDS).cur);
			config.setBoolean(Configuration.SPELL_IGNORESENTENCECAPITALIZATION, Preferences.getPreferenceBoolean(IGNORESENTENCECAPITALIZATION).cur);
			config.setBoolean(Configuration.SPELL_IGNOREUPPERCASE, Preferences.getPreferenceBoolean(IGNOREUPPERCASE).cur);
			
			// Do the spellcheck
			TreeContext tree = (TreeContext) doc.getTree();
			checkSpelling(tree.getRootNode());
		}
	}
	
	/**
	 * Checks the spelling on a node. Called by each of the public spell checking
	 * methods repeatedly as they check their respective scopes.
	 */
	private void checkSpelling(Node node) {
		if (node == null) {
			return;
		}
		this.current_node = node;
		this.current_offset = 0;
		spellCheck.checkSpelling(new StringWordTokenizer(node.getValue()));
		
		if (sleep_count++ >= SLEEP_THROTTLE) {
			// Makes sure other threads get a chance to update themselves.
			// The use of the throttle keeps us from sleeping too much.
			try {Thread.sleep(0);} catch (InterruptedException e) {}
			sleep_count = 0;
		}
		
		for (int i = 0; i < node.numOfChildren(); i++) {
			if (this.dialog.shouldStop()) {
				return;
			}
			Node child = node.getChild(i);
			checkSpelling(child);
		}
	}
	
	// DictionaryManagement
	/**
	 * Adds a word to the dictionary.
	 */
	public void addWord(String word) {
		// Only add words that do not exist in the dictionary yet.
		if (!dictionary.isCorrect(word)) {
			dictionary.addWord(word);
		}
	}
	
	
	// SpellCheckListener Interface and related methods.
	/**
	 * Gets the nth misspelled word.
	 */
	public SpellCheckEvent getMisspeltWord(int i) {
		if (i < misspelt_words.size()) {
			return (SpellCheckEvent) misspelt_words.get(i);
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the node within which the nth misspelled word was located.
	 */
	public Node getMisspeltWordNode(int i) {
		if (i < misspelt_words.size()) {
			return (Node) nodes.get(i);
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the character offset within the node which contains the nth
	 * misspelled word.
	 */
	public int getMisspeltWordOffset(int i) {
		if (i < misspelt_words.size()) {
			return ((Integer) offsets.get(i)).intValue();
		} else {
			return -1;
		}
	}
	
	/**
	 * Gets the current number of misspelled words found.
	 */
	public int getCurrentWordCount() {
		return this.misspelt_words.size();
	}
	
	/**
	 * Resets this SpellingCheckerWrapper so that it can be reused.
	 */
	public void reset() {
		this.sleep_count = 0;
		this.nodes.clear();
		this.current_node = null;
		this.offsets.clear();
		this.current_offset = 0;
		this.misspelt_words.clear();
		this.found_a_misspelling = false;
	}
	
	/**
	 * Triggered whenever a spelling error occurs. Handles updates to the GUI and
	 * various fields which track the progress of the spell checking session.
	 */
	public void spellingError(SpellCheckEvent event) {
		if (this.dialog.shouldStop()) {
			return;
		}
		
		this.misspelt_words.add(event);
		this.nodes.add(this.current_node);
		this.offsets.add(new Integer(this.current_offset));
		
		if (!this.found_a_misspelling) {
			// We need this to trigger the gui when we find our first word.
			this.found_a_misspelling = true;
			this.dialog.updateGUI();
		} else {
			this.dialog.updateButtons();
		}
	}
}
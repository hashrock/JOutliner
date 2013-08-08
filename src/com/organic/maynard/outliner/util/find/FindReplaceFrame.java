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

package com.organic.maynard.outliner.util.find;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.event.DocumentRepositoryEvent;
import com.organic.maynard.outliner.event.DocumentRepositoryListener;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.menus.window.WindowMenu;
import com.organic.maynard.outliner.util.ProgressDialog;
import com.organic.maynard.outliner.util.undo.*;
import com.organic.maynard.util.crawler.*;
import com.organic.maynard.util.string.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import org.apache.oro.text.MalformedCachePatternException;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.PatternMatcherInput;
import org.apache.oro.text.regex.Perl5Compiler;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.29 $, $Date: 2004/03/21 22:57:36 $
 */

public class FindReplaceFrame extends AbstractGUITreeJDialog implements JoeReturnCodes, DocumentRepositoryListener, ActionListener, KeyListener, ListSelectionListener {
	
	// Constants
	private static final int MINIMUM_WIDTH = 550;
	private static final int MINIMUM_HEIGHT = 600;
 	private static final int INITIAL_WIDTH = 550;
	private static final int INITIAL_HEIGHT = 600;
	
	private static final String REGEX_MATCH_START = "m/";
	private static final String REGEX_MATCH_END = "/";
	private static final String REGEX_MATCH_END_IGNORE_CASE = "/i";
	
	private static final String REGEX_REPLACE_START = "s/";
	private static final String REGEX_REPLACE_MIDDLE = "/";
	private static final String REGEX_REPLACE_END = "/";
	private static final String REGEX_REPLACE_END_IGNORE_CASE = "/i";
	
	
	// Pseudo Constants
	// Perl Regex
	private static Perl5Util util = null;
	private static PatternMatcherInput input = null;
	private static MatchResult result = null;
	private static Perl5Compiler compiler = null;
	
	
	// Button Text and Other Copy
	private static String FIND = null;
	private static String FIND_ALL = null;
	private static String REPLACE = null;
	private static String REPLACE_ALL = null;
	
	private static String NEW = null;
	private static String DELETE = null;
	
	private static String START_AT_TOP = null;
	private static String WRAP_ARROUND = null;
	private static String SELECTION_ONLY = null;
	private static String IGNORE_CASE = null;
	private static String INCLUDE_READ_ONLY_NODES = null;
	private static String INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS = null;
	private static String REGEXP = null;
	
	private static String CURRENT_DOCUMENT = GUITreeLoader.reg.getText("current_document");
	private static String ALL_OPEN_DOCUMENTS = GUITreeLoader.reg.getText("all_open_documents");
	private static String FILE_SYSTEM = GUITreeLoader.reg.getText("file_system");
	
	private static String PATH = GUITreeLoader.reg.getText("path");
	private static String SELECT = GUITreeLoader.reg.getText("select");
	private static String SELECT_DOTS = SELECT + GUITreeLoader.reg.getText("ellipsis");
	private static String INCLUDE_SUB_DIRECTORIES = GUITreeLoader.reg.getText("include_sub_directories");
	private static String MAKE_BACKUPS = GUITreeLoader.reg.getText("make_backups");
	private static String FILE_FILTER = GUITreeLoader.reg.getText("file_filter");
	private static String DIR_FILTER = GUITreeLoader.reg.getText("dir_filter");
	private static String INCLUDE = GUITreeLoader.reg.getText("include");
	private static String EXCLUDE = GUITreeLoader.reg.getText("exclude");
	
	private static String FILE_FILTER_INCLUDE = "file_filter_include";
	private static String FILE_FILTER_INCLUDE_IGNORE_CASE = "file_filter_include_ignore_case";
	private static String FILE_FILTER_EXCLUDE = "file_filter_exclude";
	private static String FILE_FILTER_EXCLUDE_IGNORE_CASE = "file_filter_exclude_ignore_case";
	private static String DIR_FILTER_INCLUDE = "dir_filter_include";
	private static String DIR_FILTER_INCLUDE_IGNORE_CASE ="dir_filter_include_ignore_case";
	private static String DIR_FILTER_EXCLUDE = "dir_filter_exclude";
	private static String DIR_FILTER_EXCLUDE_IGNORE_CASE = "dir_filter_exclude_ignore_case";
	
	// ToolTip Text
	private static String TT_FILTER = GUITreeLoader.reg.getText("instruction_type_globs");
	
	// Define Fields and Buttons
	private static JTabbedPane tabs;
	private static Component tabDocument;
	private static Component tabAllDocuments;
	private static Component tabFileSystem;
	
	private static JTextArea TEXTAREA_FIND = null;
	private static JTextArea TEXTAREA_REPLACE = null;
	
	private static JCheckBox CHECKBOX_START_AT_TOP = null;
	private static JCheckBox CHECKBOX_WRAP_AROUND = null;
	private static JCheckBox CHECKBOX_SELECTION_ONLY = null;
	private static JCheckBox CHECKBOX_IGNORE_CASE = null;
	private static JCheckBox CHECKBOX_INCLUDE_READ_ONLY_NODES = null;
	private static JCheckBox CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS = null;
	private static JCheckBox CHECKBOX_REGEXP = null;
	
	private static JLabel LABEL_PATH = null;
	private static JTextField TEXTFIELD_PATH = null;
	private static JButton BUTTON_SELECT = null;
	private static JCheckBox CHECKBOX_INCLUDE_SUB_DIRECTORIES = null;
	private static JCheckBox CHECKBOX_MAKE_BACKUPS = null;
	
	private static JLabel LABEL_FILE_FILTER_INCLUDE = null;
	private static JLabel LABEL_FILE_FILTER_EXCLUDE = null;
	private static JTextField TEXTFIELD_FILE_FILTER_INCLUDE = null;
	private static JTextField TEXTFIELD_FILE_FILTER_EXCLUDE = null;
	private static JCheckBox CHECKBOX_FILE_FILTER_INCLUDE_IGNORE_CASE = null;
	private static JCheckBox CHECKBOX_FILE_FILTER_EXCLUDE_IGNORE_CASE = null;
	
	private static JLabel LABEL_DIR_FILTER_INCLUDE = null;
	private static JLabel LABEL_DIR_FILTER_EXCLUDE = null;
	private static JTextField TEXTFIELD_DIR_FILTER_INCLUDE = null;
	private static JTextField TEXTFIELD_DIR_FILTER_EXCLUDE = null;
	private static JCheckBox CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE = null;
	private static JCheckBox CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE = null;
	
	private static JButton BUTTON_FIND = null;
	private static JButton BUTTON_FIND_ALL = null;
	private static JButton BUTTON_REPLACE = null;
	private static JButton BUTTON_REPLACE_ALL = null;
	
	// Define the right panel
	protected static JList FIND_REPLACE_LIST = null;
	
	private static JButton BUTTON_NEW = null;
	private static JButton BUTTON_DELETE = null;
	
	// Model
	public static FindReplaceModel model = null;
	private static FindReplaceDialog findReplaceDialog = null;
	private static JFileChooser fileChooser = null;
	
	
	// Static Fields
	
	
	// Instance Fields
	private boolean initialized = false;
	
	
	// Static Methods
	private static void updateButtons() {
		int doc_count = Outliner.documents.openDocumentCount();
		Component selectedTab = tabs.getSelectedComponent();
		if (doc_count <= 0) {
			tabs.setEnabledAt(0, false);
			tabs.setEnabledAt(1, false);
			if (tabDocument == selectedTab || tabAllDocuments == selectedTab) {
				BUTTON_FIND.setEnabled(false);
				BUTTON_FIND_ALL.setEnabled(false);
				BUTTON_REPLACE.setEnabled(false);
				BUTTON_REPLACE_ALL.setEnabled(false);
				CHECKBOX_START_AT_TOP.setEnabled(false);
				CHECKBOX_WRAP_AROUND.setEnabled(false);
				CHECKBOX_SELECTION_ONLY.setEnabled(false);
				CHECKBOX_INCLUDE_READ_ONLY_NODES.setEnabled(false);
				CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS.setEnabled(false);
			} else if (selectedTab == tabFileSystem) {
				BUTTON_FIND.setEnabled(false);
				BUTTON_FIND_ALL.setEnabled(true);
				BUTTON_REPLACE.setEnabled(false);
				BUTTON_REPLACE_ALL.setEnabled(true);
				if (CHECKBOX_INCLUDE_SUB_DIRECTORIES.isSelected()) {
					LABEL_DIR_FILTER_INCLUDE.setEnabled(true);
					LABEL_DIR_FILTER_EXCLUDE.setEnabled(true);
					TEXTFIELD_DIR_FILTER_INCLUDE.setEnabled(true);
					TEXTFIELD_DIR_FILTER_EXCLUDE.setEnabled(true);
					CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.setEnabled(true);
					CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.setEnabled(true);
				} else {
					LABEL_DIR_FILTER_INCLUDE.setEnabled(false);
					LABEL_DIR_FILTER_EXCLUDE.setEnabled(false);
					TEXTFIELD_DIR_FILTER_INCLUDE.setEnabled(false);
					TEXTFIELD_DIR_FILTER_EXCLUDE.setEnabled(false);
					CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.setEnabled(false);
					CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.setEnabled(false);
				}
			}
		} else {
			tabs.setEnabledAt(0, true);
			tabs.setEnabledAt(1, true);
			if (tabDocument == selectedTab || tabAllDocuments == selectedTab) {
				BUTTON_FIND.setEnabled(true);
				BUTTON_FIND_ALL.setEnabled(true);
				BUTTON_REPLACE.setEnabled(true);
				BUTTON_REPLACE_ALL.setEnabled(true);
				CHECKBOX_START_AT_TOP.setEnabled(true);
				CHECKBOX_WRAP_AROUND.setEnabled(true);
				CHECKBOX_SELECTION_ONLY.setEnabled(true);
				CHECKBOX_INCLUDE_READ_ONLY_NODES.setEnabled(true);
				CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS.setEnabled(true);
			} else if (selectedTab == tabFileSystem) {
				BUTTON_FIND.setEnabled(false);
				BUTTON_FIND_ALL.setEnabled(true);
				BUTTON_REPLACE.setEnabled(false);
				BUTTON_REPLACE_ALL.setEnabled(true);
				if (CHECKBOX_INCLUDE_SUB_DIRECTORIES.isSelected()) {
					LABEL_DIR_FILTER_INCLUDE.setEnabled(true);
					LABEL_DIR_FILTER_EXCLUDE.setEnabled(true);
					TEXTFIELD_DIR_FILTER_INCLUDE.setEnabled(true);
					TEXTFIELD_DIR_FILTER_EXCLUDE.setEnabled(true);
					CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.setEnabled(true);
					CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.setEnabled(true);
				} else {
					LABEL_DIR_FILTER_INCLUDE.setEnabled(false);
					LABEL_DIR_FILTER_EXCLUDE.setEnabled(false);
					TEXTFIELD_DIR_FILTER_INCLUDE.setEnabled(false);
					TEXTFIELD_DIR_FILTER_EXCLUDE.setEnabled(false);
					CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.setEnabled(false);
					CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.setEnabled(false);
				}
			}
		}
	}
	
	
	// The Constructor
	public FindReplaceFrame() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		Outliner.findReplace = this;
	}
	
	private void initialize() {
		// Initialize Perl RegExp
		util = new Perl5Util();
		compiler = new Perl5Compiler();
		monitor = new ProgressDialog();
		
		// Setup FindReplaceList in GUI before initializing model since callbacks
		// are made to the FIND_REPLACE_LIST
		FIND_REPLACE_LIST = new JList();
		FIND_REPLACE_LIST.setModel(new DefaultListModel());
		FIND_REPLACE_LIST.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		FIND_REPLACE_LIST.addListSelectionListener(this);
		
		FIND_REPLACE_LIST.addMouseListener(
			new MouseAdapter() {
                                @Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int index = FIND_REPLACE_LIST.locationToIndex(e.getPoint());
						DefaultListModel model = (DefaultListModel) FIND_REPLACE_LIST.getModel();
						findReplaceDialog.show(FindReplaceDialog.MODE_RENAME);
					}
				}
			}
		);
		
		JScrollPane jsp = new JScrollPane(FIND_REPLACE_LIST);
		
		// Initialize Model
		model = new FindReplaceModel();
		
		findReplaceDialog = new FindReplaceDialog();
		Outliner.documents.addDocumentRepositoryListener(this);
		
		// Setup GUI
		FIND = GUITreeLoader.reg.getText("find");
		FIND_ALL = GUITreeLoader.reg.getText("find_all");
		REPLACE = GUITreeLoader.reg.getText("replace");
		REPLACE_ALL = GUITreeLoader.reg.getText("replace_all");
		
		START_AT_TOP = GUITreeLoader.reg.getText("start_at_top");
		WRAP_ARROUND = GUITreeLoader.reg.getText("wrap_around");
		SELECTION_ONLY = GUITreeLoader.reg.getText("selection_only");
		IGNORE_CASE = GUITreeLoader.reg.getText("ignore_case");
		INCLUDE_READ_ONLY_NODES = GUITreeLoader.reg.getText("include_read_only_nodes");
		INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS = GUITreeLoader.reg.getText("include_read_only_nodes") + " "; // The extra space ensures that this activates a different action listener key.
		REGEXP = GUITreeLoader.reg.getText("regexp");
		
		CHECKBOX_REGEXP = new JCheckBox(REGEXP);
		CHECKBOX_REGEXP.addActionListener(this);
		CHECKBOX_START_AT_TOP = new JCheckBox(START_AT_TOP);
		CHECKBOX_START_AT_TOP.addActionListener(this);
		CHECKBOX_WRAP_AROUND = new JCheckBox(WRAP_ARROUND);
		CHECKBOX_WRAP_AROUND.addActionListener(this);
		CHECKBOX_SELECTION_ONLY = new JCheckBox(SELECTION_ONLY);
		CHECKBOX_SELECTION_ONLY.addActionListener(this);
		CHECKBOX_IGNORE_CASE = new JCheckBox(IGNORE_CASE);
		CHECKBOX_IGNORE_CASE.addActionListener(this);
		CHECKBOX_INCLUDE_READ_ONLY_NODES = new JCheckBox(INCLUDE_READ_ONLY_NODES);
		CHECKBOX_INCLUDE_READ_ONLY_NODES.addActionListener(this);
		CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS = new JCheckBox(INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS);
		CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS.addActionListener(this);
		
		LABEL_PATH = new JLabel(PATH);
		TEXTFIELD_PATH = new JTextField();
		BUTTON_SELECT = new JButton(SELECT_DOTS);
		BUTTON_SELECT.addActionListener(this);
		CHECKBOX_INCLUDE_SUB_DIRECTORIES = new JCheckBox(INCLUDE_SUB_DIRECTORIES);
		CHECKBOX_INCLUDE_SUB_DIRECTORIES.addActionListener(this);
		CHECKBOX_MAKE_BACKUPS = new JCheckBox(MAKE_BACKUPS);
		CHECKBOX_MAKE_BACKUPS.addActionListener(this);
		
		LABEL_FILE_FILTER_INCLUDE = new JLabel(INCLUDE);
		TEXTFIELD_FILE_FILTER_INCLUDE = new JTextField();
		TEXTFIELD_FILE_FILTER_INCLUDE.setToolTipText(TT_FILTER);
		CHECKBOX_FILE_FILTER_INCLUDE_IGNORE_CASE = new JCheckBox(IGNORE_CASE);
		CHECKBOX_FILE_FILTER_INCLUDE_IGNORE_CASE.addActionListener(this);
		CHECKBOX_FILE_FILTER_INCLUDE_IGNORE_CASE.setActionCommand(FILE_FILTER_INCLUDE_IGNORE_CASE);
		LABEL_FILE_FILTER_EXCLUDE = new JLabel(EXCLUDE);
		TEXTFIELD_FILE_FILTER_EXCLUDE = new JTextField();
		TEXTFIELD_FILE_FILTER_EXCLUDE.setToolTipText(TT_FILTER);
		CHECKBOX_FILE_FILTER_EXCLUDE_IGNORE_CASE = new JCheckBox(IGNORE_CASE);
		CHECKBOX_FILE_FILTER_EXCLUDE_IGNORE_CASE.addActionListener(this);
		CHECKBOX_FILE_FILTER_EXCLUDE_IGNORE_CASE.setActionCommand(FILE_FILTER_EXCLUDE_IGNORE_CASE);
		
		LABEL_DIR_FILTER_INCLUDE = new JLabel(INCLUDE);
		TEXTFIELD_DIR_FILTER_INCLUDE = new JTextField();
		TEXTFIELD_DIR_FILTER_INCLUDE.setToolTipText(TT_FILTER);
		CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE = new JCheckBox(IGNORE_CASE);
		CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.addActionListener(this);
		CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.setActionCommand(DIR_FILTER_INCLUDE_IGNORE_CASE);
		LABEL_DIR_FILTER_EXCLUDE = new JLabel(EXCLUDE);
		TEXTFIELD_DIR_FILTER_EXCLUDE = new JTextField();
		TEXTFIELD_DIR_FILTER_EXCLUDE.setToolTipText(TT_FILTER);
		CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE = new JCheckBox(IGNORE_CASE);
		CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.addActionListener(this);
		CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.setActionCommand(DIR_FILTER_EXCLUDE_IGNORE_CASE);	
		
		BUTTON_FIND = new JButton(FIND);
		BUTTON_FIND_ALL = new JButton(FIND_ALL);
		BUTTON_REPLACE = new JButton(REPLACE);
		BUTTON_REPLACE_ALL = new JButton(REPLACE_ALL);
		
		TEXTAREA_FIND = new JTextArea();
		TEXTAREA_FIND.getDocument().addDocumentListener(new FindReplaceJTextAreaDocumentListener(FindReplaceJTextAreaDocumentListener.TYPE_FIND, model));
		
		TEXTAREA_REPLACE = new JTextArea();
		TEXTAREA_REPLACE.getDocument().addDocumentListener(new FindReplaceJTextAreaDocumentListener(FindReplaceJTextAreaDocumentListener.TYPE_REPLACE, model));
		
		Insets insets = new Insets(1,3,1,3);
		Cursor cursor = new Cursor(Cursor.TEXT_CURSOR);
		
		TEXTAREA_FIND.setName(FIND);
		TEXTAREA_FIND.setCursor(cursor);
		TEXTAREA_FIND.setLineWrap(true);
		TEXTAREA_FIND.setMargin(insets);
		TEXTAREA_FIND.setRows(3);
		
		TEXTAREA_REPLACE.setName(REPLACE);
		TEXTAREA_REPLACE.setCursor(cursor);
		TEXTAREA_REPLACE.setLineWrap(true);
		TEXTAREA_REPLACE.setMargin(insets);
		TEXTAREA_REPLACE.setRows(3);
		
		// Right Panel
		NEW = GUITreeLoader.reg.getText("new");
		DELETE = GUITreeLoader.reg.getText("delete");
		
		BUTTON_NEW = new JButton(NEW);
		BUTTON_DELETE = new JButton(DELETE);
		
		// Setup JFileChooser
		fileChooser = new JFileChooser();
		fileChooser.setFileHidingEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setApproveButtonText(SELECT);
		
		// Match Options
		JPanel matchOptionsPanel = new JPanel();
		matchOptionsPanel.setLayout(new BorderLayout());
		matchOptionsPanel.setBorder(new TitledBorder(GUITreeLoader.reg.getText("border_title_match")));
		Box matchOptionsBox = Box.createVerticalBox();
		matchOptionsBox.add(CHECKBOX_REGEXP);
		matchOptionsBox.add(CHECKBOX_IGNORE_CASE);
		matchOptionsPanel.add(matchOptionsBox, BorderLayout.CENTER);
		
		// Scope Options
		
		// Define Box for File System Search
		Box fileSystemPathCheckBoxBox = Box.createHorizontalBox();
			fileSystemPathCheckBoxBox.add(CHECKBOX_INCLUDE_SUB_DIRECTORIES);
			fileSystemPathCheckBoxBox.add(Box.createHorizontalStrut(5));
			fileSystemPathCheckBoxBox.add(CHECKBOX_MAKE_BACKUPS);
			
		// File Filter Panel
		JPanel fileFilterPanel = new JPanel();
		fileFilterPanel.setBorder(new TitledBorder(" " + FILE_FILTER + " "));
		GridBagLayout fileFilterPanelLayout = new GridBagLayout();
		fileFilterPanel.setLayout(fileFilterPanelLayout);
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,2,2,2);
		
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		fileFilterPanelLayout.setConstraints(LABEL_FILE_FILTER_INCLUDE, c);
		fileFilterPanel.add(LABEL_FILE_FILTER_INCLUDE);
		
		c.weighty = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		fileFilterPanelLayout.setConstraints(TEXTFIELD_FILE_FILTER_INCLUDE, c);
		fileFilterPanel.add(TEXTFIELD_FILE_FILTER_INCLUDE);
		
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		fileFilterPanelLayout.setConstraints(CHECKBOX_FILE_FILTER_INCLUDE_IGNORE_CASE, c);
		fileFilterPanel.add(CHECKBOX_FILE_FILTER_INCLUDE_IGNORE_CASE);
		
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		fileFilterPanelLayout.setConstraints(LABEL_FILE_FILTER_EXCLUDE, c);
		fileFilterPanel.add(LABEL_FILE_FILTER_EXCLUDE);
		
		c.weighty = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		fileFilterPanelLayout.setConstraints(TEXTFIELD_FILE_FILTER_EXCLUDE, c);
		fileFilterPanel.add(TEXTFIELD_FILE_FILTER_EXCLUDE);
		
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		fileFilterPanelLayout.setConstraints(CHECKBOX_FILE_FILTER_EXCLUDE_IGNORE_CASE, c);
		fileFilterPanel.add(CHECKBOX_FILE_FILTER_EXCLUDE_IGNORE_CASE);
		
		// Directory Filter Panel
		JPanel dirFilterPanel = new JPanel();
		dirFilterPanel.setBorder(new TitledBorder(" " + DIR_FILTER + " "));
		GridBagLayout dirFilterPanelLayout = new GridBagLayout();
		dirFilterPanel.setLayout(dirFilterPanelLayout);
		
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		dirFilterPanelLayout.setConstraints(LABEL_DIR_FILTER_INCLUDE, c);
		dirFilterPanel.add(LABEL_DIR_FILTER_INCLUDE);
		
		c.weighty = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		dirFilterPanelLayout.setConstraints(TEXTFIELD_DIR_FILTER_INCLUDE, c);
		dirFilterPanel.add(TEXTFIELD_DIR_FILTER_INCLUDE);
		
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		dirFilterPanelLayout.setConstraints(CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE, c);
		dirFilterPanel.add(CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE);
		
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		dirFilterPanelLayout.setConstraints(LABEL_DIR_FILTER_EXCLUDE, c);
		dirFilterPanel.add(LABEL_DIR_FILTER_EXCLUDE);
		
		c.weighty = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		dirFilterPanelLayout.setConstraints(TEXTFIELD_DIR_FILTER_EXCLUDE, c);
		dirFilterPanel.add(TEXTFIELD_DIR_FILTER_EXCLUDE);
		
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		dirFilterPanelLayout.setConstraints(CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE, c);
		dirFilterPanel.add(CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE);
		
		// File System Panel
		JPanel fileSystemPanel = new JPanel();
		GridBagLayout fileSystemPanelLayout = new GridBagLayout();
		fileSystemPanel.setLayout(fileSystemPanelLayout);
		
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		fileSystemPanelLayout.setConstraints(LABEL_PATH, c);
		fileSystemPanel.add(LABEL_PATH);
		
		c.weighty = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		fileSystemPanelLayout.setConstraints(TEXTFIELD_PATH, c);
		fileSystemPanel.add(TEXTFIELD_PATH);
		
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.NONE;
		fileSystemPanelLayout.setConstraints(BUTTON_SELECT, c);
		fileSystemPanel.add(BUTTON_SELECT);
		
		c.weighty = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		fileSystemPanelLayout.setConstraints(fileSystemPathCheckBoxBox, c);
		fileSystemPanel.add(fileSystemPathCheckBoxBox);
		
		c.weighty = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		fileSystemPanelLayout.setConstraints(fileFilterPanel, c);
		fileSystemPanel.add(fileFilterPanel);
		
		c.weighty = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		fileSystemPanelLayout.setConstraints(dirFilterPanel, c);
		fileSystemPanel.add(dirFilterPanel);
		
		tabFileSystem = fileSystemPanel;
		
		// Define Box for Document Scope
		Box documentScopeBox = Box.createVerticalBox();
		documentScopeBox.add(CHECKBOX_START_AT_TOP);
		documentScopeBox.add(CHECKBOX_WRAP_AROUND);
		documentScopeBox.add(CHECKBOX_SELECTION_ONLY);
		documentScopeBox.add(CHECKBOX_INCLUDE_READ_ONLY_NODES);
		tabDocument = documentScopeBox;
		
		// Define Box for All Documents Scope
		Box allDocumentsScopeBox = Box.createVerticalBox();
		allDocumentsScopeBox.add(CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS);
		tabAllDocuments = allDocumentsScopeBox;
		
		// Setup Tabs
		tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		tabs.addTab(CURRENT_DOCUMENT, null, tabDocument, GUITreeLoader.reg.getText("tt_find_replace_scope_document"));
		tabs.addTab(ALL_OPEN_DOCUMENTS, null, tabAllDocuments, GUITreeLoader.reg.getText("tt_find_replace_scope_documents"));
		tabs.addTab(FILE_SYSTEM, null, tabFileSystem, GUITreeLoader.reg.getText("tt_find_replace_scope_file_system"));
		
		tabs.addChangeListener(
			new ChangeListener() {
                                @Override
				public void stateChanged(ChangeEvent e) {
					updateButtons();
				}
			}
		);
		
		// Scope Options Panel
		JPanel scopeOptionsPanel = new JPanel();
		scopeOptionsPanel.setLayout(new BorderLayout());
		scopeOptionsPanel.add(tabs, BorderLayout.CENTER);
		
		// Define Button Box
		BUTTON_FIND.addActionListener(this);
		BUTTON_FIND_ALL.addActionListener(this);
		BUTTON_REPLACE.addActionListener(this);
		BUTTON_REPLACE_ALL.addActionListener(this);
		
		Box buttonBox = Box.createHorizontalBox();
		
		buttonBox.add(BUTTON_FIND);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(BUTTON_FIND_ALL);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(BUTTON_REPLACE);
		buttonBox.add(Box.createHorizontalStrut(5));
		buttonBox.add(BUTTON_REPLACE_ALL);
		
		// Set the default button.
		getRootPane().setDefaultButton(BUTTON_FIND);
		
		// Define Find Panel
		JPanel findPanel = new JPanel();
		findPanel.setLayout(new BorderLayout());
		findPanel.setBorder(new TitledBorder(" " + FIND + " "));
		TEXTAREA_FIND.addKeyListener(this);
		JScrollPane findScrollPane = new JScrollPane(TEXTAREA_FIND);
		findPanel.add(findScrollPane, BorderLayout.CENTER);
		
		// Define Replace Panel
		JPanel replacePanel = new JPanel();
		replacePanel.setLayout(new BorderLayout());
		replacePanel.setBorder(new TitledBorder(" " + REPLACE + " "));
		TEXTAREA_REPLACE.addKeyListener(this);
		JScrollPane replaceScrollPane = new JScrollPane(TEXTAREA_REPLACE);
		replacePanel.add(replaceScrollPane, BorderLayout.CENTER);
		
		// Define FindReplace Panel
		JPanel findReplacePanel = new JPanel();
		findReplacePanel.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new EmptyBorder(new Insets(5,5,5,5))));
		GridBagLayout findReplacePanelLayout = new GridBagLayout();
		findReplacePanel.setLayout(findReplacePanelLayout);
		
		c.weighty = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		findReplacePanelLayout.setConstraints(findPanel, c);
		findReplacePanel.add(findPanel);
		
		findReplacePanelLayout.setConstraints(replacePanel, c);
		findReplacePanel.add(replacePanel);
		
		c.weighty = 0;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		findReplacePanelLayout.setConstraints(matchOptionsPanel, c);
		findReplacePanel.add(matchOptionsPanel);
		
		findReplacePanelLayout.setConstraints(scopeOptionsPanel, c);
		findReplacePanel.add(scopeOptionsPanel);
		
		findReplacePanelLayout.setConstraints(buttonBox, c);
		findReplacePanel.add(buttonBox);
		
		getContentPane().add(findReplacePanel, BorderLayout.CENTER);
		
		// Define Left Panel
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED), new EmptyBorder(new Insets(5,5,5,5))));
		
		BUTTON_NEW.addActionListener(this);
		BUTTON_DELETE.addActionListener(this);
		
		leftPanel.add(jsp, BorderLayout.CENTER);
		Box listBox = Box.createHorizontalBox();
		listBox.add(BUTTON_NEW);
		listBox.add(Box.createHorizontalStrut(5));
		listBox.add(BUTTON_DELETE);
		leftPanel.add(listBox, BorderLayout.NORTH);
		
		getContentPane().add(leftPanel, BorderLayout.EAST);
		
		FIND_REPLACE_LIST.setSelectedIndex(0);
		
		syncToModel();
		
		pack();
	}
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
        @Override
	public void show() {
		// Lazy Instantiation
		if (!initialized) {
			initialize();
			initialized = true;
		}
		
		TEXTAREA_FIND.requestFocus();
		
		int doc_count = Outliner.documents.openDocumentCount();
		if (doc_count <= 0) {
			tabs.setSelectedIndex(2);
		} else {
			tabs.setSelectedIndex(0);
		}
		super.show();
	}
	
	/**
	 * Gets the current index of the find/replace items list.
	 */
	protected int getFindReplaceItemIndex() {
		return FIND_REPLACE_LIST.getSelectedIndex();
	}
	
	
	// DocumentRepositoryListener Interface
        @Override
	public void documentAdded(DocumentRepositoryEvent e) {}
	
        @Override
	public void documentRemoved(DocumentRepositoryEvent e) {}
	
        @Override
	public void changedMostRecentDocumentTouched(DocumentRepositoryEvent e) {
		updateButtons();
	}
	
	
	// ListSelectionListenerInterface
        @Override
	public void valueChanged(ListSelectionEvent e) {
		int currentIndex = getFindReplaceItemIndex();
		
		// Sync View to Model for new index
		if ((currentIndex >= 0) && (currentIndex < model.getSize())) {
			CHECKBOX_IGNORE_CASE.setSelected(model.getIgnoreCase(currentIndex));
			CHECKBOX_REGEXP.setSelected(model.getRegExp(currentIndex));
			TEXTAREA_FIND.setText(model.getFind(currentIndex));
			TEXTAREA_REPLACE.setText(model.getReplace(currentIndex));
		}
		
		// Enable/Disable "Delete" button for the first index
		if (currentIndex <= 0) {
			BUTTON_DELETE.setEnabled(false);
		} else {
			BUTTON_DELETE.setEnabled(true);
		}
	}
	
	// KeyListener Interface
        @Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			JTextArea text = (JTextArea) e.getSource();
			
			BUTTON_FIND.doClick(100);
			
			e.consume();
			return;
		}
		
		if (e.getKeyChar() == KeyEvent.VK_TAB) {
			JTextArea text = (JTextArea) e.getSource();
			
			if (text.getName().equals(FIND)) {
				if (e.isShiftDown()) {
					FIND_REPLACE_LIST.requestFocus();
				} else {
					TEXTAREA_REPLACE.requestFocus();
				}
			} else if (text.getName().equals(REPLACE)) {
				if (e.isShiftDown()) {
					TEXTAREA_FIND.requestFocus();
				} else {
					CHECKBOX_REGEXP.requestFocus();
				}
			}
			
			e.consume();
			return;
		}
	}
	
        @Override
	public void keyTyped(KeyEvent e) {}
        @Override
	public void keyReleased(KeyEvent e) {}
	
	
	private void syncToModel() {
		CHECKBOX_START_AT_TOP.setSelected(model.getStartAtTop());
		CHECKBOX_WRAP_AROUND.setSelected(model.getWrapAround());
		CHECKBOX_SELECTION_ONLY.setSelected(model.getSelectionOnly());
		CHECKBOX_INCLUDE_READ_ONLY_NODES.setSelected(model.getIncludeReadOnly());
		CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS.setSelected(model.getIncludeReadOnlyAllDocuments());
		TEXTFIELD_PATH.setText(model.getPath());
		CHECKBOX_INCLUDE_SUB_DIRECTORIES.setSelected(model.getIncludeSubDirs());
		CHECKBOX_MAKE_BACKUPS.setSelected(model.getMakeBackups());
		TEXTFIELD_FILE_FILTER_INCLUDE.setText(model.getFileFilterInclude());
		TEXTFIELD_FILE_FILTER_EXCLUDE.setText(model.getFileFilterExclude());
		CHECKBOX_FILE_FILTER_INCLUDE_IGNORE_CASE.setSelected(model.getFileFilterIncludeIgnoreCase());
		CHECKBOX_FILE_FILTER_EXCLUDE_IGNORE_CASE.setSelected(model.getFileFilterExcludeIgnoreCase());
		TEXTFIELD_DIR_FILTER_INCLUDE.setText(model.getDirFilterInclude());
		TEXTFIELD_DIR_FILTER_EXCLUDE.setText(model.getDirFilterExclude());
		CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.setSelected(model.getDirFilterIncludeIgnoreCase());
		CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.setSelected(model.getDirFilterExcludeIgnoreCase());
		
		int mode = model.getSelectionMode();
		if (mode == FindReplaceModel.MODE_CURRENT_DOCUMENT) {
			tabs.setSelectedIndex(0);
		} else if (mode == FindReplaceModel.MODE_ALL_OPEN_DOCUMENTS) {
			tabs.setSelectedIndex(1);
		} else if (mode == FindReplaceModel.MODE_FILE_SYSTEM) {
			tabs.setSelectedIndex(2);
		} else {
			System.out.println("Unknown File Selection Mode: " + mode);
		}
		updateButtons();
	}
	
        @Override
	public void hide() {
		if (initialized) {
			model.setPath(TEXTFIELD_PATH.getText());
			model.setFileFilterInclude(TEXTFIELD_FILE_FILTER_INCLUDE.getText());
			model.setFileFilterExclude(TEXTFIELD_FILE_FILTER_EXCLUDE.getText());
			model.setDirFilterInclude(TEXTFIELD_DIR_FILTER_INCLUDE.getText());
			model.setDirFilterExclude(TEXTFIELD_DIR_FILTER_EXCLUDE.getText());
		}
		super.hide();
	}
	
	// ActionListener Interface
        @Override
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(FIND)) {
			find((OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(FIND_ALL)) {
			find_all((OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(REPLACE)) {
			replace((OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(REPLACE_ALL)) {
			replace_all((OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched());
		} else if (e.getActionCommand().equals(NEW)) {
			newFindReplace();
		} else if (e.getActionCommand().equals(DELETE)) {
			deleteFindReplace();
			
		} else if (e.getActionCommand().equals(SELECT_DOTS)) {
			int returnVal = fileChooser.showOpenDialog(this);
			if(returnVal == JFileChooser.APPROVE_OPTION) {
				TEXTFIELD_PATH.setText(fileChooser.getSelectedFile().getPath());
			}
		
		// CheckBoxes
		} else if (e.getActionCommand().equals(IGNORE_CASE)) {
			model.setIgnoreCase(getFindReplaceItemIndex(), CHECKBOX_IGNORE_CASE.isSelected());
		} else if (e.getActionCommand().equals(REGEXP)) {
			model.setRegExp(getFindReplaceItemIndex(), CHECKBOX_REGEXP.isSelected());
			
		} else if (e.getActionCommand().equals(START_AT_TOP)) {
			model.setStartAtTop(CHECKBOX_START_AT_TOP.isSelected());
		} else if (e.getActionCommand().equals(WRAP_ARROUND)) {
			model.setWrapAround(CHECKBOX_WRAP_AROUND.isSelected());
		} else if (e.getActionCommand().equals(SELECTION_ONLY)) {
			model.setSelectionOnly(CHECKBOX_SELECTION_ONLY.isSelected());
		} else if (e.getActionCommand().equals(INCLUDE_READ_ONLY_NODES)) {
			model.setIncludeReadOnly(CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected());
		} else if (e.getActionCommand().equals(INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS)) {
			model.setIncludeReadOnlyAllDocuments(CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS.isSelected());
			
		} else if (e.getActionCommand().equals(MAKE_BACKUPS)) {
			model.setMakeBackups(CHECKBOX_MAKE_BACKUPS.isSelected());
			
		} else if (e.getActionCommand().equals(FILE_FILTER_INCLUDE_IGNORE_CASE)) {
			model.setFileFilterIncludeIgnoreCase(CHECKBOX_FILE_FILTER_INCLUDE_IGNORE_CASE.isSelected());
		} else if (e.getActionCommand().equals(FILE_FILTER_EXCLUDE_IGNORE_CASE)) {
			model.setFileFilterExcludeIgnoreCase(CHECKBOX_FILE_FILTER_EXCLUDE_IGNORE_CASE.isSelected());
			
		} else if (e.getActionCommand().equals(DIR_FILTER_INCLUDE_IGNORE_CASE)) {
			model.setDirFilterIncludeIgnoreCase(CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.isSelected());
		} else if (e.getActionCommand().equals(DIR_FILTER_EXCLUDE_IGNORE_CASE)) {
			model.setDirFilterExcludeIgnoreCase(CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.isSelected());
			
		} else if (e.getActionCommand().equals(INCLUDE_SUB_DIRECTORIES)) {
			model.setIncludeSubDirs(CHECKBOX_INCLUDE_SUB_DIRECTORIES.isSelected());
			
			if (CHECKBOX_INCLUDE_SUB_DIRECTORIES.isSelected()) {
				LABEL_DIR_FILTER_INCLUDE.setEnabled(true);
				LABEL_DIR_FILTER_EXCLUDE.setEnabled(true);
				TEXTFIELD_DIR_FILTER_INCLUDE.setEnabled(true);
				TEXTFIELD_DIR_FILTER_EXCLUDE.setEnabled(true);
				CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.setEnabled(true);
				CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.setEnabled(true);
			} else {
				LABEL_DIR_FILTER_INCLUDE.setEnabled(false);
				LABEL_DIR_FILTER_EXCLUDE.setEnabled(false);
				TEXTFIELD_DIR_FILTER_INCLUDE.setEnabled(false);
				TEXTFIELD_DIR_FILTER_EXCLUDE.setEnabled(false);
				CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.setEnabled(false);
				CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.setEnabled(false);
			}
		}
	}
	
	private static int getFindReplaceMode() {
		Component selectedTab = tabs.getSelectedComponent();
		if (tabDocument == selectedTab) {
			return FindReplaceModel.MODE_CURRENT_DOCUMENT;
		} else if (tabAllDocuments == selectedTab) {
			return FindReplaceModel.MODE_ALL_OPEN_DOCUMENTS;
		} else if (tabFileSystem == selectedTab) {
			return FindReplaceModel.MODE_FILE_SYSTEM;
		} else {
			return FindReplaceModel.MODE_UNKNOWN;
		}
	}
	
	private void newFindReplace() {
		findReplaceDialog.show(FindReplaceDialog.MODE_NEW);
	}
	
	private void deleteFindReplace() {
		int selectedIndex = FIND_REPLACE_LIST.getSelectedIndex();
		
		if (selectedIndex != -1) { // Don't delete if there's no selection.
			if (selectedIndex != 0) { // Never delete the default.
				String confirm_delete = GUITreeLoader.reg.getText("confirm_delete");
				String msg = GUITreeLoader.reg.getText("do_you_want_to_delete");
				// Confirm Delete
				int result = JOptionPane.showConfirmDialog(Outliner.findReplace, msg, confirm_delete, JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					model.remove(selectedIndex);
					FIND_REPLACE_LIST.setSelectedIndex(selectedIndex - 1);
					FIND_REPLACE_LIST.requestFocus();
				}
			}
		}
	}
	
	private static void find(OutlinerDocument doc) {
		int mode = getFindReplaceMode();
		
		switch (mode) {
			case FindReplaceModel.MODE_CURRENT_DOCUMENT:
				find(
					doc, 
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				break;
			
			case FindReplaceModel.MODE_ALL_OPEN_DOCUMENTS:
				findAllOpenDocuments(
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				break;
			
			case FindReplaceModel.MODE_UNKNOWN:
				System.out.println("ERROR: Unknown Find/Replace mode.");
				break;
		}
	}
	
	private static FindReplaceResultsModel results = null;
	protected static com.organic.maynard.swing.ProgressMonitor monitor = null;
	
	private static void find_all(OutlinerDocument doc) {
		int mode = getFindReplaceMode();
		
		results = new FindReplaceResultsModel();
		
		switch (mode) {
			case FindReplaceModel.MODE_CURRENT_DOCUMENT:
				findAll(
					results,
					doc, 
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				
				break;
			
			case FindReplaceModel.MODE_ALL_OPEN_DOCUMENTS:
				findAllAllOpenDocuments(
					results,
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				
				break;
			
			case FindReplaceModel.MODE_FILE_SYSTEM:
				Thread t = new Thread(new Runnable() {
                                        @Override
					public void run() {
						//System.out.println("Thread started.");
						findAllFileSystem(
							results,
							TEXTFIELD_PATH.getText(),
							CHECKBOX_INCLUDE_SUB_DIRECTORIES.isSelected(),
							TEXTFIELD_FILE_FILTER_INCLUDE.getText(), 
							CHECKBOX_FILE_FILTER_INCLUDE_IGNORE_CASE.isSelected(), 
							TEXTFIELD_FILE_FILTER_EXCLUDE.getText(), 
							CHECKBOX_FILE_FILTER_EXCLUDE_IGNORE_CASE.isSelected(), 
							TEXTFIELD_DIR_FILTER_INCLUDE.getText(), 
							CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.isSelected(), 
							TEXTFIELD_DIR_FILTER_EXCLUDE.getText(), 
							CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.isSelected(), 
							TEXTAREA_FIND.getText(), 
							CHECKBOX_IGNORE_CASE.isSelected(), 
							CHECKBOX_REGEXP.isSelected()
						);
						//System.out.println("Thread ended.");
					}
				});
				monitor.setCanceled(false);
				t.start();
				monitor.setTitle("File System Find");
				monitor.show(); // Modal dialog, blocks thread.
				break;
			
			case FindReplaceModel.MODE_UNKNOWN:
				System.out.println("ERROR: Unknown Find/Replace mode.");
				break;
		}
		
		if (results.size() == 0) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();
			results = null; // cleanup.
			return;
		}
		
		Outliner.findReplaceResultsDialog.show(results);
		results = null; // cleanup.
	}
	
	private static void replace(OutlinerDocument doc) {
		int mode = getFindReplaceMode();
		
		switch (mode) {
			case FindReplaceModel.MODE_CURRENT_DOCUMENT:
				replace(
					doc, 
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				break;
			
			case FindReplaceModel.MODE_ALL_OPEN_DOCUMENTS:
				replaceAllOpenDocuments(
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				break;
			
			case FindReplaceModel.MODE_UNKNOWN:
				System.out.println("ERROR: Unknown Find/Replace mode.");
				break;
		}
	}
	
	private static void replace_all(OutlinerDocument doc) {
		int mode = getFindReplaceMode();
		
		results = new FindReplaceResultsModel();
		
		switch (mode) {
			case FindReplaceModel.MODE_CURRENT_DOCUMENT:
				replaceAll(
					results,
					doc, 
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				
				doc.panel.layout.redraw();
				break;
			
			case FindReplaceModel.MODE_ALL_OPEN_DOCUMENTS:
				replaceAllAllOpenDocuments(
					results,
					TEXTAREA_FIND.getText(), 
					TEXTAREA_REPLACE.getText(), 
					CHECKBOX_SELECTION_ONLY.isSelected(), 
					CHECKBOX_START_AT_TOP.isSelected(),
					CHECKBOX_IGNORE_CASE.isSelected(), 
					CHECKBOX_INCLUDE_READ_ONLY_NODES_ALL_DOCUMENTS.isSelected(), 
					CHECKBOX_WRAP_AROUND.isSelected(),
					CHECKBOX_REGEXP.isSelected()
				);
				
				Outliner.documents.redrawAllOpenDocuments();
				break;
			
			case FindReplaceModel.MODE_FILE_SYSTEM:
				Thread t = new Thread(new Runnable() { 
                                        @Override
					public void run() { 
						replaceAllFileSystem(
							results,
							TEXTFIELD_PATH.getText(),
							CHECKBOX_INCLUDE_SUB_DIRECTORIES.isSelected(),
							TEXTFIELD_FILE_FILTER_INCLUDE.getText(), 
							CHECKBOX_FILE_FILTER_INCLUDE_IGNORE_CASE.isSelected(), 
							TEXTFIELD_FILE_FILTER_EXCLUDE.getText(), 
							CHECKBOX_FILE_FILTER_EXCLUDE_IGNORE_CASE.isSelected(), 
							TEXTFIELD_DIR_FILTER_INCLUDE.getText(), 
							CHECKBOX_DIR_FILTER_INCLUDE_IGNORE_CASE.isSelected(), 
							TEXTFIELD_DIR_FILTER_EXCLUDE.getText(), 
							CHECKBOX_DIR_FILTER_EXCLUDE_IGNORE_CASE.isSelected(), 
							TEXTAREA_FIND.getText(), 
							TEXTAREA_REPLACE.getText(), 
							CHECKBOX_IGNORE_CASE.isSelected(), 
							CHECKBOX_MAKE_BACKUPS.isSelected(), 
							CHECKBOX_REGEXP.isSelected()
						);
					}
				});
				monitor.setCanceled(false);
				t.start();
				monitor.setTitle(GUITreeLoader.reg.getText("file_system_replace"));
				monitor.show(); // Modal dialog, blocks thread.
				break;
			
			case FindReplaceModel.MODE_UNKNOWN:
				System.out.println("ERROR: Unknown Find/Replace mode.");
				break;
		}
		
		if (results.size() == 0) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();
			return;
		}
		
		Outliner.findReplaceResultsDialog.show(results);
		results = null; // cleanup.
	}
	
	// This method is public and should have no direct dependancy on 
	// find/replace GUI so that it can be called from other classes.
	public static void find(
		OutlinerDocument doc, 
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		NodeRangePair location = findLocation(
			doc, 
			sFind, 
			sReplace, 
			false, 
			selectionOnly, 
			startAtTop,
			ignoreCase, 
			includeReadOnlyNodes, 
			wrapAround,
			isRegexp
		);
		
		if (location == null) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();
		} else {
			// Shorthand
			JoeTree tree = doc.tree;
			
			// Insert the node into the visible nodes and clear the selection.
			tree.insertNode(location.node);
			tree.clearSelection();
			
			// Record the EditingNode and CursorPosition
			tree.setEditingNode(location.node);
			tree.setCursorPosition(location.endIndex);
			tree.setCursorMarkPosition(location.startIndex);
			tree.setComponentFocus(OutlineLayoutManager.TEXT);
			
			// Update Preferred Caret Position
			doc.setPreferredCaretPosition(location.endIndex);
			
			// Freeze Undo Editing
			UndoableEdit.freezeUndoEdit(location.node);
			
			// Bring the window to the front
			try {
				Outliner.outliner.requestFocus();
				doc.setSelected(true);
			} catch (java.beans.PropertyVetoException pve) {
				pve.printStackTrace();
			}
			
			// Redraw and Set Focus
			doc.panel.layout.draw(location.node,OutlineLayoutManager.TEXT);
		}
	}
	
	private static FileSystemFind fileSystemFind = null;
	private static FileSystemReplace fileSystemReplace = null;
	
	private static String[] convertListToStringArray(java.util.List list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = (String) list.get(i);
		}
		return array;
	}
	
	public static void findAllFileSystem(
		FindReplaceResultsModel model, 
		String startingPath, 
		boolean includeSubDirectories,
		String fileFilterInclude, 
		boolean fileFilterIncludeIgnoreCase,
		String fileFilterExclude, 
		boolean fileFilterExcludeIgnoreCase,
		String dirFilterInclude, 
		boolean dirFilterIncludeIgnoreCase,
		String dirFilterExclude, 
		boolean dirFilterExcludeIgnoreCase,
		String sFind, 
		boolean ignoreCase, 
		boolean isRegexp
	) {
		// Lazy Instantiation
		if (fileSystemFind == null) {
			fileSystemFind = new FileSystemFind();
		}
		
		// Prep Query
		if (isRegexp) {
			sFind = prepareRegEx(false, ignoreCase, sFind, "");
			if (sFind == null) {
				// An Error Occurred so abort.
				return;
			}
		}
		
		// Prepare Filters
		com.organic.maynard.util.crawler.FileFilter fileFilter = new TypeGlobFileFilter(fileFilterInclude, fileFilterIncludeIgnoreCase, fileFilterExclude, fileFilterExcludeIgnoreCase);
		com.organic.maynard.util.crawler.FileFilter dirFilter = null;
		if (includeSubDirectories) {
			dirFilter = new TypeGlobFileFilter(dirFilterInclude, dirFilterIncludeIgnoreCase, dirFilterExclude, dirFilterExcludeIgnoreCase);
		} else {
			dirFilter = new NoSubDirectoryFilter();
		}
		
		// Do it
		int success = fileSystemFind.find(model, fileFilter, dirFilter, startingPath, sFind, isRegexp, ignoreCase, includeSubDirectories);
		
		if (success == FAILURE) {
			Outliner.findReplace.monitor.close();
			String msg = GUITreeLoader.reg.getText("file_or_dir_does_not_exist");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, startingPath);
			JOptionPane.showMessageDialog(Outliner.outliner, msg, GUITreeLoader.reg.getText("error_title"), JOptionPane.ERROR_MESSAGE);
			
		}
	}
	
	public static void replaceAllFileSystem(
		FindReplaceResultsModel model, 
		String startingPath, 
		boolean includeSubDirectories,
		String fileFilterInclude, 
		boolean fileFilterIncludeIgnoreCase,
		String fileFilterExclude, 
		boolean fileFilterExcludeIgnoreCase,
		String dirFilterInclude, 
		boolean dirFilterIncludeIgnoreCase,
		String dirFilterExclude, 
		boolean dirFilterExcludeIgnoreCase,
		String sFind, 
		String sReplace, 
		boolean ignoreCase, 
		boolean makeBackups, 
		boolean isRegexp
	) {
		// Lazy Instantiation
		if (fileSystemReplace == null) {
			fileSystemReplace = new FileSystemReplace();
		}
		
		// Prep Query
		if (isRegexp) {
			sReplace = prepareRegEx(true, ignoreCase, sFind, sReplace);
			sFind = prepareRegEx(false, ignoreCase, sFind, "");
			if (sFind == null || sReplace == null) {
				// An Error Occurred so abort.
				return;
			}
		}
		
		// Prepare Filters
		com.organic.maynard.util.crawler.FileFilter fileFilter = new TypeGlobFileFilter(fileFilterInclude, fileFilterIncludeIgnoreCase, fileFilterExclude, fileFilterExcludeIgnoreCase);
		com.organic.maynard.util.crawler.FileFilter dirFilter = null;
		if (includeSubDirectories) {
			dirFilter = new TypeGlobFileFilter(dirFilterInclude, dirFilterIncludeIgnoreCase, dirFilterExclude, dirFilterExcludeIgnoreCase);
		} else {
			dirFilter = new NoSubDirectoryFilter();
		}
		
		// Do it
		int success = fileSystemReplace.replace(model, fileFilter, dirFilter, startingPath, sFind, sReplace, isRegexp, ignoreCase, makeBackups, includeSubDirectories);
		
		if (success == FAILURE) {
			Outliner.findReplace.monitor.close();
			String msg = GUITreeLoader.reg.getText("file_or_dir_does_not_exist");
			msg = Replace.replace(msg,GUITreeComponentRegistry.PLACEHOLDER_1, startingPath);
			JOptionPane.showMessageDialog(Outliner.outliner, msg, GUITreeLoader.reg.getText("error_title"), JOptionPane.ERROR_MESSAGE);
			
		}
	}
	
	// This method is public and should have no direct dependancy on 
	// find/replace GUI so that it can be called from other classes.
	public static void findAllOpenDocuments(
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		Iterator openDocuments = Outliner.documents.getLoopedOpenDocumentIterator();
		
		boolean matchFound = false;
		
		while (openDocuments.hasNext()) {
			OutlinerDocument doc = (OutlinerDocument) openDocuments.next();
			
			NodeRangePair location = null;
			
			if (doc == (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched()) {
				location = findLocation(
					doc, 
					sFind, 
					sReplace, 
					false, 
					selectionOnly, 
					startAtTop,
					ignoreCase, 
					includeReadOnlyNodes, 
					wrapAround,
					isRegexp
				);
			} else {
				Node firstNode = doc.tree.getRootNode().getFirstChild();
				Node lastNode = doc.tree.getRootNode().getLastChild().getLastDecendent();
				
				location = findText(
					firstNode,
					0, 
					lastNode, 
					lastNode.getValue().length(), 
					sFind,
					sReplace, 
					false, 
					false,
					false,
					ignoreCase,
					includeReadOnlyNodes,
					false,
					isRegexp
				);
			}
			
			if (location == null) {
				
			} else {
				// Shorthand
				JoeTree tree = doc.tree;
				
				// Insert the node into the visible nodes and clear the selection.
				tree.insertNode(location.node);
				tree.clearSelection();
				
				// Record the EditingNode and CursorPosition
				tree.setEditingNode(location.node);
				tree.setCursorPosition(location.endIndex);
				tree.setCursorMarkPosition(location.startIndex);
				tree.setComponentFocus(OutlineLayoutManager.TEXT);
				
				// Update Preferred Caret Position
				doc.setPreferredCaretPosition(location.endIndex);
				
				// Freeze Undo Editing
				UndoableEdit.freezeUndoEdit(location.node);
				
				// Bring the window to the front
				Outliner.outliner.requestFocus();
				WindowMenu.changeToWindow(doc);
				
				matchFound = true;
				break;
			}
		}
		
		if (!matchFound) {
			// One last try on the entire current doc since we may have missed a match in the portion of the doc before the cursor..
			find(
				(OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched(), 
				sFind,
				sReplace,
				false,
				true,
				ignoreCase,
				includeReadOnlyNodes,
				false,
				isRegexp
			);
		}
	}
	
	public static void findAllAllOpenDocuments(
		FindReplaceResultsModel results,
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		Iterator openDocuments = Outliner.documents.getLoopedOpenDocumentIterator();
		
		while (openDocuments.hasNext()) {
			OutlinerDocument doc = (OutlinerDocument) openDocuments.next();
			
			findAll(
				results,
				doc, 
				sFind,
				sReplace,
				false,
				true,
				ignoreCase,
				includeReadOnlyNodes,
				false,
				isRegexp
			);
		}
	}
	
	public static void findAll(
		FindReplaceResultsModel results,
		OutlinerDocument doc, 
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		int count = 0;
		
		String replacement = sReplace;
		String textToMatch = sFind;
		if (textToMatch.equals("")) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();
			
			return;
		}
		
		if (selectionOnly) {
			if (doc.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				if (doc.tree.getCursorPosition() == doc.tree.getCursorMarkPosition()) {
					// No selection, so return.
					return;
				} else {
					int cursor = doc.tree.getCursorPosition();
					int mark = doc.tree.getCursorMarkPosition();
					
					Node nodeStart = doc.tree.getEditingNode();
					int cursorStart = Math.min(cursor,mark);
					Node nodeEnd = doc.tree.getEditingNode();
					int cursorEnd = Math.max(cursor,mark);
					
					while (true) {
						//System.out.println("range: " + cursorStart + " : " + cursorEnd);
						if ((nodeStart == nodeEnd) && (cursorStart == cursorEnd)) {break;}
						NodeRangePair location = findText(
							nodeStart,
							cursorStart,
							nodeEnd,
							cursorEnd,
							textToMatch,
							replacement,
							false,
							true,
							true, 
							ignoreCase, 
							includeReadOnlyNodes, 
							wrapAround, 
							isRegexp
						);
						
						if (location == null) {
							if (count == 0) {
								return;
							} else {
								break;
							}
						}
						if (location.loopedOver) {break;}
						
						// Add the Result
						String match = location.node.getValue().substring(location.startIndex, location.endIndex);
						int lineNumber = location.node.getLineNumber();
						FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, "", false);
						results.addResult(result);
						
						nodeStart = location.node;
						cursorStart = location.endIndex;
						
						count++;
					}
					
					// Adjust cursor and mark for new selection.
					doc.tree.setCursorPosition(cursorEnd);
					doc.tree.setCursorMarkPosition(Math.min(cursor,mark));
				}
			} else {
				for (int i = 0; i < doc.tree.getSelectedNodes().size(); i++) {					
					Node nodeStart = doc.tree.getSelectedNodes().get(i);
					int cursorStart = 0;
					Node nodeEnd = nodeStart.getLastDecendent();
					int cursorEnd = nodeEnd.getValue().length();
					
					while (true) {
						//System.out.println("range: " + cursorStart + " : " + cursorEnd);
						if ((nodeStart == nodeEnd) && (cursorStart == cursorEnd)) {break;}
						NodeRangePair location = findText(
							nodeStart,
							cursorStart,
							nodeEnd,
							cursorEnd,
							textToMatch,
							replacement,
							false,
							false,
							true, 
							ignoreCase, 
							includeReadOnlyNodes, 
							wrapAround, 
							isRegexp
						);
						
						if (location == null) {
							break;
						}
						if (location.loopedOver) {break;}
						
						// Add the Result
						String match = location.node.getValue().substring(location.startIndex, location.endIndex);
						String replacementTemp = sReplace;
						int lineNumber = location.node.getLineNumber();
						
						FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, "", false);
						results.addResult(result);

						nodeStart = location.node;
						cursorStart = location.endIndex;
						
						count++;
					}
					
				}
			}
		} else {
			Node nodeStart = doc.tree.getRootNode().getFirstChild();
			int cursorStart = 0;
			Node nodeEnd = doc.tree.getRootNode().getLastDecendent();
			int cursorEnd = nodeEnd.getValue().length();
			
			while (true) {
				//System.out.println("range: " + cursorStart + " : " + cursorEnd);
				NodeRangePair location = findText(
					nodeStart,
					cursorStart,
					nodeEnd,
					cursorEnd,
					textToMatch,
					replacement,
					false,
					false,
					true, 
					ignoreCase, 
					includeReadOnlyNodes, 
					wrapAround, 
					isRegexp
				);
				
				if (location == null) {
					if (count == 0) {
						return;
					} else {
						break;
					}
				}
				if (location.loopedOver) {break;}
				
				// Add the Result
				String match = location.node.getValue().substring(location.startIndex, location.endIndex);
				String replacementTemp = sReplace;
				int lineNumber = location.node.getLineNumber();
				if (isRegexp) {
					replacementTemp = FindReplaceFrame.replacementText.substring(0, FindReplaceFrame.matchLength + FindReplaceFrame.difference);
				}
				FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, "", false);
				results.addResult(result);
				
				nodeStart = location.node;
				cursorStart = location.endIndex;
				
				count++;
			}
		}
	}
	
	
	
	
	public static void replace(
		OutlinerDocument doc, 
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		NodeRangePair location = findLocation(
			doc, 
			sFind, 
			sReplace, 
			true, 
			selectionOnly, 
			startAtTop,
			ignoreCase, 
			includeReadOnlyNodes, 
			wrapAround,
			isRegexp
		);
		
		if (location == null) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();
		} else {
			// Shorthand
			JoeTree tree = doc.tree;
			
			// Create the undoable
			int difference = sReplace.length() - (location.endIndex - location.startIndex);
			if (isRegexp) {
				difference = FindReplaceFrame.difference;
			}
			
			String oldText = location.node.getValue();
			String newText = oldText.substring(0,location.startIndex) + sReplace + oldText.substring(location.endIndex,oldText.length());
			
			if (isRegexp) {
				newText = oldText.substring(0,location.startIndex) + FindReplaceFrame.replacementText;
			}
			
			int oldPosition = location.endIndex;
			int newPosition = location.endIndex + difference;
			doc.getUndoQueue().add(new UndoableEdit(location.node,oldText,newText,oldPosition,newPosition,oldPosition,location.startIndex));
			
			// Update the model
			location.node.setValue(newText);
			
			// Insert the node into the visible nodes and clear the selection.
			tree.insertNode(location.node);
			tree.clearSelection();
			
			// Record the EditingNode and CursorPosition
			tree.setEditingNode(location.node);
			tree.setCursorPosition(location.endIndex + difference);
			tree.setCursorMarkPosition(location.startIndex);
			tree.setComponentFocus(OutlineLayoutManager.TEXT);
			
			// Update Preferred Caret Position
			doc.setPreferredCaretPosition(location.endIndex);
			
			// Freeze Undo Editing
			UndoableEdit.freezeUndoEdit(location.node);
			
			// Bring the window to the front
			try {
				Outliner.outliner.requestFocus();
				doc.setSelected(true);
			} catch (java.beans.PropertyVetoException pve) {
				pve.printStackTrace();
			}
			
			// Redraw and Set Focus
			doc.panel.layout.draw(location.node,OutlineLayoutManager.TEXT);
		}
	}
	
	// This method is public and should have no direct dependancy on 
	// find/replace GUI so that it can be called from other classes.
	public static void replaceAllOpenDocuments(
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		Iterator openDocuments = Outliner.documents.getLoopedOpenDocumentIterator();
		
		boolean matchFound = false;
		
		while (openDocuments.hasNext()) {
			OutlinerDocument doc = (OutlinerDocument) openDocuments.next();
			
			NodeRangePair location = null;
			
			if (doc == (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched()) {
				location = findLocation(
					doc, 
					sFind, 
					sReplace, 
					false, 
					selectionOnly, 
					startAtTop,
					ignoreCase, 
					includeReadOnlyNodes, 
					wrapAround,
					isRegexp
				);
			} else {
				Node firstNode = doc.tree.getRootNode().getFirstChild();
				Node lastNode = doc.tree.getRootNode().getLastChild().getLastDecendent();
				
				location = findText(
					firstNode,
					0, 
					lastNode, 
					lastNode.getValue().length(), 
					sFind,
					sReplace, 
					false, 
					false,
					false,
					ignoreCase,
					includeReadOnlyNodes,
					false,
					isRegexp
				);
			}
			
			if (location == null) {
				// Do Nothing
			} else {
				// Shorthand
				JoeTree tree = doc.tree;
				
				// Create the undoable
				int difference = sReplace.length() - (location.endIndex - location.startIndex);
				if (isRegexp) {
					difference = FindReplaceFrame.difference;
				}
				
				String oldText = location.node.getValue();
				String newText = oldText.substring(0,location.startIndex) + sReplace + oldText.substring(location.endIndex,oldText.length());
				
				if (isRegexp) {
					newText = oldText.substring(0,location.startIndex) + FindReplaceFrame.replacementText;
				}
				
				int oldPosition = location.endIndex;
				int newPosition = location.endIndex + difference;
				doc.getUndoQueue().add(new UndoableEdit(location.node,oldText,newText,oldPosition,newPosition,oldPosition,location.startIndex));
				
				// Update the model
				location.node.setValue(newText);
				
				// Insert the node into the visible nodes and clear the selection.
				tree.insertNode(location.node);
				tree.clearSelection();
				
				// Record the EditingNode and CursorPosition
				tree.setEditingNode(location.node);
				tree.setCursorPosition(location.endIndex + difference);
				tree.setCursorMarkPosition(location.startIndex);
				tree.setComponentFocus(OutlineLayoutManager.TEXT);
				
				// Update Preferred Caret Position
				doc.setPreferredCaretPosition(location.endIndex);
				
				// Freeze Undo Editing
				UndoableEdit.freezeUndoEdit(location.node);
				
				// Bring the window to the front
				Outliner.outliner.requestFocus();
				WindowMenu.changeToWindow(doc);
				
				matchFound = true;
				break;
			}
		}
		
		if (!matchFound) {
			// One last try on the entire current doc since we may have missed a match in the portion of the doc before the cursor..
			replace(
				(OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched(), 
				sFind,
				sReplace,
				false,
				true,
				ignoreCase,
				includeReadOnlyNodes,
				false,
				isRegexp
			);
		}
	}
	
	public static void replaceAllAllOpenDocuments(
		FindReplaceResultsModel results,
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		Iterator openDocuments = Outliner.documents.getLoopedOpenDocumentIterator();
		
		while (openDocuments.hasNext()) {
			OutlinerDocument doc = (OutlinerDocument) openDocuments.next();
			
			replaceAll(
				results,
				doc, 
				sFind,
				sReplace,
				false,
				true,
				ignoreCase,
				includeReadOnlyNodes,
				false,
				isRegexp
			);
		}
	}
	
	public static void replaceAll(
		FindReplaceResultsModel results,
		OutlinerDocument doc, 
		String sFind,
		String sReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		int count = 0;
		
		String replacement = sReplace;
		String textToMatch = sFind;
		if (textToMatch.equals("")) {
			// Beep to alert user no result found.
			Outliner.outliner.getToolkit().beep();
			
			return;
		}
		
		CompoundUndoableEdit undoable = new CompoundUndoableEdit(doc.tree);
		boolean undoableAdded = false;
		
		if (selectionOnly) {
			if (doc.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				if (doc.tree.getCursorPosition() == doc.tree.getCursorMarkPosition()) {
					// No selection, so return.
					return;
				} else {
					int cursor = doc.tree.getCursorPosition();
					int mark = doc.tree.getCursorMarkPosition();
					
					Node nodeStart = doc.tree.getEditingNode();
					int cursorStart = Math.min(cursor,mark);
					Node nodeEnd = doc.tree.getEditingNode();
					int cursorEnd = Math.max(cursor,mark);
					
					while (true) {
						//System.out.println("range: " + cursorStart + " : " + cursorEnd);
						if ((nodeStart == nodeEnd) && (cursorStart == cursorEnd)) {break;}
						NodeRangePair location = findText(
							nodeStart,
							cursorStart,
							nodeEnd,
							cursorEnd,
							textToMatch,
							replacement,
							false,
							true,
							true, 
							ignoreCase, 
							includeReadOnlyNodes, 
							wrapAround, 
							isRegexp
						);
						
						if (location == null) {
							if (count == 0) {
								return;
							} else {
								break;
							}
						}
						if (location.loopedOver) {break;}
						
						if (!undoableAdded) {
							doc.getUndoQueue().add(undoable);
							undoableAdded = true;
						}
						
						// Replace the Text
						String oldText = location.node.getValue();
						String newText = oldText.substring(0,location.startIndex) + sReplace + oldText.substring(location.endIndex,oldText.length()); //
						if (isRegexp) {
							newText = oldText.substring(0,location.startIndex) + FindReplaceFrame.replacementText;
						}
						location.node.setValue(newText);
						
						// Add the primitive undoable
						undoable.addPrimitive(new PrimitiveUndoableEdit(location.node,oldText,newText));
						
						// Add the Result
						String match = oldText.substring(location.startIndex, location.endIndex);
						String replacementTemp = sReplace;
						int lineNumber = location.node.getLineNumber();
						if (isRegexp) {
							replacementTemp = FindReplaceFrame.replacementText.substring(0, FindReplaceFrame.matchLength + FindReplaceFrame.difference);
						}
						FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, replacementTemp, true);
						results.addResult(result);
						
						// Setup for next replacement
						int difference = sReplace.length() - (location.endIndex - location.startIndex);
						if (isRegexp) {
							difference = FindReplaceFrame.difference;
						}
						
						if (nodeEnd == location.node) {
							cursorEnd += difference;
						}
						nodeStart = location.node;
						cursorStart = location.endIndex + difference;
						
						count++;
					}
					
					// Adjust cursor and mark for new selection.
					doc.tree.setCursorPosition(cursorEnd);
					doc.tree.setCursorMarkPosition(Math.min(cursor,mark));
				}
			} else {
				for (int i = 0; i < doc.tree.getSelectedNodes().size(); i++) {
					Node nodeStart = doc.tree.getSelectedNodes().get(i);
					int cursorStart = 0;
					Node nodeEnd = nodeStart.getLastDecendent();
					int cursorEnd = nodeEnd.getValue().length();
					
					while (true) {
						//System.out.println("range: " + cursorStart + " : " + cursorEnd);
						if ((nodeStart == nodeEnd) && (cursorStart == cursorEnd)) {break;}
						NodeRangePair location = findText(
							nodeStart,
							cursorStart,
							nodeEnd,
							cursorEnd,
							textToMatch,
							replacement,
							false,
							false,
							true, 
							ignoreCase, 
							includeReadOnlyNodes, 
							wrapAround, 
							isRegexp
						);
						
						if (location == null) {
							break;
						}
						if (location.loopedOver) {break;}
						
						if (!undoableAdded) {
							doc.getUndoQueue().add(undoable);
							undoableAdded = true;
						}
						
						// Replace the Text
						String oldText = location.node.getValue();
						String newText = oldText.substring(0,location.startIndex) + sReplace + oldText.substring(location.endIndex,oldText.length()); //
						if (isRegexp) {
							newText = oldText.substring(0,location.startIndex) + FindReplaceFrame.replacementText;
						}
						location.node.setValue(newText);
						
						// Add the primitive undoable
						undoable.addPrimitive(new PrimitiveUndoableEdit(location.node,oldText,newText));
						
						// Add the Result
						String match = oldText.substring(location.startIndex, location.endIndex);
						String replacementTemp = sReplace;
						int lineNumber = location.node.getLineNumber();
						if (isRegexp) {
							replacementTemp = FindReplaceFrame.replacementText.substring(0, FindReplaceFrame.matchLength + FindReplaceFrame.difference);
						}
						FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, replacementTemp, true);
						results.addResult(result);
						
						// Setup for next replacement
						int difference = sReplace.length() - (location.endIndex - location.startIndex);
						if (isRegexp) {
							difference = FindReplaceFrame.difference;
						}
						
						if (nodeEnd == location.node) {
							cursorEnd += difference;
						}
						nodeStart = location.node;
						cursorStart = location.endIndex + difference;
						
						count++;
					}
					
				}
			}
		} else {
			Node nodeStart = doc.tree.getRootNode().getFirstChild();
			int cursorStart = 0;
			Node nodeEnd = doc.tree.getRootNode().getLastDecendent();
			int cursorEnd = nodeEnd.getValue().length();
			
			while (true) {
				//System.out.println("range: " + cursorStart + " : " + cursorEnd);
				NodeRangePair location = findText(
					nodeStart,
					cursorStart,
					nodeEnd,
					cursorEnd,
					textToMatch,
					replacement,
					false,
					false,
					true, 
					ignoreCase, 
					includeReadOnlyNodes, 
					wrapAround, 
					isRegexp
				);
				
				if (location == null) {
					if (count == 0) {
						return;
					} else {
						break;
					}
				}
				if (location.loopedOver) {break;}
				
				if (!undoableAdded) {
					doc.getUndoQueue().add(undoable);
					undoableAdded = true;
				}
				
				// Replace the Text
				String oldText = location.node.getValue();
				String newText = oldText.substring(0,location.startIndex) + sReplace + oldText.substring(location.endIndex,oldText.length()); //
				if (isRegexp) {
					newText = oldText.substring(0,location.startIndex) + FindReplaceFrame.replacementText;
				}
				location.node.setValue(newText);
				
				// Add the primitive undoable
				undoable.addPrimitive(new PrimitiveUndoableEdit(location.node,oldText,newText));
				
				// Add the Result
				String match = oldText.substring(location.startIndex, location.endIndex);
				String replacementTemp = sReplace;
				int lineNumber = location.node.getLineNumber();
				if (isRegexp) {
					replacementTemp = FindReplaceFrame.replacementText.substring(0, FindReplaceFrame.matchLength + FindReplaceFrame.difference);
				}
				FindReplaceResult result = new FindReplaceResult(doc, lineNumber, location.startIndex, match, replacementTemp, true);
				results.addResult(result);
				
				// Setup for next replacement
				int difference = sReplace.length() - (location.endIndex - location.startIndex);
				if (isRegexp) {
					difference = FindReplaceFrame.difference;
				}
				
				if (nodeEnd == location.node) {
					cursorEnd += difference;
				}
				nodeStart = location.node;
				cursorStart = location.endIndex + difference;
				
				count++;
			}
		}
	}
	
	private static NodeRangePair findLocation (
		OutlinerDocument doc, 
		String textToMatch,
		String replacement,
		boolean isReplace,
		boolean selectionOnly,
		boolean startAtTop,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		NodeRangePair location = null;
		
		if (textToMatch.equals("")) {
			return null;
		}
		
		if (selectionOnly) {
			if (doc.tree.getComponentFocus() == OutlineLayoutManager.TEXT) {
				if (doc.tree.getCursorPosition() == doc.tree.getCursorMarkPosition()) {
					// No selection, so return.
					return null;
				} else {
					Node node = doc.tree.getEditingNode();
					int cursor = doc.tree.getCursorPosition();
					int mark = doc.tree.getCursorMarkPosition();
					
					location = findText(
						node, 
						Math.min(cursor,mark), 
						node, 
						Math.max(cursor,mark), 
						textToMatch, 
						replacement, 
						false, 
						true, 
						isReplace, 
						ignoreCase, 
						includeReadOnlyNodes, 
						wrapAround, 
						isRegexp
					);
				}
			} else {
				for (int i = 0; i < doc.tree.getSelectedNodes().size(); i++) {
					// Record the Insert in the undoable
					Node nodeStart = doc.tree.getSelectedNodes().get(i);
					int cursorStart = 0;
					Node nodeEnd = nodeStart.getLastDecendent();
					int cursorEnd = nodeEnd.getValue().length();
					
					location = findText(
						nodeStart, 
						cursorStart, 
						nodeEnd, 
						cursorEnd, 
						textToMatch, 
						replacement, 
						false, 
						false, 
						isReplace, 
						ignoreCase, 
						includeReadOnlyNodes, 
						wrapAround, 
						isRegexp
					);
					
					if (location != null) {
						break;
					}
				}
			}
		} else {
			// End Values
			Node nodeEnd = doc.tree.getEditingNode();
			int cursorEnd = doc.tree.getCursorPosition();
			
			// Start Values
			Node nodeStart = null;
			int cursorStart = 0;
			
			if (startAtTop) {
				nodeStart = doc.tree.getRootNode().getFirstChild();
				nodeEnd = doc.tree.getRootNode().getLastDecendent();
				cursorStart = 0;
				cursorEnd = nodeEnd.getValue().length();
			} else {
				nodeStart = doc.tree.getEditingNode();
				cursorStart = doc.tree.getCursorPosition();
				if (nodeStart.isSelected()) {
					cursorStart = 0;
					cursorEnd = 0;
				}
			}
			
			location = findText(
				nodeStart, 
				cursorStart, 
				nodeEnd, 
				cursorEnd, 
				textToMatch, 
				replacement, 
				false, 
				false, 
				isReplace, 
				ignoreCase, 
				includeReadOnlyNodes, 
				wrapAround, 
				isRegexp
			);
		}
		
		return location;
	}
	
	private static NodeRangePair findText(
		Node startNode, 
		int start, 
		Node endNode, 
		int end, 
		String match,
		String replacement, 
		boolean loopedOver, 
		boolean done,
		boolean isReplace,
		boolean ignoreCase,
		boolean includeReadOnlyNodes,
		boolean wrapAround,
		boolean isRegexp
	) {
		// [srk] possible bug w/ bad params
		// check for nulls
		if (startNode == null
			|| endNode == null
			|| match == null
			|| replacement == null
		) {
			return null;
		}
		
		String text = startNode.getValue();
		
		// Find the match
		int matchStart = -1;
		if (startNode == endNode) {
			if (end > start) {
				matchStart = matchText(text.substring(start,end), match, replacement, ignoreCase, isRegexp, isReplace);
				done = true;
			} else {
				matchStart = matchText(text.substring(start,text.length()), match, replacement, ignoreCase, isRegexp, isReplace);
			}
		} else {
			matchStart = matchText(text.substring(start,text.length()), match, replacement, ignoreCase, isRegexp, isReplace);
		}
		
		// Match Found
		if (matchStart != -1) {
			// Deal with read-only nodes for replace
			if (isReplace && !includeReadOnlyNodes && !startNode.isEditable()) {
				// Do nothing so we keep Looking
			} else {
				matchStart += start;
				int matchEnd = matchStart;
				if (isRegexp) {
					matchEnd += FindReplaceFrame.matchLength;
				} else {
					matchEnd += match.length();
				}
				return new NodeRangePair(startNode,matchStart,matchEnd,loopedOver);
			}
		}
		
		// We ran out of places to look.
		if (done) {
			return null;
		}
		
		// No match found, so move on to the next node.
		Node nextNodeToSearch = startNode.nextNode();
		if (nextNodeToSearch.isRoot()) {
			if (!wrapAround) {
				return null;
			}
			nextNodeToSearch = nextNodeToSearch.nextNode();
			loopedOver = true;
		}
		
		// We've gone as far as we can so stop.
		if (endNode == nextNodeToSearch) {
			done = true;
		}
		
		// Try it again
		return findText(
			nextNodeToSearch, 
			0, 
			endNode, 
			end, 
			match, 
			replacement, 
			loopedOver, 
			done, 
			isReplace, 
			ignoreCase, 
			includeReadOnlyNodes, 
			wrapAround, 
			isRegexp
		);
	}
	
	
	private static int matchLength = 0;
	private static int difference = 0;
	private static String replacementText = null;
	private static char[] reservedRegexChars = {'/'};
	
	private static String prepareRegEx(boolean isReplace, boolean ignoreCase, String match, String replacement) {
		StringBuffer retVal = new StringBuffer();
		
		// Escape '/' characters
		match = StringTools.replace(match, "/", "\\/");
		replacement = StringTools.replace(replacement, "/", "\\/");
		
		if (isReplace) {
			retVal.append(REGEX_REPLACE_START).append(match).append(REGEX_REPLACE_MIDDLE).append(replacement);
			if (ignoreCase) {
				retVal.append(REGEX_REPLACE_END_IGNORE_CASE);
			} else {
				retVal.append(REGEX_REPLACE_END);
			}
		
		} else {
			retVal.append(REGEX_MATCH_START).append(match);
			if (ignoreCase) {
				retVal.append(REGEX_MATCH_END_IGNORE_CASE);
			} else {
				retVal.append(REGEX_MATCH_END);
			}
		}
		
		// Compile the Regex to check for syntax errors
		try {
			compiler.compile(retVal.toString());
			return retVal.toString();
		} catch (MalformedPatternException e) {
			// Syntax error found so display error and return null
			JOptionPane.showMessageDialog(Outliner.outliner, e.getMessage());
			return null;
		}
	}
	
	private static int matchText(
		String text, 
		String match, 
		String replacement, 
		boolean ignoreCase,
		boolean isRegexp,
		boolean isReplace
	) {
		if (isRegexp) {
			// Prepare input
			input = new PatternMatcherInput(text);
			
			// Prepare the regex
			String regex = prepareRegEx(false, ignoreCase, match, replacement);
			if (regex == null) {
				// An Error Occurred so abort.
				return -1;
			}
			
			if (isReplace) {
				// Prepare the replacement regex
				String subRegex = prepareRegEx(isReplace, ignoreCase, match, replacement);
				if (subRegex == null) {
					// An Error Occurred so abort.
					return -1;
				}
				// Do the regex find and return result
				try {
					if (util.match(regex, input)) {
						result = util.getMatch();
						
						FindReplaceFrame.replacementText = util.substitute(subRegex, text);
						
						FindReplaceFrame.matchLength = result.length(); // Store length since this method does not return it.
						FindReplaceFrame.difference = FindReplaceFrame.replacementText.length() - text.length();
						
						int matchStartIndex = result.beginOffset(0);
						int matchEndIndex = matchStartIndex + FindReplaceFrame.matchLength;
						int replacementEndIndex = matchEndIndex + FindReplaceFrame.difference;
						
						FindReplaceFrame.replacementText = FindReplaceFrame.replacementText.substring(matchStartIndex, FindReplaceFrame.replacementText.length());
						return matchStartIndex;
					}
				} catch (MalformedCachePatternException e) {
					System.out.println("MalformedCachePatternException: " + e.getMessage());
				}
				return -1;
			} else {
				// Do the regex find and return result
				try {
					if (util.match(regex, input)) {
						result = util.getMatch();
						matchLength = result.length(); // Store length since this method does not return it.
						return result.beginOffset(0);
					}
				} catch (MalformedCachePatternException e) {
					System.out.println("MalformedCachePatternException: " + e.getMessage());
				}
				return -1;
			}
		} else {
			if (ignoreCase) {
				text = text.toLowerCase();
				match = match.toLowerCase();
				return text.indexOf(match);
			} else {
				return text.indexOf(match);
			}
		}
	}
}

class FindReplaceDialog extends JDialog implements ActionListener {
	
	// Constants
	public static final int MODE_NEW = 0;
	public static final int MODE_RENAME = 1;
	
	private int currentMode = -1;
	
	private static String OK = null;
	private static String CANCEL = null;
	private static String NEW_FIND_REPLACE = null;
	private static String RENAME_FIND_REPLACE = null;
	private static String NAME = null;
	
	private static String ERROR_EXISTANCE = null;
	
	
	// GUI Elements
	private JButton buttonOK = null;
	private JButton buttonCancel = null;
	private JTextField nameField = null;
	private JLabel errorLabel = null;
	
	// Constructors	
	public FindReplaceDialog() {
		super(Outliner.findReplace, "", true);
		
		OK = GUITreeLoader.reg.getText("ok");
		CANCEL = GUITreeLoader.reg.getText("cancel");
		NEW_FIND_REPLACE = GUITreeLoader.reg.getText("new_find_replace");
		RENAME_FIND_REPLACE = GUITreeLoader.reg.getText("rename_find_replace");
		NAME = GUITreeLoader.reg.getText("name");
		ERROR_EXISTANCE = GUITreeLoader.reg.getText("error_name_existance");
		
		buttonOK = new JButton(OK);
		buttonCancel = new JButton(CANCEL);
		nameField = new JTextField(20);
		errorLabel = new JLabel(" ");
		
		// Create the Layout
		setResizable(false);
		
		// Adding window adapter to fix problem where initial focus won't go to the textfield.
		// Solution found at: http://forums.java.sun.com/thread.jsp?forum=57&thread=124417&start=15&range=15;
		addWindowListener(
			new WindowAdapter() {
                                @Override
				public void windowOpened(WindowEvent e) {
					nameField.requestFocus();
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
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		
		// Define the Center Panel
		JLabel label = new JLabel(NAME);
		
		JPanel mainPanel = new JPanel();
		GridBagLayout mainPanelLayout = new GridBagLayout();
		mainPanel.setLayout(mainPanelLayout);
		
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(2,2,2,2);
		
		c.weighty = 1;
		c.weightx = 0;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanelLayout.setConstraints(label, c);
		mainPanel.add(label);
		
		c.weighty = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanelLayout.setConstraints(nameField, c);
		mainPanel.add(nameField);
		
		c.weighty = 1;
		c.weightx = 1;
		c.anchor = GridBagConstraints.WEST;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		mainPanelLayout.setConstraints(errorLabel, c);
		mainPanel.add(errorLabel);
		
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		// Set the default button
		getRootPane().setDefaultButton(buttonOK);
		
		pack();
	}
	
	public void show(int mode) {
		this.currentMode = mode;
		
		if (mode == MODE_NEW) {
			setTitle(NEW_FIND_REPLACE);
			nameField.setText("");
		} else if (mode == MODE_RENAME) {
			setTitle(RENAME_FIND_REPLACE);
			FindReplaceModel model = Outliner.findReplace.model;
			String name = model.getName(Outliner.findReplace.getFindReplaceItemIndex());
			nameField.setText(name);
		}
		
		errorLabel.setText(" ");
		
		nameField.requestFocus();
		
		Rectangle r = Outliner.outliner.getBounds();
		setLocation((int) (r.getCenterX() - getWidth()/2), (int) (r.getCenterY() - getHeight()/2));
		
		super.show();
	}
	
	// ActionListener Interface
        @Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(OK)) {
			ok();
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		}
	}
	
	private void ok() {
		String name = nameField.getText();
		
		// Validate Existence
		if ((name == null) || name.equals("")) {
			errorLabel.setText(ERROR_EXISTANCE);
			return;
		}
		
		// All is good so lets make the change
		FindReplaceModel model = Outliner.findReplace.model;
		
		if (currentMode == MODE_NEW) {
			model.add(model.getSize(), name, "", "", false, false);
			Outliner.findReplace.FIND_REPLACE_LIST.setSelectedIndex(model.getSize() - 1);
		} else if (currentMode == MODE_RENAME) {
			model.setName(Outliner.findReplace.getFindReplaceItemIndex(), name);
		}
		
		Outliner.findReplace.FIND_REPLACE_LIST.requestFocus();
		
		this.hide();
	}
	
	private void cancel() {
		hide();
	}
}


// Inner Classes
/**
 * Listends for changes to the text in either the "find" and "replace" text areas
 * and communicates these changes to the FindReplaceModel.
 */
class FindReplaceJTextAreaDocumentListener implements DocumentListener {
	// Constants
	public static final int TYPE_FIND = 0;
	public static final int TYPE_REPLACE = 1;
	public static final int TYPE_UNKNOWN = -1;
	
	
	// Instance Fields
	private int type = TYPE_UNKNOWN;
	private FindReplaceModel model;
	
	
	// Constructor
	public FindReplaceJTextAreaDocumentListener(
		int type,
		FindReplaceModel model
	) {
		this.type = type;
		this.model = model;
	}
	
	
	// DocumentListener Interface
        @Override
	public void changedUpdate(DocumentEvent e) {
		update(e);
	}
	
        @Override
	public void insertUpdate(DocumentEvent e) {
		update(e);
	}
	
        @Override
	public void removeUpdate(DocumentEvent e) {
		update(e);
	}
	
	
	// Methods
	private void update(DocumentEvent e) {
		javax.swing.text.Document textarea_doc = e.getDocument();
		
		// Get the text from the textarea
		String text = "";
		try {
			text = textarea_doc.getText(0, textarea_doc.getLength());
		} catch (javax.swing.text.BadLocationException ble) {
			ble.printStackTrace();
		}
		
		// Get the index?
		int currentIndex = Outliner.findReplace.getFindReplaceItemIndex();
		
		// Update the model based on the type
		if (type == TYPE_FIND) {
			model.setFind(currentIndex, text);
		} else if (type == TYPE_REPLACE) {
			model.setReplace(currentIndex, text);
		}
	}
}
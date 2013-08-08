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
import javax.swing.event.*;
import javax.swing.tree.*;
import org.xml.sax.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.6 $, $Date: 2004/02/02 10:17:42 $
 */

public class PreferencesFrame extends AbstractGUITreeJDialog implements TreeSelectionListener, ActionListener, JoeXMLConstants {

	// Constants
	private static final int MINIMUM_WIDTH = 575;
	private static final int MINIMUM_HEIGHT = 430;
 	private static final int INITIAL_WIDTH = 575;
	private static final int INITIAL_HEIGHT = 430;


	// Instance Fields
	public JTree tree = null;
	
	private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("");
	private DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

	private DefaultMutableTreeNode lastNode = rootNode;
	private int lastNodeDepth = -1;

	private HashMap panelJspMap = new HashMap();


	// Main Component Containers
	public static final JPanel RIGHT_PANEL = new JPanel();
	public static final CardLayout CARD_LAYOUT = new CardLayout();
	public static final JPanel BOTTOM_PANEL = new JPanel();
	
		
	// Button Text and Other Copy
	public static String OK = null;
	public static String CANCEL = null;
	public static String APPLY = null;
	public static String RESTORE_DEFAULTS = null;

	// Define Fields and Buttons
	public static JButton BOTTOM_OK = null;
	public static JButton BOTTOM_CANCEL = null;
	public static JButton BOTTOM_APPLY = null;


	// The Constructor
	public PreferencesFrame() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);

		// Button Text and Other Copy
		OK = GUITreeLoader.reg.getText("ok");
		CANCEL = GUITreeLoader.reg.getText("cancel");
		APPLY = GUITreeLoader.reg.getText("apply");
		RESTORE_DEFAULTS = GUITreeLoader.reg.getText("restore_defaults");

		// Define Fields and Buttons
		BOTTOM_OK = new JButton(OK);
		BOTTOM_CANCEL = new JButton(CANCEL);
		BOTTOM_APPLY = new JButton(APPLY);
	}


	// GUITreeComponent interface
        @Override
	public void startSetup(Attributes atts) {
		super.startSetup(atts);
		
		// Define the Bottom Panel
		BOTTOM_PANEL.setLayout(new FlowLayout());
		
		BOTTOM_OK.addActionListener(this);
		BOTTOM_PANEL.add(BOTTOM_OK);

		BOTTOM_CANCEL.addActionListener(this);
		BOTTOM_PANEL.add(BOTTOM_CANCEL);

		BOTTOM_APPLY.addActionListener(this);
		BOTTOM_PANEL.add(BOTTOM_APPLY);
		
		// Set the default button
		getRootPane().setDefaultButton(BOTTOM_OK);

		// Define the Right Panel
		RIGHT_PANEL.setLayout(CARD_LAYOUT);
	}
	
        @Override
	public void endSetup(Attributes atts) {
		// Define the JTree		
		tree = new JTree(treeModel);
		tree.addTreeSelectionListener(this);
		tree.setRootVisible(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Put it all together
		JScrollPane jsp = new JScrollPane(tree);
		jsp.setMinimumSize(new Dimension(165,0));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, jsp, RIGHT_PANEL);
		splitPane.setResizeWeight(0.0);
		
		getContentPane().add(BOTTOM_PANEL, BorderLayout.SOUTH);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		super.endSetup(atts);		
	}
	
	public void addPanel(AbstractPreferencesPanel panel, String title, int depth) {
		JScrollPane jsp = new JScrollPane(panel);
		panelJspMap.put(title,jsp);
		PreferencesFrame.RIGHT_PANEL.add(jsp, title);
		
		addPanelToTree(title,depth);
	}
	
	private void addPanelToTree(String name, int depth) {
		DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
		
		if (depth > lastNodeDepth) {
			lastNode.add(newNode);
		} else if (depth == lastNodeDepth) {
			((DefaultMutableTreeNode) lastNode.getParent()).add(newNode);
		} else {
			int depthDifference = lastNodeDepth - depth;
			
			TreeNode parent = lastNode.getParent();
			for (int i = 0; i < depthDifference; i++) {
				parent = parent.getParent();
			}
			
			((DefaultMutableTreeNode) parent).add(newNode);
		}
		
		treeModel.reload();

		lastNode = newNode;
		lastNodeDepth = depth;
	}
	
	// TreeSelectionListener interface
        @Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		String key = (String) node.getUserObject();
		JScrollPane jsp = (JScrollPane) panelJspMap.get(key);
		jsp.getVerticalScrollBar().setValue(0); // Scrolls the right panel back to the top whenever we change cards.
		CARD_LAYOUT.show(RIGHT_PANEL, key);
	}


	// ActionListener Interface
        @Override
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(OK)) {
			main_ok();
		} else if (e.getActionCommand().equals(APPLY)) {
			main_apply();
		} else if (e.getActionCommand().equals(CANCEL)) {
			main_cancel();
		}
	}
	
	private void main_ok() {
		main_apply();
		hide();
	}

	private void main_apply() {
		Preferences.applyTemporaryToCurrent();
		Preferences.applyCurrentToApplication();
		Preferences.saveConfigFile(Outliner.CONFIG_FILE);
		Outliner.documents.redrawAllOpenDocuments();
	}

	public void main_cancel() {
		// Restore Prefs
		Preferences.restoreTemporaryToCurrent();

		// Restore GUI
		Preferences prefs = (Preferences) GUITreeLoader.reg.get(GUITreeComponentRegistry.PREFERENCES);

		Iterator it = prefs.getPreferencesPanelKeys();
		while (it.hasNext()) {
			String key = (String) it.next();
			PreferencesPanel panel = prefs.getPreferencesPanel(key);
			panel.setToCurrent();
		}

		hide();
	}
	
	// Misc Methods
	// Grabbed from JDK1.4.0 JTree.getNextMatch(String,int,postion)
	// Simplified the code since I always want to search the entire tree from the start moving forward.
	// Also removed the uppercase code since I want to match case.
    public static TreePath getNextMatch(JTree tree, String prefix) {
		int max = tree.getRowCount();
		if (prefix == null) {
	    	throw new IllegalArgumentException();
		}

		// start search from the next/previous element froom the selected element
		int row = 0;
		do {
		    TreePath path = tree.getPathForRow(row);
		    String text = tree.convertValueToText(path.getLastPathComponent(), tree.isRowSelected(row), tree.isExpanded(row), true, row, false);
		    
		    if (text.startsWith(prefix)) {
				return path;
		    }
		    row = (row + 1 + max) % max;
		} while (row != 0);
		
		return null;
    }
}
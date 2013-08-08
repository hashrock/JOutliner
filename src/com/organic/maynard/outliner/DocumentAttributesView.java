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
 
package com.organic.maynard.outliner;

import com.organic.maynard.outliner.dom.*;
import com.organic.maynard.outliner.event.*;
import com.organic.maynard.outliner.guitree.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.14 $, $Date: 2002/12/21 02:49:41 $
 */
 
public class DocumentAttributesView extends AbstractGUITreeJDialog implements ActionListener, DocumentRepositoryListener {
	
	// Constants
	private static final int INITIAL_WIDTH = 350;
	private static final int INITIAL_HEIGHT = 400;
	private static final int MINIMUM_WIDTH = 250;
	private static final int MINIMUM_HEIGHT = 300;
	
	protected static String CLOSE = null;
	
	// GUI Elements
	protected DocumentAttributesPanel attPanel = null;
	
	protected JButton buttonCLOSE = null;
	
	
	// The Constructors
	public DocumentAttributesView() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		Outliner.documentAttributes = this;
		
		Outliner.documents.addDocumentRepositoryListener(this);
	}
	
	private void initialize() {
		CLOSE = GUITreeLoader.reg.getText("close");
		
		buttonCLOSE = new JButton(CLOSE);
		
		// Define the Bottom Panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout());
		bottomPanel.add(buttonCLOSE);
		
		getContentPane().add(bottomPanel,BorderLayout.SOUTH);
		
		// Add Listeners
		buttonCLOSE.addActionListener(this);
		
		// Define the Center Panel
		attPanel = new DocumentAttributesPanel();
		JScrollPane jsp = new JScrollPane(attPanel);
		
		getContentPane().add(jsp,BorderLayout.CENTER);
		
		// Set the default button
		getRootPane().setDefaultButton(buttonCLOSE);
	}
	
	private boolean initialized = false;
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	// Configuration
	protected JoeTree tree = null;
	
	public void configure(JoeTree tree) {
		// Lazy Instantiation
		if (!initialized) {
			initialize();
			initialized = true;
		}
		
		this.tree = tree;
		
		attPanel.update(this);
	}
	
	public void configureAndShow(JoeTree tree) {
		configure(tree);
		
		super.show();
	}
	
	// Accessors
	public DocumentAttributesPanel getDocumentAttributesPanel() {
		return this.attPanel;
	}
	
	
	// DocumentRepositoryListener Interface
        @Override
	public void documentAdded(DocumentRepositoryEvent e) {}
	
        @Override
	public void documentRemoved(DocumentRepositoryEvent e) {}
	
        @Override
	public void changedMostRecentDocumentTouched(DocumentRepositoryEvent e) {
		calculateEnabledState(e.getDocument());
	}
	
	private void calculateEnabledState(Document doc) {
		if (doc == null) {
			this.tree = null;
			if (this.isVisible()) {
				attPanel.update(this);
			}
		} else if (doc.getTree() != this.tree) {
			this.tree = doc.getTree();
			if (this.isVisible()) {
				attPanel.update(this);
			}
		}
	}
	
	
	// ActionListener Interface
        @Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(CLOSE)) {
			close();
		}
	}
	
	private void close() {
		hide();
	}
}

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

import com.organic.maynard.outliner.guitree.*;
import java.awt.*;
import javax.swing.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.11 $, $Date: 2004/01/30 00:12:42 $
 */

public class DocumentStatistics extends AbstractGUITreeJDialog {
	
	// Constants
	private static final int INITIAL_WIDTH = 200;
	private static final int INITIAL_HEIGHT = 125;
	private static final int MINIMUM_WIDTH = 200;
	private static final int MINIMUM_HEIGHT = 125;
	
	
	// GUI Components
	private static JLabel documentTitleName = null;
	private static JLabel documentTitleValue = null;
	
	private static JLabel lineCountName = null;
	private static JLabel lineCountValue = null;
	
	private static JLabel charCountName = null;
	private static JLabel charCountValue = null;
	
	
	// The Constructors
	public DocumentStatistics() {
		super(true, true, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		Outliner.statistics = this;
	}
	
	private void initialize() {
		documentTitleValue = new JLabel("");
		lineCountValue = new JLabel("");
		charCountValue = new JLabel("");
		
		documentTitleName = new JLabel(GUITreeLoader.reg.getText("document") + " ");
		lineCountName = new JLabel(GUITreeLoader.reg.getText("lines") + " ");
		charCountName = new JLabel(GUITreeLoader.reg.getText("characters") + " ");
		
		// Create the Layout
		Box vBox = Box.createVerticalBox();
		
		Box documentTitleBox = Box.createHorizontalBox();
		documentTitleBox.add(documentTitleName);
		documentTitleBox.add(documentTitleValue);
		vBox.add(documentTitleBox);
		
		vBox.add(Box.createVerticalStrut(5));
		
		Box lineCountBox = Box.createHorizontalBox();
		lineCountBox.add(lineCountName);
		lineCountBox.add(lineCountValue);
		vBox.add(lineCountBox);
		
		vBox.add(Box.createVerticalStrut(5));
		
		Box charCountBox = Box.createHorizontalBox();
		charCountBox.add(charCountName);
		charCountBox.add(charCountValue);
		vBox.add(charCountBox);
		
		getContentPane().add(vBox,BorderLayout.CENTER);
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
		
		OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
		documentTitleValue.setText(doc.getTitle());
		
		int lineCount = doc.tree.getLineCount();
		lineCountValue.setText("" + lineCount);
		
		int charCount = doc.tree.getCharCount();
		charCountValue.setText("" + charCount);
		
		super.show();
	}
}

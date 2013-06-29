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
 
package com.organic.maynard.outliner.io.formats;

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.io.*;
import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.StringTools;
import com.organic.maynard.io.FileTools;
import com.organic.maynard.xml.XMLTools;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.4 $, $Date: 2004/01/30 00:12:42 $
 */

class SelectHTMLExportStyleDialog extends AbstractOutlinerJDialog implements ActionListener {
	
	// Constants
	private static final int INITIAL_WIDTH = 200;
	private static final int INITIAL_HEIGHT = 125;
	private static final int MINIMUM_WIDTH = 200;
	private static final int MINIMUM_HEIGHT = 125;
	
	// Text Assets
	private static String OK = null;
	private static String CANCEL = null;
	
	// GUI Components
	private static JButton bProceed = null;
	private static JButton bCancel = null;
	
	private static JComboBox styles = new JComboBox();
	
	// Data
	private boolean shouldProceed = false;
	private File styleName = null;
	
	// Directories
	private static File CSS_DIR = null;
	
	// The Constructor
	public SelectHTMLExportStyleDialog(Frame owner, String title) {
		super(true, true, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		// Initialize
		CSS_DIR = new File(Outliner.PREFS_DIR + System.getProperty("com.organic.maynard.outliner.Outliner.cssdir", "css") + System.getProperty("file.separator"));
		
		OK = GUITreeLoader.reg.getText("ok");
		CANCEL = GUITreeLoader.reg.getText("cancel");
		
		bProceed = new JButton(OK);
		bCancel = new JButton(CANCEL);
		
		bProceed.addActionListener(this);
		bCancel.addActionListener(this);
		
		// Create the Layout
		Box vBox = Box.createVerticalBox();
		
		vBox.add(styles);
		
		vBox.add(Box.createVerticalStrut(5));
		
		Box buttonBox = Box.createHorizontalBox();
		buttonBox.add(bProceed);
		buttonBox.add(bCancel);
		vBox.add(buttonBox);
		
		getContentPane().add(vBox,BorderLayout.CENTER);
	}
	
	public void show() {
		// Get list of styles and populate comboBox.
		styles.removeAllItems();
		
		File[] fileNames = CSS_DIR.listFiles();
		
		for (int i = 0; i < fileNames.length; i++) {
			File file = fileNames[i];
			if (file.isFile()) {
				styles.addItem(file);
			}
		}
		
		super.show();
	}
	
	// Accessors
	public boolean shouldProceed() {
		return this.shouldProceed;
	}
	
	public String getStyle(String lineEnding) {
		return FileTools.readFileToString(this.styleName, lineEnding);
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(OK)) {
			ok();
		} else if (e.getActionCommand().equals(CANCEL)) {
			cancel();
		}
	}
	
	private void ok() {
		this.styleName = (File) styles.getSelectedItem();
		this.shouldProceed = true;
		hide();
	}
	
	private void cancel() {
		this.shouldProceed = false;
		hide();
	}
}

public class HTMLExportFileFormat extends AbstractFileFormat implements ExportFileFormat, JoeReturnCodes {
	
	private SelectHTMLExportStyleDialog dialog = null;
	// Constructors
	public HTMLExportFileFormat() {}
	
	
	// ExportFileFormat Interface
	public boolean supportsComments() {return true;}
	public boolean supportsEditability() {return false;}
	public boolean supportsMoveability() {return false;}
	public boolean supportsAttributes() {return false;}
	public boolean supportsDocumentAttributes() {return false;}
	
	public byte[] save(JoeTree tree, DocumentInfo docInfo) {
		String lineEnding = PlatformCompatibility.platformToLineEnding(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_LINE_ENDING));
		
		// Show dialog where use can pick a CSS or cancel. Do lazy instantiation since text assets won't be ready yet.
		if (dialog == null) {
			dialog = new SelectHTMLExportStyleDialog(Outliner.outliner,"Select a Style");
		}
		
		dialog.show();
		
		if (!dialog.shouldProceed()) {
			return null;
		}
		
		// Get the CSS
		String template = dialog.getStyle(lineEnding);
		
		
		// Prepare the file
		String merged = prepareFile(tree, docInfo, lineEnding, template);
		
		try {
			return merged.getBytes(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return merged.getBytes();
		}
	}
	
	private String prepareFile(JoeTree tree, DocumentInfo docInfo, String lineEnding, String template) {
		
		// Figure out max_style_depth
		int max_style_depth = 0;
		String match = "{!max_style_depth:";
		int start = template.indexOf(match);
		if (start != -1) {
			start += match.length();
			int end = template.indexOf("}", start);
			if (end != -1) {
				try {
					max_style_depth = Integer.parseInt(template.substring(start, end));
					template = StringTools.replace(template, "{!max_style_depth:" + max_style_depth + "}", "");
				} catch (Exception e) {
					System.out.println("ERROR: Could not parse max_style_depth: " + e.getMessage());
				}
			}
		}
		
		// Do replacements on the template.
		template = StringTools.replace(template, "{$encoding}",      XMLTools.escapeHTML(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE)));
		template = StringTools.replace(template, "{$title}",         XMLTools.escapeHTML(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH)));
		template = StringTools.replace(template, "{$date_created}",  XMLTools.escapeHTML(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_DATE_CREATED)));
		template = StringTools.replace(template, "{$date_modified}", XMLTools.escapeHTML(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_DATE_MODIFIED)));
		template = StringTools.replace(template, "{$owner_name}",    XMLTools.escapeHTML(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_NAME)));
		template = StringTools.replace(template, "{$owner_email}",   XMLTools.escapeHTML(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_EMAIL)));
		
		// Do Font and Color Replacements
		String fontFace = Preferences.getPreferenceString(Preferences.FONT_FACE).cur;
		template = StringTools.replace(template, "{$font_face}", fontFace);
		
		String fontSize = "" + Preferences.getPreferenceInt(Preferences.FONT_SIZE).cur;
		template = StringTools.replace(template, "{$font_size}", fontSize);
		
		String desktopBackgroundColor = convertColorToWebColorValue(Preferences.getPreferenceColor(Preferences.DESKTOP_BACKGROUND_COLOR).cur);
		template = StringTools.replace(template, "{$desktop_background_color}", desktopBackgroundColor);
		
		String panelBackgroundColor = convertColorToWebColorValue(Preferences.getPreferenceColor(Preferences.PANEL_BACKGROUND_COLOR).cur);
		template = StringTools.replace(template, "{$panel_background_color}", panelBackgroundColor);
		
		String textareaBackgroundColor = convertColorToWebColorValue(Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).cur);
		template = StringTools.replace(template, "{$textarea_background_color}", textareaBackgroundColor);
		
		String textareaForegroundColor = convertColorToWebColorValue(Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).cur);
		template = StringTools.replace(template, "{$textarea_foreground_color}", textareaForegroundColor);
		
		String textareaCommentColor = convertColorToWebColorValue(Preferences.getPreferenceColor(Preferences.TEXTAREA_COMMENT_COLOR).cur);
		template = StringTools.replace(template, "{$textarea_comment_color}", textareaCommentColor);
		
		String selectedChildColor = convertColorToWebColorValue(Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).cur);
		template = StringTools.replace(template, "{$selected_child_color}", selectedChildColor);
		
		String lineNumberColor = convertColorToWebColorValue(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_COLOR).cur);
		template = StringTools.replace(template, "{$line_number_color}", lineNumberColor);
		
		String lineNumberSelectedColor = convertColorToWebColorValue(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_COLOR).cur);
		template = StringTools.replace(template, "{$line_number_selected_color}", lineNumberSelectedColor);
		
		String lineNumberSelectedChildChildColor = convertColorToWebColorValue(Preferences.getPreferenceColor(Preferences.LINE_NUMBER_SELECTED_CHILD_COLOR).cur);
		template = StringTools.replace(template, "{$line_number_selected_child_color}", lineNumberSelectedChildChildColor);
		
		
		// Outline
		StringBuffer buf = new StringBuffer();
		
		Node node = tree.getRootNode();
		for (int i = 0; i < node.numOfChildren(); i++) {
			buildOutlineElement(node.getChild(i), lineEnding, buf, max_style_depth);
		}
		
		template = StringTools.replace(template, "{$outline}", buf.toString());
		
		return template;
	}
	
	protected static String convertColorToWebColorValue(Color c) {
		String red = Integer.toHexString(c.getRed());
		if (red.length() == 1) {
			red = "0" + red;
		}
		String green = Integer.toHexString(c.getGreen());
		if (green.length() == 1) {
			green = "0" + green;
		}
		String blue = Integer.toHexString(c.getBlue());
		if (blue.length() == 1) {
			blue = "0" + blue;
		}
		return new StringBuffer().append("#").append(red).append(green).append(blue).toString();
	}
	
	private static final String CSS_BRANCH = "branch";
	private static final String CSS_LEAF = "leaf";
	private static final String CSS_BRANCH_COMMENT = "branchcomment";
	private static final String CSS_LEAF_COMMENT = "leafcomment";
	
	private void buildOutlineElement(Node node, String lineEnding, StringBuffer buf, int max_style_depth) {
		indent(node, buf);
		
		// Calculate CSS Class
		String cssClass = "";
		if (node.isLeaf()) {
			if (node.isComment()) {
				cssClass = CSS_LEAF_COMMENT;
			} else {
				cssClass = CSS_LEAF;
			}
		} else {
			if (node.isComment()) {
				cssClass = CSS_BRANCH_COMMENT;
			} else {
				cssClass = CSS_BRANCH;
			}
		}
		
		if (node.getDepth() <= max_style_depth) {
			cssClass = cssClass + node.getDepth();
		}
		
		
		buf.append("<div class=\"").append(cssClass).append("\">").append(XMLTools.escapeHTML(node.getValue())).append(lineEnding);
		
		if (!node.isLeaf()) {
			for (int i = 0; i < node.numOfChildren(); i++) {
				buildOutlineElement(node.getChild(i), lineEnding, buf, max_style_depth);
			}
		}
		indent(node, buf);
		buf.append("</div>").append(lineEnding);
	}
	
	private void indent(Node node, StringBuffer buf) {
		for (int i = 0; i < node.getDepth(); i++) {
			buf.append("\t");
		}
	}
}
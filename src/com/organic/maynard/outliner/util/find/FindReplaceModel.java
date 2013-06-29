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

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.*;
import java.util.*;
import java.io.*;
import org.xml.sax.*;
import javax.swing.*;
import com.organic.maynard.xml.XMLTools;
import com.organic.maynard.xml.XMLProcessor;
import com.organic.maynard.io.FileTools;

/**
 * Holds the configuration for all find/replace items. Handles loading and saving
 * of the items to disk.
 * 
 * @author  $Author: maynardd $
 * @version $Revision: 1.9 $, $Date: 2004/03/21 08:08:27 $
 */

public class FindReplaceModel extends XMLProcessor {
	
	// Constants
	
	// Find/Replace Scope Modes
	public static final int MODE_CURRENT_DOCUMENT = 1;
	public static final int MODE_ALL_OPEN_DOCUMENTS = 2;
	public static final int MODE_FILE_SYSTEM = 3;
	public static final int MODE_UNKNOWN = -1;
	
	// Defaults
	/** The name of th default find/replace item. */
	protected static final String DEFAULT_NAME = "Default";
	
	// XML document elements and attributes
	private static final String E_ROOT = "root";
	private static final String E_ITEM = "item";
	
	private static final String A_NAME = "name";
	private static final String A_FIND = "find";
	private static final String A_REPLACE = "replace";
	private static final String A_REGEXP = "regexp";
	private static final String A_IGNORE_CASE = "ignore_case";
	
	private static final String A_SELECTION_MODE = "selection_mode";
	
	private static final String A_START_AT_TOP = "start_at_top";
	private static final String A_WRAP_AROUND = "wrap_around";
	private static final String A_SELECTION_ONLY = "selection_only";
	private static final String A_INCLUDE_READ_ONLY = "include_read_only";
	private static final String A_INCLUDE_READ_ONLY_ALL_DOCUMENTS = "include_read_only_all_documents";
	
	private static final String A_PATH = "path";
	private static final String A_INCLUDE_SUB_DIRS = "include_sub_dirs";
	private static final String A_MAKE_BACKUPS = "make_backups";
	private static final String A_FILE_FILTER_INCLUDE = "file_filter_include";
	private static final String A_FILE_FILTER_INCLUDE_IGNORE_CASE = "file_filter_include_ignore_case";
	private static final String A_FILE_FILTER_EXCLUDE = "file_filter_exclude";
	private static final String A_FILE_FILTER_EXCLUDE_IGNORE_CASE = "file_filter_exclude_ignore_case";
	private static final String A_DIR_FILTER_INCLUDE = "dir_filter_include";
	private static final String A_DIR_FILTER_INCLUDE_IGNORE_CASE = "dir_filter_include_ignore_case";
	private static final String A_DIR_FILTER_EXCLUDE = "dir_filter_exclude";
	private static final String A_DIR_FILTER_EXCLUDE_IGNORE_CASE = "dir_filter_exclude_ignore_case";
	
	
	// Instance Fields
	private ArrayList names = new ArrayList(); // Strings
	
	private ArrayList finds = new ArrayList(); // Strings
	private ArrayList replaces = new ArrayList(); // Strings
	private ArrayList ignoreCases = new ArrayList(); // Booleans
	private ArrayList regExps = new ArrayList(); // Booleans
	
	// Global Settings
	private int selectionMode = 0;
	
	private boolean startAtTop = false;
	private boolean wrapAround = false;
	private boolean selectionOnly = false;
	private boolean includeReadOnly = false;
	private boolean includeReadOnlyAllDocuments = false;
	
	private String path = "";
	private boolean includeSubDirs = false;
	private boolean makeBackups = false;
	private String fileFilterInclude = "";
	private boolean fileFilterIncludeIgnoreCase = false;
	private String fileFilterExclude = "";
	private boolean fileFilterExcludeIgnoreCase = false;
	private String dirFilterInclude = "";
	private boolean dirFilterIncludeIgnoreCase = false;
	private String dirFilterExclude = "";
	private boolean dirFilterExcludeIgnoreCase = false;
	
	
	// Constructors
	/**
	 * Creates a new FindReplaceModel by loading the XML based configuration file
	 * from disk.
	 */
	public FindReplaceModel() {
		super();
		
		try {
			super.process(Outliner.FIND_REPLACE_FILE);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Make sure size is at least 1.
		if (getSize() <= 0) {
			add(0,DEFAULT_NAME,"","",false,false);
		}
	}
	
	
	// Accessors
	/**
	 * Gets the number of find/replace items in this model.
	 */
	public int getSize() {
		return names.size();
	}
	
	/**
	 * Adds a new find/replace item to the model.
	 *
	 * @param i the index of the item to add.
	 * @param name the name of the item to add
	 * @param find the find text for the item.
	 * @param replace the replace text for the item.
	 * @param ignoreCase indicates if case should be ignored in the find.
	 * @param regExp indicates if the find/replace text should be interpreted as
	 *               a regular expression.
	 */
	public void add(
		int i,
		String name,
		String find,
		String replace,
		boolean ignoreCase,
		boolean regExp
	) {
		addName(i,name);
		addFind(i,find);
		addReplace(i,replace);
		addIgnoreCase(i,ignoreCase);
		addRegExp(i,regExp);
	}
	
	/**
	 * Removes the find/replace item from this model located at the provided index.
	 * Also removes it from the GUI at the same time.
	 */
	public void remove(int i) {
		if (i < names.size()) {
			// Remove from model
			names.remove(i);
			finds.remove(i);
			replaces.remove(i);
			ignoreCases.remove(i);
			regExps.remove(i);
			
			// Remove from JList
			((DefaultListModel) Outliner.findReplace.FIND_REPLACE_LIST.getModel()).removeElementAt(i);
		} else {
			System.out.println("Error: attempt to remove a find/replace item with an invalid index: " + i);
		}
	}
	
	/**
	 * Gets the name of the item found at the provided index.
	 */
	public String getName(int i) {
		return (String) this.names.get(i);
	}
	
	public void setName(int i, String s) {
		this.names.set(i, s);
		((DefaultListModel) Outliner.findReplace.FIND_REPLACE_LIST.getModel()).setElementAt(s,i);
	}
	
	public void addName(int i, String s) {
		this.names.add(i, s);
		((DefaultListModel) Outliner.findReplace.FIND_REPLACE_LIST.getModel()).insertElementAt(s,i);
	}
	
	public String getFind(int i) {
		return (String) this.finds.get(i);
	}
	
	public void setFind(int i, String s) {
		this.finds.set(i, s);
	}
	
	public void addFind(int i, String s) {
		this.finds.add(i, s);
	}
	
	
	public String getReplace(int i) {
		return (String) this.replaces.get(i);
	}
	
	public void setReplace(int i, String s) {
		this.replaces.set(i, s);
	}
	
	public void addReplace(int i, String s) {
		this.replaces.add(i, s);
	}
	
	
	public boolean getRegExp(int i) {
		return ((Boolean) this.regExps.get(i)).booleanValue();
	}
	
	public void setRegExp(int i, boolean b) {
		this.regExps.set(i, new Boolean(b));
	}
	
	public void setRegExp(int i, String b) {
		this.regExps.set(i, new Boolean(b));
	}
	
	public void addRegExp(int i, boolean b) {
		this.regExps.add(i, new Boolean(b));
	}
	
	public void addRegExp(int i, String b) {
		this.regExps.add(i, new Boolean(b));
	}
	
	
	public boolean getIgnoreCase(int i) {
		return ((Boolean) this.ignoreCases.get(i)).booleanValue();
	}
	
	public void setIgnoreCase(int i, boolean b) {
		this.ignoreCases.set(i, new Boolean(b));
	}
	
	public void setIgnoreCase(int i, String b) {
		this.ignoreCases.set(i, new Boolean(b));
	}
	
	public void addIgnoreCase(int i, boolean b) {
		this.ignoreCases.add(i, new Boolean(b));
	}
	
	public void addIgnoreCase(int i, String b) {
		this.ignoreCases.add(i, new Boolean(b));
	}
	
	
	// Scope Settings
	public int getSelectionMode() {
		return this.selectionMode;
	}
	
	public void setSelectionMode(int selectionMode) {
		this.selectionMode = selectionMode;
	}
	
	public void setSelectionMode(String selectionMode) {
		try {
			this.selectionMode = Integer.parseInt(selectionMode);
		} catch (NumberFormatException e) {
			this.selectionMode = MODE_UNKNOWN;
		}
	}
	
	public boolean getStartAtTop() {
		return this.startAtTop;
	}
	
	public void setStartAtTop(boolean startAtTop) {
		this.startAtTop = startAtTop;
	}
	
	public void setStartAtTop(String startAtTop) {
		this.startAtTop = Boolean.valueOf(startAtTop).booleanValue();
	}
	
	
	public boolean getWrapAround() {
		return this.wrapAround;
	}
	
	public void setWrapAround(boolean wrapAround) {
		this.wrapAround = wrapAround;
	}
	
	public void setWrapAround(String wrapAround) {
		this.wrapAround = Boolean.valueOf(wrapAround).booleanValue();
	}
	
	
	public boolean getSelectionOnly() {
		return this.selectionOnly;
	}
	
	public void setSelectionOnly(boolean selectionOnly) {
		this.selectionOnly = selectionOnly;
	}
	
	public void setSelectionOnly(String selectionOnly) {
		this.selectionOnly = Boolean.valueOf(selectionOnly).booleanValue();
	}
	
	
	public boolean getIncludeReadOnly() {
		return this.includeReadOnly;
	}
	
	public void setIncludeReadOnly(boolean includeReadOnly) {
		this.includeReadOnly = includeReadOnly;
	}
	
	public void setIncludeReadOnly(String includeReadOnly) {
		this.includeReadOnly = Boolean.valueOf(includeReadOnly).booleanValue();
	}
	
	public boolean getIncludeReadOnlyAllDocuments() {
		return this.includeReadOnlyAllDocuments;
	}
	
	public void setIncludeReadOnlyAllDocuments(boolean includeReadOnlyAllDocuments) {
		this.includeReadOnlyAllDocuments = includeReadOnlyAllDocuments;
	}
	
	public void setIncludeReadOnlyAllDocuments(String includeReadOnlyAllDocuments) {
		this.includeReadOnlyAllDocuments = Boolean.valueOf(includeReadOnlyAllDocuments).booleanValue();
	}
	
	
	public String getPath() {
		return this.path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	
	public boolean getIncludeSubDirs() {
		return this.includeSubDirs;
	}
	
	public void setIncludeSubDirs(boolean includeSubDirs) {
		this.includeSubDirs = includeSubDirs;
	}
	
	public void setIncludeSubDirs(String includeSubDirs) {
		this.includeSubDirs = Boolean.valueOf(includeSubDirs).booleanValue();}
	
	public boolean getMakeBackups() {
		return this.makeBackups;
	}
	
	public void setMakeBackups(boolean makeBackups) {
		this.makeBackups = makeBackups;
	}
	
	public void setMakeBackups(String makeBackups) {
		this.makeBackups = Boolean.valueOf(makeBackups).booleanValue();
	}
	
	
	public String getFileFilterInclude() {
		return this.fileFilterInclude;
	}
	
	public void setFileFilterInclude(String fileFilterInclude) {
		this.fileFilterInclude = fileFilterInclude;
	}
	
	
	public boolean getFileFilterIncludeIgnoreCase() {
		return this.fileFilterIncludeIgnoreCase;
	}
	
	public void setFileFilterIncludeIgnoreCase(boolean fileFilterIncludeIgnoreCase) {
		this.fileFilterIncludeIgnoreCase = fileFilterIncludeIgnoreCase;
	}
	
	public void setFileFilterIncludeIgnoreCase(String fileFilterIncludeIgnoreCase) {
		this.fileFilterIncludeIgnoreCase = Boolean.valueOf(fileFilterIncludeIgnoreCase).booleanValue();
	}
	
	
	public String getFileFilterExclude() {
		return this.fileFilterExclude;
	}
	
	public void setFileFilterExclude(String fileFilterExclude) {
		this.fileFilterExclude = fileFilterExclude;
	}
	
	
	public boolean getFileFilterExcludeIgnoreCase() {
		return this.fileFilterExcludeIgnoreCase;
	}
	
	public void setFileFilterExcludeIgnoreCase(boolean fileFilterExcludeIgnoreCase) {
		this.fileFilterExcludeIgnoreCase = fileFilterExcludeIgnoreCase;
	}
	
	public void setFileFilterExcludeIgnoreCase(String fileFilterExcludeIgnoreCase) {
		this.fileFilterExcludeIgnoreCase = Boolean.valueOf(fileFilterExcludeIgnoreCase).booleanValue();
	}
	
	
	public String getDirFilterInclude() {
		return this.dirFilterInclude;
	}
	
	public void setDirFilterInclude(String dirFilterInclude) {
		this.dirFilterInclude = dirFilterInclude;
	}
	
	
	public boolean getDirFilterIncludeIgnoreCase() {
		return this.dirFilterIncludeIgnoreCase;
	}
	
	public void setDirFilterIncludeIgnoreCase(boolean dirFilterIncludeIgnoreCase) {
		this.dirFilterIncludeIgnoreCase = dirFilterIncludeIgnoreCase;
	}
	
	public void setDirFilterIncludeIgnoreCase(String dirFilterIncludeIgnoreCase) {
		this.dirFilterIncludeIgnoreCase = Boolean.valueOf(dirFilterIncludeIgnoreCase).booleanValue();
	}
	
	
	public String getDirFilterExclude() {
		return this.dirFilterExclude;
	}
	
	public void setDirFilterExclude(String dirFilterExclude) {
		this.dirFilterExclude = dirFilterExclude;
	}
	
	
	public boolean getDirFilterExcludeIgnoreCase() {
		return this.dirFilterExcludeIgnoreCase;
	}
	
	public void setDirFilterExcludeIgnoreCase(boolean dirFilterExcludeIgnoreCase) {
		this.dirFilterExcludeIgnoreCase = dirFilterExcludeIgnoreCase;
	}
	
	public void setDirFilterExcludeIgnoreCase(String dirFilterExcludeIgnoreCase) {
		this.dirFilterExcludeIgnoreCase = Boolean.valueOf(dirFilterExcludeIgnoreCase).booleanValue();
	}
	
	
	// Saving the Config File
	public void saveConfigFile() {
		try {
			FileTools.dumpStringToFile(new File(Outliner.FIND_REPLACE_FILE), prepareConfigFile(), "UTF-8");
		} catch (IOException ioe) {
			JOptionPane.showMessageDialog(null, GUITreeLoader.reg.getText("message_could_not_save_find_replace_config") + ": " + ioe.getMessage());
		}
	}
	
	private String prepareConfigFile() {
		StringBuffer buf = new StringBuffer();
		
		buf.append(XMLTools.getXmlDeclaration(null)).append(PlatformCompatibility.LINE_END_DEFAULT);
		buf.append("<").append(E_ROOT);
			buf.append(" ").append(A_SELECTION_MODE).append("=\"").append(XMLTools.escapeXMLAttribute("" + getSelectionMode())).append("\"");
			buf.append(" ").append(A_START_AT_TOP).append("=\"").append(XMLTools.escapeXMLAttribute("" + getStartAtTop())).append("\"");
			buf.append(" ").append(A_WRAP_AROUND).append("=\"").append(XMLTools.escapeXMLAttribute("" + getWrapAround())).append("\"");
			buf.append(" ").append(A_SELECTION_ONLY).append("=\"").append(XMLTools.escapeXMLAttribute("" + getSelectionOnly())).append("\"");
			buf.append(" ").append(A_INCLUDE_READ_ONLY).append("=\"").append(XMLTools.escapeXMLAttribute("" + getIncludeReadOnly())).append("\"");
			buf.append(" ").append(A_INCLUDE_READ_ONLY_ALL_DOCUMENTS).append("=\"").append(XMLTools.escapeXMLAttribute("" + getIncludeReadOnlyAllDocuments())).append("\"");
			buf.append(" ").append(A_PATH).append("=\"").append(XMLTools.escapeXMLAttribute("" + getPath())).append("\"");
			buf.append(" ").append(A_INCLUDE_SUB_DIRS).append("=\"").append(XMLTools.escapeXMLAttribute("" + getIncludeSubDirs())).append("\"");
			buf.append(" ").append(A_MAKE_BACKUPS).append("=\"").append(XMLTools.escapeXMLAttribute("" + getMakeBackups())).append("\"");
			buf.append(" ").append(A_FILE_FILTER_INCLUDE).append("=\"").append(XMLTools.escapeXMLAttribute("" + getFileFilterInclude())).append("\"");
			buf.append(" ").append(A_FILE_FILTER_INCLUDE_IGNORE_CASE).append("=\"").append(XMLTools.escapeXMLAttribute("" + getFileFilterIncludeIgnoreCase())).append("\"");
			buf.append(" ").append(A_FILE_FILTER_EXCLUDE).append("=\"").append(XMLTools.escapeXMLAttribute("" + getFileFilterExclude())).append("\"");
			buf.append(" ").append(A_FILE_FILTER_EXCLUDE_IGNORE_CASE).append("=\"").append(XMLTools.escapeXMLAttribute("" + getFileFilterExcludeIgnoreCase())).append("\"");
			buf.append(" ").append(A_DIR_FILTER_INCLUDE).append("=\"").append(XMLTools.escapeXMLAttribute("" + getDirFilterInclude())).append("\"");
			buf.append(" ").append(A_DIR_FILTER_INCLUDE_IGNORE_CASE).append("=\"").append(XMLTools.escapeXMLAttribute("" + getDirFilterIncludeIgnoreCase())).append("\"");
			buf.append(" ").append(A_DIR_FILTER_EXCLUDE).append("=\"").append(XMLTools.escapeXMLAttribute("" + getDirFilterExclude())).append("\"");
			buf.append(" ").append(A_DIR_FILTER_EXCLUDE_IGNORE_CASE).append("=\"").append(XMLTools.escapeXMLAttribute("" + getDirFilterExcludeIgnoreCase())).append("\"");
		buf.append(">").append(PlatformCompatibility.LINE_END_DEFAULT);
		
		for (int i = 0; i < getSize(); i++) {
			buf.append("<").append(E_ITEM);
			
			buf.append(" ").append(A_NAME).append("=\"").append(XMLTools.escapeXMLAttribute(getName(i))).append("\"");
			buf.append(" ").append(A_FIND).append("=\"").append(XMLTools.escapeXMLAttribute(getFind(i))).append("\"");
			buf.append(" ").append(A_REPLACE).append("=\"").append(XMLTools.escapeXMLAttribute(getReplace(i))).append("\"");
			buf.append(" ").append(A_IGNORE_CASE).append("=\"").append(XMLTools.escapeXMLAttribute("" + getIgnoreCase(i))).append("\"");
			buf.append(" ").append(A_REGEXP).append("=\"").append(XMLTools.escapeXMLAttribute("" + getRegExp(i))).append("\"");
			
			buf.append("/>").append(PlatformCompatibility.LINE_END_DEFAULT);
		}
		
		XMLTools.writeElementEnd(buf, 0, PlatformCompatibility.LINE_END_DEFAULT, E_ROOT);
		return buf.toString();
	}
	
	// Sax DocumentHandler Implementation
	public void startDocument () {}
	public void endDocument () {}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		if (qName.equals(E_ITEM)) {
			int size = getSize();
			
			addName(size, atts.getValue(A_NAME));
			addFind(size, atts.getValue(A_FIND));
			addReplace(size, atts.getValue(A_REPLACE));
			addIgnoreCase(size, atts.getValue(A_IGNORE_CASE));
			addRegExp(size, atts.getValue(A_REGEXP));
		} else if (qName.equals(E_ROOT)) {
			setSelectionMode(atts.getValue(A_SELECTION_MODE));
			setStartAtTop(atts.getValue(A_START_AT_TOP));
			setWrapAround(atts.getValue(A_WRAP_AROUND));
			setSelectionOnly(atts.getValue(A_SELECTION_ONLY));
			setIncludeReadOnly(atts.getValue(A_INCLUDE_READ_ONLY));
			setIncludeReadOnlyAllDocuments(atts.getValue(A_INCLUDE_READ_ONLY_ALL_DOCUMENTS));
			
			setPath(atts.getValue(A_PATH));
			setIncludeSubDirs(atts.getValue(A_INCLUDE_SUB_DIRS));
			setMakeBackups(atts.getValue(A_MAKE_BACKUPS));
			setFileFilterInclude(atts.getValue(A_FILE_FILTER_INCLUDE));
			setFileFilterIncludeIgnoreCase(atts.getValue(A_FILE_FILTER_INCLUDE_IGNORE_CASE));
			setFileFilterExclude(atts.getValue(A_FILE_FILTER_EXCLUDE));
			setFileFilterExcludeIgnoreCase(atts.getValue(A_FILE_FILTER_EXCLUDE_IGNORE_CASE));
			setDirFilterInclude(atts.getValue(A_DIR_FILTER_INCLUDE));
			setDirFilterIncludeIgnoreCase(atts.getValue(A_DIR_FILTER_INCLUDE_IGNORE_CASE));
			setDirFilterExclude(atts.getValue(A_DIR_FILTER_EXCLUDE));
			setDirFilterExcludeIgnoreCase(atts.getValue(A_DIR_FILTER_EXCLUDE_IGNORE_CASE));
		}
	}
	
	public void endElement(String namespaceURI, String localName, String qName) {}
	public void characters(char ch[], int start, int length) throws SAXException {}
}

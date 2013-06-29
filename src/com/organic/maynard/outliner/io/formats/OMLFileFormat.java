/**
 * Copyright (C) 2003 Maynard Demmon, maynard@organic.com
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
import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.StringTools;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import com.organic.maynard.xml.XMLProcessor;
import com.organic.maynard.xml.XMLTools;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.6 $, $Date: 2004/05/18 22:12:47 $
 */

public class OMLFileFormat extends XMLProcessor implements SaveFileFormat, OpenFileFormat, JoeReturnCodes {
	
	// Constants
		/** The threshold over which we use data elements rather than text atts. */
		public static final int TEXT_THRESHOLD = 20; // TBD: this should be a pref.
		
		// XML Structure
		public static final String ELEMENT_OML = "oml";
		public static final String ELEMENT_HEAD = "head";
		public static final String ELEMENT_METADATA = "metadata";
		public static final String ELEMENT_BODY = "body";
		public static final String ELEMENT_OUTLINE = "outline";
		public static final String ELEMENT_DATA = "data";
		public static final String ELEMENT_ITEM = "item";
		
		public static final String ATTRIBUTE_TEXT = "text";
		public static final String ATTRIBUTE_TYPE = "type";
		public static final String ATTRIBUTE_CREATED = "created";
		public static final String ATTRIBUTE_MODIFIED = "modified";
		public static final String ATTRIBUTE_URL = "url";
		public static final String ATTRIBUTE_NAME = "name";
		public static final String ATTRIBUTE_VERSION = "version";
		
		// Names
		public static final String TITLE = "title";
		public static final String DATE_CREATED = "dateCreated";
		public static final String DATE_MODIFIED = "dateModified";
		public static final String OWNER_NAME = "ownerName";
		public static final String OWNER_EMAIL = "ownerEmail";
		public static final String EXPANSION_STATE = "expansionState";
		public static final String VERTICAL_SCROLL_STATE = "vertScrollState";
		public static final String WINDOW_TOP = "windowTop";
		public static final String WINDOW_LEFT = "windowLeft";
		public static final String WINDOW_BOTTOM = "windowBottom";
		public static final String WINDOW_RIGHT = "windowRight";
		
		public static final String APPLY_FONT_STYLE_FOR_COMMENTS = "applyStyleForComments";
		public static final String APPLY_FONT_STYLE_FOR_EDITABILITY = "applyStyleForEditability";
		public static final String APPLY_FONT_STYLE_FOR_MOVEABILITY = "applyStyleForMoveability";
		
		public static final String IS_READ_ONLY_ATTS_LIST = "readOnlyAttsList";
		
		public static final String IS_EDITABLE = "isEditable";
		public static final String IS_MOVEABLE = "isMoveable";
		public static final String IS_COMMENT = "isComment";
	
	// Constructors
	public OMLFileFormat() {
		super();
	}
	
	private String name = null;
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	
	// SaveFileFormat Interface
	public byte[] save(JoeTree tree, DocumentInfo docInfo) {
		StringBuffer buf = prepareFile(tree, docInfo);
		
		try {
			return buf.toString().getBytes(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return buf.toString().getBytes();
		}
	}
	
	public boolean supportsComments() {return true;}
	public boolean supportsEditability() {return true;}
	public boolean supportsMoveability() {return true;}	
	public boolean supportsAttributes() {return true;}
	public boolean supportsDocumentAttributes() {return true;}
	
	private StringBuffer prepareFile(JoeTree tree, DocumentInfo docInfo) {
		String lineEnding = PlatformCompatibility.platformToLineEnding(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_LINE_ENDING));
		
		StringBuffer buf = new StringBuffer();
		
		buf.append("<?xml version=\"1.0\" encoding=\"").append(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE)).append("\"?>").append(lineEnding);
		buf.append("<").append(ELEMENT_OML).append(" ").append(ATTRIBUTE_VERSION).append("=\"1.0\">").append(lineEnding);
		
		buf.append("<").append(ELEMENT_HEAD).append(">").append(lineEnding);
		
			appendMetadataElement(buf, TITLE, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH), lineEnding);
			appendMetadataElement(buf, DATE_CREATED, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_DATE_CREATED), lineEnding);
			appendMetadataElement(buf, DATE_MODIFIED, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_DATE_MODIFIED), lineEnding);
			appendMetadataElement(buf, OWNER_NAME, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_NAME), lineEnding);
			appendMetadataElement(buf, OWNER_EMAIL, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_EMAIL), lineEnding);
			appendMetadataElement(buf, EXPANSION_STATE, docInfo.getExpandedNodesStringShifted(1), lineEnding);
			appendMetadataElement(buf, VERTICAL_SCROLL_STATE, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_VERTICAL_SCROLL_STATE), lineEnding);
			appendMetadataElement(buf, WINDOW_TOP, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_WINDOW_TOP), lineEnding);
			appendMetadataElement(buf, WINDOW_LEFT, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_WINDOW_LEFT), lineEnding);
			appendMetadataElement(buf, WINDOW_BOTTOM, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_WINDOW_BOTTOM), lineEnding);
			appendMetadataElement(buf, WINDOW_RIGHT, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_WINDOW_RIGHT), lineEnding);
			
			appendMetadataElement(buf, APPLY_FONT_STYLE_FOR_COMMENTS, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_COMMENTS), lineEnding);
			appendMetadataElement(buf, APPLY_FONT_STYLE_FOR_EDITABILITY, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_EDITABILITY), lineEnding);
			appendMetadataElement(buf, APPLY_FONT_STYLE_FOR_MOVEABILITY, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_MOVEABILITY), lineEnding);
			
			buildMetadataElements(tree, lineEnding, buf);
		
		buf.append("</").append(ELEMENT_HEAD).append(">").append(lineEnding);
		
		buf.append("<").append(ELEMENT_BODY).append(">").append(lineEnding);
			Node node = tree.getRootNode();
			for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
				buildOutlineElement(node.getChild(i), lineEnding, buf);
			}
		buf.append("</").append(ELEMENT_BODY).append(">").append(lineEnding);
		
		buf.append("</").append(ELEMENT_OML).append(">").append(lineEnding);
		return buf;
	}
	
	private void appendMetadataElement(StringBuffer buf, String name, String value, String line_ending) {
		if (name == null || name.length() == 0) {
			return;
		}
		buf.append("\t");
		buf.append("<").append(ELEMENT_METADATA).append(" ").append(ATTRIBUTE_NAME).append("=\"").append(escapeXMLAttribute(name)).append("\">");
		XMLTools.writeCDATA(buf, value);
		buf.append("</").append(ELEMENT_METADATA).append(">").append(line_ending);
	}
	
	private void buildMetadataElements(JoeTree tree, String line_ending, StringBuffer buf) {
		Iterator it = tree.getAttributeKeys();
		
		StringBuffer readOnlyAtts = new StringBuffer();
		if (it != null) {
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = tree.getAttribute(key);
				
				if (isReservedMetadataName(key)) {
					continue;
				}
				
				boolean isReadOnly = tree.isReadOnly(key);
				if (isReadOnly) {
					readOnlyAtts.append(key).append(" ");
				}
				appendMetadataElement(buf, key, value.toString(), line_ending);
			}
		}
		
		if (readOnlyAtts.length() > 0) {
			appendMetadataElement(buf, IS_READ_ONLY_ATTS_LIST, readOnlyAtts.toString().trim(), line_ending);
		}
	}
	
	private boolean isReservedMetadataName(String name) {
		if (TITLE.equals(name)) {
			return true;
		} else if (DATE_CREATED.equals(name)) {
			return true;
		} else if (DATE_MODIFIED.equals(name)) {
			return true;
		} else if (OWNER_NAME.equals(name)) {
			return true;
		} else if (OWNER_EMAIL.equals(name)) {
			return true;
		} else if (EXPANSION_STATE.equals(name)) {
			return true;
		} else if (VERTICAL_SCROLL_STATE.equals(name)) {
			return true;
		} else if (WINDOW_TOP.equals(name)) {
			return true;
		} else if (WINDOW_LEFT.equals(name)) {
			return true;
		} else if (WINDOW_BOTTOM.equals(name)) {
			return true;
		} else if (WINDOW_RIGHT.equals(name)) {
			return true;
		} else if (APPLY_FONT_STYLE_FOR_COMMENTS.equals(name)) {
			return true;
		} else if (APPLY_FONT_STYLE_FOR_EDITABILITY.equals(name)) {
			return true;
		} else if (APPLY_FONT_STYLE_FOR_MOVEABILITY.equals(name)) {
			return true;
		} else if (IS_READ_ONLY_ATTS_LIST.equals(name)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void buildOutlineElement(Node node, String line_ending, StringBuffer buf) {
		indent(node, buf);
		buf.append("<").append(ELEMENT_OUTLINE);
		
		Object attribute = node.getAttribute(ATTRIBUTE_CREATED);
		if (attribute != null) {
			buf.append(" ").append(ATTRIBUTE_CREATED).append("=\"").append(escapeXMLAttribute(attribute.toString())).append("\"");
		}
		attribute = node.getAttribute(ATTRIBUTE_MODIFIED);
		if (attribute != null) {
			buf.append(" ").append(ATTRIBUTE_MODIFIED).append("=\"").append(escapeXMLAttribute(attribute.toString())).append("\"");
		}
		attribute = node.getAttribute(ATTRIBUTE_TYPE);
		if (attribute != null) {
			buf.append(" ").append(ATTRIBUTE_TYPE).append("=\"").append(escapeXMLAttribute(attribute.toString())).append("\"");
		}
		attribute = node.getAttribute(ATTRIBUTE_URL);
		if (attribute != null) {
			buf.append(" ").append(ATTRIBUTE_URL).append("=\"").append(escapeXMLAttribute(attribute.toString())).append("\"");
		}
		
		String node_value = node.getValue();
		if (node_value.length() <= TEXT_THRESHOLD) {
			buf.append(" ").append(ATTRIBUTE_TEXT).append("=\"").append(escapeXMLAttribute(node_value)).append("\"");
		}
		
		buf.append(">").append(line_ending);
			
			// Data Element
			appendDataElement(node, buf, node.getValue(), line_ending);
			
			// Item Elements
			if (node.getCommentState() == Node.COMMENT_TRUE) {
				appendItemElement(node, buf, IS_COMMENT, "true", line_ending);
			} else if (node.getCommentState() == Node.COMMENT_FALSE) {
				appendItemElement(node, buf, IS_COMMENT, "false", line_ending);
			}
			
			if (node.getEditableState() == Node.EDITABLE_TRUE) {
				appendItemElement(node, buf, IS_EDITABLE, "true", line_ending);
			} else if (node.getEditableState() == Node.EDITABLE_FALSE) {
				appendItemElement(node, buf, IS_EDITABLE, "false", line_ending);
			}
			
			if (node.getMoveableState() == Node.MOVEABLE_TRUE) {
				appendItemElement(node, buf, IS_MOVEABLE, "true", line_ending);
			} else if (node.getMoveableState() == Node.MOVEABLE_FALSE) {
				appendItemElement(node, buf, IS_MOVEABLE, "false", line_ending);
			}
			
			buildItemElements(node, buf, line_ending);
			
			// Child Outlines
			for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
				buildOutlineElement(node.getChild(i), line_ending, buf);
			}
		indent(node, buf);
		buf.append("</").append(ELEMENT_OUTLINE).append(">").append(line_ending);
	}
	
	/**
	 * Writes a tab based indent to the StringBuffer.
	 */
	public static void indent(Node node, StringBuffer buf) {
		for (int i = 0, limit = node.getDepth(); i < limit; i++) {
			buf.append("\t");
		}
	}
	
	private boolean isReservedItemName(String name) {
		if (ATTRIBUTE_TEXT.equals(name)) {
			return true;
		} else if (ATTRIBUTE_CREATED.equals(name)) {
			return true;
		} else if (ATTRIBUTE_MODIFIED.equals(name)) {
			return true;
		} else if (ATTRIBUTE_TYPE.equals(name)) {
			return true;
		} else if (ATTRIBUTE_URL.equals(name)) {
			return true;
		} else if (IS_COMMENT.equals(name)) {
			return true;
		} else if (IS_EDITABLE.equals(name)) {
			return true;
		} else if (IS_MOVEABLE.equals(name)) {
			return true;
		} else if (IS_READ_ONLY_ATTS_LIST.equals(name)) {
			return true;
		} else {
			return false;
		}
	}
	
	private void buildItemElements(Node node, StringBuffer buf, String line_ending) {
		Iterator it = node.getAttributeKeys();
		
		StringBuffer readOnlyAtts = new StringBuffer();
		if (it != null) {
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = node.getAttribute(key);
				
				//if (isReservedItemName(key)) {
				//	continue;
				//}
				
				boolean isReadOnly = node.isReadOnly(key);
				if (isReadOnly) {
					readOnlyAtts.append(key).append(" ");
				}
				appendItemElement(node, buf, key, value.toString(), line_ending);
			}
		}
		
		if (readOnlyAtts.length() > 0) {
			appendItemElement(node, buf, IS_READ_ONLY_ATTS_LIST, readOnlyAtts.toString().trim(), line_ending);
		}
	}
	
	private void appendItemElement(Node node, StringBuffer buf, String name, String value, String line_ending) {
		if (name == null || name.length() == 0) {
			return;
		}
		indent(node, buf);
		buf.append("\t");
		buf.append("<").append(ELEMENT_ITEM).append(" ").append(ATTRIBUTE_NAME).append("=\"").append(escapeXMLAttribute(name)).append("\">");
		XMLTools.writeCDATA(buf, value);
		buf.append("</").append(ELEMENT_ITEM).append(">").append(line_ending);
	}
	
	private void appendDataElement(Node node, StringBuffer buf, String value, String line_ending) {
		if (value == null || value.length() <= TEXT_THRESHOLD) {
			return;
		}
		indent(node, buf);
		buf.append("\t");
		buf.append("<").append(ELEMENT_DATA).append(">");
		XMLTools.writeCDATA(buf, value);
		buf.append("</").append(ELEMENT_DATA).append(">").append(line_ending);
	}
	
	
	// Utility Methods
	private String escapeXMLAttribute(String text) {
		text = StringTools.replace(text, "&", "&amp;");
		text = StringTools.replace(text, "<", "&lt;");
		text = StringTools.replace(text, "\"", "&quot;");
		text = StringTools.replace(text, ">", "&gt;");
		return text;
	}
	
	
	// OpenFileFormat Interface
	private boolean errorOccurred = false;
	private DocumentInfo docInfo = null;
	private JoeTree tree = null;
	private Node currentParent = null;
	
	public int open(JoeTree tree, DocumentInfo docInfo, InputStream stream) {
		// Set the objects we are going to populate.
		this.docInfo = docInfo;
		this.tree = tree;
		
		// Do the Parsing
		int success = FAILURE;
		errorOccurred = false;
		
		try {
			super.process(stream, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE));
			if (errorOccurred) {
				System.out.println("Error Occurred in OMLFileFormat");
				success = FAILURE;
				return success;
			}
			success = SUCCESS;
		} catch (SAXException e) {
			System.out.println("SAXException: " + e.getMessage());
			success = FAILURE;
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
			success = FAILURE;
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
			success = FAILURE;
		}
		
		// Cleanup
		super.reset();
		this.tree = null;
		this.docInfo = null;
		this.currentParent = null;
		
		return success;
	}
	
	// Sax DocumentHandler Implementation
	public void startDocument () {
		this.currentParent = tree.getRootNode();
		
		// Clear out any existing children.
		while (currentParent.numOfChildren() > 0) {
			currentParent.removeChild(currentParent.getLastChild());
		}
	}
	
	public void endDocument () {}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		if (qName.equals(ELEMENT_OUTLINE)) {
			NodeImpl node = new NodeImpl(tree, "");
			
			String readOnlyAttsList = new String("");
						
			for (int i = 0, limit = atts.getLength(); i < limit; i++) {
				String attName = atts.getQName(i);
				String attValue = atts.getValue(i);
				
				if (attName.equals(ATTRIBUTE_TEXT)) {
					node.setValue(attValue);
					
				} else {
					node.setAttribute(attName, attValue);
				}
			}
			
			currentParent.appendChild(node);
			currentParent = node;
		}
		
		super.startElement(namespaceURI, localName, qName, atts);
	}
	
	public void endElement(String namespaceURI, String localName, String qName) {
		String elementName = (String) elements_stack.peek();
		Attributes atts = (Attributes) attributes_stack.peek();
		String text = ((StringBuffer) characters_stack.peek()).toString();
		
		if (qName.equals(ELEMENT_OUTLINE)) {
			Node parentNode = currentParent.getParent();
			currentParent = parentNode;
		} else if (elementName.equals(ELEMENT_METADATA)) {
			String name = atts.getValue(ATTRIBUTE_NAME);
			if (!handleMetaDataCharacters(name, text)) {
				System.out.println("Error handling metadata element.");
			}
		} else if (elementName.equals(ELEMENT_ITEM)) {
			String name = atts.getValue(ATTRIBUTE_NAME);
			if (!handleItemCharacters(name, text)) {
				System.out.println("Error handling item element.");
			}
		} else if (elementName.equals(ELEMENT_DATA)) {
			if (!handleDataCharacters(text)) {
				System.out.println("Error handling data element.");
			}
		}
		
		super.endElement(namespaceURI, localName, qName);
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		super.characters(ch, start, length);
	}
	
	private boolean handleItemCharacters(String name, String text) {
		if (name.equals(IS_READ_ONLY_ATTS_LIST)) {
			if (text != null) {
				// TBD: ensure that read_only_atts_list is process last. Means it must come last when file is saved.
				StringTokenizer tok = new StringTokenizer(text);
				while (tok.hasMoreTokens()) {
					String key = tok.nextToken();
					currentParent.setReadOnly(key, true);
				}
			}
			
		} else if (name.equals(IS_MOVEABLE)) {
			if (text != null && text.equals("false")) {
				currentParent.setMoveableState(Node.MOVEABLE_FALSE);
			} else if (text != null && text.equals("true")) {
				currentParent.setMoveableState(Node.MOVEABLE_TRUE);
			}
			
		} else if (name.equals(IS_EDITABLE)) {
			if (text != null && text.equals("false")) {
				currentParent.setEditableState(Node.EDITABLE_FALSE);
			} else if (text != null && text.equals("true")) {
				currentParent.setEditableState(Node.EDITABLE_TRUE);
			}
			
		} else if (name.equals(IS_COMMENT)) {
			if (text != null && text.equals("false")) {
				currentParent.setCommentState(Node.COMMENT_FALSE);
			} else if (text != null && text.equals("true")) {
				currentParent.setCommentState(Node.COMMENT_TRUE);
			}
			
		} else {
			currentParent.setAttribute(name, text);
		}
		
		return true;
	}
	
	private boolean handleDataCharacters(String text) {
		currentParent.setValue(currentParent.getValue() + text);
		return true;
	}
	
	private boolean handleMetaDataCharacters(String name, String text) {
		if (name.equals(IS_READ_ONLY_ATTS_LIST)) {
			if (text != null) {
				// TBD: ensure that read_only_atts_list is process last. Means it must come last when file is saved.
				StringTokenizer tok = new StringTokenizer(text);
				while (tok.hasMoreTokens()) {
					String key = tok.nextToken();
					tree.setReadOnly(key, true);
				}
			}
		} else if (name.equals(TITLE)) {
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_TITLE, text);
		
		} else if (name.equals(DATE_CREATED)) {
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_DATE_CREATED, text);
		
		} else if (name.equals(DATE_MODIFIED)) {
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_DATE_MODIFIED, text);
		
		} else if (name.equals(OWNER_NAME)) {
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_NAME, text);
		
		} else if (name.equals(OWNER_EMAIL)) {
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_EMAIL, text);
		
		} else if (name.equals(EXPANSION_STATE)) {
			docInfo.setExpandedNodesStringShifted(text, -1);
		
		} else if (name.equals(VERTICAL_SCROLL_STATE)) {
			try {
				PropertyContainerUtil.setPropertyAsInt(docInfo, DocumentInfo.KEY_VERTICAL_SCROLL_STATE, Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		
		} else if (name.equals(WINDOW_TOP)) {
			try {
				PropertyContainerUtil.setPropertyAsInt(docInfo, DocumentInfo.KEY_WINDOW_TOP, Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		
		} else if (name.equals(WINDOW_LEFT)) {
			try {
				PropertyContainerUtil.setPropertyAsInt(docInfo, DocumentInfo.KEY_WINDOW_LEFT, Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		
		} else if (name.equals(WINDOW_BOTTOM)) {
			try {
				PropertyContainerUtil.setPropertyAsInt(docInfo, DocumentInfo.KEY_WINDOW_BOTTOM, Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		
		} else if (name.equals(WINDOW_RIGHT)) {
			try {
				PropertyContainerUtil.setPropertyAsInt(docInfo, DocumentInfo.KEY_WINDOW_RIGHT, Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		
		} else if (name.equals(APPLY_FONT_STYLE_FOR_COMMENTS)) {
			PropertyContainerUtil.setPropertyAsBoolean(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_COMMENTS, Boolean.valueOf(text).booleanValue());
		
		} else if (name.equals(APPLY_FONT_STYLE_FOR_EDITABILITY)) {
			PropertyContainerUtil.setPropertyAsBoolean(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_EDITABILITY, Boolean.valueOf(text).booleanValue());
		
		} else if (name.equals(APPLY_FONT_STYLE_FOR_MOVEABILITY)) {
			PropertyContainerUtil.setPropertyAsBoolean(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_MOVEABILITY, Boolean.valueOf(text).booleanValue());
		
		} else {
			boolean isReadOnly = tree.isReadOnly(name);
			tree.setAttribute(name, text, isReadOnly);
		}
		
		return true; // TBD: what types of conditons would cause this to return false?
	}
	
	
	
	// ErrorHandler Interface
	public void error(SAXParseException e) {
		System.out.println("SAXParserException Error: " + e.getMessage());
		this.errorOccurred = true;
	}
	
	public void fatalError(SAXParseException e) {
		System.out.println("SAXParserException Fatal Error: " + e.getMessage());
		this.errorOccurred = true;
	}
	
	public void warning(SAXParseException e) {
		System.out.println("SAXParserException Warning: " + e.getMessage());
		this.errorOccurred = true;
	}
	
	
	// File Extensions
	private HashMap extensions = new HashMap();
	
	public void addExtension(String ext, boolean isDefault) {
		extensions.put(ext, new Boolean(isDefault));
	}
	
	public void removeExtension(String ext) {
		extensions.remove(ext);
	}
	
	public String getDefaultExtension() {
		Iterator i = getExtensions();
		while (i.hasNext()) {
			String key = (String) i.next();
			Boolean value = (Boolean) extensions.get(key);
			
			if (value.booleanValue()) {
				return key;
			}
		}
		
		return null;
	}
	
	public Iterator getExtensions() {
		return extensions.keySet().iterator();
	}

	public boolean extensionExists(String ext) {
		Iterator it = getExtensions();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (ext.equals(key)) {
				return true;
			}
		}
		
		return false;
	}
}

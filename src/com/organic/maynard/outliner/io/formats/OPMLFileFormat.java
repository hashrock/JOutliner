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
import java.io.*;
import java.util.*;
import com.organic.maynard.util.string.StringTools;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import com.organic.maynard.xml.XMLProcessor;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.8 $, $Date: 2004/05/19 06:33:45 $
 */

public class OPMLFileFormat extends XMLProcessor implements SaveFileFormat, OpenFileFormat, JoeReturnCodes {
	
	// Constants
	public static final String ELEMENT_OPML = "opml";
	public static final String ELEMENT_HEAD = "head";
	public static final String ELEMENT_TITLE = "title";
	public static final String ELEMENT_DATE_CREATED = "dateCreated";
	public static final String ELEMENT_DATE_MODIFIED = "dateModified";
	public static final String ELEMENT_OWNER_NAME = "ownerName";
	public static final String ELEMENT_OWNER_EMAIL = "ownerEmail";
	public static final String ELEMENT_EXPANSION_STATE = "expansionState";
	public static final String ELEMENT_VERTICAL_SCROLL_STATE = "vertScrollState";
	public static final String ELEMENT_WINDOW_TOP = "windowTop";
	public static final String ELEMENT_WINDOW_LEFT = "windowLeft";
	public static final String ELEMENT_WINDOW_BOTTOM = "windowBottom";
	public static final String ELEMENT_WINDOW_RIGHT = "windowRight";
	public static final String ELEMENT_BODY = "body";
	public static final String ELEMENT_OUTLINE = "outline";
	public static final String ELEMENT_DOCUMENT_ATTRIBUTE = "documentAttribute";
	
	public static final String ELEMENT_APPLY_FONT_STYLE_FOR_COMMENTS = "applyStyleForComments";
	public static final String ELEMENT_APPLY_FONT_STYLE_FOR_EDITABILITY = "applyStyleForEditability";
	public static final String ELEMENT_APPLY_FONT_STYLE_FOR_MOVEABILITY = "applyStyleForMoveability";
	
	public static final String ATTRIBUTE_TEXT = "text";
	public static final String ATTRIBUTE_KEY = "key";
	
	public static final String ATTRIBUTE_CREATED = "created";
	public static final String ATTRIBUTE_MODIFIED = "modified";
	
	public static final String ATTRIBUTE_IS_READ_ONLY = "readOnly"; //Another way of saying isEditable.
	public static final String ATTRIBUTE_IS_READ_ONLY_ATTS_LIST = "readOnlyAttsList"; 
	
	public static final String ATTRIBUTE_IS_EDITABLE = "isEditable";
	public static final String ATTRIBUTE_IS_MOVEABLE = "isMoveable";
	public static final String ATTRIBUTE_IS_COMMENT = "isComment";
	
	// Constructors
	public OPMLFileFormat() {
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
		buf.append("<").append(ELEMENT_OPML).append(" version=\"1.0\">").append(lineEnding);
		
		buf.append("<").append(ELEMENT_HEAD).append(">").append(lineEnding);
		
		buf.append("<").append(ELEMENT_TITLE).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_PATH))).append("</").append(ELEMENT_TITLE).append(">").append(lineEnding); // We'll use path for the title since that is how our outliner difines window titles.
		buf.append("<").append(ELEMENT_DATE_CREATED).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_DATE_CREATED))).append("</").append(ELEMENT_DATE_CREATED).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_DATE_MODIFIED).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_DATE_MODIFIED))).append("</").append(ELEMENT_DATE_MODIFIED).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_OWNER_NAME).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_NAME))).append("</").append(ELEMENT_OWNER_NAME).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_OWNER_EMAIL).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_EMAIL))).append("</").append(ELEMENT_OWNER_EMAIL).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_EXPANSION_STATE).append(">").append(escapeXMLText(docInfo.getExpandedNodesStringShifted(1))).append("</").append(ELEMENT_EXPANSION_STATE).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_VERTICAL_SCROLL_STATE).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_VERTICAL_SCROLL_STATE))).append("</").append(ELEMENT_VERTICAL_SCROLL_STATE).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_WINDOW_TOP).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_WINDOW_TOP))).append("</").append(ELEMENT_WINDOW_TOP).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_WINDOW_LEFT).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_WINDOW_LEFT))).append("</").append(ELEMENT_WINDOW_LEFT).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_WINDOW_BOTTOM).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_WINDOW_BOTTOM))).append("</").append(ELEMENT_WINDOW_BOTTOM).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_WINDOW_RIGHT).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_WINDOW_RIGHT))).append("</").append(ELEMENT_WINDOW_RIGHT).append(">").append(lineEnding);
		
		buf.append("<").append(ELEMENT_APPLY_FONT_STYLE_FOR_COMMENTS).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_COMMENTS))).append("</").append(ELEMENT_APPLY_FONT_STYLE_FOR_COMMENTS).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_APPLY_FONT_STYLE_FOR_EDITABILITY).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_EDITABILITY))).append("</").append(ELEMENT_APPLY_FONT_STYLE_FOR_EDITABILITY).append(">").append(lineEnding);
		buf.append("<").append(ELEMENT_APPLY_FONT_STYLE_FOR_MOVEABILITY).append(">").append(escapeXMLText(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_MOVEABILITY))).append("</").append(ELEMENT_APPLY_FONT_STYLE_FOR_MOVEABILITY).append(">").append(lineEnding);
		
		buildDocumentAttributes(tree, lineEnding, buf);
		
		buf.append("</").append(ELEMENT_HEAD).append(">").append(lineEnding);
		
		buf.append("<").append(ELEMENT_BODY).append(">").append(lineEnding);
		Node node = tree.getRootNode();
		for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
			buildOutlineElement(node.getChild(i), lineEnding, buf);
		}
		buf.append("</").append(ELEMENT_BODY).append(">").append(lineEnding);
		
		buf.append("</").append(ELEMENT_OPML).append(">").append(lineEnding);
		return buf;
	}
	
	private void buildDocumentAttributes(JoeTree tree, String lineEnding, StringBuffer buf) {
		Iterator it = tree.getAttributeKeys();
		if (it != null) {
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = tree.getAttribute(key);
				boolean isReadOnly = tree.isReadOnly(key);
				
				buf.append("<").append(ELEMENT_DOCUMENT_ATTRIBUTE);
				buf.append(" ").append(ATTRIBUTE_KEY).append("=\"").append(escapeXMLAttribute(key)).append("\"");
				if (isReadOnly) {
					buf.append(" ").append(ATTRIBUTE_IS_READ_ONLY).append("=\"true\"");
				}
				buf.append(">");
				buf.append(escapeXMLText(value.toString()));
				buf.append("</").append(ELEMENT_DOCUMENT_ATTRIBUTE).append(">").append(lineEnding);
			}
		}
	}
	
	private void buildOutlineElement(Node node, String lineEnding, StringBuffer buf) {
		indent(node, buf);
		buf.append("<").append(ELEMENT_OUTLINE).append(" ");
		
		if (node.getCommentState() == Node.COMMENT_TRUE) {
			buf.append(ATTRIBUTE_IS_COMMENT).append("=\"true\" ");
		} else if (node.getCommentState() == Node.COMMENT_FALSE) {
			buf.append(ATTRIBUTE_IS_COMMENT).append("=\"false\" ");
		}
		
		if (node.getEditableState() == Node.EDITABLE_TRUE) {
			buf.append(ATTRIBUTE_IS_EDITABLE).append("=\"true\" ");
		} else if (node.getEditableState() == Node.EDITABLE_FALSE) {
			buf.append(ATTRIBUTE_IS_EDITABLE).append("=\"false\" ");
		}
		
		if (node.getMoveableState() == Node.MOVEABLE_TRUE) {
			buf.append(ATTRIBUTE_IS_MOVEABLE).append("=\"true\" ");
		} else if (node.getMoveableState() == Node.MOVEABLE_FALSE) {
			buf.append(ATTRIBUTE_IS_MOVEABLE).append("=\"false\" ");
		}
		
		buf.append(ATTRIBUTE_TEXT).append("=\"").append(escapeXMLAttribute(node.getValue())).append("\"");
		buildAttributes(node, buf);
		
		if (node.isLeaf()) {
			buf.append("/>").append(lineEnding);
		} else {
			buf.append(">").append(lineEnding);
			
			for (int i = 0, limit = node.numOfChildren(); i < limit; i++) {
				buildOutlineElement(node.getChild(i), lineEnding, buf);
			}
			
			indent(node, buf);
			buf.append("</").append(ELEMENT_OUTLINE).append(">").append(lineEnding);		
		}	
	}
	
	private void indent(Node node, StringBuffer buf) {
		for (int i = 0, limit = node.getDepth(); i < limit; i++) {
			buf.append("\t");
		}
	}
	
	private void buildAttributes(Node node, StringBuffer buf) {
		Iterator it = node.getAttributeKeys();
		
		StringBuffer readOnlyAtts = new StringBuffer();
		if (it != null) {
			while (it.hasNext()) {
				String key = (String) it.next();
				Object value = node.getAttribute(key);
				boolean isReadOnly = node.isReadOnly(key);
				if (isReadOnly) {
					readOnlyAtts.append(key).append(" ");
				}
				buf.append(" ").append(key).append("=\"").append(escapeXMLAttribute(value.toString())).append("\"");
			}
		}
		
		if (readOnlyAtts.length() > 0) {
			buf.append(" ").append(ATTRIBUTE_IS_READ_ONLY_ATTS_LIST).append("=\"").append(readOnlyAtts.toString().trim()).append("\"");
		}
	}
	
	private String escapeXMLAttribute(String text) {
		text = StringTools.replace(text, "&", "&amp;");
		text = StringTools.replace(text, "<", "&lt;");
		text = StringTools.replace(text, "\"", "&quot;");
		text = StringTools.replace(text, ">", "&gt;");
		return text;
	}
	
	private String escapeXMLText(String text) {
		text = StringTools.replace(text, "&", "&amp;");
		text = StringTools.replace(text, "<", "&lt;");
		text = StringTools.replace(text, "]]>", "]]&gt;");
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
				System.out.println("Error Occurred in OPMLFileFormat");
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
					
				} else if (attName.equals(ATTRIBUTE_IS_READ_ONLY_ATTS_LIST)) {
					if (attValue != null) {
						readOnlyAttsList = attValue;
					}
					
				} else if (attName.equals(ATTRIBUTE_IS_MOVEABLE)) {
					if (attValue != null && attValue.equals("false")) {
						node.setMoveableState(Node.MOVEABLE_FALSE);
					} else if (attValue != null && attValue.equals("true")) {
						node.setMoveableState(Node.MOVEABLE_TRUE);
					}
					
				} else if (attName.equals(ATTRIBUTE_IS_EDITABLE)) {
					if (attValue != null && attValue.equals("false")) {
						node.setEditableState(Node.EDITABLE_FALSE);
					} else if (attValue != null && attValue.equals("true")) {
						node.setEditableState(Node.EDITABLE_TRUE);
					}
					
				} else if (attName.equals(ATTRIBUTE_IS_COMMENT)) {
					if (attValue != null && attValue.equals("false")) {
						node.setCommentState(Node.COMMENT_FALSE);
					} else if (attValue != null && attValue.equals("true")) {
						node.setCommentState(Node.COMMENT_TRUE);
					}
					
				} else if (attName.equals(ATTRIBUTE_CREATED)) {
					node.setAttribute(attName, attValue, true);
					
				} else if (attName.equals(ATTRIBUTE_MODIFIED)) {
					node.setAttribute(attName, attValue, true);
					
				} else {
					node.setAttribute(attName, attValue);
				}
			}
			
			// Set ReadOnly Property for Attributes
			StringTokenizer tok = new StringTokenizer(readOnlyAttsList);
			while (tok.hasMoreTokens()) {
				String key = tok.nextToken();
				node.setReadOnly(key, true);
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
			
		} else if (elementName.equals(ELEMENT_TITLE)) {
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_TITLE, text);
		
		} else if (elementName.equals(ELEMENT_DATE_CREATED)) {
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_DATE_CREATED, text);
		
		} else if (elementName.equals(ELEMENT_DATE_MODIFIED)) {
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_DATE_MODIFIED, text);
		
		} else if (elementName.equals(ELEMENT_OWNER_NAME)) {
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_NAME, text);
		
		} else if (elementName.equals(ELEMENT_OWNER_EMAIL)) {
			PropertyContainerUtil.setPropertyAsString(docInfo, DocumentInfo.KEY_OWNER_EMAIL, text);
		
		} else if (elementName.equals(ELEMENT_EXPANSION_STATE)) {
			docInfo.setExpandedNodesStringShifted(text, -1);
		
		} else if (elementName.equals(ELEMENT_VERTICAL_SCROLL_STATE)) {
			try {
				PropertyContainerUtil.setPropertyAsInt(docInfo, DocumentInfo.KEY_VERTICAL_SCROLL_STATE, Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		
		} else if (elementName.equals(ELEMENT_WINDOW_TOP)) {
			try {
				PropertyContainerUtil.setPropertyAsInt(docInfo, DocumentInfo.KEY_WINDOW_TOP, Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		
		} else if (elementName.equals(ELEMENT_WINDOW_LEFT)) {
			try {
				PropertyContainerUtil.setPropertyAsInt(docInfo, DocumentInfo.KEY_WINDOW_LEFT, Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		
		} else if (elementName.equals(ELEMENT_WINDOW_BOTTOM)) {
			try {
				PropertyContainerUtil.setPropertyAsInt(docInfo, DocumentInfo.KEY_WINDOW_BOTTOM, Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		
		} else if (elementName.equals(ELEMENT_WINDOW_RIGHT)) {
			try {
				PropertyContainerUtil.setPropertyAsInt(docInfo, DocumentInfo.KEY_WINDOW_RIGHT, Integer.parseInt(text));
			} catch (NumberFormatException e) {}
		
		} else if (elementName.equals(ELEMENT_APPLY_FONT_STYLE_FOR_COMMENTS)) {
			PropertyContainerUtil.setPropertyAsBoolean(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_COMMENTS, Boolean.valueOf(text).booleanValue());
		
		} else if (elementName.equals(ELEMENT_APPLY_FONT_STYLE_FOR_EDITABILITY)) {
			PropertyContainerUtil.setPropertyAsBoolean(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_EDITABILITY, Boolean.valueOf(text).booleanValue());
		
		} else if (elementName.equals(ELEMENT_APPLY_FONT_STYLE_FOR_MOVEABILITY)) {
			PropertyContainerUtil.setPropertyAsBoolean(docInfo, DocumentInfo.KEY_APPLY_FONT_STYLE_FOR_MOVEABILITY, Boolean.valueOf(text).booleanValue());
		
		} else if (elementName.equals(ELEMENT_DOCUMENT_ATTRIBUTE)) {
			String key = atts.getValue(ATTRIBUTE_KEY);
			boolean isReadOnly = tree.isReadOnly(key);
			tree.setAttribute(key, text, isReadOnly);
		}
		
		super.endElement(namespaceURI, localName, qName);
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		super.characters(ch, start, length);
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

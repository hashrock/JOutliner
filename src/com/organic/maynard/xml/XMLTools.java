/**
 * Copyright (C) 2004 Maynard Demmon, maynard@organic.com
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

package com.organic.maynard.xml;

import java.util.*;
import com.organic.maynard.util.string.StringTools;

/**
 * A collection of static methods useful for working with XML.
 */
public class XMLTools {
	// Constants
	/**
	 * The version of XML that will be written by the getXmlDeclaration method
	 * if no version String is provided.
	 */
	public static final String DEFAULT_XML_VERSION = "1.0";
	
	
	// Constructors
	/** Empty constructor. */
	private XMLTools() {}
	
	
	// Class Methods
	
	// String Escapes
	/**
	 * Escapes the provided String for use within an element's attribute of an 
	 * XML document.
	 */
	public static String escapeXMLAttribute(String text) {
		text = StringTools.replace(text, "&", "&amp;");
		text = StringTools.replace(text, "<", "&lt;");
		text = StringTools.replace(text, "\"", "&quot;");
		return text;
	}
	
	/**
	 * Escapes the provided String for use as PCDATA within an XML document.
	 */
	public static String escapeXMLText(String text) {
		text = StringTools.replace(text, "&", "&amp;");
		text = StringTools.replace(text, "<", "&lt;");
		text = StringTools.replace(text, "]]>", "]]&gt;");
		return text;
	}
	
	/**
	 * Escapes the provided String for use as a comment within an XML document.
	 * This will convert all instances of the String "-->" to "--&gt;".
	 */
	public static String escapeXMLComment(String text) {
		text = StringTools.replace(text, "-->", "--&gt;");
		return text;
	}
	
	/**
	 * Escapes the provided String for use as a CDATA block within an XML document.
	 * This will convert all instances of the String "]]>" to "]]]]><![CDATA[>".
	 */
	public static String escapeCDATA(String text) {
		text = StringTools.replace(text, "]]>", "]]]]><![CDATA[>");
		return text;
	}
	
	// Validity Checks
	/**
	 * Tests if the provided String would make a valid XML attribute name.
	 *
	 * The algorithm used to test the validity is:
	 * Must match (Letter | '_' | ':') (Letter | Digit | '.' | '-' | '_' | ':')*
	 * XML allows CombiningChar | Extender but I'm gonna be more restrictive since it's easier.
	 * at some point we should improve this or find some code that already does this.
	 */
	public static boolean isValidXMLAttributeName(String text) {
		if (text == null) {
			return false;
		}
		
		char[] chars = text.toCharArray();
		
		if (chars.length <= 0) {
			return false;
		}
		
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			
			if (i == 0) {
				if (Character.isLetter(c) || c == '_' || c == ':') {
					continue;
				} else {
					return false;
				}
			} else {
				if (Character.isLetterOrDigit(c) || c == '.' || c == '-' || c == '_' || c == ':') {
					continue;
				} else {
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Just uses the attribute name test. TBD: enhance to be element name specific.
	 */
	public static boolean isValidXMLElementName(String text) {
		return isValidXMLAttributeName(text);
	}
	
	// XML Document Writing
	/**
	 * Writes the start of an XML element to the provided StringBuffer.
	 * @param buf the buffer to write to.
	 * @param depth the number of indent levels to indent.
	 * @param empty indicates if this is an empty element or not.
	 * @param line_ending the line ending to use at the end of the element. If null
	 *                    is provided then no line ending will be appended.
	 * @param name the name of the element.
	 * @param attributes a map of attributes which will be inserted into the element.
	 */
	public static void writeElementStart(
		StringBuffer buf, 
		int depth, 
		boolean empty, 
		String line_ending, 
		String name, 
		Map attributes
	) throws IllegalArgumentException {
		if (isValidXMLElementName(name)) {
			indent(buf, depth);
			buf.append("<").append(name);
			if (attributes != null) {
				Iterator it = attributes.keySet().iterator();
				while (it.hasNext()) {
					Object key = it.next();
					Object value = attributes.get(key);
					try {
						writeAttribute(buf, key.toString(), value.toString());
					} catch (NullPointerException npe) {
						throw new IllegalArgumentException("Attribute map contained a null for key: [" + key + "] value: [" + value + "]");
					}
				}
			}
			if (empty) {
				buf.append("/>");
			} else {
				buf.append(">");
			}
			if (line_ending != null) {
				buf.append(line_ending);
			}
		} else {
			throw new IllegalArgumentException("Invalid element name: " + name);
		}
	}
	
	/**
	 * Writes the end of an XML element to the provided StringBuffer.
	 * @param buf the buffer to write to.
	 * @param depth the number of indent levels to indent.
	 * @param line_ending the line ending to use at the end of the element. If null
	 *                    is provided then no line ending will be appended.
	 * @param name the name of the element.
	 */
	public static void writeElementEnd(
		StringBuffer buf, 
		int depth,
		String line_ending, 
		String name
	) throws IllegalArgumentException {
		if (isValidXMLElementName(name)) {
			indent(buf, depth);
			buf.append("</").append(name).append(">");
			if (line_ending != null) {
				buf.append(line_ending);
			}
		} else {
			throw new IllegalArgumentException("Invalid element name: " + name);
		}
	}
	
	/**
	 * Writes parsed character data to the provided StringBuffer.
	 * @param buf the buffer to write to.
	 * @param text the text to append.
	 */
	public static void writePCData(StringBuffer buf, String text) {
		buf.append(escapeXMLText(text));
	}
	
	/**
	 * Writes parsed character data to the provided StringBuffer.
	 * @param buf the buffer to write to.
	 * @param text the text to append.
	 */
	public static void writeCDATA(StringBuffer buf, String cdata) {
		buf.append("<![CDATA[");
		buf.append(escapeCDATA(cdata));
		buf.append("]]>");
	}
	
	/**
	 * Writes an XML comment to the provided StringBuffer.
	 * @param buf the buffer to write to.
	 * @param depth the number of indent levels to indent.
	 * @param line_ending the line ending to use at the end of the comment. If null
	 *                    is provided then no line ending will be appended.
	 * @param comment the text of the comment
	 */
	public static void writeComment(
		StringBuffer buf, 
		int depth, 
		String line_ending, 
		String comment
	) {
			indent(buf, depth);
			buf.append("<!--");
			buf.append(escapeXMLComment(comment));
			buf.append("-->");
			if (line_ending != null) {
				buf.append(line_ending);
			}
	}
	
	/**
	 * Writes an attribute to the provided StringBuffer.
	 * @param buf the buffer to write to.
	 * @param name the name of the attribute.
	 * @param value the value of the attribute.
	 */
	private static void writeAttribute(
		StringBuffer buf, 
		String name, 
		String value
	) throws IllegalArgumentException {
		if (isValidXMLAttributeName(name)) {
			buf.append(" ").append(name).append("=\"").append(escapeXMLAttribute(value)).append("\"");
		} else {
			throw new IllegalArgumentException("Invalid attribute name: " + name);
		}
	}
	
	/**
	 * Writes a tab based indent to the StringBuffer.
	 * @param buf the buffer to write to.
	 * @param depth the number of indent levels to append.
	 */
	private static void indent(StringBuffer buf, int depth) {
		for (int i = 0; i < depth; i++) {
			buf.append("\t");
		}
	}
	
	// Miscellaneous
	/**
	 * Gets the XML version declaration for an XML document with the default
	 * version id.
	 */
	public static String getXMLDeclaration() {
		return getXmlDeclaration(null);
	}
	
	/**
	 * Gets the XML version declaration for an XML document.
	 */
	public static String getXmlDeclaration(String version) {
		if (version == null) {
			version = DEFAULT_XML_VERSION;
		}
		
		return new StringBuffer().append("<?xml version=\"").append(version).append("\"?>").toString();
	}
	
	// HTML Related
	/**
	 * Escapes the provided text for output as text in an HTML document.
	 * @param text the text to escape.
	 */
	public static String escapeHTML(String text) {
		text = StringTools.replace(text,"&","&amp;");
		text = StringTools.replace(text,"<","&lt;");
		text = StringTools.replace(text,">","&gt;");
		text = StringTools.replace(text,"\"","&quot;");
		return text;
	}
	
	/**
	 * Unescapes the provided text into an HTML document.
	 * @param text the text to enescape.
	 */
	public static String unescapeHTML(String text) {
		text = StringTools.replace(text,"&amp;","&");
		text = StringTools.replace(text,"&lt;","<");
		text = StringTools.replace(text,"&gt;",">");
		text = StringTools.replace(text,"&quot;","\"");
		return text;
	}
}

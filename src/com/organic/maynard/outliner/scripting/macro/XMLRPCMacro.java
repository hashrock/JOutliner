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
 
package com.organic.maynard.outliner.scripting.macro;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.util.string.*;
import java.util.*;
import org.apache.xmlrpc.*;
import com.organic.maynard.xml.XMLTools;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2004/02/02 10:17:42 $
 */

public class XMLRPCMacro extends MacroImpl {
	
	// Constants
	private static final String E_XMLRPC = "xmlrpc";
	private static final String E_URL = "url";
	private static final String E_CALL = "xmlrpc_call";
	private static final String E_REPLACE = "replace";
	
	// Instance Fields
	private boolean replace = false;
	private String url = "http://127.0.0.1/RPC2";
	private String xmlrpcCall = "";
	
	// Class Fields
	private static XMLRPCMacroConfig macroConfig = new XMLRPCMacroConfig();
	
	
	// The Constructors
	public XMLRPCMacro() {
		this("");
	}
	
	public XMLRPCMacro(String name) {
		super(name, true, Macro.COMPLEX_UNDOABLE);
	}
	
	
	// Accessors
	public String getURL() {return url;}
	public void setURL(String url) {this.url = url;}
	
	public boolean isReplacing() {return this.replace;}
	public void setReplacing(boolean replace) {this.replace = replace;}
	
	public String getCall() {return xmlrpcCall;}
	public void setCall(String xmlrpcCall) {this.xmlrpcCall = xmlrpcCall;}
	
	
	// Macro Interface
	public MacroConfig getConfigurator() {return this.macroConfig;}
	public void setConfigurator(MacroConfig macroConfig) {}
	
	public NodeRangePair process(NodeRangePair nodeRangePair) {
		// Get the selected text
		String requestXmlString = null;
		String firstChunk = "";
		String lastChunk = "";
		
		boolean textSelection = false;
		if ((nodeRangePair.startIndex != -1) && (nodeRangePair.endIndex != -1)) {
			textSelection = true;
			
			requestXmlString = nodeRangePair.node.getValue();
			firstChunk = requestXmlString.substring(0,nodeRangePair.startIndex);
			lastChunk = requestXmlString.substring(nodeRangePair.endIndex, requestXmlString.length());
			requestXmlString = requestXmlString.substring(nodeRangePair.startIndex, nodeRangePair.endIndex);
		} else {
			StringBuffer buf = new StringBuffer();
			nodeRangePair.node.getRecursiveValue(buf, Preferences.LINE_END_STRING, true);
			// Trim last line ending
			buf.delete(buf.length() - Preferences.LINE_END_STRING.length(), buf.length());
			requestXmlString = buf.toString();		
		}
		
		if (!xmlrpcCall.equals("")) {
			// If xmlrpcCall is not empty then munge it
			requestXmlString = munge(requestXmlString);
		} else {
			// Trim leading crap before the XML declaration
			int startIndex = requestXmlString.indexOf("<");
			if (startIndex > 0) {
				requestXmlString = requestXmlString.substring(startIndex, requestXmlString.length());
			}
		}
		
		// Instantiate a Client and make the request
		try {
			XmlRpcClient client = new XmlRpcClient(url);
			Object obj = client.execute(requestXmlString);
			StringBuffer buf = new StringBuffer();
			convertObjectToString(obj, buf, 0);
			String text = buf.toString();
			
			Node replacementNode = null;
			
			// Do the right replacement for the selection type.
			if (textSelection) {
				text = Replace.replace(text, "\t", "");
				text = Replace.replace(text, "\r", "");
				text = Replace.replace(text, "\n", "");
				nodeRangePair.node.setValue(firstChunk + text + lastChunk);
				nodeRangePair.startIndex = firstChunk.length();
				nodeRangePair.endIndex = nodeRangePair.startIndex + text.length();
			} else {
				replacementNode = PadSelection.pad(text, nodeRangePair.node.getTree(), nodeRangePair.node.getDepth(), PlatformCompatibility.LINE_END_UNIX).getFirstChild();
				nodeRangePair.node = replacementNode;
				nodeRangePair.startIndex = -1;
				nodeRangePair.endIndex = -1;
			}
			
			// Display the result
			if (isReplacing()) {
				return nodeRangePair;
			} else {
				System.out.println(obj.toString());
				return null;
			}
			
		} catch (Exception e) {
			System.out.println("Exception: " + e);
			return null;
		}
	}
	
	private String munge(String text) {
		return Replace.replace(xmlrpcCall, "{$value}", text);
	}
	
	private void convertObjectToString(Object obj, StringBuffer buf, int depth) {
		for (int i = 0; i < depth; i++) {
			buf.append("\t");
		}
		
		if (obj instanceof Vector) {
			Vector v = (Vector) obj;
			
			buf.append("vector:\n");
			for (int i = 0; i < v.size(); i++) {
				convertObjectToString(v.elementAt(i), buf, depth + 1);
			}
			
		} else if (obj instanceof Hashtable) {
			Hashtable h = (Hashtable) obj;
			
			buf.append("hashtable:\n");
			
			Enumeration myEnum = h.keys();
			while (myEnum.hasMoreElements()) {
				Object key = myEnum.nextElement();
				Object value = h.get(key);
				convertObjectToString(key, buf, depth + 1);
				convertObjectToString(value, buf, depth + 2);
			}
			
		} else {
			buf.append(obj.toString()).append("\n");
		}
	}
	
	
	// Saving the Macro
	protected void prepareFile (StringBuffer buf) {
		buf.append(XMLTools.getXmlDeclaration(null)).append("\n");
		
		XMLTools.writeElementStart(buf, 0, false, "\n", E_XMLRPC, null);
			
			XMLTools.writeElementStart(buf, 0, false, null, E_URL, null);
				XMLTools.writePCData(buf, getURL());
			XMLTools.writeElementEnd(buf, 0, "\n", E_URL);
			
			XMLTools.writeElementStart(buf, 0, false, null, E_CALL, null);
				XMLTools.writePCData(buf, getCall());
			XMLTools.writeElementEnd(buf, 0, "\n", E_CALL);
			
			XMLTools.writeElementStart(buf, 0, false, null, E_REPLACE, null);
				XMLTools.writePCData(buf, "" + isReplacing());
			XMLTools.writeElementEnd(buf, 0, "\n", E_REPLACE);
			
		XMLTools.writeElementEnd(buf, 0, "\n", E_XMLRPC);
	}
	
	
	// Sax DocumentHandler Implementation
	protected void handleCharacters(String elementName, String text) {
		if (elementName.equals(E_URL)) {
			StringBuffer existingText = new StringBuffer(getURL());
			existingText.append(text);
			setURL(existingText.toString());
		} else if (elementName.equals(E_CALL)) {
			StringBuffer existingText = new StringBuffer(getCall());
			existingText.append(text);
			setCall(existingText.toString());
		} else if (elementName.equals(E_REPLACE)) {
			setReplacing(Boolean.valueOf(text).booleanValue());
		}
	}
}
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
import com.organic.maynard.util.string.*;
import com.organic.maynard.xml.XMLTools;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.2 $, $Date: 2004/02/02 07:31:59 $
 */

public class HTMLEscapeMacro extends MacroImpl {
	
	// Constants
	private static final String E_ESCAPE = "escape";
	
	// Instance Fields
	private boolean escape = true;
	
	// Class Fields
	private static HTMLEscapeMacroConfig macroConfig = new HTMLEscapeMacroConfig();
	
	
	// The Constructors
	public HTMLEscapeMacro() {
		this("");
	}
	
	public HTMLEscapeMacro(String name) {
		super(name, true, Macro.SIMPLE_UNDOABLE);
	}
	
	
	// Accessors
	public boolean isEscaping() {return this.escape;}
	public void setEscaping(boolean escape) {this.escape = escape;}
	
	
	// Macro Interface
	public MacroConfig getConfigurator() {return this.macroConfig;}
	public void setConfigurator(MacroConfig macroConfig) {}
	
	public NodeRangePair process(NodeRangePair nodeRangePair) {
		Node node = nodeRangePair.node;
		
		boolean textSelection = false;
		if ((nodeRangePair.startIndex != -1) && (nodeRangePair.endIndex != -1)) {
			textSelection = true;
		}
		
		String text = node.getValue();
		String firstChunk = "";
		String lastChunk = "";
		if (textSelection) {
			firstChunk = text.substring(0,nodeRangePair.startIndex);
			lastChunk = text.substring(nodeRangePair.endIndex,text.length());
			text = text.substring(nodeRangePair.startIndex,nodeRangePair.endIndex);
		}
		
		int lengthBefore = text.length();
		text = transform(text);
		int lengthAfter = text.length();
		
		int difference = lengthAfter - lengthBefore;
		
		if (textSelection) {
			nodeRangePair.endIndex += difference;
			nodeRangePair.startIndex = nodeRangePair.endIndex;
		}
		
		node.setValue(firstChunk + text + lastChunk);
		return nodeRangePair;
	}
	
	private String transform(String text) {
		if (isEscaping()) {
			return XMLTools.escapeHTML(text);
		} else {
			try {
				return XMLTools.unescapeHTML(text);
			} catch (Exception e) {
				return text;
			}
		}
	}
	
	
	// Saving the Macro
	protected void prepareFile (StringBuffer buf) {
		buf.append(XMLTools.getXmlDeclaration(null) + "\n");
		XMLTools.writeElementStart(buf, 0, false, null, E_ESCAPE, null);
			XMLTools.writePCData(buf, "" + isEscaping());
		XMLTools.writeElementEnd(buf, 0, "\n", E_ESCAPE);
	}
	
	
	// Sax DocumentHandler Implementation
	protected void handleCharacters(String elementName, String text) {
		if (elementName.equals(E_ESCAPE)) {
			setEscaping(Boolean.valueOf(text).booleanValue());
		}
	}
}
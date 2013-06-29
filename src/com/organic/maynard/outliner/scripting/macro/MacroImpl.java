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
import javax.swing.*;
import com.organic.maynard.util.string.*;
import java.io.*;
import org.xml.sax.*;
import java.util.*;
import com.organic.maynard.io.FileTools;
import com.organic.maynard.xml.XMLTools;
import com.organic.maynard.xml.XMLProcessor;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.2 $, $Date: 2004/02/02 10:17:42 $
 */

public abstract class MacroImpl extends XMLProcessor implements Macro {

	// Constants
	private static final String SAVE_EXT = ".txt";
	
	
	// Class Fields
  //private static Parser parser = new com.jclark.xml.sax.Driver();
	private static ArrayList elementStack = new ArrayList();
	
	
	// Instance Fields
	private String name = null;
	private boolean undoable = true;
	private int undoableType = Macro.NOT_UNDOABLE;
	
	
	// The Constructors
	public MacroImpl() {
		super();
	}
	
	public MacroImpl(String name, boolean undoable, int undoableType) {
		super();
		this.name = name;
		this.undoable = undoable;
		this.undoableType = undoableType;
		
		//parser.setDocumentHandler(this);
		//parser.setErrorHandler(this);
	}
	
	
	// Macro Interface
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getFileName() {
		return getName() + SAVE_EXT;
	}
	
	public boolean isUndoable() {
		return undoable;
	}
	
	public int getUndoableType() {
		return undoableType;
	}
	
	abstract public NodeRangePair process(NodeRangePair nodeRangePair);
	
	public boolean init(File file) {
		try {
			errorOccurred = false;
			//FileInputStream fileInputStream = new FileInputStream(file);
			//parser.parse(new InputSource(fileInputStream));
			super.process(file.getPath());
			if (errorOccurred) {
				return false;
			}
			return true;
		} catch (SAXException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean save(File file) {
		StringBuffer buf = new StringBuffer();
		
		prepareFile(buf);
		
		FileTools.dumpStringToFile(file, buf.toString());
		
		return true;
	}
	
	protected abstract void prepareFile(StringBuffer buf);
	
	// Sax DocumentHandler Implementation
	public void startDocument () {}
	
	public void endDocument () {}
	
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
		elementStack.add(qName);
	}
	
	public void endElement(String namespaceURI, String localName, String qName) {
		elementStack.remove(elementStack.size() - 1);
	}
	
	public void characters(char ch[], int start, int length) throws SAXException {
		String text = new String(ch, start, length);
		String elementName = (String) elementStack.get(elementStack.size() - 1);
		
		handleCharacters(elementName, text);
	}
	
	protected abstract void handleCharacters(String elementName, String text);
	
	
	// ErrorHandler Interface
	protected static boolean errorOccurred = false;
	
	public void error(SAXParseException e) {
		System.out.println("SAXParserException Error: " + e);
		this.errorOccurred = true;
	}
	
	public void fatalError(SAXParseException e) {
		System.out.println("SAXParserException Fatal Error: " + e);
		this.errorOccurred = true;
	}
	
	public void warning(SAXParseException e) {
		System.out.println("SAXParserException Warning: " + e);
		this.errorOccurred = true;
	}
}
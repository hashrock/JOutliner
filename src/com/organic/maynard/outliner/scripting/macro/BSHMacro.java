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

import javax.swing.JOptionPane;

import bsh.Interpreter;
import bsh.NameSpace;

import com.organic.maynard.outliner.Node;
import com.organic.maynard.outliner.NodeRangePair;
import com.organic.maynard.outliner.scripting.script.BSHScriptConfig;
import com.organic.maynard.outliner.scripting.script.Script;
import com.organic.maynard.outliner.scripting.script.ScriptConfig;
import com.organic.maynard.xml.XMLTools;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2004/02/02 10:17:42 $
 */

public class BSHMacro extends MacroImpl implements Script {
	
	// Constants
	private static final String E_SCRIPT = "script";
	
	
	// Instance Fields
	protected String script = "";
	
	
	// Class Fields
	private static BSHMacroConfig macroConfig = new BSHMacroConfig();
	private static BSHScriptConfig scriptConfig = new BSHScriptConfig();
	
	
	// The Constructors
	public BSHMacro() {
		this("");
	}
	
	public BSHMacro(String name) {
		super(name, true, Macro.COMPLEX_UNDOABLE);
	}
	
	
	// Accessors
	public String getScript() {return script;}
	public void setScript(String script) {this.script = script;}
	
	
	// Macro Interface
	public MacroConfig getConfigurator() {return this.macroConfig;}
	public void setConfigurator(MacroConfig macroConfig) {}
		
	public NodeRangePair process(NodeRangePair nodeRangePair) {
		Node node = nodeRangePair.node;
		
		// Create the mini tree from the replacement pattern
		if (script.equals("")) {
			return null;
		}
		
		Node replacementNode = node.cloneClean();
		
		try {
			Interpreter bsh = new Interpreter();
			NameSpace nameSpace = new NameSpace(bsh.getClassManager(), "outliner");
			nameSpace.importPackage("com.organic.maynard.outliner");
			
			bsh.setNameSpace(nameSpace);
			bsh.set("node", replacementNode);
			bsh.set("nodeRangePair", nodeRangePair);
			bsh.eval(script);
		} catch (Exception e) {
			System.out.println("BSH Exception: " + e.getMessage());
			
			JOptionPane.showMessageDialog(node.getTree().getDocument(), "BSH Exception: " + e.getMessage());
			
			return null;
		}
		
		nodeRangePair.node = replacementNode;
		nodeRangePair.startIndex = -1;
		nodeRangePair.endIndex = -1;
		
		return nodeRangePair;
	}
	
	
	// Saving the Macro
	protected void prepareFile (StringBuffer buf) {
		buf.append(XMLTools.getXmlDeclaration(null) + "\n");
		XMLTools.writeElementStart(buf, 0, false, null, E_SCRIPT, null);
			XMLTools.writePCData(buf, getScript());
		XMLTools.writeElementEnd(buf, 0, "\n", E_SCRIPT);
	}
	
	
	// Sax DocumentHandler Implementation
	protected void handleCharacters(String elementName, String text) {
		if (elementName.equals(E_SCRIPT)) {
			StringBuffer existingScript = new StringBuffer(getScript());
			existingScript.append(text);
			setScript(existingScript.toString());
		}
	}
	
	
	// Script Interface
	private boolean isStartup = false;
	private boolean isShutdown = false;
	
	public boolean isStartupScript() {return isStartup;}
	public void setStartupScript(boolean b) {this.isStartup = b;}
	
	public boolean isShutdownScript() {return isShutdown;}
	public void setShutdownScript(boolean b) {this.isShutdown = b;}
	
	public ScriptConfig getScriptConfigurator() {return this.scriptConfig;}
	public void setScriptConfigurator(ScriptConfig scriptConfig) {}
	
	private Interpreter bsh = new Interpreter();
	
	public void process() throws Exception {
		// Create the mini tree from the replacement pattern
		if (script.equals("")) {
			return;
		}
		
		NameSpace nameSpace = new NameSpace(bsh.getClassManager(), "outliner");
		nameSpace.importPackage("com.organic.maynard.outliner");
		bsh.setNameSpace(nameSpace);
		bsh.eval(script);
	}
}
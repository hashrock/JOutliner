/**
 * Copyright (C) 2002 Paul Scott-Murphy, paul@scott-murphy.com
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
 *  - The name of the copyright holder may not be used to endorse or
 *    promote products derived from this software without specific
 *    prior written permission. 
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

package com.psm.wiki;

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.io.*;
import com.organic.maynard.outliner.io.formats.AbstractFileFormat;
import com.organic.maynard.outliner.*;
import java.io.*;
import java.util.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.5 $, $Date: 2004/01/30 00:12:43 $
 */

interface WikiParserHandler {
	void start();
	void end();
	void startElement(String type, String value, boolean isComment);
	void line(String line);
	void endElement();
	String currentElement();
}

class WikiParserException extends Exception {
	WikiParserException(String reason) {
		super(reason);
	}
}

class WikiHandler implements WikiParserHandler {
	DocumentInfo docInfo;
	JoeTree tree;
	Node current_node;
	Stack node_stack;
	
	WikiHandler(DocumentInfo docInfo, JoeTree tree) {
		this.docInfo = docInfo;
		this.tree = tree;
		this.node_stack = new Stack();
	}
	
	public void start() {
		current_node = tree.getRootNode();
		while (current_node.numOfChildren() > 0) {
			current_node.removeChild(current_node.getLastChild());
		}
	}
	
	public void end() {}
	
	public void startElement(String type, String value, boolean isComment) {
		NodeImpl node = new NodeImpl(tree, "");
		if (isComment) {
			node.setCommentState(Node.COMMENT_TRUE);
		} else {
			node.setCommentState(Node.COMMENT_FALSE);
		}
		node.setAttribute("type", type, false);
		node.setValue(value);
		node_stack.push(node);
		current_node.appendChild(node);
		current_node = node;
	}
	
	public void endElement() {
		Node parent_node = current_node.getParent();
		if (parent_node != null) {
			current_node = parent_node;
			node_stack.pop();
		}
	}
	
	public void line(String line) {
		String value = current_node.getValue();
		current_node.setValue(value + " " + line);
	}
	
	public String currentElement() {
		return current_node.getValue();
	}
}

class WikiFormatter {
	private DocumentInfo docInfo;
	private JoeTree tree;
	
	WikiFormatter(DocumentInfo docInfo, JoeTree tree) {
		this.docInfo = docInfo;
		this.tree = tree;
	}
	
	public StringBuffer write() {
		String eol = PlatformCompatibility.platformToLineEnding(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_LINE_ENDING));
		StringBuffer buf = new StringBuffer();
		
		Node node = tree.getRootNode();
		for (int i = 0; i < node.numOfChildren(); i++) {
			buildWikiElement(node.getChild(i), eol, buf);
		}
		
		// Remove all instances where the "\" char is used to escape quotes
		//
		int length = buf.length();
		for (int i = 0; i < length; i++) {
			if (buf.charAt(i) == '\\') {
				buf.deleteCharAt(i);
			}
		}
		
		return buf;
	}
	
	private void buildWikiElement(Node node, String eol, StringBuffer buf) {
		if (node.getCommentState() == Node.COMMENT_TRUE) {
			buildWikiElementComment(node, eol, buf);
		} else {
			buildWikiElementNormal(node, eol, buf);
		}
	}
	
	private void buildWikiElementComment(Node node, String eol, StringBuffer buf) {
		buf.append("{{{").append(eol);
		buildWikiElementNoComments(node, eol, buf);
		buf.append("}}}").append(eol);
	}
	
	private void buildWikiElementNormal(Node node, String eol, StringBuffer buf) {
		buildSingleWikiElement(node, eol, buf);
		
		for (int i = 0; i < node.numOfChildren(); i++) {
			buildWikiElement(node.getChild(i), eol, buf);
		}
	}
	
	private void buildWikiElementNoComments(Node node, String eol, StringBuffer buf) {
		buildSingleWikiElement(node, eol, buf);
		
		for (int i = 0; i < node.numOfChildren(); i++) {
			buildWikiElementNoComments(node.getChild(i), eol, buf);
		}
	}
	
	private void buildSingleWikiElement(Node node, String eol, StringBuffer buf) {
		String type = (String) node.getAttribute("type");
		if (type != null) {
			if (type.equals("heading")) {
				int depth = node.getDepth();
				for (int i = 0; i < depth + 1; i++) {
					buf.append("=");
				}
				buf.append(" " + node.getValue() + " ");
				for (int i = 0; i < depth + 1; i++) {
					buf.append("=");
				}
			} else if (type.equals("bullet")) {
				indent(node, buf);
				buf.append("* ");
				buf.append(node.getValue());
			} else if (type.equals("number")) {
				indent(node, buf);
				buf.append("1. ");
				buf.append(node.getValue());
			} else if (type.equals("link")) {
				indent(node, buf);
				buf.append("[\"");
				buf.append(node.getValue());
				buf.append("\"]");
			} else {
				indent(node, buf);
				buf.append(node.getValue());
				if (!node.isLastChild() && node.isLeaf()) {
				    buf.append(eol);
				}
			}
		} else {
			indent(node, buf);
			buf.append(node.getValue());
			if (!node.isLastChild() && node.isLeaf()) {
				buf.append(eol);
			}
		}
		
		buf.append(eol);
	}
	
	private void indent(Node node, StringBuffer buf) {
		int depth = node.getDepth();
		for (int i = 0; i < depth; i++) {
			buf.append(" ");
		}
	}
}

class WikiParser {
	private static final int LINE_UNKNOWN = 0;
	private static final int LINE_EMPTY = 1;
	private static final int LINE_BODY = 2;
	private static final int LINE_HEADING = 3;
	private static final int LINE_BULLET = 4;
	private static final int LINE_NUMBER = 5;
	private static final int LINE_BLOCK_START = 6;
	private static final int LINE_BLOCK_END = 7;
	private static final int LINE_COMMENT = 8;
	private static final int LINE_LINK = 9;
	private static final int LINE_RULE = 10;
	private static final int LINE_MACRO = 11;
	
	private WikiHandler handler;
	private int heading_level;
	private int level;
	private String line;
	
	public WikiParser(WikiHandler handler) {
		this.handler = handler;
		
		heading_level = 0;
		level = 0;
		line = null;
	}
	
	int lead(String line) {
		int length = line.length();
		int i = 0;
		for (i = 0; i < length; i++) {
			if (line.charAt(i) != ' ') {
				break;
			}
		}
		
		return i;
	}
	
	int lineType(String line) {
		int lead_space = lead(line);
		String trim = line.trim();
		
		if (trim.equals("")) {
			return LINE_EMPTY;
		}
		
		if (lead_space > 0) {
			if (trim.startsWith("* ")) {
				return LINE_BULLET;
			} else if (trim.startsWith("1. ")) {
				return LINE_NUMBER;
			}
		}
		
		if (trim.startsWith("=")) {
			return LINE_HEADING;
		} else if (trim.startsWith("{{{")) {
			return LINE_BLOCK_START;
		} else if (trim.startsWith("}}}")) {
			return LINE_BLOCK_END;
		} else if (trim.startsWith("[\"") && trim.endsWith("\"]")) {
			return LINE_LINK;
		} else if (trim.startsWith("--")) {
			return LINE_RULE;
		} else if (trim.startsWith("[[")) {
			return LINE_MACRO;
		}
		
		return LINE_BODY;
	}
	
	int lineLevel(String line, int type, StringBuffer value) {
		int result;
		int index;
		int length;
		
		switch (type) {
			case LINE_HEADING:
				String trim = line.trim();
				length = trim.length();
				index = 0;
				for (index = 0; index < length; index++) {
					if (trim.charAt(index) != '=') {
						break;
					}
				}
				int endIndex = trim.indexOf('=', index);
				value.append(trim.substring(index + 1, endIndex - 1));
				result = index - 1;
				break;
				
			case LINE_BULLET:
				length = line.length();
				index = 0;
				for (index = 0; index < length; index++) {
					if (line.charAt(index) != ' ') {
						break;
					}
				}
				value.append(line.substring(index + 2));
				result = index;
				break;
				
			case LINE_NUMBER:
				length = line.length();
				index = 0;
				for (index = 0; index < length; index++) {
					if (line.charAt(index) != ' ') {
						break;
					}
				}
				value.append(line.substring(index + 3));
				result = index;
				break;
				
			case LINE_COMMENT:
				length = line.length();
				index = 0;
				for (index = 0; index < length; index++) {
					if (line.charAt(index) != ' ') {
						break;
					}
				}
				value.append(line.substring(index + 3));
				result = index;
				break;
				
			default:
				length = line.length();
				index = 0;
				for (index = 0; index < length; index++) {
					if (line.charAt(index) != ' ') {
						break;
					}
				}
				value.append(line.substring(index));
				result = index;
				break;
		}
		
		return result;
	}
	
	void parse(BufferedReader buf) throws WikiParserException {
		handler.start();
		
		parseNode(buf, 0, LINE_EMPTY, false);
		
		handler.end();
	}
	
	void parseNode(BufferedReader buf, int level, int prev, boolean asComment) throws WikiParserException {
		try {
			// Read a line if we need to
			if (line == null) {
				line = buf.readLine();
			}
			
			int newlevel;
			
			// Deal with the line
			while (line != null) {
				StringBuffer body = new StringBuffer();
				int type = lineType(line);
				
				switch (type) {
					case LINE_EMPTY:
						if (prev != LINE_EMPTY) {
							handler.endElement();
							prev = LINE_EMPTY;
						}
						line = null;
						break;
						
					case LINE_BODY:
						newlevel = lineLevel(line, type, body);
						if (newlevel > level) {
							handler.startElement("body", body.toString(), asComment);
							line = null;
							parseNode(buf, newlevel, LINE_BODY, asComment);
						} else if (newlevel == level) {
							if (prev == LINE_BODY) {
								handler.line(line);
							} else if (prev == LINE_EMPTY) {
								handler.startElement("body", body.toString(), asComment);
							} else {
								handler.endElement();
								handler.startElement("body", body.toString(), asComment);
							}
							prev = LINE_BODY;
							line = null;
						} else {
							handler.endElement();
							return;
						}
						break;
						
					case LINE_HEADING:
						newlevel = lineLevel(line, type, body);
						if (newlevel > level) {
							handler.startElement("heading", body.toString(), asComment);
							line = null;
							parseNode(buf, newlevel, LINE_HEADING, asComment);
						} else if (newlevel == level) {
							if (prev == LINE_EMPTY) {
								handler.startElement("heading", body.toString(), asComment);
							} else {
								handler.endElement();
								handler.startElement("heading", body.toString(), asComment);
							}
							prev = LINE_HEADING;
							line = null;
						} else {
							handler.endElement();
							return;
						}
						break;
						
					case LINE_BULLET:
						newlevel = lineLevel(line, type, body);
						if (newlevel > level) {
							handler.startElement("bullet", body.toString(), asComment);
							line = null;
							parseNode(buf, newlevel, LINE_BULLET, asComment);
						} else if (newlevel == level) {
							if (prev == LINE_EMPTY) {
								handler.startElement("bullet", body.toString(), asComment);
							} else {
								handler.endElement();
								handler.startElement("bullet", body.toString(), asComment);
							}
							prev = LINE_BULLET;
							line = null;
						} else {
							handler.endElement();
							return;
						}
						break;
						
					case LINE_NUMBER:
						newlevel = lineLevel(line, type, body);
						if (newlevel > level) {
							handler.startElement("number", body.toString(), asComment);
							line = null;
							parseNode(buf, newlevel, LINE_BULLET, asComment);
						} else if (newlevel == level) {
							if (prev == LINE_EMPTY) {
								handler.startElement("number", body.toString(), asComment);
							} else {
								handler.endElement();
								handler.startElement("number", body.toString(), asComment);
							}
							prev = LINE_NUMBER;
							line = null;
						} else {
							handler.endElement();
							return;
						}
						break;
						
					case LINE_BLOCK_START:
						newlevel = 0;
						if (newlevel > level) {
							line = null;
							parseNode(buf, newlevel, LINE_EMPTY, true);
						} else if (newlevel == level) {
							if (prev != LINE_EMPTY) {
								handler.endElement();
								prev = LINE_EMPTY;
							}
							asComment = true;
							line = null;
						} else {
							handler.endElement();
							return;
						}
						break;
						
					case LINE_BLOCK_END:
						newlevel = 0;
						if (newlevel > level) {
							line = null;
							parseNode(buf, newlevel, LINE_EMPTY, false);
						} else if (newlevel == level) {
							if (prev != LINE_EMPTY) {
								handler.endElement();
								prev = LINE_EMPTY;
							}
							asComment = false;
							line = null;
						} else {
							handler.endElement();
							return;
						}
						break;
						
					case LINE_LINK:
						newlevel = lineLevel(line, type, body);
						if (newlevel > level) {
							handler.startElement("link", body.toString(), asComment);
							line = null;
							parseNode(buf, newlevel, LINE_LINK, asComment);
						} else if (newlevel == level) {
							if (prev == LINE_EMPTY) {
								handler.startElement("link", body.toString(), asComment);
							} else {
								handler.endElement();
								handler.startElement("link", body.toString(), asComment);
							}
							prev = LINE_LINK;
							line = null;
						} else {
							handler.endElement();
							return;
						}
						break;
						
					case LINE_RULE:
						newlevel = lineLevel(line, type, body);
						if (newlevel > level) {
							handler.startElement("rule", body.toString(), asComment);
							line = null;
							parseNode(buf, newlevel, LINE_RULE, asComment);
						} else if (newlevel == level) {
							handler.endElement();
							handler.startElement("rule", body.toString(), asComment);
							prev = LINE_RULE;
							line = null;
						} else {
							handler.endElement();
							return;
						}
						break;
						
					case LINE_MACRO:
						newlevel = lineLevel(line, type, body);
						if (newlevel > level) {
							handler.startElement("macro", body.toString(), asComment);
							line = null;
							parseNode(buf, newlevel, LINE_RULE, asComment);
						} else if (newlevel == level) {
							handler.endElement();
							handler.startElement("macro", body.toString(), asComment);
							prev = LINE_MACRO;
							line = null;
						} else {
							handler.endElement();
							return;
						}
						break;
						
					default:
						throw new WikiParserException("Unknown line type");
				}
				
				if (line == null) {
					line = buf.readLine();
				}
			}
		} catch (IOException ex) {
			throw new WikiParserException("Trouble reading file");
		}
	}
}

public class WikiFileFormat extends AbstractFileFormat implements SaveFileFormat, OpenFileFormat, JoeReturnCodes {
	private HashMap extensions = new HashMap();
	private DocumentInfo docInfo;
	private JoeTree tree;
	
	public int open(JoeTree tree, DocumentInfo docInfo, InputStream stream) {
		int result = FAILURE;
		
		this.docInfo = docInfo;
		this.tree = tree;
		
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(stream, PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE));
			BufferedReader buf = new BufferedReader(inputStreamReader);
			
			WikiHandler handler = new WikiHandler(docInfo, tree);
			WikiParser parser = new WikiParser(handler);
			parser.parse(buf);
			result = SUCCESS;
		} catch (Exception e) {
			result = FAILURE;
		}
		
		return result;
	}
	
	
	public byte[] save(JoeTree tree, DocumentInfo docInfo) {
		WikiFormatter formatter = new WikiFormatter(docInfo, tree);
		StringBuffer buf = formatter.write();
		
		try {
			return buf.toString().getBytes(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE));
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			return buf.toString().getBytes();
		}
	}
	
	public boolean supportsComments() {return true;}
	public boolean supportsEditability() {return true;}
	public boolean supportsMoveability() {return true;}
	public boolean supportsAttributes() {return true;}
	public boolean supportsDocumentAttributes() {return true;}
}

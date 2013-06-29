/*
 * Copyright (c) 2001, Maynard Demmon
 * Copyright (c) 2001, Organic
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 * 
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *
 *  - Neither the name of Organic nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard.util.format;

import java.util.*;

public class Markup {

	private static final boolean DEBUG = false;

	// Token Types
	private static final int TT_UNDEF = -1; // Token Type Undefined.
	private static final int TT_TEXT = 0; // Token Type Text.
	private static final int TT_COMMENT = 1; // Token Type Comment.
	private static final int TT_ELEMENT_START = 2; // Token Type Element Start
	private static final int TT_ELEMENT_END = 3; // Token Type Element End
	private static final int TT_ELEMENT_MINIMAL = 4; // Token Type Element Minimal

	// Define chars
	private static final char CHAR_TAB = '\t';
	private static final char CHAR_NEWLINE = '\n';
	private static final char CHAR_RETURN = '\r';
	private static final char CHAR_SPACE = ' ';

	// Element Types
	private String[] ELEMENTS_UNBALANCED = {"br", "hr", "img", "meta", "input"}; // Elements that can exist as <br> rather than <br/> or <br />
	private String[] ELEMENTS_INLINE = {"br", "a", "img", "b", "i", "span", "font"}; // Elements that should be treated as inline text.
	private String[] ELEMENTS_LINE_ENDER = {"br"}; // Elements that should always cause a line end.
	
	
	// Parser State
	private char[] chars;
	private int i = 0;
		
	private StringBuffer chunk = null;
	private int type = TT_TEXT;
	private String name = null;
	
	// Behaviour Settings
	private ArrayList tokens = new ArrayList();
	private ArrayList tokenNames = new ArrayList();
	private ArrayList tokenTypes = new ArrayList();

	private boolean ignoreCase = true;	
	
	// The Constructor
	public Markup() {}
	
	public Markup(String[] unbalanced, String[] inline, String[] lineEnder) {
		setElementsUnbalanced(unbalanced);
		setElementsInline(inline);
		setElementsLineEnder(lineEnder);
	}
	
	// Accessors
	public void setElementsUnbalanced(String[] elementsUnbalanced) {
		this.ELEMENTS_UNBALANCED = elementsUnbalanced;
	}
	
	public String[] getElementsUnbalanced() {
		return this.ELEMENTS_UNBALANCED;
	}

	public void setElementsInline(String[] elementsInline) {
		this.ELEMENTS_INLINE = elementsInline;
	}
	
	public String[] getElementsInline() {
		return this.ELEMENTS_INLINE;
	}

	public void setElementsLineEnder(String[] elementsLineEnder) {
		this.ELEMENTS_LINE_ENDER = elementsLineEnder;
	}
	
	public String[] getElementsLineEnder() {
		return this.ELEMENTS_LINE_ENDER;
	}
	
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	
	public boolean isIgnoreCase() {
		return this.ignoreCase;
	}


	// Methods
	private boolean isElementType(String name, String[] elementList) {
		if (name == null || elementList == null) {
			return false;
		}
		
		for (int i = 0; i < elementList.length; i++) {
			String element = elementList[i];
			if (ignoreCase) {
				if (element.equalsIgnoreCase(name)) {
					return true;
				}
			} else {
				if (element.equals(name)) {
					return true;
				}			
			}
		}
		return false;
	}
	
	private int indexOfNextNameChar(int index) {
		for (int lookahead = index; lookahead < chars.length; lookahead++) {
			char c = chars[lookahead];
	
			if (Character.isLetterOrDigit(c)) {
				return lookahead;
			}
	
			switch (c) {
				case '.':
					return lookahead;
				case '-':
					return lookahead;
				case '_':
					return lookahead;
				case ':':
					return lookahead;
			}
		}
		return -1;
	}
	
	private int indexOfNextNonNameChar(int index) {
		for (int lookahead = index; lookahead < chars.length; lookahead++) {
			char c = chars[lookahead];
	
			if (!Character.isLetterOrDigit(c)) {
				switch (c) {
					case '.':
						break;
					case '-':
						break;
					case '_':
						break;
					case ':':
						break;
					default:
						return lookahead;
				}
			}
		}
		return -1;
	}
	
	private String getNextWord(int index) {
		int start = indexOfNextNameChar(index);
		if (start == -1) {
			return null;
		}
		int end = indexOfNextNonNameChar(start);
		if (end == -1) {
			end = chars.length;
		}
	
		int length = end - start;
		char[] word = new char[length];
		System.arraycopy(chars, start, word, 0, length);
		return new String(word);
	}
	
	private void addToken (StringBuffer chunk, int type, String name) {
		tokens.add(chunk.toString());
		tokenNames.add(name);
		tokenTypes.add(new Integer(type));
	}
	
	private StringBuffer appendCharToBuffer(StringBuffer chunk, char c) {
		if (chunk == null) {
			chunk = new StringBuffer();
		}
		chunk.append(c);
		return chunk;
	}
	
	private void appendToBuffer(StringBuffer buf, String text, int depth, boolean appendLineEnd) {
		for (int i = 0; i < depth; i++) {
			buf.append(CHAR_TAB);
		}
		buf.append(text);
		if (appendLineEnd) {
			buf.append(CHAR_NEWLINE);
		}
	}
	
	// Main Method
	public StringBuffer format(char[] chars) {
		this.chars = chars;
		StringBuffer buf = new StringBuffer();
	
		// Tokenize
		while (i < chars.length) {
			char c = chars[i];
			char c2;
			char c3;
			int lookahead;
			
			switch(c) {
				case '<':
					if (type == TT_COMMENT) {
						chunk = appendCharToBuffer(chunk, c);
						break;
					}
	
					if (chunk != null) {
						addToken(chunk, type, name);
					}
	
					chunk = new StringBuffer();
					chunk.append(c);
	 
					lookahead = i + 1;
					if (lookahead < chars.length) {
						c2 = chars[lookahead];
							switch(c2) {
								case '/':
									type = TT_ELEMENT_END;
									name = getNextWord(lookahead + 1);
									break;
								case '!':
									type = TT_COMMENT;
									name = null;
									break;
								default:
									type = TT_ELEMENT_START;
									name = getNextWord(lookahead);
								}
					} else {
						type = TT_ELEMENT_START;
						name = getNextWord(lookahead);
					}
					break;
	
				case '>':
					if (type == TT_COMMENT) {
						chunk = appendCharToBuffer(chunk, c);
						break;
					}
					if (chunk != null) {
						chunk.append(c);
						addToken(chunk, type, name);
						chunk = null;
						type = TT_TEXT;
						name = null;
	
						lookahead = i + 1;
						if (lookahead < chars.length) {
							c2 = chars[lookahead];
							switch(c2) {
								case CHAR_NEWLINE:
									i++;
							}
						}
					}
					break;
	
				case '/':
					if (type == TT_COMMENT) {
						chunk = appendCharToBuffer(chunk, c);
						break;
					}
					if (chunk != null) {
						chunk.append(c);
					}
	
					lookahead = i + 1;
					if (lookahead < chars.length) {
						c2 = chars[lookahead];
						switch(c2) {
							case '>':
								type = TT_ELEMENT_MINIMAL;
						}
					}
					break;
	
				case '-':
					if (type != TT_COMMENT) {
						chunk = appendCharToBuffer(chunk, c);
						break;
					}
					if (chunk != null) {
						chunk.append(c);
					}
	
					lookahead = i + 2;
					if (lookahead < chars.length) {
						c2 = chars[lookahead - 1];
						c3 = chars[lookahead];
						if (c2 == '-' && c3 == '>') {
							i = i + 2;
							chunk.append(c2).append(c3);
							addToken(chunk, type, name);
							chunk = null;
							type = TT_TEXT;
							name = null;
	
							lookahead = i + 1;
							if (lookahead < chars.length) {
								c2 = chars[lookahead];
								switch(c2) {
									case CHAR_NEWLINE:
										i++;
								}
							}
						}
					}
					break;
	
				case CHAR_TAB:
					if (type == TT_TEXT) {
						lookahead = i + 1;
						if (lookahead < chars.length) {
							c2 = chars[lookahead];
							switch(c2) {
								case CHAR_NEWLINE:
									chunk = new StringBuffer();
							}
						}
					} 
					break;
	
				case CHAR_NEWLINE:
					if (type == TT_TEXT) {
						if (chunk != null) {
							addToken(chunk, type, name);
						}
						lookahead = i + 1;
						if (lookahead < chars.length) {
							c2 = chars[lookahead];
							switch(c2) {
								case CHAR_NEWLINE:
									chunk = new StringBuffer();
									break;
							}
						}
						chunk = null;
					}
					break;
	
				default:
					chunk = appendCharToBuffer(chunk, c);
			}
			
			i++;
		}
	
		if (chunk != null) {
			addToken(chunk, type, name);
		}
	
		// Walk the tokens and produce a String
		StringBuffer out = new StringBuffer();
		int depth = 0;
	
		if (DEBUG) {System.out.println("----------------------------");}
		for (int j = 0; j < tokens.size(); j++) {
			String token = (String) tokens.get(j);
			int type = ((Integer) tokenTypes.get(j)).intValue();
			String name = (String) tokenNames.get(j);
			if (DEBUG) {System.out.println("Type: " + type + " Name: " + name +  " Token: " + token);}


			boolean isUnbalanced;
			boolean isInline = isElementType(name, ELEMENTS_INLINE);
			boolean isLineEnder;
	
			boolean nextTokenIsInline = false;
			int nextTokenType = -1;
			int lookahead = j + 1;
			String nextTokenName = null;
			if (lookahead < tokens.size()) {
				nextTokenType = ((Integer) tokenTypes.get(lookahead)).intValue();
				nextTokenName = (String) tokenNames.get(lookahead);
				if (isElementType(nextTokenName, ELEMENTS_INLINE)) {
					nextTokenIsInline = true;
				}
			}
	
			switch (type) {
				case TT_TEXT:
					if (nextTokenIsInline) {
						appendToBuffer(out, token, depth, false);
					} else {
						appendToBuffer(out, token, depth, true);
					}
					break;
	
				case TT_ELEMENT_START:
					isUnbalanced = isElementType(name, ELEMENTS_UNBALANCED);
					isLineEnder = isElementType(name, ELEMENTS_LINE_ENDER);
					if (!isLineEnder && isInline && (nextTokenIsInline || nextTokenType == TT_TEXT)) {
						appendToBuffer(out, token, depth, false);
					} else {
						appendToBuffer(out, token, depth, true);
					}
					if (!isUnbalanced && !isInline) {
						depth++;
					}
					break;
	
				case TT_ELEMENT_END:
					isUnbalanced = isElementType(name, ELEMENTS_UNBALANCED);
					isLineEnder = isElementType(name, ELEMENTS_LINE_ENDER);
					if (!isUnbalanced && !isInline) {
						depth--;
					}
					if (!isLineEnder && isInline && (nextTokenIsInline || nextTokenType == TT_TEXT)) {
						appendToBuffer(out, token, depth, false);
					} else {
						appendToBuffer(out, token, depth, true);
					}
					break;
	
				case TT_ELEMENT_MINIMAL:
					isLineEnder = isElementType(name, ELEMENTS_LINE_ENDER);
					if (!isLineEnder && isInline && (nextTokenIsInline || nextTokenType == TT_TEXT)) {
						appendToBuffer(out, token, depth, false);
					} else {
						appendToBuffer(out, token, depth, true);
					}
					break;
	
				case TT_COMMENT:
					if ((nextTokenIsInline || nextTokenType == TT_TEXT)) {
						appendToBuffer(out, token, depth, false);
					} else {
						appendToBuffer(out, token, depth, true);
					}
					break;
			}
		}
		
		return out;
	}
}
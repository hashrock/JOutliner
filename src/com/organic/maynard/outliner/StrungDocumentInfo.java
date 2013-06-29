/**
 * Copyright (C) 2002   Stan Krute <Stan@StanKrute.com>
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

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.5 $, $Date: 2004/01/30 00:12:42 $
 */

package com.organic.maynard.outliner;

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import java.util.Comparator ;
import java.lang.ClassCastException ;

/**
 * This lets us put docInfo into a sorted-by-name/orSomeOtherString data structure
 */
public class StrungDocumentInfo implements Comparable, Comparator {
	
	// Instance Fields
	private DocumentInfo docInfo = null;
	private String string = null;
	private boolean ignoreCase = false;
	
	
	// The Constructors
	public StrungDocumentInfo(
		String someString, 
		DocumentInfo someDocInfo
		){
		docInfo = someDocInfo;
		string = someString;
	}
	
	public String getString () {
		return string;
	}
	
	public DocumentInfo getDocumentInfo () {
		return docInfo;
	}
	
	public void setString (String someString) {
		string = someString;
	}
	
	public void setDocumentInfo (DocumentInfo someDocInfo) {
		docInfo = someDocInfo;
	}
	
	public void setIgnoreCase (boolean newSetting) {
		ignoreCase = newSetting;
	}
	
	// Comparable interface method
	public int compareTo(Object obj) {
		String objString = null;
		String ourString = null;
		
		// if obj is not effectively one of us ...
		if (! this.getClass().isInstance(obj)) {
			throw new ClassCastException ();
		}
		
		// get the strings
		objString = ((StrungDocumentInfo)obj).getString();
		ourString = string;
		
		// if we're ignoring case
		if (ignoreCase) {
			// we'll compare in uppercase
			objString = objString.toUpperCase();
			ourString = ourString.toUpperCase();
		}
		
		// compare
		return ourString.compareTo(objString);
	}
	
	
	// Comparator interface methods
	public int compare(Object obj01, Object obj02) {
		String obj01String = null;
		String obj02String = null;
		
		// if obj01 or obj02 is not effectively one of us ...
		if ( (! this.getClass().isInstance(obj01)) || (! this.getClass().isInstance(obj02)) ) {
			throw new ClassCastException ();
		}
		
		// get the objects' strings
		obj01String = ((StrungDocumentInfo)obj01).getString();
		obj02String = ((StrungDocumentInfo)obj02).getString();
		
		// if we're ignoring case
		if (ignoreCase) {
			// we'll compare in uppercase
			obj01String = obj01String.toUpperCase();
			obj02String = obj02String.toUpperCase();
		}
		
		// compare
		return obj01String.compareTo(obj02String);
	}
	
	public boolean equals(Object obj) {
		String objString = null;
		String ourString = null;
		
		// if obj is not effectively one of us ...
		if (! this.getClass().isInstance(obj)) {
			throw new ClassCastException ();
		}
		
		// get the strings
		objString = ((StrungDocumentInfo)obj).getString();
		ourString = string;
		
		// if we're ignoring case
		if (ignoreCase) {
			// we'll compare in uppercase
			objString = objString.toUpperCase();
			ourString = ourString.toUpperCase();
		}
		
		// return test result
		return ourString.equals(objString);
	}
}
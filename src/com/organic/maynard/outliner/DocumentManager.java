/**
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
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

package com.organic.maynard.outliner;

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.dom.*;
import com.organic.maynard.outliner.event.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.12 $, $Date: 2004/01/30 00:12:42 $
 */

// we manage a set of documents
public class DocumentManager implements DocumentRepositoryListener, JoeReturnCodes {
	
	// private instance vars
	private boolean[] docOpenStates;
	private String[] docPaths;
	private int[] docAlfaOrder;
	private int ourDocsOpen = 0;
	
	// Constructor 
	public DocumentManager(int sizeOfDocSet) {
		// call on the ancestors
		super();
		
		// set up the data arrays
		docOpenStates = new boolean[sizeOfDocSet] ;
		docPaths = new String[sizeOfDocSet] ;
		docAlfaOrder = new int[sizeOfDocSet] ;
		
		Outliner.documents.addDocumentRepositoryListener(this);
	}


	// DocumentRepositoryListener Interface
	public void documentAdded(DocumentRepositoryEvent e) {
		// local vars
		int whichOne = isThisOneOfOurs(PropertyContainerUtil.getPropertyAsString(e.getDocument().getDocumentInfo(), DocumentInfo.KEY_PATH));
		
		// if it's one of ours ...
		if (whichOne != DOCUMENT_NOT_FOUND) {
			
			// mark it open
			docOpenStates[whichOne] = true;
		}
			
		// increment ourDocsOpen counter
		ourDocsOpen ++ ;
	}
	
	public void documentRemoved(DocumentRepositoryEvent e) {
		// local vars
		int whichOne = isThisOneOfOurs(PropertyContainerUtil.getPropertyAsString(e.getDocument().getDocumentInfo(), DocumentInfo.KEY_PATH));
		
		// if it's one of ours ...
		if (whichOne != DOCUMENT_NOT_FOUND) {
			// mark it closed
			docOpenStates[whichOne] = false;
			
			// decrement ourDocsOpen counter
			ourDocsOpen--;
	
			// do other document closing stuff
			docClosingChores(e.getDocument());
		}
	}

	// do document closing stuff
	// meant for subclassing
	// no need for subclasses to call super
	protected void docClosingChores(Document document) {}	

	public void changedMostRecentDocumentTouched(DocumentRepositoryEvent e) {}


	// determine whether one of our documents is currently open
	public boolean documentIsOpen(int docSelector) {
		return docOpenStates[docSelector] ;
	}


	// is a doc spec'd by its pathname a member of our set ? 
	// if it is, returns that doc's selector, otherwise returns DOCUMENT_NOT_FOUND
	public int isThisOneOfOurs (String docPathName) {
		
		/* TODO: 
		 *	this stoopid brute search works fine for extremely small sets
		 *	but it'll bog quickly as doc set sizes grow > 10
		 *	
		 *	stan sez he'll fix this Real Soon
		 *	
		 *	his cheap solution: an ordering array ... 
		 *		int docAlfaOrder[sizeOfDocSet]
		 *		each entry is one of our selector integers
		 *		whenever a pathname's set, adjust the ordering array
		 *		then we can binary search off of the ordering array for stuff like this
		 *		o log n baby
		 *	... so that searches can get flatly fast
		 */
		 
		// for each doc in the set 
		for ( int selector = 0;
			selector < docPaths.length;
			selector ++ ){
				
			// if it's the spec'd doc
			if (docPaths[selector].compareTo(docPathName) == 0) {
				
				// return its selector
				return selector ;	
				
			} // end if
			
		} // end for
		
		// if we get this far, it's not one of ours
		return DOCUMENT_NOT_FOUND ;
		
	} // end method isThisOneOfOurs

	
	// set the pathname of one of our documents 
	protected int setDocPath (int docSelector, String docPathName) {
		
		// make sure we're in bounds
		if ((docSelector < 0) || (docSelector > docPaths.length)) {
			return ARRAY_SELECTOR_OUT_OF_BOUNDS ;
			} // end if
		
		// we're in bounds. 
		
		// TBD ASAP
		// if the supplied path is relative rather than absolute ...
		// [if it doesn't start with http or ftp or drive designator or network designator
		// it's relative]
			// make it absolute by prefixing with the absolute path of outliner's root dir
		// for the moment, we fake it, and just assume relativity, and add in outliner's root dir path
		docPathName = Outliner.APP_DIR_PATH + docPathName;
		
		// set the string.
		docPaths[docSelector] = docPathName ;
		
		// exit triumphant
		return SUCCESS;
		
	} // end method setDocPath


	// get the pathname of one of our documents 
	protected String getDocPath (int docSelector) {
		
		// if we're outta bounds ...
		if ((docSelector < 0) || (docSelector > docPaths.length)) {
			
			// ... return bupkis
			return null ;
			
		} // end if
		
		// we're in bounds. return the path name
		return docPaths[docSelector] ;
		
	} // end method getDocPath
	
	int getOurDocsOpen() {
		return ourDocsOpen;
	}
}
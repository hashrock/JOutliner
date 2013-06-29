/**
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 12/22/01 4:20PM
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

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.4 $, $Date: 2004/01/30 00:12:42 $
 */

// we're a Palm pdb file format
// we read and write Palm pdb files 
public class PdbFileFormat extends AbstractFileFormat implements 
		PdbContentHandler,
		PdbErrorHandler,
		SaveFileFormat, 
		OpenFileFormat,
		JoeReturnCodes {
	
	// protected instance variables
	protected PdbReaderWriter ourReaderWriter= null ;
	
	protected DocumentInfo docInfo = null;
	protected JoeTree tree = null;
	
	protected boolean errorOccurred = false;
	
	protected Vector elementStack = new Vector();
	
	protected Node currentNode = null;
	protected int currentLevel = -1 ;
	
	// this stores file name extension
	protected HashMap extensions = new HashMap();
	
	protected boolean anyIsCommentInheritedAttributesFound = false; // used to provide for better interop with outliners that don't support isCommentInherited.
	protected boolean anyIsEditableInheritedAttributesFound = false; // used to provide for better interop with outliners that don't support isEditableInherited.
	protected boolean anyIsMoveableInheritedAttributesFound = false; // used to provide for better interop with outliners that don't support isMoveableInherited.
	
	
	// Constructors
	
	// NOTE most sub-classes won't need their own constructor
	//	constructors for sub-classes can just call this one via super()
	//	it'll work for most standard cases
	//	that's because the PDB-flavor-specific stuff is 
	//	isolated to a set of called protected methods
	//	for example, createReaderWriter()
	
	public PdbFileFormat() {
		try {
			// the ancestors are called
			// in this case, that's Object()
			
			// create a reader-writer
			createReaderWriter() ;
			
			// tell it we'll handle content and errors
			ourReaderWriter.setContentHandler (this) ;
			ourReaderWriter.setErrorHandler (this) ;
			
			
		} // end try

		catch (JoeException someException) {
			System.out.println("error in PdbFileFormat constructor: " + someException.getMessage()) ;
		} // end catch
		
		catch (Exception someException) {
			System.out.println ("error in PdbFileFormat constructor: " + someException.getMessage());
		} // end catch
		
		
	} // end constructor PdbFileFormat 
	
	
	// ======== PDB-flavor-specific methods ========
	
	// create a reader-writer for this format
	
	// NOTE this is PDB-flavor-specific
	// NOTE sub-classes will generally want to modify this
	// NOTE failures should throw an appropriate JoeException
	
	// for this class, we provide the generic vanilla root PDB reader-writer
	
	protected void createReaderWriter() throws JoeException {
		// try to create an appropriate reader-writer
		ourReaderWriter = new PdbReaderWriter() ;
		
		// if we fail ...
		if (ourReaderWriter == null) {
			throw new JoeException(UNABLE_TO_CREATE_OBJECT) ;
		} // end if 
	} // end protected method PdbReaderWriter
	
	
	// ======== OpenFileFormat interface Implementations ========
	
	public int open(JoeTree tree, DocumentInfo docInfo, InputStream stream) {
		// Set the objects we are going to populate.
		this.docInfo = docInfo;
		this.tree = tree;
		
		// initialize result flags
		int success = FAILURE;
		errorOccurred = false;
		
		// try to read the file
		try {
			// we'll be grabbing chunks of data from the file
			// and storing them as arrays of raw bytes
			// so we send the readerWriter a stream of raw bytes
			BufferedInputStream bIStream = new BufferedInputStream(stream) ;
			DataInputStream byteStream = new DataInputStream(stream) ;

			// ask the reader-writer to try to read the file
			ourReaderWriter.read(byteStream);

			// as it reads the file, the reader-writer 
			// sends outline and node data 
			// back to methods in this class 
			// which actually build the outline
			
			// if there are problems, errorOccurred
			// gets set to true
			
			// if the reader-writer had a problem ...
			if (errorOccurred) {
				success = FAILURE;
				} // end if
			else { // we succeeded
				success = SUCCESS;
				} // end else
			} // end try
		
//		// TBD deal with pdb exceptions
//		catch (PdbException e) {
//			success = FAILURE;
//			} // end catch
//		
		// deal with all other exceptions
		catch (Exception e) {
			
			success = FAILURE;
			
			String exceptionClass = new String(e.getClass().getName()) ;
			if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbReaderWriter:read: just had a[n] " + exceptionClass + " exception"); }
			
			} // end catch
		
		// if we get this far, we're okay
		return success;
	}
	
	// ======== PdbContentHandler interface Implementations ========
	
	// initialize the outline data structure
	// TBD this can be moved further up the line, it's quite vanilla
	public void startOutline () {
		// initialize the current node to the root node of the outline
		this.currentNode = tree.getRootNode();
		
		// clear out any existing children.
		while (currentNode.numOfChildren() > 0) {
			currentNode.removeChild(currentNode.getLastChild());
		} // end while
		
		// set the initial level to the root node's level
		currentLevel = -1 ;
		
		} // end startOutline
		
	
	// apply finishing touches to the outline data structure
	public void finishOutline () {}
	
	
	// add a node of data to the outline
	// TBD this can be moved further up the line, it's quite vanilla
	public void addNodeToOutline (NodeImpl node, int level) {
		
		// for appendage, we need to consider the current node's level
		// and the node-to-be-added's level
		int levelDelta = level - currentLevel ;
		
		// in case we need to go back up the outline
		int climbCounter = 0 ;
		Node tempNode = null ;
		
		// reader/writer has obtained and set up the node's data
		// we finish up by connecting the node to the outline
		
		// the node's part of this outline
		node.setTree(tree, false);
		
		// case out on the change in levels for appendation
		
		// if the new node's a child of the current node
		if (levelDelta == 1) {
			
			currentNode.appendChild(node) ;
			} // end if
			
		// if the new node's a sibling of the current node
		else if (levelDelta == 0) {
				
			currentNode.getParent().appendChild(node) ;
			} // end if
			
		// if the new node's an elder of the current node
		else if (levelDelta < 0) {
			
			// jump back as far as necessary
			
			// for each unit of delta, climb up a parent
			for (	climbCounter = levelDelta, tempNode = currentNode;
				climbCounter < 0 ;
				climbCounter++ ){
					
				tempNode = tempNode.getParent();
				
				} // end for
			
			// now we're at the level we need to be
			// add a sibling
			tempNode.getParent().appendChild(node) ;
			
			
			} // end if an elder
			
		// this node becomes the new current node
		currentNode = node;
		
		// its level becomes the current level
		currentLevel = level ;
		
		} // end addNodeToOutline
		
		
	// ======== PdbErrorHandler Implementations ========
	
	public void error(JoeException someException) {
		System.out.println(someException.getMessage()) ;
		this.errorOccurred = true;
	} // end method error
	
	public void fatalError(JoeException someException) {
		System.out.println(someException.getMessage()) ;
		this.errorOccurred = true;
	} // end method fatalError
	
	public void warning(JoeException someException) {
		System.out.println(someException.getMessage()) ;
		this.errorOccurred = true;
	} // end method warning
	
	
	// ======== FileFormat Implementations ========
	
	// what do we support ?
	public boolean supportsComments() {return true;}
	public boolean supportsEditability() {return true;}
	public boolean supportsMoveability() {return true;}	
	public boolean supportsAttributes() {return true;}
	public boolean supportsDocumentAttributes() {return true;}
	
	
	// ======== SaveFileFormat Implementations ========
	
	
	// save the outline 
	public byte[] save(JoeTree tree, DocumentInfo docInfo) {
		StringBuffer buf = prepareFile(tree, docInfo);
		
		
		try {
			return buf.toString().getBytes(PropertyContainerUtil.getPropertyAsString(docInfo, DocumentInfo.KEY_ENCODING_TYPE));
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return buf.toString().getBytes();
		}
	}
	
	
	// ======== TBD helpers for SaveFileFormat Implementations ========
	
	protected StringBuffer prepareFile(JoeTree tree, DocumentInfo docInfo) {
		//String lineEnding = Preferences.platformToLineEnding(docInfo.getLineEnding());
		
		StringBuffer buf = new StringBuffer();
		
		// write the prelude to data
		// TBD
		
		// write the data
		//Node node = tree.getRootNode();
		//for (int i = 0; i < node.numOfChildren(); i++) {
		//	buildOutlineElement(node.getChild(i), lineEnding, buf);
		//}
		//
		
		// write the postlude to data
		// TBD
		
		return buf;
		} // end method prepareFile
		
	protected void buildOutlineElement(Node node, String lineEnding, StringBuffer buf) {
//		buf.append("<").append(ELEMENT_OUTLINE).append(" ");
//		
//		if (node.getCommentState() == Node.COMMENT_TRUE) {
//			buf.append(ATTRIBUTE_IS_COMMENT).append("=\"true\" ");
//			buf.append(ATTRIBUTE_IS_COMMENT_INHERITED).append("=\"true\" ");
//			
//		} else if (node.getCommentState() == Node.COMMENT_FALSE) {
//			buf.append(ATTRIBUTE_IS_COMMENT).append("=\"false\" ");
//			buf.append(ATTRIBUTE_IS_COMMENT_INHERITED).append("=\"true\" ");
//			
//		} else {
//			if (node.isComment()) {
//				buf.append(ATTRIBUTE_IS_COMMENT).append("=\"true\" ");
//			}
//		}
//		
//		if (node.getEditableState() == Node.EDITABLE_TRUE) {
//			buf.append(ATTRIBUTE_IS_EDITABLE).append("=\"true\" ");
//			buf.append(ATTRIBUTE_IS_EDITABLE_INHERITED).append("=\"true\" ");
//			
//		} else if (node.getEditableState() == Node.COMMENT_FALSE) {
//			buf.append(ATTRIBUTE_IS_EDITABLE).append("=\"false\" ");
//			buf.append(ATTRIBUTE_IS_EDITABLE_INHERITED).append("=\"true\" ");
//			
//		} else {
//			if (node.isEditable()) {
//				buf.append(ATTRIBUTE_IS_EDITABLE).append("=\"true\" ");
//			}
//		}
//		
//		if (node.getMoveableState() == Node.MOVEABLE_TRUE) {
//			buf.append(ATTRIBUTE_IS_MOVEABLE).append("=\"true\" ");
//			buf.append(ATTRIBUTE_IS_MOVEABLE_INHERITED).append("=\"true\" ");
//			
//		} else if (node.getMoveableState() == Node.MOVEABLE_FALSE) {
//			buf.append(ATTRIBUTE_IS_MOVEABLE).append("=\"false\" ");
//			buf.append(ATTRIBUTE_IS_MOVEABLE_INHERITED).append("=\"true\" ");
//			
//		} else {
//			if (node.isMoveable()) {
//				buf.append(ATTRIBUTE_IS_MOVEABLE).append("=\"true\" ");
//			}
//		}
//		
//		buf.append(ATTRIBUTE_TEXT).append("=\"").append(escapeXMLAttribute(node.getValue())).append("\"");
//		buildAttributes(node, buf);
//		
//		if (node.isLeaf()) {
//			buf.append("/>").append(lineEnding);
//		} else {
//			buf.append(">").append(lineEnding);
//			
//			for (int i = 0; i < node.numOfChildren(); i++) {
//				buildOutlineElement(node.getChild(i), lineEnding, buf);
//			}
//			
//			buf.append("</").append(ELEMENT_OUTLINE).append(">").append(lineEnding);
//		}
	}
	
	protected void buildAttributes(Node node, StringBuffer buf) {
//		Iterator it = node.getAttributeKeys();
//		if (it != null) {
//			while (it.hasNext()) {
//				String key = (String) it.next();
//				Object value = node.getAttribute(key);
//				buf.append(" ").append(key).append("=\"").append(escapeXMLAttribute(value.toString())).append("\"");
//				} // end while
//		} // end if
	} // end method buildAttributes



} // end class PdbFileFormat

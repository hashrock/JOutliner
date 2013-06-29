/**
 * PdbSPReaderWriter class
 * 
 * Reads and [TBD] writes Palm Pilot pdb files created by Shadow Plan
 *	the common parts of pdb reading and writing are 
 *	handled by PdbReaderWriter
 * 
 * Members
 *  	constants
 *  		class
 *  			private
 *				int PDB_SP_RECORD_TYPE
 *				int PDB_SP_RECORD_VERSION
 *				int PDB_SP_RECORD_STATE
 *				int PDB_SP_RECORD_AUTONUMBER
 *				int PDB_SP_RECORD_PRIORITY
 *				 
 *				int PDB_SP_RECORD_PROGRESS
 *				int PDB_SP_RECORD_CREATE_TIME
 *				int PDB_SP_RECORD_TARGET_TIME
 *				int PDB_SP_RECORD_START_TIME
 *				int PDB_SP_RECORD_FINISH_TIME
 *				 
 *				int PDB_SP_RECORD_DEFAULT_COLOR
 *				int PDB_SP_RECORD_DISPLAY_FLAGS
 *				int PDB_SP_RECORD_LINK_TODO_ID
 *				int PDB_SP_RECORD_LINK_DATEBOOK_ID
 *				int PDB_SP_RECORD_LINK_FILENAME
 *				 
 *				int PDB_SP_RECORD_EAB_COUNT
 *				int PDB_SP_RECORD_TITLE_LENGTH
 *				int PDB_SP_RECORD_NOTE_LENGTH
 *				int PDB_SP_RECORD_MISC_LENGTH
 *				int PDB_SP_RECORD_RELATIONS
 *				 
 *				int PDB_SP_RECORD_FOOTER
 *				 
 *				int PDB_SP_RECORD_HAS_SIBLING
 *				int PDB_SP_RECORD_HAS_CHILD
 *				 
 *				String SHADOW_PLAN_NOTE_START_MARKUP
 *				String SHADOW_PLAN_NOTE_STOP_MARKUP
 *				 
 *				String SHADOW_PLAN_MISC_START_MARKUP
 *				String SHADOW_PLAN_MISC_STOP_MARKUP
 *				 
 *				String SHADOW_PLAN_EAB_START_MARKUP
 *				String SHADOW_PLAN_EAB_STOP_MARKUP
 *	variables
 *		class
 *			private	
 *				int nodeLevel
 *				int nextNodeLevel
 *				boolean siblingAhead
 *				Stack siblingAheadLevels
 *	methods
 * 		instance
 *  			public
 *  				constructors
 *  					PdbSPReaderWriter ()
 *			protected
 *				[TBD] int processHeader (byte[] header) 
 *				int processRecord (int, byte[], short , int ) 
 * 				[TBD] int processAppInfo (byte[])
 *				[TBD] int processSortInfo (byte[])
 *		
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 12/30/01 5:48AM
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

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.io.*;

// we use these
import java.io.*;
import java.util.Stack ;

// we read and write Palm pdb files created by Brain Forest 
public class PdbSPReaderWriter extends PdbReaderWriter {
	
	// private class constants
	
	// flavor-specific PDB file stuff
	private static final int PDB_SP_RECORD_TYPE = 0 ;  // one byte
	private static final int PDB_SP_RECORD_VERSION = 1 ;  // one byte
	private static final int PDB_SP_RECORD_STATE = 2 ; // four bytes
	private static final int PDB_SP_RECORD_AUTONUMBER = 6 ; // two bytes
	private static final int PDB_SP_RECORD_PRIORITY = 8 ; // two bytes

	private static final int PDB_SP_RECORD_PROGRESS = 10 ;  // two bytes
	private static final int PDB_SP_RECORD_CREATE_TIME = 12 ;  // four bytes
	private static final int PDB_SP_RECORD_TARGET_TIME = 16 ;  // four bytes
	private static final int PDB_SP_RECORD_START_TIME = 20 ;  // four bytes
	private static final int PDB_SP_RECORD_FINISH_TIME = 24 ;  // four bytes

	private static final int PDB_SP_RECORD_DEFAULT_COLOR = 28 ;  // four bytes
	private static final int PDB_SP_RECORD_DISPLAY_FLAGS = 32 ;  // four bytes
	private static final int PDB_SP_RECORD_LINK_TODO_ID = 36 ;  // four bytes
	private static final int PDB_SP_RECORD_LINK_DATEBOOK_ID = 40 ;  // four bytes
	private static final int PDB_SP_RECORD_LINK_FILENAME = 44 ;  // 32 bytes

	private static final int PDB_SP_RECORD_EAB_COUNT = 76 ;  // two bytes
	private static final int PDB_SP_RECORD_TITLE_LENGTH = 78 ;  // two bytes
	private static final int PDB_SP_RECORD_NOTE_LENGTH = 80 ;  // four bytes
	private static final int PDB_SP_RECORD_MISC_LENGTH = 84 ;  // two bytes
	private static final int PDB_SP_RECORD_RELATIONS = 86 ;  // two bytes

	private static final int PDB_SP_RECORD_FOOTER = 88 ; // title plus note plus misc plus EABs bytes

	private static final int PDB_SP_RECORD_HAS_SIBLING = 1 ;
	private static final int PDB_SP_RECORD_HAS_CHILD = 2 ;

	private static final String SHADOW_PLAN_NOTE_START_MARKUP = "<ShadowPlanNote>" ;
	private static final String SHADOW_PLAN_NOTE_STOP_MARKUP = "</ShadowPlanNote>" ;
	
	private static final String SHADOW_PLAN_MISC_START_MARKUP = "<ShadowPlanMisc>" ;
	private static final String SHADOW_PLAN_MISC_STOP_MARKUP = "</ShadowPlanMisc>" ;
	
	private static final String SHADOW_PLAN_EAB_START_MARKUP = "<ShadowPlanEAB>" ;
	private static final String SHADOW_PLAN_EAB_STOP_MARKUP = "</ShadowPlanEAB>" ;
	
	
	// private class variables
	private static int nodeLevel = 0;
	private static int nextNodeLevel = 0 ;
	private static boolean siblingAhead = false ;
	private static Stack siblingAheadLevels = null ;

	// constructor 
	public PdbSPReaderWriter () {
		super() ;
		
//		in case we ever do native interface code to hide some details .... 	
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbSPReaderWriter:constructor:DLL is loaded? " + bNativeInterfaceCodeLoaded); }
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbSPReaderWriter:constructor:DLL is initialized? " + bNativeInterfaceCodeInitialized); }
		
		
		} // end constructor


	// TBD process a PDB header
	// NOTE this is PDB-flavor specific 
	// and over-rides PdbReaderWriter's processHeader
	protected int processHeader (byte[] header) 
		{
		
		// TBD
		
		//if (Outliner.DEBUG) 
		//	System.out.println ("\tStan_Debug:\tPdbSPReaderWriter:processHeader"); }
		
		return SUCCESS ;
			
		} // end protected method processRecord	


	// process a PDB record chunk
	// NOTE this method is PDB-flavor specific
	// and over-rides PdbReaderWriter's.processRecord
	protected int processRecord (int recordCounter, byte[] recordData, short recordAttributes, int recordUniqueID) 
		{
		// local vars
		int processResult = SUCCESS ;
		int relations = 0 ;
		
		String nodeTitle = null;
		int titleLength = 0 ;
		
		String nodeNote = null;
		int noteLength = 0 ;
		
		String nodeMisc = null;
		int miscLength = 0 ;
		
		NodeImpl node = null;
		Integer wrappedLevel = null ;
							
		// if it's not the first record ...
		if (recordCounter > 0) {

			// it's a data-containing node
			
			// determine the node's level in the outline
			
			// if it's the first node
			if (recordCounter == 1) {
				
				// nodeLevel is just 0
				nodeLevel = 0 ;
				
				} // end if we're the first node
			
			// else it's not the first node
			else {
				// node's level was set by previous record
				nodeLevel = nextNodeLevel ;
				
				} // end else we're not the first node
			
			// determine the node level of the next node
			
			// grab current node's relations flags
			relations = BitsAndBytes.unsignedShort(recordData, PDB_SP_RECORD_RELATIONS, BitsAndBytes.HI_TO_LO) ;
			
			// note whether there's a sibling coming up
			siblingAhead = ((relations & PDB_SP_RECORD_HAS_SIBLING) != 0) ;
			
			// if current node has a child ...
			if ((relations & PDB_SP_RECORD_HAS_CHILD) != 0) {
				
				// that child is the next node
				// children live one level deeper
				nextNodeLevel = nodeLevel + 1 ;
					
				// if there's a sibling ahead
				if (siblingAhead) {
					
					// siblings live at the same level 
					// push that onto the stack
					wrappedLevel = new Integer(nodeLevel) ;
					siblingAheadLevels.push(wrappedLevel);
					
					} // end if there's a sibling ahead
				
				} // end if current node has a child
				
			// else if current node has no child, but does have a sibling
			else if (siblingAhead) {
				
				// that sibling is the next node
				// siblings live at the same level
				nextNodeLevel = nodeLevel ;
				
				} // end else if current node has a sibling
				
			// else current node has no siblings or children
			// if there are more nodes, then next node is some kind of forebear
			// we can find out if there are more nodes by checking the size of the stack
			// if it's empty, there are no more nodes
			else if (! siblingAheadLevels.empty()){
				
				// the stack's NOT empty
				//there's a next node, and it is a forebear
				// pop its level off of the stack
				wrappedLevel =  (Integer) siblingAheadLevels.pop() ;
				nextNodeLevel = wrappedLevel.intValue() ;
				wrappedLevel = null ; // avoid memory leaks
				
				} // end else current node has no siblings or children and stack's not empty
			
			// else there are no more nodes
			else {
				// let's free the stack to avoid memory leaks
				siblingAheadLevels = null ;
				
				} // end else there are no more nodes
			
			// phew
			
			// if there's node text ...
			// (Shadow Plan calls a node's text its title)
			titleLength =  BitsAndBytes.unsignedShort(recordData, PDB_SP_RECORD_TITLE_LENGTH, BitsAndBytes.HI_TO_LO) ;
			if (titleLength > 0) {
				
				// grab the node text as a string
				nodeTitle = new String(recordData, PDB_SP_RECORD_FOOTER, titleLength) ;

				// create a free-standing node with the proper contents
				node = new NodeImpl(null, nodeTitle);
	
				// set node attributes
				// TBD
	
				// add the node to the outline
				ourContentHandler.addNodeToOutline(node, nodeLevel) ;
				
				} // end if there's node text
		
		
			// if the node has a note ...
			noteLength =  (int) BitsAndBytes.unsignedInt(recordData, PDB_SP_RECORD_NOTE_LENGTH, BitsAndBytes.HI_TO_LO) ;
			if (noteLength > 0) {
				
				// grab the note text as a string
				nodeNote = new String(recordData, 
					PDB_SP_RECORD_FOOTER + titleLength+1, noteLength) ;

				// create a free-standing node with the proper contents
				node = new NodeImpl(null, nodeNote);
	
				// set node attributes
				// TBD
	
				// create a free-standing node with the proper contents
				node = new NodeImpl(null, SHADOW_PLAN_NOTE_START_MARKUP + nodeNote + SHADOW_PLAN_NOTE_STOP_MARKUP);
				
				// set node attributes
				// it's a Shadow Plan note node
				// TBD
				// for now, we encase in XMLian markup, as seen above
				
				// add the note to the outline as a child of the current node
				ourContentHandler.addNodeToOutline(node, nodeLevel + 1) ;
				
				} // end if the node has a note
			
			
			// if the node has misc stuff ...
			miscLength =  BitsAndBytes.unsignedShort(recordData, PDB_SP_RECORD_MISC_LENGTH, BitsAndBytes.HI_TO_LO) ;
			if (miscLength > 0) {
				
				// grab the misc text as a string
				nodeNote = new String(recordData, 
					PDB_SP_RECORD_FOOTER + titleLength+noteLength + 2, miscLength) ;

				// create a free-standing node with the proper contents
				node = new NodeImpl(null, nodeNote);
	
				// set node attributes
				// TBD
	
				// create a free-standing node with the proper contents
				node = new NodeImpl(null, SHADOW_PLAN_MISC_START_MARKUP + nodeNote + SHADOW_PLAN_MISC_STOP_MARKUP);
				
				// set node attributes
				// it's a Shadow Plan misc stuff node
				// TBD
				// for now, we encase in XMLian markup, as seen above
				
				// add the misc info to the outline as a child of the current node
				ourContentHandler.addNodeToOutline(node, nodeLevel + 1) ;
				
				} // end if the node has misc stuff
			
			} // end if it's not the first record

					
		// else it's the first record
		else {
			// the first record is special
			// it contains meta data RE the outline
			
			// more possibly TBD
			
			// reset nodeLevel stuff for upcoming convolutions
			nodeLevel = 0;
			nextNodeLevel = 0 ;
			siblingAhead = false ;
			siblingAheadLevels = new Stack();
					
			} // end else it's the first record
		
		// return a result
		// TBD make this real
		// TBD while doing so, get a try/catch in here
		
		return processResult ;
			
		} // end protected method processRecord	

	// process a PDB AppInfo block
	// this is a PDB-flavor specific method
	protected int processAppInfo (byte[] appInfo)
		{
		
		// TBD
			
		//if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbSPReaderWriter:processAppInfo"); }
			
		return SUCCESS ;
			
		} // end method processAppInfo;


	// process a PDB SortInfo block
	// this is a PDB-flavor specific method
	protected int processSortInfo (byte[] sortInfo)
		{
		
		// TBD
			
		//if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbSPReaderWriter:processSortInfo"); }
			
		return SUCCESS ;
		
		} // end protected method processSortInfo;
	
	} // end class PdbSPReaderWriter
	

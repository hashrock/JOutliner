/**
 * PdbBFReaderWriter class
 * 
 * Reads and [TBD] writes Palm Pilot pdb files created by Brain Forest
 *	the common parts of pdb reading and writing are 
 *	handled by PdbReaderWriter
 * 
 * Members
 *  	constants
 *  		class
 *  			private
 * 				int PDB_BF_RECORD_0_SIZE
 * 				String BRAIN_FOREST_NOTE_START_MARKUP
 * 				String BRAIN_FOREST_NOTE_STOP_MARKUP
 *	variables
 *		class
 *			private
 *				pdbBFnodeLevelOffset
 *  				pdbBFnodeTextOffset
 *	methods
 * 		instance
 *  			public
 *  				constructors
 *  					PdbBFReaderWriter ()
 *			protected
 *				int processRecord (int, byte[], short , int ) 
 * 				[TBD] int processAppInfo (byte[])
 *				[TBD] int processSortInfo (byte[])
 *		
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 12/26/01 9:52PM
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
 * @version $Revision: 1.3 $, $Date: 2002/12/16 01:51:45 $
 */

package com.organic.maynard.outliner.io.formats;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.io.*;

// we use these
import java.io.*;

// we read and write Palm pdb files created by Brain Forest 
public class PdbBFReaderWriter extends PdbReaderWriter {
	// private class constants
	
	// flavor-specific PDB file stuff
	private final int PDB_BF_RECORD_0_SIZE = 23 ;
	private final int PDB_BF_VERSION_ID_OFFSET = 0 ;
	private final String BRAIN_FOREST_NOTE_START_MARKUP = "<BrainForestNote>" ;
	private final String BRAIN_FOREST_NOTE_STOP_MARKUP = "</BrainForestNote>" ;
	
	// private class variables
	private int pdbBFnodeLevelOffset = 0 ;
	private int pdbBFnodeTextOffset = 0 ;
	
	// constructor 
	public PdbBFReaderWriter () {
		super() ;
		
//		in case we ever do native interface code to hide some details .... 	
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbBFReaderWriter:constructor:DLL is loaded? " + bNativeInterfaceCodeLoaded); }
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbBFReaderWriter:constructor:DLL is initialized? " + bNativeInterfaceCodeInitialized); }
		
		
		} // end constructor

	// process a PDB BF header block
	// this is a PDB-flavor specific method
	// TBD
	protected int processHeader (byte[] header)
		{
		
		// TBD
			
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbBFReaderWriter:processHeader"); }
			
		return SUCCESS ;
			
		} // end method processAppInfo;


	// process a PDB record chunk
	// NOTE this method is PDB-flavor specific
	// and over-rides PdbReaderWriter's.processRecord
	protected int processRecord (int recordCounter, byte[] recordData, short recordAttributes, int recordUniqueID) 
		{
		// local vars
		int processResult = SUCCESS ;
		int versionID = 0 ;
		int textScanner = 0 ;
		boolean hasNote = false ;
		int noteStart = 0;
		int noteScanner = 0 ;
		String nodeText = null;
		String noteText = null;
		NodeImpl node = null;
		 					
		// if it's not the first record ...
		if (recordCounter > 0) {
			
			// it's a text-containing node
			
			// determine the node's level in the outline
			int nodeLevel = (int) BitsAndBytes.unsignedByte(recordData, pdbBFnodeLevelOffset) ;
			
			// scan for the end of the node text
			// it's marked by a 0 byte
			for (textScanner = pdbBFnodeTextOffset; 
			      textScanner < recordData.length ; 
			      textScanner ++) {
				if (recordData[textScanner] == 0) {
					break ;
					} // end if
				} // end for
			
			// let's look for a note
			// if we're not too close to the end of the record
			if (textScanner < recordData.length - 2) {
				// set up a possible note-start marker
				noteStart = textScanner + 1 ;

				// if the next char is non-zero ...
				if (recordData[noteStart] != 0) {
				
					// the node has a note
					hasNote = true ;
				
					// scan for the end of the note
					for (noteScanner = noteStart + 1;
					      noteScanner < recordData.length;
					      noteScanner ++) {
						if (recordData[noteScanner] == 0) {
							break ;
							} // end if
						} // end for						
					} // end if there's a note
				} // end if we're not too close to the end of the record
					
			// grab the node text as a string
			// textScanner now holds the position of the end of the string
			// the length of the string is that position minus the string's starting position
			nodeText = new String(recordData, pdbBFnodeTextOffset, textScanner - pdbBFnodeTextOffset) ;

			// create a free-standing node with the proper contents
			node = new NodeImpl(null, nodeText);

			// set node attributes
			// TBD

			// add the node to the outline
			ourContentHandler.addNodeToOutline(node, nodeLevel) ;
		
			// if the node has a note ...
			if (hasNote) {
				
				// grab the note's text as a string
				noteText = new String(recordData, noteStart, noteScanner - noteStart) ;
				
				// create a free-standing node with the proper contents
				node = new NodeImpl(null, BRAIN_FOREST_NOTE_START_MARKUP + noteText + BRAIN_FOREST_NOTE_STOP_MARKUP);
				
				// set node attributes
				// it's a BrainForest note node
				// TBD
				// for now, we add a preamble, as seen above
				
				// add the note to the outline as a child of the current node
				ourContentHandler.addNodeToOutline(node, nodeLevel + 1) ;
				
				} // end if node has a note
			
			} // end if it's not the first record
					
		// else it's the first record
		else {
			
			// the first record is special
			// it contains meta data RE the outline
			
			// make sure the first record is the proper size
			if (recordData.length != PDB_BF_RECORD_0_SIZE) {
				processResult = FAILURE ;
				if (Outliner.DEBUG) { 
					System.out.println("\tStan_Debug:\tPdbBFReaderWriter:processRecord: record 0 is the wrong size");
					} // end if debug message
				} // end if size check
				
			// determine the BF file version
			// get the version integer
			versionID = BitsAndBytes.unsignedShort(recordData, PDB_BF_VERSION_ID_OFFSET, BitsAndBytes.HI_TO_LO) ;
			
			// case out on the BF file version
			// this is empirically-determined info, 
			// so watch for changes/additions in the future
			switch (versionID) {
				case 0x8006:
				case 0x8206:
					pdbBFnodeLevelOffset = 15 ;
					pdbBFnodeTextOffset = 16 ;
					break;				

				case 0x80C6: 
					pdbBFnodeLevelOffset = 16 ;
					pdbBFnodeTextOffset = 17 ;
					break;					

				case 0x0003:
					pdbBFnodeLevelOffset = 13 ;
					pdbBFnodeTextOffset = 14 ;
					break ;

				default:
					pdbBFnodeLevelOffset = 13 ;
					pdbBFnodeTextOffset = 14 ;
					if (Outliner.DEBUG) { 
						System.out.println("\tStan_Debug:\tPdbBFReaderWriter:processRecord: unknown BF file version " + versionID) ;
						} ;
					break ;
				} // end switch
					
			} // end else it's the first record
		
		
		// return a result
		// TBD make this real
		// TBD while doing so, get a try/catch in here
		
		return processResult ;
			
		} // end protected method processRecord	

	// process a PDB AppInfo block
	// this is a PDB-flavor specific method
	// TBD
	protected int processAppInfo (byte[] appInfo)
		{
		
		// TBD
			
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbBFReaderWriter:processAppInfo"); }
			
		return SUCCESS ;
			
		} // end method processAppInfo;


	// process a PDB SortInfo block
	
	// this is a PDB-flavor specific method
	
	protected int processSortInfo (byte[] sortInfo)
		{
		
		// TBD
			
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbBFReaderWriter:processSortInfo"); }
			
		return SUCCESS ;
		
		} // end protected method processSortInfo;
	
	} // end class PdbBFReaderWriter
	

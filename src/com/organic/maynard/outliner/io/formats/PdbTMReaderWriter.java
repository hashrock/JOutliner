/**
 * PdbTMReaderWriter class
 * 
 * Reads and [TBD] writes Palm Pilot pdb files created by Thought Manager
 *	[TBD] uses Java Native Interface functionality
 *	to call c++ code that does the TM-specific parts of reading and writing
 *	
 *	the common parts of pdb reading and writing are 
 *	handled by PdbReaderWriter
 * 
 * Members
 *	constants
 *		class
 *			private
 *				int PDB_TM_NODE_LEVEL
 *				int PDB_TM_NODE_TEXT
 *				int PDB_TM_APRES_TEXT
 *	variables
 *		class
 *			private
 *				boolean bNativeInterfaceCodeLoaded
 *				boolean bNativeInterfaceCodeInitialized
 *				native int ptmrwInitialize() ;
 *	methods
 *		instance
 *			public
 *				constructors
 *					PdbTMReaderWriter ()
 *			protected
 *				int processRecord (int, byte[], short, int)
 *				int processAppInfo (byte[])
 *				int processSortInfo (byte[])
 *
 *		
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 12/23/01 5:06PM
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
import java.io.*;

// we read and write Palm pdb files created by Thought Manager 
public class PdbTMReaderWriter extends PdbReaderWriter  {
	
	// private class constants
	
	// flavor-specific PDB file stuff
	// TBD move this into JNI world
	private static final int PDB_TM_NODE_LEVEL = 17 ;
	private static final int PDB_TM_NODE_TEXT = 38 ;
	private static final int PDB_TM_APRES_TEXT = 4 ;
	
	// private class variables 
	// native interface code stuff
	private static boolean bNativeInterfaceCodeLoaded ;
	private static boolean bNativeInterfaceCodeInitialized ;
	private static native int ptmrwInitialize() ;
	
/*
	Native Interface Code stuff
	TBD
	
	// try to get the Native Interface Code loaded and initialized
	// TBD get this working on Mac and Linux/Unix
	static {
	// try to load and initialize the JNI code
		try {
			bNativeInterfaceCodeLoaded = false ;
//			if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:statics:about to load PdbTMReaderWriter.dll"); }
			// load the DLL
			System.loadLibrary("PdbTMReaderWriter");
//			if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:statics:just back from loading PdbTMReaderWriter.dll"); }
			bNativeInterfaceCodeLoaded = true ;
			
//			if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:statics:about to initialize PdbTMReaderWriter.dll"); }
			// initialize the DLL
			if (ptmrwInitialize() == SUCCESS) {
				bNativeInterfaceCodeInitialized = true;
				}
			else {
				bNativeInterfaceCodeInitialized = true;
				}
//			if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:statics:just back from initializing PdbTMReaderWriter.dll"); }
			} // end try
		catch (SecurityException e) {
//			if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:statics:library won't load due to security problems"); }
			} // end catch
		catch (UnsatisfiedLinkError e) {
//			if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:statics:library or library function can't be found"); }
			} // end catch
		catch (Exception e) {
//			if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:statics:unspecified error"); }
			} // end catch
		
		} // end static
*/	
	
	// constructor
	public PdbTMReaderWriter () {
		super() ;
	
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:constructor:DLL is loaded? " + bNativeInterfaceCodeLoaded); }
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:constructor:DLL is initialized? " + bNativeInterfaceCodeInitialized); }
		
		
		} // end constructor

	
	// process a PDB record chunk
	// NOTE this is PDB-flavor specific
	// over-rides PdbReaderWriter.processRecord
	protected int processRecord (int recordCounter, byte[] recordData, short recordAttributes, int recordUniqueID) 
		{
							
		// if it's not the first record
		if (recordCounter > 0) {
			
			// it's a node
			
			
			// determine the node level
			// TBD make this a JNI call
			int nodeLevel = (int) BitsAndBytes.unsignedByte(recordData, PDB_TM_NODE_LEVEL) ;
			
			// grab the node text
			// TBD make this a JNI call
			
			// determine the length of the node text
			int nodeTextLength = recordData.length 
						- PDB_TM_NODE_TEXT 
						- PDB_TM_APRES_TEXT 
						- 1 ; // trailing 0 at end of text
			
			// grab the node text into a String
			String nodeText = new String(recordData, PDB_TM_NODE_TEXT, nodeTextLength) ;
		
			// create a free-standing node with the proper contents
			NodeImpl node = new NodeImpl(null, nodeText);

			// let's set some node attributes
			// TBD
			// TBD make this a JNI call

			// okay, the node's all set up
					
			// add the node to the outline
			ourContentHandler.addNodeToOutline(node, nodeLevel) ;

			} // end if it's not the first record
					
		// else it's the first record
		else {
			
			// first record is special
			// contains ??? data
			// handle that
			// TBD
			// TBD make this a JNI call
			
			} // end else it's the first record
		
		
		// return a result
		// TBD make this real
		// TBD while doing so, get a try/catch in here
		
		return SUCCESS ;
			
		} // end protected method processRecord	

	// process a PDB AppInfo block
	// this is a PDB-flavor specific method
	protected int processAppInfo (byte[] appInfo)
		{
		
		// TBD
			
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:processAppInfo"); }
			
		return SUCCESS ;
			
		} // end method processAppInfo;


	// process a PDB SortInfo block
	// this is a PDB-flavor specific method
	protected int processSortInfo (byte[] sortInfo)
		{
		
		// TBD
			
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:processSortInfo"); }
			
		return SUCCESS ;
		
		} // end protected method processSortInfo;
	
	} // end class PdbTMReaderWriter
	

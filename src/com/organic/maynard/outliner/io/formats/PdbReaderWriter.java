/**
 * PdbReaderWriter class
 * 
 * Reads and writes Palm Pilot pdb files
 *
 * This is the vanilla Palm PDB reader-writer class
 * Other Palm PDB reader-writers should extend this
 * 
 * TBD	Instances of PdbReaderWriter read Palm Pilot Pdb files
 * TBD	but, because they know nothing about flavor-specific formats 
 * TBD	for Header, AppInfo, SortInfo, and Record block data, 
 * TBD	they build outlines in which each
 * TBD	block is identified by a level 0 node, and the node's contents
 * TBD	are put in level 1 subnodes in hex and text representations,
 * TBD	16 bytes per node
 *
 * Specific flavors of Palm PDB files should extend PdbReaderWriter
 * Methods to tweak for specific PDB flavors include
 *
 *	constructor
 *	
 *	processHeader
 *	processAppInfo
 *	processSortInfo
 *	processRecord
 *	
 *	prepareHeader
 *	prepareAppInfo
 *	prepareSortInfo
 *	prepareRecord
 * 
 * members
 *	constants
 *		class
 *			protected
 *				int PDB_HEADER_LENGTH
 *				int PDB_HEADER_APP_INFO
 *				int PDB_HEADER_SORT_INFO
 *				int PDB_RECORD_LIST_ENTRIES_START
 *				int PDB_RECORD_LIST_ENTRY_LENGTH
 *				int PDB_RL_ATTRIB
 *				int PDB_RL_UNIQUE
 *	variables
 *		instance
 *			protected
 *				PdbContentHandler ourContentHandler
 *				PdbErrorHandler ourErrorHandler
 *	methods
 * 		instance
 *			public
 *				void PdbReaderWriter ()	[constructor]
 *				
 *				int setContentHandler (PdbContentHandler) 
 *				int setErrorHandler (PdbErrorHandler) 
 *				
 *				int read (DataInputStream)
 *				
 *			protected
 *				int processHeader (byte[])
 *				int processAppInfo (byte[])
 *				int processSortInfo (byte[])
 *				int processRecord (int, byte[], short, int) 
 *				
 *
 *		
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 9/3/01 8:25AM
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
import java.io.* ;

// we read and write Palm pdb files
public class PdbReaderWriter  

	implements JoeReturnCodes {
	
	// private class constants
	
	// common PDB file stuff
	protected static final int PDB_HEADER_LENGTH = 72 ;
	protected static final int PDB_HEADER_APP_INFO = 52 ;
	protected static final int PDB_HEADER_SORT_INFO = 56 ;
	protected static final int PDB_RECORD_LIST_ENTRIES_START = 78;
	protected static final int PDB_RECORD_LIST_ENTRY_LENGTH = 8 ;
	protected static final int PDB_RL_ATTRIB = 4 ;
	protected static final int PDB_RL_UNIQUE = 5 ;
	
	// private instance vars
	protected PdbContentHandler ourContentHandler= null ;
	protected PdbErrorHandler ourErrorHandler = null ;

	// constructor
	public PdbReaderWriter () 
		{
		// subclasses can place flavor-specific init stuff here
		
//		if (Outliner.DEBUG) System.out.println("\tStan_Debug:\tPdbReaderWriter:constructor"); 

		} // end constructor

	
	// set the content handler
	// the read and write methods, and their delegees, send outline data 
	// to the content handler, which places it into an outline
	public int setContentHandler (PdbContentHandler aContentHandler) {
		
		// we don't accept null handlers
		if (aContentHandler == null) {
			return NULL_OBJECT_REFERENCE ; 
			} // end if
		
		// set it, return in triumph
		ourContentHandler = aContentHandler ;
		return SUCCESS ;
		
		} // end method setContentHandler

	
	// set the error handler
	public int setErrorHandler (PdbErrorHandler anErrorHandler) {
		
		// we don't accept null handlers
		if (anErrorHandler == null) {
			return NULL_OBJECT_REFERENCE ; 
			} // end if
		
		// set it, return in triumph
		ourErrorHandler = anErrorHandler ;
		return SUCCESS ;
		
		} // end method setErrorHandler

	
	// read and parse the file
	
	// called from a Pdb*FileFormat.open method
	
	// as outline elements are digested, spew em to our PdbContentHandler
	// (which is usually a Pdb*FileFormat thing)
	
	// we receive the file as a DataInputStream, 
	// and we store the raw info in raw byte arrays
	
	// this function is generic
	//based on the stock pdb header/recordList/AppInfo/SortInfo/Records structure
	
	// it calls four methods that do all flavor-specific work
	//	processHeaderInfo
	//	processAppInfo
	//	processSortInfo
	//	processRecord
	
	// the only time you'd need to subclass this method is
	// if a particular PDB-flavor mangles the stock pdb structure
	
	public int read (DataInputStream byteStream)
		{
		
		// local vars
		
		// assume success
		int readResult = SUCCESS ;
		
		// number of records in the pdb file
		int numRecords ;
		
		// loop counter
		int recordCounter ;
		
		// place finder-keepers
		int recordDataOffset = 0 ;
		int nextRecordDataOffset ;
		int recordListByteOffset;
		int appInfoOffset ;
		int sortInfoOffset ;
		int nextRecordListOffset ;	// rarely non-zero
		
		// record properties
		int recordUniqueID ;	// we use just 3 bytes of this
		short recordAttributes ;
		
		// sizes of data blocks
		int appInfoSize ;
		int sortInfoSize ;
		int recordListSize ; 
		int recordDataSize ;
		
		// byte arrays to store raw data
		byte[] header ;
		byte[] recordList = null;
		byte[] appInfo;
		byte[] sortInfo;
		byte[] recordData ;
		
		// scratchpads
		int scratch00 ;
		
		// end local vars
		
		// try to do read the file and spew contents to an outline
		try {	
			// call on the content handler to start the outline
			ourContentHandler.startOutline() ;
			
			// determine the file's length
			int fileLength = byteStream.available() ;
			
			// read the file's header info
			header = new byte [PDB_HEADER_LENGTH] ;
			byteStream.read(header) ;
			
			// do any special header processing
			processHeader(header) ;
			
			// read the next record list id
			// this should be zero
			// TBD deal with this intelligently in the rare case it isn't
			// TBF for now we just ignore other record lists
			nextRecordListOffset = byteStream.readInt() ;
			
			// we're at the first record list
			// read the number of records
			numRecords = byteStream.readUnsignedShort() ;
			
			// if there are some records
			if (numRecords != 0) {
			
				// read in the array of recordList entries
				recordListSize = numRecords * PDB_RECORD_LIST_ENTRY_LENGTH ;
				recordList = new byte [recordListSize] ;
				byteStream.read (recordList) ;
				
				// determine the offset to the first record
				// it's in the first recordList entry
				recordDataOffset = (int) BitsAndBytes.unsignedInt(recordList, 0, BitsAndBytes.HI_TO_LO) ;
				
				} // end if
			
			// read padding bytes
			scratch00 = byteStream.readShort() ;
				
			// grap the AppInfo and SortInfo block offsets
			appInfoOffset = (int) BitsAndBytes.unsignedInt(
					header, PDB_HEADER_APP_INFO, BitsAndBytes.HI_TO_LO) ;
			sortInfoOffset = (int) BitsAndBytes.unsignedInt(
					header, PDB_HEADER_SORT_INFO, BitsAndBytes.HI_TO_LO) ;
			
			// if there's an AppInfo block ...
			if (appInfoOffset > 0) {
				
				// determine the size of the AppInfo block
				
				// if there's a SortInfo block
				if (sortInfoOffset > 0) {
				
					// a SortInfo block follows an AppInfo block
					appInfoSize = sortInfoOffset - appInfoOffset ;
					
					} // end if there's a SortInfo block
					
				// else there's no SortInfo block
				else {
					// if there are some records
					if (numRecords != 0) {
					
						// block after AppInfo is the first record
						// use its offset for AppInfo size calculation
						appInfoSize = recordDataOffset - appInfoOffset ;
						
						} // end if there are some records
						
					// else there are no records
					else {
					
						// there are no blocks after AppInfo
						// use the size of the file itself for AppInfo size calculation
						appInfoSize = fileLength - appInfoOffset ;
						
						} // end else there are some records
					
					} // end else there are no records
				
				// okay. we know the size of the AppInfo block
				
				// one last filter: make sure the block size is > 0
				if (appInfoSize > 0) {
					
					// create a byte array
					appInfo = new byte [appInfoSize] ;
					
					// read the data
					byteStream.read (appInfo) ;
					
					// process it
					// NOTE: this is a PDB-flavor-specific method
					processAppInfo(appInfo) ;
					
					} // end if AppInfo block is > 0
				
				} // end if there's an AppInfo chunk

			// if there's a  SortInfo block ...
			if (sortInfoOffset > 0) {
				
				// determine the size of the SortInfo block
				
				// if there are some records
				if (numRecords != 0) {
				
					// block after SortInfo is the first record
					// use its offset for SortInfo size calculation
					sortInfoSize = recordDataOffset - sortInfoOffset ;
					
					} // end if there are some records
					
				// else there are no records
				else {
				
					// there are no blocks after SortInfo
					// use the size of the file itself for SortInfo size calculation
					sortInfoSize = fileLength - sortInfoOffset ;
					
					} // end else there are no records
				
				// okay. we know the size of the SortInfo block
				
				// one last filter: make sure the block size is > 0
				if (sortInfoSize > 0) {
					
					// create a byte array
					sortInfo = new byte [sortInfoSize] ;
					
					// read the data
					byteStream.read (sortInfo) ;
					
					// process it
					// NOTE: this is a PDB-flavor-specific method
					processSortInfo(sortInfo) ;
					
					} // end if SortInfo block is > 0
				
				} // end if there's a Sort Info chunk
			
			// if we have no records
			if (numRecords == 0) {
			
				// we're done
				
				// it's just an extremely empty outline
				
				// finish off the outline
				ourContentHandler.finishOutline() ;
				
				} // end if there are no records
			
			// else there are some records
			else { 
				
				// we're ready to read in and process the records
				
				// for each record list entry
				for (	recordCounter = 0; 
					
					recordCounter < numRecords; 
					
					recordCounter++,
					recordDataOffset = nextRecordDataOffset) {
										
					// figure our byte offset into the record list
					// NOTE cheap trick used here to multiply by 8,
					//	the current size of a record list entry
					//IF that changes, this trick must change/go
					recordListByteOffset = recordCounter << 3 ; 
					
					// grab the rest of the record list entry data
					recordAttributes = BitsAndBytes.unsignedByte(
							recordList, 
							recordListByteOffset + PDB_RL_ATTRIB) ;
					recordUniqueID = BitsAndBytes.unsignedBytesToInt(
							recordList, 
							recordListByteOffset + PDB_RL_UNIQUE,
							 3,
							BitsAndBytes.HI_TO_LO) ;
					
					// we need to figure out the size of this record
					
					//if we're not at the final record ...
					if (recordCounter < (numRecords -1)) {
						
						// grab next record's offset
						nextRecordDataOffset = (int) BitsAndBytes.unsignedInt(
							recordList, 
							recordListByteOffset + PDB_RECORD_LIST_ENTRY_LENGTH,
							BitsAndBytes.HI_TO_LO) ;
					
						// figure record size
						recordDataSize = nextRecordDataOffset - recordDataOffset;
						
						} // end if
					
					// else we're at the final record
					else {
						// figure record size
						recordDataSize = (int)fileLength - recordDataOffset;
						
						// set next record data offset to keep compiler happy
						nextRecordDataOffset = 0 ;
						
						} // end else
					
					// we've got the size of the record data
					
					// read the record data into a byte array
					recordData = new byte[recordDataSize] ;
					byteStream.read(recordData) ;
					
					// process the record data
					// NOTE: this is a PDB-flavor-specific method
					processRecord(	recordCounter,
							recordData,
							recordAttributes,
							recordUniqueID) ;
					
					} // end for each item in the record list
				
				// finish off the outline
				ourContentHandler.finishOutline() ;
				
				} // end else there are some records
			
			}  // end try
		
		// deal with problems
		// TBD make this a more specific set of catchers
		catch (IOException e) {
			
			String exceptionClass = new String(e.getClass().getName()) ;
			if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbTMReaderWriter:read: just had a[n] " + exceptionClass + " exception"); }
			
			readResult = FAILURE ;
			
			} // end catch
	
		return readResult ;
		
		} // end method read
	
	
	// process a PDB header
	
	// this is a PDB-flavor specific method
	// this vanilla version does nothing
	
	protected int processHeader (byte[] header) 
		{
		
		// TBD
		
//		if (Outliner.DEBUG) 
//			System.out.println ("\tStan_Debug:\tPdbReaderWriter:processHeader"); }
		
		return SUCCESS ;
			
		} // end protected method processRecord	


	// process a PDB record block
	
	// this is a PDB-flavor specific method
	// TBD this vanilla version spews the record out in hex and text format
	
	protected int processRecord (int recordCounter, byte[] recordData, short recordAttributes, int recordUniqueID) 
		{
		
		// TBD
		
//		if (Outliner.DEBUG) { System.out.println
//			("\tStan_Debug:\tPdbReaderWriter:processRecord for record #" + (recordCounter+1)); }
		
		return SUCCESS ;
			
		} // end protected method processRecord	


	// process a PDB AppInfo block
	
	// this is a PDB-flavor specific method
	// this vanilla version spews the block out in hex and text format
	
	protected int processAppInfo (byte[] appInfo)
		{
		
		// TBD
			
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbReaderWriter:processAppInfo"); }
			
		return SUCCESS ;
			
		} // end method processAppInfo;


	// process a PDB SortInfo block
	
	// this is a PDB-flavor specific method
	// TBD this vanilla version spews the block out in hex and text format
	
	protected int processSortInfo (byte[] sortInfo)
		{
		
		// TBD
			
//		if (Outliner.DEBUG) { System.out.println("\tStan_Debug:\tPdbReaderWriter:processSortInfo"); }
			
		return SUCCESS ;
		
		} // end protected method processSortInfo;


	} // end class PdbReaderWriter
	

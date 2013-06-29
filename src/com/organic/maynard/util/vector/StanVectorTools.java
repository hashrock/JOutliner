/**
 * StanVectorTools class
 * 
 * A few useful vector tools
 * 
 * members
 *	methods
 *		public
 *			class aka static
 *				[TBD] void ensureSizeSaveHead (Vector, int)
 *				void ensureSizeSaveTail (Vector, int)
 *				void moveElementsHeadward (Vector, int, int, int) ;
 *				[TBD] void moveElementsTailward (Vector, int, int, int) ;
 *				void moveElementToHead (Vector)
 *				void moveElementToTail (Vector)
 *				void swapElements (Vector, int, int)
 *				void removeDupesHeadside (Vector)
 *				[TBD] void removeDupesTailside (Vector)
 *				reverse (Vector)
 *				Vector arrayListToVector (ArrayList)
 *				ArrayList vectorToArrayList (Vector) 
 *
 *		
 * Copyright (C) 2002 Stan Krute <Stan@StanKrute.com>
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
 * @version $Revision: 1.8 $ Date:$
 */

// we're part of this
package com.organic.maynard.util.vector;

// we use these
import java.util.ArrayList ;
import java.util.Vector ;
import java.util.TreeSet ;

// a few useful vector tools
public class StanVectorTools {

	// TBD [srk] run rigorous tests on these to ensure no memory leakage
	
	// Public Class Methods
	
	// move an element to the tail end of a vector
	public static void moveElementToTail(Vector someVector, int index) {
		
		// size - 1 is useful
		int limit = someVector.size() - 1;
		
		// validate parameters
		// also leave if element's already at the tail
		if ((someVector == null) || (index < 0) || (index >= limit)) {
			return ;
		} // end if
		
		// grab the element to be moved
		Object theNewTail = someVector.get(index) ;
		
		// move everyone further tailward one spot headward
		for (int position = index, stop = limit ; position < stop; position ++) {
			someVector.set(position, someVector.get(position + 1)) ;
		} // end for
		
		// set the movee tailmost
		someVector.set(limit, theNewTail) ;
		
		// done
		return ;
		
	} // end method moveElementToTail 
		
		
	// move an element to the head end of a vector
	public static void moveElementToHead(Vector someVector, int index) {
		
		// size is useful
		int size = someVector.size();
		
		// validate parameters
		// also leave if element is already at the head
		if ((someVector == null) || (index <= 0) || (index >= size)) {
			return ;
		} // end if
		
		// grab the element to be moved
		Object theNewHead = someVector.get(index) ;
		
		// move everyone further headward one spot tailward
		for (int position = index - 1; position >= 0; position --) {
			someVector.set(position + 1, someVector.get(position)) ;
		} // end for
		
		// set the movee headmost
		someVector.set(0, theNewHead) ;
		
		// done
		return ;
		
	} // end method moveElementToTop 
		
		
	// swap the positions of two elements
	public static void swapElements(Vector someVector, int indexOne, int indexTwo) {
		
		// size is useful
		int size = someVector.size() ;
		
		// validate parameters
		// also leave in identity case
		if (	(someVector == null) || 
			(indexOne < 0) || (indexOne >= size)||
			(indexTwo < 0) || (indexTwo >= size)||
			(indexOne == indexTwo)	)
			{
			return ;
		} // end if
		
		// grab element one
		Object elementOne = someVector.get(indexOne) ;
		
		// set element two in its place
		someVector.set(indexOne, someVector.get(indexTwo)) ;
		
		// set it in element two's place
		someVector.set(indexTwo, elementOne) ;
		
		// done
		return ;
		
	} // end method swapElements


	// trim a vector's size
	// if method has to eliminate entries, it eliminates them from the head
	public static void trimSizeSaveTail(Vector someVector, int goalSize) {
	
	// get our current size
	int curSize = someVector.size() ;
	
	// compare with the goal size
	int diff = curSize - goalSize ;
	
	// if we're just right ...
	if (diff == 0) {
		// leave
		return ;
	} // end if
	
	// if we're too large
	if (diff > 0) {
		
		// move goalSize entries headward
		for (int i = 0, j = diff ; i < goalSize; i++, j++) {
			someVector.set(i, someVector.get(j)) ;
		} // end for

		// this'll chop off excess entries
		someVector.setSize(goalSize) ;
		
	} // end if
	
	} // end method trimSizeSaveTail


	// remove duplicate entries
	// if method has to eliminate entries, it eliminates them from the headward side
	public static void removeDupesHeadside(Vector someVector) {
	
		// we'll be storing census info here
		TreeSet censusTree = new TreeSet() ;
		if (censusTree == null) {
			return ;
		} // end if
		
		// grab the vector's starting size
		int size = someVector.size() ;
		
		// starting at the tail
		int position = size - 1;
		
		// local var to hold each vector element;s hashcode
		Integer hash = null ;
		
		// until we get all the way to the head
		while (position >= 0) {
			
			// grab the element's hashcode
			hash = new Integer(someVector.get(position).hashCode()) ;
			
			// if this element's hashcode isn't in the tree yet
			if (! censusTree.contains(hash)) {
				
				// add it to the tree
				censusTree.add(hash) ;
				
			// else this is a dupe
			} else {
				// slide more tailward elements headwards
				moveElementsHeadward(someVector, position + 1, size-1, 1) ;
				
				// that crushes the dupe
				
				// our size just shrunk
				size-- ;
				
			} // end if-else notIn-isDupe
			
			// next !
			position-- ;
			
		} // end while
		
		// okay, any dupes are gone
		
		// if our size changed ...
		if (size < someVector.size()) {
			
			// resize
			someVector.setSize(size) ;
			
			// setSize cuts off from the tail end
			// which is correct, since we slid uniques headwards
				
		} // end if our size changed
			
	} // end method removeDupesHeadside
	
	
	// moves a range of elements headward
	public static void moveElementsHeadward(
					Vector someVector, 
					int startElement,
					int stopElement,
					int magnitude ){
		
		// local vars
		int size = 0 ;
		int limit = 0 ;
		int scratch = 0 ;
		
		// make sure start's <=  stop
		if (startElement > stopElement) {
			scratch = startElement ;
			startElement = stopElement ;
			stopElement = scratch ;
		} // end if we had to swap stop/start
						
		// if something's hinky with the parameters
		if (	(someVector == null) ||
			((size = someVector.size()) == 0) ||
			(startElement < 0) ||
			(startElement > (limit = size - 1)) ||
			(stopElement < 0) ||
			(stopElement > limit) ||
			((startElement - magnitude) < 0) ||
			(magnitude == 0) 	
			) {
			return ;
		} // end if bad parms
		
		// for each element to be moved
		for (int position = startElement; position <= stopElement; position ++) {
			
			// move it
			someVector.set(position - magnitude, someVector.get(position)) ;
			
		} // end for
		
	} // end method moveElementsHeadward
	
	
	// Vectorize an ArrayList
	public static Vector arrayListToVector (ArrayList someArrayList) {
		
		// if we've got a null param, leave
		if (someArrayList == null) {
			return null ;
		} // end if
		
		// try to create a vector
		Vector someVector = new Vector () ;
		
		// if we failed, leave
		if (someVector == null) {
			return null ;
		} // end if
		
		// okay, we've got a right-sized Vector, it's .... Copy Time !
		
		// for each element of the ArrayList ...
		for (int counter = 0, aLsize = someArrayList.size(); counter < aLsize; counter++) {
			
			// copy it to the Vector
			someVector.add(someArrayList.get(counter)) ;
			
		} // end for
		
		// done
		return someVector ;
		
	} // end arrayListToVector
	
	
	// ArrayListize a Vector
	public static ArrayList vectorToArrayList (Vector someVector) {
		
		// if we've got a null param, leave
		if (someVector == null) {
			return null ;
		} // end if
		
		// try to create an ArrayList
		ArrayList someArrayList = new ArrayList () ;
		
		// if we failed, leave
		if (someArrayList == null) {
			return null ;
		} // end if
		
		// okay, we've got a right-sized ArrayList, it's .... Copy Time !
		
		// for each element of the Vector ...
		for (int counter = 0, vecSize = someVector.size(); counter < vecSize; counter++) {
			
			// copy it to the ArrayList
			someArrayList.add(someVector.get(counter)) ;
			
		} // end for
		
		// done
		return someArrayList ;
		
	} // end vectorToArrayList
	
	
} // end class StanVectorTools

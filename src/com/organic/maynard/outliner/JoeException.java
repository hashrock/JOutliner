/**
 * JoeException class
 * 
 * JOE-specific exceptions
 * uses JOE return codes
 * 
 * extends Exception
 * implements JoeReturnCodes
 * 
 * members
 *	constants
 *		class
 *			public
 *				aaa
 *
 *		
 * Copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
 * Last Touched: 9/11/01 2:29PM
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
 
 // we're part of this
package com.organic.maynard.outliner;

// we use these
import java.util.* ;
import java.lang.* ;

// we read and write Palm pdb files 
public class JoeException 
	
	extends Exception 	
	implements 
		JoeReturnCodes {

	// private class variables
	static private HashMap exceptionMessages ;
	
	// class initialization
	static {
		exceptionMessages = new HashMap() ;
		
		exceptionMessages.put(new Integer(FAILURE), "FAILURE") ;
		exceptionMessages.put(new Integer(ARRAY_SELECTOR_OUT_OF_BOUNDS), "ARRAY_SELECTOR_OUT_OF_BOUNDS") ;
		exceptionMessages.put(new Integer(DOCUMENT_NOT_FOUND), "DOCUMENT_NOT_FOUND") ;
		exceptionMessages.put(new Integer(DOCUMENT_IN_USE_ELSEWHERE), "DOCUMENT_IN_USE_ELSEWHERE") ;
		exceptionMessages.put(new Integer(UNABLE_TO_ALLOCATE_MEMORY), "UNABLE_TO_ALLOCATE_MEMORY") ;
		exceptionMessages.put(new Integer(PRINTER_COMMUNICATION_FAILURE), "PRINTER_COMMUNICATION_FAILURE") ;
		exceptionMessages.put(new Integer(ATTEMPT_TO_DIVIDE_BY_ZERO), "ATTEMPT_TO_DIVIDE_BY_ZERO") ;
		exceptionMessages.put(new Integer(USER_ABORTED), "USER_ABORTED") ;
		exceptionMessages.put(new Integer(URL_NOT_FOUND), "URL_NOT_FOUND") ;
		exceptionMessages.put(new Integer(NULL_OBJECT_REFERENCE), "NULL_OBJECT_REFERENCE") ;
		exceptionMessages.put(new Integer(UNABLE_TO_CREATE_OBJECT), "UNABLE_TO_CREATE_OBJECT") ;
		} // end static
	
		
	// Constructors
	
	public JoeException(int exceptionSelector) {
		super ((String) exceptionMessages.get(new Integer(exceptionSelector))) ;

		} // end constructor JoeException
		
	} // end class JoeException


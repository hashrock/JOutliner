/**
 * StanStringTools class
 * 
 * A few useful vector tools
 * 
 * members
 *	methods
 *		class
 *			public
 *				String trimFileExtension (String)
 				String getFileNameFromPathName (String) 
 *
 *		
 * Copyright (C) 2002 Stan Krute <Stan@StanKrute.com>
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
package com.organic.maynard.util.string;

// we use these
import java.util.*;
import java.io.File ;

// Stan's string tools
public class StanStringTools {

	// Class Methods
	
	// trim off last four characters if there's a dot three chars before the end
	public static String trimFileExtension(String fileNameString) {
		
		// local vars
		String resultString = fileNameString ;
		int dotPoint = fileNameString.length() - 4;
		
		// if we have a dot 3 chars in from right side ...
		if (fileNameString.charAt(dotPoint) == '.') {
			
			// trim last 4 chars
			resultString = fileNameString.substring(0, dotPoint) ;
			
		}
		
		return resultString ;
		
	} // end method trimOffAnyFileExtension
	

	// grab a filename from a pathname
	public static String getFileNameFromPathName(String pathNameString) {
		
		File file = new File (pathNameString) ;
		if (file == null) {
			return null ;
		} else {
			return file.getName() ;
		} // end else
		
	} // end method trimOffAnyFileExtension

	
	// return a truncated pathname
	public static String getTruncatedPathName(String pathNameString, String truncationString) {
		
		// we keep info thru the first directory
		// then separator..separator
		// then filename
		
		// if just two separators in pathname, we do nothing
		// c:\foo.txt
		// one separator, do nothing
		
		// c:\moo\foo.txt
		// two separators, do nothing
		
		// c:\moo\boo\foo.txt
		// three separators
		// replace info tween separators 2 and 3 with ..
		// c:\moo\..\foo.txt
		
		// c:\moo\boo\goo\foo.txt
		// four separators
		// replace
		
		// so: our scheme:
		// scan for separators, from left
		// note positions of 2nd and, if more than 2, last
		
		// then: build a string out of substring thru 2nd sep
		// plus our trunc string
		// plus substring from last sep thru til end
		
		// local vars
		int secondSeparator = -1 ;
		int lastSeparator = -1 ;
		int length = pathNameString.length() ;
		
		// we're scanning the full string
		for (int scanner = 0, separatorCount = 0; scanner < length; scanner ++) {
			// if we find a separator char ...
			if (pathNameString.charAt(scanner) == File.separatorChar) {
				// note the find
				separatorCount ++ ;
				// if this is the second separator ..
				if (separatorCount == 2) {
					secondSeparator = scanner ;
				// else it's provisionally the last separator
				} else {
					lastSeparator = scanner ;
				} // end if-else
			} // end if
		} // end for
		
		// if we have 2 or fewer separators, just return the pathname
		if ( (secondSeparator == -1) ||  (lastSeparator < secondSeparator) ) {
			return pathNameString ;
		} // end if
		
		// okay, we have more than 2 separators
		// [srk] var here just temp for testing ... return directly once cooked
		String resultString = (pathNameString.substring(0, secondSeparator + 1)
			+ truncationString
			+ pathNameString.substring(lastSeparator, length)) ;
		
		return resultString ;
		
	} // end method getTruncatedPathName

} // end class StanStringTools

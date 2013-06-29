/**
 * Portions copyright (C) 2001 Maynard Demmon <maynard@organic.com>
 * Portions copyright (C) 2001 Stan Krute <Stan@StanKrute.com>
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

package com.organic.maynard.outliner.io;

import java.util.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2002/12/16 01:51:45 $
 */

public interface FileFormat {
	
	/**
	 * Gets the name of this format.
	 */
	public String getName();
	
	/**
	 * Sets the name of this format.
	 */
	public void setName(String name);
	
	/**
	 * Adds a file extension to this format.
	 *
	 * @param ext the extension to add.
	 * @param isDefault indicates that the provided
	 *        extension is the default. If the format already
	 *        has a default extension then that extension will lose
	 *        it's default status, but will still remain as a
	 *        regular extension unless specifically removed.
	 */
	public void addExtension(String ext, boolean isDefault);
	
	/**
	 * Removes an extension from this format.
	 *
	 * @param ext the file extension to remove.
	 */
	public void removeExtension(String ext);
	
	/**
	 * Gets an Iterator consisting of all extensions contained
	 * by this format.
	 *
	 * @return an Iterator of file extensions for this format.
	 */
	public Iterator getExtensions();
	
	/**
	 * Gets the default file extension for this format.
	 *
	 * @return the default extension.
	 */
	public String getDefaultExtension();
	
	/**
	 * Indicates if the extension exists for this file
	 * format.
	 *
	 * @param ext the extension we are checking existence for.
	 * @return true indicates the extension exists, false
	 *         indicates it does not.
	 */
	public boolean extensionExists(String ext);
	
	
	/** Indicates if this format can store comment attributes. */
	public boolean supportsComments();
	
	/** Indicates if this format can store editability attributes. */
	public boolean supportsEditability();
	
	/** Indicates if this format can store moveability attributes. */
	public boolean supportsMoveability();
	
	/** Indicates if this format can store node attributes. */
	public boolean supportsAttributes();
	
	/** Indicates if this format can store document attributes. */
	public boolean supportsDocumentAttributes();
}
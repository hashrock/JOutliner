/**
 * Portions copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002   Stan Krute <Stan@StanKrute.com>
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

import com.organic.maynard.outliner.model.DocumentInfo;
import com.organic.maynard.outliner.model.propertycontainer.*;
import com.organic.maynard.outliner.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.4 $, $Date: 2004/01/30 00:12:42 $
 */

public interface FileProtocol {
	/**
	 * Used by the <code>selectFileToSave</code> method to indicate that the 
	 * GUI should be configured for a save.
	 */	
	public static final int SAVE = 0;
	
	/**
	 * Used by the <code>selectFileToSave</code> method to indicate that the 
	 * GUI should be configured for an export.
	 */	
	public static final int EXPORT = 1;
	
	/**
	 * Used by the <code>selectFileToOpen</code> method to indicate that the 
	 * GUI should be configured for an open.
	 */	
	public static final int OPEN = 2;
	
	/**
	 * Used by the <code>selectFileToOpen</code> method to indicate that the 
	 * GUI should be configured for an import.
	 */	
	public static final int IMPORT = 3;
	
	/**
	 * Gets the name of this protocol.
	 *
	 * @return this protocol's unique name.
	 */
	public String getName();
	
	/**
	 * Sets the name of this protocol. This name must be unique. The name is 
	 * also used for protocol selection in the GUI.
	 *
	 * @param name sets this protocol's unique name.
	 */	
	public void setName(String name);
	
	/**
	 * Handles selection of a file to open. All GUI elements involved in the 
	 * selection process must be managed by this method. The results of this 
	 * selection process are placed in the provided <code>DocumentInfo</code> 
	 * object. It is strongly suggested that the GUI allow the user to select 
	 * an <code>OpenFileFormat</code> and an <code>encoding type</code>.
	 *
	 * @param docInfo the <code>DocumentInfo</code> object to store the results 
	 *                of the selection process.
	 * @param type     indicates if this is an Open or an Import.
	 * @return        <code>true</code> indicates success and <code>false</code> 
	 *                indicates failure.
	 */		
	public boolean selectFileToOpen(DocumentInfo docInfo, int type);
	
	/**
	 * Handles selection of a file to save. All GUI elements involved in the 
	 * selection process must be managed by this method. The results of this 
	 * selection process are placed in the provided <code>OutlinerDocument's</code> 
	 * associated <code>DocumentInfo<code> object. It is strongly suggested 
	 * that the GUI allow the user to select an <code>OpenFileFormat</code>, 
	 * line ending (platform) and an <code>encoding type</code>.
	 *
	 * @param document the <code>OutlinerDocument</code> that contains the 
	 *                 <code>DocumentInfo</code> object to store the results 
	 *                 of the selection process.
	 * @param type     indicates if this is a Save or an Export.
	 * @return         <code>true</code> indicates success and <code>false</code> 
	 *                 indicates failure.
	 */	
	public boolean selectFileToSave(OutlinerDocument document, int type);
	
	/**
	 * Saves the data stored in the DocumentInfo. The <code>byte[]</code> data is pulled
	 * from the DocumentInfo object's getOutputBytes method.
	 *
	 * @param docInfo the <code>DocumentInfo</code> object that holds the 
	 *                <code>byte[]</code> array to save.
	 * @return        <code>true</code> indicates success and <code>false</code> 
	 *                indicates failure.
	 */		
	public boolean saveFile(DocumentInfo docInfo);
	
	/**
	 * Open a file and stores an <code>InputStream</code> in the <code>DocumentInfo</code>. 
	 * The <code>InputStream</code> is stored by using the <code>DocumentInfo</code> 
	 * object's <code>setInputStream</code> method.
	 *
	 * @param docInfo the <code>DocumentInfo</code> object that holds the 
	 *                <code>InputStream</code> for the file to open.
	 * @return        <code>true</code> indicates success and <code>false</code> 
	 *                indicates failure.
	 */	
	public boolean openFile(DocumentInfo docInfo);
}
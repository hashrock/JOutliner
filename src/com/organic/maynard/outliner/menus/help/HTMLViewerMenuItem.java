/**
 * Copyright (C) 2003 Maynard Demmon, maynard@organic.com
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

package com.organic.maynard.outliner.menus.help;

/**
 * Loads an HTML page via the HTMLViewerDialog.
 *
 * @author  $Author: maynardd $
 * @version $Revision: 1.2 $, $Date: 2004/02/02 10:17:42 $
 */

import com.organic.maynard.outliner.menus.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.guitree.*;
import java.awt.event.*;
import org.xml.sax.*;
import java.net.URL;
import java.io.IOException;

public class HTMLViewerMenuItem extends AbstractOutlinerMenuItem implements ActionListener, GUITreeComponent, JoeReturnCodes {
	
	// Constants
	public static final String A_PATH = "path";
	
	
	// Instance Fields
	private URL resource_url = null;
	
	
	// GUITreeComponent interface
	public void startSetup(Attributes atts) {
		super.startSetup(atts);
		
		String path = atts.getValue(A_PATH);
		
		URL url_path = null;
		if (path == null) {
			System.out.println("WARNING: path attribute not provided for HTMLViewerMenuItem: " + getText());
		} else if (path.indexOf("://") != -1) {
			try {
				url_path = new URL(path);
			} catch (java.net.MalformedURLException e) {
				System.out.println("WARNING: path for HTMLViewerMenuItem: " + getText() + " was not a valid URL: " + path);
			}
		} else {
			url_path = Thread.currentThread().getContextClassLoader().getResource(path);
		}
		
		if (url_path != null) {
			setResourceURL(url_path);
			
			addActionListener(this);
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}
	
	
	// Accessors
	private void setResourceURL(URL url) {
		resource_url = url;
	}
	
	private URL getResourceURL() {
		return resource_url;
	}
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		Outliner.html_viewer.show();
		Outliner.html_viewer.addURL(getResourceURL());
	}
}
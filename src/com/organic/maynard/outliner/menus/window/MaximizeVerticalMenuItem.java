/**
 * Copyright (C) 2002  Stan Krute <Stan@StanKrute.com>
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
 * @version $Revision: 1.3 $, $Date: 2004/02/02 10:17:42 $
 */

package com.organic.maynard.outliner.menus.window;

import com.organic.maynard.outliner.menus.*;
import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.guitree.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.xml.sax.*;

public class MaximizeVerticalMenuItem 
	extends AbstractOutlinerMenuItem 
	implements ActionListener, GUITreeComponent 
{
	
	// GUITreeComponent interface
	public void startSetup(Attributes atts) {
		super.startSetup(atts);
		addActionListener(this);
	}
	
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// if we're not in a totally-maximized state 
		// [which would make this all pointless] ...
		if (!Outliner.desktop.isMaximized()) {
			
			// grabaholda the topmost doc
			OutlinerDocument doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
			
			// see how tall we can get
			Dimension curAvailSpace = Outliner.desktop.getCurrentAvailableSpace();
			double maxHeight = curAvailSpace.getHeight();
			
			// get the doc's current location
			Point pLocation = doc.getLocation();
		
			// set its top point to the top of the content area
			pLocation.setLocation(pLocation.getX(), 0);
			
			// get the doc's current size
			Dimension dSize = new Dimension() ;
			dSize.setSize((int)doc.getSize().getWidth(), (int)maxHeight);
			
			// set the doc's new location and size
			doc.setLocation(pLocation);
			doc.setSize(dSize);
			
			// let the vertical scroll bar adjust for our new size
			Outliner.jsp.getVerticalScrollBar().revalidate();
		}
	}
}
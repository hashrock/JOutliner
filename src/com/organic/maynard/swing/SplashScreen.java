/**
 * Copyright (C) 2002 Maynard Demmon, maynard@organic.com
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

package com.organic.maynard.swing;

/**
 * Based on code found at: http://developer.java.sun.com/developer/qow/archive/24/index.html.
 *
 * @author  $Author: maynardd $
 * @version $Revision: 1.1 $, $Date: 2002/08/18 08:51:57 $
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.JWindow;

public class SplashScreen extends JWindow {
	
	// Instance Fields
	Image image = null;

	// Constructor
	public SplashScreen(URL filename) {
		// Get Image
		MediaTracker mt = new MediaTracker(this);
		image = Toolkit.getDefaultToolkit().getImage(filename);
		mt.addImage(image,0);
		try {
			mt.waitForID(0);
		} catch(InterruptedException ie){}

		// Set size of image
		setSize(image.getWidth(null),image.getHeight(null));
		
		// Center Window
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winDim = getBounds();
		setLocation((screenDim.width - winDim.width) / 2,(screenDim.height - winDim.height) / 2);
		
		setVisible(true);
	}

        @Override
	public void paint(Graphics g) {
		if (image != null) {
			g.drawImage(image,0,0,this);
		}
	}
}
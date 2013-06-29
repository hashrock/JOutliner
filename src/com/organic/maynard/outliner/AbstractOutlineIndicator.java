/**
 * Copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
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
 
package com.organic.maynard.outliner;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.awt.image.*;
import java.awt.geom.*;

abstract public class AbstractOutlineIndicator extends JLabel {

	// Static Fields
	public static int SPACING = 4;

	// Instance Fields
	public OutlinerCellRendererImpl renderer = null;
	
	private boolean prop = false;
	private boolean propInherited = false;


	// The Constructor
	public AbstractOutlineIndicator(OutlinerCellRendererImpl renderer, String toolTipText) {
		this.renderer = renderer;
		
		setVerticalAlignment(SwingConstants.TOP);
		setOpaque(true);
		setVisible(false);

		setToolTipText(toolTipText);
		
		updateIcon();
	}
	
	public void destroy() {
		removeAll();
		renderer = null;
	}
	
	public boolean isManagingFocus() {return true;}

	// Used to fire key events
	public void fireKeyEvent(KeyEvent event) {
		processKeyEvent(event);
	}


	// Accessors
	public boolean isProperty() {return prop;}
	public void setProperty(boolean prop) {this.prop = prop;}

	public boolean isPropertyInherited() {return propInherited;}
	public void setPropertyInherited(boolean propInherited) {this.propInherited = propInherited;}


	// Abstract Methods
	abstract public void updateIcon();
}
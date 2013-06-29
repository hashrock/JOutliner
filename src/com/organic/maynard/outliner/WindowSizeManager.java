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

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.6 $, $Date: 2004/01/30 00:12:42 $
 */

public class WindowSizeManager implements ComponentListener {
	
	// Fields
	private int minWidth = 100;
	private int minHeight = 100;
	
	private int initialWidth = 100;
	private int initialHeight = 100;
	
	private boolean resizeOnShow = true;
	
	
	// The Constructor
	public WindowSizeManager(int minWidth, int minHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}
	
	public WindowSizeManager(int initialWidth, int initialHeight, int minWidth, int minHeight) {
		this(true, initialWidth, initialHeight, minWidth, minHeight);
	}
	
	public WindowSizeManager(boolean resizeOnShow, int initialWidth, int initialHeight, int minWidth, int minHeight) {
		this.resizeOnShow = resizeOnShow;
		
		this.minWidth = minWidth;
		this.minHeight = minHeight;
		this.initialWidth = initialWidth;
		this.initialHeight = initialHeight;
	}
	
	
	// ComponentListener Interface
	public void componentResized(ComponentEvent e) {
		Component comp = e.getComponent();
		
		int width = comp.getWidth();
		int height = comp.getHeight();
		
		boolean resize = false;
		
		if (width < minWidth) {
			resize = true;
			width = minWidth;
		}
		if (height < minHeight) {
			resize = true;
			height = minHeight;
		}
		if (resize) {
			comp.setSize(width, height);
		}
	}
	
	public void componentMoved(ComponentEvent e) {}
	
	public void componentShown(ComponentEvent e) {
		if (resizeOnShow) {
			e.getComponent().setSize(initialWidth, initialHeight);
		}
	}
	
	public void componentHidden(ComponentEvent e) {}
}
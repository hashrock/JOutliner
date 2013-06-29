/**
 * Portions copyright (C) 2000, 2001 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002 Stan Krute <Stan@StanKrute.com>
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
import javax.swing.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.15 $, $Date: 2004/01/31 00:33:09 $
 */

public class OutlinerDesktop extends JDesktopPane implements Scrollable {
	
	// The Constructor
	public OutlinerDesktop() {
		super();
		//setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		setDesktopManager(new OutlinerDesktopManager());
	}
	
	
	// Accessors
	public boolean isMaximized() {
		return ((OutlinerDesktopManager) getDesktopManager()).isMaximized();
	}
	
	public void setMaximized(boolean maximized) {
		((OutlinerDesktopManager) getDesktopManager()).setMaximized(maximized);
	}
	
	
	// Overridden Methods
	public Dimension getPreferredSize() {
		if (isMaximized()) {
			return new Dimension(getParent().getWidth(), getParent().getHeight());
		}
		
		int scrollBarWidth = Outliner.jsp.getVerticalScrollBar().getWidth();
		int maxWidth = getParent().getWidth() - scrollBarWidth;
		int maxHeight = getParent().getHeight() - scrollBarWidth;
		
		Component[] children = getComponents();
		
		for (int i = 0; i < children.length; i++) {
			Component component = children[i];
			
			if (component instanceof JInternalFrame) {
				if (!((JInternalFrame) component).isIcon()) {
					Point p = component.getLocation();
					int x = component.getWidth() + p.x;
					int y = component.getHeight() + p.y;
					
					if (x > maxWidth) {
						maxWidth = x;
					}
					
					if (y > maxHeight) {
						maxHeight = y;
					}
				}
			}
		}
		//System.out.println(maxWidth + " : " + maxHeight);
		return new Dimension(maxWidth, maxHeight);
	}
	
	
	// Scrollable Interface
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	
	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		//System.out.println("getScrollableUnitIncrement");
		switch(orientation) {
			case SwingConstants.VERTICAL:
				return visibleRect.height / 10;
				
			case SwingConstants.HORIZONTAL:
				return visibleRect.width / 10;
				
			default:
				throw new IllegalArgumentException("Invalid orientation: " + orientation);
		}
	}
	
	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		//System.out.println("getScrollableBlockIncrement");
		switch(orientation) {
			case SwingConstants.VERTICAL:
				return visibleRect.height;
				
			case SwingConstants.HORIZONTAL:
				return visibleRect.width;
				
			default:
				throw new IllegalArgumentException("Invalid orientation: " + orientation);
		}
	}
	
	public boolean getScrollableTracksViewportHeight() {
		//System.out.println("getScrollableTracksViewportHeight");
		return false;
	}
	
	public boolean getScrollableTracksViewportWidth() {
		//System.out.println("getScrollableTracksViewportWidth");
		return false;
	}
	
	// useful for filling the available space
	public Dimension getCurrentAvailableSpace() {
		return new Dimension(getParent().getWidth(), getParent().getHeight());
	}
	
	// add any visible scrollbars to available space value
	// useful before tiling, since scrollbars melt away post-tile
	public void addScrollbarsToAvailSpace(Dimension availSpace) {
		// if a vertical scrollbar is showing ...
		JScrollBar scrollbar = Outliner.jsp.getVerticalScrollBar();
		if (scrollbar.isVisible()) {
			availSpace.width += scrollbar.getWidth();
		}
		
		// if a horizontal scrollbar is showing ...
		scrollbar = Outliner.jsp.getHorizontalScrollBar();
		if (scrollbar.isVisible()) {
			availSpace.height += scrollbar.getHeight();
		}
	}
}
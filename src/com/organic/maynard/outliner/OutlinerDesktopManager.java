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

import com.organic.maynard.outliner.guitree.*;
import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.outliner.dom.Document;

import java.awt.*;
import javax.swing.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.22 $, $Date: 2004/01/31 00:33:09 $
 */

public class OutlinerDesktopManager extends DefaultDesktopManager {
	
	// Direction Constants
	private static final int NORTH = 1;
	private static final int NORTHEAST = 2;
	private static final int EAST = 3;
	private static final int SOUTHEAST = 4;
	private static final int SOUTH = 5;
	private static final int SOUTHWEST = 6;
	private static final int WEST = 7;
	private static final int NORTHWEST = 8;
	
	// Minimized Icon Constants
	private static final int ICON_WIDTH = 150;
	private static final int ICON_HEIGHT = 25;
	
	
	private boolean isDragging = false;
	
	//JInternalFrame State
	private int resizeDirection = 0;
	private int startingX = 0;
	private int startingWidth = 0;
	private int startingY = 0;
	private int startingHeight = 0;
	
	public static boolean activationBlock = false;
	
	
	// The Constructor
	public OutlinerDesktopManager() {
		super();
	}
	
	
	public boolean isDragging() {
		return isDragging;
	}
	
	public boolean isMaximized() {
		return Preferences.getPreferenceBoolean(Preferences.IS_MAXIMIZED).cur;
	}
	
	public void setMaximized(boolean b) {
		Preferences.getPreferenceBoolean(Preferences.IS_MAXIMIZED).cur = b;
	}
	
	// DesktopManagerInterface
	public void beginResizingFrame(JComponent f, int direction) {
		//System.out.println("beginResizingFrame");
		resizeDirection = direction;
		startingX = f.getLocation().x;
		startingWidth = f.getWidth();
		startingY = f.getLocation().y;
		startingHeight = f.getHeight();
		super.beginResizingFrame(f,direction);
	}
	
	public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
		//System.out.println("resizingFrame");
		// Ensure a minimum size for the window.
		if (f instanceof JInternalFrame) {
			int minWidth = OutlinerDocument.MIN_WIDTH;
			int minHeight = OutlinerDocument.MIN_HEIGHT;
			
			if (newWidth < minWidth) {
				newWidth = minWidth;
				if ((resizeDirection == NORTHWEST) || (resizeDirection == WEST) || (resizeDirection == SOUTHWEST)) {
					newX = (startingX + startingWidth - minWidth);
				} else {
					newX = f.getLocation().x;
				}
			}
			
			if (newHeight < minHeight) {
				newHeight = minHeight;
				if ((resizeDirection == NORTHEAST) || (resizeDirection == NORTH) || (resizeDirection == NORTHWEST)) {
					newY = (startingY + startingHeight - minHeight);
				} else {
					newY = f.getLocation().y;
				}
			}
		}
		
		// Prevent resizing of the frame above or to the left.
		if (newY < 0) {
			newHeight += newY;
			newY = 0;
		}
		
		if (newX < 0) {
			newWidth += newX;
			newX = 0;
		}
		
		super.resizeFrame(f,newX,newY,newWidth,newHeight);
		updateDesktopSize(false);
	}
	
	public void endResizingFrame(JComponent f) {
		//System.out.println("endResizingFrame");
		super.endResizingFrame(f);
	}
	
	public void beginDraggingFrame(JComponent f) {
		//System.out.println("beginDraggingFrame");
		isDragging = true;
		super.beginDraggingFrame(f);
	}
	
	public void dragFrame(JComponent f, int newX, int newY) {
		//System.out.println("dragFrame");
		
		// Prevent dragging of the frame above the visible area. To the left is ok though.
		if (newY < 0) {newY = 0;}
		
		super.dragFrame(f,newX,newY);
		updateDesktopSize(false);
	}
	
	public void endDraggingFrame(JComponent f) {
		//System.out.println("endDraggingFrame");
		isDragging = false;
		super.endDraggingFrame(f);
		updateDesktopSize(true);
	}
	
	public void activateFrame(JInternalFrame f) {
		if (activationBlock) {return;}
		
		//System.out.println("activateFrame " + f.getTitle());
		super.activateFrame(f);
		
		
		if (f instanceof Document) {
			Outliner.documents.setMostRecentDocumentTouched((Document) f);
		}
		
		// Move the internalframe back so it's visible if it's outside the visible rect.
		Rectangle r = Outliner.jsp.getViewport().getViewRect();
		Rectangle r2 = f.getBounds();
		
		if (!r.intersects(r2)) {
			setBoundsForFrame(f, r.x + 5, r.y + 5, f.getWidth(), f.getHeight());			
		}
	}
	
	public void deactivateFrame(JInternalFrame f) {
		//System.out.println("deactivateFrame");
		super.deactivateFrame(f);
	}
	
	public void openFrame(JInternalFrame f) {
		//System.out.println("openFrame");
		super.openFrame(f);
		updateDesktopSize(false);
	}
	
	public void closeFrame(JInternalFrame f) {
		//System.out.println("closeFrame");
		super.closeFrame(f);
		updateDesktopSize(false);
	}
	
	public void iconifyFrame(JInternalFrame f) {
		//System.out.println("iconifyFrame");
		super.iconifyFrame(f);
		f.getDesktopIcon().setSize(ICON_WIDTH,ICON_HEIGHT);
	}
	
	public void deiconifyFrame(JInternalFrame f) {
		//System.out.println("deiconifyFrame");
		super.deiconifyFrame(f);
		updateDesktopSize(false);
	}
	
	public void maximizeFrame(JInternalFrame f) {
		//System.out.println("maximizeFrame: " + f.getTitle());
		setMaximized(true);
		super.maximizeFrame(f);
		
		// Move it to the front since under certain conditions it may not already be there.
		f.moveToFront();
		
		// Disable Stack Menu Item
		JMenuItem item = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.STACK_MENU_ITEM);
		item.setEnabled(false);
		
		// Remove the border
		((OutlinerDocument) f).hideBorder();
		
		// Make sure JInternalFrame is sized to the viewport, not the desktop.
		Dimension d = new Dimension(Outliner.jsp.getViewport().getWidth(), Outliner.jsp.getViewport().getHeight());
		f.setSize(d);
		updateDesktopSize(false);
	}
	
	public void minimizeFrame(JInternalFrame f) {
		//System.out.println("minimizeFrame: " + f.getTitle());
		setMaximized(false);
		super.minimizeFrame(f);
		
		// Enable Stack Menu Item
		JMenuItem item = (JMenuItem) GUITreeLoader.reg.get(GUITreeComponentRegistry.STACK_MENU_ITEM);
		item.setEnabled(true);
		
		// Restore the border
		((OutlinerDocument) f).showBorder();
		
		updateDesktopSize(false);
	}
	
	public void setBoundsForFrame(JComponent f, int newX, int newY, int newWidth, int newHeight) {
		//System.out.println("setBoundsForFrame");
		if (!(f instanceof JInternalFrame)) {
			newWidth = ICON_WIDTH;
			newHeight = ICON_HEIGHT;
			
			newX = findNearest(newX,ICON_WIDTH) * ICON_WIDTH;
			newY = findNearest(newY,ICON_HEIGHT) * ICON_HEIGHT;
		}
		
		super.setBoundsForFrame(f,newX,newY,newWidth,newHeight);
	}
	
	
	// Utility Methods
	private int findNearest(int value, int partition) {
		return value/partition;
	}
	
	private void updateDesktopSize(boolean repaint) {
		// This is just flailing to get it to redraw itself.
		Outliner.jsp.revalidate();
		Outliner.jsp.validate();
		
		Outliner.jsp.getHorizontalScrollBar().repaint();
		Outliner.jsp.getVerticalScrollBar().repaint();
		
		if (repaint) {
			Outliner.desktop.repaint();
		}
	}
}
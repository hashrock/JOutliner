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
import java.util.Vector ;
import java.lang.Math ;

/* ------------------------
 * [srk] currently under 
 * heavy construction.
 * please ignore the mess.
 * hope to finish by 1-31-02.
 * ------------------------
 */

public class TileGridMenuItem 
	extends AbstractOutlinerMenuItem 
	implements ActionListener, GUITreeComponent 
{
	// TBD [srk] gui_tree.xml and user-pref this stuff
	// we don't tile more than this many windows
	private static final int TILE_LIMIT = 100;
	// we have styles of tiling
	// these tile styles deal with the distribution of
	// regular/fat rows
	private static final int BOTTOM_HEAVY = 1;
	private static final int TOP_HEAVY = 2;
	private static final int HOUR_GLASS = 3;
	private static final int BLIMP = 4;
	private static int tileStyle = BOTTOM_HEAVY;
	
	// calculate a tiling pattern for a spec'd # of windows
	private int [] calcTilePattern (int numWindows) {
		// local vars
		int numColumns;
		int windowsAccountedFor;
		
		// no pattern for no windows
		if (numWindows < 1) {
			return null ;
		}
		
		// calculate pattern engine parameters
		int numRows = Math.round((float)Math.sqrt(numWindows));
		boolean uneven = (numWindows % numRows) > 0;
		int weightChangeRow = numRows - (numWindows % numRows);
		int regularRowSize = Math.round((float)((numWindows/numRows) - 0.5));
		int fatRowSize = regularRowSize + 1;
		int [] pattern = new int[numRows + 2];
		int fatCounter = 0;
		
		// for each row of the pattern
		for (int rowCounter = 1; rowCounter <= numRows; rowCounter++) {
			
			// add its number of columns to the pattern
			// if itza fat row ...
			if (uneven && (rowCounter > weightChangeRow)) {
				pattern[rowCounter -1] = fatRowSize;
				// may as well count fat rows while we're here
				// data comes in handy for tiling styling
				fatCounter++;
			// else itza regular row
			} else {
				pattern[rowCounter -1] = regularRowSize;
			}
		}
		
		// stick a data tail on the pattern donkey
		pattern[numRows] = regularRowSize;
		pattern[numRows + 1] = fatCounter;
		
		// return the pattern
		return pattern;
	}
	
	
	// GUITreeComponent interface
	public void startSetup(Attributes atts) {
		super.startSetup(atts);
		addActionListener(this);
	}
	
	
	// ActionListener Interface
	// we've been clicked - deal with it
	public void actionPerformed(ActionEvent e) {
		
		// determine how many documents are open
		int openDocCount = Outliner.documents.openDocumentCount();
		
		// if no documents are open, leave
		if (openDocCount == 0) {
			return ;
		}
		
		// a general-purpose doc var
		OutlinerDocument doc = null;
		
		// let's build a list of not-iconified windows
		// [we don't touch iconified windows]
		Vector notIconified = new Vector();
		
		// for each open document
		for (int counter = 0; counter < openDocCount; counter++) {
			// grab the doc ref
			doc = (OutlinerDocument) Outliner.documents.getDocument(counter);
			
			// if we're not iconified
			if (! doc.isIcon()) {
				// add us to the list
				notIconified.add(doc);
			}
		}
		
		// store the count
		int openNotIconifiedDocCount = notIconified.size();
		
		// if everybody's iconified, leave
		if (openNotIconifiedDocCount == 0) {
			return;
		}
		
		// if we're in a maximized state ...
		if (Outliner.desktop.isMaximized()) {
			// leave that state
			Outliner.desktop.setMaximized(false);
			
			// if there's a topmost window
			doc = (OutlinerDocument) Outliner.documents.getMostRecentDocumentTouched();
			if (doc != null) {
				// have it leave the max state
				try {
					doc.setMaximum(false);
				} catch (java.beans.PropertyVetoException pve) {
					pve.printStackTrace();
				}
			}
		}
		
		// get the raw available space
		Dimension curAvailSpace = Outliner.desktop.getCurrentAvailableSpace();
		
		// since we're tiling, any scrollbars will melt away
		Outliner.desktop.addScrollbarsToAvailSpace (curAvailSpace);
		
		// determine how much room we have to play with
		double availWidth = curAvailSpace.getWidth();
		double availHeight = curAvailSpace.getHeight();
		
		// obtain minimum tiling width and height values
		// TBD [srk] make this for real via window features figgern' and user prefs
		int minTileRowHeight = 45;
		int minTileColumnWidth = 60;
		
		// determine the maximum number of rows and columns
		int maxRows = (int)availHeight/minTileRowHeight;
		int maxColumns = (int)availWidth/minTileColumnWidth;
		
		// some row and column vars
		int regularRowHeight = 0;
		int regularColumnWidth = 0;
		int finalRowHeight = 0;
		int finalColumnWidth = 0;
		int actualRows = 0;
		int actualMaxColumns = 0;
		
		// obtain a tiling pattern
		// it contains the number of columns for each row
		int [] pattern = calcTilePattern(openNotIconifiedDocCount);
		
		// determine pattern's number of rows
		int patternRowCount = pattern.length - 2;
		
		// grab data tail from pattern donkey:
		
		// determine the number of columns in regular and fat rows
		int patternRegRowColumnCount = pattern [patternRowCount];
		int patternFatRowColumnCount = patternRegRowColumnCount + 1;
		
		// determine # of fat and regular rows in pattern
		int fatRowCount = pattern[patternRowCount + 1];
		int regRowCount = patternRowCount - fatRowCount;
		
		// end gdtfpd
		
		// how many cells in the whole pattern
		int patternTotalCellCount = patternRegRowColumnCount * regRowCount
					+ patternFatRowColumnCount * fatRowCount;
		
		// plenty of room ?
		boolean plentyOfRowRoom = patternRowCount <= maxRows;
		boolean plentyOfColumnRoom = patternFatRowColumnCount <= maxColumns;
		boolean plentyOfRoom = plentyOfRowRoom && plentyOfColumnRoom;
		
		// some vars for window size and location info
		Point ptLocation = new Point();
		Dimension dimSize = new Dimension();
		
		// if we have plenty of room
		if (plentyOfRoom) {
			// block local vars
			int docLimit = patternTotalCellCount;
			int docCounter = 0;
			int rowsColumnCount = 0;
			boolean isFatRow = false;
			int widthStd = 0;
			int widthAdj = 0;
			int rowY = 0;
			int rowHeight = 0;
			
			// determine column widths
			
			// in a regular row
			int regRowColumnWidthStd = (int) (availWidth/patternRegRowColumnCount);
			int regRowColumnWidthAdj = (int) availWidth -
						patternRegRowColumnCount * regRowColumnWidthStd;
			// for standard columns and adjustment columns
			// in a fat row
			int fatRowColumnWidthStd = (int) (availWidth/patternFatRowColumnCount);
			int fatRowColumnWidthAdj = (int) availWidth -
						patternFatRowColumnCount * fatRowColumnWidthStd;
			
			// determine row heights
			int rowHeightStd = (int) (availHeight/patternRowCount);
			int rowHeightAdj = (int) availHeight -
						patternRowCount * rowHeightStd;
			
			// for each row in the pattern
			for (int row = 0; row < patternRowCount; row++) {
				
				// determine the row's y-axis location
				rowY = row * rowHeightStd;
				
				// determine the row's height
				rowHeight = rowHeightStd;
				
				// adjust bottom row's height
				if (row == (patternRowCount - 1)) {
					rowHeight = rowHeight + rowHeightAdj;
				}
				
				// grab the row's number of columns
				// switch on tile style
				switch(tileStyle) {
				
				case BOTTOM_HEAVY:
					rowsColumnCount = pattern[patternRowCount - 1 - row];
					break;
					
				case TOP_HEAVY:
					rowsColumnCount = pattern[row];
					break;
				
				case HOUR_GLASS:  // TBD
				case BLIMP:  // TBD
				default: // TBD
					rowsColumnCount = pattern[row] ;
					break ;
				}
				
				// set up width info based on fat/reg
				if (rowsColumnCount == patternRegRowColumnCount) {
					widthStd = regRowColumnWidthStd;
					widthAdj = regRowColumnWidthAdj;
				} else {
					widthStd = fatRowColumnWidthStd;
					widthAdj = fatRowColumnWidthAdj;
				}
				
				// for each column in the row 
				for (int column = 0; column < rowsColumnCount; column ++) {
					
					// grab the next document
					doc = (OutlinerDocument)notIconified.get(docCounter++);
					
					// set up location
					ptLocation.setLocation(column * widthStd,rowY);
					
					// set up size
					if (column < (rowsColumnCount - 1)) {
						dimSize.setSize(widthStd,rowHeight);
					} else {
						dimSize.setSize(widthStd+widthAdj,rowHeight);
					}
					
					// set the doc's new location and size
					doc.setLocation(ptLocation);
					doc.setSize(dimSize);
				}
			}
		}
		
		// else we don't have enuf room, and must limit
		// ourselves to the size of the pattern
		else {
			// how many extras are there ??
			// what's the minimum height ??
			// how many extras fit in a column 
			// do we have enuf columns for extras
		}
		
		// clean up the scrollbars' areas
		Outliner.jsp.getHorizontalScrollBar().revalidate();
		Outliner.jsp.getVerticalScrollBar().revalidate();
	}
}
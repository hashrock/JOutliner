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

import com.organic.maynard.outliner.util.preferences.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class OutlineLineNumber extends JLabel {

	// Constants
	public static final int LINE_NUMBER_WIDTH_DEFAULT = 30;
	public static final int LINE_NUMBER_WIDTH_MIN = 0;
	public static final int LINE_NUMBER_HEIGHT_DEFAULT = 15;

	// Class Variables
	public static int LINE_NUMBER_HEIGHT = LINE_NUMBER_HEIGHT_DEFAULT;
	public static int LINE_NUMBER_WIDTH = LINE_NUMBER_WIDTH_DEFAULT;
	
	// Instance Fields
	public OutlinerCellRendererImpl renderer = null;
	
	// The Constructor
	public OutlineLineNumber(OutlinerCellRendererImpl renderer) {
		this.renderer = renderer;
		
		setVerticalAlignment(SwingConstants.TOP);
		
		if (Preferences.getPreferenceBoolean(Preferences.SHOW_LINE_NUMBERS).cur || Preferences.getPreferenceBoolean(Preferences.SHOW_INDICATORS).cur) {
			setOpaque(true);
		} else {
			setOpaque(false);
		}
				
		setVisible(false);
	}
	
	public void destroy() {
		removeAll();
		renderer = null;
	}
	
	public boolean isManagingFocus() {return true;}
}
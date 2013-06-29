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
import javax.swing.*;
import java.awt.event.*;

public class OutlinerPanel extends JPanel implements MouseWheelListener {
	
	// GUI Fields
	public OutlinerDocument doc = null;
	public OutlineLayoutManager layout = new OutlineLayoutManager(this);
	
	
	// The Constructor
	public OutlinerPanel(OutlinerDocument doc) {
		this.doc = doc;
		setBackground(Preferences.getPreferenceColor(Preferences.PANEL_BACKGROUND_COLOR).cur);
		setLayout(layout);
		
		addMouseWheelListener(this);
		
		//addMouseMotionListener(new TestMouseMotionListener());
	}
	
	public void destroy() {
		removeNotify();
		doc = null;
		
		setLayout(null);
		layout.destroy();
		layout = null;
		
		removeAll();
	}
	
	// MouseWheelListener Interface
	public void mouseWheelMoved(MouseWheelEvent e) {
		int clicks = e.getWheelRotation() * Preferences.getPreferenceInt(Preferences.MOUSE_WHEEL_SCROLL_SPEED).cur;
		BoundedRangeModel model = this.layout.scrollBar.getModel();
		model.setValue(model.getValue() + clicks);
	}
}

/*
public class TestMouseMotionListener extends MouseMotionAdapter {

	public void mouseMoved(MouseEvent e) {
		System.out.println("[" + e.getX() + "," + e.getY() + "]");
	}

}
*/

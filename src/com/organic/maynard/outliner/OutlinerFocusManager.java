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
import java.util.*;
import javax.swing.*;
import javax.swing.text.Caret;

import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

/**
 * This class allows re-routing of keyEvents back to the correct OutlinerCellRendererImpl.
 * It is possible when a draw is occuring that changes the draw direction that keyEvents will
 * be sent to the old renderer before the focus manager has a chance to catch up. This class
 * intercepts any miss-targetd key events and sends them off to the correct renderer, the one
 * attached to the current editing node.
 *
 * To see the problem this fixes, comment out this code and drag the scrollbar up and down quickly 
 * as you type. This keeps all the characters going to the current node, without it characters get 
 * inserted into different nodes.
 */
public class OutlinerFocusManager extends DefaultFocusManager {

	public void processKeyEvent(Component c, KeyEvent e) {
		try {
			if (c instanceof OutlinerCellRendererImpl) {
				OutlinerCellRendererImpl renderer = (OutlinerCellRendererImpl) c;
				JoeTree tree = renderer.node.getTree();
				if (renderer.node != tree.getEditingNode()) {
					tree.getDocument().panel.layout.getUIComponent(tree.getEditingNode()).fireKeyEvent(e);
					e.consume();
					return;
				}
			}
		} catch (NullPointerException npe) {
			// Document may have been destroyed in the interim, so let's abort.
			return;
		}
		super.processKeyEvent(c,e);
	}
}
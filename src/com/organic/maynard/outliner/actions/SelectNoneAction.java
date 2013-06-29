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
 
package com.organic.maynard.outliner.actions;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.util.preferences.*;
import com.organic.maynard.outliner.util.undo.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.Window;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

import com.organic.maynard.util.string.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.1 $, $Date: 2002/08/19 07:51:22 $
 */
 
public class SelectNoneAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		//System.out.println("SelectNoneAction");
		
		OutlinerCellRendererImpl textArea  = null;
		boolean isIconFocused = true;
		Component c = (Component) e.getSource();
		if (c instanceof OutlineButton) {
			textArea = ((OutlineButton) c).renderer;
		} else if (c instanceof OutlineLineNumber) {
			textArea = ((OutlineLineNumber) c).renderer;
		} else if (c instanceof OutlineCommentIndicator) {
			textArea = ((OutlineCommentIndicator) c).renderer;
		} else if (c instanceof OutlinerCellRendererImpl) {
			textArea = (OutlinerCellRendererImpl) c;
			isIconFocused = false;
		}
		
		// Shorthand
		Node node = textArea.node;
		JoeTree tree = node.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;

		//System.out.println(e.getModifiers());
		switch (e.getModifiers()) {
			case 2:
				if (isIconFocused) {
					selectNone(node, tree, layout);
				} else {
					selectNoneText(textArea, tree, layout);
				}
				break;
		}
	}


	// KeyFocusedMethods
	public static void selectNoneText(OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
		Node currentNode = textArea.node;
		
		// Clear Text Selection
		int caretPosition = textArea.getCaretPosition();
		tree.setCursorPosition(caretPosition);
		textArea.setCaretPosition(caretPosition);
		textArea.moveCaretPosition(caretPosition);
		
		// Update the undoable if neccessary
		UndoableEdit undoable = tree.getDocument().getUndoQueue().getIfEdit();
		if ((undoable != null) && (undoable.getNode() == currentNode) && (!undoable.isFrozen())) {
			undoable.setNewPosition(caretPosition);
			undoable.setNewMarkPosition(caretPosition);
		}
	}


	// IconFocusedMethods
	public static void selectNone(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		tree.setCursorPosition(0);
		tree.getDocument().setPreferredCaretPosition(0);

		tree.setComponentFocus(OutlineLayoutManager.TEXT);
		tree.clearSelection();
		
		layout.draw(currentNode,OutlineLayoutManager.TEXT);
	}
}
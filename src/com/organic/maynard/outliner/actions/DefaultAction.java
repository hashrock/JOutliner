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
 * @version $Revision: 1.3 $, $Date: 2002/12/11 05:57:35 $
 */
 
public class DefaultAction extends AbstractAction {
	
	public void actionPerformed(ActionEvent e) {
		//System.out.println("DefaultAction");
		
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
		} else if (c instanceof JTextArea) {
			// Should only happen for standard JTextArea components not used in an outliner doc.
			originalDefaultAction(e, (JTextArea) c);
			return;
		}
		
		if (textArea == null) {
			return;
		}
		
		// Shorthand
		Node node = textArea.node;
		JoeTree tree = node.getTree();
		OutlineLayoutManager layout = tree.getDocument().panel.layout;
		
		//System.out.println(e.getModifiers());
		if (isIconFocused) {
			//defaultAction(node, tree, layout);
		} else {               
			defaultActionText(e, textArea, tree, layout);
		}
	}
	
	private static void originalDefaultAction(ActionEvent e, JTextArea textArea) {
		// Insert char into textArea (Code taken from javax.swing.text.DefaultEditorKit.DefaultKeyTypedAction class)
		String content = e.getActionCommand();
		int mod = e.getModifiers();
		if ((content != null) && (content.length() > 0) && ((mod & ActionEvent.ALT_MASK) == (mod & ActionEvent.CTRL_MASK))) {
			char ch = content.charAt(0);
			
			switch(ch) {
				case KeyEvent.VK_ENTER:
				case KeyEvent.VK_TAB:
				case KeyEvent.VK_ESCAPE:
					return;
			}
			
			if ((ch >= 0x20) && (ch != 0x7F)) {
				textArea.replaceSelection(content);
			}
		}
	}
	
	
	// KeyFocusedMethods
	public static void defaultActionText(ActionEvent e, OutlinerCellRendererImpl textArea, JoeTree tree, OutlineLayoutManager layout) {
 		Node currentNode = textArea.node;
		
		int mod = e.getModifiers();
		boolean isControlDown = (mod & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK;
		boolean isAltDown = (mod & ActionEvent.ALT_MASK) == ActionEvent.ALT_MASK;
		boolean isMetaDown = (mod & ActionEvent.META_MASK) == ActionEvent.META_MASK;
		
		// If we're read-only then abort
		if (!currentNode.isEditable()) {
			if (!isControlDown && !isAltDown && !isMetaDown) {
		 		System.out.println("beep!");
				Outliner.outliner.getToolkit().beep();
			}
			return;
		}
		
		if (isControlDown || isAltDown || isMetaDown) {
	 		System.out.println("return and consume");
			return;
		}
		
		// Insert char into textArea (Code taken from javax.swing.text.DefaultEditorKit.DefaultKeyTypedAction class)
		String content = e.getActionCommand();
		if ((content != null) && (content.length() > 0) && ((mod & ActionEvent.ALT_MASK) == (mod & ActionEvent.CTRL_MASK))) {
			char ch = content.charAt(0);
			
			switch(ch) {
				case KeyEvent.VK_ENTER:
				case KeyEvent.VK_TAB:
				case KeyEvent.VK_ESCAPE:
					return;
			}
			
			if ((ch >= 0x20) && (ch != 0x7F)) {
				textArea.replaceSelection(content);
			}
		}
		
		// Record some Values
		int caretPosition = textArea.getCaretPosition();
		
		
		// Update the value in the node
		String oldText = currentNode.getValue();
		String newText = textArea.getText();
		currentNode.setValue(newText);
		
		// Put the Undoable onto the UndoQueue
		UndoableEdit undoable = tree.getDocument().getUndoQueue().getIfEdit();
		if ((undoable != null) && (undoable.getNode() == currentNode) && (!undoable.isFrozen())) {
			undoable.setNewText(newText);
			undoable.setNewPosition(caretPosition);
			undoable.setNewMarkPosition(caretPosition);
		} else {
			tree.getDocument().getUndoQueue().add(new UndoableEdit(currentNode, oldText, newText, tree.getCursorPosition(), caretPosition, tree.getCursorMarkPosition(), caretPosition));
		}
		
		// Record the EditingNode, Mark and CursorPosition
		tree.setEditingNode(currentNode);
		tree.setCursorMarkPosition(textArea.getCaret().getMark());
		tree.setCursorPosition(caretPosition, false);
		tree.getDocument().setPreferredCaretPosition(caretPosition);
		
		// Do the Redraw if we have wrapped or if we are currently off screen.
		if (textArea.getPreferredSize().height != textArea.height || !currentNode.isVisible()) {
			layout.draw(currentNode, OutlineLayoutManager.TEXT);
		}
	}
	
	
	// IconFocusedMethods
	/*public static void defaultAction(Node currentNode, JoeTree tree, OutlineLayoutManager layout) {
		// If we're read-only then abort
		if (!currentNode.isEditable()) {
			return;
		}
		
		// More short names
		Node youngestNode = tree.getYoungestInSelection();
		
		// Clear the selection since focus will change to the textarea.
		tree.clearSelection();
		
		// Replace the text with the character that was typed
		String oldText = youngestNode.getValue();
		//String newText = String.valueOf(e.getKeyChar());
		String newText = "";
		youngestNode.setValue(newText);
		
		// Record the EditingNode and CursorPosition and ComponentFocus
		tree.setEditingNode(youngestNode);
		tree.setCursorPosition(1);
		tree.setComponentFocus(OutlineLayoutManager.TEXT);
		
		// Put the Undoable onto the UndoQueue
		tree.getDocument().getUndoQueue().add(new UndoableEdit(youngestNode, oldText, newText, 0, 1, 0, 1));
		
		// Redraw and Set Focus
		layout.draw(youngestNode, OutlineLayoutManager.TEXT);
	}*/
}
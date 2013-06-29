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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;

import org.ho.yaml.Yaml;

import com.organic.maynard.outliner.actions.BackspaceAction;
import com.organic.maynard.outliner.actions.ChangeFocusAction;
import com.organic.maynard.outliner.actions.CopyAction;
import com.organic.maynard.outliner.actions.CutAction;
import com.organic.maynard.outliner.actions.DefaultAction;
import com.organic.maynard.outliner.actions.DeleteAction;
import com.organic.maynard.outliner.actions.DownAction;
import com.organic.maynard.outliner.actions.EndAction;
import com.organic.maynard.outliner.actions.HomeAction;
import com.organic.maynard.outliner.actions.InsertAndSplitAction;
import com.organic.maynard.outliner.actions.LeftAction;
import com.organic.maynard.outliner.actions.MergeAction;
import com.organic.maynard.outliner.actions.PasteAction;
import com.organic.maynard.outliner.actions.PromoteDemoteAction;
import com.organic.maynard.outliner.actions.RightAction;
import com.organic.maynard.outliner.actions.SelectAllAction;
import com.organic.maynard.outliner.actions.SelectInverseAction;
import com.organic.maynard.outliner.actions.SelectNoneAction;
import com.organic.maynard.outliner.actions.ToggleCommentAction;
import com.organic.maynard.outliner.actions.ToggleEditableAction;
import com.organic.maynard.outliner.actions.ToggleExpansionAction;
import com.organic.maynard.outliner.actions.ToggleMoveableAction;
import com.organic.maynard.outliner.actions.UpAction;
import com.organic.maynard.outliner.util.preferences.Preferences;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.36 $, $Date: 2002/12/09 22:54:31 $
 */

public class OutlinerCellRendererImpl extends JTextArea implements OutlinerCellRenderer {
	
	private static Font font = null;
	private static Font readOnlyFont = null;
	private static Font immoveableFont = null;
	private static Font immoveableReadOnlyFont = null;
	
	private static Cursor cursor = new Cursor(Cursor.TEXT_CURSOR);
	private static Insets marginInsets = new Insets(1,3,1,3);
	
	// Pre-computed values
	protected static int textAreaWidth = 0;
	public static int moveableOffset = 0;
	public static int editableOffset = 0;
	public static int commentOffset = 0;
	public static int lineNumberOffset = 0;
	public static int bestHeightComparison = 0;
	
	// Pre-stored preference settings
	public static int pIndent = 0;
	public static int pVerticalSpacing = 0;
	public static boolean pShowLineNumbers = true;
	public static boolean pShowIndicators = true;
	public static Color pCommentColor = null;
	public static Color pForegroundColor = null;
	public static Color pBackgroundColor = null;
	public static Color pSelectedChildColor = null;
	public static Color pLineNumberColor = null;
	public static Color pLineNumberSelectedColor = null;
	public static Color pLineNumberSelectedChildColor = null;
	
	protected static boolean pApplyFontStyleForComments = true;
	protected static boolean pApplyFontStyleForEditability = true;
	protected static boolean pApplyFontStyleForMoveability = true;
	
	static {
		updateFonts();
	}
	
	// Actions
	private static final ToggleCommentAction toggleCommentAction = new ToggleCommentAction();
	private static final ToggleEditableAction toggleEditableAction = new ToggleEditableAction();
	private static final ToggleMoveableAction toggleMoveableAction = new ToggleMoveableAction();
	private static final ToggleExpansionAction toggleExpansionAction = new ToggleExpansionAction();
	private static final MergeAction mergeAction = new MergeAction();
	private static final SelectNoneAction selectNoneAction = new SelectNoneAction();
	private static final PromoteDemoteAction promoteDemoteAction = new PromoteDemoteAction();
	private static final InsertAndSplitAction insertAndSplitAction = new InsertAndSplitAction();
	private static final ChangeFocusAction changeFocusAction = new ChangeFocusAction();
	private static final SelectAllAction selectAllAction = new SelectAllAction();
	private static final SelectInverseAction selectInverseAction = new SelectInverseAction();
	private static final DeleteAction deleteAction = new DeleteAction();
	private static final BackspaceAction backspaceAction = new BackspaceAction();
	private static final HomeAction homeAction = new HomeAction();
	private static final EndAction endAction = new EndAction();
	private static final CopyAction copyAction = new CopyAction();
	private static final PasteAction pasteAction = new PasteAction();
	private static final CutAction cutAction = new CutAction();
	private static final UpAction upAction = new UpAction();
	private static final DownAction downAction = new DownAction();
	private static final LeftAction leftAction = new LeftAction();
	private static final RightAction rightAction = new RightAction();
	
	private static final DefaultAction defaultAction = new DefaultAction();
	
	
	// Instance Fields
	public Node node = null;
	public OutlineButton button = new OutlineButton(this);
	public OutlineCommentIndicator iComment = new OutlineCommentIndicator(this);
	public OutlineEditableIndicator iEditable = new OutlineEditableIndicator(this);
	public OutlineMoveableIndicator iMoveable = new OutlineMoveableIndicator(this);
	public OutlineLineNumber lineNumber = new OutlineLineNumber(this);
	
	public int height = 0;
	
	public boolean hasFocus = false;
	
	
	// The Constructors
	public OutlinerCellRendererImpl() {
		super();
		
		// Add Actions to TextArea
		setupMaps(this.getInputMap(), this.getActionMap());
		setupMaps(this.button.getInputMap(), this.button.getActionMap());
		
		Keymap keymap = this.getKeymap();
		keymap.setDefaultAction(defaultAction);
		
		
		// Settings
		setFont(font);
		setCursor(cursor);
		setCaretColor(Preferences.getPreferenceColor(Preferences.SELECTED_CHILD_COLOR).cur);
		setMargin(marginInsets);
		setSelectionColor(Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).cur);
		setSelectedTextColor(Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).cur);
		setLineWrap(true);
		
		if (Preferences.getPreferenceString(Preferences.LINE_WRAP).cur.equals(Preferences.TXT_CHARACTERS)) {
			setWrapStyleWord(false);
		} else {
			setWrapStyleWord(true);
		}
		
		//setVisible(false);
	}
	
	private void setupMaps(InputMap input_map, ActionMap action_map) {
		try {
			Object object = Yaml.load(new File(Outliner.USER_PREFS_DIR + "keybind.yaml"));
			System.out.println(object.toString());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.SHIFT_MASK, false), "left");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.CTRL_MASK, false), "left");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.CTRL_MASK + Event.SHIFT_MASK, false), "left");
		action_map.put("left", leftAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.SHIFT_MASK, false), "right");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.CTRL_MASK, false), "right");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.CTRL_MASK + Event.SHIFT_MASK, false), "right");
		action_map.put("right", rightAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "down");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.SHIFT_MASK, false), "down");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.CTRL_MASK, false), "down");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, Event.CTRL_MASK + Event.SHIFT_MASK, false), "down");
		action_map.put("down", downAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.SHIFT_MASK, false), "up");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.CTRL_MASK, false), "up");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, Event.CTRL_MASK + Event.SHIFT_MASK, false), "up");
		action_map.put("up", upAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK, false), "cut");
		action_map.put("cut", cutAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK, false), "paste");
		action_map.put("paste", pasteAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK, false), "copy");
		action_map.put("copy", copyAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_END, 0, false), "end");
		action_map.put("end", endAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0, false), "home");
		action_map.put("home", homeAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, false), "delete");
		action_map.put("delete", deleteAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, false), "backspace");
		action_map.put("backspace", backspaceAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK, false), "select_inverse");
		action_map.put("select_inverse", selectInverseAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK, false), "select_all");
		action_map.put("select_all", selectAllAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0, false), "change_focus");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, Event.SHIFT_MASK, false), "change_focus");
		action_map.put("change_focus", changeFocusAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "insert_and_split");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.SHIFT_MASK, false), "insert_and_split");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK, false), "insert_and_split");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Event.CTRL_MASK + Event.SHIFT_MASK, false), "insert_and_split");
		action_map.put("insert_and_split", insertAndSplitAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false), "promote_demote");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, Event.SHIFT_MASK + Event.SHIFT_MASK, false), "promote_demote");
		action_map.put("promote_demote", promoteDemoteAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK, false), "select_none");
		action_map.put("select_none", selectNoneAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK, false), "merge");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_M, Event.CTRL_MASK + Event.SHIFT_MASK, false), "merge");
		action_map.put("merge", mergeAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0, false), "expansion");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, Event.SHIFT_MASK, false), "expansion");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, Event.CTRL_MASK, false), "expansion");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, Event.CTRL_MASK + Event.SHIFT_MASK, false), "expansion");
		action_map.put("expansion", toggleExpansionAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0, false), "comments");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, Event.SHIFT_MASK, false), "comments");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, Event.CTRL_MASK, false), "comments");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, Event.CTRL_MASK + Event.SHIFT_MASK, false), "comments");
		action_map.put("comments", toggleCommentAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0, false), "editable");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, Event.SHIFT_MASK, false), "editable");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, Event.CTRL_MASK, false), "editable");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, Event.CTRL_MASK + Event.SHIFT_MASK, false), "editable");
		action_map.put("editable", toggleEditableAction);
		
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0, false), "moveable");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, Event.SHIFT_MASK, false), "moveable");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, Event.CTRL_MASK, false), "moveable");
		input_map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, Event.CTRL_MASK + Event.SHIFT_MASK, false), "moveable");
		action_map.put("moveable", toggleMoveableAction);	
	}
	
	public void destroy() {
		removeAll();
		removeNotify();
		
		node = null;
		
		button = null;
		lineNumber = null;
		iComment = null;
		iEditable = null;
		iMoveable = null;
	}
	
	public boolean isManagingFocus() {
		return true;
	}
	
	public static void updateFonts() {
		font = new Font(Preferences.getPreferenceString(Preferences.FONT_FACE).cur, Font.PLAIN, Preferences.getPreferenceInt(Preferences.FONT_SIZE).cur);
		
		readOnlyFont = new Font(Preferences.getPreferenceString(Preferences.FONT_FACE).cur, Font.ITALIC, Preferences.getPreferenceInt(Preferences.FONT_SIZE).cur);
		immoveableFont = new Font(Preferences.getPreferenceString(Preferences.FONT_FACE).cur, Font.BOLD, Preferences.getPreferenceInt(Preferences.FONT_SIZE).cur);
		immoveableReadOnlyFont = new Font(Preferences.getPreferenceString(Preferences.FONT_FACE).cur, Font.BOLD + Font.ITALIC, Preferences.getPreferenceInt(Preferences.FONT_SIZE).cur);
	}
	
	// Used to fire key events
	public void fireKeyEvent(KeyEvent event) {
		processKeyEvent(event);
	}
	
	
	// OutlinerCellRenderer Interface
	public void setVisible(boolean visibility) {
		super.setVisible(visibility);
		button.setVisible(visibility);
		lineNumber.setVisible(visibility);
		if (pShowIndicators) {
			iComment.setVisible(visibility);
			iEditable.setVisible(visibility);
			iMoveable.setVisible(visibility);
		} else {
			iComment.setVisible(false);
			iEditable.setVisible(false);
			iMoveable.setVisible(false);		
		}
	}
	
	protected void verticalShift (int amount) {
		setLocation(getX(), getY() + amount);
		button.setLocation(button.getX(), button.getY() + amount);
		if (pShowLineNumbers) {
			lineNumber.setLocation(lineNumber.getX(), lineNumber.getY() + amount); // Might want to take this out of the if.
		}
		if (pShowIndicators) {
			iComment.setLocation(iComment.getX(), iComment.getY() + amount);
			iEditable.setLocation(iEditable.getX(), iEditable.getY() + amount);
			iMoveable.setLocation(iMoveable.getX(), iMoveable.getY() + amount);
		}
	}
	
	public void drawUp(Point p, Node node) {
		this.node = node;
		
		// Adjust color when we are selected
		updateColors();
		
		// Update the button
		updateButton();
		
		// Update font
		updateFont();
		
		// Draw the TextArea
		setText(node.getValue());
		
		int indent = node.getDepth() * pIndent;
		int width = textAreaWidth - indent;
		
		// Size needs to be set twice. The first time forces the lines to flow. The second then sets the correct height.
		setSize(width,32);
		height = getBestHeight();
		p.y -= (height + pVerticalSpacing);
		setBounds(p.x + indent + OutlineButton.BUTTON_WIDTH, p.y, width, height);
		
		// Draw the Button
		button.setBounds(p.x + indent, p.y, OutlineButton.BUTTON_WIDTH, height);
		
		// Draw the LineNumber
		if (pShowLineNumbers) {
			if (node.getTree().getDocument().hoistStack.isHoisted()) {
				// TODO: This value should be pre-calculated.
				int offset = node.getTree().getDocument().hoistStack.getLineCountOffset() + node.getLineNumber(node.getTree().getLineCountKey());
				lineNumber.setText("" + offset);
			} else {
				lineNumber.setText("" + node.getLineNumber(node.getTree().getLineCountKey()));
			}
		}
		
		lineNumber.setBounds(
			lineNumberOffset, 
			p.y, 
			OutlineLineNumber.LINE_NUMBER_WIDTH + indent, 
			height
		);
		
		// Draw Indicators
		if (pShowIndicators) {
			// Update the Indicators
			updateCommentIndicator();
			updateEditableIndicator();
			updateMoveableIndicator();
			
			iComment.setBounds(
				commentOffset, 
				p.y, 
				OutlineCommentIndicator.BUTTON_WIDTH, 
				height
			);
			
			iEditable.setBounds(
				editableOffset, 
				p.y, 
				OutlineEditableIndicator.BUTTON_WIDTH, 
				height
			);
			
			iMoveable.setBounds(
				moveableOffset, 
				p.y, 
				OutlineMoveableIndicator.BUTTON_WIDTH, 
				height
			);
		}
	}
		
	public void drawDown(Point p, Node node) {
		this.node = node;
		
		// Adjust color when we are selected
		updateColors();
				
		// Update the button
		updateButton();
		
		// Update font
		updateFont();
				
		// Draw the TextArea
		setText(node.getValue());
		
		int indent = node.getDepth() * pIndent;
		int width = textAreaWidth - indent;
		
		// Size needs to be set twice. The first time forces the lines to flow. The second then sets the correct height.
		setSize(width,32);
		height = getBestHeight();
		setBounds(p.x + indent + OutlineButton.BUTTON_WIDTH, p.y, width, height);
		
		// Draw the Button
		button.setBounds(p.x + indent, p.y, OutlineButton.BUTTON_WIDTH, height);
		
		// Draw the LineNumber
		if (pShowLineNumbers) {
			if (node.getTree().getDocument().hoistStack.isHoisted()) {
				// TODO: This value should be pre-calculated.
				int offset = node.getTree().getDocument().hoistStack.getLineCountOffset()  + node.getLineNumber(node.getTree().getLineCountKey());
				lineNumber.setText("" + offset);
			} else {
				lineNumber.setText("" + node.getLineNumber(node.getTree().getLineCountKey()));
			}
		}
		
		lineNumber.setBounds(
			lineNumberOffset, 
			p.y, 
			OutlineLineNumber.LINE_NUMBER_WIDTH + indent, 
			height
		);
		
		// Draw Indicators
		if (pShowIndicators) {
			// Update the Indicators
			updateCommentIndicator();
			updateEditableIndicator();
			updateMoveableIndicator();
			
			iComment.setBounds(
				commentOffset, 
				p.y, 
				OutlineCommentIndicator.BUTTON_WIDTH, 
				height
			);
			
			iEditable.setBounds(
				editableOffset, 
				p.y, 
				OutlineEditableIndicator.BUTTON_WIDTH, 
				height
			);
			
			iMoveable.setBounds(
				moveableOffset, 
				p.y, 
				OutlineMoveableIndicator.BUTTON_WIDTH, 
				height
			);
		}
		
		p.y += height + pVerticalSpacing;
	}
	
	private void updateFont() {
		// [srk] same protection added to updateColors
		// make sure node is not null
		if (node == null) {
			return ;
		} // end if
		
		if (node.isEditable()) {
			setEditable(true);
			
			if (!node.isMoveable() && pApplyFontStyleForMoveability) {
				setFont(immoveableFont);
			} else {
				setFont(font);
			}
		} else {
			setEditable(false);
			
			if (!node.isMoveable() && pApplyFontStyleForMoveability) {
				if (pApplyFontStyleForEditability) {
					setFont(immoveableReadOnlyFont);
				} else {
					setFont(immoveableFont);
				}
			} else {
				if (pApplyFontStyleForEditability) {
					setFont(readOnlyFont);
				} else {
					setFont(font);
				}
			}
		}	
	}
	
	private void updateColors() {
		// [srk] bug -- we got a null pointer exception here
		// make sure node is not null
		if (node == null) {
			return ;
		} // end if
		
		if (node.isAncestorSelected()) {
			if (pApplyFontStyleForComments && node.isComment()) {
				setForeground(pCommentColor);
			} else {
				setForeground(pBackgroundColor);
			}
			
			lineNumber.setForeground(pSelectedChildColor);
			
			if (node.isSelected()) {
				setBackground(pForegroundColor);
				lineNumber.setBackground(pLineNumberSelectedColor);
				button.setBackground(pForegroundColor);
				iComment.setBackground(pLineNumberSelectedColor);
				iEditable.setBackground(pLineNumberSelectedColor);
				iMoveable.setBackground(pLineNumberSelectedColor);
				
			} else {
				setBackground(pSelectedChildColor);
				lineNumber.setBackground(pLineNumberSelectedChildColor);
				button.setBackground(pSelectedChildColor);
				iComment.setBackground(pLineNumberSelectedChildColor);
				iEditable.setBackground(pLineNumberSelectedChildColor);
				iMoveable.setBackground(pLineNumberSelectedChildColor);
			}
			
		} else {
			if (pApplyFontStyleForComments && node.isComment()) {
				setForeground(pCommentColor);
			} else {
				setForeground(pForegroundColor);
			}
			
			setBackground(pBackgroundColor);
			lineNumber.setForeground(pForegroundColor);
			lineNumber.setBackground(pLineNumberColor);
			button.setBackground(pBackgroundColor);
			iComment.setBackground(pLineNumberColor);
			iEditable.setBackground(pLineNumberColor);
			iMoveable.setBackground(pLineNumberColor);
		}
	}
	
	private void updateButton() {
		// [srk] same protection added to updateColors
		// make sure node is not null
		if (node == null) {
			return ;
		} // end if
		
		if (node.isAncestorSelected()) {
			button.setSelected(true);
		} else {
			button.setSelected(false);
		}
		
		if (node.isLeaf()) {
			button.setNode(false);
		} else {
			button.setNode(true);
			if (node.isExpanded()) {
				button.setOpen(true);
			} else {
				button.setOpen(false);
			}
		}
		
		button.updateIcon();
	}
	
	private void updateCommentIndicator() {
		// [srk] same protection added to updateColors
		// make sure node is not null
		if (node == null) {
			return ;
		} // end if
		
		if (node.getCommentState() == Node.COMMENT_TRUE) {
			iComment.setPropertyInherited(false);
			iComment.setProperty(true);
			
		} else if (node.getCommentState() == Node.COMMENT_FALSE) {
			iComment.setPropertyInherited(false);
			iComment.setProperty(false);
		
		} else {
			iComment.setPropertyInherited(true);
			iComment.setProperty(node.isComment());
		}
		
		iComment.updateIcon();
	}
	
	private void updateEditableIndicator() {
		// [srk] same protection added to updateColors
		// make sure node is not null
		if (node == null) {
			return ;
		} // end if
		
		if (node.getEditableState() == Node.EDITABLE_TRUE) {
			iEditable.setPropertyInherited(false);
			iEditable.setProperty(true);
			
		} else if (node.getEditableState() == Node.EDITABLE_FALSE) {
			iEditable.setPropertyInherited(false);
			iEditable.setProperty(false);
		
		} else {
			iEditable.setPropertyInherited(true);
			iEditable.setProperty(node.isEditable());
		}
		
		iEditable.updateIcon();
	}
	
	private void updateMoveableIndicator() {
		// [srk] same protection added to updateColors
		// make sure node is not null
		if (node == null) {
			return ;
		} // end if
		
		if (node.getMoveableState() == Node.MOVEABLE_TRUE) {
			iMoveable.setPropertyInherited(false);
			iMoveable.setProperty(true);
			
		} else if (node.getMoveableState() == Node.MOVEABLE_FALSE) {
			iMoveable.setPropertyInherited(false);
			iMoveable.setProperty(false);
		
		} else {
			iMoveable.setPropertyInherited(true);
			iMoveable.setProperty(node.isMoveable());
		}
		
		iMoveable.updateIcon();
	}
	
	protected int getBestHeight() {
		return Math.max(getPreferredSize().height, bestHeightComparison);
	}
}
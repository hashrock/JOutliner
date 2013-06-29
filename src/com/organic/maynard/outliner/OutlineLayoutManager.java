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
import java.util.*;
import javax.swing.*;
import javax.swing.text.Caret;

import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.44 $, $Date: 2002/12/10 06:17:40 $
 */
 
public class OutlineLayoutManager implements LayoutManager, AdjustmentListener {
	
	static {
		javax.swing.FocusManager.setCurrentManager(new OutlinerFocusManager());
	}
	
	// Constants
	private static final int UP = 1;
	private static final int DOWN = 2;
	
	public static final int TEXT = 0;
	public static final int ICON = 1;
	
	public static final int CACHE_SIZE = Preferences.getPreferenceInt(Preferences.RENDERER_WIDGIT_CACHE_SIZE).cur;
	
	private static final Dimension MINIMUM_DIMENSION = new Dimension(0,32);
	
	
	// Class Fields
	private static Point startPoint = new Point(0,0);
	
	
	// Instance Fields
	private int drawingDirection = DOWN;
	
	private int numNodesDrawn = 1;
	private int ioFirstVisNode = 0;
	private int ioLastVisNode = 0;
	
	private boolean partialCellDrawn = false;
	
	private Node nodeToDrawFrom = null;
	private int ioNodeToDrawFrom = 0;
	
	
	private boolean drawBlock = false; // Used to prevent drawing from happening when we update the scrollbar's value.
	
	private int top = 0;
	private int bottom = 0;
	private int left = 0;
	private int right = 0;
	
	// GUI Components'
	public OutlinerPanel panel = null;
	public JScrollBar scrollBar = new JScrollBar();
	
	// Event Listeners
	private TextKeyListener textListener = new TextKeyListener();
	private IconKeyListener iconListener = new IconKeyListener();
	private IndicatorMouseListener indicatorMouseListener = new IndicatorMouseListener();
	protected InternalDragAndDropListener dndListener = new InternalDragAndDropListener();
	
	// Widgit Cache
	public OutlinerCellRendererImpl[] textAreas = new OutlinerCellRendererImpl[CACHE_SIZE];
	private OutlinerCellRendererImpl hiddenCell = new OutlinerCellRendererImpl();
	
	
	// The Constructors
	public OutlineLayoutManager(OutlinerPanel panel) {
		this.panel = panel;
		
		// Setup and Add the ScrollBar
		scrollBar.addAdjustmentListener(this); 
		panel.add(scrollBar);
		scrollBar.setVisible(true);
		
		// Initialize the Widgits
		for (int i = 0; i < CACHE_SIZE; i++) {
			OutlinerCellRendererImpl renderer = new OutlinerCellRendererImpl();
			panel.add(renderer);
			renderer.addFocusListener(textListener);
			renderer.addKeyListener(textListener);
			renderer.addMouseListener(textListener);
			renderer.addMouseListener(dndListener);
			textAreas[i] = renderer;
			
			panel.add(renderer.button);
			renderer.button.addFocusListener(iconListener);
			renderer.button.addKeyListener(iconListener);
			renderer.button.addMouseListener(iconListener);
			renderer.button.addMouseListener(dndListener);
			
			panel.add(renderer.lineNumber);
			renderer.lineNumber.addFocusListener(iconListener);
			renderer.lineNumber.addKeyListener(iconListener);
			renderer.lineNumber.addMouseListener(iconListener);
			renderer.lineNumber.addMouseListener(dndListener);
			
			panel.add(renderer.iComment);
			renderer.iComment.addMouseListener(indicatorMouseListener);
			
			panel.add(renderer.iEditable);
			renderer.iEditable.addMouseListener(indicatorMouseListener);
			
			panel.add(renderer.iMoveable);
			renderer.iMoveable.addMouseListener(indicatorMouseListener);
		}
		
		// Initialized the hidden components
		hiddenCell.addFocusListener(textListener);
		hiddenCell.addKeyListener(textListener);
		hiddenCell.addMouseListener(textListener);
		panel.add(hiddenCell);
		hiddenCell.button.addFocusListener(iconListener);
		hiddenCell.button.addKeyListener(iconListener);
		hiddenCell.button.addMouseListener(iconListener);
		panel.add(hiddenCell.button);
		hiddenCell.setVisible(true); // JDK 1.4 needs components to be visible for requestFocus() to work.
	}
	
	public void destroy() {
		// Destroy Renderers
		for (int i = 0; i < CACHE_SIZE; i++) {
			panel.remove(textAreas[i]);
			panel.remove(textAreas[i].button);
			panel.remove(textAreas[i].lineNumber);
			panel.remove(textAreas[i].iComment);
			panel.remove(textAreas[i].iEditable);
			panel.remove(textAreas[i].iMoveable);
			
			textAreas[i].button.removeMouseListener(dndListener);
			textAreas[i].button.removeFocusListener(iconListener);
			textAreas[i].button.removeKeyListener(iconListener);
			textAreas[i].button.removeMouseListener(iconListener);
			textAreas[i].button.destroy();
			
			textAreas[i].lineNumber.removeMouseListener(dndListener);
			textAreas[i].lineNumber.removeFocusListener(iconListener);
			textAreas[i].lineNumber.removeKeyListener(iconListener);
			textAreas[i].lineNumber.removeMouseListener(iconListener);
			textAreas[i].lineNumber.destroy();
			
			textAreas[i].iComment.removeMouseListener(indicatorMouseListener);
			textAreas[i].iComment.destroy();
			
			textAreas[i].iEditable.removeMouseListener(indicatorMouseListener);
			textAreas[i].iEditable.destroy();
			
			textAreas[i].iMoveable.removeMouseListener(indicatorMouseListener);
			textAreas[i].iMoveable.destroy();
			
			textAreas[i].removeMouseListener(dndListener);
			textAreas[i].removeFocusListener(textListener);
			textAreas[i].removeKeyListener(textListener);
			textAreas[i].removeMouseListener(textListener);
			textAreas[i].destroy();
			
			textAreas[i] = null;
		}
		textAreas = null;
		
		// Destroy the hidden cell
		panel.remove(hiddenCell);
		panel.remove(hiddenCell.button);
		
		hiddenCell.button.removeFocusListener(iconListener);
		hiddenCell.button.removeKeyListener(iconListener);
		hiddenCell.button.removeMouseListener(iconListener);
		hiddenCell.button.destroy();
		
		hiddenCell.lineNumber.removeKeyListener(iconListener);
		hiddenCell.lineNumber.removeMouseListener(iconListener);
		hiddenCell.lineNumber.destroy();
		
		hiddenCell.iComment.destroy();
		
		hiddenCell.iEditable.destroy();
		
		hiddenCell.iMoveable.destroy();
		
		hiddenCell.removeFocusListener(textListener);
		hiddenCell.removeKeyListener(textListener);
		hiddenCell.removeMouseListener(textListener);
		hiddenCell.destroy();
		hiddenCell = null;
		
		// Destroy Panel
		panel.remove(scrollBar);
		scrollBar.removeAdjustmentListener(this);
		scrollBar = null;
		panel = null;
		
		
		// Destroy Listeners
		dndListener.destroy();
		dndListener = null;
		
		textListener.destroy();
		textListener = null;
		
		iconListener.destroy();
		iconListener = null;
		
		nodeToDrawFrom = null;
	}
	
	// Accessors
	public OutlinerCellRendererImpl getHiddenCell() {
		return hiddenCell;
	}
	
	public void setHiddenCell(OutlinerCellRendererImpl hiddenCell) {
		this.hiddenCell = hiddenCell;
	}
	
	public void setNodeToDrawFrom(Node nodeToDrawFrom, int ioNodeToDrawFrom) {
		this.nodeToDrawFrom = nodeToDrawFrom;
		this.ioNodeToDrawFrom = ioNodeToDrawFrom;
	}
	
	public Node getNodeToDrawFrom() {
		return this.nodeToDrawFrom;
	}
	
	public int getIndexOfNodeToDrawFrom() {
		return ioNodeToDrawFrom;
	}
	
	public void updateNodeToDrawFrom() {
		ioNodeToDrawFrom = panel.doc.tree.getVisibleNodes().indexOf(nodeToDrawFrom);
	}
	
	
	// Main Drawing Methods
	public void redraw() {
		draw();
		setFocus(panel.doc.tree.getEditingNode(), panel.doc.tree.getComponentFocus());
	}
	
	public void draw(Node nodeThatMustBeVis, int focusElement) {
		draw(nodeThatMustBeVis, panel.doc.tree.getVisibleNodes().indexOf(nodeThatMustBeVis), focusElement);
	}
	
	public void draw(Node nodeThatMustBeVis, int ioNodeThatMustBeVis, int focusElement) {
		if (ioNodeThatMustBeVis <= ioFirstVisNode) {
			drawingDirection = DOWN;
			setNodeToDrawFrom(nodeThatMustBeVis, ioNodeThatMustBeVis);
		} else if (ioNodeThatMustBeVis >= ioLastVisNode) {
			drawingDirection = UP;
			setNodeToDrawFrom(nodeThatMustBeVis, ioNodeThatMustBeVis);
		}
		
		draw();
		setFocus(nodeThatMustBeVis,focusElement);
	}
		
	public void draw() {
		//System.out.println("Draw Called: " + panel.getParent().getWidth());
		numNodesDrawn = 0;
		
		// Precompute some values
		OutlinerCellRendererImpl.pApplyFontStyleForComments = panel.doc.settings.getApplyFontStyleForComments().cur;
		OutlinerCellRendererImpl.pApplyFontStyleForEditability = panel.doc.settings.getApplyFontStyleForEditability().cur;
		OutlinerCellRendererImpl.pApplyFontStyleForMoveability = panel.doc.settings.getApplyFontStyleForMoveability().cur;
		
		OutlinerCellRendererImpl.textAreaWidth = 
			panel.getParent().getWidth()
			 - OutlinerCellRendererImpl.lineNumberOffset 
			 - OutlineLineNumber.LINE_NUMBER_WIDTH 
			 - OutlineButton.BUTTON_WIDTH
			 - Preferences.getPreferenceInt(Preferences.RIGHT_MARGIN).cur 
			 - scrollBar.getWidth();
		
		
		// Hide all the nodes from the previous draw
		for (int i = 0; i < CACHE_SIZE; i++) {
			if (textAreas[i].node != null) {
				textAreas[i].node.setVisible(false);
			}
		}
		
		startPoint.x = OutlinerCellRendererImpl.lineNumberOffset + OutlineLineNumber.LINE_NUMBER_WIDTH;
		
		switch (drawingDirection) {
			case DOWN:
				drawDown();
				break;
			default:
				drawUp();
		}
		
		// Draw the hidden component so that things work when we scroll away from the editing node.
		startPoint.x = this.left;
		startPoint.y = this.bottom + 16;
		getHiddenCell().drawDown(startPoint, panel.doc.tree.getEditingNode());
		
		// Update the scrollbar
		drawBlock = true;
		scrollBar.setValues(ioFirstVisNode, numNodesDrawn, 0, panel.doc.tree.getVisibleNodes().size());
		drawBlock = false;
		
		return;
	}
	
	private void drawDown() {
		// Now Draw as many nodes as neccessary.
		startPoint.y = Preferences.getPreferenceInt(Preferences.TOP_MARGIN).cur;
		
		Node node = getNodeToDrawFrom();
		if (node == null) {
			return;
		}
		
		JoeNodeList visibleNodes = panel.doc.tree.getVisibleNodes();
		int visibleNodesSize = visibleNodes.size();
		
		int nodeIndex = ioNodeToDrawFrom;
		ioFirstVisNode = ioNodeToDrawFrom;
		
		// Pre-compute some values
		int effectiveBottom = bottom - Preferences.getPreferenceInt(Preferences.BOTTOM_MARGIN).cur;
		
		if (OutlinerCellRendererImpl.pShowLineNumbers) {
			// Increment the LineCountKey
			panel.doc.tree.incrementLineCountKey();
			
			// Prep the line numbers since this will improve performance. Don't need to do this for UP since the order preps itself.
			int index = nodeIndex + CACHE_SIZE;
			if (index >= visibleNodesSize) {
				visibleNodes.get(visibleNodesSize - 1).getLineNumber(panel.doc.tree.getLineCountKey());
			} else {
				visibleNodes.get(index).getLineNumber(panel.doc.tree.getLineCountKey());
			}
		}
		
		OutlinerCellRendererImpl renderer;
		while (true) {
			renderer = textAreas[numNodesDrawn];
			renderer.drawDown(startPoint, node);
			renderer.setVisible(true);
			renderer.node.setVisible(true);
			numNodesDrawn++;
			
			// Make sure we don't draw past the bottom. And don't count nodes that are partially drawn.
			if (startPoint.y > effectiveBottom) {
				renderer.node.setVisible(false);
				partialCellDrawn = true;
				break;
			}
			
			// Make sure we dont' try to draw more nodes than the cache size
			if (numNodesDrawn == CACHE_SIZE) {
				break;
			}
			
			// Get the Next Node to Draw
			nodeIndex++;
			if (nodeIndex == visibleNodesSize) {
				break;
			}
			node = visibleNodes.get(nodeIndex);
		}
		
		// Hide any drawing elements that were not used.
		for (int i = numNodesDrawn; i < CACHE_SIZE; i++) {
			textAreas[i].setVisible(false);
		}
		
		// Record Indexes and get things ready for the scrollbar
		if (partialCellDrawn) {
			numNodesDrawn--;
			partialCellDrawn = false;
		}
		
		ioLastVisNode = ioFirstVisNode + (numNodesDrawn - 1);
		if (ioLastVisNode >= visibleNodesSize) {
			ioLastVisNode = visibleNodesSize - 1;
		}
	}
	
	private void drawUp() {
		// Now Draw as many nodes as neccessary.
		startPoint.y = this.bottom - Preferences.getPreferenceInt(Preferences.BOTTOM_MARGIN).cur;
		
		Node node = getNodeToDrawFrom();
		if (node == null) {return;}
		
		JoeNodeList visibleNodes = panel.doc.tree.getVisibleNodes();
		int visibleNodesSize = visibleNodes.size();
		
		int nodeIndex = ioNodeToDrawFrom;
		ioLastVisNode = ioNodeToDrawFrom;
		
		// Pre-compute some values
		int effectiveTop = top + Preferences.getPreferenceInt(Preferences.TOP_MARGIN).cur;
		
		// Increment the LineCountKey
		node.getTree().incrementLineCountKey();
		
		Node newNodeToDrawFrom = null;
		int ioNewNodeToDrawFrom = nodeIndex;
		int offset = 0;
		
		OutlinerCellRendererImpl renderer;
		while (true) {
			renderer = textAreas[numNodesDrawn];
			renderer.drawUp(startPoint, node);
			renderer.setVisible(true);
			renderer.node.setVisible(true);
			numNodesDrawn++;
			
			// Make sure we don't draw past the top. And don't count nodes that are partially drawn.
			if (startPoint.y < effectiveTop) {
				renderer.node.setVisible(false);
				partialCellDrawn = true;
				break;
			}
			
			newNodeToDrawFrom = node;
			ioNewNodeToDrawFrom = nodeIndex;
			offset = startPoint.y;
			
			// Make sure we dont' try to draw more nodes than the cache size
			if (numNodesDrawn == CACHE_SIZE) {
				break;
			}
			
			// Get the Next Node to Draw
			nodeIndex--;
			if (nodeIndex == -1) {
				break;
			}
			node = visibleNodes.get(nodeIndex);
		}
		
		// Hide any drawing elements that were not used.
		for (int i = numNodesDrawn; i < CACHE_SIZE; i++) {
			textAreas[i].setVisible(false);
		}
		
		// Record some values for the extra draw down.
		int ioExtraNodeToDrawFrom = ioNodeToDrawFrom + 1;
		
		// Shift up so we are always drawing from the top
		setNodeToDrawFrom(newNodeToDrawFrom, ioNewNodeToDrawFrom);
		
		int shiftAmount = effectiveTop - offset;
		for (int i = 0; i < numNodesDrawn; i++) {
			textAreas[i].verticalShift(shiftAmount);
		}
		
		// Record Indexes and get things ready for the scrollbar
		if (partialCellDrawn) {
			numNodesDrawn--;
			partialCellDrawn = false;
		}
		
		ioFirstVisNode = ioLastVisNode - (numNodesDrawn - 1);
		if (ioLastVisNode >= visibleNodesSize) {
			ioLastVisNode = visibleNodesSize - 1;
		}
		
		drawingDirection = DOWN;
		
		// Do the extraDrawDown
		if (numNodesDrawn < CACHE_SIZE && ioExtraNodeToDrawFrom < visibleNodesSize) {
			drawDownExtraNodes(ioExtraNodeToDrawFrom);
		}
	}
	
	private void drawDownExtraNodes(int nodeIndex) {
		JoeNodeList visibleNodes = panel.doc.tree.getVisibleNodes();
		int visibleNodesSize = visibleNodes.size();
		
		Node node = visibleNodes.get(nodeIndex);
		
		startPoint.x = OutlinerCellRendererImpl.lineNumberOffset + OutlineLineNumber.LINE_NUMBER_WIDTH;
		startPoint.y = textAreas[0].getLocation().y + textAreas[0].getBestHeight() + Preferences.getPreferenceInt(Preferences.VERTICAL_SPACING).cur;
		
		// Pre-compute some values
		int effectiveBottom = bottom - Preferences.getPreferenceInt(Preferences.BOTTOM_MARGIN).cur;
		
		OutlinerCellRendererImpl renderer;
		while (true) {
			renderer = textAreas[numNodesDrawn];
			renderer.drawDown(startPoint, node);
			renderer.setVisible(true);
			renderer.node.setVisible(true);
			numNodesDrawn++;
			
			// Make sure we don't draw past the bottom. And don't count nodes that are partially drawn.
			if (startPoint.y > effectiveBottom) {
				renderer.node.setVisible(false);
				partialCellDrawn = true;
				break;
			}
			
			// Make sure we dont' try to draw more nodes than the cache size
			if (numNodesDrawn == CACHE_SIZE) {
				break;
			}
			
			// Get the Next Node to Draw
			nodeIndex++;
			if (nodeIndex == visibleNodesSize) {
				break;
			}
			node = visibleNodes.get(nodeIndex);
		}
		
		// Record Indexes and get things ready for the scrollbar
		if (partialCellDrawn) {
			numNodesDrawn--;
			partialCellDrawn = false;
		}
		
		ioLastVisNode = ioFirstVisNode + (numNodesDrawn - 1);
		if (ioLastVisNode >= visibleNodesSize) {
			ioLastVisNode = visibleNodesSize - 1;
		}
	}
	
	
	// Get UI Components
	public OutlinerCellRendererImpl getUIComponent(Node node) {
		if (node.isVisible()) {
			for (int i = 0; i < CACHE_SIZE; i++) {
				if (textAreas[i].node == node) {
					return textAreas[i];
				}			
			}
		}
		
		if (getHiddenCell().node == node) {
			return getHiddenCell();
		}
		
		return null;
	}
	
	// Focus Methods
	public void setFocus(Node node, int type) {
		OutlinerCellRendererImpl renderer = getUIComponent(node);
		
		if (renderer == null) {
			System.out.println("Focus Exception: No renderer found for node.");
			return;
		}
		
		switch (type) {
			case TEXT:
				renderer.requestFocus();
				renderer.getCaret().setVisible(true); // This fixes a problem where the caret becomes invisible when it shouldn't be.
				
				// Restore the Caret Position and text selection.
				try {
					renderer.setCaretPosition(panel.doc.tree.getCursorMarkPosition());
					renderer.moveCaretPosition(panel.doc.tree.getCursorPosition());
				} catch (Exception e) {
					System.out.println("Focus Exception: " + e);
				}
				break;
			case ICON:
				renderer.button.requestFocus();
				break;
			default:
				System.out.println("Focus Error: Not ICON or TEXT");
		}
	}
	
	
	// AdjustmentListener Interface
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (drawBlock) {
			return;
		}
		
		// Explicit call to draw and focus, so that we can scroll away from our current component focus.
		int value = e.getValue();
		JoeNodeList list = panel.doc.tree.getVisibleNodes();
		
		if (value < list.size()) {
			setNodeToDrawFrom(list.get(value), value);
			drawingDirection = DOWN;
			redraw();
		}
	}
	
	
	// LayoutManager Interface
	public void layoutContainer(Container container) {
		Insets insets = panel.getInsets();
		this.top = insets.top;
		this.bottom = panel.getSize().height - insets.bottom;
		this.left = insets.left;
		this.right = panel.getSize().width - insets.right;
		
		// Update the scrollbar size
		Dimension d = scrollBar.getPreferredSize();
		scrollBar.setBounds(right - d.width, top, d.width, bottom - top);
	}
	
	public Dimension minimumLayoutSize(Container parent) {
		return MINIMUM_DIMENSION;
	}
	
	public Dimension preferredLayoutSize(Container parent) {
		// Need to get parent because we've got the DummyJScrollPane 
		// between us and our parent window which has the size we want.
		return parent.getParent().getSize();
	}
	
	public void addLayoutComponent(String name, Component comp) {}
	
	public void removeLayoutComponent(Component comp) {}
}
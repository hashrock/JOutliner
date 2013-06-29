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

import com.organic.maynard.outliner.util.undo.*;

import java.awt.*;
import java.awt.event.*;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.22 $, $Date: 2002/12/11 05:57:35 $
 */
 
public class AttributesPanel extends AbstractAttributesPanel {
	
	// Instance Fields
	protected OutlinerDocument doc = null;
	
	
	// The Constructor
	public AttributesPanel(OutlinerDocument doc) {
		super();
		this.doc = doc;
	}
	
	
	// Data Display
	public void update() {
		if (doc.isShowingAttributes()) {
			AttributeContainer node = doc.tree.getEditingNode();
			
			model.keys.clear();
			model.values.clear();
			model.readOnly.clear();
			clearSelection();
			
			Iterator it = node.getAttributeKeys();
			if (it != null) {
				while (it.hasNext()) {
					String key = (String) it.next();
					Object value = node.getAttribute(key);
					boolean readOnly = node.isReadOnly(key);
					model.keys.add(key);
					model.values.add(value);
					model.readOnly.add(new Boolean(readOnly));
				}
			}
			
			if (isEditing()) {
				getCellEditor().cancelCellEditing();
			}
			
			model.fireTableDataChanged();
		}
	}
	
	// Data Modification
	public void newAttribute(String key, Object value, boolean isReadOnly, AttributeTableModel model) {
 		model.keys.add(key);
		model.values.add(value);
		model.readOnly.add(new Boolean(isReadOnly));
		
	   	Node node = doc.tree.getEditingNode();
		node.setAttribute(key, value, isReadOnly);
		
		// undo
		CompoundUndoable undoable = new CompoundUndoablePropertyChange(doc.tree);
		undoable.setName("New Node Attribute");
		Undoable primitive = new PrimitiveUndoableAttributeChange(node, null, null, false, key, value, isReadOnly);
		undoable.addPrimitive(primitive);
		doc.getUndoQueue().add(undoable);
		
		model.fireTableDataChanged();
	}
	
	// Delete Attribute
	public void deleteAttribute(int row, AttributeTableModel model) {
		
		Node node = doc.tree.getEditingNode();
		String key = (String) model.keys.get(row);
		
		Object oldValue = node.getAttribute(key);
		boolean oldReadOnly = node.isReadOnly(key);
		
		node.removeAttribute(key);
		model.keys.remove(row);
		model.values.remove(row);
		model.readOnly.remove(row);
		
		// undo
		CompoundUndoable undoable = new CompoundUndoablePropertyChange(doc.tree);
		undoable.setName("Delete Node Attribute");
		Undoable primitive = new PrimitiveUndoableAttributeChange(node, key, oldValue, oldReadOnly, null, null, false);
		undoable.addPrimitive(primitive);
		doc.getUndoQueue().add(undoable);
		
		model.fireTableRowsDeleted(row, row);
		
		update(); // [md] Added so that modification date updates.
	}
	
	// Toggle Editability
	public void toggleEditability(int row, AttributeTableModel model) {
		Node node = doc.tree.getEditingNode();
		String key = (String) model.keys.get(row);
		
		Object oldAndNewValue = node.getAttribute(key);
		
		boolean oldReadOnly = node.isReadOnly(key);
		boolean readOnly = !oldReadOnly;
		
		node.setReadOnly(key, readOnly);
		model.readOnly.set(row, new Boolean(readOnly));
		
		// undo
		CompoundUndoable undoable = new CompoundUndoablePropertyChange(doc.tree);
		undoable.setName("Toggle Node Attribute Editability");
		Undoable primitive = new PrimitiveUndoableAttributeChange(node, key, oldAndNewValue, oldReadOnly, key, oldAndNewValue, readOnly);
		undoable.addPrimitive(primitive);
		doc.getUndoQueue().add(undoable);
		
		update(); // [md] Added so that modification date updates.
	}
	
	// Set Value
	public void setValueAt(Object value, int row, AttributeTableModel model) {
		Node node = doc.tree.getEditingNode();
		String key = (String) model.keys.get(row);
		
		boolean readOnly = node.isReadOnly(key);
		
		Object oldValue = node.getAttribute(key);
		node.setAttribute(key, value);
		model.values.set(row, value);
		
		if (oldValue.equals(value)) {
			return;
		}
		
		// undo
		CompoundUndoable undoable = new CompoundUndoablePropertyChange(doc.tree);
		undoable.setName("Edit Node Attribute");
		Undoable primitive = new PrimitiveUndoableAttributeChange(node, key, oldValue, readOnly, key, value, readOnly);
		undoable.addPrimitive(primitive);
		doc.getUndoQueue().add(undoable);
		
		update(); // [md] Added so that modification date updates.
	}
	
	// Misc
	protected boolean isCellEditable() {
		Node node = doc.tree.getEditingNode();
		
		if (!node.isEditable()) {
			return false;
		}
		
    		return true;
	}
	
	protected boolean isCellEditable(int row) {
		if (!isCellEditable()) {
			return false;
		}
		
		AttributeContainer node = doc.tree.getEditingNode();
		String key = (String) model.keys.get(row);
		
		if (node.isReadOnly(key)) {
			return false;
		}
		
		return true;
	}
}
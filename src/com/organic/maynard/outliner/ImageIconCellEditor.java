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
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * Most of the code for this class, ButtonCellEditor, 
 * came from http://www2.gol.com/users/tame/swing/examples/JTableExamples2.html.
 */
 
public class ImageIconCellEditor extends DefaultCellEditor {
	private JButton button;
	private JButton rendererButton;
	
	private boolean   isPushed;
	
	protected int row = -1;
	protected int col = -1;
	
	public ImageIconCellEditor(JCheckBox checkBox, ImageIcon onIcon, ImageIcon offIcon) {
		super(checkBox);
		button = new JButton(offIcon);
		button.setOpaque(true);
		button.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			}
		);

		rendererButton = new JButton(onIcon);
		rendererButton.setOpaque(true);
		rendererButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fireEditingStopped();
				}
			}
		);

	}
	
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		isPushed = true;
		this.row = row;
		this.col = column;

		ImageIcon isReadOnly = (ImageIcon) value;
		if (isReadOnly == OutlineEditableIndicator.ICON_IS_NOT_PROPERTY) {
			return button;
		} else {
			return rendererButton;
		}
	}

	public Object getCellEditorValue() {
		if (isPushed)  {
			doEditing();
		}
		isPushed = false;
		return new String("x");
	}

	protected void doEditing() {}
	
	public boolean stopCellEditing() {
		isPushed = false;
		return super.stopCellEditing();
	}
	
	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
}
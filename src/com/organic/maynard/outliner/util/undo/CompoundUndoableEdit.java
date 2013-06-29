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
 
package com.organic.maynard.outliner.util.undo;

import com.organic.maynard.outliner.*;

/**
 * Holds a series of simple text changes. Currently used by the
 * find/replace code when doing text replacements to open documents.
 * This undoable should only be filled with PrimitiveUndoableEdit
 * objects.
 *
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2002/08/27 09:42:13 $
 */
 
public class CompoundUndoableEdit extends AbstractCompoundUndoable {

	
	// Constants
	private static final String DEFAULT_NAME = "Multiple Text Edits";
	
	
	// Instance Fields
	private JoeTree tree = null;
	
	// The Constructors
	public CompoundUndoableEdit(JoeTree tree) {
		this(true, tree);
	}

	public CompoundUndoableEdit(boolean isUpdatingGui, JoeTree tree) {
		super(isUpdatingGui);
		this.tree = tree;
	}


	// Destructible Interface
	public void destroy() {
		super.destroy();
		tree = null;
	}


	// Undoable Interface
	public String getName() {
		String name = super.getName();
		if (name == null) {
			return DEFAULT_NAME;
		} else {
			return name;
		}
	}

	public void undo() {
		for (int i = primitives.size() - 1; i >= 0; i--) {
			primitives.get(i).undo();
		}
		tree.getDocument().panel.layout.redraw();
	}
	
	public void redo() {
		for (int i = 0, limit = primitives.size(); i < limit; i++) {
			primitives.get(i).redo();
		}
		tree.getDocument().panel.layout.redraw();
	}
}
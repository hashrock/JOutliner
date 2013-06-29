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

import java.util.*;
import java.awt.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.1 $, $Date: 2002/12/20 01:01:40 $
 */
 
public class UndoableDocumentAttributeChange extends AbstractUndoable implements Undoable {
	
	private JoeTree tree = null;
	private String oldKey = null;
	private Object oldValue = null;
	private String newKey = null;
	private Object newValue = null;
	private boolean oldReadOnly = false;
	private boolean newReadOnly = false;
	
	
	// The Constructors
	public UndoableDocumentAttributeChange(JoeTree tree, String oldKey, Object oldValue, boolean oldReadOnly, String newKey, Object newValue, boolean newReadOnly) {
		this.tree = tree;
		this.oldKey = oldKey;
		this.oldValue = oldValue;
		this.oldReadOnly = oldReadOnly;
		this.newKey = newKey;
		this.newValue = newValue;
		this.newReadOnly = newReadOnly;
	}
	
	public void destroy() {
		tree = null;
		oldValue = null;
		newValue = null;
	}
	
	
	// Undoable Interface
	public void undo() {
		if (oldKey == null) {
			if (newKey == null) {
				// Do Nothing, since this should never happen.
			} else {
				tree.removeAttribute(newKey);
			}
		} else {
			if (newKey == null) {
				tree.setAttribute(oldKey, oldValue, oldReadOnly);
			} else {
				if (!oldKey.equals(newKey)) {
					tree.removeAttribute(newKey);
				}
				tree.setAttribute(oldKey, oldValue, oldReadOnly);
			}
		}
		
		Outliner.documentAttributes.configureAndShow(tree);
	}
	
	public void redo() {
		if (oldKey == null) {
			if (newKey == null) {
				// Do Nothing, since this should never happen.
			} else {
				tree.setAttribute(newKey, newValue, newReadOnly);
			}
		} else {
			if (newKey == null) {
				tree.removeAttribute(oldKey);
			} else {
				if (!oldKey.equals(newKey)) {
					tree.removeAttribute(oldKey);
				}
				tree.setAttribute(newKey, newValue, newReadOnly);
			}
		}
		
		Outliner.documentAttributes.configureAndShow(tree);
	}
}
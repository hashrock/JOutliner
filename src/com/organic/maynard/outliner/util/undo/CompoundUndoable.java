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
 * A CompoundUndoable is an object that may contain multiple undoable
 * actions that should all be undone/redone as a single unit. By calling
 * the undo and redo methods on the CompoundUndoable, all the undoables
 * contained by the CompoundUndoable will be undone/redone.
 * 
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2002/08/27 09:42:13 $
 */
 
public interface CompoundUndoable extends Undoable {

	/**
	 * Returns the number of primitive Undoables in this CompoundUndoable.
	 */
	public int getPrimitiveCount();

	/**
	 * Adds an undoable to this CompoundUndoable. Undoables added to a
	 * ComoundUndoable are referred to as PrimitiveUndoables, although no
	 * marker interface exists to indicate this. Any Undoable may serve
	 * as a primitive, although special PrimitiveUndoable classes that are
	 * as lightweight as possible work best.
	 *
	 * @param primitive the <code>Undoable</code> to be added to this
	 *                  <code>CompoundUndoable</code>. 
	 */		
	public void addPrimitive(Undoable primitive);

	/**
	 * Indicates if this <code>CompoundUndoable</code> currently contains
	 * any one or more primitives. 
	 *
	 * @return        <code>true</code> indicates this <code>CompoundUndoable</code> 
	 *                contains at least one primitive, <code>false</code> indicates
	 *                this <code>CompoundUndoable</code> contains no primitives.
	 */		
	public boolean isEmpty();

	/**
	 * Indicates that this <code>CompoundUndoable</code> may make modifications/updates to
	 * the Graphic User Interface. This setting us used to improve performance, when you are 
	 * including a bunch of <code>CompoundUndoables</code> within a single <code>CompoundUndoable</code>.
	 * The performance gain is achieved, by only having one (typically the last) of the
	 * <code>CompoundUndoables</code> update the GUI, rather than all of them, since the GUI update is
	 * usually expensive from a performance standpoint.
	 *
	 * @return        <code>true</code> indicates this <code>CompoundUndoable</code> 
	 *                may update the GUI, <code>false</code> indicates
	 *                this <code>CompoundUndoable</code> should not update the GUI.
	 */		
	public boolean isUpdatingGui();

	/**
	 * Sets the state of the GUI update property.
	 *
	 * @param isUpdatingGui a boolean indicating the new GUI update state. 
	 */		
	public void setUpdatingGui(boolean isUpdatingGui);
}
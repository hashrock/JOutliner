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
 
package com.organic.maynard.outliner.guitree;

import com.organic.maynard.outliner.*;
import org.xml.sax.Attributes;

/**
 * A component that can be loaded from a gui_tree.xml file.
 *
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2004/02/02 10:17:41 $
 */

public interface GUITreeComponent {

	/**
	 * Gets this GUITreeComponent's id.
	 *
	 * @return the unique id of this GUITreeComponent.
	 */
	public String getGUITreeComponentID();

	/**
	 * Sets this GUITreeComponent's id.
	 *
	 * @param id a unique identifier for this GUITreeComponent.
	 */
	public void setGUITreeComponentID(String id);

	/**
	 * Called by the GuiTreeLoader when it handles the start tag for
	 * this component in the gui_tree.xml file.
	 *
	 * @param atts the XML attributes available to this component when
	 *             it was parsed in the gui_tree.xml file.
	 */
	public void startSetup(Attributes atts);

	/**
	 * Called by the GuiTreeLoader when it handles the end tag for
	 * this component in the gui_tree.xml file.
	 *
	 * @param atts the XML attributes available to this component when
	 *             it was parsed in the gui_tree.xml file.
	 */
	public void endSetup(Attributes atts);
}

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

package com.organic.maynard.outliner.menus;

import com.organic.maynard.outliner.*;
import com.organic.maynard.outliner.guitree.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import org.xml.sax.*;

/**
 * @author $Author: maynardd $
 * @version $Revision: 1.2 $, $Date: 2004/02/02 10:17:41 $
 */

public abstract class AbstractOutlinerMenuItem extends JMenuItem implements GUITreeComponent, JoeXMLConstants {

	// Constants
	/**
	 * Used in the gui_tree.xml files to designate that a generic platform
	 * specific shortcut key is to be used.
	 */
	private static final String SHORTCUT = "shortcut";

	private static final String SHIFT = "shift";
	private static final String CTRL = "control";
	private static final String ALT = "alt";
	private static final String META = "meta";

	private static final String DELETE = "delete";
	private static final String TAB = "tab";
	private static final String UP = "up";
	private static final String DOWN = "down";
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	private static final String PAGE_UP = "page_up";
	private static final String PAGE_DOWN = "page_down";
	private static final String F1 = "f1";
	private static final String F2 = "f2";
	private static final String F3 = "f3";
	private static final String F4 = "f4";
	private static final String F5 = "f5";
	private static final String F6 = "f6";
	private static final String F7 = "f7";
	private static final String F8 = "f8";
	private static final String F9 = "f9";
	private static final String F10 = "f10";
	private static final String F11 = "f11";
	private static final String F12 = "f12";
	private static final String SPACE = "space";

	// Constructors
	public AbstractOutlinerMenuItem() {
	}

	// GUITreeComponent interface
	private String id = null;

	public String getGUITreeComponentID() {
		return this.id;
	}

	public void setGUITreeComponentID(String id) {
		this.id = id;
	}

	public void startSetup(Attributes atts) {
		// Set the title of the menuItem
		setText(atts.getValue(A_TEXT));

		// Set Mnemonic
		String mnemonic = atts.getValue(AbstractOutlinerMenu.A_MNEMONIC);
		if (mnemonic != null && mnemonic.length() > 0) {
			mnemonic = mnemonic.trim().toUpperCase();
			int mnemonicInt = mnemonic.charAt(0);
			setMnemonic(mnemonicInt);
		}

		// Set KeyBinding
		int mask = 0;

		String keyBindingModifiers = atts.getValue(A_KEY_BINDING_MODIFIERS);
		if ((keyBindingModifiers != null) && (keyBindingModifiers.length() > 0)) {
			if (keyBindingModifiers.indexOf(SHORTCUT) != -1) {
				mask += Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			}
			if (keyBindingModifiers.indexOf(CTRL) != -1) {
				mask += Event.CTRL_MASK;
			}
			if (keyBindingModifiers.indexOf(SHIFT) != -1) {
				mask += Event.SHIFT_MASK;
			}
			if (keyBindingModifiers.indexOf(ALT) != -1) {
				mask += Event.ALT_MASK;
			}
			if (keyBindingModifiers.indexOf(META) != -1) {
				mask += Event.META_MASK;
			}
		}

		String keyBinding = atts.getValue(A_KEY_BINDING);
		if (keyBinding != null) {
			if (keyBinding.length() == 1) {
				setAccelerator(KeyStroke.getKeyStroke(keyBinding.charAt(0), mask, false));
			} else if (keyBinding.length() == 0) {
				// Do nothing since no keybinding has been assigned.
			} else {
				if (keyBinding.equals(TAB)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, mask, false));
				} else if (keyBinding.equals(UP)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, mask, false));
				} else if (keyBinding.equals(DOWN)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, mask, false));
				} else if (keyBinding.equals(LEFT)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, mask, false));
				} else if (keyBinding.equals(RIGHT)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, mask, false));
				} else if (keyBinding.equals(PAGE_UP)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, mask, false));
				} else if (keyBinding.equals(PAGE_DOWN)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, mask, false));
				} else if (keyBinding.equals(DELETE)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, mask, false));
				} else if (keyBinding.equals(F1)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, mask, false));
				} else if (keyBinding.equals(F2)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, mask, false));
				} else if (keyBinding.equals(F3)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, mask, false));
				} else if (keyBinding.equals(F4)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, mask, false));
				} else if (keyBinding.equals(F5)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, mask, false));
				} else if (keyBinding.equals(F6)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F6, mask, false));
				} else if (keyBinding.equals(F7)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, mask, false));
				} else if (keyBinding.equals(F8)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F8, mask, false));
				} else if (keyBinding.equals(F9)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9, mask, false));
				} else if (keyBinding.equals(F10)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F10, mask, false));
				} else if (keyBinding.equals(F11)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, mask, false));
				} else if (keyBinding.equals(F12)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, mask, false));
				} else if (keyBinding.equals(SPACE)) {
					setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, mask, false));
				}
			}
		}

		// Add this menuItem to the parent menu.
		((JMenu) GUITreeLoader.elementStack.get(GUITreeLoader.elementStack.size() - 2)).add(this);
	}

	public void endSetup(Attributes atts) {
	}
}
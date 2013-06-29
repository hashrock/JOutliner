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
 
package com.organic.maynard.outliner.scripting;

import com.organic.maynard.outliner.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import org.xml.sax.*;
import bsh.*;
import bsh.util.*;

public class BSHConsole extends AbstractGUITreeJDialog {
	
	// Constants
	private static final int INITIAL_WIDTH = 350;
	private static final int INITIAL_HEIGHT = 300;
	private static final int MINIMUM_WIDTH = 350;
	private static final int MINIMUM_HEIGHT = 300;
	
	
	// Instance Fields
	/** Indicates if this component has been initialized yet. */
	private boolean initialized = false;
	
	
	// Constructors
	/**
	 * Creates a new BeanShell Console within a dialog box.
	 */
	public BSHConsole() {
		super(
			false, 
			false, 
			false, 
			INITIAL_WIDTH, 
			INITIAL_HEIGHT, 
			MINIMUM_WIDTH, 
			MINIMUM_HEIGHT
		);
	}
	
	
	// Methods
	/**
	 * Does all necessary setup for this object before it may be used.
	 */
	private void initialize() {
		// Create BSH Console
		JConsole console = new JConsole();
		Thread thread = new Thread(new Interpreter(console));
		thread.start();
		
		// Add the JConsole to this dialog's content pane.
		getContentPane().add(console, BorderLayout.CENTER);
		
		// Change flag to indicate that initialization is complete.
		initialized = true;
	}
	
	
	/**
	 * Shows the dialog. Uses lazy instantiation to improve overall application
	 * startup times since this is a non-essential part of JOE.
	 */
	public void show() {
		// Lazy Instantiation
		if (!initialized) {
			initialize();
		}
		
		super.show();
	}
}
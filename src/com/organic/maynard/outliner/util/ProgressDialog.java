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

package com.organic.maynard.outliner.util;

import com.organic.maynard.outliner.*;

import javax.swing.border.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.6 $, $Date: 2002/07/16 21:25:29 $
 */

public class ProgressDialog extends AbstractOutlinerJDialog implements com.organic.maynard.swing.ProgressMonitor, ActionListener {

	// Constants
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 100;
 	private static final int INITIAL_WIDTH = 400;
	private static final int INITIAL_HEIGHT = 100;

	private static final String CANCEL = "Cancel";

	private JProgressBar bar = new JProgressBar();
	private JButton cancel = new JButton(CANCEL);
	private JLabel note = new JLabel("");

	private boolean isCanceled = false;


	// The Constructor
	public ProgressDialog() {
		super(false, false, true, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);

		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					setCanceled(true);
					close(); // Explicit close since the GUI might be stuck if an exception occured in the thread responsible for handling the cancel.
				}
			}
		);

		cancel.addActionListener(this);

		setMinimum(0);
		setMaximum(1);

		// Setup GUI
		JPanel panel = new JPanel();

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		panel.setLayout(gb);

			// Setup GridBagLayout
			c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(0,2,0,2);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			gb.setConstraints(note, c);
			panel.add(note);

			c.gridwidth = GridBagConstraints.RELATIVE;
			c.weightx = 0.0;
			Dimension dim = new Dimension(150,25);
			bar.setStringPainted(true);
			bar.setMinimumSize(dim);
			bar.setMaximumSize(dim);
			bar.setPreferredSize(dim);
			gb.setConstraints(bar, c);
			panel.add(bar);

			c.gridwidth = GridBagConstraints.REMAINDER;
			c.weightx = 1.0;
			gb.setConstraints(cancel, c);
			panel.add(cancel);

	    getContentPane().add(panel);
	}

	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		// File Menu
		if (e.getActionCommand().equals(CANCEL)) {
			setCanceled(true);
		}
	}

	public void show() {
		setCanceled(false);
		setMaximum(1);
		super.show();
	}

	public void close() {
		hide();
	}

	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public void setNote(String text) {
		note.setText(text);
	}

	private int sleepCounter = 0;
	private static final int SLEEP_LIMIT = 50;
	
	public void setProgress(int i) {
		// Need to sleep since NT4.0 doesn't share so well with threads, and it's likely that this dialog
		// is running in a different thread from the process updating things. Note: when setProgress is 
		// called it is executing in the thread from the process calling the method, not the thread the dialog
		// is in. Basically this gives swing time to updated the GUI.
		try {
			if (sleepCounter == 0) {
				Thread.sleep(10);
			} else if (sleepCounter == SLEEP_LIMIT) {
				sleepCounter = 0;
			} else {
				sleepCounter++;
			}
		} catch (Exception e) {}

		//System.out.println(getMinimum() + ":" + i + ":" + getMaximum());
		if (isVisible()) {
			if (i >= getMaximum()) {
				setCanceled(true);
				close();
			}
		}
		bar.setValue(i);
	}
	public int getProgress() {return bar.getValue();}

	public void setMinimum(int i) {bar.setMinimum(i);}
	public int getMinimum() {return bar.getMinimum();}
	public void setMaximum(int i) {bar.setMaximum(i);}
	public int getMaximum() {return bar.getMaximum();}
}
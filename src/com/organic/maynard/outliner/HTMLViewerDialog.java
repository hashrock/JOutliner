/**
 * Copyright (C) 2003 Maynard Demmon, maynard@organic.com
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

import com.organic.maynard.outliner.guitree.*;
import org.xml.sax.*;
import java.util.ArrayList;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.Document;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;
import java.beans.*;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.3 $, $Date: 2003/02/17 06:26:09 $
 */

public class HTMLViewerDialog extends AbstractGUITreeJDialog implements HyperlinkListener, PropertyChangeListener, ActionListener {
	
	// Constants
	private static final int INITIAL_WIDTH = 500;
	private static final int INITIAL_HEIGHT = 400;
	private static final int MINIMUM_WIDTH = 200;
	private static final int MINIMUM_HEIGHT = 125;
	
	private static String LOCATION = null;
	private static String NEXT = "next_action";
	private static String PREV = "prev_action";
	
	// Instance Fields
	private boolean initialized = false;
	
	private DefaultComboBoxModel history = null;
	private int history_location = -1;
	
	
	// GUI Components
	private JScrollPane jsp = null;
	public JEditorPane viewer = null;
	private JButton nextButton = null;
	private JButton prevButton = null;
	private JLabel location_label = null;
	private JComboBox location = null;
	
	
	// The Constructors
	public HTMLViewerDialog() {
		super(false, false, false, INITIAL_WIDTH, INITIAL_HEIGHT, MINIMUM_WIDTH, MINIMUM_HEIGHT);
		
		Outliner.html_viewer = this;
	}
	
	private void initialize() {
		this.history = new DefaultComboBoxModel();
		
		// Setup North Panel
		JPanel north_panel = new JPanel();
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		north_panel.setLayout(gridbag);
		
		LOCATION = "Location"; // GUITreeLoader.reg.getText("location");
		this.location_label = new JLabel(LOCATION + ":");
		
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(0,4,0,4);
		
		gridbag.setConstraints(this.location_label, c);
        	north_panel.add(this.location_label);
		
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0,0,0,0);
		
		this.location = new JComboBox();
		this.location.setModel(this.history);
		this.location.setActionCommand(LOCATION);
		this.location.setEditable(true);
		this.location.addActionListener(this);
		gridbag.setConstraints(this.location, c);
        	north_panel.add(this.location);
		
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		
		this.prevButton = new MyButton();
		this.prevButton.setMargin(new Insets(5,7,5,9));
		ImageIcon prevImage = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("graphics/prev.gif"));
		this.prevButton.setIcon(prevImage);
		this.prevButton.setToolTipText("Back");
		this.prevButton.setActionCommand(PREV);
		this.prevButton.addActionListener(this);
		
		gridbag.setConstraints(this.prevButton, c);
        	north_panel.add(this.prevButton);
		
		this.nextButton = new MyButton();
		this.nextButton.setMargin(new Insets(5,8,5,8));
		ImageIcon nextImage = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("graphics/next.gif"));
		this.nextButton.setIcon(nextImage);
		this.nextButton.setToolTipText("Forward");
		this.nextButton.setActionCommand(NEXT);
		this.nextButton.addActionListener(this);
		
		gridbag.setConstraints(this.nextButton, c);
        	north_panel.add(this.nextButton);
		
		getContentPane().add(north_panel, BorderLayout.NORTH);
		
		// Setup Center Panel
		this.viewer = new JEditorPane();
		this.viewer.setEditable(false);
		HTMLEditorKit htmlKit = new HTMLEditorKit();
		this.viewer.setEditorKit(htmlKit);
		this.viewer.addHyperlinkListener(this);
		this.viewer.addPropertyChangeListener(this);
		this.jsp = new JScrollPane(this.viewer);
		
		getContentPane().add(this.jsp, BorderLayout.CENTER);
		
		setHistoryLocation(-1);
		
		this.initialized = true;
	}
	
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	public void show() {
		// Lazy Instantiation
		if (!isInitialized()) {
			initialize();
		}
		
		super.show();
	}
	
	public void setHTML(URL url) {
		// Lazy Instantiation
		if (!isInitialized()) {
			initialize();
		}
		
		try {
			this.viewer.setPage(url);
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}
	}
	
	
	// History
	public void trimHistory() {
		while (history.getSize() > history_location + 1) {
			history.removeElementAt(history.getSize() - 1);
		}
	}
	
	public void clearHistory() {
		Object current = history.getElementAt(history_location);
		history.removeAllElements();
		history.addElement(current);
		setHistoryLocation(0);
	}
	
	public URL getCurrentURL() {
		return (URL) history.getElementAt(history_location);
	}
	
	public void addURL(URL url) {
		// Verify were not adding a duplicate of the current url
		if (this.history_location >= 0) {
			URL current_url = ((MyListItemWrapper) history.getElementAt(this.history_location)).url;
			if (url.equals(current_url)) {
				return;
			}
		}
		
		trimHistory();
		if (history.getSize() >= 0) {
			setHistoryLocation(this.history_location + 1);
		}
		MyListItemWrapper item = new MyListItemWrapper(url);
		history.addElement(item);
		history.setSelectedItem(item);
	}
	
	public void setHistoryLocation(int i) {
		this.history_location = i;
		
		if (this.history_location <= 0) {
			this.prevButton.setEnabled(false);
		} else {
			this.prevButton.setEnabled(true);
		}
		//System.out.println(this.history_location + ":" + history.getSize());
		if (this.history_location >= history.getSize() - 1) {
			this.nextButton.setEnabled(false);
		} else {
			this.nextButton.setEnabled(true);
		}
	}
	
	
	// HyperlinkListener Interface
	public void hyperlinkUpdate(HyperlinkEvent e) {
		if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
			URL url = e.getURL();
			try {
				viewer.setPage(url);
				addURL(url);
			} catch (IOException ex) {
				System.out.println("IOException: " + ex.getMessage());
			}
		}
	}
	
	
	// PropertyChangeListener Interface
	public void propertyChange(PropertyChangeEvent e) {
		String name = e.getPropertyName();
		if (name.equals("page")) {
			setTitle(getTitle());
		}
	}
	
	
	// ActionListener Interface
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals(NEXT)) {
			this.location.setSelectedIndex(this.history_location + 1);
		} else if (e.getActionCommand().equals(PREV)) {
			this.location.setSelectedIndex(this.history_location - 1);
		} else if (e.getActionCommand().equals(LOCATION)) {
			Object path = this.location.getSelectedItem();
			
			try {
				if (path instanceof MyListItemWrapper) {
					int index = this.location.getSelectedIndex();
					if (index >= 0) {
						setHistoryLocation(index);
					}
					
					this.viewer.setPage(((MyListItemWrapper) path).url);
				} else if (path instanceof String) {
					URL url = new URL((String) path);
					
					addURL(url);
					this.viewer.setPage(url);
				}
			} catch (MalformedURLException ex) {
				System.out.println("MalformedURLException: " + ex.getMessage());
			} catch (IOException ex) {
				System.out.println("IOException: " + ex.getMessage());
			}
		}
	}
	
	
	// Other Methods
	public String getTitle() {
		Document doc = this.viewer.getDocument();
		return (String) doc.getProperty(Document.TitleProperty);
	}
	
	// Inner Classes
	private class MyButton extends JButton implements MouseListener {
		public MyButton() {
			super();
			
			setBorderPainted(false);
			setFocusPainted(false);
			
			addMouseListener(this);
		}
		
		// MouseListener Interface
		public void mouseClicked(MouseEvent e) {}
		
		public void mouseEntered(MouseEvent e) {
			setBorderPainted(true);
		}
		
		public void mouseExited(MouseEvent e) {
			setBorderPainted(false);
		}
		
		public void mousePressed(MouseEvent e) {}
		
		public void mouseReleased(MouseEvent e) {}
	}
	
	private class MyListItemWrapper {
		public URL url;
		
		public MyListItemWrapper(URL url) {
			this.url = url;
		}
		
		public String toString() {
			return(url.toString());
		}
	}
}

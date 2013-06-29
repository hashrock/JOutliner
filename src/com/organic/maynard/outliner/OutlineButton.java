/**
 * Copyright (C) 2000, 2001, 2002 Maynard Demmon, maynard@organic.com
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
import javax.swing.*;

import java.awt.image.*;
import java.awt.geom.*;

import com.organic.maynard.outliner.util.preferences.Preferences;

import com.organic.maynard.imaging.ImageFilters;

/**
 * @author  $Author: maynardd $
 * @version $Revision: 1.16 $, $Date: 2002/12/19 00:29:04 $
 */
 
public class OutlineButton extends JLabel {
	
	// Class Fields
	public static final ImageIcon ICON_CLOSED_NODE = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("graphics/closed_node.gif"));
	
	public static int BUTTON_WIDTH = ICON_CLOSED_NODE.getIconWidth();
	public static int BUTTON_HEIGHT = ICON_CLOSED_NODE.getIconHeight();
	
	public static ImageIcon ICON_OPEN_NODE = new ImageIcon();
	public static ImageIcon ICON_OPEN_NODE_SELECTED = new ImageIcon();
	public static ImageIcon ICON_CLOSED_NODE_SELECTED = new ImageIcon();
	
	public static ImageIcon ICON_LEAF = new ImageIcon();
	public static ImageIcon ICON_LEAF_SELECTED = new ImageIcon();
	
	// Note: icons are initialized by the createIcons() method below. This method
	// is called from Outliner during it's endSetup() method.
	
	public static final ImageIcon ICON_DOWN_ARROW = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("graphics/down_arrow.gif"));
	public static final ImageIcon ICON_SE_ARROW = new ImageIcon(Thread.currentThread().getContextClassLoader().getResource("graphics/se_arrow.gif"));
	
	
	// Instance Fields
	public OutlinerCellRendererImpl renderer = null;
	
	private boolean isNode = true;
	private boolean isOpen = true;
	private boolean isSelected = false;
	
	// The Constructor
	public OutlineButton(OutlinerCellRendererImpl renderer) {
		this.renderer = renderer;
		
		setVerticalAlignment(SwingConstants.TOP);
		setOpaque(true);
		setVisible(false);
	}
	
	public void destroy() {
		removeAll();
		removeNotify();
		setIcon(null);
		renderer = null;
	}
	
	public boolean isManagingFocus() {return true;}
	
	// Used to fire key events
	public void fireKeyEvent(KeyEvent event) {
		processKeyEvent(event);
	}
	
	public void setNode(boolean isNode) {this.isNode = isNode;}
	public void setOpen(boolean isOpen) {this.isOpen = isOpen;}
	public void setSelected(boolean isSelected) {this.isSelected = isSelected;}
	
	public void updateIcon() {
		if(isNode) {
			if(isOpen) {
				if(isSelected) {
					setIcon(ICON_OPEN_NODE_SELECTED);
				} else {
					setIcon(ICON_OPEN_NODE);
				}
			} else {
				if(isSelected) {
					setIcon(ICON_CLOSED_NODE_SELECTED);
				} else {
					setIcon(ICON_CLOSED_NODE);
				}
			}	
		} else {
			if(isSelected) {
				setIcon(ICON_LEAF_SELECTED);
			} else {
				setIcon(ICON_LEAF);
			}
		}
	}
	
	
	// Static Methods
	public static void createIcons() {
		// Get the colors to use
		Color c = Preferences.getPreferenceColor(Preferences.TEXTAREA_FOREGROUND_COLOR).cur;
		int hexColor = ((c.getRed() << 16) | (c.getGreen() << 8) | c.getBlue());
		
		Color c2 = Preferences.getPreferenceColor(Preferences.TEXTAREA_BACKGROUND_COLOR).cur;
		int hexColor2 = ((c2.getRed() << 16) | (c2.getGreen() << 8) | c2.getBlue());
		
		// Transfer the image from the ImageIcon to a Graphics2D so we can manipulate it.
		Image closedImage = ICON_CLOSED_NODE.getImage();
		
		BufferedImage image = new BufferedImage(BUTTON_WIDTH, BUTTON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.drawImage(closedImage,0,0,Outliner.outliner);
		
		// Create a buffered image from the closed node image.
		RGBImageFilter colorFilter = ImageFilters.getFillFilter(hexColor);
		FilteredImageSource source = new FilteredImageSource(image.getSource(), colorFilter);
		Image closedImage2 = Outliner.outliner.createImage(source);
		
		ICON_CLOSED_NODE.setImage(closedImage2);
		
		// Re-Apply the new image to the Graphics2D so the other images can work from the colored image.
		g.drawImage(closedImage2,0,0,Outliner.outliner);
		
		// Create Buffered Image for the derived images.
		BufferedImage openImage = new BufferedImage(BUTTON_WIDTH, BUTTON_HEIGHT, image.getType());
		
		// Define a transforamtion to rotate the closed image to create the open image.
		AffineTransformOp at = new AffineTransformOp(AffineTransform.getRotateInstance((java.lang.Math.PI)/2, BUTTON_WIDTH/2, BUTTON_HEIGHT/2), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		at.filter(image, openImage);
		
		ICON_OPEN_NODE.setImage(openImage);
		
		// Lighten color to create leaf
		RGBImageFilter lightenFilter = ImageFilters.getLightenFilter(0x00999999);
		FilteredImageSource leafSource = new FilteredImageSource(image.getSource(), lightenFilter);
		Image leafImage = Outliner.outliner.createImage(leafSource);
		
		ICON_LEAF.setImage(leafImage);
		
		// Lighten color for selected leaf
		RGBImageFilter lightenFilter2 = ImageFilters.getLightenFilter(0x00333333);
		FilteredImageSource leafSelectedSource = new FilteredImageSource(leafImage.getSource(), lightenFilter2);
		Image leafSelectedImage = Outliner.outliner.createImage(leafSelectedSource);
		
		ICON_LEAF_SELECTED.setImage(leafSelectedImage);
		
		// Fill with the background color for selected images
		RGBImageFilter fillFilter = ImageFilters.getFillFilter(hexColor2);
		FilteredImageSource openSource = new FilteredImageSource(openImage.getSource(), fillFilter);
		FilteredImageSource closedSource = new FilteredImageSource(image.getSource(), fillFilter);
		Image openSelectedImage = Outliner.outliner.createImage(openSource);
		Image closedSelectedImage = Outliner.outliner.createImage(closedSource);
		
		ICON_OPEN_NODE_SELECTED.setImage(openSelectedImage);
		ICON_CLOSED_NODE_SELECTED.setImage(closedSelectedImage);
	}
}
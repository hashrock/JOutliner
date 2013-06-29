/**
 * Portions copyright (C) 2002 Maynard Demmon, maynard@organic.com
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
 
/**
 * @last touched by $Author: maynardd $
 * @version $Revision: 1.1 $, $Date: 2002/04/28 05:59:00 $
 */

// we're a part of this
package com.organic.maynard.outliner.util;

// we use these
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.Rectangle;
import com.organic.maynard.util.string.Replace;

public class Ginsu implements Runnable {
	
	// Constants
	private static final int SLEEP_SHORT = 100;
	private static final int SLEEP_LONG = 1000;
	
	
	// Class Fields
	public static Ginsu ginsu = null;
	
	
	// Instance Fields
	private boolean end = false;
	private Stack destructibles = new Stack();
	
	
	// Constructor
	public Ginsu() {}


	// Runnable Interface
	public void run() {
		while (!end) {
			process();
			try {Thread.sleep(SLEEP_LONG);} catch (InterruptedException e) {}
		}
		process();
	}
	
	private void process() {
		while(!destructibles.empty()) {
			((Destructible) destructibles.pop()).destroy();
			System.gc();			
			try {Thread.sleep(SLEEP_SHORT);} catch (InterruptedException e) {}
		}	
	}
	
	
	// Static Methods
	public static void sliceAndDice(Destructible d) {
		// Lazy instantiation
		if (ginsu == null) {
			ginsu = new Ginsu();
			Thread t = new Thread(ginsu);
			t.start();
		}
		
		ginsu.destructibles.push(d);
	}
	
	public static void stop() {
		if (ginsu != null) {
			ginsu.end = true;
		}
	}
}
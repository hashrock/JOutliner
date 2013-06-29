/*
 * Copyright (c) 2001, Maynard Demmon
 * Copyright (c) 2001, Organic
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, this 
 *    list of conditions and the following disclaimer.
 * 
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *
 *  - Neither the name of Organic nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 * IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.organic.maynard.util;

import java.util.*;

public class Queue {
	public static final int DEFAULT_MAX_SIZE = 10;
	
	protected int max_size = 0;
	protected Vector queue = new Vector();
	
	// The Constructors
	public Queue() {
		this(DEFAULT_MAX_SIZE);
	}

	public Queue(int max_size) {
		setMaxSize(max_size);
	}
	
	// The Accessors
	public synchronized void setMaxSize(int max_size) {
		this.max_size = max_size;
		if (getSize() > max_size) {
			queue.setSize(max_size);
		}
	}
	
	public synchronized int getMaxSize() {return this.max_size;}

	public synchronized int getSize() {return queue.size();}

	public synchronized boolean isFull() {
		if (getMaxSize() > getSize()) {
			return false;
		} else {
			return true;
		}
	}
	
	public synchronized Object inspectNext() {
		return queue.firstElement();
	}
	
	public synchronized Object getNext() {
		Object obj = null;
		if (getSize() > 0) {
			obj = queue.firstElement();
			queue.removeElementAt(0);
		}
		return obj;
	}
	
	public synchronized boolean add(Object obj) {
		boolean retVal = false;
		if (!isFull()) {
			queue.addElement(obj);
			retVal = true;
		}
		return retVal;
	}
}
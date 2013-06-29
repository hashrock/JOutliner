/**
 * Copyright (C) 2002 Maynard Demmon, maynard@organic.com
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
 
package com.organic.maynard.data;

import java.util.*;
import java.io.Serializable;

public class IntList implements Serializable {
	
	// Constants
	private static final int DEFAULT_SIZE = 10;
	public static final String DEFAULT_DELIMITER = ",";
	
	
	// Fields
	private int data[];
	private int size;
	private String delimiter = DEFAULT_DELIMITER;
	
	
	// Constructors
	public IntList() {
		this(DEFAULT_SIZE);
	}
	
	public IntList(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		}
		this.data = new int[initialCapacity];
	}
	
	public IntList(String int_list) {
		this(int_list, DEFAULT_DELIMITER);
	}
	
	public IntList(String int_list, String delimiter) {
		this();
		
		this.delimiter = delimiter;
		
		StringTokenizer tok = new StringTokenizer(int_list, delimiter);
		while (tok.hasMoreTokens()) {
			String int_string = tok.nextToken();
			try {
				add(Integer.parseInt(int_string));
			} catch (NumberFormatException nfe) {
				System.out.println("NumberFormatException when instantiating a new IntList.");
			}
		}
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			if (i != 0) {
				buf.append(this.delimiter);
			}
			buf.append("" + data[i]);
		}
		return buf.toString();
	}
	
	
	// Accessors
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public int get(int index) {
		RangeCheck(index);
		return data[index];
	}
	
	public void set(int index, int datum) {
		RangeCheck(index);
		data[index] = datum;
	}
	
	public void add(int datum) {
		ensureCapacity(size + 1);
		data[size++] = datum;
	}
	
	public void add(int index, int datum) {
		if (index > size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
		
		ensureCapacity(size + 1);
		System.arraycopy(data, index, data, index + 1, size - index);
		data[index] = datum;
		size++;
	}
	
	public void remove(int index) {
		RangeCheck(index);
		
		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(data, index + 1, data, index, numMoved);
		}
		--size; 
	}
	
	public void removeRange(int fromIndex, int toIndex) {
		int numMoved = size - toIndex;
		System.arraycopy(data, toIndex, data, fromIndex, numMoved);
		
		size = size - (toIndex - fromIndex);
	}
	
	public void clear() {
		this.data = new int[DEFAULT_SIZE];
		size = 0;
	}
	
	// Index Of
	public boolean contains(int datum) {
		return indexOf(datum) >= 0;
	}
	
	public int indexOf(int datum) {
		return firstIndexOf(datum);
	}
	
	public int firstIndexOf(int datum) {
		for (int i = 0; i < size; i++) {
			if (data[i] == datum) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int lastIndexOf(int datum) {
		for (int i = size - 1; i >= 0; i--) {
			if (data[i] == datum) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int indexOf(int datum, int start, int end) {
		RangeCheck(start);
		RangeCheck(end);
		
		for (int i = start; i <= end; i++) {
			if (data[i] == datum) {
				return i;
			}
		}
		
		return -1;
	}
	
	// Data Conversion
	public int[] toArray() {
		int[] result = new int[size];
		System.arraycopy(data, 0, result, 0, size);
		return result;
	}
  
  
	// Misc Methods
	private void RangeCheck(int index) {
		if (index >= size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
	}
	
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = data.length;
		
		if (minCapacity > oldCapacity) {
			int oldData[] = data;
			int newCapacity = (oldCapacity * 3)/2 + 1;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			data = new int[newCapacity];
			System.arraycopy(oldData, 0, data, 0, size);
		}
	}
}
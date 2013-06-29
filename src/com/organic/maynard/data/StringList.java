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

public class StringList implements Serializable, Cloneable {

	// Fields
	private String data[];
	private int size;

	// Constructors
	public StringList() {
		this(10);
	}
   
	public StringList(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		}
		this.data = new String[initialCapacity];
	}


	// Cloneable Interface
	public Object clone() {
		StringList newList = new StringList();
		
		for (int i = 0; i < this.size(); i++) {
			newList.add(this.get(i));
		}
		
		return newList;
	}

	// Accessors
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public String get(int index) {
		RangeCheck(index);
		return data[index];
	}

	public void set(int index, String datum) {
		RangeCheck(index);
		data[index] = datum;
	}

	public void add(String datum) {
		ensureCapacity(size + 1);
		data[size++] = datum;
	}
	
	public void add(int index, String datum) {
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
		data[--size] = null; 
	}

	public void removeRange(int fromIndex, int toIndex) {
		int numMoved = size - toIndex;
		System.arraycopy(data, toIndex, data, fromIndex, numMoved);

		int newSize = size - (toIndex - fromIndex);
		while (size != newSize) {
			data[--size] = null;
		}
	}

	public void clear() {
		for (int i = 0; i < size; i++) {
			data[i] = null;
		}

		size = 0;
	}

	// Index Of
	public boolean contains(String datum) {
		return indexOf(datum) >= 0;
	}

	public int indexOf(String datum) {
		return firstIndexOf(datum);
	}

	public int firstIndexOf(String datum) {
		for (int i = 0; i < size; i++) {
			if (data[i] == datum) {
				return i;
			}
		}
		
		return -1;
	}

	public int lastIndexOf(String datum) {
		for (int i = size - 1; i >= 0; i--) {
			if (data[i] == datum) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int indexOf(String datum, int start, int end) {
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
	public String[] toArray() {
		String[] result = new String[size];
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
			String oldData[] = data;
			int newCapacity = (oldCapacity * 3)/2 + 1;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			data = new String[newCapacity];
			System.arraycopy(oldData, 0, data, 0, size);
		}
	}
}
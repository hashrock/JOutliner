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

import org.xml.sax.*;
import com.organic.maynard.outliner.*;
import java.util.*;

public class AttributeListList {
	
	// Fields
	private Attributes attributeLists[];
	private int size;
	
	// Constructors
	public AttributeListList() {
		this(10);
	}
  
	public AttributeListList(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
		}
		this.attributeLists = new Attributes[initialCapacity];
	}
	
	
	// Accessors
	public int size() {
		return size;
	}
	
	public boolean isEmpty() {
		return size == 0;
	}
	
	public Attributes get(int index) {
		RangeCheck(index);
		return attributeLists[index];
	}
	
	public void set(int index, Attributes attributeList) {
		RangeCheck(index);
		attributeLists[index] = attributeList;
	}
	
	public void add(Attributes attributeList) {
		ensureCapacity(size + 1);
		attributeLists[size++] = attributeList;
	}
	
	public void add(int index, Attributes attributeList) {
		if (index > size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
		
		ensureCapacity(size + 1);
		System.arraycopy(attributeLists, index, attributeLists, index + 1, size - index);
		attributeLists[index] = attributeList;
		size++;
	}
	
	public void remove(int index) {
		RangeCheck(index);
		
		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(attributeLists, index + 1, attributeLists, index, numMoved);
		}
		attributeLists[--size] = null; 
	}
	
	public void removeRange(int fromIndex, int toIndex) {
		int numMoved = size - toIndex;
		System.arraycopy(attributeLists, toIndex, attributeLists, fromIndex, numMoved);
		
		int newSize = size - (toIndex - fromIndex);
		while (size != newSize) {
			attributeLists[--size] = null;
		}
	}
	
	public void trim(int newSize) {
		if (newSize > size || newSize < 0) {
			throw new IndexOutOfBoundsException("Index: " + newSize + ", Size: " + size);
		}
		
		while (size > newSize) {
			attributeLists[--size] = null;
		}
	}
	
	public void clear() {
		for (int i = 0; i < size; i++) {
			attributeLists[i] = null;
		}
		
		size = 0;
	}
	
	// Index Of
	public boolean contains(Attributes attributeList) {
		return indexOf(attributeList) >= 0;
	}
	
	public int indexOf(Attributes attributeList) {
		return firstIndexOf(attributeList);
	}
	
	public int firstIndexOf(Attributes attributeList) {
		for (int i = 0; i < size; i++) {
			if (attributeLists[i] == attributeList) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int lastIndexOf(Attributes attributeList) {
		for (int i = size - 1; i >= 0; i--) {
			if (attributeLists[i] == attributeList) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int indexOf(Attributes attributeList, int start, int end) {
		RangeCheck(start);
		RangeCheck(end);
		
		for (int i = start; i <= end; i++) {
			if (attributeLists[i] == attributeList) {
				return i;
			}
		}
		
		return -1;
	}
	
	// Data Conversion
	public Attributes[] toArray() {
		Attributes[] result = new Attributes[size];
		System.arraycopy(attributeLists, 0, result, 0, size);
		return result;
	}
	
  
	// Misc Methods
	private void RangeCheck(int index) {
		if (index >= size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
	}
	
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = attributeLists.length;
		
		if (minCapacity > oldCapacity) {
			Attributes oldData[] = attributeLists;
			int newCapacity = (oldCapacity * 3)/2 + 1;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			attributeLists = new Attributes[newCapacity];
			System.arraycopy(oldData, 0, attributeLists, 0, size);
		}
	}
}
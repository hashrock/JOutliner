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
 
package com.organic.maynard.outliner;

import java.util.*;

public class NodeList 
	implements JoeNodeList {

	// Fields
	private Node nodes[];
	private int size;

	// Constructors
	public NodeList() {
		this(10);
	}
   
	public NodeList(int initialCapacity) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
		}
		this.nodes = new Node[initialCapacity];
	}


	// Accessors
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public Node get(int index) {
		RangeCheck(index);
		return nodes[index];
	}

	public void set(int index, Node node) {
		RangeCheck(index);
		nodes[index] = node;
	}

	public void add(Node node) {
		ensureCapacity(size + 1);
		nodes[size++] = node;
	}
	
	public void add(int index, Node node) {
		if (index > size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}

		ensureCapacity(size + 1);
		System.arraycopy(nodes, index, nodes, index + 1, size - index);
		nodes[index] = node;
		size++;
	}

	public void remove(int index) {
		RangeCheck(index);

		int numMoved = size - index - 1;
		if (numMoved > 0) {
			System.arraycopy(nodes, index + 1, nodes, index, numMoved);
		}
		nodes[--size] = null; 
	}

	public void removeRange(int fromIndex, int toIndex) {
		int numMoved = size - toIndex;
		System.arraycopy(nodes, toIndex, nodes, fromIndex, numMoved);

		int newSize = size - (toIndex - fromIndex);
		while (size != newSize) {
			nodes[--size] = null;
		}
	}

	public void clear() {
		for (int i = 0; i < size; i++) {
			nodes[i] = null;
		}

		size = 0;
	}

	// Index Of
	public boolean contains(Node node) {
		return indexOf(node) >= 0;
	}

	public int indexOf(Node node) {
		return firstIndexOf(node);
	}

	public int firstIndexOf(Node node) {
		for (int i = 0; i < size; i++) {
			if (nodes[i] == node) {
				return i;
			}
		}
		
		return -1;
	}

	public int lastIndexOf(Node node) {
		for (int i = size - 1; i >= 0; i--) {
			if (nodes[i] == node) {
				return i;
			}
		}
		
		return -1;
	}
	
	public int indexOf(Node node, int start, int end) {
		RangeCheck(start);
		RangeCheck(end);
		
		for (int i = start; i <= end; i++) {
			if (nodes[i] == node) {
				return i;
			}
		}
		
		return -1;
	}
	
	// Data Conversion
	public Node[] toArray() {
		Node[] result = new Node[size];
		System.arraycopy(nodes, 0, result, 0, size);
		return result;
	}
    
    
	// Misc Methods
	private void RangeCheck(int index) {
		if (index >= size || index < 0) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
	}

	public void ensureCapacity(int minCapacity) {
		int oldCapacity = nodes.length;
		
		if (minCapacity > oldCapacity) {
			Node oldData[] = nodes;
			int newCapacity = (oldCapacity * 3)/2 + 1;
			if (newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			nodes = new Node[newCapacity];
			System.arraycopy(oldData, 0, nodes, 0, size);
		}
	}
}
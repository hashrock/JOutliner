/**
 * Copyright (C) 2004 Maynard Demmon, maynard@organic.com
 * Portions copyright (C) 2002   Stan Krute <Stan@StanKrute.com>
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

package com.organic.maynard.net;

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.util.*;

/**
 * Provides a means to create and manipulate URLs. In particular, the query
 * portion of an URL.
 * See RFC 2396: Uniform Resource Identifiers (URI) for syntax information.
 */
public class MuteableURL {
	// Constants
	private static final char QUERY_DELIM = '?';
	private static final String QUERY_NVP_DELIM = "&";
	private static final String QUERY_NV_DELIM = "=";
	private static final char PORT_DELIM = ':';
	private static final char FRAGMENT_DELIM = '#';
	private static final char PROTOCOL_DELIM = ':'; // AKA Scheme
	private static final String AUTHORITY_DELIM = "//";
	private static final String PATH_DELIM = "/";
	private static final char USERINFO_DELIM = '@';
	
	
	// Instance Fields
	private ArrayList query_list;
	private ArrayList path_list;
	
	private String protocol;
	private String host;
	private int port = -1;
	private String path;
	private String file;
	private boolean is_absolute_path = true;
	private String query;
	private String fragment;
	private String authority;
	private String userInfo;
	
	
	// Constructors
	public MuteableURL(String spec) {
		String[] parsed_url = parseURL(spec);
		
		// Then store the parts
		if (parsed_url != null) {
			setProtocol(parsed_url[0]);
			setAuthority(parsed_url[1]);
			setUserInfo(parsed_url[2]);
			setHost(parsed_url[3]);
			try {
				String port_string = parsed_url[4];
				if (port_string != null && port_string.length() > 0) {
					setPort(Integer.parseInt(port_string));
				} else {
					setPort(-1);
				}
			} catch (NumberFormatException nfe) {
				setPort(-1);
				nfe.printStackTrace();
			}
			setPath(parsed_url[5]);
			setQuery(parsed_url[6]);
			setFragment(parsed_url[7]);
		}
		
		/*
		System.out.println("protocol: " + this.protocol);
		System.out.println("authority: " + this.authority);
		System.out.println("user_info: " + this.userInfo);
		System.out.println("host: " + this.host);
		System.out.println("port: " + this.port);
		System.out.println("path: " + this.path);
		System.out.println("query: " + this.query);
		System.out.println("fragment: " + this.fragment);*/
	}
	
	// Accessors
	public String getProtocol() {
		return this.protocol;
	}
	
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
	public String getHost() {
		return this.host;
	}
	
	public void setHost(String host) {
		this.host = host;
		this.authority = assembleAuthority(this.host, this.port, this.userInfo, null);
	}
	
	public String getUserInfo() {
		return this.userInfo;
	}
	
	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
		this.authority = assembleAuthority(this.host, this.port, this.userInfo, null);
	}
	
	public int getPort() {
		return this.port;
	}
	
	public void setPort(int port) {
		this.port = port;
		this.authority = assembleAuthority(this.host, this.port, this.userInfo, null);
	}
	
	public String getAuthority() {
		return this.authority;
	}
	
	public void setAuthority(String authority) {
		this.authority = authority;
		String[] authority_parts = parseAuthority(authority);
		this.userInfo = authority_parts[0];
		this.host = authority_parts[1];
		try {
			String port_string = authority_parts[2];
			if (port_string != null && port_string.length() > 0) {
				this.port = Integer.parseInt(port_string);
			} else {
				this.port = -1;
			}
		} catch (NumberFormatException nfe) {
			this.port = -1;
			nfe.printStackTrace();
		}
	}
	
	public String getPath() {
		return this.path;
	}
	
	public void setPath(String path) {
		this.path = path;
		if (this.path != null) {
			this.path_list = new ArrayList();
			parsePath(this.path_list, this.path);
		}
	}
	
	public String getFile() {
		return this.file;
	}
	
	public void setFile(String file) {
		this.file = file;
		this.path = assemblePath(this.path_list);
	}
	
	public boolean isAbsolutePath() {
		return this.is_absolute_path;
	}
	
	public void setAbsolutePath(boolean is_absolute_path) {
		this.is_absolute_path = is_absolute_path;
		this.path = assemblePath(this.path_list);
	}
	
	public String getQuery() {
		return this.query;
	}
	
	public void setQuery(String query) {
		this.query = query;
		if (this.query != null) {
			this.query_list = new ArrayList();
			parseQuery(this.query_list, this.query);
		}
	}
	
	public String getFragment() {
		return this.fragment;
	}
	
	public void setFragment(String fragment) {
		this.fragment = fragment;
	}
	
	/**
	 * Get a String representation of this URL.
	 */
	public String getURLString() {
		return assembleURL(
			this.host,
			this.port,
			this.userInfo,
			this.authority,
			this.protocol,
			this.path_list,
			this.query_list,
			this.fragment
		);
	}
	
	/**
	 * Get an java.net.URL representation of this URL. This will definitely fail
	 * for relative URLs since there will be no context for java.net.URL to use.
	 */
	public URL getURL() throws MalformedURLException {
		URL url = null;
		
		try {
			url = new URL(getURLString());
		} catch (MalformedURLException mue) {
			throw mue;
		}
		
		return url;
	}
	
	// Query Handling
	/*public boolean hasQueryKey(String key) {
		
	}
	
	public List getQueryValue(String key) {
		
	}*/
	
	// Path Handling
	
	
	// Misc
	/**
	 * Overrides the standard toString method from java.lang.Object.
	 */
	public String toString() {
		return getURLString();
	}
	
	
	// Parsing/Assembling
	/**
	 * Parses an URL into parts.
	 * Parts:
	 * 0 -> protocol
	 * 1 -> authority
	 * 2 -> userInfo
	 * 3 -> host
	 * 4 -> port
	 * 5 -> path
	 * 6 -> query
	 * 7 -> fragment
	 */
	private String[] parseURL(
		String url
	) {
		if (url == null) {
			return null;
		}
		
		String[] parts = new String[8];
		
		// Remove leading and trailing whitespace
		url = url.trim();
		
		// Fragment
		int fragment_index = url.indexOf(FRAGMENT_DELIM);
		if (fragment_index < 0) {
			parts[7] = null;
		} else {
			parts[7] = url.substring(fragment_index + 1);
			url = url.substring(0, fragment_index);
		}
		
		// Query
		int query_index = url.lastIndexOf(QUERY_DELIM);
		if (query_index < 0) {
			parts[6] = null;
		} else {
			parts[6] = url.substring(query_index + 1);
			url = url.substring(0, query_index);
		}
		
		// Protocol
		int first_colon = url.indexOf(':');
		int first_slash = url.indexOf('/');
		if (first_colon >= 0 && first_colon + 1 == first_slash) {
			parts[0] = url.substring(0, first_colon);
			url = url.substring(first_colon + 1);
		}
		
		// Authority
		if (url.startsWith("//")) {
			url = url.substring(2);
			int next_slash = url.indexOf(PATH_DELIM);
			if (next_slash >= 0) {
				parts[1] = url.substring(0, next_slash);
				url = url.substring(next_slash);
			} else {
				parts[1] = url;
				url = "";
			}
			
			String[] authority_parts = parseAuthority(parts[1]);
			parts[2] = authority_parts[0];
			parts[3] = authority_parts[1];
			parts[4] = authority_parts[2];
		}
		
		// Path
		parts[5] = url;
		
		return parts;
	}
	
	/**
	 * Assemble an URL String from it's parts.
	 */
	private String assembleURL(
		String host,
		int port,
		String userInfo,
		String authority,
		String protocol,
		List path_list,
		List query_list,
		String fragment
	) {
		StringBuffer buf = new StringBuffer();
		
		if (this.is_absolute_path) {
			// Protocol
			if (protocol != null && protocol.length() > 0) {
				buf.append(protocol);
				buf.append(PROTOCOL_DELIM);
			}
			
			// Authority
			authority = assembleAuthority(host, port, userInfo, authority);
			if (authority != null && authority.length() > 0) {
				buf.append(AUTHORITY_DELIM);
				buf.append(authority);
			}
		}
		
		// Path
		String path = assemblePath(path_list);
		if (path != null) {
			buf.append(path);
		}
		
		// Query
		String query = assembleQuery(query_list);
		if (query != null) {
			buf.append(QUERY_DELIM);
			buf.append(query);
		}
		
		// Fragment
		if (fragment != null) {
			buf.append(FRAGMENT_DELIM);
			buf.append(fragment);
		}
		
		return buf.toString();
	}
	
	/**
	 * Parses the provided authority string.
	 */
	private String[] parseAuthority(String authority) {
		String[] parts = new String[3];
		
		if (authority != null) {
			// userInfo
			int first_ampersand = authority.indexOf(USERINFO_DELIM);
			if (first_ampersand >= 0) {
				parts[0] = authority.substring(0, first_ampersand);
				authority = authority.substring(first_ampersand + 1);
			}
			
			// host
			int first_colon = authority.indexOf(PORT_DELIM);
			if (first_colon >= 0) {
				parts[1] = authority.substring(0, first_colon);
				authority = authority.substring(first_colon + 1);
			} else {
				parts[1] = authority;
				authority = "";
			}
			
			// port
			parts[2] = authority;
		}
		
		return parts;
	}
	
	/**
	 * Assemble the authority portion of an URL from it's parts.
	 */
	private String assembleAuthority(
		String host,
		int port,
		String userInfo,
		String authority
	) {
		if (authority != null) {
			return authority;
		}
		
		StringBuffer buf = new StringBuffer();
		
		// UserInfo
		if (userInfo != null) {
			buf.append(userInfo);
			buf.append(USERINFO_DELIM);
		}
		
		// Host. Note if the host is null then this URL will be malformed
		if (host != null) {
			buf.append(host);
		}
		
		// Port
		if (port > 0) {
			buf.append(PORT_DELIM);
			buf.append(port);
		}
		
		return buf.toString();
	}
	
	/**
	 * Parses the provided path string into name segments and populates the 
	 * provided list with them. The setting of the file and is_absolute_path
	 * variable are done by directly accessing "this" object. 
	 */
	private void parsePath(List list, String path) {
		if (path == null) {
			return;
		}
		
		if (path.length() == 0) {
			// is_absolute_path is ambiguous so we won't change anything
			this.file = null;
			return;
		}
		
		// Determine if the path is absolute
		if (path.startsWith(PATH_DELIM)) {
			this.is_absolute_path = true;
		} else {
			this.is_absolute_path = false;
		}
		
		StringTokenizer path_tok = new StringTokenizer(path, PATH_DELIM, true);
		boolean prev_token_was_delim = false;
		while (path_tok.hasMoreTokens()) {
			String token = path_tok.nextToken();
			
			String segment = null;
			if (PATH_DELIM.equals(token)) {
				if (prev_token_was_delim) {
					segment = "";
				} else {
					segment = null;
				}
				
				if (!path_tok.hasMoreTokens()) {
					// No file since we end in a path delimiter
					this.file = null;
				}
				
				prev_token_was_delim = true;
			} else {
				segment = token;
				
				if (!path_tok.hasMoreTokens()) {
					this.file = segment;
					segment = null;
				}
				
				prev_token_was_delim = false;
			}
			
			if (segment != null) {
				list.add(segment);
			}
		}
	}
	
	/**
	 * Assembles a path string from a list of segments.
	 */
	private String assemblePath(List list) {
		if (list == null) {
			return null;
		}
		
		StringBuffer buf = new StringBuffer();
		
		if (this.is_absolute_path && (path != null && path.length() > 0)) {
			buf.append(PATH_DELIM);
		}
		
		for (int i = 0; i < list.size(); i++) {
			String segment = (String) list.get(i);
			if (segment != null) {
				buf.append(segment);
				buf.append(PATH_DELIM);
			}
		}
		
		if (this.file != null) {
			buf.append(this.file);
		}
		
		return buf.toString();
	}
	
	/**
	 * Parses the provided query string into name value pairs and populates the
	 * provided list with them.
	 */
	private void parseQuery(List list, String query) {
		if (list == null) {
			return;
		}
		
		StringTokenizer nvp_tok = new StringTokenizer(query, QUERY_NVP_DELIM, true);
		boolean prev_token_was_delim = true;
		while (nvp_tok.hasMoreTokens()) {
			String token = nvp_tok.nextToken();
			
			String[] pair = new String[2];
			String nvp = null;
			
			if (QUERY_NVP_DELIM.equals(token)) {
				if (prev_token_was_delim) {
					nvp = "";
				} else {
					nvp = null;
				}
				
				if (!nvp_tok.hasMoreTokens()) {
					list.add(new String[2]);
				}
				
				prev_token_was_delim = true;
			} else {
				nvp = token;
				
				StringTokenizer nv_tok = new StringTokenizer(nvp, QUERY_NV_DELIM, true);
				
				String name = null;
				String nv_delim = null;
				String value = null;
				
				if (nv_tok.hasMoreTokens()) {
					token = nv_tok.nextToken();
					if (QUERY_NV_DELIM.equals(token)) {
						name = "";
						if (nv_tok.hasMoreTokens()) {
							value = nv_tok.nextToken();
						} else {
							value = "";
						}
					} else {
						name = token;
						
						if (nv_tok.hasMoreTokens()) {
							nv_delim = nv_tok.nextToken();
							value = "";
						}
						
						if (nv_tok.hasMoreTokens()) {
							value = nv_tok.nextToken();
						}
					}
				}
				
				pair[0] = name;
				pair[1] = value;
				
				prev_token_was_delim = false;
			}
			
			if (nvp != null) {
				list.add(pair);
			}
		}
	}
	
	/**
	 * Assembles a query string from a list of name value pairs. Does not include
	 * the query delimiter '?'.
	 */
	private String assembleQuery(List list) {
		if (list == null) {
			return null;
		}
		
		StringBuffer buf = new StringBuffer();
		
		for (int i = 0; i < list.size(); i++) {
			if (i != 0) {
				buf.append(QUERY_NVP_DELIM);
			}
			String[] pair = (String[]) list.get(i);
			if (pair != null && pair.length == 2) {
				String key = pair[0];
				String value = pair[1];
				if (key != null) {
					buf.append(key);
				}
				
				if (key != null && value != null) {
					buf.append(QUERY_NV_DELIM);
				}
				
				if (value != null) {
					buf.append(value);
				}
			}
		}
		
		return buf.toString();
	}
	
	
	
	
	
	// Unit Testing
	public static void main(String args[]) {
		unitTest();
		
		for (int i = 0; i < args.length; i++) {
			String test_url = args[i];
			unitTestParseAssemble(test_url);
		}
	}
	
	/**
	 * Used to perform unit tests
	 */
	private static void unitTest() {
		System.out.println("PARSE/ASSEMBLE TESTS");
		
		// Dirs and Files
		unitTestParseAssemble("http://www.organic.com");
		unitTestParseAssemble("http://www.organic.com/");
		unitTestParseAssemble("http://www.organic.com/file.jsp");
		
		unitTestParseAssemble("http://www.organic.com/dir");
		unitTestParseAssemble("http://www.organic.com/dir/");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp");
		
		unitTestParseAssemble("http://www.organic.com/dir/dir2");
		unitTestParseAssemble("http://www.organic.com/dir/dir2/");
		unitTestParseAssemble("http://www.organic.com/dir/dir2/file.jsp");
		
		// Query
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp??");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?=?");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?name");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?name=");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?name=value");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?=");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?==");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?=value");
		
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?&");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?&&");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?&&&");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?&value");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?name&");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?name&value");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?name=value&name=value");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?name=&name=value");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?=&");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?&=");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?=&=");
		unitTestParseAssemble("http://www.organic.com/dir/file.jsp?name=value+space&name=value%20space");
		
		unitTestParseAssemble("http://www.organic.com?");
		unitTestParseAssemble("http://www.organic.com?name=value");
		
		// Fragments
		unitTestParseAssemble("http://www.organic.com#");
		unitTestParseAssemble("http://www.organic.com##");
		unitTestParseAssemble("http://www.organic.com#fragment");
		unitTestParseAssemble("http://www.organic.com?name=value#fragment");
		
		// Ports
		unitTestParseAssemble("http://www.organic.com:8080");
		unitTestParseAssemble("http://www.organic.com:8080/");
		unitTestParseAssemble("http://www.organic.com:8080?");
		unitTestParseAssemble("http://www.organic.com:8080?name=value");
		
		// All Together
		unitTestParseAssemble("http://maynard:password@www.organic.com:8080/dir/file.jsp?name=value&name+space=value%20space#fragment");
		
		// Relative URLs
		unitTestParseAssemble("/file.jsp");
		unitTestParseAssemble("/");
		unitTestParseAssemble("../");
		unitTestParseAssemble("file.jsp");
		unitTestParseAssemble("dir/file.jsp?name=value");
		unitTestParseAssemble("http:/file.jsp");
		
		// Empty URL
		unitTestParseAssemble("");
		unitTestParseAssemble(null);
		
		// Test Getters and Setters
		MuteableURL m_url = new MuteableURL(null);
		m_url.setProtocol("http");
		m_url.setHost("www.organic.com");
		m_url.setPath("/dir/file.jsp");
		m_url.setQuery("this=that&these=those");
		m_url.setFragment("fragment");
		System.out.println(m_url.toString());
		
		m_url.setFile("new_file.jsp");
		System.out.println(m_url.toString());
		
		m_url.setAbsolutePath(false);
		System.out.println(m_url.toString());
		
		m_url.setAbsolutePath(true);
		System.out.println(m_url.toString());
		
		m_url.setPath("../rel/file.jsp");
		System.out.println(m_url.toString());
		
		MuteableURL m_url2 = new MuteableURL(null);
		m_url2.setProtocol("http");
		m_url2.setHost("www.organic.com");
		m_url2.setQuery("this=that&these=those");
		System.out.println(m_url2.toString());
		
		m_url2.setFile("new_file.jsp");
		System.out.println(m_url2.toString());
		
		m_url2.setAbsolutePath(false);
		System.out.println(m_url2.toString());
	}
	
	private static boolean unitTestParseAssemble (
		String test_url
	) {
		MuteableURL m_url = new MuteableURL(test_url);
		String result = m_url.getURLString();
		System.out.println("TEST URL: " + test_url);
		System.out.println("  RESULT: " + result);
		if (test_url == null) {
			System.out.println("INVALID TEST: NULL");
			System.out.println("");
			return false;
		} else if (test_url.equals(result)) {
			System.out.println("PASSED");
			System.out.println("");
			return true;
		} else {
			System.out.println("--FAILED");
			System.out.println("");
			return false;
		}
	}
}
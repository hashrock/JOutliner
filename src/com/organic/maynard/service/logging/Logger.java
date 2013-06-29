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

package com.organic.maynard.service.logging;

import java.io.*;

public class Logger {

	public static final int FATAL_ERROR = 0;
	public static final int ERROR = 1;
	public static final int WARNING = 2;
	public static final int INFO = 3;
	public static final int DEBUG = 4;

	public static final String[] MSG_TYPE_STRING = {"Fatal Error","Error","Warning","Info","Debug"};
	
	// Configuration
	private LogFile errorLog = null;
	private LogFile infoLog = null;
	private LogFileMessageFormatter msgFormatter = new LogFileMessageFormatterImpl();

	private boolean echoToConsole = true;	
	
	// The Constructors
	public Logger() throws IOException {
		this("logs/error_log.txt","logs/info_log.txt");
	}
	
	public Logger(String errorLogPath, String infoLogPath) throws IOException {
		errorLog = new LogFile(errorLogPath);
		infoLog = new LogFile(infoLogPath);
	}

	public Logger(LogFile errorLog, LogFile infoLog) throws IOException {
		this.errorLog = errorLog;
		this.infoLog = infoLog;
	}
	
	// The Accessors
	public void setErrorLog(LogFile errorLog) {this.errorLog = errorLog;}
	public LogFile getErrorLog() {return this.errorLog;}
	
	public void setInfoLog(LogFile infoLog) {this.infoLog = infoLog;}
	public LogFile getInfoLog() {return this.infoLog;}

	public void setLogFileMessageFormatter(LogFileMessageFormatter msgFormatter) {this.msgFormatter = msgFormatter;}
	public LogFileMessageFormatter getLogFileMessageFormatter() {return this.msgFormatter;}

	public void setEcho(boolean echoToConsole) {this.echoToConsole = echoToConsole;}
	public boolean getEcho() {return this.echoToConsole;}
	
	// Logging Methods
	public void logFatalError(String msg) {log(FATAL_ERROR,msg,null);}
	public void logError(String msg) {log(ERROR,msg,null);}
	public void logWarning(String msg) {log(WARNING,msg,null);}
	public void logInfo(String msg) {log(INFO,msg,null);}
	public void logDebug(String msg) {log(DEBUG,msg,null);}

	public void logFatalError(String msg, Throwable throwable) {log(FATAL_ERROR,msg,throwable);}
	public void logError(String msg, Throwable throwable) {log(ERROR,msg,throwable);}
	public void logWarning(String msg, Throwable throwable) {log(WARNING,msg,throwable);}
	public void logInfo(String msg, Throwable throwable) {log(INFO,msg,throwable);}
	public void logDebug(String msg, Throwable throwable) {log(DEBUG,msg,throwable);}
	
	public void log(int type, String msg) {log(type,msg,false,null);}
	public void log(int type, String msg, Throwable throwable) {log(type,msg,false,throwable);}

	public void logPlain(String msg) {logPlainInfo(msg,null);}
	public void logPlainInfo(String msg) {log(INFO,msg,true,null);}
	public void logPlainError(String msg) {log(ERROR,msg,true,null);}

	public void logPlain(String msg, Throwable throwable) {logPlainInfo(msg,throwable);}
	public void logPlainInfo(String msg, Throwable throwable) {log(INFO,msg,true,throwable);}
	public void logPlainError(String msg, Throwable throwable) {log(ERROR,msg,true,throwable);}
	
	public void log(int type, String msg, boolean plain) {log(type,msg,plain,null);}

	public void log(int type, String msg, boolean plain, Throwable throwable) {
		String logMsg;
		String consoleMsg;
		
		if (plain || (msgFormatter == null)) {
			logMsg = msg;
			consoleMsg = msg;		
		} else {
			logMsg = msgFormatter.prepareLogMessage(type,msg,throwable);
			consoleMsg = msgFormatter.prepareConsoleMessage(type,msg,throwable);
		}
		
		if ((type == INFO) || (type == DEBUG)) {
			infoLog.write(logMsg);
		} else if ((type == WARNING) || (type == ERROR) || (type == FATAL_ERROR)) {
			errorLog.write(logMsg);	
		}
		
		if (echoToConsole) {
			System.out.println(consoleMsg);
		}
	}
}
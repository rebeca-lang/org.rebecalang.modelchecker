package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProgramCounter implements Serializable {
	private String methodName;
	private int lineNumber;
	
	public ProgramCounter() {}
	
	public ProgramCounter(String methodName, int lineNumber) {
		this.methodName = methodName;
		this.lineNumber = lineNumber;
	}

	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	
}

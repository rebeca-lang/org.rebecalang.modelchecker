package org.rebecalang.transparentactormodelchecker.abstractrebeca;

import java.util.HashMap;

public class MethodLookup {
	private HashMap<String, String> methodLookupTable;

	public MethodLookup() {
		methodLookupTable = new HashMap<String, String>();
	}
	
	public void addMethod(String methodName, String resolvedName) {
		methodLookupTable.put(methodName, resolvedName);
	}
	
	public String resolveName(String methodName) {
		return methodLookupTable.get(methodName);
	}
}

package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Environment implements Serializable {

	private HashMap<String, Object> envVars;
	
	public Environment() {
		envVars = new HashMap<String, Object>();
	}
	
	public void setVariableValue(String varName, Object value) {
		envVars.put(varName, value);
	}
	
	public Object getVariableValue(String varName) {
		return envVars.get(varName);
	}

	public boolean hasVariableInScope(String varName) {
		return envVars.containsKey(varName);
	}
	
//	public String toString() {
//		String retValue =  "{";
//		for(Entry<String, Object> entry : envVars.entrySet()) {
//			retValue += entry.getKey() + ":" + entry.getValue();
//		}
//		return retValue + "}";
//	}

}

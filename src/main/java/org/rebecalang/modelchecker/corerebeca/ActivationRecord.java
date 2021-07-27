package org.rebecalang.modelchecker.corerebeca;

import java.io.Serializable;
import java.util.Hashtable;

@SuppressWarnings("serial")
public class ActivationRecord implements Serializable {

	private Hashtable<String, Object> definedVariables;
	private ActivationRecord previousScope;

	public void setVariableValue(String name, Object value) {
		definedVariables.put(name, value);
	}

	public Object getVariableValue(String name) {
		return definedVariables.get(name);
	}

	public void addVariable(String name, Object valueObject) {
		definedVariables.put(name, valueObject);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((definedVariables == null) ? 0 : definedVariables.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivationRecord other = (ActivationRecord) obj;
		if (definedVariables == null) {
			if (other.definedVariables != null)
				return false;
		} else if (!definedVariables.equals(other.definedVariables))
			return false;
		return true;
	}

	public void initialize() {
		definedVariables = new Hashtable<String, Object>();
	}

	public void remove(String varName) {
		definedVariables.remove(varName);
	}

	public ActivationRecord getPreviousScope() {
		return previousScope;
	}

	public void setPreviousScope(ActivationRecord previousScope) {
		this.previousScope = previousScope;
	}

	public boolean hasVariable(String varName) {
		return definedVariables.containsKey(varName);
	}

}

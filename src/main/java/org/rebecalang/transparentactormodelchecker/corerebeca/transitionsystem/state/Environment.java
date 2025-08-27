package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.rebecalang.transparentactormodelchecker.corerebeca.utils.CloningRepository;

@SuppressWarnings("serial")
public class Environment extends CoreRebecaAbstractState {

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
	
	public Set<Entry<String, Object>> getAllVariblesEntrySet() {
		return envVars.entrySet();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((envVars == null) ? 0 : envVars.hashCode());
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
		Environment other = (Environment) obj;
		if (envVars == null) {
			if (other.envVars != null)
				return false;
		} else if (!envVars.equals(other.envVars))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return envVars.toString();
	}
	
	public Environment clone() {
		return CloningRepository.cloneEnvironment(this);
	}
}

package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

public class ActivationRecord implements Cloneable {
	
	protected HashMap<String, Object> activationRecord;
	
	public ActivationRecord() {
		activationRecord = new HashMap<String, Object>();
	}
	
	public HashMap<String, Object> getActivationRecord() {
		return activationRecord;
	}
	
	public void addVariableToActivationRecord(String varName, Object value) {
		activationRecord.put(varName, value);
	}
	
	public boolean containsVariable(String varName) {
		return activationRecord.containsKey(varName);
	}

	public Object getVariableValue(String varName) {
		return activationRecord.get(varName);
	}

	public void setVariableValue(String varName, Object value) {
		activationRecord.put(varName, value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activationRecord == null) ? 0 : activationRecord.hashCode());
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
		if (activationRecord == null) {
			if (other.activationRecord != null)
				return false;
		} else if (!activationRecord.equals(other.activationRecord))
			return false;
		return true;
	}

	@Override
	public ActivationRecord clone() {
		ActivationRecord clonedAR = new ActivationRecord();
		for(Entry<String, Object> entry : this.activationRecord.entrySet()) {
			clonedAR.getActivationRecord().put(entry.getKey(), 
					CloningRepository.cloneObject(entry.getValue()));
		}
		return clonedAR;
	}
	
	@Override
	public String toString() {
		return "ac=[data:" + activationRecord.toString() + "]";
	}
	
}
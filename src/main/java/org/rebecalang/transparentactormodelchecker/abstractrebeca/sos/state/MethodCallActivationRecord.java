package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.util.Map.Entry;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

public class MethodCallActivationRecord extends ActivationRecord {
	private int scopeIndex;
	
	public MethodCallActivationRecord(int scopeIndex) {
		super();
		this.scopeIndex = scopeIndex;
	}
	
	public void setScopeIndex(int scopeIndex) {
		this.scopeIndex = scopeIndex;
	}
	
	public int getScopeIndex() {
		return scopeIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + scopeIndex;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodCallActivationRecord other = (MethodCallActivationRecord) obj;
		if (scopeIndex != other.scopeIndex)
			return false;
		return true;
	}
	
	@Override
	public MethodCallActivationRecord clone() {
		MethodCallActivationRecord clonedAR = new MethodCallActivationRecord(this.scopeIndex);
		for(Entry<String, Object> entry : this.activationRecord.entrySet()) {
			clonedAR.getActivationRecord().put(entry.getKey(), 
					CloningRepository.cloneObject(entry.getValue()));
		}
		return clonedAR;
	}
	
	@Override
	public String toString() {
		return "mac=[prev:" + scopeIndex+ "][data:" + activationRecord.toString() + "]";
	}

}

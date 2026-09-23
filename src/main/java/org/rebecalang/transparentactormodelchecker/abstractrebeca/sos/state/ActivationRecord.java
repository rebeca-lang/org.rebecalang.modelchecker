package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
	
//	public void addVariableToActivationRecord(String varName, Object value) {
//		if(value instanceof AbstractActorState) {
//			AbstractActorStateRepresentor aasr = 
//					new AbstractActorStateRepresentor(((AbstractActorState)value).getId());
//			activationRecord.put(varName, aasr);
//		} else
//			activationRecord.put(varName, value);
//	}
	
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
//		result = prime * result + ((activationRecord == null) ? 0 : activationRecord.hashCode());
		for (Entry<String, Object> entry : activationRecord.entrySet()) {
			Object value = entry.getValue();
			// equals() treats a null value as a value in its own right, so hashing has
			// to accept one too rather than dereferencing it
			if(value == null)
				result += entry.getKey().hashCode();
			else if(value.getClass().isArray())
				result += entry.getKey().hashCode() ^ Arrays.deepHashCode((Object[]) value);
			else
				result += entry.getKey().hashCode() ^ value.hashCode();
		}
//		System.out.println("\t" + result);
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
		} else {// if (!activationRecord.equals(other.activationRecord)) {
            for (Entry<String, Object> e : activationRecord.entrySet()) {
                String key = e.getKey();
                Object value = e.getValue();
                if (value == null) {
                    if (!(other.activationRecord.get(key) == null && 
                    		other.activationRecord.containsKey(key)))
                        return false;
                } else {
                	Object otherValue = other.activationRecord.get(key);
                	if(value.getClass().isArray()) {
                		if(!otherValue.getClass().isArray())
                			return false;
                		if(!Arrays.deepEquals((Object[])value, (Object[])other.activationRecord.get(key)))
                			return false;
                	} else if (!value.equals(other.activationRecord.get(key)))
                        return false;
                }
            }
		}
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
		StringBuilder result = new StringBuilder("ac={");
		for (Entry<String, Object> entry : activationRecord.entrySet()) {
			result.append(entry.getKey());
			result.append("=");
			objectValue(entry.getValue(), result);
			result.append(", ");
		}
		result.append("}");
		return result.toString();
	}

	private void objectValue(Object value, StringBuilder result) {
		if(value == null)
			result.append("null");
		else if(value instanceof Map) {
			Map<?, ?> valueMap = (Map<?, ?>) value;
			result.append("{");
			for (Object key : valueMap.keySet()) {
				result.append(key + "=");
				objectValue(valueMap.get(key), result);
				result.append(", ");
			}
			result.append("}");
		} else if(value.getClass().isArray()) {
			result.append("[");
			int length = Array.getLength(value);
			for(int cnt = 0; cnt < length; cnt++) {
				objectValue(Array.get(value, cnt), result);
				result.append(", ");
			}
			result.append("]");			
		} else {
			result.append(value);
		}
	}
}

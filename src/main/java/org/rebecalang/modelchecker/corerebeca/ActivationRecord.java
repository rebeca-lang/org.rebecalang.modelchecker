package org.rebecalang.modelchecker.corerebeca;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class ActivationRecord implements Serializable {

    private HashMap<String, Object> definedVariables;
    private ActivationRecord previousScope;
    private String relatedRebecType;

    public ActivationRecord() {
    	definedVariables = new HashMap<String, Object>();
    }
    
    public void setVariableValue(String name, Object value) {
        definedVariables.put(name, value);
    }

    public Object getVariableValue(String name) {
        return definedVariables.get(name);
    }

    public boolean contains(String name) {
        return definedVariables.containsKey(name);
    }

    public void addVariable(String name, Object valueObject) {
        definedVariables.put(name, valueObject);
    }


    @SuppressWarnings("rawtypes")
	@Override
    /**
     * There is no need to consider the values of previousScope
     * and relatedRebecType. Equal values for variables of the
     * scope (including the program counter) results in having
     * the same values for those two.
     */
	public int hashCode() {
        final int prime = 31;
        int result = 1;
        int h = 0;
        for (Entry<String, Object> entry : definedVariables.entrySet()) {
            Object value = entry.getValue();
            String key = entry.getKey();

            if (key.equals("resuming_time") || key.equals("current_time")) continue;

            if (value instanceof BaseActorState)
                h+= key.hashCode() ^ ((BaseActorState)value).getName().hashCode();
            else
                h += key.hashCode() ^ (value == null ? 0 : value.hashCode());
        }
        
        result = prime * result + h;
        
        return result;
    }    

    @SuppressWarnings("rawtypes")
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
            return other.definedVariables == null;
        } else {
        	if (definedVariables.size() != other.definedVariables.size())
        		return false;
        	for (Map.Entry<String, Object> entry : definedVariables.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value == null) {
                    if (!(other.definedVariables.get(key) == null && other.definedVariables.containsKey(key)))
                        return false;
                } else {
                	if (value instanceof BaseActorState<?>) {
                		if (!((BaseActorState)value).getName().equals(((BaseActorState)other.definedVariables.get(key)).getName()))
                			return false;
                	} else {
                		if (!value.equals(other.definedVariables.get(key)))
                			return false;
                	}
                }
        	}
        	return true;
        }
    }

    public void initialize() {
        definedVariables = new HashMap<String, Object>();
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

    public String getRelatedRebecType() {
        return relatedRebecType;
    }

    public void setRelatedRebecType(String relatedRebecType) {
        this.relatedRebecType = relatedRebecType;
    }
    
	public void export(PrintStream output) {
		for(String key : definedVariables.keySet()) {
			output.print("<variable name=\"" + key + "\" type=\"\"" + "\">");
			output.print(definedVariables.get(key));
			output.println("</variable>");
		}
	}

	public String toString() {
		String retValue = "(";
		for(String key : definedVariables.keySet()) {
			Object value = definedVariables.get(key);
			retValue += key + "->";
			retValue += (value instanceof ActorState) ? 
					((ActorState)value).getName(): 
					value;
			retValue += ",";
		}
		return retValue + ")";
	}
}
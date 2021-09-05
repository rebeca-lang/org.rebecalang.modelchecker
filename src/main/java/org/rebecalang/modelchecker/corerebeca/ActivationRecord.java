package org.rebecalang.modelchecker.corerebeca;

import java.io.Serializable;
import java.util.Hashtable;

@SuppressWarnings("serial")
public class ActivationRecord implements Serializable {

    private Hashtable<String, Object> definedVariables;
    private ActivationRecord previousScope;
    private String relatedRebecType;

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
            return other.definedVariables == null;
        } else return definedVariables.equals(other.definedVariables);
    }

    public void initialize() {
        definedVariables = new Hashtable<>();
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
}

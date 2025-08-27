package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.CloningRepository;

@SuppressWarnings("serial")
public class CoreRebecaActorScope implements Serializable, Cloneable {
	private Environment environment;
	private ArrayList<HashMap<String, Object>> scope;
	
	public CoreRebecaActorScope() {
		scope = new ArrayList<HashMap<String,Object>>();
		scope.add(new HashMap<String, Object>());
	}
	
	public void setVariableValue(Variable leftVar, Object value) {
		String variableInScope = leftVar.getVarName();
		boolean hasBase = leftVar.getBase() != null;
		if(hasBase)
			variableInScope = leftVar.getBase().getVarName();
		for(int cnt = 0; cnt < scope.size(); cnt++) {
			if(!scope.get(cnt).containsKey(variableInScope))
				continue;
			if(hasBase) {
				Variable variable = new Variable(leftVar.getVarName());
				((CoreRebecaActorState)scope.get(cnt).get(variableInScope)).setVariableValue(variable, value);
			} else
				scope.get(cnt).put(variableInScope, value);
			return;
		}
		throw new RebecaRuntimeInterpreterException("variable \"" + leftVar + "\" not found");
	}

	public void addVariableToScope(String varName, Object value) {
		scope.get(scope.size() - 1).put(varName, value);
	}


	public Object getVariableValue(String varName) {
		for(int cnt = scope.size(); cnt > 0; cnt--) {
			if(scope.get(cnt - 1).containsKey(varName))
				return scope.get(cnt - 1).get(varName);
		}
		return environment == null ? null : environment.getVariableValue(varName);
	}

	public boolean hasVariableInScope(String varName) {
		for(int cnt = 0; cnt < scope.size(); cnt++) {
			if(scope.get(cnt).containsKey(varName))
				return true;
		}
		return environment == null ? null : environment.hasVariableInScope(varName);
	}

	public void addVariableToScope(String varName) {
		addVariableToScope(varName, null);
	}

	public void pushToScope() {
		scope.add(new HashMap<String, Object>());
	}
	
	public void popFromScope() {
		scope.remove(scope.size() - 1);
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((environment == null) ? 0 : environment.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
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
		CoreRebecaActorScope other = (CoreRebecaActorScope) obj;
		if (environment == null) {
			if (other.environment != null)
				return false;
		} else if (!environment.equals(other.environment))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return scope.toString();
	}
	
	public CoreRebecaActorScope clone() {
		CoreRebecaActorScope clonedActorScope = new CoreRebecaActorScope();
		clonedActorScope.environment = CloningRepository.cloneEnvironment(this.environment);
		clonedActorScope.scope = new ArrayList<HashMap<String,Object>>();
		for(HashMap<String,Object> ar : this.scope) {
			HashMap<String,Object> clonedAR = new HashMap<String, Object>();
			for(Entry<String, Object> entry : ar.entrySet()) {
				clonedAR.put(entry.getKey(), CloningRepository.cloneObject(entry.getValue()));
			}
			clonedActorScope.scope.add(clonedAR);
		}
		return clonedActorScope;
	}
	
}

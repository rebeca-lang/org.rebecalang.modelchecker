package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

@SuppressWarnings("serial")
public class CoreRebecaActorScope implements Serializable {
	private Environment environment;
	private ArrayList<HashMap<String, Object>> scope;
	
	public CoreRebecaActorScope() {
		scope = new ArrayList<HashMap<String,Object>>();
		scope.add(new HashMap<String, Object>());
	}
	
	public void setVariableValue(Variable leftVarName, Object value) {
		for(int cnt = 0; cnt < scope.size(); cnt++) {
			if(!scope.get(cnt).containsKey(leftVarName.getVarName()))
				continue;
			scope.get(cnt).put(leftVarName.getVarName(), value);
			return;
		}
		throw new RebecaRuntimeInterpreterException("variable \"" + leftVarName + "\" not found");
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


}

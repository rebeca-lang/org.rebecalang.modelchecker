package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

@SuppressWarnings("serial")
public class ActorScope implements Serializable, Cloneable {
	
	public final static String RETURN_VALUE_VARIABLE_NAME = "$RETURN$";
	
	private ArrayList<ActivationRecord> scope;
	
	public ActorScope() {
		scope = new ArrayList<ActivationRecord>();
		scope.add(null);
		scope.add(new ActivationRecord());
	}
	
	public void setEnvironment(ActivationRecord environment) {
		scope.set(0, environment);
	}

	public void addVariableToScope(String varName, Object value) {
		scope.get(scope.size() - 1).addVariableToActivationRecord(varName, value);
	}
	
	private int getIndexValue(Object indexObject) {
		if (indexObject instanceof Variable) 
			return ((Number)getVariableValue((Variable) indexObject)).intValue();
		return ((Number)indexObject).intValue();
	}
	
	private ActivationRecord getTargetVariableActivationRecord(Variable var) {
		boolean hasBase = var.getBase() != null;
		if(hasBase) {
			ActivationRecord baseActivationRecord = 
					getTargetVariableActivationRecord(var.getBase());
			AbstractActorState baseActorState = 
					(AbstractActorState)baseActivationRecord.getVariableValue(
							var.getBase().getVarName());
			return baseActorState.getScope().getTargetVariableActivationRecord(
					new Variable(var.getVarName()));
		}
		int index = scope.size() - 1;
		do {
			ActivationRecord cursor = scope.get(index);
			if(cursor.containsVariable(var.getVarName()))
				return cursor;
			if(cursor instanceof MethodCallActivationRecord) {
				index = ((MethodCallActivationRecord)cursor).getScopeIndex();
			} else 
				index--;
		} while(index >= 0);
		throw new RebecaRuntimeInterpreterException("variable \"" + var + "\" not found");
	}
	
	public void setVariableValue(Variable var, Object value) {
		
		ActivationRecord activationRecord = 
				getTargetVariableActivationRecord(var);
		
		if(var.getIndeces().isEmpty())
			activationRecord.setVariableValue(var.getVarName(), value);
		else {
			Object object = activationRecord.getVariableValue(var.getVarName());
			int cnt = 0;
			for(; cnt < var.getIndeces().size() - 1; cnt++) {
				Object index = var.getIndeces().get(cnt);
				object = Array.get(object, getIndexValue(index));
			}
			Array.set(object, getIndexValue(var.getIndeces().get(cnt)), value);
		}
	}

	public Object getVariableValue(Variable var) {
		ActivationRecord activationRecord = 
				getTargetVariableActivationRecord(var);

		if(var.getIndeces().isEmpty())
			return activationRecord.getVariableValue(var.getVarName());
		else {
			Object object = activationRecord.getVariableValue(var.getVarName());
			int cnt = 0;
			for(; cnt < var.getIndeces().size() - 1; cnt++) {
				Object index = var.getIndeces().get(cnt);
				object = Array.get(object, getIndexValue(index));
			}
			return Array.get(object, getIndexValue(var.getIndeces().get(cnt)));
		}
	}
	
	public boolean hasVariableInScope(String varName) {
		int index = scope.size() - 1;
		do {
			ActivationRecord cursor = scope.get(index);
			if(cursor.containsVariable(varName))
				return true;
			if(cursor instanceof MethodCallActivationRecord) {
				index = ((MethodCallActivationRecord)cursor).getScopeIndex();
			} else 
				index--;
		} while(index >= 0);
		return false;
	}

	public void pushToScope() {
		scope.add(new ActivationRecord());
	}
	
	public void newCallPushToScope(Variable returnValueVariable) {
		scope.add(new MethodCallActivationRecord(1));
		if(returnValueVariable != null)
			addVariableToScope(RETURN_VALUE_VARIABLE_NAME, returnValueVariable.getVarName());
	}

	public void popFromScope() {
		scope.remove(scope.size() - 1);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
//		result = prime * result + ((environment == null) ? 0 : environment.hashCode());
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
		ActorScope other = (ActorScope) obj;
//		if (environment == null) {
//			if (other.environment != null)
//				return false;
//		} else if (!environment.equals(other.environment))
//			return false;
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
	
	public ActorScope clone() {
		ActorScope clonedActorScope = new ActorScope();
		clonedActorScope.scope = new ArrayList<ActivationRecord>();
		clonedActorScope.scope.add(CloningRepository.cloneEnvironment(this.scope.get(0)));
		for(int cnt = 1; cnt < this.scope.size(); cnt++) {
			ActivationRecord ar = this.scope.get(cnt);
			ActivationRecord clonedAR = ar.clone();
			clonedActorScope.scope.add(clonedAR);
		}
		return clonedActorScope;
	}

	public void popToReturn(Object value) {
		MethodCallActivationRecord mcar;
		for(int cnt = scope.size() - 1; cnt > 0 ; cnt--)
			if(scope.get(cnt) instanceof MethodCallActivationRecord) {
				mcar = (MethodCallActivationRecord) scope.remove(cnt);
				String returnResultValue = (String) mcar.getVariableValue(RETURN_VALUE_VARIABLE_NAME);
				setVariableValue(new Variable(returnResultValue), value);
				break;				
			} else
				scope.remove(cnt);
		
	}
}
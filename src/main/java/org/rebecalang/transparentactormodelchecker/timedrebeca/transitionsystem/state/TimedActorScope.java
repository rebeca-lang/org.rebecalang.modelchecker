package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorScope;

@SuppressWarnings("serial")
public class TimedActorScope extends ActorScope {
	public static final Variable TIME_VARIABLE_NAME = new Variable("time");

	private int time;
	
	public TimedActorScope() {
		super();
		this.time = 0;
	}
	
	@Override
	public void setVariableValue(Variable leftVar, Object value) {
		if(leftVar.getVarName().equals(TIME_VARIABLE_NAME) && leftVar.getBase() == null)
			time = (int) value;
		else
			super.setVariableValue(leftVar, value);
	}
	
//	@Override
//	public Integer getVariableValue(String varName) {
//		return time;
//	}
	
	@Override
	public TimedActorScope clone() {
		TimedActorScope cloned = (TimedActorScope) super.clone();
		cloned.time = this.time;
		return cloned;
	}
}

package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorScope;

@SuppressWarnings("serial")
public class TimedActorScope extends ActorScope {
	public static final String TIME_VARIABLE_NAME = "now";
	public static final Variable TIME_VARIABLE = new Variable(TIME_VARIABLE_NAME);

	private int time;
	
	public TimedActorScope() {
		super();
		this.time = 0;
	}
	
	@Override
	public void setVariableValue(Variable var, Object value) {
		if(var.getVarName().equals(TIME_VARIABLE_NAME) && var.getBase() == null)
			time = (int) value;
		else
			super.setVariableValue(var, value);
	}

	@Override
	public Object getVariableValue(Variable var) {
		if(var.getVarName().equals(TIME_VARIABLE_NAME) && var.getBase() == null)
			return time;
		else
			return super.getVariableValue(var);
	}

	@Override
	public TimedActorScope clone() {
		TimedActorScope cloned = (TimedActorScope) super.clone();
		cloned.time = this.time;
		return cloned;
	}
	
	@Override
	protected ActorScope newActorScope() {
		return new TimedActorScope();
	}
	
	@Override
	public String toString() {
		return "now:" + time + ", " + super.toString();
	}
}

package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state;

import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.FALSE;
import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.INF;

import java.io.Serializable;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;

@SuppressWarnings("serial")
public class TimedRebecaSystemState extends AbstractSystemState implements Serializable, Cloneable {
	
	protected TimedRebecaNetworkState networkState;

	public TimedRebecaSystemState() {
		super();
		networkState = new TimedRebecaNetworkState();
	}
	
	public int getEnablingTime() throws RuleIsDisabledException {
		int executionTime = Integer.MAX_VALUE;
		for(int actorId : this.getActorsIds()) {
			executionTime = Math.min(executionTime, 
					getEnablingTime((TimedRebecaActorState)this.getActorState(actorId)));
		}
		if(executionTime == Integer.MAX_VALUE)
			throw new RuleIsDisabledException();
		return executionTime;
	}
	
	public int getEnablingTime(TimedRebecaActorState source) {
		if(source.bagIsEmpty() && !source.hasPC())
			return Integer.MAX_VALUE;
		int now = (int) source.getVariableValue(TimedActorScope.TIME_VARIABLE);
		if(!source.hasPC())
			return Math.max(source.getFirstMessageArrivalTime(), now);
		else 
			return now;
	}


	public Pair<Boolean, Integer> shiftEquals(Object obj) {
		if(!super.equals(obj))
			return FALSE;
		TimedRebecaSystemState other = (TimedRebecaSystemState) obj;
		int shift = INF;
		for(Integer id : other.getActorsIds()) {
			TimedRebecaActorState thisActorState = (TimedRebecaActorState) this.getActorState(id);
			TimedRebecaActorState otherActorState = (TimedRebecaActorState) other.getActorState(id);
			Pair<Boolean, Integer> result = 
					thisActorState.shiftEquals(otherActorState);
			if(!result.getFirst())
				return FALSE;
			if(shift == INF)
				shift = result.getSecond();
			if(shift != result.getSecond())
				return FALSE;
		}
		return new Pair<Boolean, Integer>(true, shift);
	}
	
	public TimedRebecaSystemState clone() {
		TimedRebecaSystemState clonedState = new TimedRebecaSystemState();
		clone(clonedState);
		clonedState.networkState = networkState.clone();
		return clonedState;
	}
	
	public TimedRebecaNetworkState getNetworkState() {
		return networkState;
	}

	public void setNetworkState(TimedRebecaNetworkState networkState) {
		this.networkState = networkState;
	}
	
	public String toString() {
		String result = super.toString();
		result = result.substring(0, result.length() - 1) +  "net:" + networkState + "\n}";
		return result;
	}
}

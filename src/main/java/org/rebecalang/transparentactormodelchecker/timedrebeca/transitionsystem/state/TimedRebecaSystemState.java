package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state;

import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.FALSE;
import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.INF;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

@SuppressWarnings("serial")
public class TimedRebecaSystemState extends AbstractSystemState implements Serializable, Cloneable {
	
	protected TimedRebecaNetworkState networkState;

	public TimedRebecaSystemState() {
		super();
		networkState = new TimedRebecaNetworkState();
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
		CloningRepository.resetRepository();
		TimedRebecaSystemState clonedState = new TimedRebecaSystemState();
		clonedState.environment = environment.clone();
		clonedState.networkState = networkState.clone();
		clonedState.actorsState = (HashMap<Integer, AbstractActorState>) new HashMap<Integer, AbstractActorState>();
		for(Entry<Integer, AbstractActorState> entry : actorsState.entrySet()) {
			clonedState.actorsState.put(entry.getKey(), entry.getValue().clone());
		}
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

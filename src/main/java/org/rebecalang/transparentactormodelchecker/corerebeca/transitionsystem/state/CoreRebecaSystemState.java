package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

@SuppressWarnings("serial")
public class CoreRebecaSystemState extends AbstractSystemState implements Serializable, Cloneable {
	
	protected CoreRebecaNetworkState networkState;

	public CoreRebecaSystemState() {
		super();
		networkState = new CoreRebecaNetworkState();
	}
	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//
////		int result = 1;
////		result = prime * result + ((actorsState == null) ? 0 : actorsState.hashCode());
//		int result = prime;
//		for(Entry<Integer, AbstractActorState> entry : actorsState.entrySet()) {
//			result += entry.getKey().hashCode() ^ entry.getValue().deepHashCode();
//		}
//		
//		result = prime * result + ((environment == null) ? 0 : environment.hashCode());
//		result = prime * result + ((networkState == null) ? 0 : networkState.hashCode());
//		return result;
//	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CoreRebecaSystemState other = (CoreRebecaSystemState) obj;
		if (actorsState == null) {
			if (other.actorsState != null)
				return false;
		} else {
	        if (actorsState.size() != other.actorsState.size())
	            return false;
            for (Entry<Integer, AbstractActorState> entry : actorsState.entrySet()) {
                Integer key = entry.getKey();
                AbstractActorState value = entry.getValue();
                if (value == null) {
                    if (!(other.actorsState.get(key) == null && other.actorsState.containsKey(key)))
                        return false;
                } else {
                    if (!value.deepEquals(other.actorsState.get(key)))
                        return false;
                }
            }
		}
			
		if (environment == null) {
			if (other.environment != null)
				return false;
		} else if (!environment.equals(other.environment))
			return false;
		if (networkState == null) {
			if (other.networkState != null)
				return false;
		} else if (!networkState.equals(other.networkState))
			return false;
		return true;
	}
	
	public CoreRebecaSystemState clone() {
		CloningRepository.resetRepository();
		CoreRebecaSystemState clonedState = new CoreRebecaSystemState();
		clonedState.environment = environment.clone();
		clonedState.networkState = networkState.clone();
		clonedState.actorsState = (HashMap<Integer, AbstractActorState>) new HashMap<Integer, AbstractActorState>();
		for(Entry<Integer, AbstractActorState> entry : actorsState.entrySet()) {
			clonedState.actorsState.put(entry.getKey(), entry.getValue().clone());
		}
		return clonedState;
	}
	
	@Override
	public CoreRebecaNetworkState getNetworkState() {
		return networkState;
	}

	public void setNetworkState(CoreRebecaNetworkState networkState) {
		this.networkState = networkState;
	}
	
	public String toString() {
		String result = super.toString();
		result = result.substring(0, result.length() - 1) +  "net:" + networkState + "\n}";
		return result;
	}
}

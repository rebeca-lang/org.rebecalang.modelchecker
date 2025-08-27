package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.rebecalang.transparentactormodelchecker.corerebeca.utils.CloningRepository;

@SuppressWarnings("serial")
public class CoreRebecaSystemState extends CoreRebecaAbstractState implements Cloneable {
	
	private volatile Environment environment;
	private HashMap<Integer, CoreRebecaActorState> actorsState;
	private CoreRebecaNetworkState networkState;

	public CoreRebecaSystemState() {
		actorsState = new HashMap<Integer, CoreRebecaActorState>();
		networkState = new CoreRebecaNetworkState();
	}
	
	public Collection<CoreRebecaActorState> getActorsStatesValues() {
		return actorsState.values();
	}
	
	public Set<Integer> getActorsIds() {
		return actorsState.keySet();
	}
	
	public HashMap<Integer, CoreRebecaActorState> getActorsState() {
		return actorsState;
	}
	
	public void setActorState(int id, CoreRebecaActorState newState) {
		newState.setEnvironment(environment);
		actorsState.put(id, newState);
	}

	public void setActorState(CoreRebecaActorState newState) {
		newState.setEnvironment(environment);
		actorsState.put(newState.getId(), newState);
	}
	
	public void destroyActorState(CoreRebecaActorState newState) {
		actorsState.remove(newState.getId());
	}
	
	public void addNewActorState(CoreRebecaActorState newState) {
		Integer max = Collections.max(getActorsIds());
		newState.setId(max + 1);
		setActorState(newState);
	}

	public CoreRebecaActorState getActorState(int id) {
		return actorsState.get(id);
	}

	public CoreRebecaNetworkState getNetworkState() {
		return networkState;
	}
	
	public void setNetworkState(CoreRebecaNetworkState networkState) {
		this.networkState = networkState;
	}
	
	public void setEnvironment(Environment environment) {
		this.environment = environment;
		for(CoreRebecaActorState actorState : actorsState.values())
			actorState.setEnvironment(environment);
	}
	
	public String toString() {
		String result = "{\nEnv:" + environment + "|\n";
		for(CoreRebecaActorState actorState : actorsState.values())
			result += actorState.deepToString() + "|\n";
		result += "net:" + networkState + "\n}";
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;

//		int result = 1;
//		result = prime * result + ((actorsState == null) ? 0 : actorsState.hashCode());
		int result = prime;
		for(Entry<Integer, CoreRebecaActorState> entry : actorsState.entrySet()) {
			result += entry.getKey().hashCode() ^ entry.getValue().deepHashCode();
		}
		
		result = prime * result + ((environment == null) ? 0 : environment.hashCode());
		result = prime * result + ((networkState == null) ? 0 : networkState.hashCode());
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
		CoreRebecaSystemState other = (CoreRebecaSystemState) obj;
		if (actorsState == null) {
			if (other.actorsState != null)
				return false;
		} else {
	        if (actorsState.size() != other.actorsState.size())
	            return false;
            for (Entry<Integer, CoreRebecaActorState> entry : actorsState.entrySet()) {
                Integer key = entry.getKey();
                CoreRebecaActorState value = entry.getValue();
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
		CoreRebecaSystemState clonedState = new CoreRebecaSystemState();
		CloningRepository.resetRepository();
		clonedState.environment = environment.clone();
		clonedState.networkState = networkState.clone();
		clonedState.actorsState = new HashMap<Integer, CoreRebecaActorState>();
		for(Entry<Integer, CoreRebecaActorState> entry : actorsState.entrySet()) {
			clonedState.actorsState.put(entry.getKey(), entry.getValue().clone());
		}
		return clonedState;
	}
}

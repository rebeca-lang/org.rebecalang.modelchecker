package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;


@SuppressWarnings("serial")
public abstract class AbstractSystemState implements Serializable, Cloneable {

	protected volatile ActivationRecord environment;
	protected HashMap<Integer, AbstractActorState> actorsState;

	public AbstractSystemState() {
		actorsState = new HashMap<Integer, AbstractActorState>();
	}
	
	public Collection<? extends AbstractActorState> getActorsStatesValues() {
		return actorsState.values();
	}
	
	public Set<Integer> getActorsIds() {
		return actorsState.keySet();
	}
	
	public HashMap<Integer, ? extends AbstractActorState> getActorsState() {
		return actorsState;
	}
	
	public void setActorState(int id, AbstractActorState newState) {
		newState.setEnvironment(environment);
		actorsState.put(id, newState);
	}

	public void setActorState(AbstractActorState newState) {
		newState.setEnvironment(environment);
		actorsState.put(newState.getId(), newState);
	}
	
	public void destroyActorState(AbstractActorState newState) {
		actorsState.remove(newState.getId());
	}
	
	public void addNewActorState(AbstractActorState newState) {
		Integer max = Collections.max(getActorsIds());
		newState.setId(max + 1);
		setActorState(newState);
	}

	public AbstractActorState getActorState(int id) {
		return actorsState.get(id);
	}

	public void setEnvironment(ActivationRecord environment) {
		this.environment = environment;
		for(AbstractActorState actorState : actorsState.values())
			actorState.setEnvironment(environment);
	}
	
	public String toString() {
		String result = "{\nEnv:" + environment + "|\n";
		for(AbstractActorState actorState : actorsState.values())
			result += actorState.deepToString() + "|\n";
		result += "}";
		return result;
	}
	
	public int hashCode() {

		/**
		 * The following code is the unfold version of:
		 * <code>
		 * int result = 1;
		 * result = prime * result + ((actorsState == null) ? 0 : actorsState.hashCode());
		 * </code>
		 */
		final int prime = 31;
		int result = prime;
		for(Entry<Integer, AbstractActorState> entry : actorsState.entrySet()) {
			result += entry.getKey().hashCode() ^ entry.getValue().deepHashCode();
		}
		
		AbstractNetworkState networkState = getNetworkState();
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
		AbstractSystemState other = (AbstractSystemState) obj;
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

		AbstractNetworkState networkState = getNetworkState();
		if (networkState == null) {
			if (other.getNetworkState() != null)
				return false;
		} else if (!networkState.equals(other.getNetworkState()))
			return false;
		return true;
	}
	
	public abstract AbstractNetworkState getNetworkState();
	
	public abstract AbstractSystemState clone();
}

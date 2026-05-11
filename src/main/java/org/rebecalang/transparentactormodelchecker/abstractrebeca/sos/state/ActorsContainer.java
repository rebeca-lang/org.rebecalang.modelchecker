package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class ActorsContainer implements Serializable {
	HashMap<Integer, AbstractActorState> actorsState;
	
	public ActorsContainer() {
		actorsState = new HashMap<Integer, AbstractActorState>();
	}

	public Collection<? extends AbstractActorState> getActorsStates() {
		return actorsState.values();
	}

	public Set<Integer> getActorsIds() {
		return actorsState.keySet();
	}

	public void setActor(int id, AbstractActorState newState) {
		actorsState.put(id, newState);
	}
	
	public void destroyActorState(int id) {
		actorsState.remove(id);
	}

	public void setEnvironment(ActivationRecord environment) {
		for(AbstractActorState actorState : actorsState.values())
			actorState.setEnvironment(environment);
	}
	
	@Override
	public String toString() {
		String result = "";
		for(AbstractActorState actorState : actorsState.values())
			result += actorState.toString() + ", ";
		return result;
	}

	public String deepToString() {
		String result = "";
		for(AbstractActorState actorState : actorsState.values())
			result += actorState.deepToString() + "\n";
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime;
//		for(Entry<Integer, AbstractActorState> entry : actorsState.entrySet()) {
//			result += entry.getKey().hashCode() ^ entry.getValue().deepHashCode();
//			System.out.println(entry.getValue().deepHashCode());
//		}
//		System.out.println("..............");
		result = prime * result + ((actorsState == null) ? 0 : actorsState.hashCode());
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
		ActorsContainer other = (ActorsContainer) obj;

		if (actorsState == null && other.actorsState == null)
			return true;
		if (actorsState == null || other.actorsState == null)
			return false;
		
        if (actorsState.size() != other.actorsState.size())
            return false;
        for (Entry<Integer, AbstractActorState> entry : actorsState.entrySet()) {
            Integer key = entry.getKey();
            AbstractActorState value = entry.getValue();
            if (!value.deepEquals(other.actorsState.get(key)))
                return false;
//            if (value == null) {
//                if (!(other.actorsState.get(key) == null && other.actorsState.containsKey(key)))
//                    return false;
//            } else {
//                if (!value.deepEquals(other.actorsState.get(key)))
//                    return false;
//            }
        }
		return true;
	}

	public AbstractActorState getActorState(int id) {
		return actorsState.get(id);
	}
	
	public ActorsContainer clone() {
		ActorsContainer clonedState = new ActorsContainer(); 
		clonedState.actorsState = (HashMap<Integer, AbstractActorState>) new HashMap<Integer, AbstractActorState>();
		for(Entry<Integer, AbstractActorState> entry : actorsState.entrySet()) {
			clonedState.actorsState.put(entry.getKey(), entry.getValue().memoizedClone());
		}
		return clonedState;
	}
}

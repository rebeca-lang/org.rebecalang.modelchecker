package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;


@SuppressWarnings("serial")
public abstract class AbstractSystemState implements Serializable, Cloneable {

	protected volatile Environment environment;
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


	
	public void setEnvironment(Environment environment) {
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
	
	public abstract AbstractSystemState clone();
}

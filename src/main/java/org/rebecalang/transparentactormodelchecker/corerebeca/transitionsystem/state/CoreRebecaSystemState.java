package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

@SuppressWarnings("serial")
public class CoreRebecaSystemState extends CoreRebecaAbstractState {
	
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
		String result = "{Env:" + environment + ",";
		for(CoreRebecaActorState actorState : actorsState.values())
			result += actorState + "}";
		return result;
	}
}

package org.rebecalang.modelchecker.corerebeca;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;

@SuppressWarnings("serial")
public class State implements Serializable{

	protected Hashtable<String, ActorState> stateInfo;
	protected List<Pair<String, State>> childStates;
	protected List<Pair<String, State>> parentStates;
	private int id;
	public State() {
		super();
		stateInfo = new Hashtable<String, ActorState>();
		childStates = new LinkedList<Pair<String, State>>();
		parentStates = new LinkedList<Pair<String, State>>();
	}

	public void putActorState(String name, ActorState actorState) {

		stateInfo.put(name, actorState);
	}

	public ActorState getActorState(String name) {
		return stateInfo.get(name);
	}

	public List<ActorState> getAllActorStates() {
		LinkedList<ActorState> allActorsState = new LinkedList<ActorState>();
		Iterator<String> iterator = stateInfo.keySet().iterator();
		while (iterator.hasNext()) {
			allActorsState.add(stateInfo.get(iterator.next()));
		}
		return allActorsState;
	}

	public List<ActorState> getEnabledActors() throws ModelCheckingException {
		LinkedList<ActorState> enabledActors = new LinkedList<ActorState>();
		for (ActorState actorState : getAllActorStates()) {
			if (!actorState.actorQueueIsEmpty())
				enabledActors.add(actorState);
			else if (actorState.variableIsDefined(InstructionUtilities.PC_STRING))
				enabledActors.add(actorState);
		}
		return enabledActors;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((stateInfo == null) ? 0 : stateInfo.hashCode());
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
		State other = (State) obj;
		if (stateInfo == null) {
			if (other.stateInfo != null)
				return false;
		} else if (!stateInfo.equals(other.stateInfo))
			return false;
		return true;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addChildState(String label, State childState) {
		childStates.add(new Pair<String, State>(label, childState));
	}

	public void addParentState(String label, State parentState) {
		parentStates.add(new Pair<String, State>(label, parentState));
	}

	public List<Pair<String, State>> getChildStates() {
		return childStates;
	}

	public List<Pair<String, State>> getParentStates() {
		return parentStates;
	}

	public void setChildStates(List<Pair<String, State>> childStates) {
		this.childStates = childStates;
	}

	public void setParentStates(List<Pair<String, State>> parentStates) {
		this.parentStates = parentStates;
	}

	public void clearLinks() {
		childStates = new LinkedList<Pair<String, State>>();
		parentStates = new LinkedList<Pair<String, State>>();
	}

}

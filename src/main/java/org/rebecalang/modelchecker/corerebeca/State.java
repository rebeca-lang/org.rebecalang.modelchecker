package org.rebecalang.modelchecker.corerebeca;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class State implements Serializable {

    protected Hashtable<String, BaseActorState> stateInfo;
    protected List<Pair<String, State>> childStates;
    protected List<Pair<String, State>> parentStates;
    private int id;

    public State() {
        super();
        stateInfo = new Hashtable<String, BaseActorState>();
        childStates = new LinkedList<Pair<String, State>>();
        parentStates = new LinkedList<Pair<String, State>>();
    }

    public void putActorState(String name, BaseActorState baseActorState) {

        stateInfo.put(name, baseActorState);
    }

    public BaseActorState getActorState(String name) {
        return stateInfo.get(name);
    }

    public List<BaseActorState> getAllActorStates() {
        LinkedList<BaseActorState> allActorsState = new LinkedList<BaseActorState>();
        Iterator<String> iterator = stateInfo.keySet().iterator();
        while (iterator.hasNext()) {
            allActorsState.add(stateInfo.get(iterator.next()));
        }
        return allActorsState;
    }

    public List<BaseActorState> getEnabledActors()  {
        LinkedList<BaseActorState> enabledActors = new LinkedList<BaseActorState>();
        for (BaseActorState baseActorState : getAllActorStates()) {
            if (!baseActorState.actorQueueIsEmpty())
                enabledActors.add(baseActorState);
            else if (baseActorState.variableIsDefined(InstructionUtilities.PC_STRING))
                enabledActors.add(baseActorState);
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
			return other.stateInfo == null;
        } else return stateInfo.equals(other.stateInfo);
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

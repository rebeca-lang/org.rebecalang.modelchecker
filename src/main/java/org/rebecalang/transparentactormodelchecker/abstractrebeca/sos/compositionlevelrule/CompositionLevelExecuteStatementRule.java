package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.NewInstanceAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.ExecuteStatementRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.networklevelrule.NetworkReceiveMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.beans.factory.annotation.Autowired;

public class CompositionLevelExecuteStatementRule extends AbstractSOSRule<AbstractSystemState>{
	
	ExecuteStatementRule executeStatementSOSRule;
	
	@Autowired
	NetworkReceiveMessageRule networkLevelReceiveMessageRule;
	
	public void setExecuteStatementSOSRule(ExecuteStatementRule executeStatementSOSRule) {
		this.executeStatementSOSRule = executeStatementSOSRule;
	}
	
	@Override
	public Transition<AbstractSystemState> applyRule(AbstractSystemState base, AbstractSystemState state,
			Object... additional) throws RuleIsDisabledException {
		Transition<AbstractSystemState> transitions = new Transition<AbstractSystemState>();

		Set<Integer> actorsIds = base.getActorsState().keySet();
		int priority = Integer.MAX_VALUE;
		for(Integer actorId : actorsIds) {
			AbstractActorState actorState = state.getActorState(actorId);
			if(executeStatementSOSRule.isEnabled(actorState))
				priority = Math.min(priority, actorState.getPriority());
		}
		boolean firstRound = true;
		for(Iterator<Integer> iterator = actorsIds.iterator(); iterator.hasNext();) {
			Integer actorId = iterator.next();
			AbstractActorState baseState = base.getActorState(actorId);
			if(!executeStatementSOSRule.isEnabled(baseState) ||
					baseState.getPriority() > priority)
				continue;
			
			if(!firstRound)
				state = base.clone();
			firstRound = false;
			
			AbstractActorState actorState = state.getActorState(actorId);
			Transition<AbstractActorState> executionResult = 
					executeStatementSOSRule.applyRule(base.getActorState(actorId), actorState);
			
			if(isSendMessageTransition(executionResult)) {
				Action action = executionResult.getDestinationsActions().get(0);
				networkLevelReceiveMessageRule.applyRule(
						base.getNetworkState(), state.getNetworkState(),
						action);
				transitions.addDestination(action, state);
			} else if(isNewInstanceTransition(executionResult)) {
				Action action = executionResult.getDestinationsActions().get(0);
				NewInstanceAction newInstanceAction = 
						(NewInstanceAction) action;
				state.addNewActorState(newInstanceAction.getNewInstanceReference());
				transitions.addDestination(action, state);
			} else {
				int size = executionResult.size();
				List<Action> actions = executionResult.getDestinationsActions();
				List<AbstractActorState> states = executionResult.getDestinationsStates();
				for(int cnt = 0; cnt < size; cnt++) {
					AbstractActorState newState = states.get(cnt);
					state.setActorState(newState.getId(), newState);
					transitions.addDestination(actions.get(cnt), state);
					if(cnt != size - 1)
						state = base.clone();
				}
			}
		}
		if(transitions.isEmpty())
			throw new RuleIsDisabledException();
		return transitions;
	}

	private boolean isNewInstanceTransition(Transition<AbstractActorState> transition) {
		List<Action> actions = transition.getDestinationsActions();
		if(actions.size() != 1)
			return false;
		return (transition.getDestinationsActions().get(0) instanceof NewInstanceAction);
	}

	private boolean isSendMessageTransition(Transition<AbstractActorState> transition) {
		List<Action> actions = transition.getDestinationsActions();
		if(actions.size() != 1)
			return false;
		return (transition.getDestinationsActions().get(0) instanceof MessageAction);
	}

	public void setMethodLookupTable(HashMap<String, String> methodLookupTable) {
		executeStatementSOSRule.setMethodLookupTable(methodLookupTable);
		
	}
}

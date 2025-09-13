package org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelsosrule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.NewInstanceAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.AbstractTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.NondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.actorlevelsosrule.CoreRebecaActorLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.networklevelsosrule.CoreRebecaNetworkLevelReceiveMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaCompositionLevelExecuteStatementSOSRule extends AbstractSOSRule<CoreRebecaSystemState>{

	@Autowired
	CoreRebecaActorLevelExecuteStatementSOSRule coreRebecaActorLevelExecuteStatementSOSRule;
	
	@Autowired
	CoreRebecaNetworkLevelReceiveMessageSOSRule coreRebecaNetworkLevelReceiveMessageSOSRule;
	
	@Override
	public NondeterministicTransition<CoreRebecaSystemState> applyRule(CoreRebecaSystemState base, CoreRebecaSystemState state) throws RuleIsDisabledException {
		NondeterministicTransition<CoreRebecaSystemState> transitions = 
				new NondeterministicTransition<CoreRebecaSystemState>();

		Set<Integer> actorsIds = base.getActorsState().keySet();
		int priority = Integer.MAX_VALUE;
		for(Integer actorId : actorsIds) {
			AbstractActorState coreRebecaActorState = state.getActorState(actorId);
			if(coreRebecaActorState.hasVariableInScope(CoreRebecaActorState.PC))
				priority = Math.min(priority, coreRebecaActorState.getPriority());
		}
		boolean firstRound = true;
		for(Iterator<Integer> iterator = actorsIds.iterator(); iterator.hasNext();) {
			Integer actorId = iterator.next();
			CoreRebecaActorState baseState = (CoreRebecaActorState) base.getActorState(actorId);
			if(!baseState.hasVariableInScope(CoreRebecaActorState.PC) ||
					baseState.getPriority() > priority)
				continue;
			
			if(!firstRound)
				state = base.clone();
			
			CoreRebecaActorState coreRebecaActorState = (CoreRebecaActorState) state.getActorState(actorId);
			AbstractTransition<CoreRebecaActorState> executionResult = 
					coreRebecaActorLevelExecuteStatementSOSRule.applyRule(
							(CoreRebecaActorState) base.getActorState(actorId), coreRebecaActorState);
			
			if(executionResult instanceof DeterministicTransition<CoreRebecaActorState>) {
				DeterministicTransition<CoreRebecaActorState> transition = 
						(DeterministicTransition<CoreRebecaActorState>)executionResult;
				if(transition.getAction() instanceof MessageAction) {
					coreRebecaNetworkLevelReceiveMessageSOSRule.applyRule(
							base.getNetworkState(),
							transition.getAction(), state.getNetworkState());
				} else if(transition.getAction() instanceof NewInstanceAction){
					NewInstanceAction newInstanceAction = (NewInstanceAction) transition.getAction();
					state.addNewActorState(newInstanceAction.getNewInstanceReference());
				}
				transitions.addDestination(transition.getAction(), state);
			} else if(executionResult instanceof NondeterministicTransition<CoreRebecaActorState>) {
				Iterator<Pair<? extends Action, CoreRebecaActorState>> transitionsIterator = 
						((NondeterministicTransition<CoreRebecaActorState>) executionResult).getDestinations().iterator();
				while(transitionsIterator.hasNext()) {
					Pair<? extends Action, CoreRebecaActorState> transition = transitionsIterator.next();
					CoreRebecaActorState actorState = transition.getSecond();
					state.setActorState(actorState.getId(), coreRebecaActorState);
					transitions.addDestination(transition.getFirst(), state);
					if(transitionsIterator.hasNext()) {
						state = base.clone();
					}
				}
			} else {
				throw new RebecaRuntimeInterpreterException("Unknown transition type");						
			}
//			if(iterator.hasNext())
//				state = base.clone();
		}

		if(transitions.getDestinations().isEmpty())
			throw new RuleIsDisabledException();
		return transitions;
	}

	@Override
	public NondeterministicTransition<CoreRebecaSystemState> applyRule(
			CoreRebecaSystemState base, Action action, CoreRebecaSystemState state) throws RuleIsDisabledException {
		throw new RebecaRuntimeInterpreterException("Composition level execute statement rule does not accept input action");	
	}

	public void setMethodLookupTable(HashMap<String, String> methodLookupTable) {
		coreRebecaActorLevelExecuteStatementSOSRule.setMethodLookupTable(methodLookupTable);
		
	}

}

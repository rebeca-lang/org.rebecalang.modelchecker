package org.rebecalang.transparentactormodelchecker.corerebeca.sos.actorlevelsosrule;

import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TakeMessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaActorLevelTakeMessageSOSRule extends AbstractSOSRule<CoreRebecaActorState> {

	@Override
	public DeterministicTransition<CoreRebecaActorState> applyRule(CoreRebecaActorState base, CoreRebecaActorState state) throws RuleIsDisabledException {
		if(state.messageQueueIsEmpty())
			throw new RuleIsDisabledException();
		CoreRebecaMessageState message = state.getFirstMessage();
		state.pushToScope();
		HashMap<String,Object> parameters = message.getParameters();
		for(Entry<String, Object> entry : parameters.entrySet()) {
			state.addVariableToScope(entry.getKey(), entry.getValue());
		}
		state.addVariableToScope("sender", message.getSender());
		Pair<String, Integer> pc = new Pair<String, Integer>(message.getName(), 0);
		state.addVariableToScope(CoreRebecaActorState.PC, pc);

		DeterministicTransition<CoreRebecaActorState> result = 
				new DeterministicTransition<CoreRebecaActorState>();
		result.setAction(new TakeMessageAction(message));
		result.setDestination(state);
		return result;
	}
	
	@Override
	public boolean isEnabled(CoreRebecaActorState source) {
		return !source.hasVariableInScope(CoreRebecaActorState.PC) && 
			   !source.messageQueueIsEmpty();
	}

	@Override
	public DeterministicTransition<CoreRebecaActorState> applyRule(CoreRebecaActorState base, Action action, CoreRebecaActorState state) {
		throw new RebecaRuntimeInterpreterException("Actor Level take message rule does not accept input action");
	}

}

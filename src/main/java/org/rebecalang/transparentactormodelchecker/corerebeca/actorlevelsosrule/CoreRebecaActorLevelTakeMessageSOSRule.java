package org.rebecalang.transparentactormodelchecker.corerebeca.actorlevelsosrule;

import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaActorLevelTakeMessageSOSRule extends AbstractSOSRule<CoreRebecaActorState> {

	@Override
	public CoreRebecaDeterministicTransition<CoreRebecaActorState> applyRule(CoreRebecaActorState source) {
		if(source.messageQueueIsEmpty())
			throw new RebecaRuntimeInterpreterException("Execution rule is disabled");
		CoreRebecaMessageState message = source.getFirstMessage();
		source.pushToScope();
		HashMap<String,Object> parameters = message.getParameters();
		for(Entry<String, Object> entry : parameters.entrySet()) {
			source.addVariableToScope(entry.getKey(), entry.getValue());
		}
		source.addVariableToScope("sender", message.getSender());
		Pair<String, Integer> pc = new Pair<String, Integer>();
		pc.setFirst(message.getName());
		pc.setSecond(0);
		source.addVariableToScope(CoreRebecaActorState.PC, pc);

		CoreRebecaDeterministicTransition<CoreRebecaActorState> result = 
				new CoreRebecaDeterministicTransition<CoreRebecaActorState>();
		result.setAction(new MessageAction(message));
		result.setDestination(source);
		return result;
	}
	
	@Override
	public boolean isEnable(CoreRebecaActorState source) {
		return !source.hasVariableInScope(CoreRebecaActorState.PC) && 
			   !source.messageQueueIsEmpty();
	}

	@Override
	public CoreRebecaDeterministicTransition<CoreRebecaActorState> applyRule(Action action, CoreRebecaActorState source) {
		throw new RebecaRuntimeInterpreterException("Actor Level take message rule does not accept input action");
	}

}

package org.rebecalang.transparentactormodelchecker.corerebeca.actorlevelsosrule;

import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaActorLevelReceiveSOSRule extends AbstractSOSRule<CoreRebecaActorState> {

	@Override
	public CoreRebecaDeterministicTransition<CoreRebecaActorState> applyRule(CoreRebecaActorState source) throws RuleIsDisabledException {
//		if(!source.hasVariableInScope(CoreRebecaActorState.PC))
//			throw new RebecaRuntimeInterpreterException("Execution rule is disabled");
//		CoreRebecaMessage message = source.getFirstMessage();
//		source.pushToScope();
//		HashMap<String,Object> parameters = message.getParameters();
//		for(Entry<String, Object> entry : parameters.entrySet()) {
//			source.addVariableToScope(entry.getKey(), entry.getValue());
//		}
//		Pair<String, Integer> pc = new Pair<String, Integer>();
//		pc.setFirst(message.getName());
//		pc.setSecond(0);
//		source.addVariableToScope(CoreRebecaActorState.PC, pc);
//		return source;
		throw new RebecaRuntimeInterpreterException("Actor Level recieve message rule requires input action.");
	}
	
	@Override
	public CoreRebecaDeterministicTransition<CoreRebecaActorState> applyRule(Action action, CoreRebecaActorState source) throws RuleIsDisabledException {
		source.receiveMessage(((MessageAction)action).getMessage());
		
		CoreRebecaDeterministicTransition<CoreRebecaActorState> result =
				new CoreRebecaDeterministicTransition<CoreRebecaActorState>();
		result.setDestination(source);
		return result;
	}

}

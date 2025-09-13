package org.rebecalang.transparentactormodelchecker.corerebeca.sos.actorlevelsosrule;

import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaActorLevelReceiveSOSRule extends AbstractSOSRule<CoreRebecaActorState> {

	@Override
	public DeterministicTransition<CoreRebecaActorState> applyRule(CoreRebecaActorState base, CoreRebecaActorState state) throws RuleIsDisabledException {
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
	public DeterministicTransition<CoreRebecaActorState> applyRule(
			CoreRebecaActorState base, Action action, CoreRebecaActorState source) throws RuleIsDisabledException {
		source.receiveMessage(((MessageAction)action).getMessage());
		
		DeterministicTransition<CoreRebecaActorState> result =
				new DeterministicTransition<CoreRebecaActorState>();
		result.setDestination(source);
		return result;
	}

}

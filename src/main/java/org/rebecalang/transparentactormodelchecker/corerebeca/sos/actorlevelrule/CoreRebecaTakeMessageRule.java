package org.rebecalang.transparentactormodelchecker.corerebeca.sos.actorlevelrule;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TakeMessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.TakeMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaTakeMessageRule extends TakeMessageRule {

	@Override
	public Transition<AbstractActorState> applyRule(AbstractActorState base, AbstractActorState state, Object... additional) throws RuleIsDisabledException {
		CoreRebecaActorState coreRebecaActorState = (CoreRebecaActorState)state;
		if(coreRebecaActorState.messageQueueIsEmpty())
			throw new RuleIsDisabledException();
		CoreRebecaMessageState message = coreRebecaActorState.getEnableMessage();
		
		prepareScope(state, message);

		return Transition.createDeterministicTransition(new TakeMessageAction(message), state);
	}
	
	@Override
	public boolean isEnabled(AbstractActorState source) {
		return !((CoreRebecaActorState)source).messageQueueIsEmpty() && !source.hasVariableInScope(AbstractActorState.PC);
	}
}
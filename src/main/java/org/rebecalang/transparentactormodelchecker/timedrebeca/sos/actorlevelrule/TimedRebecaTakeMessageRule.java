package org.rebecalang.transparentactormodelchecker.timedrebeca.sos.actorlevelrule;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TakeMessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.TakeMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaActorState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class TimedRebecaTakeMessageRule extends TakeMessageRule {

	@Override
	public Transition<AbstractActorState> applyRule(AbstractActorState base, AbstractActorState state, Object... additional) throws RuleIsDisabledException {
		TimedRebecaActorState timedRebecaActorState = (TimedRebecaActorState)state;
		if(timedRebecaActorState.messageQueueIsEmpty())
			throw new RuleIsDisabledException();
		TimedRebecaMessageState message = timedRebecaActorState.getEnableMessage();
		
		prepareScope(state, message);

		return Transition.createDeterministicTransition(new TakeMessageAction(message), state);
	}
	
	@Override
	public boolean isEnabled(AbstractActorState source) {
		return !((TimedRebecaActorState)source).messageQueueIsEmpty();
	}
}
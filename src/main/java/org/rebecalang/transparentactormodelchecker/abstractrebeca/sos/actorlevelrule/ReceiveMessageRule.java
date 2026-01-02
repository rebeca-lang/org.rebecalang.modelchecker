package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class ReceiveMessageRule extends AbstractSOSRule<AbstractActorState> {

	@Override
	public Transition<AbstractActorState> applyRule(AbstractActorState base, AbstractActorState state, Object... additional) throws RuleIsDisabledException {
		MessageAction action = (MessageAction) additional[0];
		state.receiveMessage(((MessageAction)action).getMessage());
		
		return Transition.createDeterministicTransition(action, state);
	}

}

package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.networklevelrule;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractNetworkState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class NetworkReceiveMessageRule extends AbstractSOSRule<AbstractNetworkState>{

	@Override
	public Transition<AbstractNetworkState> applyRule(AbstractNetworkState base, AbstractNetworkState state, Object... additional) throws RuleIsDisabledException {
		MessageAction action = (MessageAction)additional[0];
		AbstractMessageState message = action.getMessage();
		state.addMessage(message);
		return Transition.createDeterministicTransition(action, state);
	}
}
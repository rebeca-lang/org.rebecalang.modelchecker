package org.rebecalang.transparentactormodelchecker.corerebeca.networklevelsosrule;

import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaNetworkLevelReceiveMessageSOSRule extends AbstractSOSRule<CoreRebecaNetworkState>{

	@Override
	public CoreRebecaDeterministicTransition<CoreRebecaNetworkState> applyRule(CoreRebecaNetworkState source) throws RuleIsDisabledException {
		throw new RebecaRuntimeInterpreterException("Network level recieve message rule requires input action");
	}

	@Override
	public CoreRebecaDeterministicTransition<CoreRebecaNetworkState> applyRule(Action action, CoreRebecaNetworkState source) throws RuleIsDisabledException {
		CoreRebecaMessageState message = ((MessageAction) action).getMessage();
		source.addMessage(message);
		return new CoreRebecaDeterministicTransition<CoreRebecaNetworkState>(action, source);
	}

}

package org.rebecalang.transparentactormodelchecker.corerebeca.sos.networklevelsosrule;

import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaNetworkState;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaNetworkLevelReceiveMessageSOSRule extends AbstractSOSRule<CoreRebecaNetworkState>{

	@Override
	public DeterministicTransition<CoreRebecaNetworkState> applyRule(CoreRebecaNetworkState base, CoreRebecaNetworkState state) throws RuleIsDisabledException {
		throw new RebecaRuntimeInterpreterException("Network level recieve message rule requires input action");
	}

	@Override
	public DeterministicTransition<CoreRebecaNetworkState> applyRule(CoreRebecaNetworkState base, Action action, CoreRebecaNetworkState source) throws RuleIsDisabledException {
		CoreRebecaMessageState message = ((MessageAction) action).getMessage();
		source.addMessage(message);
		return new DeterministicTransition<CoreRebecaNetworkState>(action, source);
	}

}

package org.rebecalang.transparentactormodelchecker.timedrebeca.sos.networklevelrule;

import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class TimedRebecaNetworkLevelDeliverMessage extends AbstractSOSRule<TimedRebecaNetworkState> {

	@Override
	public Transition<TimedRebecaNetworkState> applyRule(TimedRebecaNetworkState base, TimedRebecaNetworkState state, Object... additional) throws RuleIsDisabledException {
		throw new RuleIsDisabledException();
	}
}
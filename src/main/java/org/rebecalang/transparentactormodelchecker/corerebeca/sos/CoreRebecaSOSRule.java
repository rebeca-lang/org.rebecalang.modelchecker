package org.rebecalang.transparentactormodelchecker.corerebeca.sos;

import java.util.List;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.AbstractTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.NondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelsosrule.CoreRebecaCompositionLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelsosrule.CoreRebecaCompositionLevelNetworkDeliverySOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelsosrule.CoreRebecaCompositionLevelTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreRebecaSOSRule extends AbstractSOSRule<CoreRebecaSystemState>{

	@Autowired
	CoreRebecaCompositionLevelExecuteStatementSOSRule executeStatementSOSRule;
	
	@Autowired
	CoreRebecaCompositionLevelNetworkDeliverySOSRule networkDeliverySOSRule;
	
	@Autowired
	CoreRebecaCompositionLevelTakeMessageSOSRule takeMessageSOSRule;
	
	@Override
	public AbstractTransition<CoreRebecaSystemState> applyRule(CoreRebecaSystemState base, CoreRebecaSystemState state) throws RuleIsDisabledException {
		NondeterministicTransition<CoreRebecaSystemState> transitions = new 
				NondeterministicTransition<CoreRebecaSystemState>();
		List<Pair<? extends Action, CoreRebecaSystemState>> destinations = null;
		try {
			destinations = executeStatementSOSRule.applyRule(base, state).getDestinations();
			transitions.addAllDestinations(destinations);
			state = base.clone();
		} catch (RuleIsDisabledException exception) {}
			
		try {
			destinations = takeMessageSOSRule.applyRule(base, state).getDestinations();
			transitions.addAllDestinations(destinations);
			state = base.clone();
		} catch (RuleIsDisabledException exception) {}

		try {
			destinations = networkDeliverySOSRule.applyRule(base, state).getDestinations();
			transitions.addAllDestinations(destinations);
		} catch (RuleIsDisabledException exception) {}

		if(transitions.getDestinations().isEmpty())
			throw new RuleIsDisabledException();
		return transitions;
	}

	@Override
	public AbstractTransition<CoreRebecaSystemState> applyRule(
			CoreRebecaSystemState base, Action synchAction,
			CoreRebecaSystemState state) throws RuleIsDisabledException {
		throw new RebecaRuntimeInterpreterException("Core Rebeca level rule does not accept input action");	
	}

}

package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.List;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.corerebeca.compositionlevelsosrule.CoreRebecaCompositionLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.compositionlevelsosrule.CoreRebecaCompositionLevelNetworkDeliverySOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.compositionlevelsosrule.CoreRebecaCompositionLevelTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaAbstractTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.RebecaStateSerializationUtils;
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
	public CoreRebecaAbstractTransition<CoreRebecaSystemState> applyRule(CoreRebecaSystemState source) throws RuleIsDisabledException {
		CoreRebecaNondeterministicTransition<CoreRebecaSystemState> transitions = new 
				CoreRebecaNondeterministicTransition<CoreRebecaSystemState>();
		CoreRebecaSystemState backup = RebecaStateSerializationUtils.clone(source);
		List<Pair<? extends Action, CoreRebecaSystemState>> destinations = null;
		try {
			destinations = executeStatementSOSRule.applyRule(source).getDestinations();
			transitions.addAllDestinations(destinations);
			source = RebecaStateSerializationUtils.clone(backup);
		} catch (RuleIsDisabledException exception) {}
			
		try {
			destinations = takeMessageSOSRule.applyRule(source).getDestinations();
			transitions.addAllDestinations(destinations);
			source = RebecaStateSerializationUtils.clone(backup);
		} catch (RuleIsDisabledException exception) {}

		try {
			destinations = networkDeliverySOSRule.applyRule(source).getDestinations();
			transitions.addAllDestinations(destinations);
		} catch (RuleIsDisabledException exception) {}
		if(transitions.getDestinations().isEmpty())
			throw new RuleIsDisabledException();
		return transitions;
	}

	@Override
	public CoreRebecaAbstractTransition<CoreRebecaSystemState> applyRule(Action synchAction,
			CoreRebecaSystemState source) throws RuleIsDisabledException {
		throw new RebecaRuntimeInterpreterException("Core Rebeca level rule does not accept input action");	
	}

}

package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.List;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.Feature;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.TransparentActorAbstractModelChecker;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelExecuteStatementRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelNetworkDeliveryRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelrule.CoreRebecaCompositionLevelTakeMessageRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.CoreRebecaTransitionSystem;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TransparentActorCoreRebecaFineGrainedDFSModelChecker extends TransparentActorAbstractModelChecker<CoreRebecaSystemState> {

	@Autowired
	@Qualifier("CORE_REBECA")
	CompositionLevelExecuteStatementRule executeStatementRule;
	
	@Autowired
	@Qualifier("CORE_REBECA")
	CompositionLevelNetworkDeliveryRule networkDeliveryRule;
	
	@Autowired
	CoreRebecaCompositionLevelTakeMessageRule takeMessageSOSRule;
	
	public TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel,SymbolTable> compiledRebecaFile, RILModel rilModel, Set<Feature> features) {
		this.compiledRebecaFile = compiledRebecaFile;
		this.rilModel = rilModel;
		transitionSystem = new CoreRebecaTransitionSystem();
		setInitialState();
		
		TransparentActorTransitionSystemState<CoreRebecaSystemState> initialState = 
				transitionSystem.getInitialState();

		try {
			dfs(initialState);
		} catch (RuleIsDisabledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TransparentActorModelCheckingResult result = 
				new TransparentActorModelCheckingResult(TransparentActorModelCheckingResult.SATISFIED);
		result.setTransitionSystem(transitionSystem);
		return result;
	}

	private void dfs(TransparentActorTransitionSystemState<CoreRebecaSystemState> state) throws RuleIsDisabledException {
		Transition<AbstractSystemState> transitions = expandState(
						state.getState(), state.getState().clone());
		int size = transitions.size();
//		List<Action> actions = transitions.getDestinationsActions();
		List<AbstractSystemState> states = transitions.getDestinationsStates();
		
		for(int cnt = 0; cnt < size; cnt++) {
			Pair<Boolean, TransparentActorTransitionSystemState<CoreRebecaSystemState>> result = 
					transitionSystem.addIfNotExists(state, (CoreRebecaSystemState) states.get(cnt));
//			System.out.println("S" + state.getId() + " -> S" + result.getSecond().getId() + "[label=\"" + actions.get(cnt).getActionLabel() +"\"]\n");
			if(result.getFirst())
				dfs(result.getSecond());
		}
	}
	
	private Transition<AbstractSystemState> expandState(CoreRebecaSystemState base, CoreRebecaSystemState state) throws RuleIsDisabledException {
		Transition<AbstractSystemState> transitions = new 
				Transition<AbstractSystemState>();
		try {
			transitions.merge(executeStatementRule.applyRule(base, state));
			state = base.clone();
		} catch (RuleIsDisabledException exception) {}
			
		try {
			transitions.merge(takeMessageSOSRule.applyRule(base, state));
			state = base.clone();
		} catch (RuleIsDisabledException exception) {}

		try {
			transitions.merge(networkDeliveryRule.applyRule(base, state));
		} catch (RuleIsDisabledException exception) {}

		if(transitions.getDestinationsStates().isEmpty())
			throw new RuleIsDisabledException();
		return transitions;
	}

	@Override
	protected CoreRebecaSystemState createSystemState() {
		return new CoreRebecaSystemState();
	}

	@Override
	protected CompositionLevelExecuteStatementRule getCompositionLevelExecuteStatementRule() {
		return executeStatementRule;
	}

	@Override
	protected CompositionLevelNetworkDeliveryRule getCompositionLevelNetworkDeliveryRule() {
		return networkDeliveryRule;
	}

	@Override
	protected AbstractActorState createAbstractActorState() {
		return new CoreRebecaActorState(CoreRebecaActorState.NO_ACTOR_ID);
	}
}

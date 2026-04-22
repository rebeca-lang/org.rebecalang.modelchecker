package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.Feature;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.ModelCheckingException;
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
public class TransparentActorCoreRebecaCoarseGrainedDFSModelChecker extends TransparentActorAbstractModelChecker<CoreRebecaSystemState> {

	@Autowired
	@Qualifier("CORE_REBECA")
	CompositionLevelExecuteStatementRule executeStatementRule;
	
	@Autowired
	@Qualifier("CORE_REBECA")
	CompositionLevelNetworkDeliveryRule networkDeliveryRule;
	
	@Autowired
	CoreRebecaCompositionLevelTakeMessageRule takeMessageSOSRule;

	private boolean completeTransitionSystem;
	
	public TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel,SymbolTable> compiledRebecaFile, RILModel rilModel, Set<Feature> features) {
		this.compiledRebecaFile = compiledRebecaFile;
		this.rilModel = rilModel;
		transitionSystem = new CoreRebecaTransitionSystem();
		if(features.contains(Feature.CompleteTransitionSystem))
			this.completeTransitionSystem = true;
		
		setInitialState();
		
		TransparentActorTransitionSystemState<CoreRebecaSystemState> initialState = 
				transitionSystem.getInitialState();
		long start = System.currentTimeMillis();
		try {
			dfs(initialState);
		} catch (Exception e) {
			TransparentActorModelCheckingResult result = 
					new TransparentActorModelCheckingResult(TransparentActorModelCheckingResult.INTERNAL_ERROR);
			result.setTransitionSystem(transitionSystem);
			result.setTime(System.currentTimeMillis() - start);
			return result;
		}

		TransparentActorModelCheckingResult result = 
				new TransparentActorModelCheckingResult(TransparentActorModelCheckingResult.SATISFIED);
		result.setTime(System.currentTimeMillis() - start);
		result.setTransitionSystem(transitionSystem);
		return result;
	}

	private void dfs(TransparentActorTransitionSystemState<CoreRebecaSystemState> state) throws ModelCheckingException {
		CoreRebecaSystemState systemState = state.getState().clone();
		Transition<AbstractSystemState> transitions = null;
		try {
			transitions = takeMessageSOSRule.applyRule(state.getState(), systemState);
		} catch (RuleIsDisabledException e) {
			if(completeTransitionSystem)
				return;
			else
				throw new ModelCheckingException(state);
		}
		
		for(int cnt = 0; cnt < transitions.size(); cnt++) {
			systemState = (CoreRebecaSystemState) transitions.getDestinationsStates().get(cnt);
//			Action action = transitions.getDestinationsActions().get(cnt);
			List<AbstractSystemState> destinations = new ArrayList<AbstractSystemState>();
			destinations.add(systemState);
			destinations.addAll(courseGraindExecuteMessageServer(systemState));
//			System.out.println(action.getActionLabel());
			deliverAllMessagesAndStore(state, destinations);
//			if(transitionSystem.size() % 20 == 0)
//				System.out.println(transitionSystem.size());
		}
	}

	private void deliverAllMessagesAndStore(TransparentActorTransitionSystemState<CoreRebecaSystemState> state,
			List<AbstractSystemState> destinations) throws ModelCheckingException {
		CoreRebecaSystemState systemState;
		for(int stateCounter = 0; stateCounter < destinations.size(); stateCounter++) {
			systemState = (CoreRebecaSystemState) destinations.get(stateCounter);
			try {
				while(true) {
					networkDeliveryRule.applyRule(systemState, systemState);
				}
			} catch (RuleIsDisabledException exception) {}
//			long temp = System.nanoTime();
			Pair<Boolean, TransparentActorTransitionSystemState<CoreRebecaSystemState>> result = 
					transitionSystem.addIfNotExists(state, systemState);
//			System.out.println("S" + state.getId() + " -> S" + result.getSecond().getId() + "[label=\"" + "\"]\n");//action.getActionLabel() +"\"]\n");
//			System.out.println(System.nanoTime() - temp);
			if(result.getFirst())
				dfs(result.getSecond());
		}
	}

	protected List<AbstractSystemState> courseGraindExecuteMessageServer(CoreRebecaSystemState systemState) {
		List<AbstractSystemState> destinations = new ArrayList<AbstractSystemState>();
		destinations.add(systemState);
		try {
			List<AbstractSystemState> nextRoundNewDestinations = new ArrayList<AbstractSystemState>();
			while(true) {
				nextRoundNewDestinations.clear();
				for(int stateCounter = 0; stateCounter < destinations.size(); stateCounter++) {
					systemState = (CoreRebecaSystemState) destinations.get(stateCounter);
					List<AbstractSystemState> newDestinations = 
							executeStatementRule.applyRule(systemState, systemState).getDestinationsStates();
					for(int cnt2 = 1; cnt2 < newDestinations.size(); cnt2++)
						nextRoundNewDestinations.add(newDestinations.get(cnt2));						
				}
				destinations.addAll(nextRoundNewDestinations);
			}
		} catch (RuleIsDisabledException exception) {}
		return destinations;
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
		return new CoreRebecaActorState(AbstractActorState.NO_ACTOR_ID);
	}
}

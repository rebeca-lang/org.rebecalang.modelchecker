package org.rebecalang.transparentactormodelchecker.timedrebeca;

import java.util.Set;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.Feature;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.TransparentActorAbstractModelChecker;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelExecuteStatementRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelNetworkDeliveryRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.compositionlevelrule.CompositionLevelTakeMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.TimedRebecaTransitionSystem;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaActorState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaSystemState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("TIMED_REBECA")
public class TransparentActorTimedRebecaFTTSModelChecker extends TransparentActorAbstractModelChecker<TimedRebecaSystemState> {

	@Autowired
	@Qualifier("TIMED_REBECA")
	protected CompositionLevelTakeMessageRule compositionLevelTakeMessageSOSRule;

	@Override
	public TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel, SymbolTable> compiledRebecaFile,
			RILModel rilModel, Set<Feature> features) {
		this.compiledRebecaFile = compiledRebecaFile;
		this.rilModel = rilModel;
		transitionSystem = new TimedRebecaTransitionSystem();
//		setInitialState();
//		
//		TransparentActorTransitionSystemState<TimedRebecaSystemState> initialState = 
//				transitionSystem.getInitialState();

		return null;
	}

	@Override
	protected TimedRebecaSystemState createSystemState() {
		return new TimedRebecaSystemState();
	}

	@Override
	protected CompositionLevelExecuteStatementRule getCompositionLevelExecuteStatementRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CompositionLevelNetworkDeliveryRule getCompositionLevelNetworkDeliveryRule() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractActorState createAbstractActorState() {
		return new TimedRebecaActorState(CoreRebecaActorState.NO_ACTOR_ID);
	}

//	public TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel,SymbolTable> compiledRebecaFile, RILModel rilModel) {
//		this.compiledRebecaFile = compiledRebecaFile;
//		this.rilModel = rilModel;
//		transitionSystem = new TransparentActorCoreRebecaTransitionSystem();
//		setInitialState();
//		
//		TransparentActorCoreRebecaTransitionSystemState initialState = 
//				transitionSystem.getInitialState();
//
//		try {
//			dfs(initialState);
//		} catch (RuleIsDisabledException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		TransparentActorModelCheckingResult result = 
//				new TransparentActorModelCheckingResult(TransparentActorModelCheckingResult.SATISFIED);
//		result.setTransitionSystem(transitionSystem);
//		return result;
//	}
//
//	private void dfs(TransparentActorCoreRebecaTransitionSystemState state) throws RuleIsDisabledException {
//		NondeterministicTransition<CoreRebecaSystemState> transitions = 
//				(NondeterministicTransition<CoreRebecaSystemState>) compositionLevelTakeMessageSOSRule.applyRule(
//						RebecaStateSerializationUtils.clone(state.getState()));
//		List<Pair<? extends Action, CoreRebecaSystemState>> destinations = 
//				transitions.getDestinations();
//		for(Pair<? extends Action, CoreRebecaSystemState> destination : destinations) {
//			CoreRebecaSystemState newState = destination.getSecond();
//			try {
//				while (true) {
//					NondeterministicTransition<CoreRebecaSystemState> stmtExecResult = 
//							compositionLevelExecuteStatementSOSRule.applyRule(newState);
//				}
//			} catch(RuleIsDisabledException exception) {}
//			try {
//				while(true) {
//					compositionLevelNetworkDeliverySOSRule.applyRule(newState);
//				}
//			} catch(RuleIsDisabledException exception) {}
//
//			Pair<Boolean, TransparentActorCoreRebecaTransitionSystemState> result = 
//					transitionSystem.addIfNotExists(state, destination.getSecond());
////			System.out.println("S" + state.getId() + " -> S" + result.getSecond().getId() + "[label=\"" + destination.getFirst().getActionLabel() +"\"]\n");
////			System.out.println(result.getSecond());
////			System.out.println("........................");
//			if(result.getFirst())
//				dfs(result.getSecond());
//		}
//	}
}

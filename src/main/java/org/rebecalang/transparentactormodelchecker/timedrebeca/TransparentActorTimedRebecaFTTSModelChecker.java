package org.rebecalang.transparentactormodelchecker.timedrebeca;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.corerebeca.TransparentActorAbstractModelChecker;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelsosrule.CoreRebecaCompositionLevelTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaSystemState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("TIMED_REBECA")
public class TransparentActorTimedRebecaFTTSModelChecker extends TransparentActorAbstractModelChecker<TimedRebecaSystemState> {

	@Autowired
	protected CoreRebecaCompositionLevelTakeMessageSOSRule compositionLevelTakeMessageSOSRule;

	@Override
	protected TimedRebecaSystemState createSystemState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel, SymbolTable> compiledRebecaFile,
			RILModel rilModel) {
		// TODO Auto-generated method stub
		return null;
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

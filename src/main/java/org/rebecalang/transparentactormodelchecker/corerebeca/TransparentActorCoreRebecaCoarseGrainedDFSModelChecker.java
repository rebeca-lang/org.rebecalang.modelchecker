package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.List;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.NondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelsosrule.CoreRebecaCompositionLevelTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.CoreRebecaTransitionSystem;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("CORE_REBECA")
public class TransparentActorCoreRebecaCoarseGrainedDFSModelChecker extends TransparentActorCoreRebecaAbstractModelChecker {

	@Autowired
	protected CoreRebecaCompositionLevelTakeMessageSOSRule compositionLevelTakeMessageSOSRule;

	public TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel,SymbolTable> compiledRebecaFile, RILModel rilModel) {
		this.compiledRebecaFile = compiledRebecaFile;
		this.rilModel = rilModel;
		long start = System.currentTimeMillis();
		transitionSystem = new CoreRebecaTransitionSystem();
		setInitialState();
		
		TransparentActorTransitionSystemState<CoreRebecaSystemState> initialState = 
				(TransparentActorTransitionSystemState<CoreRebecaSystemState>) transitionSystem.getInitialState();

		try {
			dfs(initialState);
		} catch (RuleIsDisabledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TransparentActorModelCheckingResult result = 
				new TransparentActorModelCheckingResult(TransparentActorModelCheckingResult.SATISFIED);
		result.setTransitionSystem(transitionSystem);
		result.setTime((System.currentTimeMillis() - start) / 1000);
		result.setCollisions(transitionSystem.getCollisions());
		return result;
	}

	private void dfs(TransparentActorTransitionSystemState<CoreRebecaSystemState> state) throws RuleIsDisabledException {
		CoreRebecaSystemState base = state.getState();
		CoreRebecaSystemState cloned = state.getState().clone();
		NondeterministicTransition<CoreRebecaSystemState> transitions = 
				(NondeterministicTransition<CoreRebecaSystemState>) compositionLevelTakeMessageSOSRule.applyRule(
						base, cloned);
		List<Pair<? extends Action, CoreRebecaSystemState>> destinations = 
				transitions.getDestinations();
		for(Pair<? extends Action, CoreRebecaSystemState> destination : destinations) {
			CoreRebecaSystemState newState = destination.getSecond();
//			CoreRebecaSystemState backup = newState.clone();
			try {
				while (true) {
					NondeterministicTransition<CoreRebecaSystemState> stmtExecResult = 
							compositionLevelExecuteStatementSOSRule.applyRule(newState, newState);
					if(stmtExecResult.getDestinations().size() > 1)
						assert false;
				}
			} catch(RuleIsDisabledException exception) {}
			try {
//				backup = newState.clone();
				while(true) {
					compositionLevelNetworkDeliverySOSRule.applyRule(newState, newState);
				}
			} catch(RuleIsDisabledException exception) {}

			Pair<Boolean, TransparentActorTransitionSystemState<CoreRebecaSystemState>> result = 
					transitionSystem.addIfNotExists(state, destination.getSecond());
//			System.out.println("S" + state.getId() + " -> S" + result.getSecond().getId() + "[label=\"" + destination.getFirst().getActionLabel() +"\"]\n");
//			System.out.println(result.getSecond());
//			System.out.println("........................");
			if(result.getFirst())
				dfs(result.getSecond());
		}
	}
}

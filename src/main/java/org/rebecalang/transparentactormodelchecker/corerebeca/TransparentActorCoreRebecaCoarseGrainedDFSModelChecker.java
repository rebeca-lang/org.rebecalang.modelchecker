package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.List;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.corerebeca.compositionlevelsosrule.CoreRebecaCompositionLevelTakeMessageSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.Action;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.RebecaStateSerializationUtils;
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
		transitionSystem = new TransparentActorCoreRebecaTransitionSystem();
		setInitialState();
		
		TransparentActorCoreRebecaTransitionSystemState initialState = 
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

	private void dfs(TransparentActorCoreRebecaTransitionSystemState state) throws RuleIsDisabledException {
		CoreRebecaNondeterministicTransition<CoreRebecaSystemState> transitions = 
				(CoreRebecaNondeterministicTransition<CoreRebecaSystemState>) compositionLevelTakeMessageSOSRule.applyRule(
						RebecaStateSerializationUtils.clone(state.getState()));
		List<Pair<? extends Action, CoreRebecaSystemState>> destinations = 
				transitions.getDestinations();
		for(Pair<? extends Action, CoreRebecaSystemState> destination : destinations) {
			CoreRebecaSystemState newState = destination.getSecond();
			try {
				while (true) {
					CoreRebecaNondeterministicTransition<CoreRebecaSystemState> stmtExecResult = 
							compositionLevelExecuteStatementSOSRule.applyRule(newState);
				}
			} catch(RuleIsDisabledException exception) {}
			try {
				while(true) {
					compositionLevelNetworkDeliverySOSRule.applyRule(newState);
				}
			} catch(RuleIsDisabledException exception) {}

			Pair<Boolean, TransparentActorCoreRebecaTransitionSystemState> result = 
					transitionSystem.addIfNotExists(state, destination.getSecond());
//			System.out.println("S" + state.getId() + " -> S" + result.getSecond().getId() + "[label=\"" + destination.getFirst().getActionLabel() +"\"]\n");
//			System.out.println(result.getSecond());
//			System.out.println("........................");
			if(result.getFirst())
				dfs(result.getSecond());
		}
	}
}

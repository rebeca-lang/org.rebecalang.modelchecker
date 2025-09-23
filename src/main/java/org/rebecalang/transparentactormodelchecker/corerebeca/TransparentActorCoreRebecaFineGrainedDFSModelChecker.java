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
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.CoreRebecaTransitionSystem;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("CORE_REBECA")
public class TransparentActorCoreRebecaFineGrainedDFSModelChecker extends TransparentActorCoreRebecaAbstractModelChecker {

	public TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel,SymbolTable> compiledRebecaFile, RILModel rilModel) {
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
		NondeterministicTransition<CoreRebecaSystemState> transitions = 
				(NondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(
						state.getState(), state.getState().clone());
		List<Pair<? extends Action, CoreRebecaSystemState>> destinations = 
				transitions.getDestinations();
		for(Pair<? extends Action, CoreRebecaSystemState> destination : destinations) {
			Pair<Boolean, TransparentActorTransitionSystemState<CoreRebecaSystemState>> result = 
					transitionSystem.addIfNotExists(state, destination.getSecond());
			if(result.getFirst())
				dfs(result.getSecond());
			System.out.println("S" + state.getId() + " -> S" + result.getSecond().getId() + "[label=\"" + destination.getFirst().getActionLabel() +"\"]\n");
		}
	}
	@Override
	protected CoreRebecaSystemState createSystemState() {
		return new CoreRebecaSystemState();
	}
}

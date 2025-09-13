package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.LinkedList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystem;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.NondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("CORE_REBECA")
public class TransparentActorCoreRebecaFineGrainedBFSModelChecker extends TransparentActorCoreRebecaAbstractModelChecker {

	public TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel,SymbolTable> compiledRebecaFile, RILModel rilModel) {
		this.compiledRebecaFile = compiledRebecaFile;
		this.rilModel = rilModel;
		transitionSystem = new TransparentActorTransitionSystem<CoreRebecaSystemState>();
		setInitialState();
		
		TransparentActorTransitionSystemState<CoreRebecaSystemState> initialState = 
				transitionSystem.getInitialState();

		LinkedList<TransparentActorTransitionSystemState<CoreRebecaSystemState>> openBorder = 
				new LinkedList<TransparentActorTransitionSystemState<CoreRebecaSystemState>>();
		
		openBorder.add(initialState);
		
		try {
			while(!openBorder.isEmpty()) {
				TransparentActorTransitionSystemState<CoreRebecaSystemState> readyToExpand = 
						openBorder.remove();
				NondeterministicTransition<CoreRebecaSystemState> transitions = 
						(NondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(
								readyToExpand.getState(), readyToExpand.getState().clone());
				List<Pair<? extends Action, CoreRebecaSystemState>> destinations = 
						transitions.getDestinations();
				for(Pair<? extends Action, CoreRebecaSystemState> destination : destinations) {
					Pair<Boolean, TransparentActorTransitionSystemState<CoreRebecaSystemState>> result = 
							transitionSystem.addIfNotExists(readyToExpand, destination.getSecond());
					if(result.getFirst())
						openBorder.addLast(result.getSecond());
					System.out.println("S" + readyToExpand.getId() + " -> S" + result.getSecond().getId() + "[label=\"" + destination.getFirst().getActionLabel() +"\"]\n");
				}
			}
		} catch(RuleIsDisabledException exception) {}
		TransparentActorModelCheckingResult result = 
				new TransparentActorModelCheckingResult(TransparentActorModelCheckingResult.SATISFIED);
		result.setTransitionSystem(transitionSystem);
		return result;
	}

	@Override
	protected CoreRebecaSystemState createSystemState() {
		return new CoreRebecaSystemState();
	}
}

package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class EndMethodCallSOSRule extends AbstractSOSRule<Pair<? extends AbstractActorState, InstructionBean>> {

	@Override
	public DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> 
			applyRule(Pair<? extends AbstractActorState, InstructionBean> base, Pair<? extends AbstractActorState, InstructionBean> state) {

		state.getFirst().popFromScope();

		DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> result = 
				new DeterministicTransition<Pair<? extends AbstractActorState,InstructionBean>>();
		result.setDestination(state);
		result.setAction(TauAction.TAU);

		return result;
	}

	@Override
	public DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> 
			applyRule(Pair<? extends AbstractActorState, InstructionBean> base,
					Action action, Pair<? extends AbstractActorState, InstructionBean> source) {
		throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
	}

}

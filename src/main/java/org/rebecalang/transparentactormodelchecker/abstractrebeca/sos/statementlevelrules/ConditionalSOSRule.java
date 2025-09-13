package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class ConditionalSOSRule extends AbstractSOSRule<Pair<? extends AbstractActorState, InstructionBean>> {

	@Override
	public DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> applyRule(
			Pair<? extends AbstractActorState, InstructionBean> base, Pair<? extends AbstractActorState, InstructionBean> state) {
		Action resultAction = TauAction.TAU;
		JumpIfNotInstructionBean jinsb = (JumpIfNotInstructionBean) state.getSecond();

		Object conditionValue = false;
		if(jinsb.getCondition() != null)
			conditionValue = AssignmentSOSRule.getValue(
					jinsb.getCondition(), state.getFirst());

		if((Boolean)conditionValue) {
			state.getFirst().movePCtoTheNextInstruction();
		} else {
			state.getFirst().setPC(new Pair<String, Integer>(jinsb.getMethodName(), jinsb.getLineNumber()));						
		}

		DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> result = 
				new DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>>();
		result.setDestination(state);
		result.setAction(resultAction);
		return result;
	}

	@Override
	public DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> applyRule(
			Pair<? extends AbstractActorState, InstructionBean> base, Action action,
			Pair<? extends AbstractActorState, InstructionBean> state) {
		throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
	}

}

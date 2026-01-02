package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class ConditionalRule extends AbstractSOSRule<AbstractActorState> {

	@Override
	public Transition<AbstractActorState> applyRule(
			AbstractActorState base, AbstractActorState state, Object... additional) {
		Action resultAction = TauAction.TAU;
		JumpIfNotInstructionBean jinsb = (JumpIfNotInstructionBean) additional[0];

		Object conditionValue = false;
		if(jinsb.getCondition() != null)
			conditionValue = AssignmentRule.getValue(
					jinsb.getCondition(), state);

		if((Boolean)conditionValue) {
			state.movePCtoTheNextInstruction();
		} else {
			state.setPC(new Pair<String, Integer>(jinsb.getMethodName(), jinsb.getLineNumber()));						
		}

		return Transition.createDeterministicTransition(resultAction, state);
	}
}

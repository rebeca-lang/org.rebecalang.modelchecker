package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule;

import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ReturnInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.TauAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class ReturnRule extends AbstractSOSRule<AbstractActorState> {

	@Override
	public Transition<AbstractActorState> applyRule(
			AbstractActorState base, AbstractActorState state, Object... additional) {
		Action resultAction = TauAction.TAU;

		ReturnInstructionBean rib = (ReturnInstructionBean) additional[0];
		
		Object returnValue = rib.getReturnValue();
		if(returnValue instanceof Variable)
			returnValue = state.getVariableValue((Variable) returnValue);
		state.popToReturn(returnValue);
		
		return Transition.createDeterministicTransition(resultAction, state);
	}
}

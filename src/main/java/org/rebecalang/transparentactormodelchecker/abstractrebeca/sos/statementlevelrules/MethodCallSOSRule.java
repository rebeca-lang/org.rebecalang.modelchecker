package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules;

import java.util.HashMap;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MethodCallAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.springframework.stereotype.Component;

@Component
public class MethodCallSOSRule extends AbstractSOSRule<Pair<? extends AbstractActorState, InstructionBean>> {

	private HashMap<String, String> methodLookupTable;

	@Override
	public DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> 
			applyRule(Pair<? extends AbstractActorState, InstructionBean> base, Pair<? extends AbstractActorState, InstructionBean> state) {

		MethodCallInstructionBean mcib = 
				(MethodCallInstructionBean) state.getSecond();
		AbstractActorState actorState = state.getFirst();
		actorState.movePCtoTheNextInstruction();
		if(mcib.getBase() != null) {
			actorState = (AbstractActorState) actorState.getVariableValue(
					((Variable)mcib.getBase()).getVarName());
		}
		actorState.pushToScope();
		for(String paramName : mcib.getParameters().keySet()) {
			Object paramValue = mcib.getParameters().get(paramName);
			if(paramValue instanceof Variable)
				paramValue = state.getFirst().getVariableValue(((Variable)paramValue).getVarName());
			actorState.addVariableToScope(paramName, paramValue);
		}
		String methodName = methodLookupTable.get(mcib.getMethodName());
		actorState.addVariableToScope(AbstractActorState.PC, 
				new Pair<String, Integer>(methodName, 0));
		
		DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> result = 
				new DeterministicTransition<Pair<? extends AbstractActorState,InstructionBean>>();
		result.setDestination(state);
		result.setAction(new MethodCallAction(methodName));

		return result;
	}

	@Override
	public DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> 
			applyRule(Pair<? extends AbstractActorState, InstructionBean> base, 
					Action action, Pair<? extends AbstractActorState, InstructionBean> source) {
		throw new RebecaRuntimeInterpreterException("Execute statement rule does not accept input action");
	}

	public void setMethodLookupTable(HashMap<String, String> methodLookupTable) {
		this.methodLookupTable = methodLookupTable;
	}

}

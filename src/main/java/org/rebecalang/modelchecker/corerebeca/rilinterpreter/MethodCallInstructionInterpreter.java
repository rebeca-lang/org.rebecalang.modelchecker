package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import java.util.LinkedList;
import java.util.List;

import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class MethodCallInstructionInterpreter extends InstructionInterpreter {

	@Override
	public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
		actorState.increasePC();

		MethodCallInstructionBean mcib = (MethodCallInstructionBean) ib;

		ActorState receiverState = (ActorState) actorState.retrieveVariableValue(mcib.getBase());

		List<Object> calculatedValuesOfParams = new LinkedList<Object>();
		for(int cnt = 0; cnt < mcib.getParameters().size(); cnt++) {
			Object paramValue = mcib.getParameters().get(cnt);
			if (paramValue instanceof Variable)
				calculatedValuesOfParams.add(actorState.retrieveVariableValue((Variable) paramValue));
			else
				calculatedValuesOfParams.add(paramValue);
		}
		actorState.pushInActorScope(actorState.getTypeName(), receiverState.getTypeName());
		for(int cnt = 0; cnt < mcib.getParameters().size(); cnt++) {
			Object paramValue = calculatedValuesOfParams.get(cnt);
			String paramName = mcib.getParametersNames().get(cnt);
			actorState.addVariableToRecentScope(paramName, paramValue);
		}
		actorState.initializePC(receiverState.getTypeName() + "." + mcib.getMethodName().split("\\.")[1], 0);
		int a = 2;
		return;
	}

}

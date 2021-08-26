package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

import java.util.LinkedList;
import java.util.List;

public class MethodCallInstructionInterpreter extends InstructionInterpreter {

    @Override
    public void interpret(InstructionBean ib, BaseActorState baseActorState, State globalState) {
        baseActorState.increasePC();

        MethodCallInstructionBean mcib = (MethodCallInstructionBean) ib;
        List<Object> calculatedValuesOfParams = new LinkedList<Object>();
        for (int cnt = 0; cnt < mcib.getParameters().size(); cnt++) {
            Object paramValue = mcib.getParameters().get(cnt);
            if (paramValue instanceof Variable)
                calculatedValuesOfParams.add(baseActorState.retrieveVariableValue((Variable) paramValue));
            else
                calculatedValuesOfParams.add(paramValue);
        }
        baseActorState.pushInActorScope(baseActorState.getTypeName(), ((MethodCallInstructionBean) ib).getMethodName().split("\\.")[0]);
        for (int cnt = 0; cnt < mcib.getParameters().size(); cnt++) {
            Object paramValue = calculatedValuesOfParams.get(cnt);
            String paramName = mcib.getParametersNames().get(cnt);
            baseActorState.addVariableToRecentScope(paramName, paramValue);
        }
        baseActorState.initializePC(mcib.getMethodName(), 0);
        return;
    }

}

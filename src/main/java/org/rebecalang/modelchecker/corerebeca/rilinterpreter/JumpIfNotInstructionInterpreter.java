package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

public class JumpIfNotInstructionInterpreter extends InstructionInterpreter {


    @Override
    public void interpret(InstructionBean ib, BaseActorState baseActorState, State globalState) {
        JumpIfNotInstructionBean jiib = (JumpIfNotInstructionBean) ib;
        if (jiib.getCondition() == null) {
            baseActorState.setPC(jiib.getMethodName(), jiib.getLineNumber());
            return;
        }
        Object tempCond = jiib.getCondition();
        if ((jiib.getCondition() instanceof Variable)) {
            tempCond = (Boolean) baseActorState.retrieveVariableValue((Variable) jiib.getCondition());
        }
        if (tempCond == Boolean.FALSE) {
            baseActorState.setPC(jiib.getMethodName(), jiib.getLineNumber());
        } else {
            baseActorState.increasePC();
        }

    }

}

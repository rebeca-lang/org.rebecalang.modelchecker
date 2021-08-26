package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.PopARInstructionBean;

public class PopARInstructionInterpreter extends InstructionInterpreter {

    @Override
    public void interpret(InstructionBean ib, BaseActorState baseActorState, State globalState) {

        PopARInstructionBean paib = (PopARInstructionBean) ib;
        for (int i = 0; i < paib.getNumberOfPops(); i++)
            baseActorState.popFromActorScope();
        baseActorState.increasePC();
    }

}

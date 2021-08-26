package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

public class EndMsgSrvInstructionInterpreter extends InstructionInterpreter {

    @Override
    public void interpret(InstructionBean ib, BaseActorState baseActorState, State globalState) {
        baseActorState.popFromActorScope();
//		actorState.clearPC();
    }

    @Override
    public String toString() {
        return "endMethod";
    }
}

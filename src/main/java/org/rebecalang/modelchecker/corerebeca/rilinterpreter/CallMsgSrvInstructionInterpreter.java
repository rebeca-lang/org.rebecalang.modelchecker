package org.rebecalang.modelchecker.corerebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.CallMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

import java.util.ArrayList;

public class CallMsgSrvInstructionInterpreter extends InstructionInterpreter {

    @Override
    public void interpret(InstructionBean ib, BaseActorState baseActorState, State globalState) {
        CallMsgSrvInstructionBean cmib = (CallMsgSrvInstructionBean) ib;
        BaseActorState receiverState = (BaseActorState) baseActorState.retrieveVariableValue(cmib.getReceiver());
        String msgSrvName = cmib.getMsgsrvName();
        MessageSpecification msgSpec = new MessageSpecification(msgSrvName, new ArrayList<Object>(), baseActorState);
        receiverState.addToQueue(msgSpec);
        baseActorState.increasePC();
    }
}
package org.rebecalang.modelchecker.timedrebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modelchecker.timedrebeca.TimedActorState;
import org.rebecalang.modelchecker.timedrebeca.TimedMessageSpecification;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.timedrebeca.rilinstruction.CallTimedMsgSrvInstructionBean;

import java.util.ArrayList;

public class CallTimedMsgSrvInstructionInterpreter extends InstructionInterpreter {

    @Override
    public void interpret(InstructionBean ib, BaseActorState baseActorState, State globalState) {
        CallTimedMsgSrvInstructionBean ctmib = (CallTimedMsgSrvInstructionBean) ib;
        TimedActorState receiverState = (TimedActorState) baseActorState.retrieveVariableValue(ctmib.getReceiver());
        String msgSrvName = receiverState.getTypeName() + "." + ctmib.getMsgsrvName().split("\\.")[1];
        MessageSpecification msgSpec = new TimedMessageSpecification(msgSrvName, new ArrayList<>(),
                baseActorState, (int)ctmib.getAfter(), (int)ctmib.getDeadline());
        receiverState.addToQueue(msgSpec);
        baseActorState.increasePC();
    }
}
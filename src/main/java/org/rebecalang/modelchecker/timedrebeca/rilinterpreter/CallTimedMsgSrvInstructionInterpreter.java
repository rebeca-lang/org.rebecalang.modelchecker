package org.rebecalang.modelchecker.timedrebeca.rilinterpreter;

import org.rebecalang.modelchecker.corerebeca.ActorState;
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
    public void interpret(InstructionBean ib, ActorState actorState, State globalState) {
        CallTimedMsgSrvInstructionBean ctmib = (CallTimedMsgSrvInstructionBean) ib;
        MessageSpecification msgSpec = new TimedMessageSpecification(ctmib.getMsgsrvName(), new ArrayList<Object>(),
                actorState, (int)ctmib.getAfter(), (int)ctmib.getDeadline());
        TimedActorState receiverState = (TimedActorState) actorState.retrieveVariableValue(ctmib.getReceiver());
        receiverState.addToQueue(msgSpec);
        actorState.increasePC();
    }
}
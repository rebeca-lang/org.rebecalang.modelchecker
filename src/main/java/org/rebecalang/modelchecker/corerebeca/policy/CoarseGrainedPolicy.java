package org.rebecalang.modelchecker.corerebeca.policy;

import org.rebecalang.modelchecker.corerebeca.MessageSpecification;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.EndMsgSrvInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.MethodCallInstructionBean;

public class CoarseGrainedPolicy extends AbstractPolicy {

    public CoarseGrainedPolicy() {
        breakable = false;
    }

    @Override
    public void executedInstruction(InstructionBean ib) {
        breakable = ib instanceof EndMsgSrvInstructionBean ||
                (ib instanceof MethodCallInstructionBean && ((MethodCallInstructionBean) ib).getMethodName().equals("delay$int"));
    }

    @Override
    public void pick(MessageSpecification executableMessage) {
        breakable = false;
    }

}

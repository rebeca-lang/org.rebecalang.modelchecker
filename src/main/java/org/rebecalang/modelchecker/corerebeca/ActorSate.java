package org.rebecalang.modelchecker.corerebeca;

import org.rebecalang.modelchecker.corerebeca.policy.AbstractPolicy;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionInterpreter;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.ProgramCounter;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

import java.util.LinkedList;

public class ActorSate extends BaseActorState {
    private LinkedList<MessageSpecification> queue;

    public ActorSate() {
        setQueue(new LinkedList<>());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((actorScopeStack == null) ? 0 : actorScopeStack.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((queue == null) ? 0 : queue.hashCode());
        result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ActorSate other = (ActorSate) obj;
        if (actorScopeStack == null) {
            if (other.actorScopeStack != null)
                return false;
        } else if (!actorScopeStack.equals(other.actorScopeStack))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (queue == null) {
            if (other.queue != null)
                return false;
        } else if (!queue.equals(other.queue))
            return false;
        if (typeName == null) {
            return other.typeName == null;
        } else return typeName.equals(other.typeName);
    }

    public MessageSpecification getMessage() {
        return queue.peek();
    }

    public LinkedList<MessageSpecification> getQueue() {
        return queue;
    }

    public void setQueue(LinkedList<MessageSpecification> queue) {
        this.queue = queue;
    }

    @Override
    public void addToQueue(MessageSpecification msgSpec) {
        queue.add(msgSpec);
    }

    @Override
    public boolean actorQueueIsEmpty() {
        return queue.isEmpty();
    }

    public void execute(State state, RILModel transformedRILModel,
                        AbstractPolicy policy) {

        do {
            if (variableIsDefined(InstructionUtilities.PC_STRING)) {
                ProgramCounter pc = getPC();
                String methodName = pc.getMethodName();
                int lineNumber = pc.getLineNumber();
                InstructionBean instruction = transformedRILModel.getInstructionList(methodName).get(lineNumber);
                InstructionInterpreter interpreter = StatementInterpreterContainer.getInstance()
                        .retrieveInterpreter(instruction);
                policy.executedInstruction(instruction);
                interpreter.interpret(instruction, this, state);

            } else if (!queue.isEmpty()) {
                MessageSpecification executableMessage = queue.poll();
                policy.pick(executableMessage);
                String msgName = getTypeName() + "." + executableMessage.getMessageName().split("\\.")[1];
                if (!transformedRILModel.getMethodNames().contains(msgName)) {
                    msgName = executableMessage.getMessageName();
                }
                String relatedRebecType = msgName.split("\\.")[0];
                actorScopeStack.pushInScopeStack(getTypeName(), relatedRebecType);
                addVariableToRecentScope("sender", executableMessage.getSenderActorState());
                initializePC(msgName, 0);
            } else
                throw new RebecaRuntimeInterpreterException("this case should not happen!");
        } while (!policy.isBreakable());
    }
}

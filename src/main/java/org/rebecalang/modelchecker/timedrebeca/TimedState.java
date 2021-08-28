package org.rebecalang.modelchecker.timedrebeca;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class TimedState extends State {

    private LinkedList<TimeBundle> timeBundles;
    private TimeBundle currentTimeBundle;

    public void checkForTimeStep(int enablingTime) {
        List<BaseActorState> allActorStates = getAllActorStates();
        if (!allActorStates.isEmpty()) {
            int currentTime = ((TimedActorState) allActorStates.get(0)).getCurrentTime();
            if (enablingTime > currentTime) {
                for (BaseActorState actorState : allActorStates) {
                    ((TimedActorState) actorState).setCurrentTime(enablingTime);
                }
            }
        }
    }

    public int getEnablingTime() throws ModelCheckingException {
        int minExecutionTime = Integer.MAX_VALUE;
        for (BaseActorState baseActorState : getAllActorStates()) {
            if (baseActorState.variableIsDefined(InstructionUtilities.PC_STRING))
			{
				minExecutionTime = Math.min(minExecutionTime, ((TimedActorState) baseActorState).getResumingTime());
			} else {
				int firstTimeActorCanPeekNewMsg = ((TimedActorState) baseActorState).firstTimeActorCanPeekNewMessage();
				minExecutionTime = Math.min(minExecutionTime, firstTimeActorCanPeekNewMsg);
			}
        }
        if (minExecutionTime == Integer.MAX_VALUE)
            throw new ModelCheckingException("Deadlock");
        int currentTime = ((TimedActorState) getAllActorStates().get(0)).getCurrentTime();
        if (minExecutionTime < currentTime) minExecutionTime = currentTime;
        return minExecutionTime;
    }

    public List<TimedActorState> getEnabledActors(int enablingTime) {
        List<TimedActorState> enabledActors = new ArrayList<>();
        for (BaseActorState baseActorState : getAllActorStates()) {
            if ((baseActorState.variableIsDefined(InstructionUtilities.PC_STRING))) {
                if (((TimedActorState) baseActorState).getResumingTime() <= enablingTime) {
                    enabledActors.add(((TimedActorState) baseActorState));
                }
            } else if (((TimedActorState) baseActorState).firstTimeActorCanPeekNewMessage() == enablingTime) {
                enabledActors.add(((TimedActorState) baseActorState));
            }
        }
        return enabledActors;
    }
}

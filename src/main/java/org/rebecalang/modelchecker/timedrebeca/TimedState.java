package org.rebecalang.modelchecker.timedrebeca;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;

@SuppressWarnings("serial")
public class TimedState extends State {

	private LinkedList<TimeBundle> timeBundles;
	private TimeBundle currentTimeBundle;

	public int getEnablingTime() throws ModelCheckingException {
		int minExecutionTime = Integer.MAX_VALUE;
		for (BaseActorState baseActorState : getAllActorStates()) {
			int firstTimeActorCanPeekNewMsg = firstTimeActorCanPeekNewMessage(baseActorState);
			minExecutionTime = Math.min(minExecutionTime, firstTimeActorCanPeekNewMsg);
		}
		return minExecutionTime;
	}

	public List<BaseActorState> getEnabledActors() throws ModelCheckingException {
		LinkedList<BaseActorState> enabledActors = new LinkedList<BaseActorState>();
		ArrayList<Pair<Integer, BaseActorState>> actorsMinExecutionTimes = new ArrayList<Pair<Integer, BaseActorState>>();
		int minExecutionTime = Integer.MAX_VALUE;
		for (BaseActorState baseActorState : getAllActorStates()) {
			int firstTimeActorCanPeekNewMsg = firstTimeActorCanPeekNewMessage(baseActorState);
			minExecutionTime = Math.min(minExecutionTime, firstTimeActorCanPeekNewMsg);
			Pair<Integer, BaseActorState> actorTimePair = new Pair<Integer, BaseActorState>(firstTimeActorCanPeekNewMsg,
					baseActorState);
			actorsMinExecutionTimes.add(actorTimePair);
		}
		if (minExecutionTime == Integer.MAX_VALUE)
			throw new ModelCheckingException("Deadlock");
		for (Pair<Integer, BaseActorState> actorTimePair : actorsMinExecutionTimes) {
			if (actorTimePair.getFirst() == minExecutionTime)
				enabledActors.add(actorTimePair.getSecond());
		}
		return enabledActors;
	}

	private int firstTimeActorCanPeekNewMessage(BaseActorState baseActorState) {
		if (baseActorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
			throw new RuntimeException("This version supports coarse grained execution.");
		} else {
			if (!baseActorState.actorQueueIsEmpty()) {
				ActorTimeBundle actorTimeBundle = currentTimeBundle.getActorTimeBundle(baseActorState.getName());
				int actorNow = actorTimeBundle.getNow();
				TimeBundleElement firstMsgTime = actorTimeBundle.getQueueBundles().getFirst();
				return Math.max(actorNow, firstMsgTime.getMessageArrivalTime());
			}
		}
		return Integer.MAX_VALUE;
	}
}

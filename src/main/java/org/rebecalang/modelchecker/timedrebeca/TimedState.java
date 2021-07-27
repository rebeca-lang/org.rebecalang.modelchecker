package org.rebecalang.modelchecker.timedrebeca;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.ActorState;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modelchecker.corerebeca.State;
import org.rebecalang.modelchecker.corerebeca.rilinterpreter.InstructionUtilities;

@SuppressWarnings("serial")
public class TimedState extends State {

	private LinkedList<TimeBundle> timeBundles;
	private TimeBundle currentTimeBundle;

	public int getEnablingTime() throws ModelCheckingException {
		int minExecutionTime = Integer.MAX_VALUE;
		for (ActorState actorState : getAllActorStates()) {
			int firstTimeActorCanPeekNewMsg = firstTimeActorCanPeekNewMessage(actorState);
			minExecutionTime = Math.min(minExecutionTime, firstTimeActorCanPeekNewMsg);
		}
		return minExecutionTime;
	}

	public List<ActorState> getEnabledActors() throws ModelCheckingException {
		LinkedList<ActorState> enabledActors = new LinkedList<ActorState>();
		ArrayList<Pair<Integer, ActorState>> actorsMinExecutionTimes = new ArrayList<Pair<Integer, ActorState>>();
		int minExecutionTime = Integer.MAX_VALUE;
		for (ActorState actorState : getAllActorStates()) {
			int firstTimeActorCanPeekNewMsg = firstTimeActorCanPeekNewMessage(actorState);
			minExecutionTime = Math.min(minExecutionTime, firstTimeActorCanPeekNewMsg);
			Pair<Integer, ActorState> actorTimePair = new Pair<Integer, ActorState>(firstTimeActorCanPeekNewMsg,
					actorState);
			actorsMinExecutionTimes.add(actorTimePair);
		}
		if (minExecutionTime == Integer.MAX_VALUE)
			throw new ModelCheckingException("Deadlock");
		for (Pair<Integer, ActorState> actorTimePair : actorsMinExecutionTimes) {
			if (actorTimePair.getFirst() == minExecutionTime)
				enabledActors.add(actorTimePair.getSecond());
		}
		return enabledActors;
	}

	private int firstTimeActorCanPeekNewMessage(ActorState actorState) {
		if (actorState.variableIsDefined(InstructionUtilities.PC_STRING)) {
			throw new RuntimeException("This version supports coarse grained execution.");
		} else {
			if (!actorState.actorQueueIsEmpty()) {
				ActorTimeBundle actorTimeBundle = currentTimeBundle.getActorTimeBundle(actorState.getName());
				int actorNow = actorTimeBundle.getNow();
				TimeBundleElement firstMsgTime = actorTimeBundle.getQueueBundles().getFirst();
				return Math.max(actorNow, firstMsgTime.getMessageArrivalTime());
			}
		}
		return Integer.MAX_VALUE;
	}
}

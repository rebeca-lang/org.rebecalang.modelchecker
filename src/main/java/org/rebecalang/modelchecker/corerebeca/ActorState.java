package org.rebecalang.modelchecker.corerebeca;

import java.io.PrintStream;
import java.util.LinkedList;

import org.rebecalang.modelchecker.corerebeca.utils.RILUtils;

@SuppressWarnings("serial")
public class ActorState extends BaseActorState<MessageSpecification> {
	private LinkedList<MessageSpecification> queue;

	public ActorState() {
		initializeQueue();
	}

	public void initializeQueue() {
		setQueue(new LinkedList<>());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((queue == null) ? 0 : queue.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActorState other = (ActorState) obj;
		if (queue == null) {
			if (other.queue != null)
				return false;
		} else if (!queue.equals(other.queue))
			return false;
		return true;
	}


	public MessageSpecification getMessage(boolean isPeek) {
		return queue.peek() != null ? (isPeek ? queue.peek() : queue.poll()) : null;
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

	@Override
	public String toString() {
		String retValue = super.toString();
		retValue += "\n queue:[";
		for(MessageSpecification ms : queue) {
			retValue += RILUtils.convertToString(ms) + ",";
		}
		return retValue + "]";
	}

	@Override
	protected void exportQueueContent(PrintStream output) {
		output.println("<queue>");
		for (MessageSpecification messageSpecification : queue) {
			messageSpecification.export(output);
		}
		output.println("</queue>");
	}


//	private boolean continueExecutionOfMessageServer() {
//		return variableIsDefined(InstructionUtilities.PC_STRING);
//	}
}

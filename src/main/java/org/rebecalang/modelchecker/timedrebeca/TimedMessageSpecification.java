package org.rebecalang.modelchecker.timedrebeca;

import java.io.PrintStream;
import java.util.Map;
import java.util.Map.Entry;

import org.rebecalang.modelchecker.corerebeca.BaseActorState;
import org.rebecalang.modelchecker.corerebeca.MessageSpecification;

@SuppressWarnings("serial")
public class TimedMessageSpecification extends MessageSpecification {
    private int minStartTime;
	private int maxStartTime;
	private int period;
	private int relativeDeadline;
	protected int priority = Integer.MAX_VALUE;

	public TimedMessageSpecification(
            String messageName,
			int priority,
			Map<String, Object> parameters,
			BaseActorState<?> baseActorState,
			int minStartTime,
            int maxStartTime,
            int relativeDeadline,
			int period) {
        super(messageName, parameters, baseActorState);
        this.maxStartTime     = maxStartTime;
        this.minStartTime     = minStartTime;
        this.period           = period;
        this.priority           = priority;
		this.relativeDeadline = relativeDeadline;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + priority;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;

		TimedMessageSpecification other = (TimedMessageSpecification) obj;

		if (priority != other.priority) return false;

		return true;
	}

	public int getMinStartTime() {
		return this.minStartTime;
	}

	public void setMinStartTime(int minStartTime) {
		this.minStartTime = minStartTime;
	}

	public void setMaxStartTime(int maxStartTime) {
		this.maxStartTime = maxStartTime;
	}

	public int getMaxStartTime() {
		return this.maxStartTime;
	}

	public void setPeriod(int period) {
		this.period = period;
	}

	public int getPeriod() {
		return this.period;
	}

	public int getRelativeDeadline() {
		return this.relativeDeadline;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public void export(PrintStream output) {
		output.print("<message sender=\"" + senderActorState.getName() + 
				"\" arrival=\"" + minStartTime + "\"" +
				" deadline=\"" + maxStartTime + "\">");
		output.print(messageName + "(");
		for(Entry<String, Object> entry : parameters.entrySet())
			output.print(entry.getKey() + "->" + entry.getValue() + ", ");
		output.println(")</message>");
	}
}

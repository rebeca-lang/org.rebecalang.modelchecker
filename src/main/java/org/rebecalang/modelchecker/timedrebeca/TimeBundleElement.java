package org.rebecalang.modelchecker.timedrebeca;

public class TimeBundleElement {

	private int messageArrivalTime;
	private int messageDeadline;

	public int getMessageArrivalTime() {
		return messageArrivalTime;
	}

	public void setMessageArrivalTime(int messageArrivalTime) {
		this.messageArrivalTime = messageArrivalTime;
	}

	public int getMessageDeadline() {
		return messageDeadline;
	}

	public void setMessageDeadline(int messageDeadline) {
		this.messageDeadline = messageDeadline;
	}

}

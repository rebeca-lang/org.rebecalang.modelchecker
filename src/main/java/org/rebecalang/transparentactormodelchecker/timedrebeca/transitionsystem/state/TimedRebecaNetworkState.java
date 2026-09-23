package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.ArrayList;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractNetworkState;

import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.FALSE;
import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.TRUE;

@SuppressWarnings("serial")
public class TimedRebecaNetworkState extends AbstractNetworkState implements Serializable, Cloneable {
	
	ArrayList<TimeBucket> receivedMessages;
	
	public TimedRebecaNetworkState() {
		receivedMessages = new ArrayList<TimeBucket>();
	}
	
	public ArrayList<TimeBucket> getReceivedMessages() {
		return receivedMessages;
	}
	
	public void setReceivedMessages(ArrayList<TimeBucket> receivedMessages) {
		this.receivedMessages = receivedMessages;
	}
	
	@Override
	public void addMessage(AbstractMessageState abstractMessage) {
		TimedRebecaMessageState message = (TimedRebecaMessageState) abstractMessage;
		TimeBucket timeBucket = null;
		if(receivedMessages.size() == 0) {
			timeBucket = new TimeBucket(message.getArrival());
			receivedMessages.add(timeBucket);
		} else {
			int arrivalTime = message.getArrival();
			int cnt = 0;
			boolean sameTimeBucketExists = false;
			for(; cnt < receivedMessages.size(); cnt++) {
				int time = receivedMessages.get(cnt).getTime();
				if(time < arrivalTime)
					continue;
				sameTimeBucketExists = (time == arrivalTime);
				break;
			}
			if(sameTimeBucketExists) {
				timeBucket = receivedMessages.get(cnt);
 			} else {
 				timeBucket = new TimeBucket(arrivalTime);
 				receivedMessages.add(cnt, timeBucket);
 			}
		}
		ActorReceivingBucket receiverMessages = 
				timeBucket.getReceiverMessages(message.getReceiverId());
		receiverMessages.add(message);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((receivedMessages == null) ? 0 : receivedMessages.hashCode());
		return result;
	}

	private boolean basicEquality(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (getClass() != other.getClass())
			return false;
		if (receivedMessages == null) {
			if (((TimedRebecaNetworkState)other).receivedMessages != null)
				return false;
		} else {
			if(((TimedRebecaNetworkState)other).receivedMessages.size() != this.receivedMessages.size())
				return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!basicEquality(obj))
			return false;
		TimedRebecaNetworkState other = (TimedRebecaNetworkState) obj;
		for (int cnt = 0; cnt < this.receivedMessages.size(); cnt++) {
			TimeBucket thisTimeBucket = this.receivedMessages.get(cnt);
			TimeBucket otherTimeBucket = other.receivedMessages.get(cnt);
			if(!thisTimeBucket.equals(otherTimeBucket))
				return false;
		}
		return true;
	}
	
	public Pair<Boolean, Integer> shiftEquals(TimedRebecaNetworkState other) {
		if(!basicEquality(other))
			return FALSE;
		if(this.receivedMessages.size() == 0)
			return TRUE;
		int shift = other.receivedMessages.get(0).getTime() - 
			       this.receivedMessages.get(0).getTime();
	
		for (int cnt = 0; cnt < this.receivedMessages.size(); cnt++) {
			TimeBucket thisTimeBucket = this.receivedMessages.get(cnt);
			TimeBucket otherTimeBucket = other.receivedMessages.get(cnt);
			if(otherTimeBucket.getTime() - thisTimeBucket.getTime() != shift)
				return FALSE;
			Pair<Boolean, Integer> result = thisTimeBucket.shiftEquals(otherTimeBucket);
			if(!result.getFirst())
				return FALSE;
			if(result.getSecond() != shift)
				return FALSE;
		}
		return new Pair<Boolean, Integer>(true, shift);
	}
	
	@Override
	public String toString() {
		return receivedMessages.toString();
	}
	
	@Override
	public TimedRebecaNetworkState clone() {
		TimedRebecaNetworkState clonedNetworkState = new TimedRebecaNetworkState();
		for (TimeBucket timeBucket : receivedMessages) {
			clonedNetworkState.receivedMessages.add(timeBucket.clone());
		}
		return clonedNetworkState;
	}
	
	@Override
	public boolean hasMessage() {
		return !receivedMessages.isEmpty();
	}
}

package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractNetworkState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

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
	
	public void addMessage(TimedRebecaMessageState message) {
		TimeBucket timeBucket = null;
		if(receivedMessages.size() == 0) {
			timeBucket = new TimeBucket(message.getArrival());
			receivedMessages.add(timeBucket);
		} else {
			int arrivalTime = message.getArrival();
			for(int cnt = 0; cnt < receivedMessages.size(); cnt++) {
				int time = receivedMessages.get(cnt).getTime();
				if(time < arrivalTime)
					continue;
				if(time == arrivalTime) {
					timeBucket = receivedMessages.get(cnt);
	 			} else {
	 				timeBucket = new TimeBucket(arrivalTime);
	 				receivedMessages.add(cnt, timeBucket);
	 			}
				break;
			}			
		}
		ActorReceivingBucket receiverMessages = 
				timeBucket.getReceiverMessages(message.getReceiver());
		receiverMessages.add(message);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((receivedMessages == null) ? 0 : receivedMessages.hashCode());
		return result;
	}

	public Pair<Boolean, Integer> shiftEquals(TimedRebecaNetworkState other) {
		if (this == other)
			return TRUE;
		if (other == null)
			return FALSE;
		if (getClass() != other.getClass())
			return FALSE;
		int shift = 0;
		if (receivedMessages == null) {
			if (other.receivedMessages != null)
				return FALSE;
		} else {
			if(other.receivedMessages.size() != this.receivedMessages.size())
				return FALSE;
			if(this.receivedMessages.size() == 0)
				return TRUE;
			shift = other.receivedMessages.get(0).getTime() - 
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
		}
		return new Pair<Boolean, Integer>(true, shift);
	}
	
	@Override
	public String toString() {
		return receivedMessages.toString();
	}
	
	public TimedRebecaNetworkState clone() {
		TimedRebecaNetworkState clonedNetworkState = new TimedRebecaNetworkState();
		for (TimeBucket timeBucket : receivedMessages) {
			clonedNetworkState.receivedMessages.add(timeBucket.clone());
		}
		return clonedNetworkState;
	}
}

class TimeBucket implements Cloneable {
	private int time;
	private HashMap<Integer,ActorReceivingBucket> messages;
	
	public TimeBucket(int time) {
		this.time = time;
		messages = new HashMap<Integer, ActorReceivingBucket>();
	}

	public ActorReceivingBucket getReceiverMessages(AbstractActorState actor) {
		if(messages.containsKey(actor.getId()))
			return messages.get(actor.getId());
		else {
			ActorReceivingBucket bucket = new ActorReceivingBucket();
			messages.put(actor.getId(), bucket);
			return bucket;
		}
	}

	public HashMap<Integer, ActorReceivingBucket> getMessages() {
		return messages;
	}
	
	public int getTime() {
		return time;
	}
	
	public TimeBucket clone() {
		TimeBucket timeBucket = new TimeBucket(time);
		for(Entry<Integer, ActorReceivingBucket> arBucket : timeBucket.messages.entrySet()) {
			timeBucket.messages.put(arBucket.getKey(), arBucket.getValue().clone());
		}
		return timeBucket;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((messages == null) ? 0 : messages.hashCode());
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
		TimeBucket other = (TimeBucket) obj;
		if (messages == null) {
			if (other.messages != null)
				return false;
		} else if (!messages.equals(other.messages))
			return false;
		return true;
	}
	
	public Pair<Boolean, Integer> shiftEquals(TimeBucket other) {
		if (this == other)
			return TRUE;
		if (other == null)
			return FALSE;
		if (getClass() != other.getClass())
			return FALSE;
		int shift = Integer.MIN_VALUE;
		if (this.messages.size() != other.messages.size())
			return FALSE;
		if(this.messages.size() == 0)
			return TRUE;
		
		for(Integer key : this.messages.keySet()) {
			Pair<Boolean, Integer> result = this.messages.get(key).shiftEquals(other.messages.get(key));
			if(!result.getFirst())
				return FALSE;
			if(shift == Integer.MIN_VALUE)
				shift = result.getSecond();
			if(shift != result.getSecond())
				return FALSE;
		}
		return new Pair<Boolean, Integer>(true, shift);
	}
	
	@Override
	public String toString() {
		return "[" + time + ", " + messages.toString() + "]";
	}
}

class ActorReceivingBucket implements Cloneable {
	private HashMap<Integer,ArrayList<TimedRebecaMessageState>> sentMessages;

	public ActorReceivingBucket() {
		sentMessages = new HashMap<Integer, ArrayList<TimedRebecaMessageState>>();
	}
	public Pair<Boolean, Integer> shiftEquals(ActorReceivingBucket other) {
		if (this == other)
			return TRUE;
		if (other == null)
			return FALSE;
		if (getClass() != other.getClass())
			return FALSE;
		int shift = Integer.MIN_VALUE;
		if (this.sentMessages.size() != other.sentMessages.size())
			return FALSE;
		if(this.sentMessages.size() == 0)
			return TRUE;
		for(Integer key : sentMessages.keySet()) {
			ArrayList<TimedRebecaMessageState> thisMessages = this.sentMessages.get(key);
			ArrayList<TimedRebecaMessageState> otherMessages = other.sentMessages.get(key);
			if(otherMessages == null)
				return FALSE;
			if(thisMessages.size() != otherMessages.size())
				return FALSE;
			if(this.sentMessages.size() == 0)
				return TRUE;
			for(int cnt = 0; cnt < this.sentMessages.size(); cnt++) {
				Pair<Boolean, Integer> result = thisMessages.get(cnt).shiftEquals(otherMessages.get(cnt));
				if(!result.getFirst())
					return FALSE;
				if(shift == Integer.MIN_VALUE)
					shift = result.getSecond();
				if(shift != result.getSecond())
					return FALSE;
			}
		}
		return new Pair<Boolean, Integer>(true, shift);
	}
	public void add(TimedRebecaMessageState message) {
		int receiverID = message.getReceiver().getId();
		if(!sentMessages.containsKey(receiverID))
			sentMessages.put(receiverID, new ArrayList<TimedRebecaMessageState>());
		sentMessages.get(receiverID).add(message);
	}
	public HashMap<Integer, ArrayList<TimedRebecaMessageState>> getSentMessages() {
		return sentMessages;
	}
	
	public ActorReceivingBucket clone() {
		ActorReceivingBucket arBucket = new ActorReceivingBucket();
		for(Entry<Integer,ArrayList<TimedRebecaMessageState>> receivingBucket : sentMessages.entrySet()) {
			ArrayList<TimedRebecaMessageState> cloneMessages = 
					CloningRepository.cloneArrayList(receivingBucket.getValue());
			arBucket.sentMessages.put(receivingBucket.getKey(), cloneMessages);
		}
		return arBucket;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sentMessages == null) ? 0 : sentMessages.hashCode());
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
		ActorReceivingBucket other = (ActorReceivingBucket) obj;
		if (sentMessages == null) {
			if (other.sentMessages != null)
				return false;
		} else if (!sentMessages.equals(other.sentMessages))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return sentMessages.toString();
	}
}

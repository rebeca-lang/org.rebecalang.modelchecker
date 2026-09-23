package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state;

import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.FALSE;
import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.TRUE;

import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;

public class TimeBucket implements Cloneable {
	private int time;
	private HashMap<Integer,ActorReceivingBucket> messages;
	
	public TimeBucket(int time) {
		this.time = time;
		messages = new HashMap<Integer, ActorReceivingBucket>();
	}

	public ActorReceivingBucket getReceiverMessages(int actorID) {
		if(messages.containsKey(actorID))
			return messages.get(actorID);
		else {
			ActorReceivingBucket bucket = new ActorReceivingBucket();
			messages.put(actorID, bucket);
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
		for(Entry<Integer, ActorReceivingBucket> arBucket : this.messages.entrySet()) {
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
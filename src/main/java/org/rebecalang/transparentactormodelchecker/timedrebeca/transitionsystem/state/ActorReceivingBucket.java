package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state;

import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.FALSE;
import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.TRUE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

public class ActorReceivingBucket implements Cloneable {
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
			for(int cnt = 0; cnt < thisMessages.size(); cnt++) {
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
		int receiverID = message.getReceiverId();
		if(!sentMessages.containsKey(receiverID))
			sentMessages.put(receiverID, new ArrayList<TimedRebecaMessageState>());
		sentMessages.get(receiverID).add(message);
	}

//	public HashMap<Integer, ArrayList<TimedRebecaMessageState>> getSentMessages() {
//		return sentMessages;
//	}
	
	public ArrayList<TimedRebecaMessageState> getAllSentMessages() {
		ArrayList<TimedRebecaMessageState> allMessages = new ArrayList<TimedRebecaMessageState>();
		for(ArrayList<TimedRebecaMessageState> messageStates : sentMessages.values())
			allMessages.addAll(messageStates);
		return allMessages;
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
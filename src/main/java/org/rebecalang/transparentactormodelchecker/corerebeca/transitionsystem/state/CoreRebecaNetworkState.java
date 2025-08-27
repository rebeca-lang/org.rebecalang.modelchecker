package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.CloningRepository;

@SuppressWarnings("serial")
public class CoreRebecaNetworkState extends CoreRebecaAbstractState {
	private HashMap<Pair<Integer, Integer>, ArrayList<CoreRebecaMessageState>> receivedMessages;
	
	public CoreRebecaNetworkState() {
		receivedMessages = new HashMap<Pair<Integer, Integer>, ArrayList<CoreRebecaMessageState>>();
	}
	
	public HashMap<Pair<Integer, Integer>, ArrayList<CoreRebecaMessageState>> getReceivedMessages() {
		return receivedMessages;
	}
	public void setReceivedMessages(HashMap<Pair<Integer, Integer>, ArrayList<CoreRebecaMessageState>> receivedMessages) {
		this.receivedMessages = receivedMessages;
	}
	
	public void addMessage(CoreRebecaMessageState message) {
		Pair<Integer, Integer> key = new Pair<Integer, Integer>(
				message.getSender().getId(), message.getReceiver().getId());
		if(!receivedMessages.containsKey(key))
			receivedMessages. put(key, new ArrayList<CoreRebecaMessageState>());
		receivedMessages.get(key).add(message);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((receivedMessages == null) ? 0 : receivedMessages.hashCode());
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
		CoreRebecaNetworkState other = (CoreRebecaNetworkState) obj;
		if (receivedMessages == null) {
			if (other.receivedMessages != null)
				return false;
		} else if (!receivedMessages.equals(other.receivedMessages))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return receivedMessages.toString();
	}
	
	public CoreRebecaNetworkState clone() {
		CoreRebecaNetworkState clonedNetworkState = new CoreRebecaNetworkState();
		for(Entry<Pair<Integer, Integer>, ArrayList<CoreRebecaMessageState>> entry : receivedMessages.entrySet()) {
			Pair<Integer, Integer> key = 
					new Pair<Integer, Integer>(entry.getKey().getFirst(), entry.getKey().getSecond());
			ArrayList<CoreRebecaMessageState> messages = CloningRepository.cloneMessageQueue(entry.getValue());
			clonedNetworkState.receivedMessages.put(key, messages);
		}
		return clonedNetworkState;
	}
}

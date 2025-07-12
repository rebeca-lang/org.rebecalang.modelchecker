package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.util.ArrayList;
import java.util.HashMap;

import org.rebecalang.compiler.utils.Pair;

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
	
}

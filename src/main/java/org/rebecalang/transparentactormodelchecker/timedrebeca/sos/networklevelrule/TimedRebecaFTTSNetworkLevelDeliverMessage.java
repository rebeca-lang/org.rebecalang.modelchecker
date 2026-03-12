package org.rebecalang.transparentactormodelchecker.timedrebeca.sos.networklevelrule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.MessageAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.networklevelrule.NetworkLevelDeliverMessageRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractNetworkState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.ActorReceivingBucket;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimeBucket;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaNetworkState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class TimedRebecaFTTSNetworkLevelDeliverMessage extends NetworkLevelDeliverMessageRule {

	@Override
	public Transition<AbstractNetworkState> applyRule(AbstractNetworkState base, AbstractNetworkState state,
			Object... additional) throws RuleIsDisabledException {
		Transition<AbstractNetworkState> transitions = new Transition<AbstractNetworkState>();
		TimedRebecaNetworkState timedRebecaNetworkState = (TimedRebecaNetworkState)state;
		
		ArrayList<TimeBucket> receivedMessages = timedRebecaNetworkState.getReceivedMessages();
		if(!receivedMessages.isEmpty()) {
			TimeBucket timeBucket = receivedMessages.get(0);
			if(timeBucket.getMessages().size() == 1)
				receivedMessages.remove(0);
			Iterator<Entry<Integer, ActorReceivingBucket>> timeIntanceReceivedMessagesIterator = 
					timeBucket.getMessages().entrySet().iterator();
			if(timeIntanceReceivedMessagesIterator.hasNext()) {
				ActorReceivingBucket receivingBucket =
						timeIntanceReceivedMessagesIterator.next().getValue();
				timeIntanceReceivedMessagesIterator.remove();
				ArrayList<TimedRebecaMessageState> allSentMessages = 
						receivingBucket.getAllSentMessages();
				MessageAction action = new MessageAction(allSentMessages.get(0));
				transitions.addDestination(action, state);					
			}
		}
//		while(!receivedMessages.isEmpty()) {
//			TimeBucket timeBucket = receivedMessages.remove(0);
//			Iterator<Entry<Integer, ActorReceivingBucket>> timeIntanceReceivedMessagesIterator = 
//					timeBucket.getMessages().entrySet().iterator();
//			while(timeIntanceReceivedMessagesIterator.hasNext()) {
//				ActorReceivingBucket receivingBucket =
//						timeIntanceReceivedMessagesIterator.next().getValue();
//				ArrayList<TimedRebecaMessageState> allSentMessages = 
//						receivingBucket.getAllSentMessages();
//				for(TimedRebecaMessageState messageState : allSentMessages) {
//					MessageAction action = new MessageAction(messageState);
//					transitions.addDestination(action, state);					
//				}
//			}
//		}
		
		if(transitions.getDestinationsStates().isEmpty())
			throw new RuleIsDisabledException();
		
		return transitions;
	}

}
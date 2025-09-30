package org.rebecalang.transparentactormodelchecker.timedrebeca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.TimedRebecaTransitionSystem;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedActorScope;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaActorState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaSystemState;

public class TimedRebecaTransitionSystemTest {

    
	TimedRebecaTransitionSystem transitionSystem;
	TimedRebecaSystemState initialState;
	TimedRebecaSystemState otherState;
	TimedRebecaActorState otherActor1, otherActor2;

    @BeforeEach
    public void setup() throws CodeCompilationException {
    	transitionSystem = new TimedRebecaTransitionSystem();
    	    	
    	TimedRebecaActorState actor1 = new TimedRebecaActorState(0);
    	TimedRebecaActorState actor2 = new TimedRebecaActorState(1);
    	
    	TimedRebecaMessageState message1 = new TimedRebecaMessageState();
		message1.setName("m1");
		message1.setSender(actor1);
		message1.setReceiver(actor1);
		message1.setParameters(new HashMap<String, Object>());
		message1.setArrival(10);
		message1.setDeadline(20);
		actor1.receiveMessage(message1);
		
		TimedRebecaMessageState message2 = new TimedRebecaMessageState();
		message2.setName("m2");
		message2.setSender(actor2);
		message2.setReceiver(actor2);
		message2.setParameters(new HashMap<String, Object>());
		message2.setArrival(20);
		message2.setDeadline(30);
		actor2.receiveMessage(message2);

		initialState = new TimedRebecaSystemState();
		initialState.setActorState(actor1);
		initialState.setActorState(actor2);
    	TransparentActorTransitionSystemState<TimedRebecaSystemState> initial = 
    			new TransparentActorTransitionSystemState<TimedRebecaSystemState>(0);
    	initial.setState(initialState);
    	transitionSystem.setInitialState(initial);
    	
    	
    	otherActor1 = new TimedRebecaActorState(0);
    	otherActor2 = new TimedRebecaActorState(1);
    	
    	TimedRebecaMessageState message3 = new TimedRebecaMessageState();
		message3.setName("m1");
		message3.setSender(otherActor1);
		message3.setReceiver(otherActor1);
		message3.setParameters(new HashMap<String, Object>());
		message3.setArrival(10);
		message3.setDeadline(20);
		otherActor1.receiveMessage(message3);
		
		TimedRebecaMessageState message4 = new TimedRebecaMessageState();
		message4.setName("m2");
		message4.setSender(otherActor2);
		message4.setReceiver(otherActor2);
		message4.setParameters(new HashMap<String, Object>());
		message4.setArrival(20);
		message4.setDeadline(30);
		otherActor2.receiveMessage(message4);

		otherState = new TimedRebecaSystemState();
		otherState.setActorState(otherActor1);
		otherState.setActorState(otherActor2);
    }
    
    @Test
    public void testTheSameHashCodes() {
    	assertEquals(initialState.hashCode(), otherState.hashCode());
    }
    
    @Test
    public void testZeroShiftEquivalent() {
    	
    	Pair<Boolean, Integer> result = initialState.shiftEquals(otherState);
    	
    	assertTrue(result.getFirst());
    	assertEquals(0, result.getSecond());
    }
    
    @Test
    public void testOneShiftEquivalent() {
    	
    	TimedRebecaMessageState firstMessage = otherActor1.getFirstMessage();
    	firstMessage.setArrival(11);
    	firstMessage.setDeadline(21);
    	otherActor1.receiveMessage(firstMessage);
    	otherActor1.setVariableValue(new Variable(TimedActorScope.TIME_VARIABLE_NAME), 1);

    	firstMessage = otherActor2.getFirstMessage();
    	firstMessage.setArrival(21);
    	firstMessage.setDeadline(31);
    	otherActor2.receiveMessage(firstMessage);
    	otherActor2.setVariableValue(new Variable(TimedActorScope.TIME_VARIABLE_NAME), 1);

    	Pair<Boolean, Integer> result = initialState.shiftEquals(otherState);
    	
    	assertTrue(result.getFirst());
    	assertEquals(1, result.getSecond());
    }
    
    @Test
	public void testTwoShiftEquivalentSystemStates() {
    	TimedRebecaActorState actor1 = new TimedRebecaActorState(0);
    	actor1.setVariableValue(new Variable(TimedActorScope.TIME_VARIABLE_NAME), 1);
    	TimedRebecaActorState actor2 = new TimedRebecaActorState(1);
    	actor2.setVariableValue(new Variable(TimedActorScope.TIME_VARIABLE_NAME), 1);
    	
    	TimedRebecaMessageState message1 = new TimedRebecaMessageState();
		message1.setName("m1");
		message1.setSender(actor1);
		message1.setReceiver(actor1);
		message1.setParameters(new HashMap<String, Object>());
		message1.setArrival(11);
		message1.setDeadline(21);
		
		TimedRebecaMessageState message2 = new TimedRebecaMessageState();
		message2.setName("m2");
		message2.setSender(actor2);
		message2.setReceiver(actor2);
		message2.setParameters(new HashMap<String, Object>());
		message2.setArrival(21);
		message2.setDeadline(31);

    	TimedRebecaSystemState systemState = new TimedRebecaSystemState();
    	systemState.setActorState(actor1);
    	systemState.setActorState(actor2);
		
    	Pair<Boolean, TransparentActorTransitionSystemState<TimedRebecaSystemState>> result = 
    			transitionSystem.addIfNotExists(transitionSystem.getInitialState(), systemState);

    	assertTrue(result.getFirst());
	}
}

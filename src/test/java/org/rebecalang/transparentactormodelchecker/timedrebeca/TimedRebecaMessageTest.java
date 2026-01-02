package org.rebecalang.transparentactormodelchecker.timedrebeca;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaActorState;
import org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState;

public class TimedRebecaMessageTest {

    TimedRebecaActorState coreRebecaActorState;
    TimedRebecaMessageState message1;
    TimedRebecaMessageState message2;
    
    @BeforeEach
    public void setup() throws CodeCompilationException {
    	coreRebecaActorState = new TimedRebecaActorState(0);
		message1 = new TimedRebecaMessageState();
		message1.setName("m1");
		message1.setSenderId(coreRebecaActorState.getId());
		message1.setReceiverId(coreRebecaActorState.getId());
		message1.setParameters(new HashMap<String, Object>());
		message1.setArrival(10);
		message1.setDeadline(20);
		
		message2 = new TimedRebecaMessageState();
		message2.setName("m2");
		message2.setSenderId(coreRebecaActorState.getId());
		message2.setReceiverId(coreRebecaActorState.getId());
		message2.setParameters(new HashMap<String, Object>());
		message2.setArrival(10);
		message2.setDeadline(20);
    }
    
	@Test
	public void TestNotEqualBecauseOfMessageName() {
		Pair<Boolean, Integer> result = message1.shiftEquals(message2);
		assertFalse(result.getFirst());
		assertEquals(0, result.getSecond());
	}
	
	@Test
	public void TestAreShiftEquivalent() {
		message2.setArrival(20);
		message2.setDeadline(30);
		message2.setName("m1");
		
		Pair<Boolean, Integer> result = message1.shiftEquals(message2);
		assertTrue(result.getFirst());
		assertEquals(10, result.getSecond());
	}
	
	@Test
	public void TestNotEqualBecauseOfParams() {
		message2.setName("m1");
		message2.getParameters().put("p1", 10);
		
		Pair<Boolean, Integer> result = message2.shiftEquals(message1);
		assertFalse(result.getFirst());
	}
	
	@Test
	public void TestNotEqualBecauseOfDeadlines() {
		message2.setName("m1");
		message2.setDeadline(TimedRebecaMessageState.INF);

		Pair<Boolean, Integer> result = message2.shiftEquals(message1);
		assertFalse(result.getFirst());
		
		result = message1.shiftEquals(message2);
		assertFalse(result.getFirst());
		
		message1.setDeadline(TimedRebecaMessageState.INF);
		result = message2.shiftEquals(message1);
		assertTrue(result.getFirst());
		result = message1.shiftEquals(message2);
		assertTrue(result.getFirst());
	}
}

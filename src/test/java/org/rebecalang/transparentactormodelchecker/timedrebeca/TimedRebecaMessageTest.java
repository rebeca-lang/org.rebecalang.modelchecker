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

	@Test
	public void GIVEN_TheSameMessage_WHEN_ShiftEqualsIsCalled_THEN_TheShiftIsZero() {
		message2.setName("m1");

		Pair<Boolean, Integer> result = message1.shiftEquals(message2);

		assertTrue(result.getFirst());
		assertEquals(0, result.getSecond());
	}

	@Test
	public void GIVEN_AnEarlierMessage_WHEN_ShiftEqualsIsCalled_THEN_TheShiftIsNegative() {
		message2.setName("m1");
		message2.setArrival(4);
		message2.setDeadline(14);

		Pair<Boolean, Integer> result = message1.shiftEquals(message2);

		assertTrue(result.getFirst());
		assertEquals(-6, result.getSecond());
	}

	@Test
	public void GIVEN_ArrivalAndDeadlineMoveByDifferentAmounts_WHEN_ShiftEqualsIsCalled_THEN_TheyAreNotEquivalent() {
		message2.setName("m1");
		message2.setArrival(20);
		message2.setDeadline(25);

		assertFalse(message1.shiftEquals(message2).getFirst());
	}

	@Test
	public void GIVEN_ADifferentReceiver_WHEN_ShiftEqualsIsCalled_THEN_TheyAreNotEquivalent() {
		message2.setName("m1");
		message2.setReceiverId(coreRebecaActorState.getId() + 1);

		assertFalse(message1.shiftEquals(message2).getFirst());
	}

	@Test
	public void GIVEN_AMessage_WHEN_ItIsCloned_THEN_TimingIsCopiedAndIndependent() {
		TimedRebecaMessageState clone = message1.clone();

		assertEquals(message1.getName(), clone.getName());
		assertEquals(10, clone.getArrival());
		assertEquals(20, clone.getDeadline());

		clone.setArrival(99);
		assertEquals(10, message1.getArrival());
	}

	private TimedRebecaMessageState incoming(String name, int sender, int arrival) {
		TimedRebecaMessageState message = new TimedRebecaMessageState();
		message.setName(name);
		message.setSenderId(sender);
		message.setReceiverId(coreRebecaActorState.getId());
		message.setParameters(new HashMap<String, Object>());
		message.setArrival(arrival);
		message.setDeadline(arrival + 10);
		return message;
	}

	@Test
	public void GIVEN_AFreshActor_WHEN_TheBagIsInspected_THEN_ItIsEmptyUntilAMessageArrives() {
		assertTrue(coreRebecaActorState.bagIsEmpty());
		assertTrue(coreRebecaActorState.messageQueueIsEmpty());

		coreRebecaActorState.receiveMessage(incoming("m", 1, 10));

		assertFalse(coreRebecaActorState.bagIsEmpty());
		assertFalse(coreRebecaActorState.messageQueueIsEmpty());
	}

	@Test
	public void GIVEN_MessagesArriveOutOfOrder_WHEN_TheyAreReceived_THEN_TheBagIsOrderedByArrival() {
		coreRebecaActorState.receiveMessage(incoming("late", 1, 30));
		coreRebecaActorState.receiveMessage(incoming("early", 2, 10));

		assertEquals(10, coreRebecaActorState.getFirstMessageArrivalTime());
	}

	@Test
	public void GIVEN_MessagesWithDifferentArrivals_WHEN_EnabledIndecesAreAskedForATime_THEN_OnlyArrivedOnesAreEnabled() {
		coreRebecaActorState.receiveMessage(incoming("early", 1, 10));
		coreRebecaActorState.receiveMessage(incoming("late", 2, 30));

		assertEquals(1, coreRebecaActorState.getEnableMessagesIndeces(20).size());
		assertEquals(2, coreRebecaActorState.getEnableMessagesIndeces(30).size());
		assertEquals(0, coreRebecaActorState.getEnableMessagesIndeces(5).size());
	}

	@Test
	public void GIVEN_TwoMessagesFromOneSender_WHEN_EnabledIndecesAreAsked_THEN_OnlyTheFirstIsEnabled() {
		coreRebecaActorState.receiveMessage(incoming("first", 1, 10));
		coreRebecaActorState.receiveMessage(incoming("second", 1, 20));

		assertEquals(1, coreRebecaActorState.getEnableMessagesIndeces(30).size());
		assertEquals(0, coreRebecaActorState.getEnableMessagesIndeces(30).get(0));
	}

	@Test
	public void GIVEN_AMessageInTheBag_WHEN_ItIsTakenAsEnabled_THEN_ItLeavesTheBag() {
		coreRebecaActorState.receiveMessage(incoming("m", 1, 10));

		TimedRebecaMessageState taken = coreRebecaActorState.getEnableMessage(0);

		assertEquals("m", taken.getName());
		assertTrue(coreRebecaActorState.bagIsEmpty());
	}
}

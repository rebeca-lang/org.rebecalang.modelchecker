package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.HashMap;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;

@SuppressWarnings("serial")
public class TimedRebecaMessageState extends AbstractMessageState implements Serializable, Cloneable {
	
	protected final static Pair<Boolean, Integer> FALSE = new Pair<Boolean, Integer>(false, 0);
	protected final static Pair<Boolean, Integer> TRUE = new Pair<Boolean, Integer>(true, 0);
	public final static int INF = Integer.MAX_VALUE;

	protected int arrival;
	protected int deadline;
	
	public TimedRebecaMessageState() {
		super();
	}
	public TimedRebecaMessageState(String name, HashMap<String, Object> parameters) {
		super(name, parameters);
	}
	
	public int getArrival() {
		return arrival;
	}
	public void setArrival(int arrival) {
		this.arrival = arrival;
	}
	public int getDeadline() {
		return deadline;
	}
	public void setDeadline(int deadline) {
		this.deadline = deadline;
	}
	public String toString() {
		return super.toString() + "after(" + arrival + ") deadline(" + (deadline == Integer.MAX_VALUE ? "INF)" : (deadline + ")")); 
	}
	
	public TimedRebecaMessageState clone() {
		TimedRebecaMessageState clonedMessageState = new TimedRebecaMessageState();
		clone(clonedMessageState);
		clonedMessageState.arrival = this.arrival;
		clonedMessageState.deadline = this.deadline;
		
		return clonedMessageState;
	}
	
	public Pair<Boolean, Integer> shiftEquals(TimedRebecaMessageState other) {
		if (!this.equals(other))
			return FALSE;
		int shift = other.arrival - this.arrival;
		if(this.deadline == INF)
			if(other.deadline != INF)
				return FALSE;
			else
				return new Pair<Boolean, Integer>(true, shift);
		if(other.deadline == INF)
			return FALSE;
		if(shift != other.deadline - this.deadline)
			return FALSE;
		return new Pair<Boolean, Integer>(true, shift);
	}
}

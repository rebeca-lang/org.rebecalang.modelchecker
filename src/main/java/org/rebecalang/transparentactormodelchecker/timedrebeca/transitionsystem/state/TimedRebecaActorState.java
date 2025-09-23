package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BaseClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorScope;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.FALSE;
import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.TRUE;
import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedActorScope.TIME_VARIABLE_NAME;
@SuppressWarnings("serial")
public class TimedRebecaActorState extends AbstractActorState implements Serializable, Cloneable {

	protected ArrayList<TimedRebecaMessageState> queue;

	public TimedRebecaActorState(int id) {
		super(id);
		queue = new ArrayList<TimedRebecaMessageState>();
	}
	
	public TimedRebecaMessageState getFirstMessage() {
		return queue.remove(0);
	}

	public void receiveMessage(TimedRebecaMessageState newMessage) {
		queue.add(newMessage);
	}

	public String toString() {
		return "actor->" + id;
	}
	public String deepToString() {
		String result = super.deepToString();
		result = result.substring(0, result.length() - 1);
		return result + ",\n queue:(" + queue + ")]";
	}

	public void movePCtoTheNextInstruction() {
		Pair<String, Integer> pc = getPC();
		pc.setSecond(pc.getSecond() + 1);
	}

	public static TimedRebecaActorState createTempTimedRebecaActorState() {
		return new TimedRebecaActorState(-1);
	}

	public static TimedRebecaActorState createTempCoreRebecaActorState(Type type) {
		TimedRebecaActorState temp = createTempTimedRebecaActorState();
		try {
			BaseClassDeclaration metaData = type.getTypeSystem().getMetaData(type);
			if(metaData instanceof ReactiveClassDeclaration) {
				ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) metaData;
				temp.addVariablesToScope(rcd.getStatevars());
				temp.addVariablesToScope(rcd.getKnownRebecs());
				temp.addVariableToScope("self", temp);
			}
		} catch (CodeCompilationException e) {
			e.printStackTrace();
		}
		return temp;
	}

	public int deepHashCode() {
		final int prime = 31;
		int result = super.deepHashCode();
		result = prime * result + ((queue == null) ? 0 : queue.hashCode());
		return result;
	}
	
	public boolean deepEquals(Object obj) {
		if (this == obj)
			return true;
		if(!super.deepEquals(obj))
			return false;
		TimedRebecaActorState other = (TimedRebecaActorState) obj;
		if (queue == null) {
			if (other.queue != null)
				return false;
		} else if (!queue.equals(other.queue))
			return false;
		return true;
	}

	public TimedRebecaActorState clone() {
		TimedRebecaActorState actor = (TimedRebecaActorState) CloningRepository.getActor(this.id);
		if(actor != null)
			return actor;
		TimedRebecaActorState clonedState = new TimedRebecaActorState(this.id);
		CloningRepository.addActor(clonedState);
		clonedState.queue = CloningRepository.cloneArrayList(this.queue);
		clonedState.priority = this.priority;
		clonedState.rilModel = this.rilModel;
		clonedState.scope = this.scope.clone();
		return clonedState;
	}

	public boolean messageQueueIsEmpty() {
		return queue.isEmpty();
	}
	
	public Pair<Boolean, Integer> shiftEquals(TimedRebecaActorState other) {
		int thisTime = (int) this.scope.getVariableValue(TIME_VARIABLE_NAME);
		int otherTime = (int) other.scope.getVariableValue(TIME_VARIABLE_NAME);
		int shift = otherTime - thisTime;
		if(this.deepEquals(other))
			return FALSE;
		if(this.queue.size() != other.queue.size())
			return FALSE;
		if(this.queue.size() == 0)
			return TRUE;
		
		for(int cnt = 0; cnt < this.queue.size(); cnt++) {
			Pair<Boolean, Integer> result = 
					this.queue.get(cnt).shiftEquals(other.queue.get(cnt));
			if(!result.getFirst())
				return FALSE;
			if(shift != result.getSecond())
				return FALSE;
		}
		return new Pair<Boolean, Integer>(true, shift);
	}

	@Override
	public ActorScope getNewActorScope() {
		return new TimedActorScope();
	}
}

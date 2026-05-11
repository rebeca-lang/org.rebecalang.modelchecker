package org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state;

import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedActorScope.TIME_VARIABLE;
import static org.rebecalang.transparentactormodelchecker.timedrebeca.transitionsystem.state.TimedRebecaMessageState.FALSE;

import java.io.Serializable;
import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BaseClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorScope;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

@SuppressWarnings("serial")
public class TimedRebecaActorState extends AbstractActorState implements Serializable, Cloneable {

	protected ArrayList<TimedRebecaMessageState> bag;

	public TimedRebecaActorState(int id) {
		super(id);
		bag = new ArrayList<TimedRebecaMessageState>();
	}
	
	public void receiveMessage(AbstractMessageState newMessage) {
		TimedRebecaMessageState message = (TimedRebecaMessageState) newMessage;
		bag.add(message);
		for(int cnt = bag.size() - 2; cnt >= 0; cnt--) {
			int arrival = bag.get(cnt).getArrival();
			if(message.getArrival() > arrival)
				break;
			if(message.getArrival() == arrival)
				if(message.getName().compareTo(bag.get(cnt).getName()) < 0)
					break;
			bag.set(cnt + 1, bag.get(cnt));
			bag.set(cnt, message);
		}
	}

	public String deepToString() {
		String result = super.deepToString();
		result = result.substring(0, result.length() - 1);
		return result + ",\nbag:(" + bag + ")]";
	}

	private static TimedRebecaActorState createTempTimedRebecaActorState() {
		return new TimedRebecaActorState(-1);
	}

	private static TimedRebecaActorState createTempTimedRebecaActorState(Type type) {
		TimedRebecaActorState temp = createTempTimedRebecaActorState();
		try {
			BaseClassDeclaration metaData = type.getTypeSystem().getMetaData(type);
			if(metaData instanceof ReactiveClassDeclaration) {
				ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) metaData;
				temp.addFieldsVariablesToScope(rcd.getStatevars());
				temp.addFieldsVariablesToScope(rcd.getKnownRebecs());
			}
		} catch (CodeCompilationException e) {
			e.printStackTrace();
		}
		return temp;
	}

	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((bag == null) ? 0 : bag.hashCode());
		return result;
	}
//	public int deepHashCode() {
//		final int prime = 31;
//		int result = super.deepHashCode();
//		result = prime * result + ((bag == null) ? 0 : bag.hashCode());
//		return result;
//	}
	
	public boolean deepEquals(Object obj) {
		if (this == obj)
			return true;
		if(!super.deepEquals(obj))
			return false;
		TimedRebecaActorState other = (TimedRebecaActorState) obj;
		if (bag == null) {
			if (other.bag != null)
				return false;
		} else if (!bag.equals(other.bag))
			return false;
		return true;
	}

	public TimedRebecaActorState memoizedClone() {
		TimedRebecaActorState actor = (TimedRebecaActorState) CloningRepository.getActor(this.id);
		if(actor != null)
			return actor;
		TimedRebecaActorState clonedState = new TimedRebecaActorState(this.id);
		CloningRepository.addActor(clonedState);
		clonedState.bag = CloningRepository.cloneArrayList(this.bag);
		clonedState.priority = this.priority;
		clonedState.rilModel = this.rilModel;
		clonedState.scope = this.scope.clone();
		return clonedState;
	}

	public boolean bagIsEmpty() {
		return bag.isEmpty();
	}
	
	public Pair<Boolean, Integer> shiftEquals(TimedRebecaActorState other) {
		int thisTime = (int) this.scope.getVariableValue(TIME_VARIABLE);
		int otherTime = (int) other.scope.getVariableValue(TIME_VARIABLE);
		int shift = otherTime - thisTime;
		if(!this.deepEquals(other))
			return FALSE;
		if(this.bag.size() != other.bag.size())
			return FALSE;
		if(this.bag.size() == 0)
			return new Pair<Boolean, Integer>(true, shift);
		
		for(int cnt = 0; cnt < this.bag.size(); cnt++) {
			Pair<Boolean, Integer> result = 
					this.bag.get(cnt).shiftEquals(other.bag.get(cnt));
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

	@Override
	public boolean isEnable() {
		return hasVariableInScope(AbstractActorState.PC) || !bag.isEmpty();
	}

	@Override
	public AbstractMessageState getNewMessageState() {
		return new TimedRebecaMessageState();
	}

	@Override
	public AbstractActorState createNewActorState(Type type) {
		return createTempTimedRebecaActorState(type);
	}

	public boolean messageQueueIsEmpty() {
		return bag.isEmpty();
	}

	public TimedRebecaMessageState getEnableMessage(int index) {
		return bag.remove(index);
	}
	
	public ArrayList<Integer> getEnableMessagesIndeces(int time) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for(int cnt = 0; cnt < bag.size(); cnt++) {
			if(bag.get(cnt).getArrival() > time)
				break;
			boolean repeated = false;
			for(int cnt2 = 0; cnt2 < cnt; cnt2++) {
				if(bag.get(cnt2).getSenderId() == bag.get(cnt).getSenderId()) {
					repeated = true;
					break;
				}
			}
			if(!repeated)
				result.add(cnt);
		}
		return result;
	}

	public int getFirstMessageArrivalTime() {
		return bag.get(0).arrival;
	}
}

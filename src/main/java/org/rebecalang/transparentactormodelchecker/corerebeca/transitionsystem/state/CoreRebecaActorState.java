package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BaseClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

@SuppressWarnings("serial")
public class CoreRebecaActorState extends AbstractActorState implements Serializable, Cloneable {

	protected ArrayList<CoreRebecaMessageState> queue;

	public CoreRebecaActorState(int id) {
		super(id);
		queue = new ArrayList<CoreRebecaMessageState>();
	}
	
	public CoreRebecaMessageState getFirstMessage() {
		return queue.remove(0);
	}

	public void receiveMessage(CoreRebecaMessageState newMessage) {
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

	public static CoreRebecaActorState createTempCoreRebecaActorState() {
		return new CoreRebecaActorState(-1);
	}

	public static CoreRebecaActorState createTempCoreRebecaActorState(Type type) {
		CoreRebecaActorState temp = createTempCoreRebecaActorState();
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
		CoreRebecaActorState other = (CoreRebecaActorState) obj;
		if (queue == null) {
			if (other.queue != null)
				return false;
		} else if (!queue.equals(other.queue))
			return false;
		return true;
	}

	public CoreRebecaActorState clone() {
		CoreRebecaActorState actor = (CoreRebecaActorState) CloningRepository.getActor(this.id);
		if(actor != null)
			return actor;
		CoreRebecaActorState clonedState = new CoreRebecaActorState(this.id);
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
}

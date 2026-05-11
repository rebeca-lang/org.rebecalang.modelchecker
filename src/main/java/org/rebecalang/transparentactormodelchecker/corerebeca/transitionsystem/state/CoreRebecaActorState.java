package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.io.Serializable;
import java.util.ArrayList;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.BaseClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractMessageState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorScope;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;

@SuppressWarnings("serial")
public class CoreRebecaActorState extends AbstractActorState implements Serializable, Cloneable {

	protected ArrayList<CoreRebecaMessageState> queue;
	
	public CoreRebecaActorState(int id) {
		super(id);
		queue = new ArrayList<CoreRebecaMessageState>();
	}
	
	public CoreRebecaMessageState getEnabledMessage() {
		return queue.isEmpty() ? null : queue.remove(0);
	}

	@Override
	public void receiveMessage(AbstractMessageState newMessage) {
		queue.add((CoreRebecaMessageState) newMessage);
	}

	public String deepToString() {
		String result = super.deepToString();
		result = result.substring(0, result.length() - 1);
		return result + ",\nqueue:(" + queue + ")]";
	}

	private static CoreRebecaActorState createTempCoreRebecaActorState() {
		return new CoreRebecaActorState(-1);
	}

	private static CoreRebecaActorState createTempCoreRebecaActorState(Type type) {
		CoreRebecaActorState temp = createTempCoreRebecaActorState();
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
		result = prime * result + ((queue == null) ? 0 : queue.hashCode());
		return result;
	}
	
//	public int deepHashCode() {
//		final int prime = 31;
//		int result = super.deepHashCode();
//		result = prime * result + ((queue == null) ? 0 : queue.hashCode());
//		return result;
//	}
//	
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

	public CoreRebecaActorState memoizedClone() {
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

	@Override
	public ActorScope getNewActorScope() {
		return new ActorScope();
	}

	@Override
	public boolean isEnable() {
		return hasVariableInScope(AbstractActorState.PC) || !queue.isEmpty();
	}

	@Override
	public AbstractMessageState getNewMessageState() {
		return new CoreRebecaMessageState();
	}

	@Override
	public AbstractActorState createNewActorState(Type type) {
		return createTempCoreRebecaActorState(type);
	}
}

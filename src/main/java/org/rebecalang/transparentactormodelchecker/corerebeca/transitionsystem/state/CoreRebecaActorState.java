package org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state;

import java.util.ArrayList;

import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.corerebeca.RebecaRuntimeInterpreterException;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

@SuppressWarnings("serial")
public class CoreRebecaActorState extends CoreRebecaAbstractState {

	transient public final static String PC = "$PC$";
	transient public final static Integer MIN_PRIORITY = Integer.MAX_VALUE - 1;

	transient RILModel rilModel;

	private int id;
	private int priority;
	private CoreRebecaActorScope scope;
	private ArrayList<CoreRebecaMessageState> queue;

	public CoreRebecaActorState(int id) {
		this.id = id;
		this.priority = MIN_PRIORITY;
		scope = new CoreRebecaActorScope();
		queue = new ArrayList<CoreRebecaMessageState>();
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}
	
	public RILModel getRILModel() {
		return rilModel;
	}

	public void setRILModel(RILModel rilModel) {
		this.rilModel = rilModel;
	}

	public boolean messageQueueIsEmpty() {
		return queue.isEmpty();
	}

	public CoreRebecaMessageState getFirstMessage() {
		return queue.remove(0);
	}

	public void receiveMessage(CoreRebecaMessageState newMessage) {
		queue.add(newMessage);
	}

	@SuppressWarnings("unchecked")
	public InstructionBean getEnabledInstruction() {
		Pair<String, Integer> pc = (Pair<String, Integer>) scope.getVariableValue(PC);
		if (pc == null)
			throw new RebecaRuntimeInterpreterException("No enabled instruction");

		ArrayList<InstructionBean> instructionsList = rilModel.getInstructionList(pc.getFirst());

		return instructionsList.get(pc.getSecond());
	}

	public void setEnvironment(Environment environment) {
		scope.setEnvironment(environment);
	}

	public String toString() {
		return id + "\n[scope:(" + scope + "),\n queue:(" + queue + ")]";
	}

	@SuppressWarnings("unchecked")
	public void movePCtoTheNextInstruction() {
		Pair<String, Integer> pc = (Pair<String, Integer>) scope.getVariableValue(PC);
		pc.setSecond(pc.getSecond() + 1);
	}

	public boolean hasVariableInScope(String varName) {
		return scope.hasVariableInScope(varName);
	}

	public void pushToScope() {
		scope.pushToScope();
	}

	public void addVariableToScope(String varName, Object varValue) {
		scope.addVariableToScope(varName, varValue);
	}

	public Object getVariableValue(String varName) {
		return varName.equals("self") ? this : scope.getVariableValue(varName);
	}

	public void setVariableValue(Variable varName, Object varValue) {
		scope.setVariableValue(varName, varValue);
	}

	public void popFromScope() {
		scope.popFromScope();
	}

	public void addVariableToScope(String varName) {
		scope.addVariableToScope(varName);
	}
	
	public boolean isTempActorState() {
		return id == -1;
	}
	
	public static CoreRebecaActorState createTempCoreRebecaActorState() {
		return new CoreRebecaActorState(-1);
	}
}

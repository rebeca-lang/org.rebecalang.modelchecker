package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;

@SuppressWarnings("serial")
public abstract class AbstractActorState implements Serializable, Cloneable {

	public transient final static String PC = "$PC$";
	protected transient final static Variable PC_VARIABLE = new Variable("$PC$");
	public transient final static Integer MIN_PRIORITY = Integer.MAX_VALUE - 1;

	protected transient RILModel rilModel;

	protected int id;
	protected int priority;
	protected ActorScope scope;

	public AbstractActorState(int id) {
		this.id = id;
		this.priority = MIN_PRIORITY;
		scope = new ActorScope();
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

	public InstructionBean getEnabledInstruction() {
		Pair<String, Integer> pc = getPC();
		if (pc == null)
			return null;

		ArrayList<InstructionBean> instructionsList = rilModel.getInstructionList(pc.getFirst());

		return instructionsList.get(pc.getSecond());
	}

	@SuppressWarnings("unchecked")
	public Pair<String, Integer> getPC() {
		return (Pair<String, Integer>) scope.getVariableValue(PC);
	}

	public void setPC(Pair<String, Integer> pc) {
		scope.setVariableValue(PC_VARIABLE, pc);
	}

	public void setEnvironment(Environment environment) {
		scope.setEnvironment(environment);
	}

	public String toString() {
		return "actor->" + id;
	}
	public String deepToString() {
		return id + "->[scope:(" + scope + ")]";
	}

	public void movePCtoTheNextInstruction() {
		Pair<String, Integer> pc = getPC();
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
//
//	public static AbstractActorState<? extends AbstractMessageState> createTempCoreRebecaActorState() {
//		return new AbstractActorState(-1);
//	}

//	public static AbstractActorState<? extends AbstractMessageState> createTempCoreRebecaActorState(Type type) {
//		AbstractActorState<? extends AbstractMessageState> temp = createTempCoreRebecaActorState();
//		try {
//			BaseClassDeclaration metaData = type.getTypeSystem().getMetaData(type);
//			if(metaData instanceof ReactiveClassDeclaration) {
//				ReactiveClassDeclaration rcd = (ReactiveClassDeclaration) metaData;
//				temp.addVariablesToScope(rcd.getStatevars());
//				temp.addVariablesToScope(rcd.getKnownRebecs());
//				temp.addVariableToScope("self", temp);
//			}
//		} catch (CodeCompilationException e) {
//			e.printStackTrace();
//		}
//		return temp;
//	}


	protected void addVariablesToScope(List<FieldDeclaration> fields) {
		for(FieldDeclaration fd : fields)
			for(VariableDeclarator vd : fd.getVariableDeclarators())
				addVariableToScope(vd.getVariableName());
	}

	@Override
	public int hashCode() {
		return id;
	}

	public int deepHashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + priority;
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractActorState other = (AbstractActorState) obj;
		return id == other.id;
	}
	
	public boolean deepEquals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractActorState other = (AbstractActorState) obj;
		if (id != other.id)
			return false;
		if (priority != other.priority)
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		return true;
	}

	public abstract AbstractActorState clone();
}

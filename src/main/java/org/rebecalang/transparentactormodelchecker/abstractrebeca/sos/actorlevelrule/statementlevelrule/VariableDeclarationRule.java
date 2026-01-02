package org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule;

import java.lang.reflect.Array;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ArrayType;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Type;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.AbstractSOSRule;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
import org.springframework.stereotype.Component;

@Component
public class VariableDeclarationRule extends AbstractSOSRule<AbstractActorState> {

	@Override
	public Transition<AbstractActorState> applyRule(
			AbstractActorState base, AbstractActorState state, Object... additional) {

		DeclarationInstructionBean vdib = (DeclarationInstructionBean) additional[0];
		Type type = vdib.getType();
		if(type instanceof ArrayType) {
			ArrayType arrayType = (ArrayType) type;
			List<Integer> arrayDimensions = arrayType.getDimensions();
			int[] dimentions = new int[arrayDimensions.size()];
			for(int cnt = 0; cnt < arrayDimensions.size(); cnt++) {
				dimentions[cnt] = arrayDimensions.get(cnt);
			}
			state.addVariableToScope(vdib.getVarName(), Array.newInstance(Object.class, dimentions));
		} else
			state.addVariableToScope(vdib.getVarName(), null);
		state.movePCtoTheNextInstruction();

		return Transition.createDeterministicTauTransition(state);
	}
}
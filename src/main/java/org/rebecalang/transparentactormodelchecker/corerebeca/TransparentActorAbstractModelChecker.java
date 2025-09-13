package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.MethodDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.RILUtilities;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.RuleIsDisabledException;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystem;
import org.rebecalang.transparentactormodelchecker.TransparentActorTransitionSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractSystemState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.Environment;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelsosrule.CoreRebecaCompositionLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.compositionlevelsosrule.CoreRebecaCompositionLevelNetworkDeliverySOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.StateGenerationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("CORE_REBECA")
public abstract class TransparentActorAbstractModelChecker<T extends AbstractSystemState> {

	protected HashMap<String, String> methodLookupTable;
	protected Pair<RebecaModel,SymbolTable> compiledRebecaFile;
	protected RILModel rilModel;
	
	protected TransparentActorTransitionSystem<T> transitionSystem;

	@Autowired
	protected CoreRebecaCompositionLevelExecuteStatementSOSRule compositionLevelExecuteStatementSOSRule;
	@Autowired
	protected CoreRebecaCompositionLevelNetworkDeliverySOSRule compositionLevelNetworkDeliverySOSRule;

	protected abstract T createSystemState();
	
	protected void setInitialState() {
		Environment environment = 
				StateGenerationUtils.getEnvironment(compiledRebecaFile.getFirst());		
		
		T systemState = createSystemState();
		systemState.setEnvironment(environment);
		
		TransparentActorTransitionSystemState<T> initialState = 
				new TransparentActorTransitionSystemState<T>(0);
		initialState.setState(systemState);
		transitionSystem.setInitialState(initialState);
		initialState.setNextStates(new ArrayList<TransparentActorTransitionSystemState<T>>());
		initialState.setPreviousStates(new ArrayList<TransparentActorTransitionSystemState<T>>());
		
		initializeMethodBindingTable();
		
		executeMainBody();
	}

	private void executeMainBody() {
		TransparentActorTransitionSystemState<T> initialState = 
				transitionSystem.getInitialState();
		T state = initialState.getState();
		
		CoreRebecaActorState tempState = CoreRebecaActorState.createTempCoreRebecaActorState();
		tempState.setRILModel(rilModel);
	
		ArrayList<InstructionBean> instructions = rilModel.getInstructionList("main");
		InstructionBean lastStatement = instructions.remove(instructions.size() - 2);
	
		tempState.addVariableToScope(CoreRebecaActorState.PC, 
				new Pair<String, Integer>("main", 0));
		state.setActorState(tempState);
		tempState.setPriority(Integer.MAX_VALUE);
		
		try {
			while(true) {
				T backup = (T) state.clone();
				compositionLevelExecuteStatementSOSRule.applyRule((CoreRebecaSystemState) backup, (CoreRebecaSystemState) state);
			}
		} catch(RuleIsDisabledException exception) {}
		
		try {
			while(true) {
				T backup = (T) state.clone();
				compositionLevelNetworkDeliverySOSRule.applyRule((CoreRebecaSystemState) backup, (CoreRebecaSystemState) state);
			}
		} catch(RuleIsDisabledException exception) {}
		
		state.destroyActorState(tempState);
		
		instructions.add(instructions.size() - 1, lastStatement);
	}

	private void initializeMethodBindingTable() {
		RebecaModel rebecaModel = compiledRebecaFile.getFirst();
		
		methodLookupTable = new HashMap<String, String>();
		List<ReactiveClassDeclaration> rcds = rebecaModel.getRebecaCode().getReactiveClassDeclaration();
		for(ReactiveClassDeclaration rcd : rcds) {
			for(MethodDeclaration md : rcd.getSynchMethods()) {
				String methodName = RILUtilities.computeMethodName(rcd, md);
				methodLookupTable.put(methodName, methodName);
			}
			for(MethodDeclaration md : rcd.getMsgsrvs()) {
				String methodName = RILUtilities.computeMethodName(rcd, md);
				methodLookupTable.put(methodName, methodName);
			}
			for(MethodDeclaration md : rcd.getConstructors()) {
				String methodName = RILUtilities.computeMethodName(rcd, md);
				methodLookupTable.put(methodName, methodName);
			}
		}
		compositionLevelExecuteStatementSOSRule.setMethodLookupTable(methodLookupTable);
	}

	public abstract TransparentActorModelCheckingResult modelcheck(Pair<RebecaModel,SymbolTable> compiledRebecaFile, RILModel rilModel);

}
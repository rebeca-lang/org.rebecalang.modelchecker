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
import org.rebecalang.transparentactormodelchecker.corerebeca.compositionlevelsosrule.CoreRebecaCompositionLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.compositionlevelsosrule.CoreRebecaCompositionLevelNetworkDeliverySOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.Environment;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.StateGenerationUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TransparentActorCoreRebecaAbstractModelChecker {

	protected TransparentActorCoreRebecaTransitionSystem transitionSystem;
	protected HashMap<String, String> methodLookupTable;
	protected Pair<RebecaModel,SymbolTable> compiledRebecaFile;
	protected RILModel rilModel;
	@Autowired
	protected CoreRebecaCompositionLevelExecuteStatementSOSRule compositionLevelExecuteStatementSOSRule;
	@Autowired
	protected CoreRebecaCompositionLevelNetworkDeliverySOSRule compositionLevelNetworkDeliverySOSRule;
	
	@Autowired
	protected CoreRebecaSOSRule sosRule;

	public TransparentActorCoreRebecaAbstractModelChecker() {
		super();
	}

	protected void setInitialState() {
		Environment environment = 
				StateGenerationUtils.getEnvironment(compiledRebecaFile.getFirst());		
		
		CoreRebecaSystemState systemState = new CoreRebecaSystemState();
		systemState.setEnvironment(environment);
		
		TransparentActorCoreRebecaTransitionSystemState initialState = 
				new TransparentActorCoreRebecaTransitionSystemState(0);
		initialState.setState(systemState);
		transitionSystem.setInitialState(initialState);
		initialState.setNextStates(new ArrayList<TransparentActorCoreRebecaTransitionSystemState>());
		initialState.setPreviousStates(new ArrayList<TransparentActorCoreRebecaTransitionSystemState>());
		
		initializeMethodBindingTable();
		
		executeMainBody();
	}

	private void executeMainBody() {
		TransparentActorCoreRebecaTransitionSystemState initialState = 
				transitionSystem.getInitialState();
		CoreRebecaSystemState state = initialState.getState();
		
		CoreRebecaActorState tempState = CoreRebecaActorState.createTempCoreRebecaActorState();
		tempState.setRILModel(rilModel);

		ArrayList<InstructionBean> instructions = rilModel.getInstructionList("main");
		InstructionBean lastStatement = instructions.remove(instructions.size() - 2);

		tempState.addVariableToScope(CoreRebecaActorState.PC, 
				new Pair<String, Integer>("main", 0));
		state.setActorState(tempState);
		tempState.setPriority(Integer.MAX_VALUE);
		
		try {
			while(true)
				compositionLevelExecuteStatementSOSRule.applyRule(state);
		} catch(RuleIsDisabledException exception) {}
		
		try {
			while(true)
				compositionLevelNetworkDeliverySOSRule.applyRule(state);
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
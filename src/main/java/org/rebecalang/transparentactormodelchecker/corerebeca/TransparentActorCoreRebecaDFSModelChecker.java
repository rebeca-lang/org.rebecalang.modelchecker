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
import org.rebecalang.transparentactormodelchecker.corerebeca.compositionlevelsosrule.CoreRebecaCompositionLevelExecuteStatementSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.compositionlevelsosrule.CoreRebecaCompositionLevelNetworkDeliverySOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaSystemState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.Environment;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaNondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.corerebeca.utils.StateGenerationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("CORE_REBECA")
public class TransparentActorCoreRebecaDFSModelChecker {

	private TransparentActorCoreRebecaTransitionSystem transitionSystem;
	
	private HashMap<String, String> methodLookupTable;

	private Pair<RebecaModel,SymbolTable> compiledRebecaFile;
		
	private RILModel rilModel;
	
	@Autowired
	CoreRebecaCompositionLevelExecuteStatementSOSRule compositionLevelExecuteStatementSOSRule; 
	
	@Autowired
	CoreRebecaCompositionLevelNetworkDeliverySOSRule compositionLevelNetworkDeliverySOSRule;

	@Autowired
	CoreRebecaSOSRule sosRule;
	
	public void modelcheck(Pair<RebecaModel,SymbolTable> compiledRebecaFile, RILModel rilModel) {
		this.compiledRebecaFile = compiledRebecaFile;
		this.rilModel = rilModel;
		transitionSystem = new TransparentActorCoreRebecaTransitionSystem();
		setInitialState();
		
		TransparentActorCoreRebecaTransitionSystemState initialState = 
				transitionSystem.getInitialState();
		
		CoreRebecaNondeterministicTransition<CoreRebecaSystemState> transitions = 
				(CoreRebecaNondeterministicTransition<CoreRebecaSystemState>) sosRule.applyRule(initialState.getState());
		transitions.getDestinations();
	}

	private void setInitialState() {
		Environment environment = 
				StateGenerationUtils.getEnvironment(compiledRebecaFile.getFirst());		
		
		CoreRebecaSystemState systemState = new CoreRebecaSystemState();
		systemState.setEnvironment(environment);
		
		TransparentActorCoreRebecaTransitionSystemState initialState = 
				TransparentActorCoreRebecaTransitionSystemState.createEmptyState();
		initialState.setState(systemState);
		transitionSystem.setInitialState(initialState);
		
		initializeMethodBindingTable();
		
		executeMainBody();
	}

	private void executeMainBody() {
		TransparentActorCoreRebecaTransitionSystemState initialState = 
				transitionSystem.getInitialState();
		CoreRebecaSystemState state = initialState.getState();
		
		CoreRebecaActorState tempState = CoreRebecaActorState.createTempCoreRebecaActorState();
		tempState.setRILModel(rilModel);
		removeEndMethodFromMainBlock(rilModel.getInstructionList("main"));
		tempState.addVariableToScope(CoreRebecaActorState.PC, 
				new Pair<String, Integer>("main", 0));
		state.setActorState(tempState);
		tempState.setPriority(Integer.MAX_VALUE);
		
		while (tempState.getVariableValue(CoreRebecaActorState.PC) != null) {
			compositionLevelExecuteStatementSOSRule.applyRule(state);
		}
		while(true) {
			if(compositionLevelNetworkDeliverySOSRule.applyRule(state).
					getDestinations().isEmpty())
				break;
		}
		state.destroyActorState(tempState);
	}

	private void removeEndMethodFromMainBlock(ArrayList<InstructionBean> instructionList) {
		ArrayList<InstructionBean> instructions = rilModel.getInstructionList("main");
		instructions.remove(instructions.size() - 2);
	}

	//TODO Polymorphism is ignored!
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
}

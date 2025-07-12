package org.rebecalang.transparentactormodelchecker;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryPrimitiveType;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule.CoreRebecaAssignmentSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule.CoreRebecaRebecInstantiationSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.statementlevelrule.CoreRebecaVariableDeclarationSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.action.NewInstanceAction;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.transition.CoreRebecaDeterministicTransition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class StatementsSOSRulesTest {
	
	@Autowired
	public ExceptionContainer exceptionContainer;
	
	@Autowired
	protected GenericApplicationContext appContext;
	
    @Autowired
    protected RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;
    
    @Autowired
    protected CoreRebecaAssignmentSOSRule assignmentSOSRule;

    @Autowired
    protected CoreRebecaRebecInstantiationSOSRule rebecaRebecInstantiationSOSRule;
    
    @Autowired
    protected CoreRebecaVariableDeclarationSOSRule variableDeclarationSOSRule;
    
//    @Mock
    protected CoreRebecaTypeSystem typeSystem;
    
    CoreRebecaActorState coreRebecaActorState;
    
    @BeforeEach
    public void setup() throws CodeCompilationException {
    	coreRebecaActorState = new CoreRebecaActorState(0);
    	typeSystem = new CoreRebecaTypeSystem();
    	typeSystem.clear();
    	OrdinaryPrimitiveType type = new OrdinaryPrimitiveType();
    	type.setName("A");
    	typeSystem.addNewType(type);
    }

    @Test
    public void GIVEN_ActorStateIsEmpty_WHEN_DeclarationInstructionIsExecuted_THEN_ANewVariableIsAddedToTheState() {
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	DeclarationInstructionBean dib = new DeclarationInstructionBean("var1");
    	Pair<CoreRebecaActorState, InstructionBean> state = new Pair<CoreRebecaActorState, InstructionBean>(coreRebecaActorState, dib);
		variableDeclarationSOSRule.applyRule(state);
    	
		Variable v = new Variable("var1");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v, 10, null, null);
		state.setSecond(aib);
		assignmentSOSRule.applyRule(state);
		
		Assertions.assertEquals(10, state.getFirst().getVariableValue("var1"));
    }
    
    @Test
    public void GIVEN_ActorStateHasThreeVariables_WHEN_AssignmentInstructionIsExecuted_THEN_CalculatedValueHasToSetInTheState() {

    	coreRebecaActorState.addVariableToScope("var1", 1);
    	coreRebecaActorState.addVariableToScope("var2", 2);
    	coreRebecaActorState.addVariableToScope("var3", 3);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	
		Variable v1 = new Variable("var1");
		Variable v2 = new Variable("var2");
		Variable v3 = new Variable("var3");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v1, v2, v3, "-");
    	Pair<CoreRebecaActorState, InstructionBean> state = new Pair<CoreRebecaActorState, InstructionBean>(coreRebecaActorState, aib);
		state.setSecond(aib);
		assignmentSOSRule.applyRule(state);
		
		Assertions.assertEquals(state.getFirst().getVariableValue("var1"), -1);
    }
    
    @Test
    public void rebecInstantiationTest() throws CodeCompilationException {
    	coreRebecaActorState.addVariableToScope("var1", 1);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
		Variable v1 = new Variable("var1");
		RebecInstantiationInstructionBean riib = new RebecInstantiationInstructionBean();
		riib.setType(typeSystem.getType("A"));
		riib.setResultTarget(v1);
    	Pair<CoreRebecaActorState, InstructionBean> state = new Pair<CoreRebecaActorState, InstructionBean>(coreRebecaActorState, riib);
		CoreRebecaDeterministicTransition<Pair<CoreRebecaActorState, InstructionBean>> result = rebecaRebecInstantiationSOSRule.applyRule(state);
		Assertions.assertEquals(result.getAction().getClass(), NewInstanceAction.class);

    }
}
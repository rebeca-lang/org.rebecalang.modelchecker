package org.rebecalang.transparentactormodelchecker.corerebeca;

import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.ReactiveClassDeclaration;
import org.rebecalang.compiler.utils.CodeCompilationException;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.AssignmentInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.DeclarationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.JumpIfNotInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.NonDetValue;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.RebecInstantiationInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.ReturnInstructionBean;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckerConfig;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.NewInstanceAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.AssignmentRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.ConditionalRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.RebecInstantiationRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.ReturnRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.actorlevelrule.statementlevelrule.VariableDeclarationRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActivationRecord;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorScope;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActorsContainer;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
import org.rebecalang.transparentactormodelchecker.transitionsystem.Transition;
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
    protected AssignmentRule assignmentSOSRule;

    @Autowired
    protected ReturnRule returnSOSRule;

    @Autowired
    protected RebecInstantiationRule rebecInstantiationSOSRule;
    
    @Autowired
    protected VariableDeclarationRule variableDeclarationSOSRule;
    
    @Autowired
    protected ConditionalRule conditionalSOSRule;
    
    protected CoreRebecaTypeSystem typeSystem;
    
    CoreRebecaActorState coreRebecaActorState;
    
    @BeforeEach
    public void setup() throws CodeCompilationException {
    	coreRebecaActorState = new CoreRebecaActorState(0);
    	ActivationRecord environment = new ActivationRecord();
    	ActorsContainer actorsContainer = new ActorsContainer();
    	coreRebecaActorState.setEnvironment(environment);
    	actorsContainer.setActor(0, coreRebecaActorState);
    	environment.setVariableValue(ActorScope.ACTORS_IN_ENVIRONMENT_VARIABLE_NAME,
    			actorsContainer);

    	
    	typeSystem = new CoreRebecaTypeSystem();
    	typeSystem.clear();
    	ReactiveClassDeclaration rcd = new ReactiveClassDeclaration();
    	rcd.setName("A");
    	typeSystem.addReactiveClassType(rcd);
    	CloningRepository.resetRepository();
    }

    @Test
    public void GIVEN_ActorStateIsEmpty_WHEN_DeclarationInstructionIsExecuted_THEN_ANewVariableIsAddedToTheState() {
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	DeclarationInstructionBean dib = new DeclarationInstructionBean("var1",
    			CoreRebecaTypeSystem.INT_TYPE);
		variableDeclarationSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, dib);
    	
		Variable v = new Variable("var1");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v, 10, null, null);
		assignmentSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, aib);
		
		Assertions.assertEquals(10, coreRebecaActorState.getVariableValue(v));
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
		assignmentSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, aib);
		
		Assertions.assertEquals(coreRebecaActorState.getVariableValue(v1), -1);
    }
    
    @Test
    public void GIVEN_ActorStateHasOneVariable_WHEN_ReturnInstructionIsExecuted_THEN_TheCorrectValueHasToBeStoredInTargetVariable() {

    	coreRebecaActorState.addVariableToScope("var1", 1);
		Variable v1 = new Variable("var1");
    	coreRebecaActorState.newCallPushToScope(v1);
    	coreRebecaActorState.pushToScope();
    	coreRebecaActorState.addVariableToScope("var2", 12);
		Variable v2 = new Variable("var2");
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	ReturnInstructionBean rib = new ReturnInstructionBean(v2);
    	
		returnSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, rib);
		
		Assertions.assertEquals(coreRebecaActorState.getVariableValue(v1), 12);
    }
    
    @Test
    public void rebecInstantiationTest() throws CodeCompilationException {
    	coreRebecaActorState.addVariableToScope("var1", 1);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
		Variable v1 = new Variable("var1");
		RebecInstantiationInstructionBean riib = new RebecInstantiationInstructionBean();
		riib.setType(typeSystem.getType("A"));
		riib.setResultTarget(v1);
		Transition<AbstractActorState> result = 
				rebecInstantiationSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, riib);
		Assertions.assertEquals(result.getDestinationsActions().get(0).getClass(), NewInstanceAction.class);

    }

    @Test
    public void GIVEN_ActorStateHasAVariable_WHEN_AssignmentInstructionIsExecutedAndAccessedBySelfKeyword_THEN_ValueHasToBeUpdated() {    	
    	
    	coreRebecaActorState.addVariableToScope("var1", 1);
    	coreRebecaActorState.addVariableToScope("self", coreRebecaActorState);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
//    	coreRebecaActorState.setEnvironment(environment);
    	
    	Variable base = new Variable("self");
    	Variable v1 = new Variable(base, "var1");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v1, 10, null, null);
		assignmentSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, aib);
		
		Assertions.assertEquals(coreRebecaActorState.getVariableValue(v1), 10);
    }

    @Test
    public void GIVEN_ActorStateHasAVariable_WHEN_ConditionalInstructionIsExecutedAndConditionIsTrue_THEN_NoJumpIsNeeded() {

    	coreRebecaActorState.addVariableToScope("var1", false);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	Variable v1 = new Variable("var1");
    	JumpIfNotInstructionBean jinib = new JumpIfNotInstructionBean(v1, "m1", 1);
		conditionalSOSRule.applyRule(coreRebecaActorState, coreRebecaActorState, jinib);
		
		Pair<String, Integer> pc = (Pair<String, Integer>) coreRebecaActorState.getPC();
		Assertions.assertEquals(pc.getFirst(), "m1");
		Assertions.assertEquals(pc.getSecond(), 1);
    }
    
    @Test
    public void GIVEN_ActorStateHasAVariable_WHEN_NondetInstructionIsExecuted_THEN_ThreeResultStates() {
    	coreRebecaActorState.addVariableToScope("var1", 5);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));
    	NonDetValue ndv = new NonDetValue();
    	ndv.addNonDetValue(3);
    	ndv.addNonDetValue(4);
    	Variable var1 = new Variable("var1");
		ndv.addNonDetValue(var1);
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(var1, ndv, null, null);
    	Pair<CoreRebecaActorState, InstructionBean> state = new Pair<CoreRebecaActorState, InstructionBean>(coreRebecaActorState, aib);
    	Transition<AbstractActorState> result = assignmentSOSRule.applyRule(
    			coreRebecaActorState, coreRebecaActorState, aib);
    	List<AbstractActorState> destinations = result.getDestinationsStates();
    	Iterator<AbstractActorState> iterator = destinations.iterator();
    	AbstractActorState first = iterator.next();
    	Assertions.assertEquals(3, first.getVariableValue(var1));
    	AbstractActorState second = iterator.next();
    	Assertions.assertEquals(4, second.getVariableValue(var1));
    	AbstractActorState third = iterator.next();
    	Assertions.assertEquals(5, third.getVariableValue(var1));
    	state.getFirst();
    }

    
//    @Configuration
//    @ComponentScan(basePackages = { 
//    		"org.rebecalang.transparentactormodelchecker.abstractrebeca", 
//    		"org.rebecalang.transparentactormodelchecker.corerebeca.sos.statementlevelrule", 
//    		"org.rebecalang.transparentactormodelchecker.timedrebeca.sos.statementlevelrule"
//    		})
//    public static class Config {
//    	
//    }
}
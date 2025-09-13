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
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.Variable;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckerConfig;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.Action;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.action.NewInstanceAction;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.AbstractActorState;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.Environment;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.AssignmentSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.ConditionalSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.statementlevelrules.VariableDeclarationSOSRule;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.DeterministicTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.transitionsystem.NondeterministicTransition;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.util.CloningRepository;
import org.rebecalang.transparentactormodelchecker.corerebeca.sos.statementlevelrule.CoreRebecaRebecInstantiationSOSRule;
import org.rebecalang.transparentactormodelchecker.corerebeca.transitionsystem.state.CoreRebecaActorState;
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
    protected AssignmentSOSRule assignmentSOSRule;

    @Autowired
    protected CoreRebecaRebecInstantiationSOSRule rebecaRebecInstantiationSOSRule;
    
    @Autowired
    protected VariableDeclarationSOSRule variableDeclarationSOSRule;
    
    @Autowired
    protected ConditionalSOSRule conditionalSOSRule;
    
    protected CoreRebecaTypeSystem typeSystem;
    
    CoreRebecaActorState coreRebecaActorState;
    
    @BeforeEach
    public void setup() throws CodeCompilationException {
    	coreRebecaActorState = new CoreRebecaActorState(0);
    	coreRebecaActorState.setEnvironment(new Environment());
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

    	DeclarationInstructionBean dib = new DeclarationInstructionBean("var1");
    	Pair<CoreRebecaActorState, InstructionBean> state = new Pair<CoreRebecaActorState, InstructionBean>(coreRebecaActorState, dib);
		variableDeclarationSOSRule.applyRule(state, state);
    	
		Variable v = new Variable("var1");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v, 10, null, null);
		state.setSecond(aib);
		assignmentSOSRule.applyRule(state, state);
		
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
		assignmentSOSRule.applyRule(state, state);
		
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
		DeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> result = rebecaRebecInstantiationSOSRule.applyRule(state, state);
		Assertions.assertEquals(result.getAction().getClass(), NewInstanceAction.class);

    }

    @Test
    public void GIVEN_ActorStateHasAVariable_WHEN_AssignmentInstructionIsExecutedAndAccessedBySelfKeyword_THEN_ValueHasToBeUpdated() {

    	coreRebecaActorState.addVariableToScope("var1", 1);
    	coreRebecaActorState.addVariableToScope("self", coreRebecaActorState);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	
    	Variable base = new Variable("self");
    	Variable v1 = new Variable(base, "var1");
    	AssignmentInstructionBean aib = new AssignmentInstructionBean(v1, 10, null, null);
    	Pair<CoreRebecaActorState, InstructionBean> state = new Pair<CoreRebecaActorState, InstructionBean>(coreRebecaActorState, aib);
		state.setSecond(aib);
		assignmentSOSRule.applyRule(state, state);
		
		Assertions.assertEquals(state.getFirst().getVariableValue("var1"), 10);
    }

    @Test
    public void GIVEN_ActorStateHasAVariable_WHEN_ConditionalInstructionIsExecutedAndConditionIsTrue_THEN_NoJumpIsNeeded() {

    	coreRebecaActorState.addVariableToScope("var1", false);
    	coreRebecaActorState.addVariableToScope(CoreRebecaActorState.PC, new Pair<String, Integer>("-", 0));

    	Variable v1 = new Variable("var1");
    	JumpIfNotInstructionBean jinib = new JumpIfNotInstructionBean(v1, "m1", 1);
    	Pair<CoreRebecaActorState, InstructionBean> state = new Pair<CoreRebecaActorState, InstructionBean>(coreRebecaActorState, jinib);
		state.setSecond(jinib);
		conditionalSOSRule.applyRule(state, state);
		
		@SuppressWarnings("unchecked")
		Pair<String, Integer> pc = (Pair<String, Integer>) state.getFirst().getVariableValue(CoreRebecaActorState.PC);
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
    	NondeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>> result = 
    			(NondeterministicTransition<Pair<? extends AbstractActorState, InstructionBean>>) 
    			assignmentSOSRule.applyRule(state, state);
    	List<Pair<? extends Action,Pair<? extends AbstractActorState,InstructionBean>>> destinations = result.getDestinations();
    	Iterator<Pair<? extends Action, Pair<? extends AbstractActorState, InstructionBean>>> iterator = destinations.iterator();
    	AbstractActorState first = iterator.next().getSecond().getFirst();
    	Assertions.assertEquals(3, first.getVariableValue("var1"));
    	AbstractActorState second = iterator.next().getSecond().getFirst();
    	Assertions.assertEquals(4, second.getVariableValue("var1"));
    	AbstractActorState third = iterator.next().getSecond().getFirst();
    	Assertions.assertEquals(5, third.getVariableValue("var1"));
    	state.getFirst();
    }

}
package org.rebecalang.transparentactormodelchecker.timedrebeca;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.FileUtils;
import org.rebecalang.compiler.utils.Pair;

import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;

import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckerConfig;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.Feature;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class TimedRebecaModelsTest {

	public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/transparentactormodelchecker/"; 

	@Autowired
	public ExceptionContainer exceptionContainer;
	
	@Autowired
    protected RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;
    
    @Autowired
    TransparentActorTimedRebecaFTTSModelChecker fttsModelChecker;
    
    @Test
    public void GIVEN_RebecaModel_WHEN_No_Error() throws ModelCheckingException, IOException {
		String rebecaModel = 
				"""
					reactiveclass Customer(3) {
					    knownrebecs { Agent a; }
					    statevars {
							byte id;
					    }
					    Customer(byte myId) {
					        id = myId;
							self.try();
					    }
					    msgsrv try() {
					    	a.requestTicket();
					    }
					    msgsrv ticketIssued() {
					        self.try() after(30);
					    }
					}
					reactiveclass Agent(10) {
					    knownrebecs { TicketService ts; }
					    msgsrv requestTicket() {
					        ts.requestTicket((Customer)sender) deadline(24);
					    }
					    msgsrv ticketIssued(Customer customer) {
							customer.ticketIssued();
					    }
					}
					reactiveclass TicketService(10) {
					    knownrebecs { Agent a; }
					    statevars {
					        int issueDelay;
					    }
					    TicketService(int myIssueDelay) {
					        issueDelay = myIssueDelay;
					    }
					    msgsrv requestTicket(Customer customer) {
					        delay(issueDelay);
					        a.ticketIssued(customer);
					    }
					}
					main {
					    Agent a(ts):();
					    TicketService ts(a):(2);
					    Customer c1(a):(1);
//					    Customer c2(a):(2);
					}				
				""";
		File rebecaFile = FileUtils.createTempFile(rebecaModel);
		
		HashSet<CompilerExtension> extention = new HashSet<CompilerExtension>();
		extention.add(CompilerExtension.TIMED_REBECA);
		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, extention, CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			return;
		}
        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, extention, CoreVersion.CORE_2_3);
        
        fttsModelChecker.modelcheck(compiledRebecaFile, transformedRILModel, new HashSet<Feature>());
        
		printRILModel(transformedRILModel);
//		TransparentActorModelCheckingResult dfsResult = dfsModelChecker.modelcheck(compiledRebecaFile, transformedRILModel);
//
//		TransparentActorModelCheckingResult bfsResult = fineGrainedBFSModelChecker.modelcheck(compiledRebecaFile, transformedRILModel);
//
//		Assertions.assertEquals(dfsResult.getTransitionSystem().size(),
//				bfsResult.getTransitionSystem().size());
		
//		TransparentActorModelCheckingResult coarseDfsResult = coarseGrainedDFSModelChecker.modelcheck(compiledRebecaFile, transformedRILModel);
//		coreRebecaModelChecker.modelCheck(model, modelCheckerSetting);
//
//		if(!exceptionContainer.exceptionsIsEmpty())
//			System.out.println(exceptionContainer);
//
//		Assertions.assertTrue(exceptionContainer.exceptionsIsEmpty());
//
//		StateSpace<State<? extends BaseActorState<?>>> stateSpace = coreRebecaModelChecker.getStateSpace();
//		State<ActorState> initialState = (State<ActorState>) stateSpace.getInitialState();
//		StateSpaceUtil.printStateSpace(initialState,
//				new PrintStream(new FileOutputStream(new File(policy + filename))));
//
//		Assertions.assertEquals(statespaceSize, stateSpace.size());
	}

//	@ParameterizedTest
//	@MethodSource("philosophers")
//	public void GIVEN_DiningPhilosopherModel_WHEN_No_Error(String model, int statespaceSize) throws ModelCheckingException, IOException {
//		File rebecaFile = FileUtils.createTempFile(model);
//		
//		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
//				rebecaModelCompiler.compileRebecaFile(rebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
//		if(!exceptionContainer.exceptionsIsEmpty()) {
//			exceptionContainer.print(System.out);
//			return;
//		}
//        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
//        		compiledRebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
//        
//
//		printRILModel(transformedRILModel);
//		
//		TransparentActorModelCheckingResult coarseDfsResult = coarseGrainedDFSModelChecker.modelcheck(compiledRebecaFile, transformedRILModel);
//		Assertions.assertEquals(statespaceSize, coarseDfsResult.getTransitionSystem().size());
//	}
//	
	private void printRILModel(RILModel transformedRILModel) {
		for(String methodName : transformedRILModel.getMethodNames()) {
			System.out.println(methodName);
			int counter = 0;
			for(InstructionBean instruction : transformedRILModel.getInstructionList(methodName)) {
				System.out.println("" + counter++ +":" + instruction);
			}
			System.out.println("...............................................");
		}
	}
//	
//	protected static Stream<Arguments> philosophers() {
//	    return Stream.of(
//	    		Arguments.arguments(DiningPhilosophersSourceCodes.TWO_PHILOSOPHERS, 106),
//	    		Arguments.arguments(DiningPhilosophersSourceCodes.THREE_PHILOSOPHERS, 1472),
//	    		Arguments.arguments(DiningPhilosophersSourceCodes.FOUR_PHILOSOPHERS, 18054),
//	    		Arguments.arguments(DiningPhilosophersSourceCodes.FIVE_PHILOSOPHERS, 214108)
//	    );
//	}
}

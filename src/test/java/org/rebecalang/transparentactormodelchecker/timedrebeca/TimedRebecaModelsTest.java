package org.rebecalang.transparentactormodelchecker.timedrebeca;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.rebecalang.compiler.CompilerConfig;
import org.rebecalang.compiler.modelcompiler.RebecaModelCompiler;
import org.rebecalang.compiler.modelcompiler.SymbolTable;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.utils.CompilerExtension;
import org.rebecalang.compiler.utils.CoreVersion;
import org.rebecalang.compiler.utils.ExceptionContainer;
import org.rebecalang.compiler.utils.FileUtils;
import org.rebecalang.compiler.utils.Pair;
import org.rebecalang.modelchecker.ModelCheckerConfig;
import org.rebecalang.modelchecker.corerebeca.ModelCheckingException;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.modeltransformer.ril.corerebeca.rilinstruction.InstructionBean;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckerConfig;
import org.rebecalang.transparentactormodelchecker.TransparentActorModelCheckingResult;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.Feature;
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
    
	@ParameterizedTest
	@MethodSource("customers")
    public void ticketService(String model, int statespaceSize) throws ModelCheckingException, IOException {
		File rebecaFile = FileUtils.createTempFile(model);
		
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
        
        TransparentActorModelCheckingResult result = 
        		fttsModelChecker.modelcheck(compiledRebecaFile, transformedRILModel, new HashSet<Feature>());
        System.out.println(result.getTransitionSystem().size());
        
//		printRILModel(transformedRILModel);
	}
	
	protected static Stream<Arguments> customers() {
	    return Stream.of(
	    		Arguments.arguments(TicketServiceSourceCodes.ONE_CUSTOMER, 105)
	    		, Arguments.arguments(TicketServiceSourceCodes.TWO_CUSTOMERS, 105)
//	    		, Arguments.arguments(TicketServiceSourceCodes.THREE_CUSTOMERS, 105)
	    );
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

package org.rebecalang.transparentactormodelchecker;

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
import org.rebecalang.modelchecker.corerebeca.utils.Policy;
import org.rebecalang.modeltransformer.ModelTransformerConfig;
import org.rebecalang.modeltransformer.ril.RILModel;
import org.rebecalang.modeltransformer.ril.Rebeca2RILModelTransformer;
import org.rebecalang.transparentactormodelchecker.corerebeca.TransparentActorCoreRebecaDFSModelChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@ContextConfiguration(classes = {CompilerConfig.class, ModelCheckerConfig.class, ModelTransformerConfig.class, TransparentActorModelCheckerConfig.class}) 
@SpringJUnitConfig
@TestPropertySource(properties = {"log4j.configurationFile='log4j2.xml'"})
public class ModelsTest {

	public static final String MODEL_FILES_BASE = "src/test/resources/org/rebecalang/transparentactormodelchecker/"; 

	@Autowired
	public ExceptionContainer exceptionContainer;
	
	@Autowired
    protected RebecaModelCompiler rebecaModelCompiler;

    @Autowired
    protected Rebeca2RILModelTransformer rebeca2RILModelTransformer;
    
    @Autowired
    protected TransparentActorCoreRebecaDFSModelChecker dfsModelChecker;

	@ParameterizedTest
	@MethodSource("modelToStateSpace")
	public void GIVEN_RebecaModel_WHEN_No_Error(String filename, int statespaceSize, Policy policy) throws ModelCheckingException, IOException {
		String rebecaModel = 
				"""
					reactiveclass Ping(3) {
						knownrebecs {
							Pong po;
						}
						statevars{int t;}
						Ping() {
							self.ping();
						}
						Ping(int i) {
							self.ping();
						}
					
						msgsrv ping() {
							self.t = t;
							po.pong();
						}
					}
					reactiveclass Pong(3) {
						knownrebecs {
							Ping pi;
						}
						Pong(int j) {
							pong();
						}
						msgsrv pong() {
							pi.ping();
						}
					}
					
					main {
						Ping pi(po):(4 + 2);
						Pong po(pi):(3);
					}				
				""";
		File rebecaFile = FileUtils.createTempFile(rebecaModel);
		
//		File model = new File(MODEL_FILES_BASE + filename);
		
		Pair<RebecaModel, SymbolTable> compiledRebecaFile = 
				rebecaModelCompiler.compileRebecaFile(rebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
		if(!exceptionContainer.exceptionsIsEmpty()) {
			exceptionContainer.print(System.out);
			return;
		}
        RILModel transformedRILModel = rebeca2RILModelTransformer.transformModel(
        		compiledRebecaFile, new HashSet<CompilerExtension>(), CoreVersion.CORE_2_3);
        
        dfsModelChecker.modelcheck(compiledRebecaFile, transformedRILModel);
        
//		for(String methodName : transformedRILModel.getMethodNames()) {
//			System.out.println(methodName);
//			int counter = 0;
//			for(InstructionBean instruction : transformedRILModel.getInstructionList(methodName)) {
//				System.out.println("" + counter++ +":" + instruction);
//			}
//			System.out.println("...............................................");
//		}

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
	
	protected static Stream<Arguments> modelToStateSpace() {
	    return Stream.of(
//	    		Arguments.arguments("pingpong.rebeca", 3, Policy.COARSE_GRAINED_POLICY)
	    		Arguments.arguments("pingpong.rebeca", 12, Policy.FINE_GRAINED_POLICY)
	    );
	}
}

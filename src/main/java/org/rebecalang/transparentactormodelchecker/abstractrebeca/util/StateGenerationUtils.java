package org.rebecalang.transparentactormodelchecker.abstractrebeca.util;

import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.FieldDeclaration;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.Literal;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.OrdinaryVariableInitializer;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.RebecaModel;
import org.rebecalang.compiler.modelcompiler.corerebeca.objectmodel.VariableDeclarator;
import org.rebecalang.transparentactormodelchecker.abstractrebeca.sos.state.ActivationRecord;

public class StateGenerationUtils {
    public static ActivationRecord getEnvironment(RebecaModel rebecaModel) {
    	ActivationRecord environment = new ActivationRecord();
        for (FieldDeclaration fieldDeclaration : rebecaModel.getRebecaCode().getEnvironmentVariables()) {
            for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariableDeclarators()) {
                Literal literal = (Literal) ((OrdinaryVariableInitializer) variableDeclarator.getVariableInitializer()).getValue();
                environment.setVariableValue(variableDeclarator.getVariableName(), literal.getLiteralValue());
            }
        }
        return environment;
    }

}

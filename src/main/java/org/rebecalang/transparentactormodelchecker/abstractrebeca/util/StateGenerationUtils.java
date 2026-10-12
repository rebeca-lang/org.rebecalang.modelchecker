package org.rebecalang.transparentactormodelchecker.abstractrebeca.util;

import org.rebecalang.compiler.modelcompiler.corerebeca.CoreRebecaTypeSystem;
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
                Object value = literal.getLiteralValue();
                if(fieldDeclaration.getType() == CoreRebecaTypeSystem.BYTE_TYPE || 
                		fieldDeclaration.getType() == CoreRebecaTypeSystem.SHORT_TYPE || 
                		fieldDeclaration.getType() == CoreRebecaTypeSystem.INT_TYPE)
                    value = Integer.parseInt(literal.getLiteralValue());
                else if(fieldDeclaration.getType() == CoreRebecaTypeSystem.FLOAT_TYPE || 
                		fieldDeclaration.getType() == CoreRebecaTypeSystem.DOUBLE_TYPE)
                	value = Double.parseDouble(literal.getLiteralValue());
                else if(fieldDeclaration.getType() == CoreRebecaTypeSystem.BOOLEAN_TYPE)
                	value = Boolean.parseBoolean(literal.getLiteralValue());
                else if(fieldDeclaration.getType() == CoreRebecaTypeSystem.NULL_TYPE)
                	value = null;
                environment.setVariableValue(variableDeclarator.getVariableName(), value);
            }
        }
        return environment;
    }

}

package com.scriptbasic.executors.commands;

import com.scriptbasic.executors.leftvalues.BasicLeftValue;
import com.scriptbasic.interfaces.ExecutionException;
import com.scriptbasic.interfaces.Interpreter;
import com.scriptbasic.interfaces.LeftValue;

public class CommandLocal extends AbstractCommandLeftValueListed {

    @Override
    public void execute(final Interpreter interpreter)
            throws ExecutionException {
        for (final LeftValue variable : getLeftValueList()) {
            // TODO instance check and that the modifier list is null
            final String variableName = ((BasicLeftValue) variable).getIdentifier();
            interpreter.getVariables().registerLocalVariable(variableName);
        }
    }
}

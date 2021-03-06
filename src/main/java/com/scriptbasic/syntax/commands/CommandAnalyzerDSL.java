package com.scriptbasic.syntax.commands;

import com.scriptbasic.context.Context;
import com.scriptbasic.executors.GenericExpressionList;
import com.scriptbasic.executors.commands.CommandCall;
import com.scriptbasic.executors.rightvalues.FunctionCall;
import com.scriptbasic.interfaces.AnalysisException;
import com.scriptbasic.interfaces.BasicSyntaxException;
import com.scriptbasic.interfaces.Expression;
import com.scriptbasic.interfaces.LexicalElement;
import com.scriptbasic.spi.Command;

import java.util.LinkedList;
import java.util.List;

public class CommandAnalyzerDSL extends AbstractCommandAnalyzer {
    private final List<DslLine> dslLines = new LinkedList<>();

    protected CommandAnalyzerDSL(final Context ctx) {
        super(ctx);
    }

    // sentence "assert that $expression is the same as $expression" call assert

    @Override
    public Command analyze() throws AnalysisException {
        ctx.lexicalAnalyzer.resetLine();
        final var lexeme = ctx.lexicalAnalyzer.get();
        if (lexeme != null && lexeme.isSymbol("sentence")) {
            defineDSLRule();
            return null;
        } else {
            for (final DslLine line : dslLines) {
                ctx.lexicalAnalyzer.resetLine();
                final var command = analyzeWith(line);
                if (command != null) {
                    return command;
                }
            }
            throw new BasicSyntaxException("Can not analyze the line as DSL.");
        }
    }

    private Command analyzeWith(final DslLine line) {
        final var expressionList = new GenericExpressionList();
        for (final String match : line.syntaxElements) {

            if (match.equalsIgnoreCase("$expression")) {
                final Expression expression;
                try {
                    expression = analyzeExpression();
                } catch (final AnalysisException ignored) {
                    return null;
                }
                expressionList.add(expression);
            } else {
                final LexicalElement lexicalElement;
                try {
                    lexicalElement = ctx.lexicalAnalyzer.get();
                } catch (final AnalysisException ignored) {
                    return null;
                }
                if (!lexicalElement.getLexeme().equalsIgnoreCase(match)) {
                    return null;
                }
            }
        }
        final var functionCall = new FunctionCall();
        functionCall.setVariableName(line.methodName);
        functionCall.setExpressionList(expressionList);
        return new CommandCall(functionCall);
    }

    private void defineDSLRule() throws AnalysisException {
        final var actualSentence = ctx.lexicalAnalyzer.get();
        if (!actualSentence.isString()) {
            throw new BasicSyntaxException("There should be a string after the keyword 'sentence'");
        }
        final var sentence = actualSentence.stringValue();
        final var callsKW = ctx.lexicalAnalyzer.get();
        if (!callsKW.isSymbol("call")) {
            throw new BasicSyntaxException("missing keyword 'call' after string in command 'sentence'");
        }
        final var functionNameLexicalElement = ctx.lexicalAnalyzer.get();
        if (!functionNameLexicalElement.isIdentifier()) {
            throw new BasicSyntaxException("there should be a function name after the keyword 'call' defining a sentenceó");
        }
        consumeEndOfLine();
        final String[] syntaxElements = sentence.split("\\s+");
        if (syntaxElements.length == 0) {
            throw new BasicSyntaxException("sentence can not be empty");
        }
        final var startElement = syntaxElements[0];
        if (startElement.equals("'") || startElement.equalsIgnoreCase("rem")) {
            throw new BasicSyntaxException("sentence should not look like as a comment");
        }
        dslLines.add(new DslLine(functionNameLexicalElement.getLexeme(), syntaxElements));
    }

    private class DslLine {
        final String methodName;
        final String[] syntaxElements;

        private DslLine(final String methodName, final String[] syntaxElements) {
            this.methodName = methodName;
            this.syntaxElements = syntaxElements;
        }
    }

}

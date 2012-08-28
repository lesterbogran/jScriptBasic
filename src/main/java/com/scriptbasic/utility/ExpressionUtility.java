/**
 * 
 */
package com.scriptbasic.utility;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.scriptbasic.exceptions.GenericSyntaxException;
import com.scriptbasic.executors.commands.CommandSub;
import com.scriptbasic.executors.leftvalues.BasicLeftValue;
import com.scriptbasic.executors.operators.JavaObjectFieldAccessOperator;
import com.scriptbasic.executors.rightvalues.BasicStringValue;
import com.scriptbasic.executors.rightvalues.VariableAccess;
import com.scriptbasic.interfaces.AnalysisException;
import com.scriptbasic.interfaces.BasicRuntimeException;
import com.scriptbasic.interfaces.ExecutionException;
import com.scriptbasic.interfaces.Expression;
import com.scriptbasic.interfaces.ExpressionList;
import com.scriptbasic.interfaces.ExtendedInterpreter;
import com.scriptbasic.interfaces.LeftValue;
import com.scriptbasic.interfaces.LeftValueList;
import com.scriptbasic.interfaces.RightValue;

/**
 * @author Peter Verhas date June 28, 2012
 * 
 */
public final class ExpressionUtility {
	private ExpressionUtility() {
		UtilityUtility.throwExceptionToEnsureNobodyCallsIt();
	}

	private static boolean parameterLengthMatch(Class<?>[] parameterTypes,
			List<RightValue> args) {
		if (args == null) {
			return parameterTypes.length == 0;
		}
		return parameterTypes.length == args.size();
	}

	public static RightValue callBasicFunction(ExtendedInterpreter interpreter,
			RightValue[] argumentValues, CommandSub commandSub,
			String functionName) throws ExecutionException {
		RightValue result = null;
		interpreter.push();
		LeftValueList arguments = commandSub.getArguments();
		registerLocalVariablesWithValues(arguments, argumentValues, interpreter);
		interpreter.disableHook();
		interpreter.setReturnValue(null);
		interpreter.enableHook();
		interpreter.getHook().beforeSubroutineCall(functionName, arguments,
				argumentValues);
		interpreter.execute(commandSub.getNextCommand());
		result = interpreter.getReturnValue();
		interpreter.pop();
		return result;
	}

	/**
	 * @param arguments
	 * @param argumentValues
	 * @throws ExecutionException
	 */
	private static void registerLocalVariablesWithValues(
			LeftValueList arguments, RightValue[] argumentValues,
			ExtendedInterpreter interpreter) throws ExecutionException {
		if (arguments != null) {
			Iterator<LeftValue> argumentIterator = arguments.iterator();
			for (int i = 0; i < argumentValues.length; i++) {
				if (argumentIterator.hasNext()) {
					LeftValue argument = argumentIterator.next();
					if (argument instanceof BasicLeftValue) {
						String name = ((BasicLeftValue) argument)
								.getIdentifier();
						interpreter.getVariables().registerLocalVariable(name);
						interpreter.setVariable(name, argumentValues[i]);
					} else {
						throw new BasicRuntimeException(
								"subroutine formal argument list is too long");
					}
				} else {
					throw new BasicRuntimeException(
							"subroutine formal argument is erroneous");
				}
			}
		}
	}

	public static Object[] getObjectArray(List<RightValue> args, Method method,
			ExtendedInterpreter extendedInterpreter) throws ExecutionException {
		Class<?>[] parameterTypes = method.getParameterTypes();
		// if the declaring class of the method implements the interface
		// WHATEVER //TODO find a good name for the interface that is to be
		// implemented by the embedding Java program
		// and the first parameter of the method is
		// com.scriptbasic.interfaces.interpreter then auto magically
		// pass that parameter to the method
		if (!parameterLengthMatch(parameterTypes, args)) {
			throw new BasicRuntimeException(
					"Different number of parameters calling the Java method '"
							+ method.getName() + "'");
		}
		ArrayList<Object> result = null;
		if (args != null) {
			result = new ArrayList<Object>();
			int parameterIndex = 0;
			for (RightValue arg : args) {
				Object object = CastUtility.cast(
						RightValueUtility.getValueObject(arg),
						parameterTypes[parameterIndex]);
				result.add(object);
				parameterIndex++;
			}
		}
		return result == null ? null : result.toArray(new Object[0]);
	}

	public static List<RightValue> evaluateExpressionList(
			ExtendedInterpreter extendedInterpreter,
			ExpressionList expressionList) throws ExecutionException {
		List<RightValue> args = null;
		if (expressionList != null) {
			args = new LinkedList<RightValue>();
			for (Expression expression : expressionList) {
				args.add(expression.evaluate(extendedInterpreter));
			}
		}
		return args;
	}

	/**
	 * Convert an expression of the form 'a.b.c.d' into a String.
	 * 
	 * @param expression
	 * @return the string containing the dots and the identifiers
	 * @throws AnalysisException
	 *             when the expression does not match the format
	 * 
	 */
	public static String convertToString(Expression expression)
			throws AnalysisException {
		if (expression instanceof VariableAccess) {
			return ((VariableAccess) expression).getVariableName();
		}
		if (expression instanceof JavaObjectFieldAccessOperator) {
			JavaObjectFieldAccessOperator ofao = (JavaObjectFieldAccessOperator) expression;
			return convertToString(ofao.getLeftOperand()) + "."
					+ convertToString(ofao.getRightOperand());
		}
		if (expression instanceof BasicStringValue) {
			return ((BasicStringValue) expression).getValue();
		}
		throw new GenericSyntaxException(
				"class, package or symbol name are not vaid in command USE");
	}
}

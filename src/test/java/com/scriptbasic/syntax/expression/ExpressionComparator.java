package com.scriptbasic.syntax.expression;

import com.scriptbasic.executors.AbstractIdentifieredExpression;
import com.scriptbasic.executors.AbstractIdentifieredExpressionListedExpression;
import com.scriptbasic.executors.operators.AbstractBinaryOperator;
import com.scriptbasic.executors.operators.AbstractUnaryOperator;
import com.scriptbasic.executors.rightvalues.AbstractPrimitiveRightValue;
import com.scriptbasic.interfaces.Expression;
import com.scriptbasic.interfaces.ExpressionList;

import java.util.Iterator;

import static org.junit.Assert.*;

public class ExpressionComparator {

    private static void assertEqual(final AbstractBinaryOperator a,
                                    final AbstractBinaryOperator b) {
        assertEqual(a.getLeftOperand(), b.getLeftOperand());
        assertEqual(a.getRightOperand(), b.getRightOperand());
    }

    private static void assertEqualOperand(final AbstractUnaryOperator a,
                                           final AbstractUnaryOperator b) {
        assertEqual(a.getOperand(), b.getOperand());
    }

    public static void assertEqual(final ExpressionList a,
                                   final ExpressionList b) {
        if (a == null && b == null) {
            return;
        }
        if (a == null || b == null) {
            fail();
        }
        final Iterator<Expression> itea = a.iterator();
        final Iterator<Expression> iteb = b.iterator();

        while (itea.hasNext() && iteb.hasNext()) {
            assertEqual(itea.next(), iteb.next());
        }
        assertFalse(itea.hasNext() || iteb.hasNext());
    }

    private static void assertEqual(
            final AbstractIdentifieredExpressionListedExpression a,
            final AbstractIdentifieredExpressionListedExpression b) {
        if (a.getVariableName() == null && b.getVariableName() == null) {
            return;
        }
        assertEquals(a.getVariableName() + "!=" + b.getVariableName(), a
                .getVariableName(), b.getVariableName());
        assertEqual(a.getExpressionList(), b.getExpressionList());
    }

    private static void assertEqual(final AbstractPrimitiveRightValue<?> a,
                                    final AbstractPrimitiveRightValue<?> b) {
        assertEquals(a.getValue() + "!=" + b.getValue(), a.getValue(), b.getValue());
    }

    private static void assertEqual(final AbstractIdentifieredExpression a,
                                    final AbstractIdentifieredExpression b) {
        assertEquals(a.getVariableName(), b.getVariableName());
    }

    public static void assertEqual(final Expression a, final Expression b) {

        if (a == null && b == null) {
            return;
        }
        if (a == null || b == null) {
            fail();
        }

        assertEquals(a.getClass() + "!=" + b.getClass(), a.getClass(), b.getClass());

        if (a instanceof AbstractPrimitiveRightValue<?>) {
            assertEqual((AbstractPrimitiveRightValue<?>) a,
                    (AbstractPrimitiveRightValue<?>) b);
            return;
        }

        if (a instanceof AbstractBinaryOperator) {
            assertEqual((AbstractBinaryOperator) a, (AbstractBinaryOperator) b);
            return;
        }
        if (a instanceof AbstractUnaryOperator) {
            assertEqualOperand((AbstractUnaryOperator) a, (AbstractUnaryOperator) b);
            return;
        }
        if (a instanceof AbstractIdentifieredExpressionListedExpression) {
            assertEqual((AbstractIdentifieredExpressionListedExpression) a,
                    (AbstractIdentifieredExpressionListedExpression) b);
            return;
        }
        if (a instanceof AbstractIdentifieredExpression) {
            assertEqual((AbstractIdentifieredExpression) a,
                    (AbstractIdentifieredExpression) b);
            return;
        }
        fail();
    }
}

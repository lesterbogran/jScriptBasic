package com.scriptbasic.executors.rightvalues;

import com.scriptbasic.interfaces.BasicRuntimeException;
import com.scriptbasic.spi.RightValue;

public class BasicStringValue extends AbstractPrimitiveRightValue<String> {
    public BasicStringValue(final String s) {
        setValue(s);
    }

    @SuppressWarnings("unchecked")
    public static String asString(final RightValue rv)            throws BasicRuntimeException {
        try {
            final String resultString;
            if (rv == null
                    || ((AbstractPrimitiveRightValue<Object>) rv).getValue() == null) {
                resultString = "undef";
            } else if (rv.isString() || rv.isNumeric() || rv.isBoolean()
                    || rv.isJavaObject()) {
                resultString = ((AbstractPrimitiveRightValue<Object>) rv)
                        .getValue().toString();
            } else {
                throw new BasicRuntimeException("Can not convert value to string");
            }
            return resultString;
        } catch (final ClassCastException cce) {
            throw new BasicRuntimeException("Error converting to string", cce);
        }
    }

    @Override
    public String toString(){
        try {
            return asString(this);
        } catch (final BasicRuntimeException e) {
            return super.toString();
        }
    }
}

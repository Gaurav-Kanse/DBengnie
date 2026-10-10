package com.gaurav.enigine.functional;

@FunctionalInterface
public interface CheckedFunction<I, O, E extends Exception> {
    O apply(I in) throws E;
}

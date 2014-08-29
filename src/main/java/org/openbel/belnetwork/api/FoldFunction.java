package org.openbel.belnetwork.api;

public interface FoldFunction<T, U> {

    public U initial();
    
    public U fold(T item, U accumulated);
}

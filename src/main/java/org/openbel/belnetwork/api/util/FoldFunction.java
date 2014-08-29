package org.openbel.belnetwork.api.util;

public interface FoldFunction<T, U> {

    public U initial();
    
    public U fold(T item, U accumulated);
}

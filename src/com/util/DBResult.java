package com.util;

import java.util.Iterator;

public class DBResult<T> implements Iterator<T> {


    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        return null;
    }
}

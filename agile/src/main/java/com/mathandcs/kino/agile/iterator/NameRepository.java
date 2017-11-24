package com.mathandcs.kino.agile.iterator;

/**
 * Created by dashwang on 11/24/17.
 */
public class NameRepository implements Container {

    public String[] names = {"Tom", "Jack", "Kyle"};

    @Override
    public Iterator getIterator() {
        return new NameIterator();
    }

    public class NameIterator implements Iterator {

        int index;

        @Override
        public boolean hasNext() {
            if(index < names.length) {
                return true;
            }
            return false;
        }

        @Override
        public Object next() {
            if(hasNext()) {
                return names[index++];
            }
            return null;
        }
    }
}

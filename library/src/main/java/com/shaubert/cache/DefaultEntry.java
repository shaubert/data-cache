package com.shaubert.cache;

import java.util.*;

public class DefaultEntry<DATA> implements Entry<DATA> {
    private final String key;
    private final Class<DATA> dataClass;

    private Set<Object> marks = new HashSet<>();
    private Collection<Object> readOnlyMarks = Collections.unmodifiableCollection(marks);

    private DATA value;
    private boolean mergeable;

    public DefaultEntry(String key, Class<DATA> dataClass) {
        this.key = key;
        this.dataClass = dataClass;
        this.mergeable = MergeableData.class.isAssignableFrom(dataClass);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public DATA getValue() {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(DATA value) {
        if (value != null && this.value != null && mergeable) {
            this.value = ((MergeableData<DATA>) this.value).merge(value);
        } else {
            this.value = value;
        }
    }

    @Override
    public boolean hasValue() {
        return value != null;
    }

    @Override
    public boolean addMark(Object mark) {
        if (mark == null) return false;

        return marks.add(mark);
    }

    @Override
    public boolean removeMark(Object mark) {
        if (mark == null) return false;

        return marks.remove(mark);
    }

    @Override
    public boolean hasMark(Object mark) {
        return marks.contains(mark);
    }

    @Override
    public boolean hasMarkOf(Class<?> markClass) {
        return getMarkOf(markClass) != null;
    }

    @Override
    public Collection<Object> getMarks() {
        return readOnlyMarks;
    }

    @Override
    public boolean hasMarks() {
        return !marks.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getMarkOf(Class<T> markClass) {
        if (markClass == null) return null;

        for (Object mark : marks) {
            if (mark != null
                    && markClass.isAssignableFrom(mark.getClass())) {
                return (T) mark;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getMarksOf(Class<T> markClass) {
        ArrayList<T> list = new ArrayList<>();
        if (markClass == null) return list;

        for (Object mark : marks) {
            if (mark != null
                    && markClass.isAssignableFrom(mark.getClass())) {
                list.add((T) mark);
            }
        }
        return list;
    }

    @Override
    public void clear() {
        value = null;
        marks.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public final Class<DATA> getDataClass() {
        return dataClass;
    }

}

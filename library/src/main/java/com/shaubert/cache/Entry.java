package com.shaubert.cache;

import java.util.Collection;

public interface Entry<DATA> {

    DirtyMark DIRTY_MARK = DirtyMark.MARK;

    FailureMark FAILURE_MARK = FailureMark.MARK;

    UpdatingMark UPDATING_MARK = UpdatingMark.MARK;

    /**
     * @return Entry unique key for mapping in {@link com.shaubert.cache.Cache Cache}.
     */
    String getKey();

    /**
     * @return {@link java.lang.Class Class} associated with this cache entry — value class.
     */
    Class<DATA> getDataClass();

    /**
     * @return cached value or null.
     */
    DATA getValue();

    /**
     * Put value in cache entry. If value implements {@link com.shaubert.cache.MergeableData MergeableData} than new value
     * will be merged with previous value (if exists).
     * @param value new value
     */
    void setValue(DATA value);

    /**
     * @return true if value not null, false otherwise
     */
    boolean hasValue();

    /**
     * Marks cache entry with provided mark. Look at {@link com.shaubert.cache.UpdatingMark UpdatingMark}
     * or {@link com.shaubert.cache.DirtyMark DirtyMark}
     * @param mark mark to put in marks set.
     * @return {@code true} if this set of marks was modified, {@code false} otherwise.
     */
    boolean addMark(Object mark);

    /**
     * Remove mark from set.
     * @param mark mark to be removed.
     * @return {@code true} if this set of marks was modified, {@code false} otherwise.
     */
    boolean removeMark(Object mark);

    /**
     * Checks that mark exists in set.
     * @param mark mark to be checked.
     * @return {@code true} if mark was found, {@code false} otherwise.
     */
    boolean hasMark(Object mark);

    /**
     * Checks that mark with that class exists in set.
     * @param markClass mark class to be checked.
     * @return {@code true} if mark with that class was found, {@code false} otherwise.
     */
    boolean hasMarkOf(Class<?> markClass);

    /**
     * @return all added marks.
     */
    Collection<Object> getMarks();

    /**
     * @return true if marks not empty; false otherwise.
     */
    boolean hasMarks();

    /**
     * @param markClass mark class to find.
     * @param <T>
     * @return first mark with that class or null in no mark was found.
     */
    <T> T getMarkOf(Class<T> markClass);

    /**
     * @param markClass mark class to find.
     * @param <T>
     * @return collection of marks with that class or empty collection in no marks were found.
     */
    <T> Collection<T> getMarksOf(Class<T> markClass);

    /**
     * Reset all to null or false.
     */
    void clear();
}

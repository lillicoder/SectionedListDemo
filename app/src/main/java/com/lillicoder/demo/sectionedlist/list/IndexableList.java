/**
 * Copyright 2014 Scott Weeden-Moody
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lillicoder.demo.sectionedlist.list;

import java.util.*;

/**
 * <p>
 *     {@link List} that supports the {@link Indexable} interface.
 * </p>
 *
 * <p>
 *     This list implementation supports any arbitrary index key and
 *     any arbitrary list item type. The index key type must implement
 *     {@link Comparable} to allow for indexing operations.
 * </p>
 * @param <K> Type of object this list is indexable by.
 * @param <E> Type of object this list contains.
 */
public class IndexableList<K extends Comparable<K>, E> implements Indexable<K>, List<E> {

    private List<E> mItems;

    private Class<K> mKeyClassType;
    private K mKey;
    private CharSequence mLabel;

    /**
     * Instantiates this list with the given key and label {@link CharSequence}.
     * @param key Key for this list.
     * @param label Label for this list.
     */
    public IndexableList(Class<K> keyClassType, K key, CharSequence label) {
        mKeyClassType = keyClassType;
        mKey = key;
        mLabel = label;

        mItems = new ArrayList<E>();
    }

    /**
     * Instantiates this list with the given key, label {@link CharSequence} and initial capacity.
     * @param key Key for this list.
     * @param label Label for this list.
     * @param capacity Initial capacity for this list.
     */
    public IndexableList(K key, CharSequence label, int capacity) {
        mKey = key;
        mLabel = label;

        mItems = new ArrayList<E>(capacity);
    }

    /**
     * Gets the {@link Class} type for this list's key.
     * @return Class type for this list's key.
     */
    public Class<K> getKeyClassType() {
        return mKeyClassType;
    }

    @Override
    public K getKey() {
        return mKey;
    }

    @Override
    public CharSequence getLabel() {
        return mLabel;
    }

    @Override
    public void add(int location, E object) {
        mItems.add(location, object);
    }

    @Override
    public boolean add(E object) {
        return mItems.add(object);
    }

    @Override
    public boolean addAll(int location, Collection<? extends E> collection) {
        return mItems.addAll(location, collection);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return mItems.addAll(collection);
    }

    @Override
    public void clear() {
        mItems.clear();
    }

    @Override
    public boolean contains(Object object) {
        return mItems.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return mItems.containsAll(collection);
    }

    @Override
    public E get(int location) {
        return mItems.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return mItems.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return mItems.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return mItems.lastIndexOf(object);
    }

    @Override
    public ListIterator<E> listIterator() {
        return mItems.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int location) {
        return mItems.listIterator(location);
    }

    @Override
    public E remove(int location) {
        return mItems.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return mItems.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return mItems.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return mItems.retainAll(collection);
    }

    @Override
    public E set(int location, E object) {
        return mItems.set(location, object);
    }

    @Override
    public int size() {
        return mItems.size();
    }

    @Override
    public List<E> subList(int start, int end) {
        return mItems.subList(start, end);
    }

    @Override
    public Object[] toArray() {
        return mItems.toArray();
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return mItems.toArray(array);
    }

    @Override
    public int compareTo(Indexable<K> another) {
        K anotherKey = another.getKey();
        return mKey.compareTo(anotherKey);
    }

}

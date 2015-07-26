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

/**
 * Interface describing an object that can be indexed by an arbitrary key.
 * @param <T> Type of index key implementers may be indexed by.
 */
public interface Indexable<T extends Comparable<T>> extends Comparable<Indexable<T>> {

    /**
     * Gets the key {@link T} for this object.
     * @return Index key.
     */
    public T getKey();

    /**
     * <p>Gets the label {@link CharSequence} for this object's key.</p>
     *
     * <p>
     *     The label is a display value that represents a human
     *     readable representation of an index. This label does
     *     not have to correspond to any particular value of
     *     the index key. This value should not be used when indexing
     *     or sorting but rather for logging and display purposes.
     * </p>
     * @return Index label.
     */
    public CharSequence getLabel();

}

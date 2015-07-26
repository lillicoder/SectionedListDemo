package com.lillicoder.demo.sectionedlist.widget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import com.lillicoder.demo.sectionedlist.list.IndexableList;
import junit.framework.Assert;

import java.util.*;

/**
 * <p>
 *     {@link BaseAdapter} implementation that can support an
 *     arbitrary collection of {@link IndexableList}.
 * </p>
 *
 * <p>
 *     This adapter is designed to support any collection of indexable lists where each list
 *     has the same indexable key type and list item type. This adapter assumes that there
 *     is a desire to generate two types of views for an associated {@link android.widget.ListView}:
 *     a header view representing the indexable information and child views under a
 *     header representing the list items.
 * </p>
 * @param <K> Type of object each indexable list is indexable by.
 * @param <E> Type of object each indexable list contains.
 */
public abstract class IndexableListAdapter<K extends Comparable<K>, E>
    extends BaseAdapter
    implements SectionIndexer {

    private static final String PRECONDITION_NULL_MAP =
        "Cannot instantiate a section list adapter with a null map of sections.";
    private static final String PRECONDITION_NULL_SECTIONS =
        "Cannot instantiate a section list adapter with a null collection of sections.";

    private List<Wrapper<K, E>> mItems;

    private Indexer<K, E> mIndexer;

    public IndexableListAdapter(List<IndexableList<K, E>> sections) {
        Assert.assertTrue(PRECONDITION_NULL_SECTIONS, sections != null);
        mItems = flatten(sections);
        mIndexer = new Indexer<K, E>(sections);
    }

    /**
     * Instantiates this adapter with the given map of {@link Collection}. Each key represents
     * a section and each collection mapped to a key represents the items for that section.
     * @param sections Map of sections for this adapter.
     */
    public IndexableListAdapter(Map<K, ? extends Collection<E>> sections) {
        Assert.assertTrue(PRECONDITION_NULL_MAP, sections != null);

        List<IndexableList<K, E>> sectionsAsList = convertToList(sections);
        mItems = flatten(sectionsAsList);
        mIndexer = new Indexer<K, E>(sections);
    }

    /**
     * Gets an view for the given child.
     * @param child Child section item.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return View for the given child.
     */
    protected abstract View getChildView(E child, View convertView, ViewGroup parent);

    /**
     * Gets a header view for the given {@link IndexableList}.
     * @param section Sortable list containing all elements for a section.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent that this view will eventually be attached to.
     * @return Header view for the given section.
     */
    protected abstract View getHeaderView(IndexableList<K, E> section, View convertView, ViewGroup parent);

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        // We return actual underlying values as AdapterView can query for items
        // and users of this adapter shouldn't have to handle wrappers
        Wrapper<K, E> wrapper = mItems.get(position);
        return wrapper.getValue();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Delegate to getHeaderView or getChildView
        Wrapper<K, E> wrapper = mItems.get(position);
        if (wrapper.isSection()) {
            return getHeaderView((IndexableList<K, E>) wrapper.getValue(), convertView, parent);
        } else {
            return getChildView((E) wrapper.getValue(), convertView, parent);
        }
    }

    @Override
    public boolean isEnabled(int position) {
        // Sections items are never enabled
        Wrapper<K, E> wrapper = mItems.get(position);
        return !wrapper.isSection();
    }

    @Override
    public Object[] getSections() {
        return mIndexer.getSections();
    }

    @Override
    public int getPositionForSection(int section) {
        return mIndexer.getPositionForSection(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return mIndexer.getSectionForPosition(position);
    }

    /**
     * Converts the given {@link Map} of sections into a collection of {@link IndexableList}.
     * @param sections Sections map to convert.
     * @return Collection of indexable lists converted from the given map.
     */
    private List<IndexableList<K, E>> convertToList(Map <K, ? extends Collection<E>> sections) {
        Set<K> keys = sections.keySet();
        List<IndexableList<K, E>> sectionsList = new ArrayList<IndexableList<K, E>>(keys.size());

        for (K sectionKey : keys) {
            Collection<E> sectionItems = sections.get(sectionKey);

            IndexableList<K, E> section =
                new IndexableList<K, E>(sectionKey, sectionKey.toString(), sectionItems.size());
            section.addAll(sectionItems);

            sectionsList.add(section);
        }

        return sectionsList;
    }

    /**
     * Flattens the given collection of {@link IndexableList} into a collection of {@link Wrapper}.
     * @param sections Sections to flatten.
     * @return Collection of wrappers for each section and section item in the given collection.
     */
    private List<Wrapper<K, E>> flatten(List<IndexableList<K, E>> sections) {
        List<Wrapper<K, E>> items = new ArrayList<Wrapper<K, E>>();

        for (IndexableList<K, E> section : sections) {
            Wrapper<K, E> header = new Wrapper<K, E>(section);
            items.add(header);

            for (E element : section) {
                Wrapper<K, E> item = new Wrapper<K, E>(element);
                items.add(item);
            }
        }

        return items;
    }

    /**
     * General purpose {@link SectionIndexer} for use with a {@link IndexableListAdapter}.
     * @param <E>
     */
    private static class Indexer<K extends Comparable<K>, E> implements SectionIndexer {

        private static final String PRECONDITION_NULL_SECTIONS =
            "Cannot make a section list adapter indexer with null sections.";

        private Object[] mSections;
        private int[] mSectionCounts;
        private int[] mPositionMarkers;

        /**
         * Instantiates this indexer from the given collection of {@link IndexableList}.
         * @param sections Collection of indexable lists.
         */
        public Indexer(List<IndexableList<K, E>> sections) {
            Assert.assertTrue(PRECONDITION_NULL_SECTIONS, sections != null);

            // We can simply walk over the collection of lists
            // and generate the section and position arrays in one pass
            mSections = new Object[sections.size()];
            mSectionCounts = new int[mSections.length];
            mPositionMarkers = new int[mSections.length];

            int position = 0;
            for (int index = 0; index < sections.size(); index++) {
                // Get section value for position
                IndexableList<K, E> section = sections.get(index);
                mSections[index] = section.getKey();

                // Get section size for position
                int sectionCount = section.size();
                mSectionCounts[index] = sectionCount;

                // Mark position for section and increase the counter
                mPositionMarkers[index] = position;
                position += sectionCount;
            }

        }

        /**
         * Instantiates this indexer from the given {@link Map} of sections.
         * @param sections Map of sections.
         */
        public Indexer(Map<K, ? extends Collection<E>> sections) {
            Assert.assertTrue(PRECONDITION_NULL_SECTIONS, sections != null);

            // Sections are the keys in the map
            Set<K> keys = sections.keySet();
            mSections = new Object[keys.size()];
            keys.toArray(mSections);

            // Section counts are the number of items per key and position marker
            // is starting index of the section in the overall collection of items
            mSectionCounts = new int[mSections.length];
            mPositionMarkers = new int[mSections.length];

            int position = 0;
            for (int index = 0; index < mSections.length; index++) {
                // Get summaries for section
                Object key = mSections[index];
                Collection<E> summaries = sections.get(key);

                // Section count is size of the summaries list + 1 for the section header
                int sectionCount = summaries.size() + 1;
                mSectionCounts[index] = sectionCount;

                // Set the position marker and increase the value by the number
                // of items in this section for the next iteration
                mPositionMarkers[index] = position;
                position += sectionCount;
            }
        }

        @Override
        public Object[] getSections() {
            return mSections;
        }

        @Override
        public int getPositionForSection(int section) {
            // If section is out of range, return -1 to indicate no position
            if (section < 0 || section >= mSections.length) {
                return -1;
            }

            return mPositionMarkers[section];
        }

        @Override
        public int getSectionForPosition(int position) {
            int lastSectionIndex = mSections.length - 1;
            int lastSectionStart = mPositionMarkers[lastSectionIndex];
            int lastSectionEnd = mSectionCounts[lastSectionIndex] - 1;
            int lastItemPosition = lastSectionStart + lastSectionEnd;

            // If position is out of range, return -1 to indicate no section
            if (position < 0 || position > lastItemPosition) {
                return -1;
            }

            int index = Arrays.binarySearch(mPositionMarkers, position);

            /*
             * Comment below taken from the framework source:
             *
             * Consider this example: section positions are 0, 3, 5; the supplied
             * position is 4. The section corresponding to position 4 starts at
             * position 3, so the expected return value is 1. Binary search will not
             * find 4 in the array and thus will return -insertPosition-1, i.e. -3.
             * To get from that number to the expected value of 1 we need to negate
             * and subtract 2.
             */
            return index >= 0 ? index : -index - 2;
        }

    }

    /**
     * Wrapper that is used to create a consistent model for {@link IndexableListAdapter}.
     * @param <K> Type of sort key for wrapped sections.
     * @param <E> Type of item for wrapped section items.
     */
    private static class Wrapper<K extends Comparable<K>, E> {

        private static final String EXCEPTION_NO_WRAPPED_VALUE =
            "No value wrapped, this item wrapper cannot be used.";

        private IndexableList<K, E> mSection;
        private E mElement;

        /**
         * Instantiates this wrapper with the given section.
         * @param section Section to wrap.
         */
        public Wrapper(IndexableList<K, E> section) {
            mSection = section;
        }

        /**
         * Instantiates this wrapper with the given element.
         * @param element Element to wrap.
         */
        public Wrapper(E element) {
            mElement = element;
        }

        /**
         * Gets the wrapped value for this wrapper.
         * @return Wrapper value.
         */
        public Object getValue() {
            if (mElement != null) {
                return mElement;
            } else if (mSection != null) {
                return mSection;
            } else {
                throw new IllegalStateException(EXCEPTION_NO_WRAPPED_VALUE);
            }
        }

        /**
         * Determines if this wrapper represents a section.
         * @return {@code true} if this wrapper represents a section,
         *         {@code false} otherwise.
         */
        public boolean isSection() {
            return mSection != null;
        }

    }

}

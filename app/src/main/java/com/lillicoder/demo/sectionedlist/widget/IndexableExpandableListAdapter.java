package com.lillicoder.demo.sectionedlist.widget;

import android.util.Log;
import android.widget.BaseExpandableListAdapter;
import android.widget.SectionIndexer;
import com.lillicoder.demo.sectionedlist.list.IndexableList;
import junit.framework.Assert;

import java.util.*;

/**
 * <p>
 *     {@link BaseExpandableListAdapter} implementation that can support an
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
public abstract class IndexableExpandableListAdapter<K extends Comparable<K>, E>
    extends BaseExpandableListAdapter
    implements SectionIndexer {

    private static final String PRECONDITION_NULL_LIST =
        "Cannot instantiate this adapter with a null list of sections.";

    private static final String PRECONDITION_NULL_MAP =
        "Cannot instantiate this adapter with a null map of sections.";

    private List<IndexableList<K, E>> mSections;

    private Indexer<K, E> mIndexer;

    /**
     * Instantiates this adapter with the given {@link List} of {@link IndexableList}.
     * @param sections List of indexable lists for this adapter,
     *                 where each indexable list represents a section.
     */
    public IndexableExpandableListAdapter(List<IndexableList<K, E>> sections) {
        Assert.assertTrue(PRECONDITION_NULL_LIST, sections != null);

        mSections = sections;
        mIndexer = new Indexer<K, E>(sections);
    }

    /**
     * Instantiates this adapter with the given {@link Map} of sections. The given map will be converted
     * to a two-dimensional list that is suitable for use with this adapter.
     * @param sections Map of sections for this adapter, with each key representing a section and that key's associated
     *                 collection representing the items for that section.
     */
    public IndexableExpandableListAdapter(Map<K, Collection<E>> sections) {
        Assert.assertTrue(PRECONDITION_NULL_MAP, sections != null);

        mSections = convertToList(sections);
        mIndexer = new Indexer<K, E>(mSections);
    }

    @Override
    public E getChild(int groupPosition, int childPosition) {
        IndexableList<K, E> section = getGroup(groupPosition);
        return section.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        IndexableList<K, E> section = mSections.get(groupPosition);
        return section.size();
    }

    @Override
    public IndexableList<K, E> getGroup(int groupPosition) {
        return mSections.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mSections.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getPositionForSection(int section) {
        return mIndexer.getPositionForSection(section);
    }

    @Override
    public Object[] getSections() {
        return mIndexer.getSections();
    }

    @Override
    public int getSectionForPosition(int position) {
        return mIndexer.getSectionForPosition(position);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return mSections.isEmpty();
    }

    /**
     * Converts the given sections {@link Map} to a {@link List} of {@link IndexableList}. Each indexable list
     * created will use the key's toString() method as that list's label.
     * @param sections Sections to convert.
     * @return List of indexable lists converted from the given map.
     */
    private List<IndexableList<K, E>> convertToList(Map<K, Collection<E>> sections) {
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
     * {@link SectionIndexer} implementation that handles creating the proper section tracking information.
     * @param <K> Type of object sections are indexable by.
     * @param <E> Type of object each section contains.
     */
    private static class Indexer<K extends Comparable<K>, E> implements SectionIndexer {

        private static final String TAG = "IndexableExpandableListAdapter.Indexer";

        private static final String PRECONDITION_NULL_ITEMS =
            "Cannot instantiate indexer with null items.";

        private static final String WARNING_POSITION_INDEX_OUT_OF_BOUNDS =
            "Cannot get section index for position %d, positions range is [0,%d].";

        private static final String WARNING_SECTION_INDEX_OUT_OF_BOUNDS =
            "Cannot get starting position for section %d, sections range is [0,%d].";

        private static final int INVALID_POSITION = -1;
        private static final int INVALID_SECTION = -1;

        private CharSequence[] mSections;
        private int[] mSectionSizes;
        private int[] mSectionStartPositions;

        public Indexer(List<IndexableList<K, E>> sections) {
            Assert.assertTrue(PRECONDITION_NULL_ITEMS, sections != null);

            // One section per list
            mSections = new CharSequence[sections.size()];

            // One element for each section's size and starting position
            mSectionSizes = new int[mSections.length];
            mSectionStartPositions = new int[mSections.length];

            // Populate section sizes and starting positions
            int lastPosition = 0;
            for (int sectionPosition = 0; sectionPosition < sections.size(); sectionPosition++) {
                // Section starting position is the last position we reached
                mSectionStartPositions[sectionPosition] = lastPosition;

                IndexableList<K, E> section = sections.get(sectionPosition);
                int sectionSize = section.size();

                // Increase position by section size for next section
                lastPosition += sectionSize;

                // Store section size
                mSectionSizes[sectionPosition] = sectionSize;
            }
        }

        @Override
        public CharSequence[] getSections() {
            return mSections;
        }

        @Override
        public int getPositionForSection(int section) {
            if (section < 0 || section >= mSections.length) {
                Log.w(TAG, String.format(WARNING_SECTION_INDEX_OUT_OF_BOUNDS,
                                         section,
                                         mSections.length));
                return INVALID_POSITION;
            }

            return mSectionStartPositions[section];
        }

        @Override
        public int getSectionForPosition(int position) {
            // Last item position is the last section's start position plus the position of the last item that section
            int lastSectionIndex = mSections.length - 1;
            int lastSectionStartPosition = mSectionStartPositions[lastSectionIndex];
            int lastSectionLastItemPosition = mSectionSizes[lastSectionIndex] - 1;
            int lastItemPosition = lastSectionStartPosition + lastSectionLastItemPosition;
            if (position < 0 || position > lastItemPosition) {
                Log.w(TAG, String.format(WARNING_POSITION_INDEX_OUT_OF_BOUNDS,
                                         position,
                                         lastItemPosition));
                return INVALID_SECTION;
            }

            int closestSection = Arrays.binarySearch(mSectionStartPositions, position);

            /*
             * Consider this example: section positions are 0, 3, 5; the supplied
             * position is 4. The section corresponding to position 4 starts at
             * position 3, so the expected return value is 1. Binary search will not
             * find 4 in the array and thus will return -insertPosition-1, i.e. -3.
             * To get from that number to the expected value of 1 we need to negate
             * and subtract 2.
             */
            return closestSection >= 0 ? closestSection : closestSection - 2;
        }

    }

}

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

package com.lillicoder.demo.sectionedlist;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.lillicoder.demo.sectionedlist.list.IndexableList;
import com.lillicoder.demo.sectionedlist.widget.IndexableListAdapter;

import java.util.*;

public class DemoActivity extends ActionBarActivity {

    private ListView mList;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        mList = (ListView) findViewById(R.id.DemoActivity_list);
        mProgressBar = (ProgressBar) findViewById(R.id.DemoActivity_progressBar);

        showProgressBar();

        Map<String, List<String>> animals = getAnimalsByName();
        SimpleIndexableListAdapter adapter = new SimpleIndexableListAdapter(animals);
        mList.setAdapter(adapter);

        showList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.demo, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.DemoActivityMenu_toggleFastScroller) {
            // Toggle fast scroller and update menu item title
            mList.setFastScrollEnabled(!mList.isFastScrollEnabled());
            if (mList.isFastScrollEnabled()) {
                item.setTitle(R.string.menu_disable_fast_scroller);
            } else {
                item.setTitle(R.string.menu_enable_fast_scroller);
            }

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Gets a map of animals names where each key in the map is the starting letter and each
     * entry is the collection of animal names beginning with that starting letter.
     * @return Map of animal names.
     */
    private Map<String, List<String>> getAnimalsByName() {
        // TreeMap is used to get a proper sort order by default
        Map<String, List<String>> animalsByName = new TreeMap<String, List<String>>();

        Resources resources = getResources();
        String[] animals = resources.getStringArray(R.array.animals);
        for (String animal : animals) {
            String firstLetter = animal.substring(0, 1);

            List<String> namesForLetter = animalsByName.get(firstLetter);
            if (namesForLetter == null) {
                namesForLetter = new ArrayList<String>();
                namesForLetter.add(animal);

                animalsByName.put(firstLetter, namesForLetter);
            } else {
                namesForLetter.add(animal);
            }
        }

        return animalsByName;
    }

    /**
     * Shows the {@link ListView} for this activity.
     */
    private void showList() {
        mList.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Shows the {@link ProgressBar} for this activity.
     */
    private void showProgressBar() {
        mList.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Simple {@link IndexableListAdapter} that shows sections of strings.
     */
    private static class SimpleIndexableListAdapter extends IndexableListAdapter<String, String> {

        private static final Object TAG_CHILD = "tag_childView";
        private static final Object TAG_HEADER = "tag_headerView";

        public SimpleIndexableListAdapter(Map<String, ? extends Collection<String>> sections) {
            super(sections);
        }

        @Override
        protected View getChildView(String child, View convertView, ViewGroup parent) {
            if (convertView == null || !TAG_CHILD.equals(convertView.getTag())) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.list_item_child, parent, false);
                convertView.setTag(TAG_CHILD);
            }

            TextView childView = (TextView) convertView;
            childView.setText(child);

            return childView;
        }

        @Override
        protected View getHeaderView(IndexableList<String, String> section, View convertView, ViewGroup parent) {
            if (convertView == null || !TAG_HEADER.equals(convertView.getTag())) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                convertView = inflater.inflate(R.layout.list_item_header, parent, false);
                convertView.setTag(TAG_HEADER);
            }

            TextView headerView = (TextView) convertView;
            headerView.setText(section.getLabel());

            return headerView;
        }

    }

}

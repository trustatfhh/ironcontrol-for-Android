/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of ironcontrol for android, version 1.0.2, implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2013 - 2015 Trust@HsH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.hshannover.f4.trust.ironcontrol.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import de.hshannover.f4.trust.ironcontrol.R;

public class TabFragment extends Fragment implements OnTabChangeListener {

	public static final String TAB_SIMPLE = "Simple";
	public static final String TAB_ADVANCED = "Advanced";

	private View mRoot;
	private TabHost mTabHost;
	private int mCurrentTab;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.fragment_tab, null);
		mTabHost = (TabHost) mRoot.findViewById(android.R.id.tabhost);
		setupTabs();
		return mRoot;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(mCurrentTab);
		onTabChanged(TAB_SIMPLE);
	}

	private void setupTabs() {
		mTabHost.setup(); // important!

		// Adding all TabSpec to TabHost
		mTabHost.addTab(newTab(TAB_SIMPLE,R.id.tab1));
		mTabHost.addTab(newTab(TAB_ADVANCED,R.id.tab2));
	}

	private TabSpec newTab(String tabName, int tabId){
		TabSpec newSpecTab = mTabHost.newTabSpec(tabName);
		newSpecTab.setIndicator(tabName);
		newSpecTab.setContent(tabId);
		return newSpecTab;
	}

	@Override
	public void onTabChanged(String tabId) {
		FragmentManager fm = getFragmentManager();

		if (TAB_SIMPLE.equals(tabId)) {
			if (fm.findFragmentByTag(tabId) == null) {
				fm.beginTransaction().replace(R.id.tab1, new SimpleRequestFragment(), tabId).commit();
			}
			mCurrentTab = 0;
			return;
		}
		if (TAB_ADVANCED.equals(tabId)) {
			if (fm.findFragmentByTag(tabId) == null) {
				fm.beginTransaction().replace(R.id.tab2, new AdvancedRequestFragment(), tabId).commit();
			}
			mCurrentTab = 1;
			return;
		}
	}
}

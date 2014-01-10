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
 * This file is part of ironcontrol for android, version 1.0.1, implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2013 Trust@HsH
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
package de.hshannover.f4.trust.ironcontrol.view.irondetect;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import de.hshannover.f4.trust.ironcontrol.logger.Level;
import de.hshannover.f4.trust.ironcontrol.logger.Logger;
import de.hshannover.f4.trust.ironcontrol.logger.LoggerFactory;

public class PageAdapter extends FragmentPagerAdapter {

	private static final Logger logger = LoggerFactory.getLogger(PageAdapter.class);

	private final String[] titles = { "Rules", "Signatures", "Anomalies", "Conditions"};

	private final int NUM_PAGES = 4;

	private static PageListFragment rules, signatures, anomalies, conditions;

	private static IrondetectRemoteReceiver receiver = null;

	public PageAdapter(Context context, FragmentManager fm) {
		super(fm);
		logger.log(Level.DEBUG, "New...");

		if(receiver == null){
			receiver = new IrondetectRemoteReceiver(context, this);
			receiver.start();

			rules = new PageListFragment();
			signatures = new PageListFragment();
			anomalies = new PageListFragment();
			conditions = new PageListFragment();
		}

		logger.log(Level.DEBUG, "...New");
	}

	@Override
	public Fragment getItem(int pos) {
		Bundle bundle = new Bundle();
		bundle.putInt("pos", pos);
		switch(pos){
		case 0:
			return rules;
		case 1:
			return signatures;
		case 2:
			return anomalies;
		case 3:
			return conditions;
		}
		return null;
	}

	@Override
	public int getCount() {
		return NUM_PAGES;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return titles[position];
	}

	public void newGuiData(GuiData data){
		switch(data.getType()){
		case RULES: rules.addGuiData(data);
		break;
		case SIGNATURES: signatures.addGuiData(data);
		break;
		case ANOMALY: anomalies.addGuiData(data);
		break;
		case CONDITIONS: conditions.addGuiData(data);
		break;
		}
	}

}

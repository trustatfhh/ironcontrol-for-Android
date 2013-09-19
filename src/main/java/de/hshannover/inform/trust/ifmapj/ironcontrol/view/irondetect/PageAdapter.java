package de.hshannover.inform.trust.ifmapj.ironcontrol.view.irondetect;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Level;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.Logger;
import de.hshannover.inform.trust.ifmapj.ironcontrol.logic.logger.LoggerFactory;

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

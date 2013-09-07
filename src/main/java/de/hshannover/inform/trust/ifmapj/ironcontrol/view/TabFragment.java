package de.hshannover.inform.trust.ifmapj.ironcontrol.view;

import de.hshannover.inform.trust.ifmapj.ironcontrol.R;
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
			if (fm.findFragmentByTag(tabId) == null)
				fm.beginTransaction().replace(R.id.tab1, new SimpleRequestFragment(), tabId).commit();
			mCurrentTab = 0;
			return;
		}
		if (TAB_ADVANCED.equals(tabId)) {
			if (fm.findFragmentByTag(tabId) == null)
				fm.beginTransaction().replace(R.id.tab2, new AdvancedRequestFragment(), tabId).commit();
			mCurrentTab = 1;
			return;
		}
	}
}

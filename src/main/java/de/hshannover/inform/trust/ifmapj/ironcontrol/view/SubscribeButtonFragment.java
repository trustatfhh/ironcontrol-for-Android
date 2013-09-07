package de.hshannover.inform.trust.ifmapj.ironcontrol.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;

public class SubscribeButtonFragment extends Fragment {

	private View mRoot;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRoot = inflater.inflate(R.layout.fragment_button_subscribe, null);
		return mRoot;
	}

}

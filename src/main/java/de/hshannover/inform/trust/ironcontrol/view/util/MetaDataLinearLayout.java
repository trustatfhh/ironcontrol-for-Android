package de.hshannover.inform.trust.ironcontrol.view.util;

import android.content.Context;
import android.widget.LinearLayout;

public class MetaDataLinearLayout extends LinearLayout {

	public MetaDataLinearLayout(Context context) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	public MetaDataLinearLayout(Context context, int orientation) {
		super(context);
		setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		setOrientation(orientation);
	}

}

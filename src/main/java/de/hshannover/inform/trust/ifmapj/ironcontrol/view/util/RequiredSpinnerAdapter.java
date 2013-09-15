package de.hshannover.inform.trust.ifmapj.ironcontrol.view.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;

/**
 * Class for RequiredSpinnerAdapter. Change the Text Color for a required element.
 * 
 * @author Marcel Reichenbach
 * @version 1.0
 */

public class RequiredSpinnerAdapter extends PromptSpinnerAdapter {

	private Context context;
	private boolean required;

	public RequiredSpinnerAdapter(Context context, CharSequence prompt,	int textArrayResId, boolean required) {
		super(context, prompt, textArrayResId);

		this.context = context;
		this.required = required;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);

		if (v instanceof TextView && required) {

			((TextView)v).setTextColor(context.getResources().getColor(R.color.SteelBlue));

		}

		return v;
	}
}


package de.hshannover.inform.trust.ifmapj.ironcontrol.view.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.hshannover.inform.trust.ifmapj.ironcontrol.R;

/**
 * Class for connection management
 * @author Marcel Reichenbach
 * @version %I%, %G%
 * @since 0.1
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


package de.hshannover.inform.trust.ironcontrol.view.util;

import android.content.Context;
import android.text.InputType;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import de.hshannover.inform.trust.ironcontrol.R;

public class MetaDataEditText extends EditText {

	public MetaDataEditText(Context context) {
		super(context);
	}

	public MetaDataEditText(Context context, String hint, int type, int w, int h) {
		super(context);
		setHint(hint);
		setTag(hint);
		setInputType(type);
		setLayoutParams(new LinearLayout.LayoutParams(w, h, 1.0f));
	}

	public MetaDataEditText(Context context, String hint, int type) {
		this(context, hint, type, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public MetaDataEditText(Context context, String hint) {
		this(context, hint, InputType.TYPE_CLASS_TEXT);
	}

	public MetaDataEditText(Context context, String hint, boolean required) {
		this(context, hint);

		if(required){
			setHintTextColor(context.getResources().getColor(R.color.SteelBlue));
		}
	}
}

package de.hshannover.inform.trust.ironcontrol.view.dialogs;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.net.Uri;
import de.hshannover.inform.trust.ironcontrol.R;
import de.hshannover.inform.trust.ironcontrol.database.entities.Requests;
import de.hshannover.inform.trust.ironcontrol.database.entities.Responses;
import de.hshannover.inform.trust.ironcontrol.logger.Level;
import de.hshannover.inform.trust.ironcontrol.logger.Logger;
import de.hshannover.inform.trust.ironcontrol.logger.LoggerFactory;

public abstract class MultichoiceListDialog extends Builder{

	private static final Logger logger = LoggerFactory.getLogger(MultichoiceListDialog.class);

	protected Context context;

	private boolean[] selectedItems;

	private String[] rowLabels, rowIds;

	protected int resIdButton;

	public MultichoiceListDialog(Context context, Uri uri, int resIdTitle, int resIdButton) {
		super(context);
		logger.log(Level.DEBUG, "NEW...");
		this.context = context;
		this.resIdButton = resIdButton;

		getUriData(uri);

		this.selectedItems = new boolean[rowIds.length];

		setTitle(resIdTitle);
		setMultiChoiceItems(rowLabels);
		setPositiveButton();
		setNegativeButton();
		setNeutralButton();

		logger.log(Level.DEBUG, "...NEW");
	}

	private void getUriData(Uri uri){
		Cursor cData= context.getContentResolver().query(uri, null, null, null, null);

		rowLabels = new String[cData.getCount()];
		rowIds = new String[cData.getCount()];

		int i = 0;
		while(cData.moveToNext()){
			int column_NAME = cData.getColumnIndex(Requests.COLUMN_NAME);
			int column_ID = cData.getColumnIndex(Requests.COLUMN_ID);

			if(column_NAME == -1 || column_ID == -1){
				// for Responses
				int column_DATE = cData.getColumnIndex(Responses.COLUMN_DATE);
				int column_TIME = cData.getColumnIndex(Responses.COLUMN_TIME);
				column_ID = cData.getColumnIndex(Responses.COLUMN_ID);

				rowIds[i] = cData.getString(column_ID);
				rowLabels[i] = cData.getString(column_DATE) + " " + cData.getString(column_TIME);

			}else {
				// for Requests
				rowIds[i] = cData.getString(column_ID);
				rowLabels[i] = cData.getString(column_NAME);
			}

			i++;
		}

		cData.close();
	}

	private void setMultiChoiceItems(String[] rowLabels){
		setMultiChoiceItems(rowLabels, selectedItems, new OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {}});
	}

	private void setPositiveButton() {
		setPositiveButton(getPositiveButtonLabel(), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int clicked) {
				callBack(clicked);
			}
		});
	}

	protected String getPositiveButtonLabel() {
		return context.getResources().getString(R.string.ok);
	}

	private void setNegativeButton() {
		setNegativeButton(getNegativeButtonLabel(), null);
	}

	protected String getNegativeButtonLabel() {
		return context.getResources().getString(R.string.string_abort);
	}

	protected void setNeutralButton() {
		setNeutralButton(getNeutralButtonLabel(), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int clicked) {
				callBack(clicked);
			}
		});
	}

	protected String getNeutralButtonLabel() {
		return context.getResources().getString(R.string.ok);
	}

	/**
	 * The callBack() comes from the multichoice button listeners neutral- and positive-button.
	 * @param clicked is the button id DialogInterface.BUTTON_POSITIVE or DialogInterface.BUTTON_NEUTRAL
	 */
	protected void callBack(int clicked){
		if (context instanceof MultichoiceListEvent) {
			MultichoiceListEvent event = (MultichoiceListEvent) context;

			event.onClickeMultichoiceDialogButton(getSelectedRowIds(), resIdButton, clicked);
		}else {
			logger.log(Level.WARN, "context is not instance of MultichoiceListEvent");
		}
	}

	protected List<String> getSelectedRowIds() {
		List<String> selectedIds = new ArrayList<String>();

		for (int i = 0; i < selectedItems.length; i++) {
			if (selectedItems[i]) {
				selectedIds.add(rowIds[i]);
			}
		}
		return selectedIds;
	}

	public boolean isEmpty(){
		if(rowLabels.length == 0){
			return true;
		}

		return false;
	}
}


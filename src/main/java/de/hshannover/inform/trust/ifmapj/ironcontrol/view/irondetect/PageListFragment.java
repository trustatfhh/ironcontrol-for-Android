package de.hshannover.inform.trust.ifmapj.ironcontrol.view.irondetect;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class PageListFragment extends ListFragment {

	private ArrayList<GuiData> dataList;

	private PageListArrayAdapter mAdapter;

	public PageListFragment(){
		this.dataList = new ArrayList<GuiData>();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.mAdapter = new PageListArrayAdapter(getActivity(), dataList);

		setListAdapter(mAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// index for a inverted list
		int index = l.getAdapter().getCount() -1 -position;
		new IrondetectItemPopUp(getActivity(), mAdapter.getItem(index)).show();
	}

	public void addGuiData(final GuiData data){

		if(mAdapter != null && getActivity() != null){
			// if initialized and activity is active
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					dataList.add(data);
					mAdapter.notifyDataSetChanged();

				}
			});
		} else {

			dataList.add(data);

		}
	}
}
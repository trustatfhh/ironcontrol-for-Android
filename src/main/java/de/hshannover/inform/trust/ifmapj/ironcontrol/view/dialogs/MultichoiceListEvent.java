package de.hshannover.inform.trust.ifmapj.ironcontrol.view.dialogs;

import java.util.List;

public interface MultichoiceListEvent {

	public void onClickeMultichoiceDialogButton(List<String> selectedRowIds, int resIdButton, int clicked);

}

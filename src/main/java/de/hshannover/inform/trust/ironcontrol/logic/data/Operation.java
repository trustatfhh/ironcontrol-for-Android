package de.hshannover.inform.trust.ironcontrol.logic.data;

import de.hshannover.inform.trust.ironcontrol.R;

/**
 * Enum operation types
 * 
 * @author Anton Saenko
 * @author Arne Loth
 * @author Daniel Wolf
 * @version 1.0
 * @since 0.1
 */
public enum Operation {
	NOTIFY,
	UPDATE,
	DELETE;


	public static Operation valueOf(int buttonResource){
		switch(buttonResource){

		case R.id.bPublishUpdate: return UPDATE;

		case R.id.bSubscribeUpdate: return UPDATE;

		case R.id.bPublishNotify: return NOTIFY;

		case R.id.bPublishDelete: return DELETE;

		case R.id.bSubscribeDelete: return DELETE;

		default: return null;
		}
	}
}
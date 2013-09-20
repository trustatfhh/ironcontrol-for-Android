package de.hshannover.inform.trust.ifmapj.ironcontrol.logic.data;

import de.fhhannover.inform.trust.ifmapj.identifier.Identifier;
import de.fhhannover.inform.trust.ifmapj.identifier.Identifiers;
import de.fhhannover.inform.trust.ifmapj.identifier.IdentityType;

/**
 * Class for connection management
 * @author Anton Saenko
 * @author Arne Loth
 * @author Daniel Wolf
 * @since 0.1
 */
public class BuildIdetifiers {
	
	/**
	 * Returns an identifier.
	 * 
	 * @return  Identifier
	 * @since 0.1
	 */
	public static Identifier build(String idType, String name){
		if (idType.equals("Access Request")){
			return Identifiers.createAr(name);
		}else if (idType.equals("IP-Address")){
			return Identifiers.createIp4(name);
		}else if (idType.equals("MAC-Address")){
			return Identifiers.createMac(name);
		}else if (idType.equals("Device")){
			return Identifiers.createDev(name);
		}else if (idType.equals("Identity")){
			return Identifiers.createIdentity(IdentityType.userName, name);
//		}else if (){
//			//TODO Identifier build -- IF-Map 2.0 compatibility
		}else{
			//TODO Return a real exception
			return null;
		}
	}
}



/*
 * #%L
 * =====================================================
 *   _____                _     ____  _   _       _   _
 *  |_   _|_ __ _   _ ___| |_  / __ \| | | | ___ | | | |
 *    | | | '__| | | / __| __|/ / _` | |_| |/ __|| |_| |
 *    | | | |  | |_| \__ \ |_| | (_| |  _  |\__ \|  _  |
 *    |_| |_|   \__,_|___/\__|\ \__,_|_| |_||___/|_| |_|
 *                             \____/
 * 
 * =====================================================
 * 
 * Hochschule Hannover
 * (University of Applied Sciences and Arts, Hannover)
 * Faculty IV, Dept. of Computer Science
 * Ricklinger Stadtweg 118, 30459 Hannover, Germany
 * 
 * Email: trust@f4-i.fh-hannover.de
 * Website: http://trust.f4.hs-hannover.de/
 * 
 * This file is part of ironcontrol for android, version 1.0.1, implemented by the Trust@HsH research group at the Hochschule Hannover.
 * %%
 * Copyright (C) 2013 Trust@HsH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package de.hshannover.f4.trust.ironcontrol.logic.data;

import de.hshannover.f4.trust.ifmapj.identifier.Identifier;
import de.hshannover.f4.trust.ifmapj.identifier.Identifiers;
import de.hshannover.f4.trust.ifmapj.identifier.IdentityType;

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



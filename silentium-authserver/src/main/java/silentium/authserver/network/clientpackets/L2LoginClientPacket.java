/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.authserver.network.clientpackets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import silentium.authserver.L2LoginClient;
import silentium.commons.network.mmocore.ReceivablePacket;

/**
 * @author KenM
 */
public abstract class L2LoginClientPacket extends ReceivablePacket<L2LoginClient> {
	private static Logger _log = LoggerFactory.getLogger(L2LoginClientPacket.class.getName());

	@Override
	protected final boolean read() {
		try {
			return readImpl();
		} catch (Exception e) {
			_log.error("ERROR READING: " + getClass().getSimpleName());
			e.printStackTrace();
			return false;
		}
	}

	protected abstract boolean readImpl();
}

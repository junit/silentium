/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.handlers.user;

import silentium.commons.database.DatabaseFactory;
import silentium.gameserver.handler.IUserCommandHandler;
import silentium.gameserver.model.L2Clan;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.serverpackets.SystemMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Support for /clanwarlist command
 *
 * @author Tempy
 */
public class ClanWarsList implements IUserCommandHandler {
	private static final int[] COMMAND_IDS = { 88, 89, 90 };

	@Override
	public boolean useUserCommand(final int id, final L2PcInstance activeChar) {
		if (id != COMMAND_IDS[0] && id != COMMAND_IDS[1] && id != COMMAND_IDS[2])
			return false;

		final L2Clan clan = activeChar.getClan();
		if (clan == null) {
			activeChar.sendMessage("You are not in a clan.");
			return false;
		}

		SystemMessage sm;
		try (Connection con = DatabaseFactory.getConnection()) {
			final PreparedStatement statement;

			// Attack List
			if (id == 88) {
				activeChar.sendPacket(SystemMessageId.CLANS_YOU_DECLARED_WAR_ON);
				statement = con.prepareStatement("select clan_name,clan_id,ally_id,ally_name from clan_data,clan_wars where clan1=? and clan_id=clan2 and clan2 not in (select clan1 from clan_wars where clan2=?)");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, clan.getClanId());
			}
			// Under Attack List
			else if (id == 89) {
				activeChar.sendPacket(SystemMessageId.CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU);
				statement = con.prepareStatement("select clan_name,clan_id,ally_id,ally_name from clan_data,clan_wars where clan2=? and clan_id=clan1 and clan1 not in (select clan2 from clan_wars where clan1=?)");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, clan.getClanId());
			}
			// ID = 90, War List
			else {
				activeChar.sendPacket(SystemMessageId.WAR_LIST);
				statement = con.prepareStatement("select clan_name,clan_id,ally_id,ally_name from clan_data,clan_wars where clan1=? and clan_id=clan2 and clan2 in (select clan1 from clan_wars where clan2=?)");
				statement.setInt(1, clan.getClanId());
				statement.setInt(2, clan.getClanId());
			}

			final ResultSet rset = statement.executeQuery();

			while (rset.next()) {
				final String clanName = rset.getString("clan_name");
				final int ally_id = rset.getInt("ally_id");

				if (ally_id > 0) {
					// Target With Ally
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_S2_ALLIANCE);
					sm.addString(clanName);
					sm.addString(rset.getString("ally_name"));
				} else {
					// Target Without Ally
					sm = SystemMessage.getSystemMessage(SystemMessageId.S1_NO_ALLI_EXISTS);
					sm.addString(clanName);
				}

				activeChar.sendPacket(sm);
			}

			activeChar.sendPacket(SystemMessageId.FRIEND_LIST_FOOTER);

			rset.close();
			statement.close();
		} catch (Exception e) {
		}
		return true;
	}

	@Override
	public int[] getUserCommandList() {
		return COMMAND_IDS;
	}
}
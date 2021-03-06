/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.handlers.admin;

import silentium.gameserver.handler.IAdminCommandHandler;
import silentium.gameserver.model.L2Object;
import silentium.gameserver.model.L2World;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.network.clientpackets.Say2;

import java.util.Collection;
import java.util.StringTokenizer;

public class AdminKick implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = { "admin_character_disconnect", "admin_kick", "admin_kick_non_gm" };

	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar) {
		if ("admin_character_disconnect".equals(command) || "admin_kick".equals(command))
			disconnectCharacter(activeChar);

		if (command.startsWith("admin_kick")) {
			final StringTokenizer st = new StringTokenizer(command);
			if (st.countTokens() > 1) {
				st.nextToken();
				final String player = st.nextToken();
				final L2PcInstance plyr = L2World.getInstance().getPlayer(player);
				if (plyr != null) {
					plyr.logout();
					activeChar.sendChatMessage(0, Say2.ALL, "SYS", plyr.getName() + " have been kicked from server.");
				}
			}
		}

		if (command.startsWith("admin_kick_non_gm")) {
			int counter = 0;
			final Collection<L2PcInstance> pls = L2World.getInstance().getAllPlayers().values();

			for (final L2PcInstance player : pls) {
				if (!player.isGM()) {
					counter++;
					player.logout();
				}
			}
			activeChar.sendChatMessage(0, Say2.ALL, "SYS", "A total of " + counter + " players have been kicked.");
		}
		return true;
	}

	private static void disconnectCharacter(final L2PcInstance activeChar) {
		final L2Object target = activeChar.getTarget();
		L2PcInstance player = null;

		if (target instanceof L2PcInstance)
			player = (L2PcInstance) target;
		else
			return;

		if (player == activeChar)
			activeChar.sendChatMessage(0, Say2.ALL, "SYS", "You cannot disconnect your own character.");
		else {
			activeChar.sendChatMessage(0, Say2.ALL, "SYS", player.getName() + " have been kicked from server.");
			player.logout();
		}
	}

	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}
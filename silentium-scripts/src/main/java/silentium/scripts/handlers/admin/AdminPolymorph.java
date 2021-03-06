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
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.clientpackets.Say2;

import java.util.StringTokenizer;

/**
 * This class handles polymorph commands.
 */
public class AdminPolymorph implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = { "admin_polymorph", "admin_unpolymorph", "admin_polymorph_menu", "admin_unpolymorph_menu" };

	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar) {
		if (activeChar.isMounted())
			return false;

		if (command.startsWith("admin_polymorph")) {
			final StringTokenizer st = new StringTokenizer(command);
			final L2Object target = activeChar.getTarget();
			try {
				st.nextToken();
				final String p1 = st.nextToken();
				if (st.hasMoreTokens()) {
					final String p2 = st.nextToken();
					doPolymorph(activeChar, target, p2, p1);
				} else
					doPolymorph(activeChar, target, p1, "npc");
			} catch (Exception e) {
				activeChar.sendChatMessage(0, Say2.ALL, "SYS", "Usage: //polymorph [type] <id>");
			}

			if (command.contains("menu"))
				showMainPage(activeChar);
		} else if (command.startsWith("admin_unpolymorph")) {
			doUnpoly(activeChar, activeChar.getTarget());

			if (command.contains("menu"))
				showMainPage(activeChar);
		}

		return true;
	}

	private static void doPolymorph(final L2PcInstance activeChar, L2Object target, final String id, final String type) {
		// Target the GM if no target is found.
		if (target == null)
			target = activeChar;

		if (!target.getPoly().setPolyInfo(type, id)) {
			activeChar.sendPacket(SystemMessageId.APPLICANT_INFORMATION_INCORRECT);
			return;
		}

		activeChar.sendChatMessage(0, Say2.ALL, "SYS", "You polymorphed " + target.getName() + " into a " + type + " with id: " + id + '.');
	}

	private static void doUnpoly(final L2PcInstance activeChar, L2Object target) {
		// Target the GM if no target is found.
		if (target == null)
			target = activeChar;

		if (!target.getPoly().isMorphed()) {
			activeChar.sendPacket(SystemMessageId.INCORRECT_TARGET);
			return;
		}

		target.getPoly().setPolyInfo(null, "1");

		activeChar.sendChatMessage(0, Say2.ALL, "SYS", "You successfully unpolymorphed " + target.getName() + '.');
	}

	private static void showMainPage(final L2PcInstance activeChar) {
		AdminHelpPage.showHelpPage(activeChar, "effects_menu.htm");
	}

	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}
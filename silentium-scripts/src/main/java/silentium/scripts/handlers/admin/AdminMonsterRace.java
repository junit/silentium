/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.handlers.admin;

import silentium.gameserver.ThreadPoolManager;
import silentium.gameserver.handler.IAdminCommandHandler;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.entity.MonsterRace;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.serverpackets.DeleteObject;
import silentium.gameserver.network.serverpackets.MonRaceInfo;
import silentium.gameserver.network.serverpackets.PlaySound;
import silentium.gameserver.network.serverpackets.SystemMessage;

public class AdminMonsterRace implements IAdminCommandHandler {
	private static final String[] ADMIN_COMMANDS = { "admin_mons" };

	protected static int state = -1;

	@Override
	public boolean useAdminCommand(final String command, final L2PcInstance activeChar) {
		if ("admin_mons".equalsIgnoreCase(command)) {
			/*
			 * -1 0 to initialize the race 0 15322 to start race 13765 -1 in middle of race -1 0 to end the race 8003 to 8027
			 */
			final int[][] codes = { { -1, 0 }, { 0, 15322 }, { 13765, -1 }, { -1, 0 } };
			final MonsterRace race = MonsterRace.getInstance();

			if (state == -1) {
				state++;
				race.newRace();
				race.newSpeeds();
				activeChar.broadcastPacket(new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()));
			} else if (state == 0) {
				state++;
				activeChar.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.MONSRACE_RACE_START));
				activeChar.broadcastPacket(new PlaySound(1, "S_Race", 0, 0, 0, 0, 0));
				activeChar.broadcastPacket(new PlaySound(0, "ItemSound2.race_start", 1, 121209259, 12125, 182487, -3559));
				activeChar.broadcastPacket(new MonRaceInfo(codes[state][0], codes[state][1], race.getMonsters(), race.getSpeeds()));

				ThreadPoolManager.getInstance().scheduleGeneral(new RunRace(codes, activeChar), 5000);
			}
		}
		return true;
	}

	class RunRace implements Runnable {

		private final int[][] codes;
		private final L2PcInstance activeChar;

		RunRace(final int[][] pCodes, final L2PcInstance pActiveChar) {
			codes = pCodes;
			activeChar = pActiveChar;
		}

		@Override
		public void run() {
			activeChar.broadcastPacket(new MonRaceInfo(codes[2][0], codes[2][1], MonsterRace.getInstance().getMonsters(), MonsterRace.getInstance().getSpeeds()));
			ThreadPoolManager.getInstance().scheduleGeneral(new RunEnd(activeChar), 30000);
		}
	}

	class RunEnd implements Runnable {
		private final L2PcInstance activeChar;

		RunEnd(final L2PcInstance pActiveChar) {
			activeChar = pActiveChar;
		}

		@Override
		public void run() {
			for (int i = 0; i < 8; i++)
				activeChar.broadcastPacket(new DeleteObject(MonsterRace.getInstance().getMonsters()[i]));

			state = -1;
		}
	}

	@Override
	public String[] getAdminCommandList() {
		return ADMIN_COMMANDS;
	}
}
/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have
 * received a copy of the GNU General Public License along with this program. If not, see <http://l2j.ru/>.
 */
package silentium.scripts.vmasters;

import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.quest.Quest;

public final class Bronk extends Quest
{
	// Quest NPCs
	private static final int BRONK = 30525;

	public Bronk(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(BRONK);
		addTalkId(BRONK);
	}

	public static void main(String[] args)
	{
		new Bronk(-1, "Bronk", "vmasters");
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.contains("-01") || event.contains("-02") || event.contains("-03") || event.contains("-04"))
			return event;
		else
			return null;
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		switch (talker.getClassId())
		{
			case dwarvenFighter:
				return "30525-01.htm";
			case artisan:
				return "30525-05.htm";
			case warsmith:
				return "30525-06.htm";
			case scavenger:
			case bountyHunter:
				return "30525-07.htm";
			default:
				return "30525-07.htm";
		}
	}
}
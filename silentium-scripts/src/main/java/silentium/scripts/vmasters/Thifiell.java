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

public final class Thifiell extends Quest
{
	// Quest NPCs
	private static final int THIFIELL = 30358;

	public Thifiell(int questId, String name, String descr)
	{
		super(questId, name, descr);
		addStartNpc(THIFIELL);
		addTalkId(THIFIELL);
	}

	public static void main(String[] args)
	{
		new Thifiell(-1, "Thifiell", "vmasters");
	}

	@Override
	public String onAdvEvent(String event, L2Npc npc, L2PcInstance player)
	{
		if (event.contains("-01") || event.contains("-02") || event.contains("-03") || event.contains("-04") || event.contains("-05") || event.contains("-06") || event.contains("-07") || event.contains("-08") || event.contains("-09") || event.contains("-10"))
			return event;
		else
			return null;
	}

	@Override
	public String onTalk(L2Npc npc, L2PcInstance talker)
	{
		switch (talker.getClassId())
		{
			case darkFighter:
				return "30358-01.htm";
			case darkMage:
				return "30358-02.htm";
			case darkWizard:
			case shillienOracle:
			case palusKnight:
			case assassin:
				return "30358-12.htm";
			default:
				return "30358-11.htm";
		}
	}
}
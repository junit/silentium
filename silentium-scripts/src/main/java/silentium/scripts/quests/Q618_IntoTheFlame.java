/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.quests;

import silentium.commons.utils.Rnd;
import silentium.gameserver.configs.MainConfig;
import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.quest.Quest;
import silentium.gameserver.model.quest.QuestState;
import silentium.gameserver.scripting.ScriptFile;

public class Q618_IntoTheFlame extends Quest implements ScriptFile {
	private static final String qn = "Q618_IntoTheFlame";

	// NPCs
	private static final int KLEIN = 31540;
	private static final int HILDA = 31271;

	// Items
	private static final int VACUALITE_ORE = 7265;
	private static final int VACUALITE = 7266;

	// Reward
	private static final int FLOATING_STONE = 7267;

	public Q618_IntoTheFlame(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { VACUALITE_ORE, VACUALITE };

		addStartNpc(KLEIN);
		addTalkId(KLEIN, HILDA);

		// Kookaburras, Bandersnatches, Grendels
		addKillId(21274, 21275, 21276, 21277, 21282, 21283, 21284, 21285, 21290, 21291, 21292, 21293);
	}

	public static void onLoad() {
		new Q618_IntoTheFlame(618, "Q618_IntoTheFlame", "Into The Flame", "quests");
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		final int cond = st.getInt("cond");
		if (cond == 0 && "31540-03.htm".equalsIgnoreCase(event)) {
			st.setState(QuestState.STARTED);
			st.set("cond", "1");
			st.playSound(QuestState.SOUND_ACCEPT);
		} else if ("31540-05.htm".equalsIgnoreCase(event)) {
			if (cond == 4 && st.getQuestItemsCount(VACUALITE) > 0) {
				st.takeItems(VACUALITE, 1);
				st.giveItems(FLOATING_STONE, 1);
				st.playSound(QuestState.SOUND_FINISH);
				st.exitQuest(true);
			} else
				htmltext = "31540-03.htm";
		} else if (cond == 1 && "31271-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31271-05.htm".equalsIgnoreCase(event)) {
			if (cond == 3 && st.getQuestItemsCount(VACUALITE_ORE) == 50) {
				st.takeItems(VACUALITE_ORE, -1);
				st.giveItems(VACUALITE, 1);
				st.set("cond", "4");
				st.playSound(QuestState.SOUND_MIDDLE);
			} else
				htmltext = "31271-03.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(final L2Npc npc, final L2PcInstance player) {
		String htmltext = getNoQuestMsg();
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		switch (st.getState()) {
			case QuestState.CREATED:
				if (player.getLevel() < 60) {
					htmltext = "31540-01.htm";
					st.exitQuest(true);
				} else
					htmltext = "31540-02.htm";
				break;

			case QuestState.STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getNpcId()) {
					case KLEIN:
						htmltext = cond == 4 && st.getQuestItemsCount(VACUALITE) > 0 ? "31540-04.htm" : "31540-03.htm";
						break;

					case HILDA:
						if (cond == 1)
							htmltext = "31271-01.htm";
						else if (cond == 3 && st.getQuestItemsCount(VACUALITE_ORE) == 50)
							htmltext = "31271-04.htm";
						else htmltext = cond == 4 ? "31271-06.htm" : "31271-03.htm";
						break;
				}
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(final L2Npc npc, final L2PcInstance player, final boolean isPet) {
		final L2PcInstance partyMember = getRandomPartyMember(player, npc, "2");
		if (partyMember == null)
			return null;

		final QuestState st = partyMember.getQuestState(qn);
		if (st.isStarted()) {
			final int count = st.getQuestItemsCount(VACUALITE_ORE);
			if (st.getInt("cond") == 2 && count < 50) {
				int chance = (int) (50 * MainConfig.RATE_QUEST_DROP);
				int numItems = chance / 100;
				chance %= 100;

				if (Rnd.get(100) < chance)
					numItems += 1;

				if (numItems > 0) {
					if (count + numItems >= 50) {
						numItems = 50 - count;
						st.playSound(QuestState.SOUND_MIDDLE);
						st.set("cond", "3");
					} else
						st.playSound(QuestState.SOUND_ITEMGET);

					st.giveItems(VACUALITE_ORE, numItems);
				}
			}
		}
		return null;
	}
}
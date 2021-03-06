/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.quests;

import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.quest.Quest;
import silentium.gameserver.model.quest.QuestState;
import silentium.gameserver.scripting.ScriptFile;

public class Q010_IntoTheWorld extends Quest implements ScriptFile {
	private static final String qn = "Q010_IntoTheWorld";

	// Items
	private static final int VeryExpensiveNecklace = 7574;

	// Rewards
	private static final int ScrollOfEscapeGiran = 7559;
	private static final int MarkOfTraveler = 7570;

	// NPCs
	private static final int Reed = 30520;
	private static final int Balanki = 30533;
	private static final int Gerald = 30650;

	public Q010_IntoTheWorld(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { VeryExpensiveNecklace };

		addStartNpc(Balanki);
		addTalkId(Balanki, Reed, Gerald);
	}

	public static void onLoad() {
		new Q010_IntoTheWorld(10, "Q010_IntoTheWorld", "Into The World", "quests");
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		final String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		if ("30533-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		} else if ("30520-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "2");
			st.giveItems(VeryExpensiveNecklace, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("30650-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "3");
			st.takeItems(VeryExpensiveNecklace, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("30520-04.htm".equalsIgnoreCase(event)) {
			st.set("cond", "4");
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("30533-05.htm".equalsIgnoreCase(event)) {
			st.giveItems(ScrollOfEscapeGiran, 1);
			st.rewardItems(MarkOfTraveler, 1);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(false);
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
				htmltext = player.getLevel() >= 3 && player.getLevel() <= 10 && player.getRace().ordinal() == 4 ? "30533-01.htm" : "30533-01a.htm";
				break;

			case QuestState.STARTED:
				final int cond = st.getInt("cond");
				switch (npc.getNpcId()) {
					case Balanki:
						if (cond >= 1 && cond <= 3)
							htmltext = "30533-03.htm";
						else if (cond == 4)
							htmltext = "30533-04.htm";
						break;

					case Reed:
						if (cond == 1)
							htmltext = "30520-01.htm";
						else if (cond == 2)
							htmltext = "30520-02a.htm";
						else if (cond == 3)
							htmltext = "30520-03.htm";
						else if (cond == 4)
							htmltext = "30520-04a.htm";
						break;

					case Gerald:
						if (cond == 2)
							htmltext = "30650-01.htm";
						else if (cond >= 3)
							htmltext = "30650-04.htm";
						break;
				}
				break;

			case QuestState.COMPLETED:
				htmltext = Quest.getAlreadyCompletedMsg();
				break;
		}

		return htmltext;
	}
}
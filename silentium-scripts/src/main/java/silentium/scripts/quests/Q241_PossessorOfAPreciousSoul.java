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

public class Q241_PossessorOfAPreciousSoul extends Quest implements ScriptFile {
	private static final String qn = "Q241_PossessorOfAPreciousSoul";

	// NPCs
	private static final int TALIEN = 31739;
	private static final int GABRIELLE = 30753;
	private static final int GILMORE = 30754;
	private static final int KANTABILON = 31042;
	private static final int STEDMIEL = 30692;
	private static final int VIRGIL = 31742;
	private static final int OGMAR = 31744;
	private static final int RAHORAKTI = 31336;
	private static final int KASSANDRA = 31743;
	private static final int CARADINE = 31740;
	private static final int NOEL = 31272;

	// Monsters
	private static final int BARAHAM = 27113;
	private static final int MALRUK_SUCCUBUS = 20244;
	private static final int MALRUK_SUCCUBUS_TUREN = 20245;
	private static final int SPLINTER_STAKATO = 21508;
	private static final int SPLINTER_STAKATO_WALKER = 21509;
	private static final int SPLINTER_STAKATO_SOLDIER = 21510;
	private static final int SPLINTER_STAKATO_DRONE1 = 21511;
	private static final int SPLINTER_STAKATO_DRONE2 = 21512;

	// Items
	private static final int LEGEND_OF_SEVENTEEN = 7587;
	private static final int MALRUK_SUCCUBUS_CLAW = 7597;
	private static final int ECHO_CRYSTAL = 7589;
	private static final int POETRY_BOOK = 7588;
	private static final int CRIMSON_MOSS = 7598;
	private static final int RAHORAKTIS_MEDICINE = 7599;
	private static final int LUNARGENT = 6029;
	private static final int HELLFIRE_OIL = 6033;
	private static final int VIRGILS_LETTER = 7677;

	public Q241_PossessorOfAPreciousSoul(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		questItemIds = new int[] { LEGEND_OF_SEVENTEEN, MALRUK_SUCCUBUS_CLAW, ECHO_CRYSTAL, POETRY_BOOK, CRIMSON_MOSS, RAHORAKTIS_MEDICINE };

		addStartNpc(TALIEN);
		addTalkId(TALIEN, GABRIELLE, GILMORE, KANTABILON, STEDMIEL, VIRGIL, OGMAR, RAHORAKTI, KASSANDRA, CARADINE, NOEL);

		addKillId(BARAHAM, MALRUK_SUCCUBUS, MALRUK_SUCCUBUS_TUREN, SPLINTER_STAKATO, SPLINTER_STAKATO_WALKER, SPLINTER_STAKATO_SOLDIER, SPLINTER_STAKATO_DRONE1, SPLINTER_STAKATO_DRONE2);
	}

	public static void onLoad() {
		new Q241_PossessorOfAPreciousSoul(241, "Q241_PossessorOfAPreciousSoul", "Possessor Of A Precious Soul 1", "quests");
	}

	@Override
	public String onAdvEvent(final String event, final L2Npc npc, final L2PcInstance player) {
		String htmltext = event;
		final QuestState st = player.getQuestState(qn);
		if (st == null)
			return htmltext;

		// Talien
		if ("31739-03.htm".equalsIgnoreCase(event)) {
			st.set("cond", "1");
			st.setState(QuestState.STARTED);
			st.playSound(QuestState.SOUND_ACCEPT);
		} else if ("31739-07.htm".equalsIgnoreCase(event)) {
			st.set("cond", "5");
			st.takeItems(LEGEND_OF_SEVENTEEN, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31739-10.htm".equalsIgnoreCase(event)) {
			st.set("cond", "9");
			st.takeItems(ECHO_CRYSTAL, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31739-13.htm".equalsIgnoreCase(event)) {
			st.set("cond", "11");
			st.takeItems(POETRY_BOOK, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Gabrielle
		else if ("30753-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "2");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Gilmore
		else if ("30754-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "3");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Kantabilon
		else if ("31042-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "6");
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31042-05.htm".equalsIgnoreCase(event)) {
			st.set("cond", "8");
			st.takeItems(MALRUK_SUCCUBUS_CLAW, 10);
			st.giveItems(ECHO_CRYSTAL, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Stedmiel
		else if ("30692-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "10");
			st.giveItems(POETRY_BOOK, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Virgil
		else if ("31742-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "12");
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31742-05.htm".equalsIgnoreCase(event)) {
			st.set("cond", "18");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Ogmar
		else if ("31744-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "13");
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Rahorakti
		else if ("31336-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "14");
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31336-05.htm".equalsIgnoreCase(event)) {
			st.set("cond", "16");
			st.takeItems(CRIMSON_MOSS, 5);
			st.giveItems(RAHORAKTIS_MEDICINE, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Kassandra
		else if ("31743-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "17");
			st.takeItems(RAHORAKTIS_MEDICINE, 1);
			st.playSound(QuestState.SOUND_MIDDLE);
		}
		// Caradine
		else if ("31740-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "19");
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31740-05.htm".equalsIgnoreCase(event)) {
			st.giveItems(VIRGILS_LETTER, 1);
			st.addExpAndSp(263043, 0);
			st.playSound(QuestState.SOUND_FINISH);
			st.exitQuest(false);
		}
		// Noel
		else if ("31272-02.htm".equalsIgnoreCase(event)) {
			st.set("cond", "20");
			st.playSound(QuestState.SOUND_MIDDLE);
		} else if ("31272-05.htm".equalsIgnoreCase(event)) {
			if (st.hasQuestItems(HELLFIRE_OIL) && st.getQuestItemsCount(LUNARGENT) >= 5) {
				st.takeItems(LUNARGENT, 5);
				st.takeItems(HELLFIRE_OIL, 1);
				st.set("cond", "21");
				st.playSound(QuestState.SOUND_MIDDLE);
			} else
				htmltext = "31272-07.htm";
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
				if (!player.isSubClassActive() || player.getLevel() < 50) {
					htmltext = "31739-02.htm";
					st.exitQuest(true);
				} else
					htmltext = "31739-01.htm";
				break;

			case QuestState.STARTED:
				if (!player.isSubClassActive())
					break;

				final int cond = st.getInt("cond");
				switch (npc.getNpcId()) {
					case TALIEN:
						if (cond == 1)
							htmltext = "31739-04.htm";
						else if (cond == 2 || cond == 3)
							htmltext = "31739-05.htm";
						else if (cond == 4)
							htmltext = "31739-06.htm";
						else if (cond == 5)
							htmltext = "31739-08.htm";
						else if (cond == 8)
							htmltext = "31739-09.htm";
						else if (cond == 9)
							htmltext = "31739-11.htm";
						else if (cond == 10)
							htmltext = "31739-12.htm";
						else if (cond == 11)
							htmltext = "31739-14.htm";
						break;

					case GABRIELLE:
						if (cond == 1)
							htmltext = "30753-01.htm";
						else if (cond == 2)
							htmltext = "30753-03.htm";
						break;

					case GILMORE:
						if (cond == 2)
							htmltext = "30754-01.htm";
						else if (cond == 3)
							htmltext = "30754-03.htm";
						break;

					case KANTABILON:
						if (cond == 5)
							htmltext = "31042-01.htm";
						else if (cond == 6)
							htmltext = "31042-03.htm";
						else if (cond == 7)
							htmltext = "31042-04.htm";
						else if (cond == 8)
							htmltext = "31042-06.htm";
						break;

					case STEDMIEL:
						if (cond == 9)
							htmltext = "30692-01.htm";
						else if (cond == 10)
							htmltext = "30692-03.htm";
						break;

					case VIRGIL:
						if (cond == 11)
							htmltext = "31742-01.htm";
						else if (cond == 12)
							htmltext = "31742-03.htm";
						else if (cond == 17)
							htmltext = "31742-04.htm";
						else if (cond == 18)
							htmltext = "31742-06.htm";
						break;

					case OGMAR:
						if (cond == 12)
							htmltext = "31744-01.htm";
						else if (cond == 13)
							htmltext = "31744-03.htm";
						break;

					case RAHORAKTI:
						if (cond == 13)
							htmltext = "31336-01.htm";
						else if (cond == 14)
							htmltext = "31336-03.htm";
						else if (cond == 15)
							htmltext = "31336-04.htm";
						else if (cond == 16)
							htmltext = "31336-06.htm";
						break;

					case KASSANDRA:
						if (cond == 16)
							htmltext = "31743-01.htm";
						else if (cond == 17)
							htmltext = "31743-03.htm";
						break;

					case CARADINE:
						if (cond == 18)
							htmltext = "31740-01.htm";
						else if (cond == 19)
							htmltext = "31740-03.htm";
						else if (cond == 21)
							htmltext = "31740-04.htm";
						break;

					case NOEL:
						if (cond == 19)
							htmltext = "31272-01.htm";
						else if (cond == 20) {
							htmltext = st.hasQuestItems(HELLFIRE_OIL) && st.getQuestItemsCount(LUNARGENT) >= 5 ? "31272-04.htm" : "31272-03.htm";
						} else if (cond == 21)
							htmltext = "31272-06.htm";
						break;
				}
				break;

			case QuestState.COMPLETED:
				htmltext = getAlreadyCompletedMsg();
				break;
		}
		return htmltext;
	}

	@Override
	public String onKill(final L2Npc npc, final L2PcInstance player, final boolean isPet) {
		final QuestState st = player.getQuestState(qn);
		if (st == null || !st.isStarted() || !player.isSubClassActive())
			return null;

		switch (npc.getNpcId()) {
			case BARAHAM:
				if (checkPlayerCondition(player, npc, "cond", "3")) {
					st.set("cond", "4");
					st.giveItems(LEGEND_OF_SEVENTEEN, 1);
					st.playSound(QuestState.SOUND_MIDDLE);
				}
				break;

			case MALRUK_SUCCUBUS:
			case MALRUK_SUCCUBUS_TUREN:
				if (checkPlayerCondition(player, npc, "cond", "6"))
					if (st.dropQuestItems(MALRUK_SUCCUBUS_CLAW, 1, 10, 200000))
						st.set("cond", "7");
				break;

			case SPLINTER_STAKATO:
			case SPLINTER_STAKATO_WALKER:
			case SPLINTER_STAKATO_SOLDIER:
			case SPLINTER_STAKATO_DRONE1:
			case SPLINTER_STAKATO_DRONE2:
				if (checkPlayerCondition(player, npc, "cond", "14"))
					if (st.dropQuestItems(CRIMSON_MOSS, 1, 5, 25000))
						st.set("cond", "15");
				break;
		}
		return null;
	}
}
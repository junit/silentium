/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.scripts.ai;

import silentium.commons.utils.Rnd;
import silentium.gameserver.ai.CtrlIntention;
import silentium.gameserver.instancemanager.GrandBossManager;
import silentium.gameserver.model.L2CharPosition;
import silentium.gameserver.model.L2Object;
import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2GrandBossInstance;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.quest.Quest;
import silentium.gameserver.network.serverpackets.PlaySound;
import silentium.gameserver.network.serverpackets.SocialAction;
import silentium.gameserver.network.serverpackets.SpecialCamera;
import silentium.gameserver.scripting.ScriptFile;
import silentium.gameserver.templates.StatsSet;
import silentium.gameserver.utils.Util;

import java.util.Collection;

/**
 * Dr. Chaos is a boss @ Pavel's Ruins. Some things to know :
 * <ul>
 * <li>As a mad scientist, he thinks all are spies, and for so if you stand too much longer near him you're considered as an
 * "assassin from Black Anvil Guild".</li>
 * <li>You can chat with him, but if you try too much he will become angry.</li>
 * <li>That adaptation sends a decent cinematic made with the different social actions too.</li>
 * <li>The status of the RB is saved under GBs table, in order to retrieve the state if server restarts.</li>
 * <li>The spawn of the different NPCs (Dr. Chaos / War golem) is handled by that script aswell.</li>
 * </ul>
 *
 * @author Kerberos, Tryskell.
 */
public class DrChaos extends Quest implements ScriptFile {
	private static final int DOCTOR_CHAOS = 32033;
	private static final int CHAOS_GOLEM = 25512;

	private long _lastAttackVsGolem = 0;
	private int _pissedOffTimer;

	// Dr. Chaos status tracking
	private static final byte NORMAL = 0; // Dr. Chaos is in NPC form.
	private static final byte CRAZY = 1; // Dr. Chaos entered on golem form.
	private static final byte DEAD = 2; // Dr. Chaos has been killed and has not yet spawned.

	public static void onLoad() {
		new DrChaos(-1, "DrChaos", "DrChaos", "ai");
	}

	public DrChaos(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);

		addFirstTalkId(DOCTOR_CHAOS); // Different HTMs following actual humor.
		addSpawnId(DOCTOR_CHAOS); // Timer activation at 30sec + paranoia activity.

		addKillId(CHAOS_GOLEM); // Message + despawn.
		addAttackActId(CHAOS_GOLEM); // Random messages when he attacks.

		final StatsSet info = GrandBossManager.getInstance().getStatsSet(CHAOS_GOLEM);
		final int status = GrandBossManager.getInstance().getBossStatus(CHAOS_GOLEM);

		// Load the reset date and time for Dr. Chaos from DB.
		if (status == DEAD) {
			final long temp = info.getLong("respawn_time") - System.currentTimeMillis();
			if (temp > 0)
				startQuestTimer("reset_drchaos", temp, null, null);
			else {
				// The time has already expired while the server was offline. Delete the saved time and
				// immediately spawn Dr. Chaos. Also the state need to be changed for NORMAL
				addSpawn(DOCTOR_CHAOS, 96320, -110912, -3328, 8191, false, 0);
				GrandBossManager.getInstance().setBossStatus(CHAOS_GOLEM, NORMAL);
			}
		}
		// Spawn the war golem.
		else if (status == CRAZY) {
			final int loc_x = info.getInteger("loc_x");
			final int loc_y = info.getInteger("loc_y");
			final int loc_z = info.getInteger("loc_z");
			final int heading = info.getInteger("heading");
			final int hp = info.getInteger("currentHP");
			final int mp = info.getInteger("currentMP");

			final L2GrandBossInstance golem = (L2GrandBossInstance) addSpawn(CHAOS_GOLEM, loc_x, loc_y, loc_z, heading, false, 0);
			GrandBossManager.getInstance().addBoss(golem);

			final L2Npc _golem = golem;

			_golem.setCurrentHpMp(hp, mp);
			_golem.setRunning();

			// start monitoring Dr. Chaos's inactivity
			_lastAttackVsGolem = System.currentTimeMillis();
			startQuestTimer("golem_despawn", 60000, _golem, null, true);
		}
		// Spawn the regular NPC.
		else
			addSpawn(DOCTOR_CHAOS, 96320, -110912, -3328, 8191, false, 0);
	}

	@Override
	public String onAdvEvent(final String event, L2Npc npc, final L2PcInstance player) {
		if ("reset_drchaos".equalsIgnoreCase(event)) {
			GrandBossManager.getInstance().setBossStatus(CHAOS_GOLEM, NORMAL);
			addSpawn(DOCTOR_CHAOS, 96320, -110912, -3328, 8191, false, 0);
		}
		// despawn the live Dr. Chaos after 30 minutes of inactivity
		else if ("golem_despawn".equalsIgnoreCase(event) && npc != null) {
			if (npc.getNpcId() == CHAOS_GOLEM) {
				if (_lastAttackVsGolem + 1800000 < System.currentTimeMillis()) {
					// Despawn the war golem.
					npc.deleteMe();

					addSpawn(DOCTOR_CHAOS, 96320, -110912, -3328, 8191, false, 0); // spawn Dr. Chaos
					GrandBossManager.getInstance().setBossStatus(CHAOS_GOLEM, NORMAL); // mark Dr. Chaos is not crazy any more
					cancelQuestTimer("golem_despawn", npc, null);
				}
			}
		} else if ("1".equalsIgnoreCase(event)) {
			npc.broadcastPacket(new SocialAction(npc, 2));
			npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1, -200, 15, 5500, 13500, 0, 0, 1, 0));
		} else if ("2".equalsIgnoreCase(event))
			npc.broadcastPacket(new SocialAction(npc, 3));
		else if ("3".equalsIgnoreCase(event))
			npc.broadcastPacket(new SocialAction(npc, 1));
		else if ("4".equalsIgnoreCase(event)) {
			npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 1, -150, 10, 3500, 5000, 0, 0, 1, 0));
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(95928, -110671, -3340, 0));
		} else if ("5".equalsIgnoreCase(event)) {
			// Delete Dr. Chaos && spawn the war golem.
			npc.deleteMe();
			final L2GrandBossInstance golem = (L2GrandBossInstance) addSpawn(CHAOS_GOLEM, 96080, -110822, -3343, 0, false, 0);
			GrandBossManager.getInstance().addBoss(golem);

			// The "npc" variable attribution is now for the golem.
			npc = golem;
			npc.broadcastPacket(new SpecialCamera(npc.getObjectId(), 30, 200, 20, 6000, 8000, 0, 0, 1, 0));
			npc.broadcastPacket(new SocialAction(npc, 1));
			npc.broadcastPacket(new PlaySound(1, "Rm03_A", 0, 0, 0, 0, 0));

			// start monitoring Dr. Chaos's inactivity
			_lastAttackVsGolem = System.currentTimeMillis();
			startQuestTimer("golem_despawn", 60000, npc, null, true);
		}
		// Check every sec if someone is in range, if found, launch one task to decrease the timer.
		else if ("paranoia_activity".equalsIgnoreCase(event)) {
			if (GrandBossManager.getInstance().getBossStatus(CHAOS_GOLEM) == NORMAL) {
				final Collection<L2Object> objs = npc.getKnownList().getKnownObjects().values();
				for (final L2Object obj : objs) {
					if (obj instanceof L2PcInstance && Util.checkIfInShortRadius(500, npc, obj, true)) {
						if (((L2PcInstance) obj).isDead())
							continue;

						_pissedOffTimer -= 1;

						// Make him speak.
						if (_pissedOffTimer == 15)
							npc.broadcastNpcSay("How dare you trespass into my territory! Have you no fear?");

						// That was "too much" for that time.
						if (_pissedOffTimer <= 0)
							crazyMidgetBecomesAngry(npc);
					}
				}
			}
		}

		return super.onAdvEvent(event, npc, player);
	}

	@Override
	public String onFirstTalk(final L2Npc npc, final L2PcInstance player) {
		String htmltext = "";

		if (GrandBossManager.getInstance().getBossStatus(CHAOS_GOLEM) == NORMAL) {
			_pissedOffTimer -= 1 + Rnd.get(5); // remove 1-5 secs.

			if (_pissedOffTimer > 20 && _pissedOffTimer <= 30)
				htmltext = "<html><body>Doctor Chaos:<br>What?! Who are you? How did you come here?<br>You really look suspicious... Aren't those filthy members of Black Anvil guild send you? No? Mhhhhh... I don't trust you!</body></html>";
			else if (_pissedOffTimer > 10 && _pissedOffTimer <= 20)
				htmltext = "<html><body>Doctor Chaos:<br>Why are you standing here? Don't you see it's a private propertie? Don't look at him with those eyes... Did you smile?! Don't make fun of me! He will ... destroy ... you ... if you continue!</body></html>";
			else if (_pissedOffTimer > 0 && _pissedOffTimer <= 10)
				htmltext = "<html><body>Doctor Chaos:<br>I know why you are here, traitor! He discovered your plans! You are assassin ... sent by the Black Anvil guild! But you won't kill the Emperor of Evil!</body></html>";
			else if (_pissedOffTimer <= 0) // That was "too much" for that time.
				crazyMidgetBecomesAngry(npc);
		}

		return htmltext;
	}

	@Override
	public String onSpawn(final L2Npc npc) {
		// 30 seconds timer at initialization.
		_pissedOffTimer = 30;

		// Initialization of the paranoia.
		startQuestTimer("paranoia_activity", 1000, npc, null, true);

		return null;
	}

	@Override
	public String onKill(final L2Npc npc, final L2PcInstance player, final boolean isPet) {
		cancelQuestTimer("golem_despawn", npc, null);
		npc.broadcastNpcSay("Urggh! You will pay dearly for this insult.");

		// "lock" Dr. Chaos for regular RB time (12H fixed + 24H random)
		final long respawnTime = 43200000 + Rnd.get(86400000);
		GrandBossManager.getInstance().setBossStatus(CHAOS_GOLEM, DEAD);
		startQuestTimer("reset_drchaos", respawnTime, null, null);

		// also save the respawn time so that the info is maintained past reboots
		final StatsSet info = GrandBossManager.getInstance().getStatsSet(CHAOS_GOLEM);
		info.set("respawn_time", System.currentTimeMillis() + respawnTime);
		GrandBossManager.getInstance().setStatsSet(CHAOS_GOLEM, info);

		return null;
	}

	@Override
	public String onAttackAct(final L2Npc npc, final L2PcInstance victim) {
		final int chance = Rnd.get(300);

		// Choose a message from 3 choices (1/100)
		if (chance < 3) {
			String message = "";
			switch (chance) {
				case 0:
					message = "Bwah-ha-ha! Your doom is at hand! Behold the Ultra Secret Super Weapon!";
					break;
				case 1:
					message = "Foolish, insignificant creatures! How dare you challenge me!";
					break;
				default:
					message = "I see that none will challenge me now!";
					break;
			}

			// Make him speak.
			npc.broadcastNpcSay(message);
		}
		return null;
	}

	/**
	 * Launches the complete animation.
	 *
	 * @param npc    the midget.
	 * @param player the victim.
	 */
	private void crazyMidgetBecomesAngry(final L2Npc npc) {
		if (GrandBossManager.getInstance().getBossStatus(CHAOS_GOLEM) == NORMAL) {
			// Set the status to "crazy".
			GrandBossManager.getInstance().setBossStatus(CHAOS_GOLEM, CRAZY);

			// Cancels the paranoia timer.
			cancelQuestTimer("paranoia_activity", npc, null);

			// Makes the NPC moves near the Strange Box speaking.
			npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new L2CharPosition(96323, -110914, -3328, 0));
			npc.broadcastNpcSay("Fools! Why haven't you fled yet? Prepare to learn a lesson!");

			// Delayed animation timers.
			startQuestTimer("1", 2000, npc, null); // 2 secs, time to launch dr.C anim 2. Cam 1 on.
			startQuestTimer("2", 4000, npc, null); // 2,5 secs, time to launch dr.C anim 3.
			startQuestTimer("3", 6500, npc, null); // 6 secs, time to launch dr.C anim 1.
			startQuestTimer("4", 12500, npc, null); // 4,5 secs to make the NPC moves to the grotto. Cam 2 on.
			startQuestTimer("5", 17000, npc, null); // 4 secs for golem spawn, and golem anim. Cam 3 on.
		}
	}
}
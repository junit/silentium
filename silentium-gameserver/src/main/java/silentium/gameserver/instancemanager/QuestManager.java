/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.instancemanager;

import java.util.Map;

import javolution.util.FastMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import silentium.gameserver.model.quest.Quest;
import silentium.gameserver.scripting.ScriptManager;

public class QuestManager extends ScriptManager<Quest>
{
	protected static final Logger _log = LoggerFactory.getLogger(QuestManager.class.getName());

	public static final QuestManager getInstance()
	{
		return SingletonHolder._instance;
	}

	private final Map<String, Quest> _quests = new FastMap<>();

	protected QuestManager()
	{
	}

	public final void report()
	{
		_log.info("QuestManager: Loaded " + _quests.size() + " quests.");
	}

	public final void save()
	{
		for (Quest q : _quests.values())
		{
			q.saveGlobalData();
		}
	}

	public final Quest getQuest(String name)
	{
		return _quests.get(name);
	}

	public final Quest getQuest(int questId)
	{
		for (Quest q : _quests.values())
		{
			if (q.getQuestIntId() == questId)
				return q;
		}
		return null;
	}

	public final void addQuest(Quest newQuest)
	{
		if (newQuest == null)
			throw new IllegalArgumentException("Quest argument cannot be null");
		Quest old = _quests.get(newQuest.getName());

		// FIXME: unloading the old quest at this point is a tad too late.
		// the new quest has already initialized itself and read the data, starting
		// an unpredictable number of tasks with that data. The old quest will now
		// save data which will never be read.
		// However, requesting the newQuest to re-read the data is not necessarily a
		// good option, since the newQuest may have already started timers, spawned NPCs
		// or taken any other action which it might re-take by re-reading the data.
		// the current solution properly closes the running tasks of the old quest but
		// ignores the data; perhaps the least of all evils...
		if (old != null)
			_log.info("QuestManager: Replaced: (" + old.getName() + ") with a new version (" + newQuest.getName() + ").");
		_quests.put(newQuest.getName(), newQuest);
	}

	public final boolean removeQuest(Quest q)
	{
		return _quests.remove(q.getName()) != null;
	}

	/**
	 * @see silentium.gameserver.scripting.ScriptManager#getAllManagedScripts()
	 */
	@Override
	public Iterable<Quest> getAllManagedScripts()
	{
		return _quests.values();
	}

	/**
	 * @see silentium.gameserver.scripting.ScriptManager#getScriptManagerName()
	 */
	@Override
	public String getScriptManagerName()
	{
		return "QuestManager";
	}

	private static class SingletonHolder
	{
		protected static final QuestManager _instance = new QuestManager();
	}
}

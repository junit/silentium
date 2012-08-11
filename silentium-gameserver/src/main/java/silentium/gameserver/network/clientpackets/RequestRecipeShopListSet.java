/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have
 * received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.network.clientpackets;

import silentium.gameserver.configs.PlayersConfig;
import silentium.gameserver.model.L2ManufactureItem;
import silentium.gameserver.model.L2ManufactureList;
import silentium.gameserver.model.actor.L2Character;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.serverpackets.ActionFailed;
import silentium.gameserver.network.serverpackets.RecipeShopMsg;

/**
 * cd(dd)
 */
public final class RequestRecipeShopListSet extends L2GameClientPacket
{
	private int _count;
	private int[] _items;

	@Override
	protected void readImpl()
	{
		_count = readD();
		if (_count < 0 || _count * 8 > _buf.remaining() || _count > PlayersConfig.MAX_ITEM_IN_PACKET)
			_count = 0;

		_items = new int[_count * 2];
		for (int x = 0; x < _count; x++)
		{
			int recipeID = readD();
			_items[x * 2 + 0] = recipeID;
			int cost = readD();
			_items[x * 2 + 1] = cost;
		}
	}

	@Override
	protected void runImpl()
	{
		final L2PcInstance player = getClient().getActiveChar();
		if (player == null)
			return;

		if (player.isInDuel())
		{
			player.sendPacket(SystemMessageId.CANT_OPERATE_PRIVATE_STORE_DURING_COMBAT);
			return;
		}

		if (player.isInsideZone(L2Character.ZONE_NOSTORE))
		{
			player.sendPacket(SystemMessageId.NO_PRIVATE_WORKSHOP_HERE);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}

		if (_count == 0)
		{
			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_NONE);
			player.broadcastUserInfo();
			player.standUp();
		}
		else
		{
			L2ManufactureList createList = new L2ManufactureList();

			for (int x = 0; x < _count; x++)
			{
				int recipeID = _items[x * 2 + 0];
				int cost = _items[x * 2 + 1];
				createList.add(new L2ManufactureItem(recipeID, cost));
			}
			createList.setStoreName(player.getCreateList() != null ? player.getCreateList().getStoreName() : "");
			player.setCreateList(createList);

			player.setPrivateStoreType(L2PcInstance.STORE_PRIVATE_MANUFACTURE);
			player.sitDown();
			player.broadcastUserInfo();
			player.sendPacket(new RecipeShopMsg(player));
			player.broadcastPacket(new RecipeShopMsg(player));
		}
	}
}
/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.network.clientpackets;

import silentium.gameserver.model.PartyMatchRoom;
import silentium.gameserver.model.PartyMatchRoomList;
import silentium.gameserver.model.PartyMatchWaitingList;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.network.SystemMessageId;
import silentium.gameserver.network.serverpackets.ExManagePartyRoomMember;
import silentium.gameserver.network.serverpackets.ExPartyRoomMember;
import silentium.gameserver.network.serverpackets.PartyMatchDetail;
import silentium.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Gnacik
 */
public final class RequestPartyMatchDetail extends L2GameClientPacket
{
	private int _roomid;
	private int _unk1;
	private int _unk2;
	private int _unk3;

	@Override
	protected void readImpl()
	{
		_roomid = readD();
		/*
		 * IF player click on Room all unk are 0 IF player click AutoJoin values are -1 1 1
		 */
		_unk1 = readD();
		_unk2 = readD();
		_unk3 = readD();
	}

	@Override
	protected void runImpl()
	{
		L2PcInstance _activeChar = getClient().getActiveChar();
		if (_activeChar == null)
			return;

		PartyMatchRoom _room = PartyMatchRoomList.getInstance().getRoom(_roomid);
		if (_room == null)
			return;

		if ((_activeChar.getLevel() >= _room.getMinLvl()) && (_activeChar.getLevel() <= _room.getMaxLvl()))
		{
			// Remove from waiting list
			PartyMatchWaitingList.getInstance().removePlayer(_activeChar);

			_activeChar.setPartyRoom(_roomid);

			_activeChar.sendPacket(new PartyMatchDetail(_activeChar, _room));
			_activeChar.sendPacket(new ExPartyRoomMember(_activeChar, _room, 0));

			for (L2PcInstance _member : _room.getPartyMembers())
			{
				if (_member == null)
					continue;

				_member.sendPacket(new ExManagePartyRoomMember(_activeChar, _room, 0));
				_member.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_ENTERED_PARTY_ROOM).addPcName(_activeChar));
			}
			_room.addMember(_activeChar);

			// Info Broadcast
			_activeChar.broadcastUserInfo();
		}
		else
			_activeChar.sendPacket(SystemMessageId.CANT_ENTER_PARTY_ROOM);
	}
}
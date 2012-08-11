/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program
 * is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have
 * received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package silentium.gameserver.configs;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public final class CustomConfig extends ConfigEngine
{
	public static boolean UNLIM_SHOTS;
	public static boolean UNLIM_SSHOTS;
	public static int START_SUBCLASS_LEVEL;
	public static boolean ANNOUNCE_BAN_CHAT;
	public static boolean ANNOUNCE_UNBAN_CHAT;
	public static boolean ANNOUNCE_BAN_ACC;
	public static boolean ANNOUNCE_UNBAN_ACC;
	public static boolean ANNOUNCE_JAIL;
	public static boolean ANNOUNCE_UNJAIL;
	public static boolean USE_PREMIUMSERVICE;
	public static float PREMIUM_RATE_XP;
	public static float PREMIUM_RATE_SP;
	public static float PREMIUM_RATE_DROP_SPOIL;
	public static float PREMIUM_RATE_DROP_ITEMS;
	public static float PREMIUM_RATE_DROP_QUEST;
	public static float PREMIUM_RATE_DROP_ITEMS_BY_RAID;
	public static float PREMIUM_RATE_DROP_ADENA;
	public static boolean SPAWN_CHAR;
	public static int SPAWN_X;
	public static int SPAWN_Y;
	public static int SPAWN_Z;
	public static boolean USE_SAY_FILTER;
	public static String CHAT_FILTER_CHARS;
	public static boolean RWHO_LOG;
	public static int RWHO_FORCE_INC;
	public static int RWHO_KEEP_STAT;
	public static int RWHO_MAX_ONLINE;
	public static boolean RWHO_SEND_TRASH;
	public static int RWHO_ONLINE_INCREMENT;
	public static float RWHO_PRIV_STORE_FACTOR;
	public static int RWHO_ARRAY[] = new int[13];

	public static void load()
	{
		try (InputStream is = new FileInputStream(CUSTOM_FILE))
		{
			Properties custom = new Properties();
			custom.load(is);
			is.close();

			UNLIM_SHOTS = Boolean.parseBoolean(custom.getProperty("UnlimitedPetShots", "False"));
			UNLIM_SSHOTS = Boolean.parseBoolean(custom.getProperty("UnlimitedCharacterShots", "False"));

			START_SUBCLASS_LEVEL = Integer.parseInt(custom.getProperty("StartSubclassLevel", "40"));

			ANNOUNCE_BAN_CHAT = Boolean.parseBoolean(custom.getProperty("AnnounceBanChat", "false"));
			ANNOUNCE_UNBAN_CHAT = Boolean.parseBoolean(custom.getProperty("AnnounceUnbanChat", "false"));
			ANNOUNCE_BAN_ACC = Boolean.parseBoolean(custom.getProperty("AnnounceBanAccount", "false"));
			ANNOUNCE_UNBAN_ACC = Boolean.parseBoolean(custom.getProperty("AnnounceUnbanAccount", "false"));
			ANNOUNCE_JAIL = Boolean.parseBoolean(custom.getProperty("AnnounceJail", "false"));
			ANNOUNCE_UNJAIL = Boolean.parseBoolean(custom.getProperty("AnnounceUnjail", "false"));

			USE_PREMIUMSERVICE = Boolean.parseBoolean(custom.getProperty("UsePremiumServices", "False"));
			PREMIUM_RATE_XP = Float.parseFloat(custom.getProperty("PremiumRateXp", "2"));
			PREMIUM_RATE_SP = Float.parseFloat(custom.getProperty("PremiumRateSp", "2"));
			PREMIUM_RATE_DROP_SPOIL = Float.parseFloat(custom.getProperty("PremiumRateDropSpoil", "2"));
			PREMIUM_RATE_DROP_ITEMS = Float.parseFloat(custom.getProperty("PremiumRateDropItems", "2"));
			PREMIUM_RATE_DROP_QUEST = Float.parseFloat(custom.getProperty("PremiumRateDropQuest", "2"));
			PREMIUM_RATE_DROP_ITEMS_BY_RAID = Float.parseFloat(custom.getProperty("PremiumRateRaidDropItems", "2"));
			PREMIUM_RATE_DROP_ADENA = Float.parseFloat(custom.getProperty("PremiumRateDropAdena", "2"));

			SPAWN_CHAR = Boolean.parseBoolean(custom.getProperty("CustomSpawn", "false"));
			SPAWN_X = Integer.parseInt(custom.getProperty("SpawnX", ""));
			SPAWN_Y = Integer.parseInt(custom.getProperty("SpawnY", ""));
			SPAWN_Z = Integer.parseInt(custom.getProperty("SpawnZ", ""));

			USE_SAY_FILTER = Boolean.parseBoolean(custom.getProperty("UseChatFilter", "False"));
			CHAT_FILTER_CHARS = custom.getProperty("ChatFilterChars", "[censored]");

			Random ppc = new Random();
			int z = ppc.nextInt(6);
			if (z == 0)
				z += 2;
			for (int x = 0; x < 8; x++)
			{
				if (x == 4)
					RWHO_ARRAY[x] = 44;
				else
					RWHO_ARRAY[x] = 51 + ppc.nextInt(z);
			}
			RWHO_ARRAY[11] = 37265 + ppc.nextInt(z * 2 + 3);
			RWHO_ARRAY[8] = 51 + ppc.nextInt(z);
			z = 36224 + ppc.nextInt(z * 2);
			RWHO_ARRAY[9] = z;
			RWHO_ARRAY[10] = z;
			RWHO_ARRAY[12] = 1;
			RWHO_LOG = Boolean.parseBoolean(custom.getProperty("RemoteWhoLog", "False"));
			RWHO_SEND_TRASH = Boolean.parseBoolean(custom.getProperty("RemoteWhoSendTrash", "False"));
			RWHO_MAX_ONLINE = Integer.parseInt(custom.getProperty("RemoteWhoMaxOnline", "0"));
			RWHO_KEEP_STAT = Integer.parseInt(custom.getProperty("RemoteOnlineKeepStat", "5"));
			RWHO_ONLINE_INCREMENT = Integer.parseInt(custom.getProperty("RemoteOnlineIncrement", "0"));
			RWHO_PRIV_STORE_FACTOR = Float.parseFloat(custom.getProperty("RemotePrivStoreFactor", "0"));
			RWHO_FORCE_INC = Integer.parseInt(custom.getProperty("RemoteWhoForceInc", "0"));

		}
		catch (Exception e)
		{
			log.warn("Server failed to load " + CUSTOM_FILE + " file.");
		}
	}
}
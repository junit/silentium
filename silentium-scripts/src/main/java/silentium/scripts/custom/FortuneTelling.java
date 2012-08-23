/*
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details. You should have received a copy of the GNU General Public License along with this program. If
 * not, see <http://l2j.ru/>.
 */
package silentium.scripts.custom;

import silentium.gameserver.model.actor.L2Npc;
import silentium.gameserver.model.actor.instance.L2PcInstance;
import silentium.gameserver.model.quest.Quest;
import silentium.gameserver.model.quest.QuestState;
import silentium.gameserver.scripting.ScriptFile;

public class FortuneTelling extends Quest implements ScriptFile {
	private static final String qn = "FortuneTelling";

	private static final String BODY = "<html><body>Шаман Мине:<br>Я вижу, что перед Вами возникает изображение... Трудно передать словами то, что я видел.<br>Как я могу поведать это? Хорошо, тогда слушайте:<br><br><center>";
	private static final String END = "</center><br><br>Примите эти слова близко к сердцу. Вы должны серьезно рассмотреть их значение...</body></html>";
	private static final String[] FORTUNE = { "То, что Вы отдали вернется к Вам выгодой.", "Когда-нибудь дракон приобретете крылья орла.", "Будьте осторожны, поскольку Вы можете быть сражены, если Вы испытываете недостаток в ясном мнении.", "Новое рассмотрение дела или начало могут быть успешными как изменение тени.",
			"Вы можете нервничать и чувствовать себя беспокоящимся из-за неблагоприятных ситуаций.", "Вы можете встретить человека, которого Вы стремились увидеть.", "Вы можете встретить много новых людей, но будет трудно найти прекрасного человека, который покорит Ваше сердце.", "Удача и возможность могут быть впереди, как будто они преподнесены в золотой ложке.",
			"Будьте уверены и действуйте всегда стойко. Вы сможете достигнуть совершенства во время нескольких непостоянных ситуациях.", "Может быть случай, когда Вы ищите утешения у людей.", "Будьте всегда независимы.", "Не расслабляйтесь со своими предосторожностями.",
			"Наблюдайте за людьми, которые проходят мимо, так как Вы можете встретить человека, который сможет помочь Вам.", "Слушайте совет, который дан Вам со скромным отношением.", "Сосредоточьтесь на том, чтобы контачить с аналогично мыслящими людьми. Они могут присоединиться к Вам для большой миссии в будущем.",
			"Пребывая занятым вместо того, чтобы быть постоянным поможет.", "Вы можете потерять свою уверенность и чувствовать себя потерянными.", "Люди вокруг Вас поощрят Вашу каждую задачу в будущем.", "Будьте добры к и заботьтесь о тех, кто близок Вам, они смогут помочь Вам в будущем.", "Ваше стремление и мечта осуществятся.",
			"Ваша ценность будет сиять, поскольку Ваш потенциал наконец раскрыт.", "Если Вы будете продолжать улыбаться без отчаяния, то люди будут доверять и помогать Вам.", "Может быть небольшая потеря, но думайте об этом как об инвестициях для вас.", "Нетерпение может расположиться впереди, поскольку ситуация неблагоприятна.",
			"Будьте ответственны со своими задачами, но не смущайтесь просить помощи у коллег.", "Вы можете попасть в опасность каждый раз, если будете импровизировать.", "Решительный акт после готового исследования привлечет людей.", "Отдых обещает большее развитие.", "Вы будете вознаграждены за Ваши усилия.",
			"Есть много вещей, которые нужно рассмотреть после столкновения с помехами.", "Рассмотрите ситуации других и лечите их всегда искренне.", "Сравнение с другими может быть полезным.", "Будьте осторожны, чтобы сдержать себя, поскольку Вас окружают искушения.", "На мгновение задержите важное решение.",
			"Будьте уверены и действуйте всегда стойко. Вы сможете достигнуть совершенства во время несколько непостоянных ситуациях.", "Посетите места, в которых Вы никогда не были прежде, это может принести удачу.", "То, что было хорошо излечиться, может столкнуться одно с другим.",
			"Ваше устойчивое преследование новой информации, поднимет Вашу ценность перед другими.", "Быть нейтральным это хороший способ, но ясность может быть полезна противореча Вашему колебанию.", "Квалифицированное уклонение необходимо, имея дело с людьми, которые выбирают борьбу, поскольку бедствие может быть его результатом.",
			"Мелочи составляют большие вещи, а ценность тривиальные вопросы.", "Вас будут ждать большие ошибки, если Вы будете не в состоянии исправить маленькие.", "На мгновение задержите важное решение.", "Лекарство необходимо для излечения от тяжелой болезни." };

	@Override
	public String onTalk(final L2Npc npc, final L2PcInstance player) {
		final QuestState st = player.getQuestState(qn);
		String htmltext = "";
		if (st == null)
			return "";
		if (st.getQuestItemsCount(57) < 1000)
			htmltext = "lowadena.htm";
		else {
			st.takeItems(57, 1000);
			st.getRandom(45);
			htmltext = BODY + FORTUNE[st.getRandom(45)] + END;
		}
		st.exitQuest(true);
		return htmltext;
	}

	public static void onLoad() {
		new FortuneTelling(-1, "FortuneTelling", "Fortune Telling", "custom");
	}

	public FortuneTelling(final int scriptId, final String name, final String dname, final String path) {
		super(scriptId, name, dname, path);
		addStartNpc(32616);
		addTalkId(32616);
	}
}
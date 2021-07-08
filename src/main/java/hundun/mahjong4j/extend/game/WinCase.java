package hundun.mahjong4j.extend.game;

import static org.mahjong4j.Score.SCORE0;

import java.util.ArrayList;
import java.util.List;

import org.mahjong4j.Score;
import org.mahjong4j.yaku.normals.NormalYaku;
import org.mahjong4j.yaku.yakuman.Yakuman;

/**
 * 当前胡牌的方案。包括自摸的方案和荣胡的方案。
 * @author hundun
 * Created on 2019/08/06
 */
public class WinCase {

	private final int han;

	private final int fu;

	private final Score score;
	
	private final List<Yakuman> yakumanList;

	private final List<NormalYaku> normalYakuList;

	public WinCase(int han, int fu, Score score, List<Yakuman> yakumanList, List<NormalYaku> normalYakuList) {
		this.han = han;
		this.fu = fu;
		this.score = score;
		this.yakumanList = yakumanList;
		this.normalYakuList = normalYakuList;
	}

	public int getHan() {
		return han;
	}

	public int getFu() {
		return fu;
	}

	public Score getScore() {
		return score;
	}

	public List<Yakuman> getYakumanList() {
		return yakumanList;
	}

	public List<NormalYaku> getNormalYakuList() {
		return normalYakuList;
	}
	
	

}

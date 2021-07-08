package hundun.mahjong4j.extend.game;

import java.util.ArrayList;
import java.util.List;

import org.mahjong4j.Player;
import org.mahjong4j.Score;
import org.mahjong4j.tile.Tile;
import org.mahjong4j.yaku.normals.NormalYaku;
import org.mahjong4j.yaku.yakuman.Yakuman;

/**
 * @author hundun
 * Created on 2019/08/05
 */
public class TenpaiCase {
	private final Tile tile;
	private final List<NormalYaku> normalYakuList;
	private final List<Yakuman> yakumanList;
	private final int sumHun;
	private final int sumFu;
	private final Score score;
	

	public TenpaiCase(Tile tile, Player player) {
		this(tile, player.getNormalYakuList(), player.getYakumanList(), player.getHan(), player.getFu(), player.getScore());
	}
	
	public TenpaiCase(Tile tile, List<NormalYaku> normalYakuList, List<Yakuman> yakumanList, int sumHun, int sumFu, Score score) {
		this.tile = tile;
		this.normalYakuList = new ArrayList<>(normalYakuList);
		this.yakumanList = new ArrayList<>(yakumanList);
		this.sumHun = sumHun;
		this.sumFu = sumFu;
		this.score = score;
	}
	
	public String yakuInfoToString(boolean infoInJapaness) {
		StringBuilder lineBuilder = new StringBuilder();
		
		if (normalYakuList.size() > 0 || yakumanList.size() > 0) {
			for (NormalYaku yaku : normalYakuList) {
			    if (infoInJapaness) {
			        lineBuilder.append(yaku.getJapanese()).append(" ").append(yaku.getHan()).append("翻，");
                } else {
                    lineBuilder.append(yaku.toString()).append(" ").append(yaku.getHan()).append("han,");
                }
				
			}
			for (Yakuman yaku : yakumanList) {
			    if (infoInJapaness) {
			        lineBuilder.append("役满 ").append(yaku.getJapanese()).append(",");
			    } else {
			        lineBuilder.append("yakuman ").append(yaku.toString()).append(",");
			    }
				
			}
			if (infoInJapaness) {
			    lineBuilder.append("合计 ").append(sumFu).append("符 ").append(sumHun).append("翻 ").append(score.getRon()).append("点");
            } else {
                lineBuilder.append("Sum:").append(sumFu).append("fu ").append(sumHun).append("hun ").append(score.getRon()).append("ron");
            }
			
			
		}
		return lineBuilder.toString();
	}
	
	public Tile getTile() {
		return tile;
	}
	
	
}

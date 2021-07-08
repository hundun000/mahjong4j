package hundun.mahjong4j.extend.utls;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

import org.mahjong4j.Player;
import org.mahjong4j.Score;
import org.mahjong4j.hands.Hands;
import org.mahjong4j.hands.Mentsu;
import org.mahjong4j.tile.Tile;
import org.mahjong4j.yaku.normals.NormalYaku;
import org.mahjong4j.yaku.yakuman.Yakuman;

import hundun.mahjong4j.extend.enhance.SuperHands;
import hundun.mahjong4j.extend.game.DiscardAdvice;
import hundun.mahjong4j.extend.game.GamePlayer;
import hundun.mahjong4j.extend.game.TenpaiCase;
import hundun.mahjong4j.extend.game.WinCase;
import hundun.mahjong4j.extend.game.board.MahjongBoard;
import hundun.mahjong4j.extend.game.board.TileKing;
import hundun.mahjong4j.extend.game.board.TileRiver;
import hundun.mahjong4j.extend.game.board.TileWall;
import hundun.mahjong4j.extend.game.board.MahjongBoard.MeiPaiType;
import hundun.mahjong4j.extend.game.board.TileRiver.TileInRiverState;

/**
 * 将java对象“字符画”化
 * @author hundun
 * Created on 2019/03/05
 */
public class CharImageTool {
	
    private static final String REACH_STICK = "(____o____)";
    
	
	
	/**
	 * @param tile 为null时，填充分割用的空白
	 * @param builder
	 */
	private static void appendOneTile(Tile tile, MultiLinesStringBuilder builder) {
		if (tile != null) {
			builder.append(tile.name().charAt(0), tile.name().charAt(1)).append("|", "|");
		} else {
		    builder.append(" ", " ");
		}
	}
	
	private enum TileBorderType {
	    /**
	     * 普通边缘，用于手牌等
	     */
	    NORMAL,
	    /**
	     * 听牌的边缘，含代表听牌的字符画
	     */
	    TEN_PAI,
	    /**
	     * 振听牌的边缘，含代表振听的字符画
	     */
	    HURI_TEN,
	    /**
	     * dora牌的边缘，含代表dora的字符画
	     */
	    DORA,
	    ;
	}
	
	private static void appendTileLeftBorder(MultiLinesStringBuilder builder) {
	    appendTileLeftBorder(TileBorderType.NORMAL, builder);
	}
	private static void appendTileLeftBorder(TileBorderType type, MultiLinesStringBuilder builder) {
		switch (type) {
		case NORMAL:
		    builder.append("|", "|");
			break;
		case TEN_PAI:
		    builder.append("!|", " |");
			break;
		case HURI_TEN:
		    builder.append(" |", "x|");
			break;
		case DORA:
		    builder.append("*|", " |");
            break;
		default:
			break;
		}
	}
	
	
	private static void appendOneMentsu(Mentsu mentsu, MultiLinesStringBuilder builder) {
		appendTileLeftBorder(builder);
		
		List<Tile> tilesInMentsu = mentsu.getTiles();
		for (Tile tile : tilesInMentsu) {
			appendOneTile(tile, builder);
		}
		// 每个面子分割一格
		appendOneTile(null, builder);
	}
	
	public static String meiPaiCandidatesToCharImage(List<? extends Mentsu> meiPaiCandidates, MeiPaiType type) {
		if (meiPaiCandidates.isEmpty()) {
			return "";
		}
		MultiLinesStringBuilder builder = new MultiLinesStringBuilder(2);

		for (Mentsu mentsu : meiPaiCandidates) {
			appendOneMentsu(mentsu, builder);
		}
		return type + ":[\n" + builder.mergeAsLines() + "]\n";
	}
	
	
	
	public static String handsToCharImage(SuperHands hands) {
	    MultiLinesStringBuilder builder = new MultiLinesStringBuilder(2);
		appendTileLeftBorder(builder);
		// last牌不与其余未副露牌在一起,即跳过一次这张牌
		boolean skipTheLast = true;
		// 添加未副露的牌
		for (int i = 0; i < SuperHands.HANDS_SIZE; i++) {
			int num = hands.getInputtedTiles()[i];
			Tile tile = Tile.valueOf(i);
			while (num > 0) {
				if (tile == hands.getLast() && skipTheLast) {
					skipTheLast = false;
				} else {
					appendOneTile(tile, builder);
				}
				num--;
			}
		}
		appendOneTile(null, builder);
		appendOneTile(null, builder);
		// 添加last
		if (hands.getLast() != null) {
			appendTileLeftBorder(builder);
			appendOneTile(hands.getLast(), builder);
			appendOneTile(null, builder);
			appendOneTile(null, builder);
		}
		// 添加副露的牌
		if (hands.getInputtedMentsuList().size() > 0) {
			for (Mentsu mentsu : hands.getInputtedMentsuList()) {
				appendOneMentsu(mentsu, builder);
			}
		}

		return builder.mergeAsLines();
	}
	
	public static String mahjongBoardToCharImage(MahjongBoard board) {
    	StringBuilder stringBuilder = new StringBuilder();
    	stringBuilder.append(board.getStateMessage()).append("\n");
    	stringBuilder.append(board.getGameEndEvent() != null ? board.getGameEndEvent().getType() : "").append("\n");
    	stringBuilder.append(tileWallAndDorasToCharImage(board.getTileWall(), board.getGeneralSituation().getDora()));
    	stringBuilder.append("=======================================\n");
    	for (int i = 0; i < MahjongBoard.NUM_PLAYERS; i++) {
    		GamePlayer player = board.getPlayer(i);
    		TileRiver tileRiver = player.getTileRiver();
    		stringBuilder.append(meiPaiCandidatesToCharImage(player.getChiCandidates(), MeiPaiType.CHI));
    		stringBuilder.append(meiPaiCandidatesToCharImage(player.getPonCandidates(), MeiPaiType.PON));
    		stringBuilder.append(meiPaiCandidatesToCharImage(player.getMinKanCandidates(), MeiPaiType.MIN_KAN));
    		stringBuilder.append(meiPaiCandidatesToCharImage(player.getAnKanCandidates(), MeiPaiType.AN_KAN));
    		stringBuilder.append(handsToCharImage(player.getHands()));
    		stringBuilder.append(reachStickToCharImage(player.getPersonalSituation().isReach()));
    		stringBuilder.append(tileRiverToCharImage(tileRiver));
    		stringBuilder.append(WinCaseToCharImage(player.getWinCase()));
    		stringBuilder.append(discardAdvicesToCharImage(player.getDiscardAdvices(), player.isCanReach()));
    		stringBuilder.append(tenpaiCasesToCharImage(player.getTenpaiCases(), player.isHuriten()));
    		stringBuilder.append("--------------------------------------\n");
    	}
    	return stringBuilder.toString();
    }
	
	private static String tileWallAndDorasToCharImage(TileWall tileWall, List<Tile> doras) {
	    StringBuilder stringBuilder = new StringBuilder();
	    
	    MultiLinesStringBuilder builder = new MultiLinesStringBuilder(2);
	    appendTileLeftBorder(TileBorderType.DORA, builder);
	    for (Tile dora : doras) {
	        appendOneTile(dora, builder);
	    }
	    appendOneTile(null, builder);
        
	    builder.append("|?|", "|?|");
        builder.append("", "X").append("", tileWall.size());
        
        stringBuilder.append(builder.mergeAsLines()).append("\n");
        return stringBuilder.toString();
    }

    private static String reachStickToCharImage(boolean isReach) {
	    if (!isReach) {
            return "";
        }
	    return REACH_STICK + "\n";
    }

    public static String tenpaiCasesToCharImage(List<TenpaiCase> tenpaiCases, boolean furiten) {
		if (tenpaiCases.isEmpty()) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		if (furiten) {
			stringBuilder.append("[!!!---furiten---!!!:\n");
		} else {
			stringBuilder.append("[tenpai:\n");
		}
		for (TenpaiCase tenpaiCase : tenpaiCases) {
		    MultiLinesStringBuilder builder = new MultiLinesStringBuilder(2);
			appendTileLeftBorder(builder);
			appendOneTile(tenpaiCase.getTile(), builder);
			builder.append(tenpaiCase.yakuInfoToString(true));
			stringBuilder.append(builder.mergeAsLines()).append("\n");
		}
		stringBuilder.append("]\n");
		return stringBuilder.toString();
	}
	
	public static String discardAdvicesToCharImage(List<DiscardAdvice> discardAdvices, boolean canReach) {
		if (discardAdvices.isEmpty()) {
			return "";
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("[advices:");
		if (canReach) {
		    stringBuilder.append(REACH_STICK).append("?");
		}
		stringBuilder.append("\n");
		for (DiscardAdvice discardAdvice : discardAdvices) {
			Tile discard = discardAdvice.getDiscardTile();
			List<Tile> tenTiles = discardAdvice.getTenpaiList();
			boolean furiten = discardAdvice.isFuriten();
			MultiLinesStringBuilder builder = new MultiLinesStringBuilder(2);
			appendTileLeftBorder(builder);
			appendOneTile(discard, builder);
			appendOneTile(null, builder);
			if (furiten) {
			    appendTileLeftBorder(TileBorderType.HURI_TEN, builder);
			} else {
			    appendTileLeftBorder(TileBorderType.TEN_PAI, builder);
			}
			
			for (Tile tile : tenTiles) {
				appendOneTile(tile, builder);
			}
			stringBuilder.append(builder.mergeAsLines());
		}
		stringBuilder.append("]\n");
		return stringBuilder.toString();
	}
	
	public static String tileRiverToCharImage(TileRiver tileRiver) {
		MultiLinesStringBuilder builder = new MultiLinesStringBuilder(2);
		
		if(tileRiver.getSize() > 0) {
			appendTileLeftBorder(builder);	
		}
		for (SimpleEntry<Tile, TileInRiverState> entry : tileRiver.getTileWithStates()) {
			appendOneTile(entry.getKey(), builder);
		}
		return builder.mergeAsLines();
	}
	
	public static String WinCaseToCharImage(WinCase winCase) {
		if (winCase == null) {
			return "";
		}
		List<NormalYaku> normalYakuList = winCase.getNormalYakuList();
		List<Yakuman> yakumanList = winCase.getYakumanList();
		int sumFu = winCase.getFu();
		int sumHun = winCase.getHan();
		Score score = winCase.getScore();
		
		StringBuilder lineBuilder = new StringBuilder();
		
		if (normalYakuList.size() > 0 || yakumanList.size() > 0) {
			for (NormalYaku yaku : normalYakuList) {
				lineBuilder.append(yaku.getJapanese()).append(" ").append(yaku.getHan()).append("翻，");
			}
			for (Yakuman yaku : yakumanList) {
				lineBuilder.append("役满 ").append(yaku.getJapanese()).append(",");
			}
			lineBuilder.setCharAt(lineBuilder.length() - 1, '\n');
			
			lineBuilder.append("合计 ").append(sumFu).append("符 ").append(sumHun).append("翻 ").append(score.getRon()).append("点\n");
		}
		return lineBuilder.toString();
	}


}

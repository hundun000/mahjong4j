package hundun.mahjong4j.extend.utls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

import org.mahjong4j.Player;
import org.mahjong4j.hands.Hands;
import org.mahjong4j.hands.Mentsu;
import org.mahjong4j.tile.Tile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import hundun.mahjong4j.extend.enhance.SuperHands;
import hundun.mahjong4j.extend.game.DiscardAdvice;
import hundun.mahjong4j.extend.game.GamePlayer;
import hundun.mahjong4j.extend.game.TenpaiCase;
import hundun.mahjong4j.extend.game.WinCase;
import hundun.mahjong4j.extend.game.board.MahjongBoard;
import hundun.mahjong4j.extend.game.board.TileRiver;
import hundun.mahjong4j.extend.game.board.TileRiver.TileInRiverState;
import hundun.mahjong4j.extend.game.event.GameEndEvent;

/**
 *
 * @author hundun
 * Created on 2019/03/09
 */
public class JsonFormatTool {
    
    static ObjectMapper objectMapper = new ObjectMapper();
	
	public static String tileRiverToString(TileRiver tileRiver) {
		List<String> tileStrings = new ArrayList<>();
		for (SimpleEntry<Tile, TileInRiverState> entry : tileRiver.getTileWithStates()) {
		    switch (entry.getValue()) {
            case NORMAL:
                tileStrings.add(entry.getKey().toString());
                break;
            case NORMAL_REACH:
                tileStrings.add("*" + entry.getKey().toString());
                break;
            case MEI:
                tileStrings.add("-" + entry.getKey().toString());
                break;
            case MEI_REACH:
                tileStrings.add("*-" + entry.getKey().toString());
                break;
            }
			
		}
		return tileStrings.toString();
	}
	
	
	
	
//	public static JsonNode handsToJson(SuperHands hands) {
//		JsonNode handsJson = new JsonNode(true);
//		String typeString;
//		int stepMSP = 9;
//		int stepKaze = 4;
//		int stepSangen = 3;
//		int from = 0;
//		int to = 0;
//		
//		int[] inputtedTilesWithoutLast = hands.getInputtedTiles().clone();
//		int[] tilesOfCurrentType;
////		if (hands.getLast() != null) {
////			inputtedTilesWithoutLast[hands.getLast().getCode()]--;
////		}
//		
//		
//		from = to;
//		to = to + stepMSP;
//		tilesOfCurrentType = Arrays.copyOfRange(inputtedTilesWithoutLast, from, to);
//		typeString = Tile.valueOf(from).getType().toString();
//		handsJson.put(typeString, Arrays.toString(tilesOfCurrentType));
//		
//		from = to;
//		to = to + stepMSP;
//		tilesOfCurrentType = Arrays.copyOfRange(inputtedTilesWithoutLast, from, to);
//		typeString = Tile.valueOf(from).getType().toString();
//		handsJson.put(typeString, Arrays.toString(tilesOfCurrentType));
//		
//		from = to;
//		to = to + stepMSP;
//		tilesOfCurrentType = Arrays.copyOfRange(inputtedTilesWithoutLast, from, to);
//		typeString = Tile.valueOf(from).getType().toString();
//		handsJson.put(typeString, Arrays.toString(tilesOfCurrentType));
//		
//		from = to;
//		to = to + stepKaze;
//		tilesOfCurrentType = Arrays.copyOfRange(inputtedTilesWithoutLast, from, to);
//		typeString = Tile.valueOf(from).getType().toString();
//		handsJson.put(typeString, Arrays.toString(tilesOfCurrentType));
//		
//		from = to;
//		to = to + stepSangen;
//		tilesOfCurrentType = Arrays.copyOfRange(inputtedTilesWithoutLast, from, to);
//		typeString = Tile.valueOf(from).getType().toString();
//		handsJson.put(typeString, Arrays.toString(tilesOfCurrentType));
//		
//		// 添加last
//		if (hands.getLast() != null) {
//			handsJson.put("last", hands.getLast().toString());
//		}
//		// 添加副露的牌
//		JsonNode inputtedMentsus = new JsonNode();
//		for (Mentsu mentsu : hands.getInputtedMentsuList()) {
//		    inputtedMentsus.add(mentsu.getTiles());
//		}
//		handsJson.put("inputted_mentsus", inputtedMentsus);
//		
//		
//		return handsJson;
//	}
//	
//	public static JsonNode mahjongBoardToJson(MahjongBoard board) {
//	    JsonNode boardJson = new JsonNode();
//	    boardJson.put("state_message", board.getStateMessage());
//    	if (board.getGameEndEvent() != null) {
//    	    boardJson.put("event", board.getGameEndEvent());
//        }
//    	JsonNode players = new JsonNode();
//    	for (int i = 0; i < MahjongBoard.NUM_PLAYERS; i++) {
//    		JsonNode playerObject = new JsonNode(true);
//    		GamePlayer player = board.getPlayer(i);
//    		
//    		
//    		if (player.getChiCandidates().size() > 0) {
//    			playerObject.put("chi_candicates", meiPaiCandidatesToJSON(player.getChiCandidates()));
//    		}
//    		
//    		if (player.getPonCandidates().size() > 0) {
//                playerObject.put("chi_candicates", meiPaiCandidatesToJSON(player.getPonCandidates()));
//            }
//    		
//    		JsonNode discardAdvices = discardAdvicesToJSON(player.getDiscardAdvices());
//    		if (discardAdvices.size() > 0) {
//                playerObject.put("discard_advices", discardAdvices);
//                playerObject.put("can_reach", player.isCanReach());
//            }
//    		
//    		JsonNode tenpaiCases = tenpaiCasesToJSON(player.getTenpaiCases());
//            if (tenpaiCases.size() > 0) {
//                playerObject.put("tenpai", tenpaiCases);
//                playerObject.put("huriten", player.isHuriten());
//            }
//    		
//    		JsonNode wincase = wincaseToJSON(player.getWinCase());
//            if (wincase != null) {
//                playerObject.put("wincase", wincase);
//            }
//            
//            playerObject.put("action_advices", player.getActionAdvices());
//    		
//            boolean isReach = player.getPersonalSituation().isReach();
//            if (isReach) {
//                playerObject.put("reach", true);
//            }
//    		playerObject.put("hands", handsToJson(player.getHands()));
//    		playerObject.put("river", tileRiverToString(player.getTileRiver()));
//    		players.add(playerObject);
//    	}
//    	boardJson.put("players", players);
//    	return boardJson;
//    }
//	
//	private static JsonNode tenpaiCasesToJSON(List<TenpaiCase> tenpaiCases) {
//	    JsonNode result = new JsonNode();
//        for (TenpaiCase tenpaiCase : tenpaiCases) {
//            JsonNode object = new JsonNode();
//            object.put("discard", tenpaiCase.getTile().toString());
//            // TODO
//            object.put("info", tenpaiCase.yakuInfoToString(false));
//            result.add(object);
//        }
//        return result;
//    }
//
//    private static JsonNode wincaseToJSON(WinCase winCase) {
//	    if (winCase == null) {
//	        return null;
//	    }
//	    
//	    JsonNode object = new JsonNode();
//	    // TODO
//	    object.put("info", CharImageTool.WinCaseToCharImage(winCase));
//        return object;
//    }
//
//
//
//
//    private static JsonNode discardAdvicesToJSON(List<DiscardAdvice> discardAdvices) {
//	    JsonNode result = new JsonNode();
//	    for (DiscardAdvice discardAdvice : discardAdvices) {
//            JsonNode object = new JsonNode();
//            object.put("furiten", discardAdvice.isFuriten());
//            object.put("discard", discardAdvice.getDiscardTile().toString());
//            object.put("tenpai", discardAdvice.getTenpaiList().toString());
//            result.add(object);
//        }
//        return result;
//    }
//
//
//
//
//    public static JsonNode meiPaiCandidatesToJSON(List<? extends Mentsu> meiPaiCandidates) {
//		JsonNode array = new JsonNode();
//        for (Mentsu mentsu : meiPaiCandidates) {
//            array.add(mentsu.getTiles());
//        }
//        return array;
//	}
	
}

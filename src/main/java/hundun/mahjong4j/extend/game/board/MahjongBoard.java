package hundun.mahjong4j.extend.game.board;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.mahjong4j.GeneralSituation;
import org.mahjong4j.PersonalSituation;
import org.mahjong4j.exceptions.IllegalMentsuSizeException;
import org.mahjong4j.exceptions.Mahjong4jException;
import org.mahjong4j.exceptions.MahjongTileOverFlowException;
import org.mahjong4j.hands.Hands;
import org.mahjong4j.tile.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hundun.mahjong4j.extend.enhance.SuperHands;
import hundun.mahjong4j.extend.exception.GameException;
import hundun.mahjong4j.extend.exception.PlayerStateTransitionExceprion;
import hundun.mahjong4j.extend.exception.UnsupportedException;
import hundun.mahjong4j.extend.game.GamePlayer;
import hundun.mahjong4j.extend.game.HandsFactory;
import hundun.mahjong4j.extend.game.board.TileRiver.TileInRiverState;
import hundun.mahjong4j.extend.game.event.GameEndEvent;
import hundun.mahjong4j.extend.game.event.RonEvent;
import hundun.mahjong4j.extend.game.event.TileWallEmptyEvent;
import hundun.mahjong4j.extend.utls.CharImageTool;


/**
 * 牌桌类。
 * 所有属于“牌桌”的组件，由牌桌类管理。如牌山。
 * @author hundun 2018年10月23日
 */
public class MahjongBoard {
	
	private static final Logger logger = LoggerFactory.getLogger(MahjongBoard.class);
	
	private GeneralSituation generalSituation;
	private GamePlayer[] players;
	private TileWall tileWall;
	private TileKing tileKing;
	public static final int NUM_PLAYERS = 4;
	public static final int NUM_TILE_FIRST_ROUND_DRAW_SPACE = 4;
    
    private int activePlayerIndex;
    /**
     * 当前玩家打出的，正在等待处理的牌
     */
    private Tile activeTile;
    
    private GameEndEvent gameEndEvent;
    
    static long seed = 123457;
    
    public MahjongBoard(List<Tile> tiles) throws Mahjong4jException, GameException {
    	checkTiles(tiles);
        gameStart(tiles);
    }
    
    private static void checkTiles(List<Tile> tiles) throws GameException {
        Map<Tile, Integer> map = new HashMap<>();
        for (Tile tile : tiles) {
            map.merge(tile, 1, (oldVal, newVal) -> oldVal + newVal);
        }
        for (Entry<Tile, Integer> entry : map.entrySet()) {
            if (entry.getValue().intValue() != 4) {
                throw new GameException("一副牌中的" + entry.getKey() + "的个数错误：" + entry.getValue());
            }
        }
    }
    
    private void gameStart(List<Tile> tiles) throws Mahjong4jException, GameException {
        // 開山
        tileWall = new TileWall(tiles.subList(0, TileWall.TILE_WALL_SIZE));
        tileKing = new TileKing(tiles.subList(TileWall.TILE_WALL_SIZE, tiles.size()));
        // 摸牌
        List<List<Tile>> playersDrawingTiles = new ArrayList<>();
        for (int i = 0; i < NUM_PLAYERS; i++) {
            playersDrawingTiles.add(new ArrayList<>());
        }
        // 摸3轮
        for (int i = 0; i < 3 ; i++) {
            // 四人轮流摸牌
            for (int GamePlayer = 0; GamePlayer < NUM_PLAYERS; GamePlayer++) {
                // 连续摸取4张
                for (int j = 0; j < NUM_TILE_FIRST_ROUND_DRAW_SPACE; j++) {
                    playersDrawingTiles.get(GamePlayer).add(tileWall.normalDraw());
                }
            }       
        }
        // 再轮流摸一张（采用庄家等第一巡再摸一张，而不是配牌最后摸两张）
        for (int GamePlayer = 0; GamePlayer < NUM_PLAYERS; GamePlayer++) {
            playersDrawingTiles.get(GamePlayer).add(tileWall.normalDraw());
        }
        // 开局1dora
        Tile[] doraPair = tileKing.newDoraAndUradora();
        this.generalSituation = new GeneralSituation(Tile.TON, doraPair[0], doraPair[1]);
        
        // ================= 实现相关的初始化 =========================
        // 设置player
        players = new GamePlayer[NUM_PLAYERS];
        Tile[] kazes = new Tile[] {Tile.TON, Tile.NAN, Tile.SHA, Tile.PEI};
        for (int i = 0; i < NUM_PLAYERS; i++) {
            SuperHands hands = HandsFactory.getHands(playersDrawingTiles.get(i));
            PersonalSituation personalSituation = new PersonalSituation(kazes[i]);
            players[i] = new GamePlayer(hands, generalSituation, personalSituation);
            
            players[i].getHands().clearLast();
            //System.out.println(JSON.toJSONString(players[i]));
        }
        
        activePlayerIndex = 0;
    }
    
    public static void main(String[] args) throws Mahjong4jException, GameException {
    	int seed = 0;
    	MahjongBoard board = MahjongBoardFactory.getMahjongBoardById(Integer.valueOf(seed));
		String scriptFileName = "script/全员掉线.txt";
		String result = board.scriptControl(scriptFileName);
		System.out.println(result);
	}
    
    public String scriptControl(String fileName) throws Mahjong4jException, GameException {
		StringBuilder builder = new StringBuilder();
		Path path = Paths.get(fileName);
		List<String> lines;
		try {
			lines = Files.readAllLines(path);
			
		} catch (IOException e) {
			e.printStackTrace();
			return "file error:" + e.getMessage();
		}
		for (String line : lines) {
			commandLineControl(line);
			builder.append("》》》").append(line).append("\n");
			builder.append(CharImageTool.mahjongBoardToCharImage(this));
			builder.append("\n");
		}
		return builder.toString();
	}
    
    public void commandLineControl(String line) throws Mahjong4jException, GameException {
		List<String> args = new ArrayList<String>(Arrays.asList(line.split(" "))) {
			private static final long serialVersionUID = -1850600430254085752L;
			@Override
			public String get(int index) {
				if (index > size() - 1) {
					return null;
				}
				return super.get(index);
			}

		};
		
		String action = args.get(0);
		String playerIndexText;
		String target;
    	
		
		
    	switch (action) {
		case "draw":
			playerNormalDraw();
			break;
		case "dis":
		case "discard":
		    target = args.get(1);
		    
			playerDiscardTile(Tile.valueOf(target));
			break;
		case "end":
		case "turnend":
			playerTurnEnd();
			break;
		case "chi":
		    playerIndexText = args.get(1);
		    target = args.get(2);
		    
			int playerIndex = Integer.valueOf(playerIndexText);
			int meiPaiCandidateIndex = Integer.valueOf(target);
			playerMeipai(playerIndex, meiPaiCandidateIndex, MeiPaiType.CHI);
			break;
		case "pon":
            playerIndexText = args.get(1);
            target = args.get(2);
            
            playerIndex = Integer.valueOf(playerIndexText);
            meiPaiCandidateIndex = Integer.valueOf(target);
            playerMeipai(playerIndex, meiPaiCandidateIndex, MeiPaiType.PON);
            break;
		case "timeout":
			playerTimeoutDiscardOneTile();
			break;
		default:
			break;
		}
		
	}
    
    
    /**
     * 当前玩家开始自己一巡
     * @throws GameException 
     * @throws IllegalMentsuSizeException 
     * @throws MahjongTileOverFlowException 
     */
    public void playerNormalDraw() throws Mahjong4jException, GameException {
    	Tile tile = tileWall.normalDraw();
    	logger.info("牌局上，玩家{}通常摸牌{}", activePlayerIndex, tile);
    	generalDraw(tile, true);
	}
    
    public void playerAnKanDraw() throws Mahjong4jException, GameException {
        Tile tile = tileKing.kanDraw();
        logger.info("牌局上，玩家{}暗杠摸牌{}", activePlayerIndex, tile);
        generalDraw(tile, false);
    }
    
    private void generalDraw(Tile tile, boolean isTurnStartDraw) throws Mahjong4jException, GameException {
        if (tile != null) {
            boolean allowReach = tileWall.size() > NUM_PLAYERS && getActivePlayer().getScore() > 1000;
            getActivePlayer().drawToLast(tile, allowReach, isTurnStartDraw);
        } else {
            for (GamePlayer player : players) {
                player.notifyTillWallEmpty();
            }
            this.gameEndEvent = new TileWallEmptyEvent();
        }
    }
    
    
    public void playerReachPass() throws Mahjong4jException, GameException {
        logger.info("牌局上，玩家{}立直通过", activePlayerIndex);
        getActivePlayer().reachPass();
    }
    
    
    
    /**
     * 当前玩家打出牌
     * @throws Mahjong4jException 
     * @throws GameException 
     */
    public void playerDiscardTile(Tile tile) throws Mahjong4jException, GameException {
        playerDiscardTile(tile, false);
    }
    public void playerDiscardTile(Tile tile, boolean wantReach) throws GameException, Mahjong4jException {
        logger.info("牌局上，玩家{}打出牌{}{}", activePlayerIndex, tile, wantReach?"并宣布立直":"");
    	
    	getActivePlayer().discardOneTile(tile, wantReach);
    	
    	for (GamePlayer player : players) {

    		if (player != getActivePlayer()) {
    		    boolean canChi = player.isNext(getActivePlayer());
    			player.notifyByOtherDiscard(tile, canChi);
    		}
    		
    	}
    	activeTile = tile;
	}
    
	public void playerTimeoutDiscardOneTile() throws Mahjong4jException, GameException {
		logger.info("牌局上，玩家{}超时", activePlayerIndex);
		// 超时摸切
		Tile tile = getPlayer(activePlayerIndex).getHands().getLast();
		playerDiscardTile(tile);
		playerTurnEnd();
	}
    
    
    
    /**
     * 自家回合结束
     * @throws Mahjong4jException 
     * @throws GameException 
     */
    public void playerTurnEnd() throws Mahjong4jException, GameException {
    	logger.info("牌局上，玩家{}回合结束", activePlayerIndex);
    	getActivePlayer().turnEnd();
    	for (GamePlayer gamePlayer : players) {
    		// renew view
    		if (gamePlayer != getActivePlayer()) {
    			gamePlayer.getChiCandidates().clear();
    			gamePlayer.getPonCandidates().clear();
    		}
    	}	
    	
    	intoNextPlayerOrNextRound();
	}
    
    private void intoNextPlayerOrNextRound() throws Mahjong4jException, GameException {
    	activePlayerIndex++;
    	if (activePlayerIndex == NUM_PLAYERS) {
    		activePlayerIndex = 0;
    		generalSituation.setFirstRound(false);
    	}
    	logger.info("牌局上，当前activePlayerIndex更新为：{}", activePlayerIndex);
    	// 自动进入下家
    	playerNormalDraw();
	}
    
    public enum MeiPaiType {
        /**
         * 吃
         */
        CHI,
        /**
         * 碰 
         */
        PON,
        /**
         * 明杠
         */
        MIN_KAN,
        /**
         * 暗杠
         */
        AN_KAN,
        /**
         * 加杠
         */
        KA_KAN
        ;
    }
    
    public void playerAnKan(int anKanPlayerIndex, int anKanCandidateIndex) throws Mahjong4jException, GameException {
        logger.info("牌局上，玩家{}：meiPaiPlayerIndex={}，meiPaiCandidateIndex={}", MeiPaiType.AN_KAN, anKanPlayerIndex, anKanCandidateIndex);
        GamePlayer anKanPlayer = getPlayer(anKanPlayerIndex);
        anKanPlayer.anKan(anKanCandidateIndex);
        
        Tile[] doraAndUra = tileKing.newDoraAndUradora();
        generalSituation.getDora().add(doraAndUra[0]);
        generalSituation.getUradora().add(doraAndUra[1]);
    }
    

    /**
     * meiPaiPlayerIndex对应的玩家，打算鸣出meiPaiCandidateIndex对应的面子
     * @param meiPaiPlayerIndex
     * @param meiPaiCandidateIndex
     * @throws GameException 
     * @throws Exception
     */
    public void playerMeipai(int meiPaiPlayerIndex, int meiPaiCandidateIndex, MeiPaiType type) throws Mahjong4jException, GameException {
    	logger.info("牌局上，玩家{}：meiPaiPlayerIndex={}，meiPaiCandidateIndex={}", type, meiPaiPlayerIndex, meiPaiCandidateIndex);
    	GamePlayer meipaiPlayer = getPlayer(meiPaiPlayerIndex);
    	switch (type) {
            case CHI:
                meipaiPlayer.chi(meiPaiCandidateIndex, activeTile);
                break;
            case PON:
                meipaiPlayer.pon(meiPaiCandidateIndex, activeTile);
                break;
            default:
                throw new UnsupportedException();
        }
    	
    	generalSituation.setFirstRound(false);
        getPlayer(activePlayerIndex).getTileRiver().lastTileMeiPai();
    	
    	getPlayer(activePlayerIndex).turnEnd();
    	activePlayerIndex = meiPaiPlayerIndex;
    	logger.info("牌局上，玩家鸣牌后，当前activePlayerIndex更新为：{}", activePlayerIndex);
	}

    
    public void playerTsumo() throws Mahjong4jException, GameException {
    	logger.info("牌局上，玩家宣布自摸：playerIndex={}", activePlayerIndex);
    	GamePlayer gamePlayer = getPlayer(activePlayerIndex);
    	
    	this.gameEndEvent = gamePlayer.doTsumo();
	}
    
    public void playerRon(int playerIndex) throws Mahjong4jException, PlayerStateTransitionExceprion {
    	logger.info("牌局上，玩家宣布铳和：playerIndex={}", playerIndex);
    	GamePlayer ronPlayer = getPlayer(playerIndex);
	    RonEvent ronEvent = ronPlayer.doRon(activeTile);
	    ronEvent.setWinPlayerIndex(playerIndex);
	    ronEvent.setLosePlayerIndex(activePlayerIndex);
	    this.gameEndEvent = ronEvent;
	}
    
    public String getStateMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Current in player[").append(activePlayerIndex).append("]. ");
        stringBuilder.append("State:").append(getActivePlayer().getState().name());
        return stringBuilder.toString();
    }
    
    // ======= quick getter =====
    public GamePlayer getPlayer(int index) {
		return players[index];
	}
    
    public GamePlayer getActivePlayer() {
        return players[activePlayerIndex];
    }
    
    // ======= getter & setter
    public GameEndEvent getGameEndEvent() {
        return gameEndEvent;
    }
    
    public GeneralSituation getGeneralSituation() {
        return generalSituation;
    }
    
    public TileWall getTileWall() {
        return tileWall;
    }
    
//    public GamePlayer[] getPlayers() {
//		return players;
//	}
//    
//    public TileRiver[] getTileRivers() {
//		return tileRivers;
//	}
    

	
}

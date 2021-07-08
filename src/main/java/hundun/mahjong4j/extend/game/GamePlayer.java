package hundun.mahjong4j.extend.game;
/**
 * @author hundun
 * Created on 2019/08/06
 */

import static org.mahjong4j.Score.SCORE0;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mahjong4j.GeneralSituation;

import org.mahjong4j.PersonalSituation;
import org.mahjong4j.Player;
import org.mahjong4j.exceptions.HandsNegativeException;
import org.mahjong4j.exceptions.HandsOverFlowException;
import org.mahjong4j.exceptions.IllegalMentsuSizeException;
import org.mahjong4j.exceptions.Mahjong4jException;
import org.mahjong4j.exceptions.MahjongTileOverFlowException;
import org.mahjong4j.hands.Kantsu;
import org.mahjong4j.hands.Kotsu;
import org.mahjong4j.hands.Mentsu;
import org.mahjong4j.hands.Shuntsu;
import org.mahjong4j.tile.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hundun.mahjong4j.extend.enhance.SuperHands;
import hundun.mahjong4j.extend.enhance.SuperPlayer;
import hundun.mahjong4j.extend.exception.GameException;
import hundun.mahjong4j.extend.exception.PlayerStateTransitionExceprion;
import hundun.mahjong4j.extend.game.board.TileRiver;
import hundun.mahjong4j.extend.game.board.TileRiver.TileInRiverState;
import hundun.mahjong4j.extend.game.event.GameEndEvent;
import hundun.mahjong4j.extend.game.event.RonEvent;
import hundun.mahjong4j.extend.game.event.TsumoEvent;

/**
 * 包括所有属于玩家的东西，包括自己的牌河等。偏向于Board-Player-Relation。
 */
public class GamePlayer {
	
	private static final Logger logger = LoggerFactory.getLogger(Player.class);
	
	private final SuperPlayer player;
	
	private PlayerState state = PlayerState.SLEEP;
	
	private TileRiver tileRiver = new TileRiver();
	/**
	 * 当前可以碰形成的面子
	 */
	private List<Kotsu> ponCandidates = new ArrayList<>();
	
	/**
     * 当前可以吃形成的面子
     */
    private List<Shuntsu> chiCandidates = new ArrayList<>();

    /**
     * 当前可以暗杠形成的面子
     */
    private List<Kantsu> anKanCandidates = new ArrayList<>();
    /**
     * 当前可以明杠形成的面子
     */
    private List<Kantsu> minKanCandidates = new ArrayList<>();
	private List<DiscardAdvice> discardAdvices = new ArrayList<>();

	private List<TenpaiCase> tenpaiCases = new ArrayList<>();
	private boolean canReach;
	
	/**
	 * 振听
	 */
	private boolean huriten;
	
	private WinCase winCase;
	
	private int score;
	
	private Set<PlayerActionAdvice> actionAdvices = new HashSet<>();
	
	public GamePlayer(SuperPlayer player) {
	    this.player = player;
		this.score = 25000;
	}

	public GamePlayer(SuperHands hands, GeneralSituation generalSituation, PersonalSituation personalSituation) throws Mahjong4jException {
		this(new SuperPlayer(hands, generalSituation, personalSituation));
	}
	
	
	
	
	
	// ========= player action ===================
	public void turnEnd() throws Mahjong4jException, GameException {
		//logger.info("玩家回合结束");
		player.getHands().clearLast();
		state = state.next(PlayerAction.NO_REACT);
		
		canReach = false;
	}
	
	
	public void drawToLast(Tile tile, boolean allowReach, boolean isTurnStartDraw) throws Mahjong4jException, GameException {
		//logger.info("玩家摸上牌:{}", tile.toString());
		player.getPersonalSituation().setTsumo(true);
		player.getHands().drawToLastThenFindMentsu(tile);
		this.winCase = calWinCase(player);
		
		state = state.next(isTurnStartDraw ? PlayerAction.TURN_START_DRAW : PlayerAction.AN_KAN_DRAW);
		
		this.genDiscardAdvices();
		this.genCanReach(allowReach);
		this.genActionAvices();
		this.genAnKanCandidates();
	}
	

	public void discardOneTile(Tile tile, boolean wantReach) throws Mahjong4jException, GameException {
		
	    
        SuperHands tryDiscardHands = player.getHands().deepClone();
        boolean canDicard = tryDiscardHands.discardOneTile(tile);
        if (!canDicard) {
            throw new HandsNegativeException(tile.toString());
        }
        if (wantReach) { 
            if (tryDiscardHands.getTenpaiList().size() == 0) {
                throw new GameException("打出此牌后不能立直");
            }
        }
	    
	    //logger.info("玩家打出牌:{}", tile.toString());
		player.getHands().discardOneTile(tile);
		tileRiver.add(tile, wantReach ? TileInRiverState.NORMAL_REACH : TileInRiverState.NORMAL);
		// clean
		player.getPersonalSituation().setTsumo(false);
		winCase = null;
		getDiscardAdvices().clear();
		
		
		if (wantReach) { 
		    state = state.next(PlayerAction.DISCARD_AND_REACH_DECLARATION);
		} else {
		    genTenpaiCases();
		    state = state.next(PlayerAction.DISCARD);
		}
		
		
		
	}
	
	/**
     * 立直宣告得到全员通过
     */
	public void reachPass() throws PlayerStateTransitionExceprion, Mahjong4jException {
	    
	    player.getPersonalSituation().setReach(true);
       
        genTenpaiCases();
	    
	    state = state.next(PlayerAction.REACH_PASS);
    }
	
	/**
     * 执行暗杠
     */
    public void anKan(int anKanCandidateIndex) throws Mahjong4jException, GameException {
        Mentsu mentsu = anKanCandidates.get(anKanCandidateIndex);
        
        player.getHands().genInputtedMentsu(mentsu, null);
        state = state.next(PlayerAction.AN_KAN);
    }
	/**
	 * 执行吃牌
	 */
	public void chi(int chiCandidateIndex, Tile activeTile) throws Mahjong4jException, GameException {
        Mentsu mentsu = chiCandidates.get(chiCandidateIndex);
        meiPai(mentsu, activeTile);
	}
	/**
     * 执行碰牌
     */
	public void pon(int ponCandidateIndex, Tile activeTile) throws Mahjong4jException, GameException {
        Mentsu mentsu = ponCandidates.get(ponCandidateIndex);
        meiPai(mentsu, activeTile);
    }
	/**
	 * 鸣牌
	 * @throws Mahjong4jException 
	 * @throws GameException 
	 */
	private void meiPai(Mentsu meiMentsu, Tile activeTile) throws Mahjong4jException, GameException {
		//Mentsu meiMentsu = meiPaiCandidates.get(meiPaiCandidateIndex);
		player.getHands().genInputtedMentsu(meiMentsu, activeTile);
		state = state.next(PlayerAction.CHI_OR_PON);
		
		ponCandidates.clear();
		chiCandidates.clear();
	}

	
	/**
	 * 计算是否属于不利听
	 * @param tenpaiList
	 * @param tileRiver
	 * @return
	 */
	public static boolean calHuritenByRiver(List<Tile> tenpaiList, TileRiver tileRiver) {
		for (Tile tenpai : tenpaiList) {
			if (tileRiver.contains(tenpai)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 被通知牌山已摸完，结束一局
	 * @throws GameException
	 */
	public void notifyTillWallEmpty() throws GameException {
	    state = state.next(PlayerAction.CAN_NOT_DRAW);
    }
	
	

	public GameEndEvent doTsumo() throws Mahjong4jException, GameException {
	    if (!canTsumo()) {
	        throw new Mahjong4jException("不能自摸");
	    }
	    
		player.getPersonalSituation().setTsumo(true);
		player.calculate();
		WinCase winCase = calWinCase(player);
		state = state.next(PlayerAction.TSUMO);
		return new TsumoEvent(winCase);
	}

	/**
	 * 执行荣胡
	 * @param ronTile 
	 * @return
	 * @throws Mahjong4jException
	 * @throws PlayerStateTransitionExceprion 
	 */
	public RonEvent doRon(Tile ronTile) throws Mahjong4jException, PlayerStateTransitionExceprion {
	    if (canRon()) {
            throw new Mahjong4jException("不能荣胡");
        }
	    
		RonEvent ronEvent = new RonEvent(getWinCase());
		state = state.next(PlayerAction.RON);
		return ronEvent;
	}
	
	// ========== checker ===========
	private boolean canRon() {
        return getWinCase() != null;
    }
	
	private boolean canTsumo() {
        return player.getHands().getCanWin();
    }
	
	public boolean isNext(GamePlayer other) {
        return player.isNext(other.player);
    }
	
	// ========== genner ==================
	/**
     * player以外的约束立直条件通过allowReach传入
     * @param allowReach
     */
	private void genCanReach(boolean allowReach) {
	    boolean allMentsuNotOpen = true;
	    for (Mentsu mentsu : player.getHands().getInputtedMentsuList()) {
	        if (mentsu.isOpen()) {
	            allMentsuNotOpen = false;
	            break;
	        }
	    }
	    
        canReach = discardAdvices.size() > 0 
                && allMentsuNotOpen 
                && allowReach;
    }
    

    private static WinCase calWinCase(SuperPlayer player) {
        player.calculate();
        if (player.getNormalYakuList().size() > 0 || player.getYakumanList().size() > 0) {
            return new WinCase(player.getHan(), player.getFu(), player.getScore(), player.getYakumanList(), player.getNormalYakuList());
        } else {
            return null;
        }
    }
    
    private void genMeiPaiCandidates(Tile activeTile, boolean canChi) {
        if (canChi) {
            genChiCandidates(activeTile, getHands());
        }
        genPonCandidates(activeTile, getHands());
        genMinKanCandidates(activeTile, getHands());
    }
    
    private void genActionAvices() {
        actionAdvices.clear();
        
        if (chiCandidates.size() > 0) {
            actionAdvices.add(PlayerActionAdvice.CHI);
        }
        
        if (ponCandidates.size() > 0) {
            actionAdvices.add(PlayerActionAdvice.PON);
        }
        
        if (anKanCandidates.size() > 0) {
            actionAdvices.add(PlayerActionAdvice.AN_KAN);
        }
        
        if (minKanCandidates.size() > 0) {
            actionAdvices.add(PlayerActionAdvice.MIN_KAN);
        }
        
        if (canReach) {
            actionAdvices.add(PlayerActionAdvice.REACH);
        }
        
        if (canTsumo()) {
            actionAdvices.add(PlayerActionAdvice.TSUMO);
        }
        
        if (canRon()) {
            actionAdvices.add(PlayerActionAdvice.RON);
        }
    }
    
    private void genAnKanCandidates() throws Mahjong4jException {
        anKanCandidates.clear();
        for (Tile tile : Tile.values()) {
            if (getHands().existInInputtedTile(tile, 4)) {
                Kantsu kanCandidate = new Kantsu(false, tile);
                anKanCandidates.add(kanCandidate);
            }
        }
    }
    
    
    
    private void genDiscardAdvices() throws Mahjong4jException {
        discardAdvices.clear();
        SuperHands testHands;
        for (Tile testDiscardTile : Tile.values()) {
            

            testHands = player.getHands().deepClone();
            boolean canDiscard = testHands.discardOneTile(testDiscardTile);
            if (!canDiscard) {
                continue;
            }

            
            List<Tile> tenpaiList = testHands.getTenpaiList();
            if (tenpaiList.size() > 0) {
                boolean testFuriten = calHuritenByRiver(tenpaiList, tileRiver) || tenpaiList.contains(testDiscardTile);
                DiscardAdvice discardAdvice = new DiscardAdvice(testDiscardTile, tenpaiList, testFuriten);
                discardAdvices.add(discardAdvice);
            }
        }
    }
    
    /**
     * 响应其他玩家打出牌：<br>
     * - 用otherDiscard尝试生成荣胡winCase <br>
     * - 计算鸣牌 <br>
     * @param otherDiscard
     * @param canChi 
     * @throws Mahjong4jException
     */
    public void notifyByOtherDiscard(Tile otherDiscard, boolean canChi) throws Mahjong4jException {
        if (tenpaiCases.size() == 0 || huriten) {
            return;
        }
        
        SuperPlayer testPlayer = player.deepClone();
        testPlayer.getPersonalSituation().setTsumo(false);
        testPlayer.getHands().drawToLastThenFindMentsu(otherDiscard);
        this.winCase = calWinCase(testPlayer);
        
        this.genMeiPaiCandidates(otherDiscard, canChi);
        this.genActionAvices();
    }
    
	private void genTenpaiCases() throws Mahjong4jException {
		tenpaiCases.clear();

		
		List<Tile> tenpaiList = player.getHands().getTenpaiList();
		for (Tile tenpai : tenpaiList) {
		    SuperPlayer testPlayer;
			// 遍历时可能尝试打出没有的牌
			testPlayer = player.deepClone();
			testPlayer.getHands().drawToLastThenFindMentsu(tenpai);
			testPlayer.calculate();

			if (testPlayer.getScore() != SCORE0) {
				TenpaiCase tenpaiCase = new TenpaiCase(tenpai, testPlayer);
				tenpaiCases.add(tenpaiCase);
			}
		}
		
		huriten = calHuritenByRiver(tenpaiList, tileRiver);
	}

    
    private void genChiCandidates(Tile activeTile, SuperHands otherHands) {
        
        
        Tile tile1;
        Tile tile2;
        Tile tile3;
        boolean otherHandsContains;
        
        if (activeTile.getNumber() >= 1 && activeTile.getNumber() <= 7) {
            tile1 = activeTile;
            tile2 = tile1.getNextTile();
            tile3 = tile2.getNextTile();
            otherHandsContains = otherHands.existInInputtedTile(tile2) & otherHands.existInInputtedTile(tile3);
            if (otherHandsContains) {
                Shuntsu chiCandidate = new Shuntsu(true, tile1, tile2, tile3);
                chiCandidates.add(chiCandidate);
            }
        }
        
        
        if (activeTile.getNumber() >= 2 && activeTile.getNumber() <= 8) {
            tile2 = activeTile;
            tile1 = tile2.getLastTile();
            tile3 = tile2.getNextTile();
            otherHandsContains = otherHands.existInInputtedTile(tile1) & otherHands.existInInputtedTile(tile3);
            if (otherHandsContains) {
                Shuntsu chiCandidate = new Shuntsu(true, tile1, tile2, tile3);
                chiCandidates.add(chiCandidate);
            }
        }
        
        if (activeTile.getNumber() >= 3 && activeTile.getNumber() <= 7) {
            tile3 = activeTile;
            tile2 = tile3.getLastTile();
            tile1 = tile2.getLastTile();
            otherHandsContains = otherHands.existInInputtedTile(tile2) & otherHands.existInInputtedTile(tile1);
            if (otherHandsContains) {
                Shuntsu chiCandidate = new Shuntsu(true, tile1, tile2, tile3);
                chiCandidates.add(chiCandidate);
            }
        }
    }

    private void genPonCandidates(Tile activeTile, SuperHands otherHands) {

        boolean handsContains;
        
        handsContains = otherHands.existInInputtedTile(activeTile, 2);
        if (handsContains) {
            Kotsu ponCandidate = new Kotsu(true, activeTile);
            ponCandidates.add(ponCandidate);
        }
        
    }
    
    private void genMinKanCandidates(Tile activeTile, SuperHands hands) {

        boolean handsContains;
        
        handsContains = hands.existInInputtedTile(activeTile, 3);
        if (handsContains) {
            Kantsu kanCandidate = new Kantsu(true, activeTile);
            minKanCandidates.add(kanCandidate);
        }
        
    }
	
	
	// ========== getter & setter ==================
	
	public WinCase getWinCase() {
		return winCase;
	}
	
	public Set<PlayerActionAdvice> getActionAdvices() {
        return actionAdvices;
    }
	
	public boolean isHuriten() {
		return huriten;
	}

	public List<TenpaiCase> getTenpaiCases() {
		return tenpaiCases;
	}


	
	public List<Kotsu> getPonCandidates() {
        return ponCandidates;
    }

    public List<Shuntsu> getChiCandidates() {
        return chiCandidates;
    }

    public TileRiver getTileRiver() {
		return tileRiver;
	}
	
	public List<DiscardAdvice> getDiscardAdvices() {
		return discardAdvices;
	}

	public SuperHands getHands() {
		return player.getHands();
	}

    public boolean isCanReach() {
        return canReach;
    }
    
    
    public PersonalSituation getPersonalSituation() {
        return player.getPersonalSituation();
    }

    public int getScore() {
        return score;
    }
    
    public PlayerState getState() {
        return state;
    }

    public List<Kantsu> getAnKanCandidates() {
        return anKanCandidates;
    }
    
    public List<Kantsu> getMinKanCandidates() {
        return minKanCandidates;
    }
    
}

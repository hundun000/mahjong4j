package hundun.mahjong4j.extend.game;
/**
 * @author hundun
 * Created on 2019/08/06
 */
public enum PlayerAction {
	/**
	 * （当前玩家）回合开始时摸牌成功
	 */
	TURN_START_DRAW,
	
	/**
     * （当前玩家）暗杠后摸牌成功
     */
    AN_KAN_DRAW,
	
	/**
     * （当前玩家）回合开始时发现没牌可摸
     */
    CAN_NOT_DRAW,
	
	/**
	 * （当前玩家）打出牌，不立直
	 */
	DISCARD,
	
	/**
     * （当前玩家）暗杠
     */
    AN_KAN,
	
	/**
	 * （当前玩家）超时未打出牌
	 */
	DISCARD_TIMEOUT,
	
	/**
     * （非当前玩家）（对当前玩家的弃牌）不鸣牌、不荣胡
     */
	NO_REACT,

	/**
	 * （非当前玩家）（对当前玩家的弃牌）鸣牌
	 */
	CHI_OR_PON,
	
	/**
     * （非当前玩家）（对当前玩家的弃牌） 荣胡
     */
    RON,
    
	/**
	 * （当前玩家）打出牌，宣布立直
	 */
	DISCARD_AND_REACH_DECLARATION,
	/**
	 * （非当前玩家）不荣胡打断立直
	 */
	REACH_PASS, 
	
	/**
	 * （当前玩家）宣布自摸
	 */
	TSUMO
	;
}

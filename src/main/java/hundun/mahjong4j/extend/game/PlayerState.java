package hundun.mahjong4j.extend.game;

import hundun.mahjong4j.extend.exception.PlayerStateTransitionExceprion;

/**
 * @author hundun
 * Created on 2019/08/06
 */
public enum PlayerState {
	/**
	 * 当前非自己回合
	 */
	SLEEP,
	/**
	 * 当前自己回合，需要打出牌， 思考中
	 */
	WAIT_DISCARD,
	
	/**
     * 当前自己回合，暗杠或加杠后，摸牌前，临时态
     */
    WAIT_KAN_DRAW,
	
	/**
	 * 当前自己回合，已打出牌，没宣布立直，等待他人是否鸣牌/荣胡
	 */
	WAIT_OTHER_REACT_DISCARD,
	
	/**
	 * 当前自己回合，已打出牌，宣布立直，等待他人是否鸣牌/荣胡
	 */
	WAIT_OTHER_REACT_REACH,
	
	/**
	 * 一局结束
	 */
	END
	;
    
    private PlayerState() {
        
    }

    
	public PlayerState next(PlayerAction action) throws PlayerStateTransitionExceprion {
		switch (this) {
		case SLEEP:
			if (action == PlayerAction.TURN_START_DRAW || action == PlayerAction.CHI_OR_PON) {
				return WAIT_DISCARD;
			}
			if (action == PlayerAction.CAN_NOT_DRAW) {
                return END;
            }
			break;
		case WAIT_DISCARD:
			if (action == PlayerAction.DISCARD || action == PlayerAction.DISCARD_TIMEOUT) {
				return WAIT_OTHER_REACT_DISCARD;
			}
			if (action == PlayerAction.DISCARD_AND_REACH_DECLARATION) {
			    return WAIT_OTHER_REACT_REACH;
			}
			if (action == PlayerAction.AN_KAN) {
                return WAIT_KAN_DRAW;
            }
			if (action == PlayerAction.TSUMO) {
                return END;
            }
			break;
		case WAIT_KAN_DRAW:
		    if (action == PlayerAction.AN_KAN_DRAW) {
                return WAIT_DISCARD;
            }
		    break;
		case WAIT_OTHER_REACT_DISCARD:
			if (action == PlayerAction.CHI_OR_PON || action == PlayerAction.NO_REACT) {
				return SLEEP;
			}
			if (action == PlayerAction.RON) {
                return END;
            }
			break;
		case WAIT_OTHER_REACT_REACH:
		    if (action == PlayerAction.REACH_PASS) {
		        return WAIT_OTHER_REACT_DISCARD;
		    }
		    if (action == PlayerAction.CHI_OR_PON || action == PlayerAction.NO_REACT || action == PlayerAction.CHI_OR_PON) {
                return SLEEP;
            }
		    if (action == PlayerAction.RON) {
                return END;
            }
            break;
		default:	
		}
		throw new PlayerStateTransitionExceprion(this, action);
	}


}

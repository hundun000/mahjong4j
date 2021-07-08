package hundun.mahjong4j.extend.game.event;
/**
 * @author hundun
 * Created on 2019/09/03
 */

import hundun.mahjong4j.extend.game.WinCase;

public class RonEvent extends GameEndEvent{
    
    
    WinCase winCase;
    int winPlayerIndex;
    int losePlayerIndex;

    public RonEvent(WinCase winCase) {
        super(EndReasonType.RON);
        this.winCase = winCase;
    }
    
    public WinCase getWinCase() {
        return winCase;
    }
    
    public EndReasonType getType() {
        return type;
    }

    public int getWinPlayerIndex() {
        return winPlayerIndex;
    }

    public void setWinPlayerIndex(int winPlayerIndex) {
        this.winPlayerIndex = winPlayerIndex;
    }

    public int getLosePlayerIndex() {
        return losePlayerIndex;
    }

    public void setLosePlayerIndex(int losePlayerIndex) {
        this.losePlayerIndex = losePlayerIndex;
    }
    
    

}

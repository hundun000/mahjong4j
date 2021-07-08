package hundun.mahjong4j.extend.game.event;

import hundun.mahjong4j.extend.game.WinCase;

/**
 * @author hundun
 * Created on 2019/09/03
 */
public class TsumoEvent extends GameEndEvent{
    final WinCase winCase;

    public TsumoEvent(WinCase winCase) {
        super(EndReasonType.TSUMO);
        this.winCase = winCase;
    }
    
    public WinCase getWinCase() {
        return winCase;
    }
    
    
}

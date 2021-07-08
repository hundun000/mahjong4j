package hundun.mahjong4j.extend.game.event;
/**
 * @author hundun
 * Created on 2019/09/03
 */
public abstract class GameEndEvent {
    final EndReasonType type;
    
    public GameEndEvent(EndReasonType type) {
        this.type = type;
    }
    
    public EndReasonType getType() {
        return type;
    }
}

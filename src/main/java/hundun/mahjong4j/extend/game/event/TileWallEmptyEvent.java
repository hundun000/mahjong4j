package hundun.mahjong4j.extend.game.event;
/**
 * @author hundun
 * Created on 2020/05/28
 */
public class TileWallEmptyEvent extends GameEndEvent{

    public TileWallEmptyEvent() {
        super(EndReasonType.TILE_WALL_EMPTY);
    }

}

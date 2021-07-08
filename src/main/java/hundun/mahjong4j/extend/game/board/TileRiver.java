package hundun.mahjong4j.extend.game.board;
/**
 * 一个玩家的牌河
 * @author hundun
 * Created on 2019/03/05
 */

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import org.mahjong4j.tile.Tile;

import hundun.mahjong4j.extend.exception.GameException;

public class TileRiver {
	
	List<SimpleEntry<Tile, TileInRiverState>> tileWithStates;
	
	
	public TileRiver() {
		tileWithStates = new ArrayList<>(36);
	}
	
	public void add(Tile tile, TileInRiverState state) {
		tileWithStates.add(new SimpleEntry<Tile, TileRiver.TileInRiverState>(tile, state));
	}
	
	public int getSize() {
		return tileWithStates.size();
	}
	
	
	public List<SimpleEntry<Tile, TileInRiverState>> getTileWithStates() {
		return tileWithStates;
	}
	
	public boolean contains(Tile tile) {
		for (SimpleEntry<Tile, TileInRiverState> entry : tileWithStates) {
			if (entry.getKey() == tile) {
				return true;
			}
		}
		return false;
	}
	
	public enum TileInRiverState{
		NORMAL,
		/**
		 * 被鸣牌
		 */
		MEI,
		/**
		 * 正常的立直牌
		 */
		NORMAL_REACH,
		/**
		 * 被鸣牌的立直牌
		 */
		MEI_REACH
	}

    /**
     * 将牌河中最后一张牌标记为被鸣走
     * @throws GameException
     */
    public void lastTileMeiPai() throws GameException {
        if (tileWithStates.size() == 0) {
            throw new GameException("空的不能lastTileMeiPai");
        }
        SimpleEntry<Tile, TileInRiverState> last = tileWithStates.get(tileWithStates.size() - 1);
        switch (last.getValue()) {
        case NORMAL:
            last.setValue(TileInRiverState.MEI);
            break;
        case NORMAL_REACH:
            last.setValue(TileInRiverState.MEI_REACH);
            break;
        case MEI:
        case MEI_REACH:
            throw new GameException(last.getValue() + "时不能lastTileMeiPai");
        }
    }
	
	
	
	

}

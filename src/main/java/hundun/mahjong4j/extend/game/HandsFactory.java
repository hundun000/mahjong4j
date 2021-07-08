package hundun.mahjong4j.extend.game;

import java.util.List;

import org.mahjong4j.exceptions.HandsOverFlowException;
import org.mahjong4j.exceptions.IllegalMentsuSizeException;
import org.mahjong4j.exceptions.MahjongTileOverFlowException;
import org.mahjong4j.hands.Hands;
import org.mahjong4j.tile.Tile;

import hundun.mahjong4j.extend.enhance.SuperHands;

public class HandsFactory {
	
	public static SuperHands getHands(List<Tile> tiles) throws HandsOverFlowException, MahjongTileOverFlowException, IllegalMentsuSizeException {
    	int size = tiles.size();
		int[] allTiles = new int[Tile.values().length];
    	for (int i = 0; i < size; i++) {
    		allTiles[tiles.get(i).getCode()]++;
		}
    	Tile last = tiles.get(size - 1);
    	return new SuperHands(allTiles, last);
	}

}

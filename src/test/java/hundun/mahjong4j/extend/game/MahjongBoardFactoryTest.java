package hundun.mahjong4j.extend.game;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.mahjong4j.tile.Tile;

import hundun.mahjong4j.extend.game.board.MahjongBoardFactory;

/**
 *
 * @author hundun
 * Created on 2019/04/04
 */
public class MahjongBoardFactoryTest {

	@Test
	public void test() {
		int[] tilesNum = {
	            0, 2, 2, 2, 2, 2, 2, 1, 0,
	            0, 0, 0, 0, 0, 0, 0, 0, 0,
	            0, 0, 0, 0, 0, 0, 0, 0, 0,
	            0, 0, 0, 0,
	            0, 0, 0
	        };
		List<Tile> tiles = MahjongBoardFactory.getTargetHandsTiles(tilesNum, Tile.M8, 0);
		System.out.println(tiles);
	}

}

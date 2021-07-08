package hundun.mahjong4j.extend.game.board;


import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.mahjong4j.exceptions.Mahjong4jException;
import org.mahjong4j.tile.Tile;



/**
 * 壁牌
 * @author hundun 2018年10月18日
 */
public class TileWall {
	public static final int TILE_WALL_SIZE = 122;
	// 約束其只能被從前端摸取
	private Stack<Tile> tiles = new Stack<>();
	// 每次開杠少摸一張
	private int numDrawLess = 0;
	
	
	public TileWall(List<Tile> tiles) throws Mahjong4jException {
		if (tiles.size() != TILE_WALL_SIZE) {
			throw new Mahjong4jException("壁牌構造時大小錯誤");
		}
		// 使出栈顺序与list顺序一致
		Collections.reverse(tiles);
		this.tiles.addAll(tiles);
	}
	
	public boolean isNotEmpty() {
		return tiles.size() > numDrawLess;
	}
	
	public int size() {
        return tiles.size();
    }
	
	public Tile normalDraw() {
		if(isNotEmpty()) {
			return tiles.pop();
		} else {
			return null;
		}
	}
	
	public void kangHappen() {
		numDrawLess++;
	}

}

package hundun.mahjong4j.extend.game.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.mahjong4j.exceptions.Mahjong4jException;
import org.mahjong4j.hands.Kantsu;
import org.mahjong4j.tile.Tile;

/**
 * 王牌。
 * @author hundun 2018年10月19日
 */
public class TileKing {
	public static final int TILE_KING_SIZE = 14;
	// 嶺上牌
	private Stack<Tile> tileHill = new Stack<>();
	private Stack<Tile> tileDoras = new Stack<>();
	public static final int TILE_HILL_SIZE = 4;
	
	
	public TileKing(List<Tile> tiles) throws Mahjong4jException {
		if (tiles.size() != TILE_KING_SIZE) {
			throw new Mahjong4jException("王牌構造時大小錯誤，预期" + TILE_KING_SIZE + ",实际" + tiles.size());
		}
		// 使出栈顺序与list顺序一致
		Collections.reverse(tiles);
		this.tileDoras.addAll(tiles);
		
		for (int i = 0; i < TILE_HILL_SIZE; i++) {
			this.tileHill.push(this.tileDoras.pop());
		}
	}
	
	public boolean isKangFull() {
		return tileHill.isEmpty();
	}
	
	public Tile kanDraw() {
		return tileHill.pop();
	}
	
	public Tile[] newDoraAndUradora() {
		Tile[] doraPair = new Tile[2];
		doraPair[0] = tileDoras.pop();
		doraPair[1] = tileDoras.pop();
		return doraPair;
	}
	
		
		
}

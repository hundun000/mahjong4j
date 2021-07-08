package hundun.mahjong4j.extend.game;
/**
 * @author hundun
 * Created on 2019/08/06
 */

import java.util.List;

import org.mahjong4j.tile.Tile;

public class DiscardAdvice {
	
	private final Tile discardTile;
	private final List<Tile> tenpaiList;
	private final boolean furiten;
	
	public DiscardAdvice(Tile discardTile, List<Tile> tenpaiList, boolean furiten) {
		this.discardTile = discardTile;
		this.tenpaiList = tenpaiList;
		this.furiten = furiten;
	}
	
	
	public Tile getDiscardTile() {
		return discardTile;
	}
	
	public List<Tile> getTenpaiList() {
		return tenpaiList;
	}
	
	public boolean isFuriten() {
		return furiten;
	}

}

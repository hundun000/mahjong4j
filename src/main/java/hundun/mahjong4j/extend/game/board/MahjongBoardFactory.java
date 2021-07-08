package hundun.mahjong4j.extend.game.board;
/**
 *
 * @author hundun
 * Created on 2019/03/05
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.mahjong4j.exceptions.Mahjong4jException;
import org.mahjong4j.tile.Tile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hundun.mahjong4j.extend.exception.GameException;

public class MahjongBoardFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(MahjongBoardFactory.class);
	

	
	/**
	 * 获取预设的牌局
	 * @throws Mahjong4jException 
	 * @throws GameException 
	 */
	public static MahjongBoard getMahjongBoardById(int id) throws Mahjong4jException, GameException {
		
		switch (id) {

		case 1:
			return new MahjongBoard(getTargetHandsTiles(new int[]{
		            1, 0, 0, 0, 0, 0, 0, 0, 1,
		            1, 0, 0, 0, 0, 0, 0, 0, 1,
		            1, 0, 0, 0, 0, 0, 0, 0, 1,
		            1, 1, 1, 1,
		            1, 1, 0}, Tile.CHN, id));
		case 2:
			return new MahjongBoard(getTargetHandsTiles(new int[]{
		            0, 2, 2, 2, 2, 2, 2, 1, 0,
		            0, 0, 0, 0, 0, 0, 0, 0, 0,
		            0, 0, 0, 0, 0, 0, 0, 0, 0,
		            0, 0, 0, 0,
		            0, 0, 0}, Tile.M8, id));
		case 3:
		    return new MahjongBoard(getTargetHandsTiles(new int[]{
                    0, 2, 2, 2, 2, 2, 2, 1, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0}, Tile.M9, id));
		case 4:
			return new MahjongBoard(getTargetHandsTiles(new int[]{
		            3, 3, 3, 0, 0, 1, 1, 0, 2,
		            0, 0, 0, 0, 0, 0, 0, 0, 0,
		            0, 0, 0, 0, 0, 0, 0, 0, 0,
		            0, 0, 0, 0,
		            0, 0, 0}, Tile.M5, id));
		case 5:
            return new MahjongBoard(getTargetHandsTiles(new int[]{
                    3, 3, 4, 0, 0, 1, 0, 0, 2,
                    0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0,
                    0, 0, 0}, Tile.M5, id));
		default:
			return new MahjongBoard(getRandomTiles(id));
		}
	}
	
	public static MahjongBoard getMahjongBoardByFile(String fileName) throws Mahjong4jException, GameException, IOException {
	    Path path = Paths.get(fileName);
        List<String> lines = Files.readAllLines(path);
        LinkedList<Tile> tiles = new LinkedList<>();
        for (String line : lines) {
	        String[] tileNamesInLine = line.split("\t");
	        for (String tileNameInLine : tileNamesInLine) {
	            try {
	                Tile tile = Tile.valueOf(tileNameInLine);
	                tiles.addLast(tile);
                } catch (IllegalArgumentException  e) {
                    throw new Mahjong4jException("牌桌构造文件错误：" + tileNameInLine + "不是Tile");
                }
	            
	        }
	    }
	    return new MahjongBoard(tiles);
	}
	
	public static List<Tile> getRandomTiles(long seed) {
    	EnumSet<Tile> tileKinds = EnumSet.allOf(Tile.class);
    	// 將所有種類的Tile各放4張進牌堆
    	List<Tile> tiles = new ArrayList<>();
    	for (Tile tile : tileKinds) {
    		for (int i = 0; i < Tile.NUM_EACH_KIND ; i++) {
    			tiles.add(tile);
    		}
    	}
    	// 洗牌
    	logger.info("seed = {}", seed);
    	Collections.shuffle(tiles, new Random(seed));
    	return tiles;
	}
	
	/**
	 * @param tilesNum 不包括last
	 * @param last
	 * @param otherTIlesSeed
	 * @return
	 */
	public static List<Tile> getTargetHandsTiles(int[] tilesNum, Tile last, int otherTIlesSeed) {
    	List<Tile> targetTiles = new ArrayList<>();
    	List<Tile> otherTiles = getRandomTiles(otherTIlesSeed);
    	// 将目标手牌从原有的牌堆中选出
    	for (int i = 0; i < tilesNum.length; i++) {
    		Tile tile = Tile.valueOf(i);
    		int num = tilesNum[i];
    		while (num-- > 0) {
    			otherTiles.remove(tile);
    			targetTiles.add(tile);
			}
    	}
    	otherTiles.remove(last);
    	
    	// 把目标手牌插到原有的牌堆指定位置，使其被同一个玩家摸到。
    	for (int i = 0; i < targetTiles.size(); i += MahjongBoard.NUM_TILE_FIRST_ROUND_DRAW_SPACE) {
    		int insertPos = i * MahjongBoard.NUM_PLAYERS;
    		// 每次连续模一栋
    		for (int j = 0 ; i + j < targetTiles.size() && j < 4; j++) {	
    			Tile tile = targetTiles.get(i + j);
    			otherTiles.add(insertPos, tile);
    		}
    	}
    	int lastPos = targetTiles.size() * MahjongBoard.NUM_PLAYERS;
    	otherTiles.add(lastPos, last);
    	
    	return otherTiles;
	}
	
	
	
	

}

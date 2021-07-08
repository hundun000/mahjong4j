package hundun.mahjong4j.extend.enhance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.mahjong4j.exceptions.HandsNegativeException;
import org.mahjong4j.exceptions.HandsOverFlowException;
import org.mahjong4j.exceptions.IllegalMentsuSizeException;
import org.mahjong4j.exceptions.Mahjong4jException;
import org.mahjong4j.exceptions.MahjongTileOverFlowException;
import org.mahjong4j.hands.Hands;
import org.mahjong4j.hands.Kantsu;
import org.mahjong4j.hands.Kotsu;
import org.mahjong4j.hands.Mentsu;
import org.mahjong4j.hands.MentsuComp;
import org.mahjong4j.hands.Shuntsu;
import org.mahjong4j.hands.Toitsu;
import org.mahjong4j.tile.Tile;
import org.mahjong4j.yaku.yakuman.KokushimusoResolver;

/**
 * 某些逻辑，理应加入Hands类，但鉴于Hands类属于共用包，所以暂且放在该子类里。
 * @author hundun
 * Created on 2019/11/20
 */
public class SuperHands extends Hands{

    public static final int HANDS_SIZE = 34;
    

    
   

    

    /**
     * @param otherTiles
     * @param last
     * @param mentsu
     * @throws MahjongTileOverFlowException
     * @throws HandsOverFlowException 
     */
    public SuperHands(int[] otherTiles, Tile last, Mentsu... mentsu) throws MahjongTileOverFlowException, IllegalMentsuSizeException, HandsOverFlowException {
        this(otherTiles, last, Arrays.asList(mentsu));
    }

    /**
     * @param allTiles lastの牌も含めて下さい合計14になるはずです
     * @param last     この牌もotherTilesに含めてください
     */
    public SuperHands(int[] allTiles, Tile last) throws HandsOverFlowException, MahjongTileOverFlowException, IllegalMentsuSizeException {
        this(allTiles, last, new ArrayList<Mentsu>());
    }
    
    public SuperHands(Hands hands) throws MahjongTileOverFlowException, IllegalMentsuSizeException, HandsOverFlowException {
        this(hands.getInputtedTiles(), hands.getLast(), hands.getInputtedMentsuList());
    }
    
    public SuperHands(int[] otherTiles, Tile last, List<Mentsu> mentsuList) throws MahjongTileOverFlowException, IllegalMentsuSizeException, HandsOverFlowException {
        super(otherTiles, last, mentsuList);
    }
    
    

    public SuperHands deepClone() throws MahjongTileOverFlowException, IllegalMentsuSizeException, HandsOverFlowException {
        return new SuperHands(Arrays.copyOf(inputtedTiles, inputtedTiles.length), last, inputtedMentsuList.toArray(new Mentsu[inputtedMentsuList.size()]));
    }

    

    



    public void drawToLastThenFindMentsu(Tile tile) throws MahjongTileOverFlowException, IllegalMentsuSizeException {
        last = tile;
        // 增加这张牌
        inputtedTiles[tile.getCode()]++;
        handsComp[tile.getCode()]++;
        checkTileOverFlow();
        
        
        
        findMentsu();
    }
    
    public boolean discardOneTile(Tile tile) {
        
        if (inputtedTiles[tile.getCode()] > 0) {
            // 打出last
            if(inputtedTiles[tile.getCode()] == 1 && tile == last) {
                clearLast();
            }
            inputtedTiles[tile.getCode()] --;
            handsComp[tile.getCode()]++;
            return true;
        } else {
            return false;
        }
        
    }

    

    /**
     * 计算是否听牌
     * 穷举尝试增加某一张牌后是否canWin,不判断该牌是否有剩（空听也是听牌）
     * @return
     * @throws IllegalMentsuSizeException 
     * @throws HandsOverFlowException 
     * @throws MahjongTileOverFlowException 
     */
    public List<Tile> getTenpaiList() throws IllegalMentsuSizeException, HandsOverFlowException{
        List<Tile> tenpaiList = new ArrayList<>();
        for (Tile testTenpai : Tile.values()) {
            
            // 新的手牌
            SuperHands testHands;
            // 遍历时可能尝试到手上某种牌大于4引发MahjongTileOverFlowException，捕获之且不用额外处理
            try {
                // clone this
                testHands = this.deepClone();
                testHands.drawToLastThenFindMentsu(testTenpai);
                if (testHands.canWin) {
                    // 有在听这张牌
                    tenpaiList.add(testTenpai);
                }
            } catch (MahjongTileOverFlowException e) {
                continue;
            } catch (HandsOverFlowException e) {
                throw e;
            }
        }
        return tenpaiList;
    }
    


    public boolean existInInputtedTile(Tile tile) {
        return existInInputtedTile(tile, 1);
    }
    public boolean existInInputtedTile(Tile tile, int num) {
        return inputtedTiles[tile.getCode()] >= num;
    }
    
    public void clearLast() {
        last = null;
    }
    


    /**
     * 产生一个新的附露
     * @param mentsu
     * @param additionTile
     * @throws Mahjong4jException
     */
    public void genInputtedMentsu(Mentsu mentsu, Tile additionTile) throws Mahjong4jException {
        boolean canGen = true;
        // 先全部检查:手牌加上active的牌，足够扣除mentsu中的牌
        int[] newArray = Arrays.copyOf(inputtedTiles, HANDS_SIZE);
        if (additionTile != null) {
            newArray[additionTile.getCode()]++;
        }
        for (Tile tile : mentsu.getTiles()) {
            newArray[tile.getCode()]--;
            if (newArray[tile.getCode()] < 0) {
                canGen = false;
                break;
            }
        }
        // 再执行消耗手牌
        if (canGen) {
            System.arraycopy(newArray, 0, inputtedTiles, 0, HANDS_SIZE);
            inputtedMentsuList.add(mentsu);
        } else {
            throw new Mahjong4jException("试图执行非法鸣牌:+" + additionTile.toString() + ",-" + mentsu.getTiles().toString());
        }
    }
    
    

    
    

}

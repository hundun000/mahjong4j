package hundun.mahjong4j.extend.utls;

import static org.mahjong4j.tile.Tile.M1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.mahjong4j.exceptions.HandsOverFlowException;
import org.mahjong4j.exceptions.IllegalMentsuSizeException;
import org.mahjong4j.exceptions.Mahjong4jException;
import org.mahjong4j.exceptions.MahjongTileOverFlowException;
import org.mahjong4j.tile.Tile;

import hundun.mahjong4j.extend.enhance.SuperHands;

/**
 * WIP
 * @author hundun
 * Created on 2021/12/10
 */
public class HandsParser {
    
    private static List<Character> validTypes = Arrays.asList('m', 'M', 's', 'S', 'p', 'P', 'w', 'W', 'd', 'D');
    
    private static enum State {
        ACCEPTED,
        HAS_TYPE_WAIT_NUM,
        ;
    }
    
    /*
     * https://suzume.hakata21.com/5zats/paishi_url.html
     * m123p1234789s338s8
     */
    public static SuperHands create(String paishiCode) throws Exception {
        
        char type = 0;
        char num = 0;
        State state = State.ACCEPTED;
        
        
        List<Character> chars = paishiCode.chars().mapToObj(e->(char)e).collect(Collectors.toList());
        List<Tile> tiles = new ArrayList<>();
        Iterator<Character> iterator = chars.iterator();
        while (iterator.hasNext()) {
            char next = iterator.next();
            switch (state) {
                case ACCEPTED:
                    if (validTypes.contains(next)) {
                        type = next;
                        state = State.HAS_TYPE_WAIT_NUM;
                    } else {
                        throw new Exception("cannot accept " + next);
                    }
                    break;
                case HAS_TYPE_WAIT_NUM:
                    if (validTypes.contains(next)) {
                        num = next;
                        state = State.ACCEPTED;
                        tiles.add(toTile(type, num));
                    } else {
                        throw new Exception("cannot accept " + next);
                    }
                    break;
                default:
                    break;
            }
            
        }
        
        int[] tilesArray = {
            1, 1, 1, 1, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 0, 0, 1, 1, 1,
            0, 0, 0, 0, 0, 0, 0, 0, 3,
            0, 0, 0, 0,
            0, 0, 0
        };
        Tile last = M1;
        SuperHands hands = new SuperHands(tilesArray, last);
        return hands;
    }
    
    private static Tile toTile(char type, char num) throws Exception {
        String tileName;
        switch (type) {
            case 'w':
            case 'W':
                switch (num) {
                    case 1:
                        tileName = Tile.HAK.name();
                        break;
                    case 2:
                        tileName = Tile.HAT.name();           
                        break;
                    case 3:
                        tileName = Tile.CHN.name();
                        break;
                    default:
                        break;
                }
            case 'd':
            case 'D':
                switch (num) {
                    case 1:
                        tileName = Tile.TON.name();
                        break;
                    case 2:
                        tileName = Tile.NAN.name();           
                        break;
                    case 3:
                        tileName = Tile.SHA.name();
                        break;
                    case 4:
                        tileName = Tile.PEI.name();
                        break;
                    default:
                        break;
                }
            default:
                tileName = ("" + num + type).toUpperCase();
                break;
        }
        if (tileName != null) {
            return Tile.valueOf(tileName);
        }
        throw new Exception("cannot to tile " + type + " " + num);
    }
    
}

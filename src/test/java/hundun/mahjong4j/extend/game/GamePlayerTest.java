package hundun.mahjong4j.extend.game;

import static org.junit.Assert.*;
import static org.mahjong4j.tile.Tile.CHN;
import static org.mahjong4j.tile.Tile.M1;
import static org.mahjong4j.tile.Tile.M2;
import static org.mahjong4j.tile.Tile.NAN;
import static org.mahjong4j.tile.Tile.PEI;
import static org.mahjong4j.tile.Tile.SHA;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mahjong4j.GeneralSituation;
import org.mahjong4j.PersonalSituation;
import org.mahjong4j.Player;
import org.mahjong4j.hands.Hands;
import org.mahjong4j.tile.Tile;

import hundun.mahjong4j.extend.enhance.SuperHands;
import hundun.mahjong4j.extend.utls.CharImageTool;

/**
 * @author hundun
 * Created on 2021/12/10
 */
public class GamePlayerTest {

    private GamePlayer player;

    @Before
    public void setUp() throws Exception {
        int[] tiles = {
            1, 1, 1, 1, 0, 0, 0, 0, 3,
            0, 0, 0, 0, 0, 0, 1, 1, 1,
            0, 0, 0, 0, 0, 0, 0, 0, 3,
            0, 0, 0, 0,
            0, 0, 0
        };
        Tile last = M1;
        SuperHands hands = new SuperHands(tiles, last);
        List<Tile> dora = new ArrayList<>(1);
        List<Tile> uradora = new ArrayList<>(1);
        GeneralSituation generalSituation;
        generalSituation = new GeneralSituation(false, false, Tile.TON, dora, uradora);
        PersonalSituation personalSituation;
        personalSituation = new PersonalSituation(Tile.TON);

        player = new GamePlayer(hands, generalSituation, personalSituation);
        player.drawToLast(Tile.M5, true, true);
    }
    
    @Test
    public void test() {
        StringBuilder stringBuilder = new StringBuilder();
        CharImageTool.stringBuilderAppendGamePlayerToCharImage(stringBuilder, player, true);
        System.out.println(stringBuilder.toString());
    }

}

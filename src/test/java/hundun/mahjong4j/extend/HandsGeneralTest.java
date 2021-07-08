package hundun.mahjong4j.extend;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mahjong4j.tile.Tile.CHN;
import static org.mahjong4j.tile.Tile.M4;
import static org.mahjong4j.tile.Tile.P4;
import static org.mahjong4j.tile.Tile.TON;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mahjong4j.hands.Hands;
import org.mahjong4j.hands.Kantsu;
import org.mahjong4j.hands.Kotsu;
import org.mahjong4j.hands.Mentsu;
import org.mahjong4j.hands.MentsuComp;
import org.mahjong4j.hands.Shuntsu;
import org.mahjong4j.hands.Toitsu;
import org.mahjong4j.tile.Tile;

/**
 * 开发过程中发现特定牌型触发bug，来这里针对该牌型测试
 * @author hundun 2018年11月7日
 */
public class HandsGeneralTest {
	private Hands actualHands;

    @Before
    public void setUp() throws Exception {
        int[] otherTiles = {
            0, 0, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            2, 0, 0, 0,
            0, 0, 0
        };
        Mentsu[] mentsus = new Mentsu[2];
        mentsus[0] = new Kotsu(true, P4);
        mentsus[1] = new Kantsu(true, CHN);

        actualHands = new Hands(otherTiles, TON, mentsus);
    }

    @Test
    public void testGetMentsuCompList() throws Exception {
        List<Mentsu> expectedMentsuList = new ArrayList<>(5);
        expectedMentsuList.add(new Toitsu(TON));
        expectedMentsuList.add(new Shuntsu(false, M4));
        expectedMentsuList.add(new Shuntsu(false, P4));
        expectedMentsuList.add(new Kotsu(true, P4));
        expectedMentsuList.add(new Kantsu(true, CHN));
        MentsuComp expected = new MentsuComp(expectedMentsuList, TON);

        assertEquals(1, actualHands.getMentsuCompSet().size());
        assertThat(actualHands.getMentsuCompSet(), hasItems(expected));
    }

    //@Test
    public void testGetCanWin() throws Exception {
        assertTrue(actualHands.getCanWin());
    }

    //@Test
    public void testGetLast() throws Exception {
        Tile expected = TON;
        assertEquals(expected, actualHands.getLast());
    }

    //@Test
    public void testGetHandsComp() throws Exception {
        int[] expected = {
            0, 0, 1, 1, 1, 0, 0, 0, 0,
            0, 0, 1, 4, 1, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0,
            2, 0, 0, 0,
            0, 0, 4
        };

        assertArrayEquals(expected, actualHands.getHandsComp());
    }
}

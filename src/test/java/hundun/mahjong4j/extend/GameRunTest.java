package hundun.mahjong4j.extend;

import org.junit.Test;
import org.mahjong4j.exceptions.Mahjong4jException;

import hundun.mahjong4j.extend.game.board.MahjongBoard;
import hundun.mahjong4j.extend.game.board.MahjongBoardFactory;
import hundun.mahjong4j.extend.utls.CharImageTool;

/**
 *
 * @author hundun
 * Created on 2019/03/04
 */
public class GameRunTest {
	
	MahjongBoard mahjongBoard;
	
	@Test
	public void test() throws Exception {
		mahjongBoard = MahjongBoardFactory.getMahjongBoardById(0);
		System.out.println(CharImageTool.mahjongBoardToCharImage(mahjongBoard));
		mahjongBoard.playerNormalDraw();
		System.out.println(CharImageTool.mahjongBoardToCharImage(mahjongBoard));
	}

}

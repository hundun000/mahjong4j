package hundun.mahjong4j.extend.enhance;

import static org.mahjong4j.Score.SCORE0;
import static org.mahjong4j.tile.TileType.SANGEN;
import static org.mahjong4j.yaku.normals.NormalYaku.CHITOITSU;
import static org.mahjong4j.yaku.normals.NormalYaku.DORA;
import static org.mahjong4j.yaku.normals.NormalYaku.PINFU;
import static org.mahjong4j.yaku.normals.NormalYaku.REACH;
import static org.mahjong4j.yaku.normals.NormalYaku.TSUMO;
import static org.mahjong4j.yaku.normals.NormalYaku.URADORA;
import static org.mahjong4j.yaku.yakuman.Yakuman.KOKUSHIMUSO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mahjong4j.GeneralSituation;
import org.mahjong4j.Mahjong4jYakuConfig;
import org.mahjong4j.PersonalSituation;
import org.mahjong4j.Player;
import org.mahjong4j.Score;
import org.mahjong4j.exceptions.Mahjong4jException;
import org.mahjong4j.hands.Hands;
import org.mahjong4j.hands.Mentsu;
import org.mahjong4j.hands.MentsuComp;
import org.mahjong4j.tile.Tile;
import org.mahjong4j.yaku.normals.NormalYaku;
import org.mahjong4j.yaku.normals.NormalYakuResolver;
import org.mahjong4j.yaku.yakuman.Yakuman;
import org.mahjong4j.yaku.yakuman.YakumanResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 某些逻辑，理应加入Player类，但鉴于Player类属于共用包，所以暂且放在该子类里。
 * @author hundun
 * Created on 2019/11/23
 */
public class SuperPlayer extends Player{
    
    private static final Logger logger = LoggerFactory.getLogger(SuperPlayer.class);

 
    public SuperPlayer(SuperHands hands) throws Mahjong4jException {
        this(hands, null, null);
    }

    public SuperPlayer(SuperHands hands, GeneralSituation generalSituation, PersonalSituation personalSituation) throws Mahjong4jException {
        super(hands, generalSituation, personalSituation);
    }


    /**
     * 只有手牌深复制，其余浅复制
     * @return
     * @throws Mahjong4jException
     */
    public SuperPlayer deepClone() throws Mahjong4jException {
        SuperPlayer player = new SuperPlayer(this.getHands().deepClone(), getGeneralSituation(), getPersonalSituation());
        return player;
    }

    

    /**
     * 算分；
     * 若能胡，则会对本类的成员：normalYakuList，yakumanList，score等，设置为胡牌时候的状态
     */
    @Override
    public void calculate() {
        getNormalYakuList().clear();
        getYakumanList().clear();
        
        super.calculate();
    }

    

    /**
     * this是否为other的下家
     */
    public boolean isNext(SuperPlayer other) {
        switch (other.getPersonalSituation().getJikaze()) {
        case TON:
            return this.getPersonalSituation().getJikaze() == Tile.NAN;
        case NAN:
            return this.getPersonalSituation().getJikaze() == Tile.SHA;
        case SHA:
            return this.getPersonalSituation().getJikaze() == Tile.PEI;
        case PEI:
            return this.getPersonalSituation().getJikaze() == Tile.TON;
        default:
            return false;
        }
    }

    

    // ============= getter & setter ===============
    


    public SuperHands getHands() {
        return (SuperHands) hands;
    }

    public GeneralSituation getGeneralSituation() {
        return generalSituation;
    }

    public PersonalSituation getPersonalSituation() {
        return personalSituation;
    }

    public void setGeneralSituation(GeneralSituation generalSituation) {
        this.generalSituation = generalSituation;
    }


    
}


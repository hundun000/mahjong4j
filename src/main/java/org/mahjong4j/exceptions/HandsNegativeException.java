package org.mahjong4j.exceptions;
/**
 * @author hundun
 * Created on 2019/08/05
 */
public class HandsNegativeException extends Mahjong4jException {

	public HandsNegativeException(String tileName) {
		super(tileName + "少牌です");
	}

}

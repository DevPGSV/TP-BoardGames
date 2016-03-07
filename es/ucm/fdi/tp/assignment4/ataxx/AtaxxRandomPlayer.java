package es.ucm.fdi.tp.assignment4.ataxx;

import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AtaxxRandomPlayer extends Player{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public GameMove requestMove(Piece p, Board board, List<Piece> pieces, GameRules rules) {
	    if (board.isFull()) {
            throw new GameError("The board is full, cannot make a random move!!");
        }
	    List<GameMove> availableMoves = rules.validMoves(board, pieces, p);
	    if (availableMoves.size() == 0) {
	        throw new GameError("Nothing to move!!");
	    }
		return availableMoves.get(Utils.randomInt(availableMoves.size()));
	}

}


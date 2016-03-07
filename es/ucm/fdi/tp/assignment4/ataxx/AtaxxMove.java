package es.ucm.fdi.tp.assignment4.ataxx;

import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.Pair;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AtaxxMove extends GameMove{
    
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Pair<Integer, Integer> origin;
    private Pair<Integer, Integer> destination;
    

    public AtaxxMove() {}
    
    public AtaxxMove(Pair<Integer, Integer> origin, Pair<Integer, Integer> destination, Piece turn) {
        super(turn);
        this.origin = origin;
        this.destination = destination;
    }
    
    private static int getMovementDistanceRadius(Pair<Integer, Integer> origin, Pair<Integer, Integer> destination) {
        return Math.max(Math.abs(destination.getFirst() - origin.getFirst()), Math.abs(destination.getSecond() - origin.getSecond()));
    }
    
    private void eatPiecesAroundCoords(Board board, Pair<Integer, Integer> coords) {
        Piece tmpPiece;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if ((coords.getFirst() + i >= 0) && (coords.getFirst() + i < board.getRows()) && (coords.getSecond() + j >= 0) && (coords.getSecond() + j < board.getCols())) {
                    tmpPiece = board.getPosition(coords.getFirst() + i, coords.getSecond() + j);
                    if ((tmpPiece != null) && (tmpPiece != getPiece()) && (tmpPiece != AtaxxRules.getObstacle())) {
                        board.setPosition(coords.getFirst() + i, coords.getSecond() + j, getPiece());
                        board.setPieceCount(getPiece(), board.getPieceCount(getPiece()) + 1);
                        board.setPieceCount(tmpPiece, board.getPieceCount(tmpPiece) - 1);
                    }
                }
            }
        }
    }
    
    @Override
	public void execute(Board board, List<Piece> pieces) {
        //System.out.println(board.getPieceCount(getPiece()));
        if (board.getPosition(this.origin.getFirst(), this.origin.getSecond()) != getPiece()) {
            throw new GameError("You don't own a piece in (" + this.origin.getFirst() + ", " + this.origin.getSecond() + ")!");
        } else if (board.getPosition(this.destination.getFirst(), this.destination.getSecond()) != null) {
            throw new GameError("position (" + this.destination.getFirst() + ", " + this.destination.getSecond() + ") is already occupied!");
        } else if (getMovementDistanceRadius(this.origin, this.destination) > 2) {
            throw new GameError("You can't yump " + getMovementDistanceRadius(this.origin, this.destination) + " cells! Maximum is 2");
        }
        
		board.setPosition(this.destination.getFirst(), this.destination.getSecond(), getPiece());
		board.setPieceCount(getPiece(), board.getPieceCount(getPiece()) + 1);
		if (getMovementDistanceRadius(this.origin, this.destination) == 2) {
		    board.setPosition(this.origin.getFirst(), this.origin.getSecond(), null);
		    board.setPieceCount(getPiece(), board.getPieceCount(getPiece()) - 1);
		}
		eatPiecesAroundCoords(board, this.destination);
	}

	@Override
	public GameMove fromString(Piece p, String str) {
	    String[] words = str.split(" ");
        if (words.length != 4) {
            return null;
        }

        try {
            Pair<Integer, Integer> origin      = new Pair<Integer, Integer>(Integer.parseInt(words[0]), Integer.parseInt(words[1]));
            Pair<Integer, Integer> destination = new Pair<Integer, Integer>(Integer.parseInt(words[2]), Integer.parseInt(words[3]));
            return createMove(origin, destination, p);
        } catch (NumberFormatException e) {
            return null;
        }
	}

	private GameMove createMove(Pair<Integer, Integer> origin, Pair<Integer, Integer> destination, Piece p) {
        return new AtaxxMove(origin, destination, p);
    }

    @Override
	public String help() {
		return "'originRow originCol destinationRow destinationCol' to move a piece from origin to destination.";
	}
    
    @Override
    public String toString() {
        if (getPiece() == null) {
            return help();
        } else {
            return "Place a piece '" + getPiece() + "' from (" + this.origin.getFirst() + ", " + this.origin.getSecond() + ") to (" + this.destination.getFirst() + "," + this.destination.getSecond() + ")";
        }
    }

}

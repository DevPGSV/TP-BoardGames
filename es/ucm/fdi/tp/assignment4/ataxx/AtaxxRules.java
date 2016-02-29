package es.ucm.fdi.tp.assignment4.ataxx;

import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.Utils;
import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.FiniteRectBoard;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.Game.State;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Pair;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

public class AtaxxRules implements GameRules {
	
	private static final Piece OBSTACLE = new Piece("*");
	private int dim;
	private int obstacles;

	public AtaxxRules(int dim, int obstacles) {
		if (dim < 5) {
			throw new GameError("Dimension must be at least 3: " + dim);
		} else if (dim % 2 == 0) {
			throw new GameError("Dimension must be odd");
		} else {
			this.dim = dim;
		}
		this.obstacles = (obstacles >= 0) ? obstacles : 0;
	}

	@Override
	public String gameDesc() {
		return ":Ataxx " + dim + "x" + dim + ": a board game for Atari guys of the nineties.";
	}

	@Override
	public Board createBoard(List<Piece> pieces) {
		Board board = new FiniteRectBoard(dim, dim);
		if (pieces.size() >= 1) {
			board.setPosition(0, 0, pieces.get(0));
			board.setPosition(board.getRows() - 1, board.getCols() - 1, pieces.get(0));
		}
		if (pieces.size() >= 2) {
			board.setPosition(board.getRows() - 1, 0, pieces.get(1));
			board.setPosition(0, board.getCols() - 1, pieces.get(1));
		}
		if (pieces.size() >= 3) {
			board.setPosition(board.getRows() / 2, 0, pieces.get(2));
			board.setPosition(board.getRows() / 2,  board.getCols() - 1, pieces.get(2));
		}
		if (pieces.size() >= 4) {
			board.setPosition(0, board.getCols() / 2, pieces.get(3));
			board.setPosition(board.getRows() - 1, board.getCols() / 2, pieces.get(3));
		}
		createObstacles(board, this.obstacles);
		return board;
	}
	
	private void createObstacles(Board board, int totalObstacles) {
		int addedObstacles = 0;
		
		keepCreatingObstacles:
		while (addedObstacles < totalObstacles) {
			try {
				Pair<Integer, Integer> coord = chooseRandomEmptyCoordinate(board);
				
				if ((addedObstacles < totalObstacles) && (board.getPosition(coord.getFirst(), coord.getSecond()) == null)) {
					board.setPosition(coord.getFirst(), coord.getSecond(), AtaxxRules.getObstacle());
					addedObstacles++;
				}
				if ((addedObstacles < totalObstacles) && (board.getPosition(board.getRows() - coord.getFirst() - 1, coord.getSecond()) == null)) {
					board.setPosition(board.getRows() - coord.getFirst() - 1, coord.getSecond(), AtaxxRules.getObstacle());
					addedObstacles++;
				}
				if ((addedObstacles < totalObstacles) && (board.getPosition(coord.getFirst(), board.getCols() - coord.getSecond() - 1) == null)) {
					board.setPosition(coord.getFirst(), board.getCols() - coord.getSecond() - 1, AtaxxRules.getObstacle());
					addedObstacles++;
				}
				if ((addedObstacles < totalObstacles) && (board.getPosition(board.getRows() - coord.getFirst() - 1, board.getCols() - coord.getSecond() - 1) == null)) {
					board.setPosition(board.getRows() - coord.getFirst() - 1, board.getCols() - coord.getSecond() - 1, AtaxxRules.getObstacle());
					addedObstacles++;
				}
			} catch (GameError e) {
				break keepCreatingObstacles;
			}
		}
	}
	
	private Pair<Integer, Integer> chooseRandomEmptyCoordinate(Board board) {
		if (board.isFull()) {
			throw new GameError("The board is full!!");
		}

		int rows = board.getRows();
		int cols = board.getCols();

		// pick an initial random position
		int currRow = Utils.randomInt(rows);
		int currCol = Utils.randomInt(cols);

		// start at (currRow,currColl) and look for the first empty position.
		while (true) {
			if (board.getPosition(currRow, currCol) == null) {
				//return createMove(currRow, currCol, p);
				return new Pair<Integer, Integer>(currRow, currCol);
			}
			currCol = (currCol + 1) % cols;
			if (currCol == 0) {
				currRow = (currRow + 1) % rows;
			}
		}
	}

	@Override
	public Piece initialPlayer(Board board, List<Piece> pieces) {
		return pieces.get(0);
	}

	@Override
	public int minPlayers() {
		return 2;
	}

	@Override
	public int maxPlayers() {
		return 4;
	}

	@Override
	public Pair<State, Piece> updateState(Board board, List<Piece> pieces, Piece turn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Piece nextPlayer(Board board, List<Piece> pieces, Piece turn) {
		Piece nt = pieces.get((pieces.indexOf(turn) + 1) % pieces.size());
		if (turn.equals(AtaxxRules.getObstacle())) {
			if (nt.equals(turn)) return nt; // Stop infinite recursion!
			nt = nextPlayer(board, pieces, nt);
		}
		return nt;
	}

	@Override
	public double evaluate(Board board, List<Piece> pieces, Piece turn) {
		return 0;
	}

	@Override
	public List<GameMove> validMoves(Board board, List<Piece> playersPieces, Piece turn) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static Piece getObstacle() {
		return AtaxxRules.OBSTACLE;
	}

}

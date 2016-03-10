package es.ucm.fdi.tp.assignment4.ataxx;

import java.util.ArrayList;
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

/**
 * <p>Rules for Ataxx game.</p>
 * <ul>
 * <li>The game is played on an NxN board (with N>=5 and odd).</li>
 * <li>The number of players is between 2 and 4.</li>
 * <li>The player turn in the given order, each moving a piece owned to and empty cell.</li>
 * <li>A piece can be moved a maximum distance of 2. If the distance is 1, the piece will be duplicated (origin & destination)</li>
 * <li>After moving a piece, pieces around the destination will be converted to the moved piece type.</li>
 * <li>When the board is full, the winner is the player with the greatest amount of pieces.</li>
 * </ul>
 * 
 * <p>Reglas para el juego Ataxx.</p>
 * <ul>
 * <li>Se juega en un tablero de NxN (con N>=5 y siendo N impar).</li>
 * <li>El numero de jugadores esta entre 2 y 4.</li>
 * <li>Los jugadores juegan en el orden proporcionado, moviendo cada uno una pieza de su posesión a una casilla vacía.</li>
 * <li>Una ficah puede moverse una distacia máxima de 2. Si la distancia es 1, la ficha se duplicará (origen & destino)</li>
 * <li>Tras mover una ficha, las fichas alrededor del destino serán convertidas al mismo tipo que la ficha movida.</li>
 * <li>Cuando el tablero está lleno, el ganador es el jugador con mayor número e fichas.</li>
 * </ul>
 *
 */
public class AtaxxRules implements GameRules {
	
    /**
     * <p>This object is returned by gameOver to indicate that the game is not over. Just to avoid creating it multiple times, etc.</p>
     */
    protected final Pair<State, Piece> gameInPlayResult = new Pair<State, Piece>(State.InPlay, null);
	/**
	 * <p>Static instance of an obstacle to avoid creating it multiple times.</p>
	 */
	private static final Piece OBSTACLE = new Piece("*");
	/**
	 * <p>Dimensions of the board</p>
	 */
	private int dim;
	/**
	 * <p>Number of obstacles</p>
	 */
	private int obstacles;

	/**
	 * <p>AtaxxRules constructor</p>
	 * 
	 * @param dim
	 *     <p>Dimensions of the board</p>
	 * @param obstacles
	 *     <p>Number of obstacles</p>
	 */
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
		
		for (Piece piece : pieces) {
		    board.setPieceCount(piece, 0);
		}
		
		if (pieces.size() >= 1) {
			board.setPosition(0, 0, pieces.get(0));
			board.setPosition(board.getRows() - 1, board.getCols() - 1, pieces.get(0));
			
			board.setPieceCount(pieces.get(0), board.getPieceCount(pieces.get(0)) + 2);
		}
		if (pieces.size() >= 2) {
			board.setPosition(board.getRows() - 1, 0, pieces.get(1));
			board.setPosition(0, board.getCols() - 1, pieces.get(1));
			
			board.setPieceCount(pieces.get(1), board.getPieceCount(pieces.get(1)) + 2);
		}
		if (pieces.size() >= 3) {
			board.setPosition(board.getRows() / 2, 0, pieces.get(2));
			board.setPosition(board.getRows() / 2,  board.getCols() - 1, pieces.get(2));
			
			board.setPieceCount(pieces.get(2), board.getPieceCount(pieces.get(2)) + 2);
		}
		if (pieces.size() >= 4) {
			board.setPosition(0, board.getCols() / 2, pieces.get(3));
			board.setPosition(board.getRows() - 1, board.getCols() / 2, pieces.get(3));
			
			board.setPieceCount(pieces.get(3), board.getPieceCount(pieces.get(3)) + 2);
		}
		createObstacles(board, this.obstacles);
		return board;
	}
	
	/**
	 * <p>Creates {@code totalObstacles} obstacles randomly in the {@code board}</p>
	 * @param board
	 *     <p>Board where obstacles will be created.</p>
	 * @param totalObstacles
	 *     <p>Number of obstacles</p>
	 */
	private void createObstacles(Board board, int totalObstacles) {
		int addedObstacles = 0;
		
		if (totalObstacles % 4 == 1) {
		    if (board.getPosition(board.getRows() / 2, board.getCols() / 2) == null) {
                board.setPosition(board.getRows() / 2, board.getCols() / 2, AtaxxRules.getObstacle());
                addedObstacles++;
            }
		}
		
		
		
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
	
	/**
	 * <p>Gets a random empty coordinate of the board.</p>
	 * 
	 * @param board
	 *     <p>Board to be searched.</p>
	 * @return
	 *     <p>A random empty coordinate of the board</p>
	 */
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
	    if (board.isFull()) {
	        State state = State.Draw;
	        Piece winner = null;
	        int winnerPoints = 0;
	        for (Piece piece : pieces) {
	            if (winnerPoints == board.getPieceCount(piece)) {
	                winner = null;
	                state = State.Draw;
	            } else if (winnerPoints < board.getPieceCount(piece)) {
	                winnerPoints = board.getPieceCount(piece);
	                winner = piece;
                    state = State.Won;
	            }
	        }
	        return new Pair<State, Piece>(state, winner);
	    }
	    
	    return gameInPlayResult;
	}

	@Override
	public Piece nextPlayer(Board board, List<Piece> pieces, Piece turn) {
		Piece nt = pieces.get((pieces.indexOf(turn) + 1) % pieces.size());
		if (validMoves(board, pieces, nt).size() == 0) {
			nt = nextPlayer(board, pieces, nt);
		}
		return nt;
	}

	@Override
	public double evaluate(Board board, List<Piece> pieces, Piece turn) {
		return 0;
	}
	
	/**
	 * <p>Creates a list of possible moves a piece at some specific coordinates execute.</p>
	 * @param board
	 *     <p>Board where the pice to move is.</p>
	 * @param turn
	 *     <p>Type of piece to move.</p>
	 * @param coords
	 *     <p>Coordinates of the piece.</p>
	 * @return
	 *     <p>List of possible moves for the specified piece.</p>
	 */
	private List<GameMove> validMovesFromCoord(Board board, Piece turn, Pair<Integer, Integer> coords) {
	    List<GameMove> moves = new ArrayList<GameMove>();
	    for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                if ((coords.getFirst() + i >= 0) && (coords.getFirst() + i < dim) && (coords.getSecond() + j >= 0) && (coords.getSecond() + j < dim)) {
                    if (board.getPosition(coords.getFirst() + i, coords.getSecond() + j) == null) {
                        moves.add(new AtaxxMove(coords, new Pair<Integer, Integer>(coords.getFirst() + i, coords.getSecond() + j), turn));
                    }
                }
            }
        }
	    return moves;
	}

	@Override
	public List<GameMove> validMoves(Board board, List<Piece> playersPieces, Piece turn) {
	    List<GameMove> moves = new ArrayList<GameMove>();
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getCols(); j++) {
                if ((board.getPosition(i, j) != null) && (board.getPosition(i, j) == turn)) {
                    moves.addAll(validMovesFromCoord(board, turn, new Pair<Integer, Integer>(i, j)));
                }
            }
        }
        return moves;
	}
	
	/**
	 * <p>AtaxxRules.OBSTACLE getter</p>
	 * 
	 * @return 
	 *     <p>AtaxxRules.OBSTACLE</p>
	 */
	public static Piece getObstacle() {
		return AtaxxRules.OBSTACLE;
	}

}

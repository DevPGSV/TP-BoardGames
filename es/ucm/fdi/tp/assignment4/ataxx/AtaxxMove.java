package es.ucm.fdi.tp.assignment4.ataxx;

import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.model.Board;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameMove;
import es.ucm.fdi.tp.basecode.bgame.model.Pair;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;

/**
 * A Class representing a move for Ataxx.
 * 
 * <p>
 * Clase para representar un movimiento del juego Ataxx.
 * 
 */
public class AtaxxMove extends GameMove{
    
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * <p>The coordinates of the piece returned by {@link GameMove#getPiece()} to be moved.</p>
     * <p>Las coordenadas de la ficha devuelta por {@link GameMove#getPiece()} que será movida.</p>
     * 
     */
    private Pair<Integer, Integer> origin;
    
    /**
     * <p>The final coordinates of the piece returned by {@link GameMove#getPiece()} to be moved.</p>
     * <p>Las coordenadas finales de la ficha devuelta por {@link GameMove#getPiece()} que será movida.</p>
     * 
     */
    private Pair<Integer, Integer> destination;
    
    /**
     * <p>This constructor should be used ONLY to get an instance of {@link AtaxxMove} to generate game moves from strings by calling {@link #fromString(String)}</p>
     * <p>Solo se debe usar este constructor para obtener objetos de {@link AtaxxMove} para generar movimientos a partir de strings usando el metodo {@link #fromString(String)}</p>
     * 
     */
    public AtaxxMove() {}
    
    
    /**
     * <p>Constructs a move for moving a piece of the type referenced by {@code turn} from {@code origin} to {@code destination}</p>
     * <p>Construye un movimiento para mover una ficha del tipo referenciado por {@code turn} de {@code origin} a {@code destination}</p>
     * 
     * @param origin
     *            <p>Initial coordinates of the piece</p>
     *            <p>Coordenadas iniciales de la ficha</p>
     * @param destination
     *            <p>Final coordinates of the piece</p>
     *            <p>Coordenadas finales de la ficha</p>
     * @param turn
     *            <p>The piece to be moved from {@code origin} to {@code destination}</p>
     *            <p>La ficha a mover de {@code origin} a {@code destination}</p>
     */
    public AtaxxMove(Pair<Integer, Integer> origin, Pair<Integer, Integer> destination, Piece turn) {
        super(turn);
        this.origin = origin;
        this.destination = destination;
    }
    
    /**
     * <p>Calculates the absolute distance between two coordinates {@code origin} and {@code destination} (in concentric square radius)</p>
     * <p>Calcula la distancia absoluta entre dos coordenadas {@code origin} y {@code destination} (en radios cuadrados concéntricos)</p>
     * 
     * @param origin
     *            <p>First coordinate</p>
     *            <p>Primera coordenada</p>
     * @param destination
     *            <p>Second coordinate</p>
     *            <p>Segunda coordenada</p>
     * @return
     *            <p>The absolute distance between two coordinates {@code origin} and {@code destination}</p>
     *            <p>La distancia absoluta entre dos coordenadas {@code origin} y {@code destination}</p>
     */
    private static int getMovementDistanceRadius(Pair<Integer, Integer> origin, Pair<Integer, Integer> destination) {
        return Math.max(Math.abs(destination.getFirst() - origin.getFirst()), Math.abs(destination.getSecond() - origin.getSecond()));
    }
    
    
    /**
     * <p>Changes all pieces around {@code coords} (except pieces of the same type: {@link GameMove#getPiece()} and obstacles) to be of the current type: {@link GameMove#getPiece()}</p>
     * <p>Cambia las piezas alrededor de {@code coords} (excepto las del mismo tipo: {@link GameMove#getPiece()} y los obstáculos) a ser del tipo actual: {@link GameMove#getPiece()}</p>
     * 
     * @param board
     *            <p>Board to use to change the pieces</p>
     *            <p>Tablero a usar para cambiar las fichas</p>
     * @param coords
     *            <p>Coordinates around which pieces will be changed</p>
     *            <p>Coordenadas alrededor de las cuales realizar el cambio</p>
     */
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

    /**
     * <p>This move can be constructed from a string of the form "originRow SPACE originCol SPACE destinationRow SPACE destinationCol"
     * where (originRow, originCol) and (destinationRow, destinationCol) are coordinates representing a position.</p>
     * 
     * <p>Se puede construir un movimiento desde un string de la forma "originRow SPACE originCol SPACE destinationRow SPACE destinationCol"
     * donde (originRow, originCol) y (destinationRow, destinationCol) son coordenadas que representan una casilla.</p>
     */
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

	/**
     * <p>Creates a move that is called from {@link #fromString(Piece, String)}.
     * Separating it from that method allows us to use this class for other
     * similar games by overriding this method.</p>
     * 
     * <p>Crea un nuevo movimiento con la misma ficha utilizada en el movimiento
     * actual. Llamado desde {@link #fromString(Piece, String)}; se separa este
     * metodo del anterior para permitir utilizar esta clase para otros juegos
     * similares sobrescribiendo este metodo.</p>
     * 
     * @param origin
     *            <p>Initial coordinates of the move</p>
     *            <p>Coordenadas iniciales del movimiento</p>
     * 
     * @param destination
     *           <p>Final coordinates of the move</p>
     *           <p>Coordenadas finales del movimiento</p>
     * @return
     *           <p>A new GameMove object</p>
     *           <p>Un nuevo objeto GameMove</p>
     */
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

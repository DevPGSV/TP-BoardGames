package es.ucm.fdi.tp.assignment4.ataxx;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.AIAlgorithm;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.GameObserver;
import es.ucm.fdi.tp.basecode.bgame.model.GameRules;
import es.ucm.fdi.tp.basecode.bgame.model.Observable;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.bgame.views.GenericConsoleView;

public class AtaxxFactory implements GameFactory {
	
	private int dim;
	private int obstacles;

	public AtaxxFactory() {
		this(7);
	}

	public AtaxxFactory(int dim) {
		if (dim < 5) {
			throw new GameError("Dimension must be at least 3: " + dim);
		} else if (dim % 2 == 0) {
			throw new GameError("Dimension must be odd");
		} else {
			this.dim = dim;
		}
		this.obstacles = 4;
	}
	
	public void setNumberOfObstacles(int obstacles) {
		this.obstacles = obstacles;
	}

	@Override
	public GameRules gameRules() {
		return new AtaxxRules(dim, obstacles);
	}

	@Override
	public Player createConsolePlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player createRandomPlayer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Player createAIPlayer(AIAlgorithm alg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Piece> createDefaultPieces() {
		List<Piece> pieces = new ArrayList<Piece>();
		pieces.add(new Piece("X"));
		pieces.add(new Piece("O"));
		pieces.add(new Piece("R"));
		pieces.add(new Piece("B"));
		//pieces.add(AtaxxRules.getObstacle());
		return pieces;
	}

	@Override
	public void createConsoleView(Observable<GameObserver> game, Controller ctrl) {
		new GenericConsoleView(game, ctrl);
	}

	@Override
	public void createSwingView(Observable<GameObserver> game, Controller ctrl, Piece viewPiece, Player randPlayer, Player aiPlayer) {
		throw new UnsupportedOperationException("There is no swing view");
	}

}

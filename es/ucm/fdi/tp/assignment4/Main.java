package es.ucm.fdi.tp.assignment4;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import es.ucm.fdi.tp.assignment4.ataxx.AtaxxFactory;
import es.ucm.fdi.tp.basecode.attt.AdvancedTTTFactory;
import es.ucm.fdi.tp.basecode.bgame.control.ConsoleCtrl;
import es.ucm.fdi.tp.basecode.bgame.control.ConsoleCtrlMVC;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.AIAlgorithm;
import es.ucm.fdi.tp.basecode.bgame.model.Game;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.connectN.ConnectNFactory;
import es.ucm.fdi.tp.basecode.ttt.TicTacToeFactory;

/**
 * <p>This is the class with the main method for the board games application.
 * 
 * It uses the Commons-CLI library for parsing command-line arguments: the game
 * to play, the players list, etc.. More information is available at
 * {@link https://commons.apache.org/proper/commons-cli/}</p>
 * 
 * <p>Esta es la clase con el metodo main de inicio del programa. Se utiliza la
 * libreria Commons-CLI para leer argumentos de la linea de ordenes: el juego al
 * que se quiere jugar y la lista de jugadores. Puedes encontrar mas informaci√≥n
 * sobre esta libreria en {@link https://commons.apache.org/proper/commons-cli/}</p>
 * .
 */
public class Main {

    /**
     * <p>The possible views.</p>
     * <p>Vistas disponibles.</p>
     */
    enum ViewInfo {
        WINDOW("window", "Swing"),
        CONSOLE("console", "Console");

        private String id;
        private String desc;

        ViewInfo(String id, String desc) {
            this.id = id;
            this.desc = desc;
        }

        public String getId() {
            return id;
        }

        public String getDesc() {
            return desc;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    /**
     * <p>The available games.</p>
     * <p>Juegos disponibles.</p>
     */
    enum GameInfo {
        CONNECTN("cn", "ConnectN"),
        TicTacToe("ttt", "Tic-Tac-Toe"),
        AdvancedTicTacToe("attt", "Advanced Tic-Tac-Toe"),
        Ataxx("ataxx", "Ataxx");

        private String id;
        private String desc;

        GameInfo(String id, String desc) {
            this.id = id;
            this.desc = desc;
        }

        public String getId() {
            return id;
        }

        public String getDesc() {
            return desc;
        }

        @Override
        public String toString() {
            return id;
        }

    }

    /**
     * <p>Player modes (manual, random, etc.)</p>
     * <p>Modos de juego.</p>
     */
    enum PlayerMode {
        MANUAL("m", "Manual"), RANDOM("r", "Random"), AI("a", "Automatics");

        private String id;
        private String desc;

        PlayerMode(String id, String desc) {
            this.id = id;
            this.desc = desc;
        }

        public String getId() {
            return id;
        }

        public String getDesc() {
            return desc;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    /**
     * <p>Default game to play.</p>
     * <p>Juego por defecto.</p>
     */
    final private static GameInfo DEFAULT_GAME = GameInfo.Ataxx;

    /**
     * <p>default view to use.</p>
     * <p>Vista por defecto.</p>
     */
    final private static ViewInfo DEFAULT_VIEW = ViewInfo.CONSOLE;

    /**
     * <p>Default player mode to use.</p>
     * <p>Modo de juego por defecto.</p>
     */
    final private static PlayerMode DEFAULT_PLAYERMODE = PlayerMode.MANUAL;

    /**
     * <p>This field includes a game factory that is constructed after parsing the
     * command-line arguments. Depending on the game selected with the -g option
     * (by default {@link #DEFAULT_GAME}).</p>
     * 
     * <p>Este atributo incluye una factoria de juego que se crea despues de
     * extraer los argumentos de la linea de ordenes. Depende del juego
     * seleccionado con la opcion -g (por defecto, {@link #DEFAULT_GAME}).</p>
     */
    private static GameFactory gameFactory;

    /**
     * <p>List of pieces provided with the -p option, or taken from
     * {@link GameFactory#createDefaultPieces()} if this option was not
     * provided.</p>
     * 
     * <p>Lista de fichas proporcionadas con la opcion -p, u obtenidas de
     * {@link GameFactory#createDefaultPieces()} si no hay opcion -p.</p>
     */
    private static List<Piece> pieces;

    /**
     * <p>A list of players. The i-th player corresponds to the i-th piece in the
     * list {@link #pieces}. They correspond to what is provided in the -p
     * option (or using the default value {@link #DEFAULT_PLAYERMODE}).</p>
     * 
     * <p>Lista de jugadores. El jugador i-esimo corresponde con la ficha i-esima
     * de la lista {@link #pieces}. Esta lista contiene lo que se proporciona en
     * la opcion -p (o el valor por defecto {@link #DEFAULT_PLAYERMODE}).</p>
     */
    private static List<PlayerMode> playerModes;

    /**
     * <p>The view to use. Depending on the selected view using the -v option or
     * the default value {@link #DEFAULT_VIEW} if this option was not provided.</p>
     * 
     * <p>Vista a utilizar. Dependiendo de la vista seleccionada con la opcion -v o
     * el valor por defecto {@link #DEFAULT_VIEW} si el argumento -v no se</p>
     * proporciona.
     */
    private static ViewInfo view;

    /**
     * <p>{@code true} if the option -m was provided, to use a separate view for
     * each piece, and {@code false} otherwise.</p>
     * 
     * <p>{@code true} si se incluye la opcion -m, para utilizar una vista separada
     * por cada ficha, o {@code false} en caso contrario.</p>
     */
    private static boolean multiviews;

    /**
     * <p>Number of rows provided with the option -d ({@code null} if not
     * provided).</p>
     * 
     * <p>Numero de filas proporcionadas con la opcion -d, o {@code null} si no se
     * incluye la opcion -d.</p>
     */
    private static Integer dimRows;
    /**
     * <p>Number of columns provided with the option -d ({@code null} if not
     * provided).</p>
     * 
     * <p>Numero de columnas proporcionadas con la opcion -d, o {@code null} si no
     * se incluye la opcion -d.</p>
     * 
     */
    private static Integer dimCols;
    
    private static Integer numObstacles;

    /**
     * <p>The algorithm to be used by the automatic player. Not used so far, it is
     * always {@code null}.</p>
     * 
     * <p>Algoritmo a utilizar por el jugador automatico. Actualmente no se
     * utiliza, por lo que siempre es {@code null}.</p>
     */
    private static AIAlgorithm aiPlayerAlg;

    /**
     * <p>Processes the command-line arguments and modify the fields of this
     * class with corresponding values. E.g., the factory, the pieces, etc.</p>
     *
     * <p>Procesa la linea de ordenes del programa y crea los objetos necesarios
     * para los atributos de esta clase. Por ejemplo, la factoria, las fichas,
     * etc.</p>
     * 
     * 
     * @param args
     *            <p>Command line arguments.</p>
     * 
     *            <p>Lista de argumentos de la linea de ordenes.</p>
     * 
     * 
     */
    private static void parseArgs(String[] args) {

        // define the valid command line options
        //
        Options cmdLineOptions = new Options();
        cmdLineOptions.addOption(constructHelpOption()); // -h or --help
        cmdLineOptions.addOption(constructGameOption()); // -g or --game
        cmdLineOptions.addOption(constructViewOption()); // -v or --view
        cmdLineOptions.addOption(constructMlutiViewOption()); // -m or
                                                                // --multiviews
        cmdLineOptions.addOption(constructPlayersOption()); // -p or --players
        cmdLineOptions.addOption(constructDimensionOption()); // -d or --dim
        cmdLineOptions.addOption(constructObstaclesOption()); // -o or --obstacles
        // parse the command line as provided in args
        //
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(cmdLineOptions, args);
            parseHelpOption(line, cmdLineOptions);
            parseDimOptionn(line);
            parseObstaclesOptionn(line);
            parseGameOption(line);
            parseViewOption(line);
            parseMultiViewOption(line);
            parsePlayersOptions(line);

            // if there are some remaining arguments, then something wrong is
            // provided in the command line!
            //
            String[] remaining = line.getArgs();
            if (remaining.length > 0) {
                String error = "Illegal arguments:";
                for (String o : remaining)
                    error += (" " + o);
                throw new ParseException(error);
            }

        } catch (ParseException | GameError e) {
            // new Piece(...) might throw GameError exception
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }

    }

    /**
     * <p>Builds the multiview (-m or --multiviews) CLI option.</p>
     * 
     * <p>Construye la opcion CLI -m.</p>
     * 
     * @return
     *      <p>CLI {@link Option} for the multiview option.</p>
     */

    private static Option constructMlutiViewOption() {
        return new Option("m", "multiviews", false,
                "Create a separate view for each player (valid only when using the " + ViewInfo.WINDOW + " view)");
    }

    /**
     * <p>Parses the multiview option (-m or --multiview). It sets the value of
     * {@link #multiviews} accordingly.</p>
     * 
     * <p>Extrae la opcion multiview (-m) y asigna el valor de {@link #multiviews}.</p>
     * 
     * @param line
     *            <p>CLI {@link CommandLine} object.</p>
     */
    private static void parseMultiViewOption(CommandLine line) {
        multiviews = line.hasOption("m");
    }

    /**
     * <p>Builds the view (-v or --view) CLI option.</p>
     * 
     * <p>Construye la opcion CLI -v.</p>
     * 
     * @return 
     *         <p>CLI {@link Option} for the view option.</p>
     *         <p>Objeto {@link Option} de esta opcion.</p>
     */
    private static Option constructViewOption() {
        String optionInfo = "The view to use ( ";
        for (ViewInfo i : ViewInfo.values()) {
            optionInfo += i.getId() + " [for " + i.getDesc() + "] ";
        }
        optionInfo += "). By defualt, " + DEFAULT_VIEW.getId() + ".";
        Option opt = new Option("v", "view", true, optionInfo);
        opt.setArgName("view identifier");
        return opt;
    }

    /**
     * <p>Parses the view option (-v or --view). It sets the value of {@link #view}
     * accordingly.</p>
     * 
     * <p>Extrae la opcion view (-v) y asigna el valor de {@link #view}.</p>
     * 
     * @param line
     *            <p>CLI {@link CommandLine} object.</p>
     * @throws ParseException
     *             <p>If an invalid value is provided (the valid values are those
     *             of {@link ViewInfo}.</p>
     */
    private static void parseViewOption(CommandLine line) throws ParseException {
        String viewVal = line.getOptionValue("v", DEFAULT_VIEW.getId());
        // view type
        for (ViewInfo v : ViewInfo.values()) {
            if (viewVal.equals(v.getId())) {
                view = v;
            }
        }
        if (view == null) {
            throw new ParseException("Uknown view '" + viewVal + "'");
        }
    }

    /**
     * <p>Builds the players (-p or --player) CLI option.</p>
     * 
     * <p>Construye la opcion CLI -p.</p>
     * 
     * @return <p>CLI {@link Option} for the list of pieces/players.</p>
     *         <p> Objeto {@link Option} de esta opcion.</p>
     */
    private static Option constructPlayersOption() {
        String optionInfo = "A player has the form A:B (or A), where A is sequence of characters (without any whitespace) to be used for the piece identifier, and B is the player mode (";
        for (PlayerMode i : PlayerMode.values()) {
            optionInfo += i.getId() + " [for " + i.getDesc() + "] ";
        }
        optionInfo += "). If B is not given, the default mode '" + DEFAULT_PLAYERMODE.getId()
                + "' is used. If this option is not given a default list of pieces from the corresponding game is used, each assigmed the mode '"
                + DEFAULT_PLAYERMODE.getId() + "'.";

        Option opt = new Option("p", "players", true, optionInfo);
        opt.setArgName("list of players");
        return opt;
    }

    /**
     * <p>Parses the players/pieces option (-p or --players). It sets the value of
     * {@link #pieces} and {@link #playerModes} accordingly.</p>
     *
     * <p>Extrae la opcion players (-p) y asigna el valor de {@link #pieces} y
     * {@link #playerModes}.</p>
     * 
     * @param line
     *            <p>CLI {@link CommandLine} object.</p>
     * @throws ParseException
     *             <p>If an invalid value is provided (@see
     *             {@link #constructPlayersOption()}).</p>
     *             <p>Si se proporciona un valor invalido (@see
     *             {@link #constructPlayersOption()}).</p>
     */
    private static void parsePlayersOptions(CommandLine line) throws ParseException {

        String playersVal = line.getOptionValue("p");

        if (playersVal == null) {
            // if no -p option, we take the default pieces from the
            // corresponding
            // factory, and for each one we use the default player mode.
            pieces = gameFactory.createDefaultPieces();
            playerModes = new ArrayList<PlayerMode>();
            for (int i = 0; i < pieces.size(); i++) {
                playerModes.add(DEFAULT_PLAYERMODE);
            }
        } else {
            pieces = new ArrayList<Piece>();
            playerModes = new ArrayList<PlayerMode>();
            String[] players = playersVal.split(",");
            for (String player : players) {
                String[] playerInfo = player.split(":");
                if (playerInfo.length == 1) { // only the piece name is provided
                    pieces.add(new Piece(playerInfo[0]));
                    playerModes.add(DEFAULT_PLAYERMODE);
                } else if (playerInfo.length == 2) { // piece name and mode are
                                                        // provided
                    pieces.add(new Piece(playerInfo[0]));
                    PlayerMode selectedMode = null;
                    for (PlayerMode mode : PlayerMode.values()) {
                        if (mode.getId().equals(playerInfo[1])) {
                            selectedMode = mode;
                        }
                    }
                    if (selectedMode != null) {
                        playerModes.add(selectedMode);
                    } else {
                        throw new ParseException("Invalid player mode in '" + player + "'");
                    }
                } else {
                    throw new ParseException("Invalid player information '" + player + "'");
                }
            }
        }
    }

    /**
     * <p>Builds the game (-g or --game) CLI option.</p>
     * <p>Construye la opcion CLI -g.</p>
     * 
     * @return <p>CLI {@link {@link Option} for the game option.</p>
     *         <p>Objeto {@link Option} de esta opcion.</p>
     */

    private static Option constructGameOption() {
        String optionInfo = "The game to play ( ";
        for (GameInfo i : GameInfo.values()) {
            optionInfo += i.getId() + " [for " + i.getDesc() + "] ";
        }
        optionInfo += "). By defualt, " + DEFAULT_GAME.getId() + ".";
        Option opt = new Option("g", "game", true, optionInfo);
        opt.setArgName("game identifier");
        return opt;
    }
    
    
    /**
     * <p>Build the obstacles (-o or --obstacles) CLI option</p>
     * @return <p>CLI {@link {@link Option} for the obstacles option.</p>
     */
    private static Option constructObstaclesOption() {
        String optionInfo = "Number of obstacles for Ataxx game. By defualt, " + 4 + ".";
        Option opt = new Option("o", "obstacles", true, optionInfo);
        opt.setArgName("number of obstacles");
        return opt;
    }

    /**
     * <p>Parses the game option (-g or --game). It sets the value of
     * {@link #gameFactory} accordingly. Usually it requires that
     * {@link #parseDimOptionn(CommandLine)} has been called already to parse
     * the dimension option.</p>
     * 
     * <p>Extrae la opcion de juego (-g). Asigna el valor del atributo
     * {@link #gameFactory}. Normalmente necesita que se haya llamado antes a
     * {@link #parseDimOptionn(CommandLine)} para extraer la dimension del
     * tablero.</p>
     * 
     * @param line
     *            <p>CLI {@link CommandLine} object.</p>
     * @throws ParseException
     *             <p>If an invalid value is provided (the valid values are those
     *             of {@link GameInfo}).</p>
     *             <p>Si se proporciona un valor invalido (Los valores validos son
     *             los de {@link GameInfo}).</p>
     */
    private static void parseGameOption(CommandLine line) throws ParseException {
        String gameVal = line.getOptionValue("g", DEFAULT_GAME.getId());
        GameInfo selectedGame = null;

        for( GameInfo g : GameInfo.values() ) {
            if ( g.getId().equals(gameVal) ) {
                selectedGame = g;
                break;
            }
        }

        if ( selectedGame == null ) {
            throw new ParseException("Uknown game '" + gameVal + "'");
        }
    
        switch ( selectedGame ) {
        case AdvancedTicTacToe:
            gameFactory = new AdvancedTTTFactory();
            break;
        case CONNECTN:
            if (dimRows != null && dimCols != null && dimRows == dimCols) {
                gameFactory = new ConnectNFactory(dimRows);
            } else {
                gameFactory = new ConnectNFactory();
            }
            break;
        case TicTacToe:
            gameFactory = new TicTacToeFactory();
            break;
        case Ataxx:
            AtaxxFactory ataxxFactory;
            if (dimRows != null && dimCols != null && dimRows == dimCols) {
                ataxxFactory = new AtaxxFactory(dimRows);
            } else {
                ataxxFactory = new AtaxxFactory();
            }
            if (numObstacles != null) {
                ataxxFactory.setNumberOfObstacles(numObstacles);
            }
            
            gameFactory = ataxxFactory;
            break;
        default:
            throw new UnsupportedOperationException("Something went wrong! This program point should be unreachable!");
        }
    
    }

    /**
     * <p>Builds the dimension (-d or --dim) CLI option.</p>
     * 
     * <p>Construye la opcion CLI -d.</p>
     * 
     * @return <p>CLI {@link {@link Option} for the dimension.</p>
     *         <p>Objeto {@link Option} de esta opcion.</p>
     */
    private static Option constructDimensionOption() {
        return new Option("d", "dim", true,
                "The board size (if allowed by the selected game). It must has the form ROWSxCOLS.");
    }

    /**
     * <p>Parses the dimension option (-d or --dim). It sets the value of
     * {@link #dimRows} and {@link #dimCols} accordingly. The dimension is
     * ROWSxCOLS.</p>
     * 
     * <p>Extrae la opcion dimension (-d). Asigna el valor de los atributos
     * {@link #dimRows} and {@link #dimCols}. La dimension es de la forma
     * ROWSxCOLS.</p>
     * 
     * @param line
     *            <p>CLI {@link CommandLine} object.</p>
     * @throws ParseException
     *             <p>If an invalid value is provided.</p>
     *             <p>Si se proporciona un valor invalido.</p>
     */
    private static void parseDimOptionn(CommandLine line) throws ParseException {
        String dimVal = line.getOptionValue("d");
        if (dimVal != null) {
            try {
                String[] dim = dimVal.split("x");
                if (dim.length == 2) {
                    dimRows = Integer.parseInt(dim[0]);
                    dimCols = Integer.parseInt(dim[1]);
                } else {
                    throw new ParseException("Invalid dimension: " + dimVal);
                }
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid dimension: " + dimVal);
            }
        }

    }
    
    /**
     * <p>Parses the obstacles option (-o or --obstacles)</p>
     * @param line
     *            <p>CLI {@link CommandLine} object.</p>
     * @throws ParseException
     *             <p>If an invalid value is provided.</p>
     *             <p>Si se proporciona un valor invalido.</p>
     */
    private static void parseObstaclesOptionn(CommandLine line) throws ParseException {
        String obsVal = line.getOptionValue("o");
        if (obsVal != null) {
            try {
                numObstacles = Integer.parseInt(obsVal);
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid obstacles: " + obsVal);
            }
        }
    }

    /**
     * <p>Builds the help (-h or --help) CLI option.</p>
     * 
     * <p>Construye la opcion CLI -h.</p>
     * 
     * @return <p>CLI {@link {@link Option} for the help option.</p>
     *         <p>Objeto {@link Option} de esta opcion.</p>
     */

    private static Option constructHelpOption() {
        return new Option("h", "help", false, "Print this message");
    }

    /**
     * <p>Parses the help option (-h or --help). It print the usage information on
     * the standard output.</p>
     * 
     * <p>Extrae la opcion help (-h) que imprime informacion de uso del programa en
     * la salida estandar.</p>
     * 
     * @param line
     *            <p>CLI {@link CommandLine} object.</p>
     * @param cmdLineOptions
     *            <p>CLI {@link Options} object to print the usage information.</p>
     * 
     */
    private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
        if (line.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
            System.exit(0);
        }
    }

    /**
     * <p>Starts a game using a {@link ConsoleCtrl} which is not based on MVC. Is
     * used only for teaching the difference from the MVC one.</p>
     * 
     * <p>MÈtodo para iniciar un juego con el controlador {@link ConsoleCtrl}, no
     * basado en MVC. Solo se utiliza para mostrar las diferencias con el
     * controlador MVC.</p>
     * 
     */
    public static void startGameNoMVC() {
        Game g = new Game(gameFactory.gameRules());
        Controller c = null;

        switch (view) {
        case CONSOLE:
            ArrayList<Player> players = new ArrayList<Player>();
            for (int i = 0; i < pieces.size(); i++) {
                switch (playerModes.get(i)) {
                case AI:
                    players.add(gameFactory.createAIPlayer(aiPlayerAlg));
                    break;
                case MANUAL:
                    players.add(gameFactory.createConsolePlayer());
                    break;
                case RANDOM:
                    players.add(gameFactory.createRandomPlayer());
                    break;
                default:
                    throw new UnsupportedOperationException(
                            "Something went wrong! This program point should be unreachable!");
                }
            }
            c = new ConsoleCtrl(g, pieces, players, new Scanner(System.in));
            break;
        case WINDOW:
            throw new UnsupportedOperationException(
                    "Swing Views are not supported in startGameNoMVC!! Please use startGameMVC instead.");
        default:
            throw new UnsupportedOperationException("Something went wrong! This program point should be unreachable!");
        }

        c.start();
    }

    /**
     * <p>Starts a game. Should be called after {@link #parseArgs(String[])} so
     * some fields are set to their appropriate values.</p>
     * 
     * <p>
     * Inicia un juego. Debe llamarse despues de {@link #parseArgs(String[])}
     * para que los atributos tengan los valores correctos.</p>
     * 
     */
    public static void startGame() {
        Game g = new Game(gameFactory.gameRules());
        Controller c = null;

        switch (view) {
        case CONSOLE:
            ArrayList<Player> players = new ArrayList<Player>();
            for (int i = 0; i < pieces.size(); i++) {
                switch (playerModes.get(i)) {
                case AI:
                    players.add(gameFactory.createAIPlayer(aiPlayerAlg));
                    break;
                case MANUAL:
                    players.add(gameFactory.createConsolePlayer());
                    break;
                case RANDOM:
                    players.add(gameFactory.createRandomPlayer());
                    break;
                default:
                    throw new UnsupportedOperationException(
                            "Something went wrong! This program point should be unreachable!");
                }
            }
            c = new ConsoleCtrlMVC(g, pieces, players, new Scanner(System.in));
            gameFactory.createConsoleView(g, c);
            break;
        case WINDOW:
            throw new UnsupportedOperationException(
                    "Swing " + (multiviews ? "Multiviews" : "Views") + " are not supported yet! ");
        default:
            throw new UnsupportedOperationException("Something went wrong! This program point should be unreachable!");
        }

        c.start();
    }

    /**
     * <p>The main method. It calls {@link #parseArgs(String[])} and then
     * {@link #startGame()}.</p>
     * 
     * <p>Metodo main. Llama a {@link #parseArgs(String[])} y a continuacion inicia
     * un juego con {@link #startGame()}.</p>
     * 
     * @param args
     *            <p>Command-line arguments.</p>
     * 
     */
    public static void main(String[] args) {
        //String[] argsX = {};
        String[] argsX = {"-g", "ataxx"};
        //String[] argsX = {"-g", "ataxx", "-o", "999"};
        //String[] argsX = {"-g", "ataxx", "-o", "16"};
        //String[] argsX = {"-g", "ataxx", "-d", "5x5"};
        //String[] argsX = {"-g", "ataxx", "-d", "5x5", "-p", "W:r,B:m,R:r,Y:r"};
        parseArgs(argsX);
        startGame();
    }

}

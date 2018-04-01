import javafx.scene.layout.Pane;
import javafx.concurrent.Task;
import javafx.concurrent.Service;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class Game {
	public boolean turn;
	public static Pane pane;
	private Player localPlayer;
	private Player remotePlayer;
	private static Board board;
	static Service<String> listenOpponent;
	static Connection connection;

	public Game(Player localPlayer, Player remotePlayer, Connection connection, Pane pane) {
		this.pane = pane;
		this.localPlayer = localPlayer;
		this.remotePlayer = remotePlayer;
		this.turn = localPlayer.color;
		this.connection = connection;
	}

	public void init() {
		this.board = new Board(this, -250, -255, 500, 510, localPlayer.color);
		localPlayer.initPieces(board);
		remotePlayer.initPieces(board);

		if(turn != Player.LOCAL){
			opponentsMove();
		} else {
			AlertBox.display("title", "your turn " + localPlayer.id);
		}
	}

	public void opponentsMove() {
		turn = Player.REMOTE;
		listenOpponent = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() {
                    	try {
                            String line;
                            while ((line = connection.buffer.poll()) == null) {
                                Thread.sleep(250);
                            }
                            return line;
                        } catch (InterruptedException ex) {
                            return null;
                            //Logger.getLogger(LoginControl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
            }
        };
		listenOpponent.start();
		listenOpponent.setOnSucceeded(e -> {
			String line = listenOpponent.getValue();
			if(line.startsWith("Opponent Left") || line.startsWith("*** Server closed ***")) {
				AlertBox.display("Error", line);
				return;
			} else if(line.startsWith("Opponent")) {
				opponentsMove();
			}
			if(!line.startsWith("Play>>")) {
				AlertBox.display("Error", "Can't read opponent's move: " + line);
				return;
			}

			String move = line.substring(6);
			int pos0[] = new int[2];
			int pos1[] = new int[2];
			pos0[0] = Character.getNumericValue(move.charAt(0));
			pos0[1] = Character.getNumericValue(move.charAt(1));
			pos1[0] = Character.getNumericValue(move.charAt(2));
			pos1[1] = Character.getNumericValue(move.charAt(3));
			boolean promote = (move.charAt(4) == '1');
			boolean dead = (move.charAt(5) == '1');

			if(dead) {
				Piece p = remotePlayer.sideBoard.getPiece(pos0[0]);
				board.drop(p, pos1);
			} else {
				Piece p0 = board.getPiece(pos0);
				Piece p1 = board.getPiece(pos1);
				if(p1 == null) {
					board.movePiece(pos0, pos1);
				} else {
					board.capture(p0, p1);
				}

				if(promote)
					p0.promote();
			}
			//decode move
			turn = Player.LOCAL;
		});
	}

	public void sendMove(String move) {
		move = "Play>>" + move;
		connection.send(move);
	}
}
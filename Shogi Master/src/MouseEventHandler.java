import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public abstract class MouseEventHandler implements EventHandler<MouseEvent> {
	static Board board;
	static Piece selectedPiece;
	static boolean selection;
    static Game game;

	public MouseEventHandler(Board board) {
		selectedPiece = null;
		selection = false;
		this.board = board;
	}

    public MouseEventHandler(Board board, Game game) {
        selectedPiece = null;
        selection = false;
        this.board = board;
        this.game = game;
    }

    public void selectPiece(Piece p) {
        p.selectedEffect();
        selection = true;
        selectedPiece = p;
    }

    public void unselectPiece() {
        selectedPiece.removeEffect();
        selection = false;
        selectedPiece = null;
    }

    protected static void finish(String move) {
        if(move == null)
            return;
        
        game.turn = Player.REMOTE;
        game.opponentsMove();
        game.sendMove(move);
    }
}
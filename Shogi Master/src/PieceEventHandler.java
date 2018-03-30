import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class PieceEventHandler implements EventHandler<MouseEvent> {
	Piece piece;
	Board board;

	public PieceEventHandler(Board board, Piece piece) {
		this.piece = piece;
		this.board = board;
	}

	@Override
	public void handle(MouseEvent mouseEvent) {
		if(mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
        	if(board.selectedSquare == null) {
        		board.selectedSquare = piece.getPos();
        		piece.selectedEffect();
        		//board.attackEffect(piece.getPos());
        	} else if(true) { //if the move if valid
        		board.movePiece(board.selectedSquare, piece.getPos());
        		board.selectedSquare = null;
        		piece.removeEffect();
        	}
        	else {
        		board.selectedSquare = null;
        		piece.removeEffect();
        	}
		}
	}
}

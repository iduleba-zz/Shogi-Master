import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class BoardEventHandler implements EventHandler<MouseEvent> {
	Board board;

	public BoardEventHandler(Board board) {
		this.board = board;
	}

	@Override
    public void handle(MouseEvent mouseEvent) {
        if(mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
        	int[] pos = board.getPos(mouseEvent.getX(), mouseEvent.getY());
        	if(board.selectedSquare == null) {
        		if(board.getPiece(pos) != null) {
        			board.selectedSquare = pos;
        			board.getPiece(pos).dangerEffect();
        		}
        		else
        			return;
        	} else if(true) { //if the move if valid
        		board.movePiece(board.selectedSquare, pos);
        		board.selectedSquare = null;
        		board.getPiece(pos).removeEffect();
        	}
        	else { //unselect
        		board.selectedSquare = null;
        		board.getPiece(pos).removeEffect();
        	}
		}
	}
}
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class PieceEventHandler extends MouseEventHandler {
	Piece piece;

	public PieceEventHandler(Board board, Piece piece) {
        super(board);
        this.piece = piece;
	}

	@Override
	public void handle(MouseEvent mouseEvent) {
        if(game.turn != Player.LOCAL){
            mouseEvent.consume();
            return;
        }
        
        String move = null;
		if(mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
            if(piece.getPlayer().location == Player.LOCAL){
                if(selection) {
                    if(selectedPiece == piece){
                        unselectPiece();
                        return;
                    }
                    unselectPiece();
                    selectPiece(piece);
                } else {
                    selectPiece(piece);
                }
            } else if(selection) {
                if(selectedPiece.dead == false){                    
                    move = board.capture(selectedPiece, piece);
                }
   
                unselectPiece();
            }
		}

        finish(move);
	}
}


import javafx.scene.input.MouseEvent;

public class BoardEventHandler extends MouseEventHandler {

    public BoardEventHandler(Board board, Game game) {
        super(board, game);
    }

    @Override
    public void handle(MouseEvent mouseEvent) {
        
        if(game.turn != Player.LOCAL){
            mouseEvent.consume();
            return;
        }
        
        String move = null;
        if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
            int[] pos = board.getPos(mouseEvent.getX(), mouseEvent.getY());
            Piece piece = board.getPiece(pos);
            //if some piece is selected
            if(selection) {
                if(piece == null) {
                    //if the selected piece is on the board
                    if(selectedPiece.dead == false) {
                        move = board.movePiece(selectedPiece.getPos(), pos);
                        unselectPiece();
                    } else { //if it's a dead piece
                        move = board.drop(selectedPiece, pos);
                        unselectPiece();
                    }
                } else if(piece.getPlayer().location == Player.LOCAL) {
                    if(selectedPiece == piece) {
                        unselectPiece();
                        return;
                    }
                    unselectPiece();
                    selectPiece(piece);
                } else {
                    //takes piece
                    if(selectedPiece.dead == false){
                        move = board.capture(selectedPiece, piece);
                    }
   
                    unselectPiece();                    
                }
            } else if(piece != null) { //if there is a not selected piece in the square
                if(piece.getPlayer().location == Player.LOCAL) {
                    selectPiece(piece);           
                }
            }
        }

        finish(move);
    }
}

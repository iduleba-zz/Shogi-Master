import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;

public class MouseEventHandler implements EventHandler<MouseEvent> {
	Board board;
	Piece selectedPiece;
	boolean selection;

	//Need to implement class Game and add to Handler
	public MouseEventHandler(Board board) {
		selectedPiece = null;
		selection = false;
		this.board = board;
	}

	@Override
    public void handle(MouseEvent mouseEvent) {
        if(mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED) {
        	//Clicked on the board
        	if(mouseEvent.getSource() instanceof Rectangle) {
        		

        	//Clicked on some piece
        	} else if(mouseEvent.getSource() instanceof Piece) {
        		Piece src = (Piece) mouseEvent.getSource();
        		//If it's the player's piece. STILL NEED TO CHECK IF IT'S THE LOCAL PLAYER TURN
        		if(src.getPlayer().location == Player.LOCAL) {
        			if(selection == true) {
        				selectedPiece.removeEffect();
        			}
        			selection = true;
        			selectedPiece = src;
        			src.selectedEffect();
        		} else {
        			//GAME LOGIC

        		}
        	}
        }
	}
}
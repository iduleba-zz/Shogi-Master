import java.util.LinkedList;
import java.util.ListIterator;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.paint.Color;

public class SideBoard {
	private int N; //N square is the capacity of the board
	private double width;
	private double height;
	private double pos_x;
	private double pos_y;
	private Pane pane;
	private final LinkedList<Piece> sidePieces;

	public SideBoard(Pane pane, double pos_x, double pos_y, double width) {
		this.N = 3;
		this.width = width;
		this.height = width*1.0833;
		this.pos_x = pos_x;
		this.pos_y = pos_y; 
		this.pane = pane;
		this.sidePieces = new LinkedList<Piece>();
		draw();
	}

	private void draw() {
		Rectangle board = new Rectangle();
		board.setX(pos_x);
		board.setY(pos_y);
		board.setWidth(width);
		board.setHeight(height);
		board.setFill(Color.rgb(232, 212, 153));
		this.pane.getChildren().add(board);
	}
	
	public void addPiece(Piece p) {
		if(sidePieces.size() < this.N * this.N) {
			updatePiecePos(p, sidePieces.size());
			p.dead = true;
			sidePieces.add(p);
		} else {
			this.N++;
			for(Piece piece: this.sidePieces) {
				updatePiecePos(piece, sidePieces.size());
			}
			addPiece(p);
		}
	}

	//TO-DO
	public void removePiece(Piece p) {
		int k = sidePieces.indexOf(p);
		sidePieces.remove(p);

		ListIterator<Piece> itr = sidePieces.listIterator(k);
		while(itr.hasNext()) {
			Piece piece = itr.next();
			updatePiecePos(piece, k);
			k++;
		}

		sidePieces.remove(p);

	}

	public double hSpacing() {
		return this.width / N;
	}

	public double vSpacing() {
		return this.height / N;
	}

	public double[] getCoordinates(int k) {
		double[] coord = new double[2];
		int i = k % this.N;
		int j = k / this.N;
		coord[0] = i*hSpacing() + hSpacing()/2;
		coord[1] = j*vSpacing() + vSpacing()/2;
		return coord;
	}

	public void updatePiecePos(Piece p, int k) {
		p.updateImgPos(getCoordinates(k)[0], 
				        getCoordinates(k)[1], 
				        hSpacing(), 
				        vSpacing());
		int i = k % this.N;
		int j = k / this.N;
		p.setPos(i, j);
	}
}
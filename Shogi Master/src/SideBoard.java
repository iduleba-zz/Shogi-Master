import java.util.LinkedList;
import java.util.ListIterator;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.scene.paint.Color;
import java.util.PriorityQueue;

public class SideBoard {
	private int N; //N square is the capacity of the board
	private double width;
	private double height;
	private double pos_x;
	private double pos_y;
	private Pane pane;
	private final LinkedList<Piece> sidePieces;
	private final PriorityQueue<Integer> freePositions;

	public SideBoard(Pane pane, double pos_x, double pos_y, double width) {
		freePositions = new PriorityQueue<>();
		for(int i = 0; i < 40; i++) {
			freePositions.offer(i);
		}

		this.N = 3;
		this.width = width;
		this.height = width*1.0833;
		this.pos_x = pos_x;
		this.pos_y = pos_y; 
		this.pane = pane;
		this.sidePieces = new LinkedList<Piece>();
		init();
	}

	private void init() {
		Rectangle board = new Rectangle();
		board.setX(pos_x);
		board.setY(pos_y);
		board.setWidth(width);
		board.setHeight(height);
		board.setFill(Color.rgb(232, 212, 153));
		this.pane.getChildren().add(board);
	}
	
	public void addPiece(Piece p) {
		if(sidePieces.size() < (this.N * this.N - 1)) {
			updatePiecePos(p, (int) freePositions.poll());
			p.dead = true;
			sidePieces.add(p);
		} else {
			this.N++;
			for(Piece piece: this.sidePieces) {
				updatePiecePos(piece, piece.getPos()[0]);
			}
			addPiece(p);
		}
	}

	public void removePiece(Piece p) {
		p.dead = false;
		sidePieces.remove(p);
		int pos = p.getPos()[0];
		freePositions.offer(pos);
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
		coord[0] = pos_x + i*hSpacing() + hSpacing()/2;
		coord[1] = pos_y + j*vSpacing() + vSpacing()/2;
		return coord;
	}

	public void updatePiecePos(Piece p, int k) {
		p.updateImgPos(getCoordinates(k)[0], 
				        getCoordinates(k)[1], 
				        hSpacing(), 
				        vSpacing());
		p.setPos(k, k);
	}

	public int getPiecePos(Piece p) {
		return p.getPos()[0];
	}

	public Piece getPiece(int k) {
		for(Piece p: sidePieces) {
			if(p.getPos()[0] == k)
				return p;
		}

		return null;
	}
}
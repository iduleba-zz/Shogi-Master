import java.util.LinkedList;

public class Player {
	public static final boolean LOCAL = true;
	public static final boolean REMOTE = false;
	public static final boolean BLACK = true;
	public static final boolean WHITE = false;

	private final LinkedList<Piece> boardPieces;
	private SideBoard sideBoard;
	public boolean color; 
	public boolean location;
	public String id;

	Player(String id, boolean color, boolean location) {
		this.id = id;
		this.color = color;
		this.location = location;
		boardPieces = new LinkedList<Piece>();
		addPieces();	
	}

	public void initPieces(Board board) {
		//init sideboard
		double x, y;
		if(location == LOCAL) {
			x = board.pos_x + board.width + 0.2*board.hSpacing();
			y = board.pos_y + board.height - 3*1.0833*board.hSpacing();
		} else {
			x = board.pos_x - 3.2*board.hSpacing();
			y = board.pos_y;
		}
		sideBoard = new SideBoard(board.getPane(), x, y, 3*board.hSpacing());


		for(Piece p: boardPieces) {
			p.init(board);
		}
	}

	private void addPieces() {
		int y = (this.color == BLACK) ? 7 : 3;
		//add the pawns
		for(int i = 0; i < 9; i++) {
			boardPieces.add(new Piece("P", this, i+1, y));
		}

		//add bishop and rook
		if(this.color == BLACK){
			boardPieces.add(new Piece("B", this, 8, 8));
			boardPieces.add(new Piece("R", this, 2, 8));
		} else {
			boardPieces.add(new Piece("B", this, 2, 2));
			boardPieces.add(new Piece("R", this, 8, 2));
		}

		y = (this.color == BLACK) ? 9 : 1;
		//add other pieces
		boardPieces.add(new Piece("K", this, 5, y));
		boardPieces.add(new Piece("G", this, 4, y));
		boardPieces.add(new Piece("G", this, 6, y));
		boardPieces.add(new Piece("S", this, 3, y));
		boardPieces.add(new Piece("S", this, 7, y));
		boardPieces.add(new Piece("N", this, 2, y));
		boardPieces.add(new Piece("N", this, 8, y));
		boardPieces.add(new Piece("L", this, 1, y));
		boardPieces.add(new Piece("L", this, 9, y));
	}
}
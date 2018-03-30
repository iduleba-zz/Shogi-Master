import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.*;

public class Board {
	public double width;
	public double height;
	public double pos_x;
	public double pos_y;
	private Pane pane;
	private boolean orientation; //true only if local player is black
	private Piece[][] square;
	public int[] selectedSquare = null;
	private GraphicsContext gc;

	Board(Pane pane, double pos_x, double pos_y, double width, double height, boolean orientation) {
		this.orientation = orientation;
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.width = width;
		this.height = height;
		this.pane = pane;
		this.square = new Piece[9][9];
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				square[i][j] = null;
			}
		}		
		init();
	}

	private void init() {
		Rectangle board = new Rectangle();
		board.setX(pos_x);
		board.setY(pos_y);
		board.setWidth(width);
		board.setHeight(height);
		board.setFill(Color.rgb(232, 212, 153));
		board.addEventHandler(MouseEvent.ANY, new BoardEventHandler(this));
		this.pane.getChildren().add(board);

		Canvas canvas = new Canvas(width, height);
		canvas.setMouseTransparent(true);
		canvas.setLayoutX(pos_x);
		canvas.setLayoutY(pos_y);
		this.gc = canvas.getGraphicsContext2D();
		this.pane.getChildren().add(canvas);

		Line[] hLines = new Line[10];
		Line[] vLines = new Line[10];

		double hSpacing = width / 9.0;
		double vSpacing = height / 9.0;
		for(int i = 0; i < 10; i++) {
			hLines[i] = new Line(pos_x, pos_y + (i)*vSpacing, pos_x + width, pos_y + (i)*vSpacing);
			this.pane.getChildren().add(hLines[i]);

			vLines[i] = new Line(pos_x + (i)*hSpacing, pos_y, pos_x + (i)*hSpacing, pos_y + height);
			this.pane.getChildren().add(vLines[i]);
		}

		hSpacing = 3*hSpacing;
		vSpacing = 3*vSpacing;
		Circle[][] circles = new Circle[2][2];
		for(int i = 0; i < 2; i++) {
			for(int j = 0; j < 2; j++) {
				circles[i][j] = new Circle(pos_x + (j+1)*hSpacing, pos_y + (i+1)*vSpacing, width/150, Color.BLACK);
				this.pane.getChildren().add(circles[i][j]);
			}
		}
	}

	//i must be between 1 and 9
	//index increases from left to right
	public double getX(int i) {
		if(orientation)
			i = 10 - i;
		return pos_x + (i-1)*(width/9.0) + (width/18.0);
	}

	//i must be between 1 and 9
	//index inscreases from bottom to top
	public double getY(int i) {
		if(orientation)
			i = 10 - i;
		return pos_y + height - (i-1)*(height/9.0) - (height/18.0);
	}

	public int[] getPos(double x, double y) {
		int[] pos = new int[2];
		x = x - pos_x;
		y = y - pos_y;
		pos[0] = 9 - (int) (x / hSpacing());
		pos[1] = 1 + (int) (y / vSpacing());

		if(!orientation) {
			pos[0] = 10 - pos[0];
			pos[1] = 10 - pos[1];
		}

		return pos;
	}

	public double vSpacing() {
		return height/9.0;
	} 

	public double hSpacing() {
		return width/9.0;
	}

	public Pane getPane() {
		return this.pane;
	}

	public void setPiece(Piece piece, int[] pos) {
		square[pos[0]-1][pos[1]-1] = piece;
		if(piece == null)
			return;
		piece.updatePos(pos, this);
	}

	public void removePiece(int[] pos) {
		Piece p = getPiece(pos);
		setPiece(null, pos);
		if(p != null) {
			int[] new_pos = {-1 , -1};
			p.updatePos(pos, this);
		}
	}

	public Piece getPiece(int[] pos) {
		return square[pos[0]-1][pos[1]-1];
	}

	public void movePiece(int[] pos0, int[] pos1) {
		if(getPiece(pos1) != null)
			return;
		Piece p = getPiece(pos0);
		setPiece(null, pos0);
		setPiece(p, pos1);
	}

	public void drawCircle(double x, double y, double r, Color color) {
		x += this.width/2;
		y += this.height/2; 
		gc.setFill(color);
        gc.fillOval(x - r, y - r, 2*r, 2*r);
	}

	public void drawRect(double x, double y, double w, double h, Color color) {
		x += this.width/2;
		y += this.height/2; 
		gc.setFill(color);
		gc.fillRect(x, y, w, h);
	}

	public void circleEffect(int[] pos) {
		double x = getX(pos[0]);
		double y = getY(pos[1]);

		drawCircle(x - hSpacing()/2, y - vSpacing()/2, 8.0, Color.rgb(163, 148, 114));
	}

	//to do
	public void attackEffect(int[] pos) {
		double x = getX(pos[0]);
		double y = getY(pos[1]);

		drawRect(x - hSpacing()/2, y - vSpacing()/2, hSpacing(), vSpacing(), Color.rgb(185, 122, 87));
	}

	public String toString() {
		return "BOARD";
	}
}
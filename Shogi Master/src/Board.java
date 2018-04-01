
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.input.MouseEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Board {

    public double width;
    public double height;
    public double pos_x;
    public double pos_y;
    private boolean orientation; //true only if local player is black
    private Piece[][] square;
    public int[] selectedSquare = null;
    private GraphicsContext gc;
    static Game game;

    Board(Game game, double pos_x, double pos_y, double width, double height, boolean orientation) {
        this.orientation = orientation;
        this.pos_x = pos_x;
        this.pos_y = pos_y;
        this.width = width;
        this.height = height;
        this.game = game;
        this.square = new Piece[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                square[i][j] = null;
            }
        }


        init();
    }

    //--------------------------- GUI -------------------------------------------------------------------------------
    private void init() {
        Rectangle board = new Rectangle();
        board.setX(pos_x);
        board.setY(pos_y);
        board.setWidth(width);
        board.setHeight(height);
        board.setFill(Color.rgb(232, 212, 153));
        board.addEventHandler(MouseEvent.ANY, new BoardEventHandler(this, game));
        Game.pane.getChildren().add(board);

        Canvas canvas = new Canvas(width, height);
        canvas.setMouseTransparent(true);
        canvas.setLayoutX(pos_x);
        canvas.setLayoutY(pos_y);
        this.gc = canvas.getGraphicsContext2D();
        Game.pane.getChildren().add(canvas);

        Line[] hLines = new Line[10];
        Line[] vLines = new Line[10];

        double hSpacing = width / 9.0;
        double vSpacing = height / 9.0;
        for (int i = 0; i < 10; i++) {
            hLines[i] = new Line(pos_x, pos_y + (i) * vSpacing, pos_x + width, pos_y + (i) * vSpacing);
            Game.pane.getChildren().add(hLines[i]);

            vLines[i] = new Line(pos_x + (i) * hSpacing, pos_y, pos_x + (i) * hSpacing, pos_y + height);
            Game.pane.getChildren().add(vLines[i]);
        }

        hSpacing = 3 * hSpacing;
        vSpacing = 3 * vSpacing;
        Circle[][] circles = new Circle[2][2];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                circles[i][j] = new Circle(pos_x + (j + 1) * hSpacing, pos_y + (i + 1) * vSpacing, width / 150, Color.BLACK);
                circles[i][j].setMouseTransparent(true);
                Game.pane.getChildren().add(circles[i][j]);
            }
        }
    }

    //i must be between 1 and 9
    //index increases from left to right
    public double getX(int i) {
        if (orientation) {
            i = 10 - i;
        }
        return pos_x + (i - 1) * (width / 9.0) + (width / 18.0);
    }

    //i must be between 1 and 9
    //index inscreases from bottom to top
    public double getY(int i) {
        if (orientation) {
            i = 10 - i;
        }
        return pos_y + height - (i - 1) * (height / 9.0) - (height / 18.0);
    }

    public int[] getPos(double x, double y) {
        int[] pos = new int[2];
        x = x - pos_x;
        y = y - pos_y;
        pos[0] = 9 - (int) (x / hSpacing());
        pos[1] = 1 + (int) (y / vSpacing());

        if (!orientation) {
            pos[0] = 10 - pos[0];
            pos[1] = 10 - pos[1];
        }

        return pos;
    }

    public double vSpacing() {
        return height / 9.0;
    }

    public double hSpacing() {
        return width / 9.0;
    }

    public Pane getPane() {
        return Game.pane;
    }
    //-----------------------------------------------------------------------------------------------------------

    //------------- EFFECTS -------------------------------------------------------------------------------------
    public void drawCircle(double x, double y, double r, Color color) {
        x += this.width / 2;
        y += this.height / 2;
        gc.setFill(color);
        gc.fillOval(x - r, y - r, 2 * r, 2 * r);
    }

    public void drawRect(double x, double y, double w, double h, Color color) {
        x += this.width / 2;
        y += this.height / 2;
        gc.setFill(color);
        gc.fillRect(x, y, w, h);
    }

    public void circleEffect(int[] pos) {
        double x = getX(pos[0]);
        double y = getY(pos[1]);

        drawCircle(x - hSpacing() / 2, y - vSpacing() / 2, 8.0, Color.rgb(163, 148, 114));
    }

    //to do
    public void attackEffect(int[] pos) {
        double x = getX(pos[0]);
        double y = getY(pos[1]);

        drawRect(x - hSpacing() / 2, y - vSpacing() / 2, hSpacing(), vSpacing(), Color.rgb(185, 122, 87));
    }
    //---------------------------------------------------------------------------------------------------------

    public void setPiece(Piece piece, int[] pos) {
        square[pos[0] - 1][pos[1] - 1] = piece;
        if (piece == null) {
            return;
        }
        piece.updatePos(pos, this);
    }

    public void removePiece(int[] pos) {
        setPiece(null, pos);
    }

    public Piece getPiece(int[] pos) {
        return square[pos[0] - 1][pos[1] - 1];
    }

    //------------- MOVES ----------------------------------------------------------------------------------------
    public String movePiece(int[] pos0, int[] pos1) {
        if (getPiece(pos1) != null) {
            return null;
        }
        Piece p = getPiece(pos0);
        setPiece(null, pos0);
        setPiece(p, pos1);

        boolean promote = false;
        if(p.getPlayer().color == Player.BLACK) {
            if(pos1[1] <= 3)
                promote = ConfirmBox.display("Promotion", "Do you want to promote the piece?");
        } else if(pos1[1] >= 7) {
            promote = ConfirmBox.display("Promotion", "Do you want to promote the piece?");
        }
        if(promote)
            p.promote();

        String move = "";
        move = move + Integer.toString(pos0[0]) + Integer.toString(pos0[1]);
        move = move + Integer.toString(pos1[0]) + Integer.toString(pos1[1]);
        move = move + (promote ? "1" : "0");
        move = move + "0";
        return move;
    }

    public String capture(Piece piece0, Piece piece1) {
        int[] pos0 = piece0.getPos().clone();
        int[] pos1 = piece1.getPos().clone();
        Player player0 = piece0.getPlayer();
        Player player1 = piece1.getPlayer();
        //piece 1 now belongs to player 0
        player1.boardPieces.remove(piece1);
        piece1.setPlayer(player0);

        //player 0 unpromote and removes piece1 from board, and adds to sideBoard
        piece1.unpromote();
        removePiece(pos1);
        player0.sideBoard.addPiece(piece1);

        //player 0 moves piece0 to pos1
        movePiece(pos0, pos1);
        boolean promote = false;
        if(piece0.getPlayer().color == Player.BLACK){
            if(pos1[1] <= 3)
                promote = ConfirmBox.display("Promotion", "Do you want to promote the piece?");
        } else if(pos1[1] >= 7) {
            promote = ConfirmBox.display("Promotion", "Do you want to promote the piece?");
        }
        if(promote)
            piece0.promote();
        
        String move = "";
        move = move + Integer.toString(pos0[0]) + Integer.toString(pos0[1]);
        move = move + Integer.toString(pos1[0]) + Integer.toString(pos1[1]);
        move = move + (promote ? "1" : "0");
        move = move + "0";
        return move;
    }

    public String drop(Piece piece, int[] pos) {
        //remove piece from dead pieces
        String move = "";
        move = move + Integer.toString(piece.getPos()[0]) + Integer.toString(piece.getPos()[1]);
        move = move + Integer.toString(pos[0]) + Integer.toString(pos[1]);
        move = move + "0";
        move = move + "1";

        piece.dead = false;
        piece.getPlayer().sideBoard.removePiece(piece);
        piece.getPlayer().boardPieces.add(piece);
        setPiece(piece, pos);

        return move;
    }

    //-----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "BOARD";
    }
}

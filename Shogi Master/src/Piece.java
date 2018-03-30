import java.util.HashMap;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.input.ClipboardContent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class Piece {
	private static HashMap<String, String> url = new HashMap<String, String>();
	static {
		url.put("P", "imgs\\PAWN.png");
		url.put("+P", "imgs\\P_PAWN.png");
		url.put("L", "imgs\\LANCE.png");
		url.put("+L", "imgs\\P_LANCE.png");
		url.put("N", "imgs\\KNIGHT.png");
		url.put("+N", "imgs\\P_KNIGHT.png");
		url.put("S", "imgs\\SILVER.png");
		url.put("+S", "imgs\\P_SILVER.png");
		url.put("G", "imgs\\GOLD.png");
		url.put("B", "imgs\\BISHOP.png");
		url.put("+B", "imgs\\P_BISHOP.png");
		url.put("R", "imgs\\ROOK.png");
		url.put("+R", "imgs\\P_ROOK.png");
		url.put("K", "imgs\\KING.png");
	}

	private int[] pos;
	public String id;
	private Image img;
	private ImageView imgV;
	private Player player;
	public boolean dead = false;

	Piece(String id, Player player, int x, int y) {
		this.pos = new int[2];
		this.pos[0] = x;
		this.pos[1] = y;
		this.player = player;
		this.id = id;
		try{
			this.img = new Image(new FileInputStream(url.get(id)));
		} catch(FileNotFoundException e) {
			System.err.println(e.getMessage());
		}		
		this.imgV = new ImageView(this.img);
		this.imgV.setPreserveRatio(true);
      	this.imgV.setSmooth(true);
      	if(this.player.location == Player.REMOTE) 
      		this.imgV.setRotate(180);
	}

	public void init(Board board) {
      	this.imgV.addEventHandler(MouseEvent.ANY, new PieceEventHandler(board, this));
		board.setPiece(this, pos);      	
		board.getPane().getChildren().add(imgV);
	}

	public Player getPlayer() {
		return this.player;
	}

	public void updateImgPos(double x, double y, double hSpacing, double vSpacing) {
		this.imgV.setFitHeight(vSpacing); 
      	this.imgV.setFitWidth(hSpacing);

		int w = (int) this.img.getWidth();
		int h = (int) this.img.getHeight();
		double final_w, final_h;
		if(w * vSpacing >= hSpacing * h) {
			final_w = hSpacing;
			final_h = hSpacing * ((double) h) / w;
		} else {
			final_h = vSpacing;
			final_w = vSpacing * ((double) w) / h;
		}

		if(this.player.location == Player.REMOTE) 
      		this.imgV.setRotate(180);

		this.imgV.setX(x - final_w/2);
		this.imgV.setY(y - final_h/2);		
	}

	public void updatePos(int[] pos, Board board) {
		this.pos = pos;
		updateImgPos(board.getX(pos[0]), board.getY(pos[1]), board.hSpacing(), board.vSpacing());
	}

	public void setPos(int i, int j) {
		this.pos[0] = i;
		this.pos[1] = j;
	}

	public void selectedEffect() {
		DropShadow ds = new DropShadow();
		ds.setRadius(10.0);
		ds.setOffsetX(0);
		ds.setOffsetY(0);
		ds.setColor(Color.color(0.2, 0.3, 0.3));

		imgV.setEffect(ds);
	}

	public void dangerEffect() {
		DropShadow ds = new DropShadow();
		ds.setRadius(30);
		ds.setOffsetX(0);
		ds.setOffsetY(0);
		ds.setColor(Color.rgb(200, 0, 0));

		imgV.setEffect(ds);
	}

	public void removeEffect() {
		imgV.setEffect(null);
	}

	public int[] getPos() {
		return this.pos;
	}

	public String toString() {
		return this.id;
	}

	public Node getNode() {
		return this.imgV;
	}

	public void promote() {
		this.id = "+" + this.id;
		updateImg();
	}

	public void updateImg() {
		try{
			this.img = new Image(new FileInputStream(url.get(this.id)));
		} catch(FileNotFoundException e) {
			System.err.println(e.getMessage());
		}		
		this.imgV = new ImageView(this.img);
		this.imgV.setPreserveRatio(true);
      	this.imgV.setSmooth(true);
      	if(this.player.location == Player.REMOTE) 
      		this.imgV.setRotate(180);
	}
}
package main.game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class MapFrag {

    private final Game GAME;
    private int x;
    private int y;
    private int screenX;
    private int screenY;
    private BufferedImage sprite;
    private boolean recup;

    protected MapFrag(Game game, int x, int y) {
        this.GAME = game;
        try {sprite = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/map_frag.png")));
        } catch (IOException e) {throw new RuntimeException(e);}
        this.x = x * GAME.getTILE_SIZE() + GAME.getTILE_SIZE() / 4;
        this.y = y * GAME.getTILE_SIZE() + GAME.getTILE_SIZE() / 4;
        recup = false;
    }


    protected void draw(Graphics g2) {

        screenX = x - GAME.getCAM().getX() + GAME.getCAM().getScreenX(); // set X écran
        screenY = y - GAME.getCAM().getY() + GAME.getCAM().getScreenY(); // set Y écran

        if (x + GAME.getTILE_SIZE() > GAME.getCAM().getX() - GAME.getCAM().getScreenX() &&
                x - GAME.getTILE_SIZE() < GAME.getCAM().getX() + GAME.getCAM().getScreenX() &&
                y + GAME.getTILE_SIZE() > GAME.getCAM().getY() - GAME.getCAM().getScreenY() &&
                y - GAME.getTILE_SIZE() < GAME.getCAM().getY() + GAME.getCAM().getScreenY()) {

            g2.drawImage(sprite, screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);

        }
    }

    protected int getX() {return x;}
    protected int getY() {return y;}
    protected boolean getRecup() {return recup;}

    protected void setRecup(boolean recup) {this.recup = recup;}

}

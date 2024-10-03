package main.game;

import java.util.Random;

public class Camera {

    private final Game GAME;

    private int x; // X monde
    private int y; // Y monde
    private int screenX; // X écran
    private int screenY; // Y écran
    // auto Move
    private final int defaultVectX;
    private final int defaultVectY;
    private int vectX;
    private int vectY;
    private boolean auto;
    private Ennemy ennemy;

    protected Camera(Game game) {
        this.GAME = game; // récupère le jeu
        screenX = GAME.getSCREEN_WIDTH()/2; // X écran
        screenY = GAME.getSCREEN_HEIGHT()/2; // Y écran
        // n'a pas d'importence
        x= 1000;
        y= 1000;
        // auto move
        Random rand = new Random();
        defaultVectX = rand.nextInt(5, 10);
        defaultVectY = rand.nextInt(5, 10);
        vectX = defaultVectX;
        vectY = defaultVectY;
        auto = false;
    }

    protected void autoMove(int death) {

        if (!auto) {
            x = GAME.getWORLD_SIZE()/2;
            y = GAME.getWORLD_SIZE()/2;
            auto = true;
            if (death == 1) for (Ennemy e : GAME.getENNEMYS()) if (e.getHasKilled()) this.ennemy = e;
        }

        if (death == 1) {
            x = ennemy.getX();
            y = ennemy.getY();
        } else {
            x += vectX;
            y += vectY;

            if (x < GAME.getSCREEN_WIDTH()/2) vectX = defaultVectX;
            else if (x > GAME.getWORLD_SIZE()-GAME.getSCREEN_WIDTH()/2) vectX = defaultVectX * -1;
            if (y < GAME.getSCREEN_HEIGHT()/2) vectY = defaultVectY;
            else if (y > GAME.getWORLD_SIZE()- GAME.getSCREEN_HEIGHT()/2) vectY = defaultVectY * -1;
        }
    }

    // GETTERS
    protected int getX() {return x;}
    protected int getY() {return y;}
    protected int getScreenX() {return screenX;}
    protected int getScreenY() {return screenY;}
    // SETTER
    protected void setX(int x) {this.x = x;}
    protected void setY(int y) {this.y = y;}
}

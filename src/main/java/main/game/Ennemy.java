package main.game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Ennemy {

    private final Game GAME;
    private int x;
    private int y;
    private int tileX;
    private int tileY;
    private int screenX;
    private int screenY;
    private final int SPEED;
    private ArrayList<int[]> particules;
    private int direction;
    private int spawnPCooldown;
    private final Color _PCOL;
    private final Color PCOL;
    private static boolean freez;
    private boolean hasKilled;



    protected Ennemy(Game game, ArrayList<int[]> tiles) {
        GAME = game;
        SPEED = 10;
        hasKilled = false;
        Random rand = new Random();
        // todo : controller le spawn pour pas qu'il apparaisse proche du joueur
        int[] s = tiles.get(rand.nextInt(tiles.size()));
        x = s[0] * GAME.getTILE_SIZE() + GAME.getTILE_SIZE() / 2;
        y = s[1] * GAME.getTILE_SIZE() + GAME.getTILE_SIZE() / 2;
        tiles.remove(s);
        particules = new ArrayList<int[]>();
        _PCOL = new Color(0, 17, 51);
        PCOL = new Color(0, 10, 30);
        freez = false;

        spawnPCooldown = 0;
    }

    protected void update() {

        Random rand = new Random();

        // si dans une nouvelle tile
        if (!freez &&
            x%GAME.getTILE_SIZE() > GAME.getTILE_SIZE()/2-SPEED-2 && x%GAME.getTILE_SIZE() < GAME.getTILE_SIZE()/2+SPEED+2 &&
            y%GAME.getTILE_SIZE() > GAME.getTILE_SIZE()/2-SPEED-2 && y%GAME.getTILE_SIZE() < GAME.getTILE_SIZE()/2+SPEED+2 &&
                (x/GAME.getTILE_SIZE() != tileX || y/GAME.getTILE_SIZE() != tileY)) {


            // sauvegarde la tile
            tileX = x/GAME.getTILE_SIZE();
            tileY = y/GAME.getTILE_SIZE();

            // centre l'entité
            x = tileX*GAME.getTILE_SIZE() + GAME.getTILE_SIZE()/2;
            y = tileY*GAME.getTILE_SIZE() + GAME.getTILE_SIZE()/2;

            // créer le conteneur des choix
            int[] choices = new int[4];
            for (int i = 0; i < 4; i++) choices[i] = -1;
            int index = 0;

            // verifie chaque direction, les ajoutes à choices et exclut le sens opposé
            if (tileX-1 > 0 && GAME.getLaby()[tileX-1][tileY] >= 0 && direction != 2) choices[index++] = 0;
            if (tileX+1 < GAME.getWORLD_RESOLUTION()-1 && GAME.getLaby()[tileX+1][tileY] >= 0 && direction != 0) choices[index++] = 2;
            if (tileY-1 > 0 && GAME.getLaby()[tileX][tileY-1] >= 0 && direction != 3) choices[index++] = 1;
            if (tileY+1 < GAME.getWORLD_RESOLUTION()-1 && GAME.getLaby()[tileX][tileY+1] >= 0 && direction != 1) choices[index++] = 3;


            if (index >= 1) { // si choix diponibles
                // set la nouvelle direction en fonction
                direction = choices[rand.nextInt(0, index)];

            } else { // sinon
                switch (direction) { // demi tour
                    case 0: direction = 2; break;
                    case 1: direction = 3; break;
                    case 2: direction = 0; break;
                    case 3: direction = 1; break;
                }
            }
        }

        // DEPLACEMENTS
        if (!freez) switch (direction) {
            case 0: x -= SPEED; break;
            case 1: y -= SPEED; break;
            case 2: x += SPEED; break;
            case 3: y += SPEED; break;
        }

        if (spawnPCooldown <= 0) {
            spawnPCooldown = rand.nextInt(1, 2);
            int delta = 50;
            particules.add(new int[]{rand.nextInt(x-delta, x+delta), rand.nextInt(y-delta, y+delta), rand.nextInt(50, 150)});
        } spawnPCooldown--;

    }

    protected void draw(Graphics g2 ) {

        // PARTICULES
        particules.removeIf(particule -> particule[2] == 0);
        int delta = 12;
        if (!particules.isEmpty()) for (int[] particule : particules) {
            int x = particule[0];
            int y = particule[1];

            int screenXp = x - GAME.getCAM().getX() + GAME.getCAM().getScreenX(); // set X écran
            int screenYp = y - GAME.getCAM().getY() + GAME.getCAM().getScreenY(); // set Y écran

            int radius = particule[2];
            particule[2]--;
            if (x + GAME.getTILE_SIZE() > GAME.getCAM().getX() - GAME.getCAM().getScreenX() &&
                    x - GAME.getTILE_SIZE() < GAME.getCAM().getX() + GAME.getCAM().getScreenX() &&
                    y + GAME.getTILE_SIZE() > GAME.getCAM().getY() - GAME.getCAM().getScreenY() &&
                    y - GAME.getTILE_SIZE() < GAME.getCAM().getY() + GAME.getCAM().getScreenY()) {
                g2.setColor(_PCOL);
                g2.fillOval(screenXp - radius/2 - delta/2, screenYp - radius/2 - delta/2, radius+delta, radius+delta);
            }
        }

        if (!particules.isEmpty()) for (int[] particule : particules) {
            int x = particule[0];
            int y = particule[1];

            int screenXp = x - GAME.getCAM().getX() + GAME.getCAM().getScreenX(); // set X écran
            int screenYp = y - GAME.getCAM().getY() + GAME.getCAM().getScreenY(); // set Y écran

            int radius = particule[2];
            particule[2]--;
            if (x + GAME.getTILE_SIZE() > GAME.getCAM().getX() - GAME.getCAM().getScreenX() &&
                    x - GAME.getTILE_SIZE() < GAME.getCAM().getX() + GAME.getCAM().getScreenX() &&
                    y + GAME.getTILE_SIZE() > GAME.getCAM().getY() - GAME.getCAM().getScreenY() &&
                    y - GAME.getTILE_SIZE() < GAME.getCAM().getY() + GAME.getCAM().getScreenY()) {
                g2.setColor(PCOL);
                g2.fillOval(screenXp - radius/2, screenYp - radius/2, radius, radius);
            }
        }

        // ENTITEE
        screenX = x - GAME.getCAM().getX() + GAME.getCAM().getScreenX(); // set X écran
        screenY = y - GAME.getCAM().getY() + GAME.getCAM().getScreenY(); // set Y écran

        if (x + GAME.getTILE_SIZE() > GAME.getCAM().getX() - GAME.getCAM().getScreenX() &&
                x - GAME.getTILE_SIZE() < GAME.getCAM().getX() + GAME.getCAM().getScreenX() &&
                y + GAME.getTILE_SIZE() > GAME.getCAM().getY() - GAME.getCAM().getScreenY() &&
                y - GAME.getTILE_SIZE() < GAME.getCAM().getY() + GAME.getCAM().getScreenY()) {
            // todo : implémenter le sprite
            g2.setColor(Color.BLUE);
            g2.drawRect(screenX, screenY, 10, 10);
        }

        // todo : implémenter le sprite de l'entité

    }

    protected static void setFreez(boolean v) {freez = v;}

    protected int getX() {return x;}
    protected int getY() {return y;}
    protected boolean getHasKilled() {return hasKilled;}
    protected void hasKilled() {hasKilled = true;}

}

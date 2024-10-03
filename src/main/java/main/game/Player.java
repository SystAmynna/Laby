package main.game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Player {

    private final Game GAME;

    private int x; // X dans le monde
    private int y; // Y dans le monde
    private int screenX; // X sur l'écran
    private int screenY; // Y sur l'écran
    private final int SPEED; // vitesse de base
    private int sprint; // multiplicateur de vitesse en cas de sprint
    private int stamina; // stamina
    private final int STAMINA_MAX; // maximum du stamina
    private final int STAMINA_DELAY; // delay avant regen du stamina
    private int staminaCooldown; // cooldown pour atteindre le delay
    private int energy; // energie
    private int energyCooldown; // cooldown avant la régression de l'énergie (definit aléatoirement)
    private Rectangle collisionBox; // boite de collision
    // AFFICHAGE
    private BufferedImage [] sprites; // liste des sprites du joueur
    private boolean moving; // si en train de bouger
    private boolean move; // cycle l'animation
    private int moveDelay; // delay avant le cyclage de l'animation
    private int moveCooldown; // cooldown pour le cyclage de l'animation
    private int direction; // direction du joueur
    private boolean freezed; // si le joueur est freeze
    private ArrayList<int[]> particules;
    private int spawnPCooldown;


    // CONSTRUCTEUR
    protected Player(Game game, int Ax, int Ay) {
        this.GAME = game; // récupère le jeu
        SPEED = 7; // vitesse
        sprint = 1; // sprint
        STAMINA_MAX = 600; // stmina max
        STAMINA_DELAY = 180; // delay avant regen stamina
        staminaCooldown = 0; // cooldown pour regen
        stamina = STAMINA_MAX; // stamina (au max par défaut)
        energy = 100; // energie
        energyCooldown = 1000; // cooldown avant baisse de energie
        moving = false; // est en train de bouger
        move = false; // cycle de bouge
        moveDelay = 10; // delay avant cycle d'animation
        moveCooldown = 0; // cooldown pour le cycle de l'animation
        direction = 0; // direction
        freezed = false; // n'est pas freez
        collisionBox = new Rectangle(10*3, 20*3, 12*3, 12*3); // boite de collision
        particules = new ArrayList<int[]>();


        x = GAME.getTILE_SIZE() * Ax + GAME.getTILE_SIZE() / 2 - GAME.getPLAYER_SIZE()/2; // definit X
        y = GAME.getTILE_SIZE() * Ay + GAME.getTILE_SIZE() / 2 - GAME.getPLAYER_SIZE()/2; // definit Y

        sprites = new BufferedImage[14]; // liste des sprites
        try {
            // ajoute tous les sprites
            sprites[0] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerH1.png")));
            sprites[1] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerH2.png")));
            sprites[2] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerBas.png")));
            sprites[3] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerBas1.png")));
            sprites[4] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerBas2.png")));
            sprites[5] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerGauche.png")));
            sprites[6] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerGauche1.png")));
            sprites[7] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerGauche2.png")));
            sprites[8] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerHaut.png")));
            sprites[9] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerHaut1.png")));
            sprites[10] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerHaut2.png")));
            sprites[11] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerDroite.png")));
            sprites[12] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerDroite1.png")));
            sprites[13] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/player/playerDroite2.png")));
        } catch (IOException e) {throw new RuntimeException(e);}
    }

    //UPDATE
    protected void update(KeyGame KEY) { // update

        // SPRINT
        if (KEY.isShift() && (KEY.isUp() || KEY.isDown() || KEY.isLeft() || KEY.isRight()) && stamina > 0) { // si sprint
            sprint = 2; // set le sprint
            moveDelay = 5; // set le delay de cycle de l'animation
            stamina--; // baisse le stamina
            staminaCooldown = STAMINA_DELAY; // reset le cooldown du stamina
        } else { // si sprint pas
            sprint = 1; // valeur de sprint
            moveDelay = 10; // set le delay de cycle de l'animation
            if (staminaCooldown != 0) staminaCooldown--; // baisse le cooldown si il est inégale à 0
        }
        if (staminaCooldown == 0 && stamina < STAMINA_MAX) {
            stamina += (KEY.isUp() || KEY.isDown() || KEY.isLeft() || KEY.isRight()) ? 1 : 2;
        }


        // ENERGIE
        if (energyCooldown==0 && energy != 0) { // si le cooldown de l'energie le permet
            energy--; // décrémente l'énergie
            Random rand = new Random(); // créer un générateur de nombre aléatoire
            energyCooldown = rand.nextInt(60, 600); // set le cooldown, prochaine baisse dans 1s à 10s
        } else if (energyCooldown != 0) energyCooldown--; // sinon décrémente le cooldown de l'énergie


        screenX = x - GAME.getCAM().getX() + GAME.getCAM().getScreenX(); // set X écran
        screenY = y - GAME.getCAM().getY() + GAME.getCAM().getScreenY(); // set Y écran

        int speed = (freezed) ? 0 : SPEED * sprint; // definit la vitesse de déplacement en fonction du sprint

        if (KEY.isUp() && !collisionY(KEY, speed)) { // haut
            y = y - speed; // modifie position
            direction = 2; // set la direction
        }
        else if (KEY.isDown() && !collisionY(KEY, speed)) { // bas
            y = y + speed;
            direction = 0;
        }
        if (KEY.isLeft() && !collisionX(KEY, speed)) { // gauche
            x = x - speed;
            direction = 1;
        }
        else if (KEY.isRight() && !collisionX(KEY, speed)) { // droite
            x = x + speed;
            direction = 3;
        }


        // set "moving" en fonction de si en train de bouger
        moving = KEY.isUp() || KEY.isDown() || KEY.isLeft() || KEY.isRight();

        if (freezed) {
            Random rand = new Random();
            if (spawnPCooldown <= 0) {
                spawnPCooldown = rand.nextInt(1, 2);
                int delta = 50;
                particules.add(new int[]{rand.nextInt(x+GAME.getPLAYER_SIZE()/2 -delta, x+GAME.getPLAYER_SIZE()/2+delta), rand.nextInt(y+GAME.getPLAYER_SIZE()/2-delta, y+GAME.getPLAYER_SIZE()/2+delta), rand.nextInt(50, 150)});
            } spawnPCooldown--;
            for (int[] p : particules) p[1]-=2;
        }

    }

    //DRAW
    protected void draw(Graphics g2) {

        if (moveCooldown == 0) { // gestion cooldown cycle animation
            moveCooldown = moveDelay;
            move = !move;
        } else moveCooldown--;


        if (freezed) {
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
                    g2.setColor(Color.RED);
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
                    g2.setColor(Color.BLACK);
                    g2.fillOval(screenXp - radius/2, screenYp - radius/2, radius, radius);
                }
            }
        }




        if (x + GAME.getTILE_SIZE() > GAME.getCAM().getX() - GAME.getCAM().getScreenX() &&
            x - GAME.getTILE_SIZE() < GAME.getCAM().getX() + GAME.getCAM().getScreenX() &&
            y + GAME.getTILE_SIZE() > GAME.getCAM().getY() - GAME.getCAM().getScreenY() &&
            y - GAME.getTILE_SIZE() < GAME.getCAM().getY() + GAME.getCAM().getScreenY()) { // si le joueur est vue par la caméra


            // AFFICHE LE JOUEUR (TOUS LES SPRITES POSSIBLES
            if (freezed) { // si freeze
                if (move) g2.drawImage(sprites[0], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                else g2.drawImage(sprites[1], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
            } else { // si non freeze
                switch (direction) { // en fonction de la direction
                    case 0: // bas
                        if (moving) {
                            if (move) g2.drawImage(sprites[3], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                            else g2.drawImage(sprites[4], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                        } else g2.drawImage(sprites[2], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                        break;
                    case 1: // gauche
                        if (moving) {
                            if (move) g2.drawImage(sprites[6], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                            else g2.drawImage(sprites[7], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                        } else g2.drawImage(sprites[5], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                        break;
                    case 2: // haut
                        if (moving) {
                            if (move) g2.drawImage(sprites[9], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                            else g2.drawImage(sprites[10], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                        } else g2.drawImage(sprites[8], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                        break;
                    case 3: // droite
                        if (moving) {
                            if (move) g2.drawImage(sprites[12], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                            else g2.drawImage(sprites[13], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                        } else g2.drawImage(sprites[11], screenX, screenY, GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
                }

            }




            // COLLISION BOX
            /*
            g2.setColor(Color.BLUE);
            g2.drawRect(screenX+ collisionBox.x, screenY+ collisionBox.y, collisionBox.width, collisionBox.height);
            g2.fillRect(screenX-2, screenY-2, 4, 4);
            */


        }





    }

    // COLLISION
    private boolean collisionY(KeyGame key, int speed) { // collision Y
        // definit la boite de collision
        int leftXw = x + collisionBox.x;
        int rightXw = x + collisionBox.x + collisionBox.width;
        int topYw = y + collisionBox.y;
        int bottomYw = y + collisionBox.y + collisionBox.height;
        // place la boite sur les tiles
        int leftX = leftXw/GAME.getTILE_SIZE();
        int rightX = rightXw/GAME.getTILE_SIZE();
        int topY = topYw/GAME.getTILE_SIZE();
        int bottomY = bottomYw/GAME.getTILE_SIZE();
        // verifie 2 tiles (car le joueur peut être entre 2 tiles)
        int tile1, tile2;
        // verifie la collision
        if (key.isUp()) {
            topY = (topYw - speed) / GAME.getTILE_SIZE();
            if (topY<0) return true;
            tile1 = GAME.getLaby()[leftX][topY];
            tile2 = GAME.getLaby()[rightX][topY];
            return tile1 < 0 || tile2 < 0;
        } else if (key.isDown()) {
            bottomY = (bottomYw + speed) / GAME.getTILE_SIZE();
            if (bottomY > GAME.getWORLD_RESOLUTION()-1) return true;
            tile1 = GAME.getLaby()[leftX][bottomY];
            tile2 = GAME.getLaby()[rightX][bottomY];
            return tile1 < 0 || tile2 < 0;
        }
        return false;
    }
    private boolean collisionX(KeyGame key, int speed) { // collision X
        int leftXw = x + collisionBox.x;
        int rightXw = x + collisionBox.x + collisionBox.width;
        int topYw = y + collisionBox.y;
        int bottomYw = y + collisionBox.y + collisionBox.height;

        int leftX = leftXw/GAME.getTILE_SIZE();
        int rightX = rightXw/GAME.getTILE_SIZE();
        int topY = topYw/GAME.getTILE_SIZE();
        int bottomY = bottomYw/GAME.getTILE_SIZE();

        int tile1, tile2;

        if (key.isLeft()) {
            leftX = (leftXw - speed) / GAME.getTILE_SIZE();
            if (leftX<0) return true;
            tile1 = GAME.getLaby()[leftX][topY];
            tile2 = GAME.getLaby()[leftX][bottomY];
            return tile1 < 0 || tile2 < 0;
        } else if (key.isRight()) {
            rightX = (rightXw + speed) / GAME.getTILE_SIZE();
            if (rightX > GAME.getWORLD_RESOLUTION()-1) return true;
            tile1 = GAME.getLaby()[rightX][topY];
            tile2 = GAME.getLaby()[rightX][bottomY];
            return tile1 < 0 || tile2 < 0;
        }
        return false;
    }

    // GETTERS
    protected int getX() {return x;}
    protected int getY() {return y;}
    protected int getScreenX() {return screenX;}
    protected int getScreenY() {return screenY;}
    protected int getSTAMINA_MAX() {return STAMINA_MAX;}
    protected int getStamina() {return stamina;}
    protected int getEnergy() {return energy;}
    protected Rectangle getCollisionBox() {return collisionBox;}
    protected boolean getFreezed() {return freezed;}
    // SETTERS
    protected void setFreezed(boolean freezed) {this.freezed = freezed;}
    protected void resetEnergy() {energy = 100;}

}

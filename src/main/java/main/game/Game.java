package main.game;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Game extends JPanel implements Runnable {

    // OBJETS
    private boolean running = true; // si la simulation tourne
    private final Thread gameThread; // Thread du jeu
    private final JFrame WINDOW; // fenêtre
    private int [][] laby; // labyrinthe pour la map
    private final KeyGame KEY; // KeyListener
    private final Camera CAM; // camera du jeu
    private final Player PLAYER; // joueur
    private final Ombre OMBRE;
    private final ArrayList<Ennemy> ENNEMYS; // liste des ennemis sur la map
    // SORTIES
    private final int Ax; // X de l'entrée
    private final int Ay; // Y de l'entrée
    private final int Bx; // X de la sortie
    private final int By; // Y de la sortie
    // AUTRES
    private int gameCooldown; // cooldown au démarrage du jeu (pour le gresillement)
    private final Color[] gresille; // liste des couleurs du gresillement
    private Color[] badGresille; // liste les couleurs du mauvait gresillement
    private boolean badG; // active ou non le mauvais gresillement
    private float gresilleAlpha; // alfa du gresillement (transparence)
    // CONSTANTES
    //original size
    private final int ORIGINAL_TILE_SIZE; // résolution Tile
    private final int ORIGINAL_PLAYER_SIZE; // résolution joueur
    private final int SCALE; // multiplicateur
    //size
    private final int TILE_SIZE; // résolution Tile écran
    private final int PLAYER_SIZE; // résolution joueur écran
    //écran
    private final int MAX_SCREEN_COL; // nombre de colonnes sur l'écran
    private final int MAX_SCREEN_ROW; // nombre de ligne sur l'écran
    private final int SCREEN_WIDTH; // résolution horizontale de l'écran
    private final int SCREEN_HEIGHT; // résolution verticale de l'écran
    //monde
    private final int WORLD_RESOLUTION; // résolution du monde tiles
    private final int WORLD_SIZE; // résolution du monde coo
    // SPRITES
    private BufferedImage [] pathSprite;
    private BufferedImage [] waySprite;
    private BufferedImage defaultWallSprite;
    private BufferedImage defaultPathSprite;
    private BufferedImage resolvePathSprite;
    private BufferedImage crane;
    // GAME
    private boolean resolve; // si le joueur a activer la résolution
    private int qte;
    private int resolveCount; // compte le nombre de fragment de map trouvé (pour afficher le chemin)
    private MapFrag [] resolveList; // liste l'emplacement des fragments
    private int [][] miniMap; // mini-map
    private boolean gameOver; // si le jeu est fini
    private boolean victory;
    private int death;
    private boolean noEnergie;
    ArrayList<int[]> freeTiles;


    // todo : implémenter les SONS



    public Game(JFrame window, int[][] laby, int c, int Ax, int Ay, int Bx, int By) {
        // OBJETS
        gameThread = new Thread(this); // initialise le Thread du jeu
        WINDOW = window; // récupère la fenêtre
        KEY = new KeyGame(this); // créer l'écouteur de clavier
        this.laby = laby; // récupère le laby
        ENNEMYS = new ArrayList<Ennemy>(); // créer la liste des ennemis
        // SORTIES
        this.Ax = Ax; // x de A
        this.Ay = Ay; // y de A
        this.Bx = Bx; // x de B
        this.By = By; // y de B
        // CONSTANTES
        //original size
        ORIGINAL_TILE_SIZE = 64;
        ORIGINAL_PLAYER_SIZE = ORIGINAL_TILE_SIZE / 2;
        SCALE = 3;
        //size
        TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;
        PLAYER_SIZE = ORIGINAL_PLAYER_SIZE * SCALE;
        //écran
        MAX_SCREEN_COL = 6;
        MAX_SCREEN_ROW = 4;
        SCREEN_WIDTH = MAX_SCREEN_COL * TILE_SIZE; // résolution horizontale de l'écran
        SCREEN_HEIGHT = MAX_SCREEN_ROW * TILE_SIZE; // résolution verticale de l'écran
        //monde
        WORLD_RESOLUTION = c;
        WORLD_SIZE = TILE_SIZE * WORLD_RESOLUTION;
        // OBJETS
        CAM = new Camera(this); // création de la caméra
        PLAYER = new Player(this, Ax, Ay); // création du joueur
        OMBRE = new Ombre(this);
        // gresillement
        badG = false;
        gresille = new Color[4]; // initialisation des couleurs possible du gresillement
        gresille[0] = Color.BLACK; // noir
        gresille[1] = Color.DARK_GRAY; // gris foncé
        gresille[2] = Color.GRAY; // gris
        gresille[3] = Color.LIGHT_GRAY; // gris clair
        badGresille = new Color[4]; // initilise le mauvais gresillement
        badGresille = new Color[4];
        badGresille[0] = new Color(204, 0, 0);
        badGresille[1] = new Color(170, 0, 0);
        badGresille[2] = new Color(136, 0, 0);
        badGresille[3] = new Color(102, 0, 0);
        // PANEL
        WINDOW.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT)); // redimensionne la fenêtre
        this.requestFocusInWindow(); // écrase les dimensions de la fenêtre avec celles du panel
        this.setBackground(Color.black); // couleur du background
        this.setDoubleBuffered(true); // garde la dernière frame pendant l'affichage de la nouvelle
        this.addKeyListener(KEY); // ajoute un écouteur de clavier
        this.setFocusable(true);
        this.setDoubleBuffered(true);
        // SPRITES
        pathSprite = new BufferedImage[6];
        waySprite = new BufferedImage[6];
        try {
            defaultWallSprite = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/wall.png")));
            defaultPathSprite = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/loserPath.png")));
            resolvePathSprite = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/pathResolve.png")));

            //crane
            crane = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/death.png")));

            // path
            pathSprite[0] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/path1.png")));
            pathSprite[1] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/path2.png")));
            pathSprite[2] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/path3.png")));
            pathSprite[3] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/path4.png")));
            pathSprite[4] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/path5.png")));
            pathSprite[5] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/path6.png")));

            // way
            waySprite[0] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/resolve/way1.png")));
            waySprite[1] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/resolve/way2.png")));
            waySprite[2] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/resolve/way3.png")));
            waySprite[3] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/resolve/way4.png")));
            waySprite[4] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/resolve/way5.png")));
            waySprite[5] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/tiles/resolve/way6.png")));

        } catch (IOException e) {throw new RuntimeException(e);}
        for (int x = 0; x < laby.length; x++) for (int y = 0; y < laby[x].length; y++) if (laby[x][y] == -1) {
            if (y+1 < WORLD_RESOLUTION-1 && laby[x][y+1] == 0) laby[x][y] = -2;
        }

        // GAME
        gameCooldown = 400; // cooldown au lancement du jeu
        gresilleAlpha = 0.0f; // transparence gresillement
        Random rand = new Random(); // créer un générateur de nombre aléatoire
        resolve = false; // resolut ?
        gameOver = false; // le jeu n'est pas fini
        victory = false;
        qte = 100;
        death = 0;
        noEnergie = false;
        resolveCount = 0; // aucun fragment trouvé
        resolveList = new MapFrag[3]; // nombre de fragment à trouver
        freeTiles = new ArrayList<int[]>(); // liste pour les tiles libres (non mur, non chemin)
        for (int i = 0; i < WORLD_RESOLUTION; i++) for (int j = 0; j < WORLD_RESOLUTION; j++) { // parcours le laby
            if (laby[i][j] == 0) {
                freeTiles.add(new int[]{i, j}); // liste les tiles libres
                laby[i][j] = rand.nextInt(2, 25); // 2 à 25 variation du sol
                }
            else if (laby[i][j] == 1) laby[i][j] = rand.nextInt(26,49); // chemins de 26 à 49
        }
        for (int i = 0; i < resolveList.length; i++) {
            int s = rand.nextInt(freeTiles.size());
            int sx = freeTiles.get(s)[0];
            int sy = freeTiles.get(s)[1];
            freeTiles.remove(s);
            resolveList[i] = new MapFrag(this, sx, sy);

        }
        ENNEMYS.add(new Ennemy(this, freeTiles));

        // mini map
        miniMap = new int[WORLD_RESOLUTION][WORLD_RESOLUTION];
        for (int i = 0; i < WORLD_RESOLUTION; i++) for (int j = 0; j < WORLD_RESOLUTION; j++) miniMap[i][j] = 0;

        gameThread.start(); // lance le Thread du jeu
    }

    // GETTERS
    protected int getSCREEN_WIDTH() {return SCREEN_WIDTH;}
    protected int getSCREEN_HEIGHT() {return SCREEN_HEIGHT;}
    protected int getTILE_SIZE() {return TILE_SIZE;}
    protected int getPLAYER_SIZE() {return PLAYER_SIZE;}
    protected int getWORLD_RESOLUTION() {return WORLD_RESOLUTION;}
    protected int getWORLD_SIZE() {return WORLD_SIZE;}
    protected Camera getCAM() {return CAM;};
    protected int [][] getLaby() {return laby;}
    protected boolean getNoEnergy() {return noEnergie;}
    protected void addQte(int v) {qte += v;}
    protected Player getPLAYER() {return PLAYER;}
    protected ArrayList<Ennemy> getENNEMYS() {return ENNEMYS;}


    @Override
    public void run() {
        int normalFps = 60; // FPS prévue
        double drawInterval = (double) (1000000000 / normalFps); // interval entre chaque dessin du panel
        double delta = 0.0;
        long lastTime = System.nanoTime();
        long timer = 0L; // compte le temps
        int fps = 0; // nombre de FPS passé depuis le début de la seconde
        long currentTime;
        long alertTime = 0;
        while (running) { // tant que le Thread Simulation fonctionne
            currentTime = System.nanoTime(); // récupère le temps de maintenant
            delta += (double) (currentTime - lastTime) / drawInterval; // calcule le delta
            timer += currentTime - lastTime; // compte le temps
            lastTime = currentTime;
            if (delta >= 1.0) {
                alertTime = System.nanoTime();
                // EXECUTION
                this.update(); // UPDATE
                this.repaint(); // AFFICHAGE
                // ------------
                alertTime = System.nanoTime() - alertTime;
                if (alertTime > drawInterval) System.out.println("[ALERTE] " + alertTime + " ms");
                --delta;
                ++fps;
            }
            if (timer >= 1000000000L) { // toute les secondes
                System.out.println("FPS: " + fps + "/" + normalFps + " | UPDATE TIME: " + alertTime); // affiche
                fps = 0; // remets à 0 les FPS pour la prochaine seconde
                timer = 0L; // remets le chronomètre à 0 pour la prochaine seconde
            }
        }
    }

    //  - UPDATE
    public void update() {

        if (gameCooldown > 0) gameCooldown--; // décrémente le cooldown du début


        // ENTITEES ?
        for (Ennemy e : ENNEMYS) {
            e.update();
        }

        if (!gameOver) { // tant que le jeu n'est pas fini


            if (!resolve) for (MapFrag frag : resolveList) {
                int distX = PLAYER.getX() - frag.getX();
                if (distX < 0) distX = distX*-1;
                int distY = PLAYER.getY() - frag.getY();
                if (distY < 0) distY = distY*-1;

                if (!frag.getRecup() && distX < PLAYER_SIZE/2 && distY < PLAYER_SIZE/2) {
                    frag.setRecup(true);
                    resolveCount++;
                    if (resolveCount >= resolveList.length) {
                        resolve = true;
                        WINDOW.setTitle("Suivre le sang...");
                    }
                }
            }

            Ennemy.setFreez(gameCooldown > 0);

            if (noEnergie) {
                PLAYER.setFreezed(true);
                badG = true;
                WINDOW.setTitle("Les tenebres vous envellopent...");
                Ennemy.setFreez(true);
                if (qte <= 0) {
                    Ennemy.setFreez(false);
                    death = 2;
                    gameOver = true;
                    noEnergie = false;
                    WINDOW.setTitle("L'Ombre vous a eu !");
                } else if (qte >= 200) {
                    if (!freeTiles.isEmpty()) ENNEMYS.add(new Ennemy(this, freeTiles));
                    Ennemy.setFreez(false);
                    PLAYER.resetEnergy();
                    noEnergie = false;
                    WINDOW.setTitle("Je te vois...");
                }
            } else {

                if (PLAYER.getEnergy() <= 0) noEnergie = true;
                badG = false;
                qte = 100;
                PLAYER.setFreezed(KEY.isEscPressed()); // si ESC alors freeze
            }


            int Px = PLAYER.getX() + PLAYER_SIZE/2;
            int Py = PLAYER.getY() + PLAYER_SIZE/2;

            for (Ennemy e : ENNEMYS) {
                int Ex = e.getX();
                int Ey = e.getY();

                double distance = Math.sqrt(Math.pow(Ex - Px, 2) + Math.pow(Ey - Py, 2));

                if (distance <= TILE_SIZE/2) {
                    e.hasKilled();
                    Ennemy.setFreez(false);
                    PLAYER.resetEnergy();
                    WINDOW.setTitle("Le sans visage vous a eu !");
                    death = 1;
                    gameOver = true;
                    badG = true;
                }
            }


            // PLAYER
            PLAYER.update(KEY); // actualise le joueur



            // CAM
            // si le joueur est pas trop proche de la bordure
            if (PLAYER.getX() > SCREEN_WIDTH/2 && PLAYER.getX() < WORLD_SIZE - (SCREEN_WIDTH/2) - PLAYER_SIZE/2) {
                CAM.setX(PLAYER.getX() + PLAYER_SIZE/2); // la caméra suit le joueur
            } else { // sinon place à la limite pour suivre le joueur
                if (PLAYER.getX() < WORLD_SIZE/2) CAM.setX(SCREEN_WIDTH/2 + PLAYER_SIZE/2);
                else CAM.setX(WORLD_SIZE - (SCREEN_WIDTH/2) );
            }
            // même chose pour Y
            if (PLAYER.getY() > SCREEN_HEIGHT/2 && PLAYER.getY() < WORLD_SIZE - (SCREEN_HEIGHT/2) - TILE_SIZE + PLAYER_SIZE) {
                CAM.setY(PLAYER.getY() + PLAYER_SIZE/2);
            } else {
                if (PLAYER.getY() < WORLD_SIZE/2) CAM.setY(SCREEN_HEIGHT/2 + PLAYER_SIZE/2);
                else CAM.setY(WORLD_SIZE - (SCREEN_HEIGHT/2) - PLAYER_SIZE/2);
            }

            // definit la boite de collision
            int leftXw = PLAYER.getX() + PLAYER.getCollisionBox().x;
            int rightXw = PLAYER.getX() + PLAYER.getCollisionBox().x + PLAYER.getCollisionBox().width;
            int topYw = PLAYER.getY() + PLAYER.getCollisionBox().y;
            int bottomYw = PLAYER.getY() + PLAYER.getCollisionBox().y + PLAYER.getCollisionBox().height;
            // place la boite sur les tiles
            int leftX = leftXw/TILE_SIZE;
            int rightX = rightXw/TILE_SIZE;
            int topY = topYw/TILE_SIZE;
            int bottomY = bottomYw/TILE_SIZE;

            // mini map
            for (int i = 0; i < WORLD_RESOLUTION; i++) for (int j = 0; j < WORLD_RESOLUTION; j++) {
                if (miniMap[i][j] == 2) miniMap[i][j] = 1;
            }
            miniMap[leftX][topY] = 2;
            miniMap[rightX][topY] = 2;
            miniMap[leftX][bottomY] = 2;
            miniMap[rightX][bottomY] = 2;


            // Detecteur de victoire
            if (leftX == Bx && rightX == Bx && topY == By && bottomY == By) {
                victory = true;
                gameOver = true;
            }


        } else { // si le jeu est fini

            // todo : implémenter le screemer du faceless


            CAM.autoMove(death);
            if (death > 0) badG = true;
        }
    }

    //  - DRAW
    protected void paintComponent(Graphics g) { // affichage
        super.paintComponent(g); // appel la méthode parent
        Graphics2D g2 = (Graphics2D) g; // converti en 2D

        Font defaultFont = g2.getFont();

        // TILES
        // affichage des tiles
        int startCol = Math.max((int) (double) (CAM.getX() / TILE_SIZE - SCREEN_WIDTH / 2 + TILE_SIZE * 2), 0); // X de départ
        int startRow = Math.max((int) (double) (CAM.getY() / TILE_SIZE - SCREEN_HEIGHT / 2 + TILE_SIZE), 0); // Y de départ
        int endCol = Math.min((int) (double) ((CAM.getX() + SCREEN_WIDTH / 2) / TILE_SIZE + TILE_SIZE ), WORLD_RESOLUTION); // X d'arrivé
        int endRow = Math.min((int) (double) ((CAM.getY() + SCREEN_HEIGHT / 2) / TILE_SIZE + TILE_SIZE ), WORLD_RESOLUTION); // Y d'arrivé

        for (int col = startCol; col < endCol; col++) { // parcours les tiles qui doivent être affiché
            for (int row = startRow; row < endRow; row++) {
                int worldX = col * TILE_SIZE; // X sur le monde
                int worldY = row * TILE_SIZE; // Y sur le monde
                int screenX = worldX - CAM.getX() + CAM.getScreenX(); // adapte la position avec la camera
                int screenY = worldY - CAM.getY() + CAM.getScreenY();

                if (worldX + TILE_SIZE > CAM.getX() - CAM.getScreenX() &&
                        worldX - TILE_SIZE < CAM.getX() + CAM.getScreenX() &&
                        worldY + TILE_SIZE > CAM.getY() - CAM.getScreenY() &&
                        worldY - TILE_SIZE < CAM.getY() + CAM.getScreenY()) { // si la Tile doit être affichée

                    // affiche la bon tile
                    if (laby[col][row] >+ 0) {
                        g2.drawImage(sprite(laby[col][row]), screenX, screenY, TILE_SIZE, TILE_SIZE, null);
                        if (resolve && (laby[col][row] >= 26 && laby[col][row] <=49)) {
                            BufferedImage sprite;
                            switch (laby[col][row]) {
                                case 26, 27, 28, 29 -> sprite = waySprite[0];
                                case 30, 31, 32, 33 -> sprite = waySprite[1];
                                case 34, 35, 36, 37 -> sprite = waySprite[2];
                                case 38, 39, 40, 41 -> sprite = waySprite[3];
                                case 42, 43, 44, 45 -> sprite = waySprite[4];
                                case 46, 47, 48, 49 -> sprite = waySprite[5];
                                default -> sprite = resolvePathSprite;
                            }
                            g2.drawImage(sprite, screenX, screenY, TILE_SIZE, TILE_SIZE, null);
                        }
                    }


                }
            }
        }

        if (!resolve) for (MapFrag frag : resolveList) {
            if (!frag.getRecup()) frag.draw(g2);
        }

        // ombres
        if (noEnergie) {
            OMBRE.draw(g2);
            g2.setFont(defaultFont);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRect(SCREEN_WIDTH/2-33, SCREEN_HEIGHT/2-3 + 100, 106, 26);
            g2.fillRect(SCREEN_WIDTH/2-30, SCREEN_HEIGHT/2 + 100, qte/2, 20);
            g2.drawImage(crane, SCREEN_WIDTH/2-100, SCREEN_HEIGHT/2+80, null);

        }

        // PLAYER
        if (!gameOver) PLAYER.draw(g2); // affiche le joueur



        // ENNEMY
        for (Ennemy ennemy : ENNEMYS) ennemy.draw(g2);

        // CAM
        /*
        int r = 5;
        g2.setColor(Color.GREEN);
        g2.fillRect(CAM.getScreenX()-r, CAM.getScreenY()-r, r*2, r*2);
        */

        // HUD
        if (!gameOver) {

            float hudAlpha = 0.6f;
            AlphaComposite hudTransp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, hudAlpha); // transparence HUD
            g2.setComposite(hudTransp);

            // STAMINA
            if (!PLAYER.getFreezed() && PLAYER.getStamina() < PLAYER.getSTAMINA_MAX()) { // affiche le stamina
                if (PLAYER.getStamina()<200 && PLAYER.getStamina()>=50) g2.setColor(Color.ORANGE); // orange si le stamina est faible
                else if (PLAYER.getStamina() < 50) g2.setColor(Color.RED); // rouge si le stamina est très faible
                else g2.setColor(Color.WHITE); // blanc par défaut
                g2.drawRect(20, SCREEN_HEIGHT-70, PLAYER.getSTAMINA_MAX()/2+6, 16); // contour
                g2.fillRect(23, SCREEN_HEIGHT-67, PLAYER.getStamina()/2, 10); // bar de stamina
            }

            // energie
            if (PLAYER.getEnergy() == 0) g2.setColor(Color.RED); // couleur rouge si plus d'energie
            else g2.setColor(Color.WHITE); // blanc par défaut
            g2.drawRect(SCREEN_WIDTH-150, 30, 106, 46); // contour
            g2.fillRect(SCREEN_WIDTH-147, 33, PLAYER.getEnergy(), 40); // bar d'energie
            g2.fillRect(SCREEN_WIDTH-44, 43, 3, 20); // petit port, pour que ça ressemble à une pile

            // MINIMAP
            // definit la boite de collision
            int leftXw = PLAYER.getX() + PLAYER.getCollisionBox().x;
            int rightXw = PLAYER.getX() + PLAYER.getCollisionBox().x + PLAYER.getCollisionBox().width;
            int topYw = PLAYER.getY() + PLAYER.getCollisionBox().y;
            int bottomYw = PLAYER.getY() + PLAYER.getCollisionBox().y + PLAYER.getCollisionBox().height;
            // place la boite sur les tiles
            int leftX = leftXw/TILE_SIZE;
            int rightX = rightXw/TILE_SIZE;
            int topY = topYw/TILE_SIZE;
            int bottomY = bottomYw/TILE_SIZE;

            int mmX = SCREEN_WIDTH - 25;
            int mmY = SCREEN_HEIGHT - 50;
            int mmC = 2;

            mmX = mmX - WORLD_RESOLUTION * mmC;
            mmY = mmY - WORLD_RESOLUTION * mmC;

            if (KEY.isMap() && !PLAYER.getFreezed()) {
                mmC = (SCREEN_HEIGHT-100)/WORLD_RESOLUTION;
                mmX = SCREEN_WIDTH/2 - mmC*(WORLD_RESOLUTION/2);
                mmY = SCREEN_HEIGHT/2 - mmC*(WORLD_RESOLUTION/2);
            }

            g2.setColor(Color.WHITE);
            if (resolveCount > 0) g2.drawString("Fragments de carte: " + resolveCount + "/" + resolveList.length , Math.min(mmX, SCREEN_WIDTH-150), mmY-10);
            g2.drawRect(mmX-3, mmY-3, WORLD_RESOLUTION*mmC+6, WORLD_RESOLUTION*mmC+6);
            for (int i = 0; i < WORLD_RESOLUTION; i++) for (int j = 0; j < WORLD_RESOLUTION; j++) if (miniMap[i][j] > 0) {
                if ((i == leftX && j == topY) || (i == leftX && j == bottomY) || (i == rightX && j == topY) || (i == rightX && j == bottomY)) {
                    g2.setColor(Color.RED);
                } else g2.setColor(Color.WHITE);
                g2.fillRect(mmX + i*mmC, mmY + j*mmC, mmC, mmC);
            }
            g2.setComposite(AlphaComposite.SrcOver);


            //EFFECT
            float alphaG = (gameCooldown > 200) ? 1.0f : (PLAYER.getFreezed()) ? 0.5f : facelessEffect();
            gresille(g2, alphaG);


        }


        // titre
        if (gameCooldown < 300 && gameCooldown != 0) {
            g2.setFont(new Font("Arial", Font.PLAIN, 30));
            g2.setColor(Color.RED);
            g2.drawString("Jouons...", SCREEN_WIDTH/2 - 50, SCREEN_HEIGHT/2 - 20);
            g2.setFont(defaultFont);
        }

        if (gameOver) {

            //EFFECT
            gresille(g2, (death == 0) ? 0.2f : 0.4f);

            g2.setColor( (victory) ? Color.BLUE : Color.RED );
            g2.setFont(new Font("Roboto", Font.BOLD, 40));
            g2.drawString((victory) ? "Vous avez reussi !" : "Vous avez peri !", SCREEN_WIDTH/2 - 150, 200);
            g2.setColor(Color.LIGHT_GRAY);
            g2.setFont(new Font("Roboto", Font.PLAIN, 20));
            g2.drawString("Maintenir ESC pour quitter...", SCREEN_WIDTH/2 - 120, 250);
            g2.setFont(new Font("Roboto", Font.PLAIN, 15));
            if (death == 1) {
                g2.drawString("Vous avez peri par \"le Sans-visage\" !", SCREEN_WIDTH/2 - 150, 300);
                g2.drawString("\"Le Sans-visage\" se deplace comme vous a travers le labyrinthe,", SCREEN_WIDTH/2 - 180, 330);
                g2.drawString("cependant, il est plus rapide que vous, sauf si vous courez.", SCREEN_WIDTH/2 - 180, 350);
                g2.drawString("\"L'Ombre\" fait apparaitre un \"Sans-visage\" si vous lui echapez !", SCREEN_WIDTH/2 - 180, 370);
                g2.drawString("[!]   A l'avenir, tachez de l'eviter !", SCREEN_WIDTH/2 - 150, 400);
            } else if (death == 2) {
                g2.drawString("Vous avez peri par \"l'Ombre\" !", SCREEN_WIDTH/2 - 150, 300);
                g2.drawString("\"L'Ombre\" vous attaque quand vous n'avez plus d'energie...", SCREEN_WIDTH/2 - 180, 330);
                g2.drawString("[!]   Tentez de vous debattre afin d'y echaper !", SCREEN_WIDTH/2 - 150, 350);
            }
            if (death > 0) g2.drawImage(crane, SCREEN_WIDTH/2 - TILE_SIZE/2 , SCREEN_WIDTH/2 - TILE_SIZE +50, TILE_SIZE, TILE_SIZE, null);


            g2.setFont(defaultFont);

        }



        // DEBUG INFOS
        /*
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(0, 0, 100, 20);
        g2.setColor(Color.WHITE);
        int tileX = (PLAYER.getX()-PLAYER_SIZE)/TILE_SIZE;
        int tileY = (PLAYER.getY()-PLAYER_SIZE)/TILE_SIZE;
        g2.drawString("X Y: "+PLAYER.getX()+" "+PLAYER.getY(), 0, 10);
        g2.drawString("Tile: "+tileX+" "+tileY, 0, 20);
        */


        if (KEY.isEscPressed()) { // si ESC
            g2.setColor(Color.RED); // couleur de contour
            g2.fillRect(0, 0, 70, 12); // contour
            g2.setColor(Color.BLACK); // couleur texte
            g2.drawString("Fermeture...", 0, 10); // texte
        }
    }

    private void gresille(Graphics2D g2, float alpha) { // Méthode de gresillement
        Random rand = new Random(); // créer un générateur de nombre aléatoire
        AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha); // defini la tansparence
        g2.setComposite(alphaComposite); // applique la transparence
        int div = 100; // diviseur
        int w = SCREEN_WIDTH / div +1; // largeur d'une cellule
        int h = SCREEN_HEIGHT / div +1; // hauteur d'une cellule
        for (int x = 0; x < div; x++) for (int y = 0; y < div; y++) { // parcous l'écran
            if (badG) g2.setColor(badGresille[rand.nextInt(badGresille.length)]); // choisi la couleur aléatoirement
            else g2.setColor(gresille[rand.nextInt(gresille.length)]); // choisi la couleur aléatoirement
            g2.fillRect(x * w, y * h, w, h); // affiche la cellule
        }
        g2.setComposite(AlphaComposite.SrcOver); // ferme la transparence
    }

    private BufferedImage sprite(int val) { // échange la valeur "val" avec un sprite
        if (val<0) {
            return defaultWallSprite;
        } else {
            if (val > 6 && val <= 25 || val > 30 && val <= 49) return pathSprite[0];
            else return switch (val) {
                case 2, 26 -> pathSprite[1];
                case 3, 27 -> pathSprite[2];
                case 4, 28 -> pathSprite[3];
                case 5, 29 -> pathSprite[4];
                case 6, 30 -> pathSprite[5];
                default -> defaultPathSprite;
            };
        }
    }

    private float facelessEffect() {
        float res = 0.1f;

        int Px = PLAYER.getX();
        int Py = PLAYER.getY();

        for (Ennemy ennemy : ENNEMYS) {
            int Ex = ennemy.getX();
            int Ey = ennemy.getY();

            double distance = Math.sqrt(Math.pow(Ex - Px, 2) + Math.pow(Ey - Py, 2));
            float _res = 0.0f;
            if (distance < 5000) _res = 0.15f;
            if (distance < 1000) _res = 0.2f;
            if (distance < 500) _res = 0.25f;
            res = Math.max(res, _res);
        }
        return res;
    }

}

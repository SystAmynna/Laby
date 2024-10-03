package main;

import main.game.Game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Labytinthe extends JPanel implements Runnable {

    // OBJETS
    private final JFrame WINDOW; // fenêtre de l'application
    private KeyLaby key; // gestionnaire clavier
    private Thread labyThread; // Thread du laby;
    private Thread gen; // Thread de génération
    // LABY
    private final int[][] grid; // grille du laby
    private final boolean SIMU; // si il faut simuler le laby
    private int entrAx; // x entrée A
    private int entrAy; // y entrée A
    private int entrBx; // x entrée B
    private int entrBy; // y entrée B
    // REGLES
    private boolean running;
    private final int W; // largeur fenêtre
    private final int H; // hauteur fenêtre
    private final int C; // Valeur de côté
    private final boolean COMPLEX; // si le labyrinthe est complexe ou non
    private final boolean RANDOM_ESC; // si les sorties sont aléatoires
    private final boolean RESOLVE; // s'il doit être résolu
    private boolean slow; // génération lente
    // CONTROLS
    private boolean resolve;
    private boolean _slow;
    private boolean game;




    // CONSTRUCTEUR
    protected Labytinthe(JFrame window, boolean simu, int w, int h, int c, boolean complex, boolean randomEsc, boolean resolve, boolean slowGen) {
        // REGLES
        running = true; // garder la boucle du run
        this.C = c; // dimension labyrinthe
        this.W = w/C*C; // largeur panel
        this.H = h/C*C; // hauteur panel
        this.COMPLEX = complex; // complexité
        this.RANDOM_ESC = randomEsc; // sorties aléatoires
        this.RESOLVE = resolve; // résolut
        this.slow = slowGen; // ralentir la génération (pour la voir)
        // LABY
        this.WINDOW = window;
        SIMU = simu; // simulation
        grid = new int[C][C]; // création de la grille du labyrinthe
        key = new KeyLaby(this); // écouteur de clavier
        // CONTROLS
        this.resolve = false;
        this._slow = true;
        // AFFICHAGE
        this.requestFocusInWindow(); // écrase les dimensions de la fenêtre avec celles du panel
        this.setPreferredSize(new Dimension(W, H)); // definition des dimensions
        this.setBackground(Color.black); // couleur du background
        this.setDoubleBuffered(true); // garde la dernière frame pendant l'affichage de la nouvelle
        this.addKeyListener(key); // ajoute un écouteur de clavier
        this.setFocusable(true);
        // THREADS
        gen = new Thread(new Runnable() { // créer un nouveau Thread pour la génération
            @Override
            public void run() { // tente de générer le laby
                try {genLaby(); } catch (InterruptedException e) {throw new RuntimeException(e);}
                // génère le labyrinthe
            }
        }); // thread la génération
        gen.start(); // lance la génération
        labyThread = new Thread(this); // initialise le laby
        if (SIMU) labyThread.start(); // le démarre (si la simulation est autorisé)
    }

    // GETTERS
    public KeyLaby getKey() {return key;}
    protected int[][] getLaby() {return grid;} // getter du laby
    public JFrame getWindow() {return WINDOW;}
    public int getW() {return W;}
    public int getH() {return H;}
    public int getC() {return C;}

    // UTILS
    private void set(int x, int y, int v) {grid[x][y] = v;} // set une cellule à la valeur "v"
    public int get(int x, int y) {return grid[x][y];} // récupère la valeur de la cellule demandée
    private int count(int v) { // compte le nombre "v" dans la grille
        int count = 0; // initialise le compte à 0
        for (int x = 0; x < C; x++)
            for (int y = 0; y < C; y++) if (get(x, y) == v) count++; // parcours la grille en comptant
        return count; // retourne le compte
    }
    private void fill(int v) { // remplie la grille avec "v"
        for (int i = 0; i < C; i++)
            for (int j = 0; j < C; j++) set(i, j, v); // parcours la grille et set chaque cellule à "v"
    }
    public void replace(int a, int b) { // remplace chaque "a" par "b"
        for (int i = 0; i < C; i++)
            for (int j = 0; j < C; j++) if (get(i, j) == a) set(i, j, b); // parcours la grille et remplace "a" par "b"
    }
    private boolean uni() { // vérifie que tous les chemins sont unifié à la même valeur
        int v = get(1, 1); // récupère une des valeurs "v"
        for (int i = 0; i < C; i++)
            for (int j = 0; j < C; j++)
                if (get(i, j) > 0 && get(i, j) != v)
                    return false; // parcours la grille et revoit FAUX si une seul fois on rencontre une valeur différente de "v"
        return true; // si toute la grille est scanné sans sortie négative, alors retourne VRAI
    }


    // RUN
    public void run() { // simulation avec actualisation 60fps
        int normalFps = 60; // FPS prévue
        double drawInterval = (double) (1000000000 / normalFps); // interval entre chaque dessin du panel
        double delta = 0.0;
        long lastTime = System.nanoTime();
        long timer = 0L; // compte le temps
        int fps = 0; // nombre de FPS passé depuis le début de la seconde
        long currentTime;
        long alertTime =0;
        while (running) { // tant que le Thread Simulation fonctionne
            currentTime = System.nanoTime(); // récupère le temps de maintenant
            delta += (double) (currentTime - lastTime) / drawInterval; // calcule le delta
            timer += currentTime - lastTime; // compte le temps
            lastTime = currentTime;
            if (delta >= 1.0) {
                alertTime = System.nanoTime();
                // EXECUTION
                this.update(); // UPDATE le labyrinthe
                this.repaint(); // AFFICHE le labyrinthe
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

    // UPDATE
    private void update() { // update de la simulation (60 x par seconde)


        if (!resolve && key.isResolve()) { // si n'est pas résolu et que demande à l'être
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {resolve();} catch (InterruptedException e) {throw new RuntimeException(e);} // résoudre
                }
            }).start();
        }

        if (_slow && key.isSlow()) { // si demande à être SLOW
            _slow = false; // désactive la possibilité de modifier SLOW
            slow = !slow; // modifie SLOW
        } else if (!key.isSlow()) _slow = true; // quand touche relâchée, re-active la possibilité de modifier SLOW

        if (key.isPlay()) game(); // lance la méthode d'initialisation du jeu, si "play" est validé

    }

    // DRAW
    protected void paintComponent(Graphics g) { // affichage
        super.paintComponent(g); // appel la méthode parent
        Graphics2D g2 = (Graphics2D) g; // converti en 2D


        Color path = Color.WHITE; // couleur des chemins
        Color wall = Color.BLACK; // couleur des murs
        Color sWall = Color.DARK_GRAY; // couleur des murs sélectionnés
        Color way = Color.GRAY; // couleur du chemin de résolution

        int cellW = W / C; // largeur d'une cellule
        int cellH = H / C; // hauteur d'une cellule

        for (int x = 0; x < C; x++) { // parcours X
            for (int y = 0; y < C; y++) { // parcours Y
                if (get(x, y) == 0) {
                    g2.setColor(path);
                } // si 0 alors couleur "path"
                else if (get(x, y) == -1) {
                    g2.setColor(wall);
                } // si -1 alors couleur "wall"
                else if (get(x, y) < -1) {
                    g2.setColor((game) ? wall : sWall);
                } // si <-1 alors couleur "sWall"
                else if (get(x, y) > 1) {
                    g2.setColor((game) ? path : Color.getHSBColor(get(x, y) % 360 / 360.0f, 1.0f, 1.0f));
                } // si >0 alors générer une couleur randiante en fonction de la valeur
                else if (get(x, y) == 1) {
                    g2.setColor((game) ? path : way);
                } // si 1 alors couleur "way"
                g2.fillRect(x * cellW, y * cellH, cellW, cellH); // dessine la cellule
            }
        }



        if (!game && key.isEscPressed()) { // si ESC est appuyé
            g2.setColor(Color.YELLOW); // couleur jaune
            g2.fillRect(0, 0, 70, 12); // encadrée
            g2.setColor(Color.RED); // couleur rouge
            g2.drawString("Fermeture...", 1, 10); // texte
        }
    }

    // GENERATION
    private void genLaby() throws InterruptedException { // génère le labyrinthe
        fill(-1); // emplie la grille de mur
        if (slow) Thread.sleep(5); // attend (en mode lent)
        // TROUER LE LES MURS ET LISTER CHAQUE MURS PERTINENT
        ArrayList<int[]> walls = new ArrayList<int[]>(); // liste les murs
        int index = 2; // valeur qui sera attribuée à chaque mur
        for (int i = 0; i < C; i++) { // parcours X
            if (slow) Thread.sleep(5); // attend (en mode lent)
            for (int j = 0; j < C; j++) { // parcours Y
                if (i % 2 != 0 && j % 2 != 0 && i != C - 1 && j != C - 1) { // créer les trous
                    set(i, j, index++); // set la cellule à "index" et augmente "index"
                }
                if (i > 0 && j > 0 && i < C - 1 && j < C - 1) { // sélectionne les murs pertinents
                    if (i % 2 == 0 && j % 2 != 0) { // si les trous sont à x-1 et x+1 du mur
                        set(i, j, -2); // mets sa valeur à -2 (pour être reconnu plus tard)
                        walls.add(new int[]{i, j}); // ajoute à la liste des murs
                    } else if (i % 2 != 0 && j % 2 == 0) { // sot les trous sont à y-1 et y+1 du mur
                        set(i, j, -3); // mets la valeur à -3 (pour être reconnu plus tard)
                        walls.add(new int[]{i, j}); // ajoute à la liste des murs
                    }

                }
            }
        }
        // CASSER DES MURS ENTRE LES TROUS ET UNIFIER LA VALEUR DE L'ESPACE (jusqu'à ce qu'il y ait un seul grand espace)
        Random rand = new Random(); // créer un générateur de nombre aléatoire
        while (!uni()) { // tant que le laby n'est pas uni (que chaque case (!=mur) soit accessible de chaque autre case (!=mur))
            if (slow) Thread.sleep(1); // attend (en mode lent)
            int[] selected = walls.get(rand.nextInt(walls.size())); // selection d'un des murs
            int x = selected[0]; // stock x du mur
            int y = selected[1]; // stock y du mur
            int v1, v2; // créer "v1" et "v2" pour stocker les valeurs de part et d'autre du mur
            walls.remove(selected); // retire le mur de la liste
            if (get(x, y) == -2 && get(x - 1, y) != get(x + 1, y)) { // si -2 (trous en x) et que les valeurs sont différentes
                v1 = get(x - 1, y); // set "v1"
                v2 = get(x + 1, y); // set "v2"
                if (count(v2) >= count(v1)) { // si "v2" domine "v1"
                    int tmp = v1; // inverse "v1" et "v2"
                    v1 = v2;
                    v2 = tmp;
                }
                set(x, y, v1); // set le mur à "v1" (dominant)
                replace(v2, v1); // remplace toutes les itérations de "v2" par "v1"
            } else if (get(x, y) == -3 && get(x, y - 1) != get(x, y + 1)) { // si -3 (trous en y) et que les valeurs sont différentes
                v1 = get(x, y - 1); // même chose...
                v2 = get(x, y + 1);
                if (count(v2) >= count(v1)) {
                    int tmp = v1;
                    v1 = v2;
                    v2 = tmp;
                }
                set(x, y, v1);
                replace(v2, v1);
            }
        }
        replace(get(1, 1), 0); // efface les traces de calcule
        replace(-2, -1); // normalise les murs
        replace(-3, -1); // normalise les murs
        // CREATION DES SORTIES
        if (slow) Thread.sleep(10); // attend (en mode lent)
        if (RANDOM_ESC) { // s'il faut choisir aléatoirement les sorties
            int pos1 = rand.nextInt(1, C / 2) * 2 - 1; // position 1 sur le mur
            int pos2 = rand.nextInt(1, C / 2) * 2 - 1; // position 2 sur le mur
            if (rand.nextBoolean()) { // si horizontal
                set(pos1, 0, 0); // place une sortie sur la bordure haut
                if (slow) Thread.sleep(10); // attend (en mode lent)
                set(pos2, C - 1, 0); // place une sortie sur la bordure basse
                // stocke les entrées
                entrAx = pos1;
                entrAy = 0;
                entrBx = pos2;
                entrBy = C - 1;
            } else { // sinon vertical
                set(0, pos1, 0); // place une sortie sur la bordure gauche
                if (slow) Thread.sleep(10); // attend (en mode lent)
                set(C - 1, pos2, 0); // place une sortie sur la bordure droite
                // stocke les entrées
                entrAx = 0;
                entrAy = pos1;
                entrBx = C - 1;
                entrBy = pos2;
            }
        } else { // si les sorties sont prédéfinis
            set(0, 1, 0); // place l'entrée A
            if (slow) Thread.sleep(10); // attend (en mode lent)
            set(C - 1, C - 2, 0); // place l'entrée B
            // stocke les entrées
            entrAx = 0;
            entrAy = 1;
            entrBx = C - 1;
            entrBy = C - 2;
        }
        // COMPLEXIFIE
        if (slow) Thread.sleep(15); // attend (en mode lent)
        if (COMPLEX)
            for (int i = 0; i < walls.size() / 10; i++) { // si complexe alors répète pour certains murs de "walls"
                if (slow) Thread.sleep(3); // attend (en mode lent)
                int[] selected = walls.get(rand.nextInt(walls.size())); // pioche un mur disponible
                int x = selected[0]; // stock son x
                int y = selected[1]; // stock son y
                walls.remove(selected); // le retire de la liste
                set(x, y, 0); // brise le mur
            }
        // RESOLUTION
        if (RESOLVE) resolve(); // si doit être résolu, appel la méthode de résolution
    }

    // RESOLUTION
    private void resolve() throws InterruptedException { // méthode qui résout le labyrinthe
        resolve = true; // confirme que le labyrinthe est résolu
        int index = 2; // set index, valeur de départ du compte des cases
        set(entrAx, entrAy, index); // set l'entrée A à "index"
        while (get(entrBx, entrBy) == 0) { // tant que l'entrée B n'est pas atteinte
            if (slow) Thread.sleep(10); // attend (en mode lent)
            for (int x = 0; x < C; x++)
                for (int y = 0; y < C; y++)
                    if (get(x, y) == index) { // pour chaque case, si elle a la même valeur que "index"
                        if (x < C - 1 && get(x + 1, y) == 0)
                            set(x + 1, y, index + 1); // mets la case à droite à "index"+1 si possible
                        if (x > 0 && get(x - 1, y) == 0)
                            set(x - 1, y, index + 1); // mets la case à gauche à "index"+1 si possible
                        if (y < C - 1 && get(x, y + 1) == 0)
                            set(x, y + 1, index + 1); // mets la case en dessous à "index"+1 si possible
                        if (y > 0 && get(x, y - 1) == 0)
                            set(x, y - 1, index + 1); // mets la case au dessus à "index"+1 si possible
                    }
            index++; // incrémente "index"
        }
        int x = entrBx; // stock X de B
        int y = entrBy; // stock Y de B
        for (int i = index; i >= 0; i--) { // répète "index" fois (donc distance entre A et B)
            if (slow) Thread.sleep(10); // attend (en mode lent)
            set(x, y, 1); // set la valeur de la case courante à 1
            // décide de la prochaine case, en prenant la valeur inférieure
            if (x + 1 < C - 1 && get(x + 1, y) == i) x++;
            else if (x - 1 > 0 && get(x - 1, y) == i) x--;
            else if (y + 1 < C - 1 && get(x, y + 1) == i) y++;
            else if (y + 1 > 0 && get(x, y - 1) == i) y--;
        }
        set(entrAx, entrAy, 1); // set A à 1
        if (slow) Thread.sleep(3); // attend (en mode lent)
        for (int i = 0; i < C; i++) for (int j = 0; j < C; j++) if (get(i, j) > 1) set(i, j, 0); // parcours la grille et passe à 0 toute valeur >1
        // efface les traces de calcul

    }

    // JEU
    protected void game() {
        game = true; // confirme le lancement du jeu (desactive l'écoute d'ESC)
        slow = false; // désactive le mode lent
        try {gen.join();} catch (InterruptedException e) {e.printStackTrace();} // attend la fin de la génération
        if (!resolve) { // résout si ce n'est pas déjà fait
            Thread res = new Thread(new Runnable() { // Thread la résolution (pour ne pas freeze)
                public void run() {
                    try {resolve();} catch (InterruptedException e) {throw new RuntimeException(e);}
                    // résoudre
                }
            });
            res.start();
            try {res.join();} catch (InterruptedException e) {throw new RuntimeException(e);} // attend la fin de la résolution
        }
        // LIBERATION DES RESSOURCES
        running = false; // désactive la simulation du labyrinthe
        this.removeKeyListener(key); // désactive le KeyListener
        key = null; // efface le KeyListener
        gen = null; // efface le Thread de génération
        labyThread = null; // efface le Thread de simulation
        // FENÊTRE
        WINDOW.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ferme à la fermeture de la fenêtre
        WINDOW.setResizable(false); // interdit la modification des dimensions
        WINDOW.setTitle("Je te vois..."); // définit le titre en fonction de l'id
        // LABYRINTHE
        //replace(1, 0);
        Game game = new Game(WINDOW, getLaby(), C, entrAx, entrAy, entrBx, entrBy); // créer le jeu
        WINDOW.setContentPane(game); // remplace le labyrinthe sur la fenêtre
        WINDOW.pack(); // compile la fenêtre
        game.requestFocusInWindow(); // attribut au jeu la priorité
        // AFFICHAGE
        WINDOW.setLocationRelativeTo(null); // centre la fenêtre
        WINDOW.setVisible(true); // affiche la fenêtre
    }



}
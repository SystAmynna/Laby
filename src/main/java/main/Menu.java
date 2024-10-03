package main;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Menu extends JPanel implements Runnable{

    // OBJET
    private boolean running;
    private final JFrame window;
    private Thread mainThread; // Thread d'execution pour le menu
    private KeyMenu key = new KeyMenu(); // créer l'écouteur de clavier
    //VARIABLES
    private int selected; // sélection du menu
    private int[][] bgLaby; // labyrinthe de fond
    private boolean alert; // sert pour alerte visuel
    private int nbOptn; // nombre d'options pour "selected"
    private boolean trolled; // s'est fait trollé ?
    // SETTINGS
    private int w; // largeur fenêtre
    private int h; // hauteur fenêtre
    private final int MIN_D; // dimension minimum de la fenêtre
    private int c; // dimension labyrinthe
    private final int MIN_C; // dimension minimum du labyrinthe
    private boolean complex; // mode de labyrinthe
    private boolean randomEsc; // mode de sorties
    private boolean resolve; // pré résolut
    private boolean slowGen; // génération lente
    // CONTROLES
    private boolean up, down, left, right; //  controles (pour éviter le spam)
    private boolean valid, back; // même chose


    // CONSTRUCTEUR
    protected Menu(JFrame window) {
        running = true;
        // REGLES PANEL
        this.window = window;
        this.setPreferredSize(new Dimension(300, 300)); // dimensions panel
        this.setBackground(Color.black); // couleur de fond (noir)
        this.setDoubleBuffered(true); // garde la frame précédente
        this.addKeyListener(this.key); // ajoute "key" au panel
        this.setFocusable(true);
        this.requestFocusInWindow(); // redimensionne la fenêtre suivant les dimensions du panel
        // VARIABLES
        selected = 0; // sélection
        alert = false; // alerte
        nbOptn = 8; // nombre d'option
        trolled = false; // trollé
        // SETTINGS
        w = 800; // largeur fenêtre
        h = 800; // hauteur fenêtre
        MIN_D = 300; // MINIMUM taille fenêtre
        c = 101; // dimension labyrinthe
        MIN_C = 11; // MINIMUM taille labyrinthe
        complex = true; // mode par défaut
        randomEsc = false; // configuration sortie par défaut
        resolve = false;
        // CONTROLES
        up = true; // haut
        down = true; // bas
        left = true; // gauche
        right = true; // droite
        valid = true; // valider
        back = true; // retour
        selected = 0; // actuellement sélectionné
        // GEN BACKGROUND LABY
        // créer un laby, sans simulation, pour en récupérer la grille, pour le background
        bgLaby = new Labytinthe(window, false, 100, 100, 101, true, false, false, false).getLaby();
        mainThread = new Thread(this); // definit le Thread
        mainThread.start(); // démarre le Thread
    }

    // RUN
    public void run() {
        int normalFps = 30;
        double drawInterval = (double)(1000000000 / normalFps);
        double delta = 0.0;
        long lastTime = System.nanoTime();
        long timer = 0L;
        long currentTime;
        while(running) {
            currentTime = System.nanoTime();
            delta += (double)(currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;
            if (delta >= 1.0) {

                this.update();
                this.repaint();

                --delta;
            }
            if (timer >= 1000000000L) {
                timer = 0L;
            }
        }
    }

    // UPDATE
    private void update() {
        //VISUEL
        alert = false; // desactive l'alerte
        // CONTROLES
        if (key.isUp() && up) { // si la touche est pressé ET pas encore executé
            up = false; // a été executé
            goUp(); // action
        } else if (!key.isUp()) up = true; // touche relaché donc re-autorise execution
        if (key.isDown() && down) { // même procédé
            down = false;
            goDown();
        } else if (!key.isDown()) down = true;
        if ((key.isLeft()) && left) { // même procédé
            left = false;
            goLeft();
        } else if (!key.isLeft()) left = true;
        if ((key.isRight()) && right) { // même procédé
            right = false;
            goRight();
        } else if (!key.isRight()) right = true;
        if ((key.isValid()) && valid) { // même procédé
            valid = false;
            goValid();
        } else if (!key.isValid()) valid = true;
    }

    // AFFICHAGE
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // appel la classe mère
        Graphics2D g2 = (Graphics2D) g; // convertie en graphique 2D
        int marge = 30; // limite gauche d'écriture
        int bort = 80; // limite haut d'écriture
        int delta = 15; // interval entre chaque ligne
        int alinea = 10; // alinéa
        Color defaultColor = Color.WHITE; // couleur par défaut
        Color selectedColor = (alert) ? Color.RED : Color.CYAN; // couleur de sélection
        String _complex = (complex) ? "COMPLEXE" : "SIMPLE"; // traduction valeur complex
        String _randomEsc = (randomEsc) ? "ALEATOIRE" : "OPPOSE"; // traduction valeur randomEsc
        String _resolve = (resolve) ? "OUI" : "NON"; // traduction valeur résolution
        String _slowGen = (slowGen) ? "LENT" : "NORMAL"; // traduction valeur slow

        // BG
        int _c = 5; // taille d'une case
        for (int i = 0; i < 101; i++) for (int j = 0; j < 101; j++) { // parcours le laby
            if (bgLaby[i][j] == 0) g2.setColor(Color.DARK_GRAY); // couleur chemins
            else g2.setColor(Color.BLACK); // couleur murs
            g2.fillRect(i * _c - 2*_c, j * _c - 2 * _c, _c, _c); // dessiner la case
        }

        // TITRE
        g2.setColor(Color.RED); // couleur titre + sous-titre
        g2.drawString("GENERATEUR DE LABYRINTHE", delta, delta*2); // titre
        g2.drawString("Fait par SystAmynna", delta+alinea, delta*3); // sous-titre

        // TUTO
        g2.setColor(Color.ORANGE); // couleur tuto
        g2.drawString("[Z],[Q],[S],[D] ou les fleches pour selectionner", delta, bort + delta*14); // tuto

        // MENU
        // variation des couleurs en fonction de la selection
        g2.setColor(defaultColor); // couleur par défaut
        g2.drawString("DIMENSIONS FENETRE:", marge, bort);
        if (selected == 1) g2.setColor(selectedColor);
        g2.drawString("Largeur: " + w, marge+alinea, bort + delta);
        g2.setColor(defaultColor);
        if (selected == 2) g2.setColor(selectedColor);
        g2.drawString("Hauteur: " + h, marge+alinea, bort + delta*2);
        g2.setColor(defaultColor);
        g2.drawString("PARAMETRES LABYRINTHE:", marge, bort + delta*4);
        if (selected == 3) g2.setColor(selectedColor);
        g2.drawString("Dimension: " + c, marge+alinea, bort + delta*5);
        g2.setColor(defaultColor);
        if (selected == 4) g2.setColor(selectedColor);
        g2.drawString("Type de labyrinthe: " + _complex, marge+alinea, bort + delta*6);
        g2.setColor(defaultColor);
        if (selected == 5) g2.setColor(selectedColor);
        g2.drawString("Sorties: " + _randomEsc, marge+alinea, bort + delta*7);
        g2.setColor(defaultColor);
        if (selected == 6) g2.setColor(selectedColor);
        g2.drawString("Resolu: " + _resolve, marge+alinea, bort + delta*8);
        g2.setColor(defaultColor);
        if (selected == 7) g2.setColor(selectedColor);
        g2.drawString("Vitesse de generation: " + _slowGen, marge+alinea, bort + delta*9);
        g2.setColor(defaultColor);
        if (selected == 8) g2.setColor(selectedColor);
        if (!trolled) g2.drawString("Parametre mystere [?]", marge+alinea, bort + delta*10);
        g2.setColor(defaultColor);
        if (selected == 0) g2.setColor(selectedColor);
        g2.drawString("[GENERER]", marge+alinea*2, bort + delta*12);

        if (key.isEscPressed()) { // affiche la tentative de fermeture
            g2.setColor(Color.RED);
            g2.drawString("Fermeture...", 1, 10);
        }
    }

    // ACTIONS
    private void goUp() { // action haut
        selected--; // fais varier la sélection
        if (selected < 0) selected = nbOptn; // garder la sélection dans les bornes
    }
    private void goDown() { // action bas
        selected++;
        if (selected > nbOptn) selected = 0;
    }
    private void goLeft() {selectDir(false);} // action gauche
    private void goRight() {selectDir(true);} // action droite
    private void selectDir(boolean dir) { // sélectionner l'action (paramétrée) en fonction de "selected"
        int ajust = (dir) ? 1 : -1; // multiplicateur permettant l'addition ou soustraction
        switch (selected) { // action en fonction de la sélection
            case 1:
                w = (w < MIN_D || (w == MIN_D && !dir)) ? MIN_D : w + 10 * ajust; // ajuster la largeur
                alert = w < MIN_D || (w == MIN_D && !dir); // active l'alerte en cas de tentative de dépassement
                break;
            case 2:
                h = (h < MIN_D || (h == MIN_D && !dir)) ? MIN_D : h + 10 * ajust; // ajuster la hauteur
                alert = h < MIN_D || (h == MIN_D && !dir);
                break;
            case 3:
                c = (c < MIN_C || (c == MIN_C && !dir)) ? MIN_C : c + 2 * ajust; // ajuster la dimension du labyrinthe
                alert = c < MIN_C || (c == MIN_C && !dir);
                break;
        }
    }
    private void goValid() {select(true);} // action valider
    private void select(boolean r) { // select pour valid et back
        switch (selected) {
            case 0:
                if (r) creerLaby(w, h, c, complex, randomEsc, resolve); // si valid créer un labyrinthe
                else alert = true; // sinon alerte
                break;
            case 4: complex = !complex; break; // reverse complex
            case 5: randomEsc = !randomEsc; break; // reverse randomEsc
            case 6:
                resolve = !resolve; // définit la valeur
                break;
            case 7:
                slowGen = !slowGen; // definit la valeur
                break;
            case 8: // mon petit paramètre secret
                try {
                    Desktop desktop = Desktop.getDesktop();
                    URI uri = new URI("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
                    desktop.browse(uri);
                    nbOptn--;
                    trolled = true;
                    selected = 7;
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
        }
    }


    // MANIPULATION DU LABYRINTHE
    private void creerLaby(int w, int h, int c, boolean complex, boolean randomEsc, boolean resolve) { // créer un labyrinthe
        // LIBERATION DES RESSOURCES
        running = false;
        this.removeKeyListener(key);
        key = null;
        mainThread = null;
        // FENÊTRE
        window.setSize(w, h); // definition les dimensions
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ferme à la fermeture de la fenêtre
        window.setResizable(false); // interdit la modification des dimensions
        window.setTitle("Labytinthe"); // définit le titre en fonction de l'id
        // LABYRINTHE
        Labytinthe laby = new Labytinthe(window, true, w, h, c, complex, randomEsc, resolve, slowGen); // créer le labyrinthe
        window.setContentPane(laby); // remplace par le labyrinthe sur la fenêtre
        laby.requestFocusInWindow();
        window.pack(); // compile la fenêtre
        // AFFICHAGE
        window.setLocationRelativeTo(null); // centre la fenêtre
        window.setVisible(true); // affiche la fenêtre
    }


}

package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyLaby implements KeyListener {

    private final Labytinthe LABY; // objet Labyrinthe
    private boolean resolve, slow; // Touches pour résoudre et ralentir
    private boolean escPressed; // si ESC est appuyé
    private final int quitDelay = 30; // delay au bout du quel le programme se ferme
    private int play = 0; // compte des touches de "PLAY" pour lancer le jeu
    private int quitCooldown; // compte du nombre d'update avant la fermeture


    // CONSTRUCTEUR
    protected KeyLaby(Labytinthe laby) {this.LABY = laby;}  // attribut laby

    @Override
    public void keyTyped(KeyEvent e) {} // detection de touche tapée

    @Override
    public void keyPressed(KeyEvent e) { // touches pressées
        int code = e.getKeyCode(); // converti la touche en "int"

        if (code == KeyEvent.VK_ESCAPE) { // ESC
            escPressed = true; // valide que ESC est appuyé
            quitCooldown++; // ajoute au cooldown
            if (quitCooldown >= quitDelay) System.exit(0); // si le Delay est atteint, quitte le programme
        }

        if (code == KeyEvent.VK_R) resolve = true; // R pour résoudre
        if (code == KeyEvent.VK_S) slow = true; // S pour le mode lent

        }

    @Override
    public void keyReleased(KeyEvent e) { // touches relachées
        int code = e.getKeyCode(); // converti la touche en "int"

        if (code == KeyEvent.VK_ESCAPE) { // ESC
            escPressed = false; // ESC n'est plus appyé
            quitCooldown = 0; // reset le cooldown
        }

        if (code == KeyEvent.VK_R) resolve = false; // R relaché
        if (code == KeyEvent.VK_S) slow = false; // S relaché

        if (play == 0 && code == KeyEvent.VK_P) play = 1; // detecte P
        else if (play == 1 && code == KeyEvent.VK_L) play = 2; // detecte L
        else if (play == 2 && code == KeyEvent.VK_A) play = 3; // detecte A
        else if (play == 3 && code == KeyEvent.VK_Y) { // detecte Y
            play = 4; // valide "play"
        } else play = 0; // si une autre touche est detecté, reset le compte
    }

    // GETTERS
    protected boolean isEscPressed() {return escPressed;} // récupère ESC
    protected boolean isResolve() {return resolve;} // récupère R, pour la résolution
    protected boolean isSlow() {return slow;} // récupère S, pour le mode lent
    protected boolean isPlay() {return play == 4;}
}

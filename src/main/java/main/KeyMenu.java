package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyMenu implements KeyListener {

    // TOUCHES
    private boolean up, down, left, right; // directions
    private boolean valid, back, escPressed; // actions
    private final int quitDelay = 30;
    private int quitCooldown;

    @Override
    public void keyTyped(KeyEvent e) {}

    // TOUCHE ENFONCE
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode(); // converti la touche en int
        switch (e.getKeyCode()) { // met la touche en vrai
            case KeyEvent.VK_UP:
            case KeyEvent.VK_Z: up = true; break; // haut
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S: down = true; break; // bas
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_Q: left = true; break; // gauche
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D: right = true; break; // droite
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE: valid = true; break; // valider
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_SHIFT: back = true; break; // retour
        }
        if (code == KeyEvent.VK_ESCAPE) {
            escPressed = true;
            quitCooldown++;
            if (quitCooldown >= quitDelay) System.exit(0);
        }

    }
    // TOUCHE RELÂCHE
    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode(); // converti la touche en int
        switch (e.getKeyCode()) { // met la touche en faux
            case KeyEvent.VK_UP:
            case KeyEvent.VK_Z: up = false; break; // haut
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S: down = false; break; // bas
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_Q: left = false; break; // gauche
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D: right = false; break; // droite
            case KeyEvent.VK_ENTER:
            case KeyEvent.VK_SPACE: valid = false; break; // valider
            case KeyEvent.VK_BACK_SPACE:
            case KeyEvent.VK_SHIFT: back = false; break; // retour
        }
        if (code == KeyEvent.VK_ESCAPE) {
            escPressed = false;
            quitCooldown = 0;
        }
    }


    // GETTERS
    protected boolean isEscPressed() {return escPressed;}
    protected boolean isUp() {return up;}
    protected boolean isDown() {return down;}
    protected boolean isLeft() {return left;}
    protected boolean isRight() {return right;}
    protected boolean isValid() {return valid;}
    protected boolean isBack() {return back;}
}

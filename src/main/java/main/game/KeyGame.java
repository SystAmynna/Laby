package main.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyGame implements KeyListener {

    private final Game GAME;
    private boolean up, down, left, right, shift, map;
    private boolean escPressed;
    private int quitCooldown = 0, quitDelay = 30;
    private boolean canMoveUp = false, canMoveDown = false, canMoveLeft = false, canMoveRight = false;

    protected KeyGame(Game game) {
        this.GAME = game;
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    private void setKeys(int code, boolean v) {
        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_UP) up = v;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) down = v;
        if (code == KeyEvent.VK_Q || code == KeyEvent.VK_LEFT) left = v;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) right = v;
        if (code == KeyEvent.VK_SHIFT) shift = v;
        if (code == KeyEvent.VK_SPACE) map = v;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        setKeys(code, true);

        if (code == KeyEvent.VK_ESCAPE) {
            escPressed = true;
            quitCooldown++;
            if (quitCooldown >= quitDelay) System.exit(0);
        }

        if (GAME.getNoEnergy()) {
            int add = 5;

            if (canMoveUp && code == KeyEvent.VK_Z) {
                canMoveUp = false;
                GAME.addQte(add);
            }
            if (canMoveDown && code == KeyEvent.VK_S) {
                canMoveDown = false;
                GAME.addQte(add);
            }
            if (canMoveLeft && code == KeyEvent.VK_Q) {
                canMoveLeft = false;
                GAME.addQte(add);
            }
            if (canMoveRight && code == KeyEvent.VK_D) {
                canMoveRight = false;
                GAME.addQte(add);
            }
        }



    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        setKeys(code, false);

        if (GAME.getNoEnergy()) {
            if (!canMoveUp && code == KeyEvent.VK_Z) canMoveUp = true;
            if (!canMoveDown && code == KeyEvent.VK_S) canMoveDown = true;
            if (!canMoveLeft && code == KeyEvent.VK_Q) canMoveLeft = true;
            if (!canMoveRight && code == KeyEvent.VK_D) canMoveRight = true;
        }

        if (code == KeyEvent.VK_ESCAPE) {
            quitCooldown = 0;
            escPressed = false;
        }
    }

    protected boolean isUp() {return up;}
    protected boolean isDown() {return down;}
    protected boolean isLeft() {return left;}
    protected boolean isRight() {return right;}
    protected boolean isShift() {return shift;}
    protected boolean isEscPressed() {return escPressed;}
    protected boolean isMap() {return map;}


}

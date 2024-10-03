package main.game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Ombre {

    private final Game GAME;
    private int moveNum;
    private int shadowNum;
    private ArrayList<int[]> move;
    private ArrayList<int[]> shadow;
    private BufferedImage [] sprites;
    private ArrayList<BufferedImage> shadowSprite;
    private int delay;
    private int cooldown;


    protected Ombre(Game game) {
        this.GAME = game;
        move = new ArrayList<int[]>();
        shadow = new ArrayList<int[]>();
        shadowSprite = new ArrayList<BufferedImage>();

        sprites = new BufferedImage[10];

        try {
            sprites[0] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/shadow/o1.png")));
            sprites[1] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/shadow/o2.png")));
            sprites[2] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/shadow/o3.png")));
            sprites[3] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/shadow/o4.png")));
            sprites[4] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/shadow/o5.png")));
            sprites[5] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/shadow/o6.png")));
            sprites[6] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/shadow/o7.png")));
            sprites[7] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/shadow/o8.png")));
            sprites[8] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/shadow/o9.png")));
            sprites[9] = ImageIO.read(Objects.requireNonNull(this.getClass().getResourceAsStream("/shadow/o10.png")));

        } catch (IOException e) {throw new RuntimeException(e);}

        reset();
    }

    private void reset() {
        if (!move.isEmpty()) move.clear();
        if (!shadow.isEmpty()) shadow.clear();
        if (!shadowSprite.isEmpty()) shadowSprite.clear();
        Random rand  = new Random();
        delay = rand.nextInt(6,30);
        cooldown = delay;

        int minX = GAME.getPLAYER().getScreenX() - GAME.getSCREEN_WIDTH()/4;
        int maxX = GAME.getPLAYER().getScreenX() + GAME.getSCREEN_WIDTH()/4;
        int minY = GAME.getPLAYER().getScreenY() - GAME.getSCREEN_HEIGHT()/4;
        int maxY = GAME.getPLAYER().getScreenY() + GAME.getSCREEN_HEIGHT()/4;

        // move
        for (int i = 0; i < rand.nextInt(1, 10); i++) move.add(new int[]{rand.nextInt(minX, maxX), rand.nextInt(minY, maxY)});
        // shadow
        for (int i = 0; i < rand.nextInt(3, 16); i++) {
            shadow.add(new int[]{rand.nextInt(minX, maxX), rand.nextInt(minY, maxY)});
            shadowSprite.add(sprites[rand.nextInt(sprites.length)]);
        }

        GAME.addQte(-5);

    }

    protected void draw(Graphics2D g2) {

        if (cooldown > 0) cooldown--;
        if (cooldown == 0) reset();

        AlphaComposite oT = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f); // transparence HUD
        g2.setComposite(oT);

        for (int i = 0; i < shadow.size(); i++) g2.drawImage(shadowSprite.get(i), shadow.get(i)[0], shadow.get(i)[1], GAME.getPLAYER_SIZE(), GAME.getPLAYER_SIZE(), null);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(Color.RED);
        for (int[] ints : move) g2.drawString("MOVE", ints[0], ints[1]);

        g2.setComposite(AlphaComposite.SrcOver);


    }


}

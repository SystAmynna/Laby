package main;

import javax.swing.*;

public class App {
    public static void main(String[] args) {

        // FENÊTRE MENU
        JFrame window = new JFrame(); // Créer la fenêtre Menu
        window.setResizable(false); // n'est pas redimensionnable
        window.setSize(300, 300); // dimensions (temporaire, car gérer par panel)
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ferme le programme à la fermeture de la fenêtre
        window.setTitle("Generateur de labyrinthe"); // définit le titre
        window.setLocationRelativeTo(null); // centre la fenêtre
        // PANEL MENU
        Menu menu = new Menu(window); // créer le panel menu
        window.add(menu); // ajoute menu à la fenêtre
        window.pack(); // compile la fenêtre
        // AFFICHAGE
        window.setVisible(true); // affiche

    }
}

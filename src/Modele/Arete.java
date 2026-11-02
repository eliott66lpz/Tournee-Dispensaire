package Modele;

import java.awt.Color;

/**
 * Représente un chemin entre deux centres medicaux. 
 * Stocke les caracteristiques de distance, de duree et de fiabilite de la liaison.
 *@author eliott
*/
public class Arete {

    private int distance;
    private int fiabilite;
    private int duree;
    private Sommet s_dep;
    private Sommet s_arr;
    private Color couleur = new Color(170, 170, 170);

    /**
     * Constructeur permettant d'initialiser une nouvelle arête du graphe avec ses attributs.
     * Cette arête relie un sommet de départ à un sommet d'arrivée 
     * @param distance La distance métrique entre les deux sommets.
     * @param fiabilite L'indice ou le taux de fiabilité/sécurité de la liaison.
     * @param duree Le coût temporel nécessaire pour traverser cette arête.
     * @param dep Le sommet d'origine de l'arête.
     * @param arr Le sommet de destination de l'arête.
     */
    public Arete(int distance, int fiabilite, int duree, Sommet dep, Sommet arr) {
        this.distance = distance;
        this.fiabilite = fiabilite;
        this.duree = duree;
        this.s_dep = dep;
        this.s_arr = arr;
    }

    /**
     * Retourne la distance.
     * @return la distance sous forme de double
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Retourne la fiabilite.
     * @return le niveau de fiabilite
     */
    public int getFiabilite() {
        return fiabilite;
    }

    /**
     * Retourne la duree.
     * @return la duree sous forme de double
     */
    public double getDuree() {
        return duree;
    }

    /**
     * Retourne le sommet de depart.
     * @return le sommet de depart
     */
    public Sommet getSommet_dep() {
        return s_dep;
    }

    /**
     * Retourne le sommet d'arrivee.
     * @return le sommet d'arrivee
     */
    public Sommet getSommet_arr() {
        return s_arr;
    }
    
    /**
     * Retourne la couleur
     * @return la couleur
     */
    public Color getCouleur() {
        return couleur;
    }

    /**
     * Modifie la couleur de l'objet.
     * @param couleur la nouvelle couleur à appliquer
     */
    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modele;

import java.awt.Color;

/**
 * Représente un sommet du graphe, correspondant à un centre médical.
 * Chaque sommet est défini par un identifiant unique et son type d'infrastructure.
 * @author eliot
 */
public class Sommet {
    private final String numero;
    private final String type;
    private Color couleur;

    /**
     * Crée un nouveau sommet (centre médical).
     * @param numero l'identifiant unique du centre
     * @param type le type de structure ("M", "O" ou "N")
     */
    public Sommet(String numero, String type) {
        this.numero = numero;
        this.type = type;
    }

    /**
     * Retourne l'identifiant unique du centre.
     * @return le numéro du sommet
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Retourne le type de structure médicale.
     * @return le type du sommet ("M", "O" ou "N")
     */
    public String getType() {
        return type;
    }
    
    /**
     * Retourne la couleur de la structure médicale
     * @return la couleur
     */
    public Color getCouleur() {
        return couleur;
    }

    /**
     * Permet de modifier la couleur de la structure médicale
     * @param couleur 
     */
    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }
    
    
    
    
}

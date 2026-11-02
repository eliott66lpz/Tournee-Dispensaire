package Modele;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 * Permet la lecture et le chargement des données du fichier CSV.
 */
public class OuvertureFichier {

    /**
     * Lit un fichier CSV ligne par ligne pour charger les données dans le graphe.
     * Cette méthode crée les sommets et configure les arêtes avec leurs caractéristiques de distance, de risque et de durée.
     * @param filePath Le chemin d'accès complet du fichier CSV à charger.
     * @param gr L'instance de la classe Graphe où les sommets et les arêtes seront ajoutés.
     */
    public void chargerAttributs(String filePath, Graphe gr) {
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int numLigne = 0;

            while ((line = br.readLine()) != null) {
                numLigne++;
                
                if (line.startsWith("//") || line.trim().isEmpty()) {
                    continue;
                }

                String[] colonnes = line.split(";", -1);
                if (colonnes.length < 2) {
                    JOptionPane.showMessageDialog(null, "Erreur : OuvertureFichier, Ligne " + numLigne + " : Structure incorrecte.", "Structure Incorrecte", JOptionPane.ERROR_MESSAGE);
                    continue;
                }

                String id = colonnes[0].trim();
                String type = colonnes[1].trim().toUpperCase();

                if (!type.equals("M") && !type.equals("O") && !type.equals("N")) {
                    JOptionPane.showMessageDialog(null, "Erreur : OuvertureFichier, Ligne " + numLigne + " : Le type du sommet " + id + " est inconnu !", "Type Inconnu", JOptionPane.ERROR_MESSAGE);
                    continue; 
                }

                gr.ajouterSommet(id, type);

                for (int i = 2; i < colonnes.length; i++) {
                    String cellule = colonnes[i].trim();
                    
                    if (cellule.equals("0") || cellule.isEmpty()) {
                        continue;
                    }

                    String[] parts = cellule.split(",");
                    
                    if (parts.length != 3) {
                        JOptionPane.showMessageDialog(null, "Erreur Ligne " + numLigne + " : La cellule '" + cellule + "' est mal formée (doit contenir 3 valeurs).");
                        continue; 
                    }

                    try {
                        int distance = (int) Double.parseDouble(parts[0].trim());
                        int risque   = (int) Double.parseDouble(parts[1].trim());
                        int duree    = (int) Double.parseDouble(parts[2].trim());
                        
                        String arr = "S" + (i - 1);

                        gr.ajouterArete(distance, risque, duree, id, arr);
                        gr.ajouterArete(distance, risque, duree, arr, id);

                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(null, "Erreur de format de nombre dans OuvertureFichier à la ligne "  + numLigne + " dans : " + cellule, "Format Invalide", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            
        } catch (IOException e) {
           JOptionPane.showMessageDialog(null, "Impossible d'ouvrir ou de lire le fichier : " + filePath, "Erreur de lecture", JOptionPane.ERROR_MESSAGE);
        }
    }
}
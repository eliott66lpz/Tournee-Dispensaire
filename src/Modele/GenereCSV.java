package Modele;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Permet de générer les fichiers CSV contenant les résultats des tournées pour
 * 1 et 2 camions selon le format demandé.
 *
 * @author Eliott
 */
public class GenereCSV {

    private static final String SEPARATEUR = ";";
    private static final String CHEMIN_FICHIER = "fichierEvaluation/";
    private static final String EXTENSION_FICHIER = ".csv";

    private final List<String> lignes1Camion = new ArrayList<>();
    private final List<String> lignes2Camions = new ArrayList<>();

    /**
     * Constructeur qui vérifie si le dossier de destination existe. Si le
     * dossier n'existe pas, il le crée automatiquement.
     */
    public GenereCSV() {
        File dossier = new File(CHEMIN_FICHIER);
        if (!dossier.exists()) {
            dossier.mkdirs();
        }
    }

    /**
     * Enregistre les résultats d'un test pour le cas d'un seul camion. Cette
     * méthode extrait uniquement les numéros des sommets du parcours.
     *
     * @param nomFichier Le nom du fichier CSV d'origine.
     * @param dureeGlouton La durée obtenue avec l'algorithme glouton.
     * @param dureeMeilleure La meilleure durée trouvée.
     * @param route1 La liste des sommets visités.
     */
    public void ajouterLigne1Camion(String nomFichier, double dureeGlouton, double dureeMeilleure, List<String> route1) {
        if (!nomFichier.endsWith(".csv")) {
            nomFichier += ".csv";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("1");
        if (route1 != null) {
            for (String id : route1) {
                sb.append(SEPARATEUR);
                String numero = id.replaceAll("[^0-9]", "");
                sb.append(numero.isEmpty() ? id : numero);
            }
        }

        String ligne = nomFichier + SEPARATEUR
                + (int) dureeGlouton + SEPARATEUR
                + (int) dureeMeilleure + SEPARATEUR
                + sb.toString();

        lignes1Camion.add(ligne);
    }

    /**
     * Enregistre les résultats d'un test pour le cas à deux camions. Elle
     * rassemble les deux parcours et les aligne.
     *
     * @param nomFichier Le nom du fichier CSV d'origine.
     * @param dureeCamion1 La durée du trajet pour le premier camion.
     * @param dureeCamion2 La durée du trajet pour le deuxième camion.
     * @param route1 Le parcours du premier camion.
     * @param route2 Le parcours du deuxième camion.
     */
    public void ajouterLigne2Camions(String nomFichier, double dureeCamion1, double dureeCamion2, List<String> route1, List<String> route2) {
        if (!nomFichier.endsWith(".csv")) {
            nomFichier += ".csv";
        }

        List<String> sommetsDuParcours = new ArrayList<>();

        sommetsDuParcours.add("1");
        if (route1 != null) {
            for (String id : route1) {
                String numero = id.replaceAll("[^0-9]", "");
                sommetsDuParcours.add(numero.isEmpty() ? id : numero);
            }
        }

        sommetsDuParcours.add("1");
        if (route2 != null) {
            for (String id : route2) {
                String numero = id.replaceAll("[^0-9]", "");
                sommetsDuParcours.add(numero.isEmpty() ? id : numero);
            }
        }

        StringBuilder sectionParcours = new StringBuilder();
        for (int i = 0; i < 13; i++) {
            if (i < sommetsDuParcours.size()) {
                sectionParcours.append(sommetsDuParcours.get(i));
            }
            if (i < 12) {
                sectionParcours.append(SEPARATEUR);
            }
        }

        String ligne = nomFichier + SEPARATEUR
                + (int) dureeCamion1 + SEPARATEUR
                + (int) dureeCamion2 + SEPARATEUR
                + sectionParcours.toString();

        lignes2Camions.add(ligne);
    }

    /**
     * Écrit les lignes enregistrées dans le fichier CSV final pour 1 camion.
     *
     * @param nomFichier Le nom du fichier à créer ou modifier.
     * @throws IOException Si une erreur survient lors de l'écriture du fichier.
     */
    public void ecrireFichier1Camion(String nomFichier) throws IOException {
        String entete = "nomFichier;duréeAlgoGlouton1camion;meilleureDurée1camion;ordreMeilleureDurée;;;;;;;;;;;";
        ecrireLignes(nomFichier, entete, lignes1Camion);
    }

    /**
     * Écrit les lignes enregistrées dans le fichier CSV final pour 2 camions.
     *
     * @param nomFichier Le nom du fichier à créer ou modifier.
     * @throws IOException Si une erreur survient lors de l'écriture du fichier.
     */
    public void ecrireFichier2Camions(String nomFichier) throws IOException {
        String entete = "nomFichier;DuréeCamion1;DuréeCamion2;;;;;;;;;;;;;";
        ecrireLignes(nomFichier, entete, lignes2Camions);
    }

    /**
     * Méthode interne qui gère l'écriture dans le fichier sur le disque. Ajoute
     * l'en-tête si le fichier est vide ou n'existait pas.
     *
     * @param nomFichier Le nom du fichier cible.
     * @param entete La ligne d'en-tête à insérer.
     * @param lignes La liste des lignes à écrire.
     * @throws IOException Si l'écriture échoue.
     */
    private void ecrireLignes(String nomFichier, String entete, List<String> lignes) throws IOException {
        File fichier = new File(CHEMIN_FICHIER + nomFichier + EXTENSION_FICHIER);
        boolean fichierExiste = fichier.exists() && fichier.length() > 0;

        try (FileWriter writer = new FileWriter(fichier, true)) {
            if (!fichierExiste) {
                writer.write(entete + "\n");
            }

            for (String ligne : lignes) {
                writer.write(ligne + "\n");
            }
        }
        lignes.clear();
    }
}

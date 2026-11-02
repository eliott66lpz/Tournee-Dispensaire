package Modele;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Calcul d'itineraires baser sur l'algorithme de Dijkstra. Determine le chemin
 * optimal entre deux centres en mode standard (duree brute) ou securise.
 *
 * @author eliott
 */
public class Dijkstra {

    /**
     * Calcule le plus court chemin entre deux centres médicaux avec
     * l'algorithme de Dijkstra. Le calcul s'adapte selon le critère choisi : il
     * évalue soit la distance globale, soit le temps de trajet total entre les
     * différents sommets.
     *
     * @param depart l'identifiant du centre médical d'origine
     * @param arrivee l'identifiant du centre médical de destination
     * @param gr le réseau de santé contenant l'ensemble des centres et des
     * routes
     * @param useDuree vrai pour baser le calcul sur la durée, faux pour
     * utiliser la distance
     * @return la valeur du coût total minimal trouvé, ou Double.MAX_VALUE si la
     * destination est inaccessible
     */
    public static double dijkstraGraphe(String depart, String arrivee, Graphe gr, boolean useDuree) {

        HashMap<String, Sommet> liste_sommet = gr.getListe_sommets();
        HashMap<String, Arete> liste_arete = gr.getListe_arete();

        ArrayList<String> listeNoeuds = new ArrayList<>(liste_sommet.keySet());
        ArrayList<Double> listeCouts = new ArrayList<>();
        ArrayList<String> noeudsVisites = new ArrayList<>();

        for (String id : listeNoeuds) {
            if (id.equals(depart)) {
                listeCouts.add(0.0);
            } else {
                listeCouts.add(Double.MAX_VALUE);
            }
        }

        while (noeudsVisites.size() < listeNoeuds.size()) {

            String noeudCourant = null;
            double coutMin = Double.MAX_VALUE;

            for (int i = 0; i < listeNoeuds.size(); i++) {
                String id = listeNoeuds.get(i);
                double cout = listeCouts.get(i);

                if (!noeudsVisites.contains(id) && cout < coutMin) {
                    coutMin = cout;
                    noeudCourant = id;
                }
            }

            if (noeudCourant == null || coutMin == Double.MAX_VALUE) {
                break;
            }

            if (noeudCourant.equals(arrivee)) {
                break;
            }

            noeudsVisites.add(noeudCourant);

            for (Arete art : liste_arete.values()) {
                String idDep = art.getSommet_dep().getNumero();
                String idArr = art.getSommet_arr().getNumero();

                String voisin = null;
                if (idDep.equals(noeudCourant)) {
                    voisin = idArr;
                } else if (idArr.equals(noeudCourant)) {
                    voisin = idDep;
                }

                if (voisin == null || noeudsVisites.contains(voisin)) {
                    continue;
                }

                double poidsRoute;
                if (useDuree == true) {
                    poidsRoute = art.getDuree();
                } else {
                    poidsRoute = art.getDistance();
                }

                double nouveauCout = coutMin + poidsRoute;

                int indexVoisin = listeNoeuds.indexOf(voisin);

                if (nouveauCout < listeCouts.get(indexVoisin)) {
                    listeCouts.set(indexVoisin, nouveauCout);
                }
            }
        }

        int indexArrivee = listeNoeuds.indexOf(arrivee);
        if (indexArrivee == -1) {
            return Double.MAX_VALUE;
        }
        return listeCouts.get(indexArrivee);
    }

    /**
     * Calcule le chemin le plus rapide entre deux centres en intégrant un
     * indice de risque. Cet algorithme de Dijkstra calcul le temps de trajet en
     * fonction de la fiabilité de la route : plus une route est risquée
     * (fiabilité basse), plus son coût augmente. Les routes avec une fiabilité
     * inférieure à 2.5 sont ignorées comme indiqué dans l'énoncé de la SAE.
     *
     * @param depart l'identifiant du centre médical d'origine
     * @param arrivee l'identifiant du centre médical de destination
     * @param gr le réseau contenant l'ensemble des centres et des routes
     * @return la valeur de la durée pondérée minimale, ou Double.MAX_VALUE si
     * le centre est inaccessible
     */
    public static double dijkstraGrapheEstimee(String depart, String arrivee, Graphe gr) {
        HashMap<String, Sommet> liste_sommet = gr.getListe_sommets();
        HashMap<String, Arete> liste_arete = gr.getListe_arete();

        ArrayList<String> listeNoeuds = new ArrayList<>(liste_sommet.keySet());
        ArrayList<Double> listeCouts = new ArrayList<>();
        ArrayList<String> noeudsVisites = new ArrayList<>();

        for (String id : listeNoeuds) {
            if (id.equals(depart)) {
                listeCouts.add(0.0);
            } else {
                listeCouts.add(Double.MAX_VALUE);
            }
        }

        while (noeudsVisites.size() < listeNoeuds.size()) {
            String noeudCourant = null;
            double coutMin = Double.MAX_VALUE;

            for (int i = 0; i < listeNoeuds.size(); i++) {
                String id = listeNoeuds.get(i);
                double cout = listeCouts.get(i);

                if (!noeudsVisites.contains(id) && cout < coutMin) {
                    coutMin = cout;
                    noeudCourant = id;
                }
            }

            if (noeudCourant == null || coutMin == Double.MAX_VALUE) {
                break;
            }

            if (noeudCourant.equals(arrivee)) {
                break;
            }

            noeudsVisites.add(noeudCourant);

            for (Arete art : liste_arete.values()) {
                String idDep = art.getSommet_dep().getNumero();
                String idArr = art.getSommet_arr().getNumero();

                String voisin = null;
                if (idDep.equals(noeudCourant)) {
                    voisin = idArr;
                } else if (idArr.equals(noeudCourant)) {
                    voisin = idDep;
                }

                if (voisin == null || noeudsVisites.contains(voisin)) {
                    continue;
                }

                double fiabilite = art.getFiabilite();

                if (fiabilite >= 2.5) {
                    double poidsRoute = (art.getDuree() * (20.0 - fiabilite)) / 10.0;
                    double nouveauCout = coutMin + poidsRoute;

                    int indexVoisin = listeNoeuds.indexOf(voisin);
                    if (nouveauCout < listeCouts.get(indexVoisin)) {
                        listeCouts.set(indexVoisin, nouveauCout);
                    }
                }
            }
        }

        int indexArrivee = listeNoeuds.indexOf(arrivee);
        if (indexArrivee == -1) {
            return Double.MAX_VALUE;
        }
        return listeCouts.get(indexArrivee);
    }

    /**
     * Calcule le chemin le plus rapide selon l'indice de risque et colore les
     * routes trouvées en jaune. Cette méthode applique l'algorithme de Dijkstra
     * en ignorant comme depuis le début les routes de fiabilité inférieure à
     * 2,5. Une fois le trajet optimal calculé, elle modifie la couleur des
     * arêtes empruntées et renvoie la durée réelle totale du voyage.
     *
     * @param depart l'identifiant du centre médical d'origine
     * @param arrivee l'identifiant du centre médical de destination
     * @param gr le réseau contenant l'ensemble des centres et des routes
     * @return la durée réelle cumulée du trajet en minutes, ou Double.MAX_VALUE
     * si aucun chemin n'est possible
     */
    public static double dijkstraGrapheEstimeeColor(String depart, String arrivee, Graphe gr) {
        HashMap<String, Sommet> liste_sommet = gr.getListe_sommets();
        HashMap<String, Arete> liste_arete = gr.getListe_arete();
        ArrayList<String> listeNoeuds = new ArrayList<>(liste_sommet.keySet());
        ArrayList<Double> listeCouts = new ArrayList<>();
        ArrayList<String> noeudsVisites = new ArrayList<>();
        HashMap<String, String> predecesseurs = new HashMap<>();
        HashMap<String, Arete> areteUtilisee = new HashMap<>();

        for (String id : listeNoeuds) {
            listeCouts.add(id.equals(depart) ? 0.0 : Double.MAX_VALUE);
            predecesseurs.put(id, null);
        }

        while (noeudsVisites.size() < listeNoeuds.size()) {
            String noeudCourant = null;
            double coutMin = Double.MAX_VALUE;

            for (int i = 0; i < listeNoeuds.size(); i++) {
                String id = listeNoeuds.get(i);
                double cout = listeCouts.get(i);
                if (!noeudsVisites.contains(id) && cout < coutMin) {
                    coutMin = cout;
                    noeudCourant = id;
                }
            }

            if (noeudCourant == null || coutMin == Double.MAX_VALUE) {
                break;
            }
            if (noeudCourant.equals(arrivee)) {
                break;
            }

            noeudsVisites.add(noeudCourant);

            for (Arete art : liste_arete.values()) {
                String idDep = art.getSommet_dep().getNumero();
                String idArr = art.getSommet_arr().getNumero();
                String voisin = null;

                if (idDep.equals(noeudCourant)) {
                    voisin = idArr;
                } else if (idArr.equals(noeudCourant)) {
                    voisin = idDep;
                }

                if (voisin == null || noeudsVisites.contains(voisin)) {
                    continue;
                }

                double fiabilite = art.getFiabilite();
                if (fiabilite < 2.5) {
                    continue;
                }
                double poidsRoute = (art.getDuree() * (20.0 - fiabilite)) / 10.0;
                double nouveauCout = coutMin + poidsRoute;
                int indexVoisin = listeNoeuds.indexOf(voisin);

                if (nouveauCout < listeCouts.get(indexVoisin)) {
                    listeCouts.set(indexVoisin, nouveauCout);
                    predecesseurs.put(voisin, noeudCourant);
                    areteUtilisee.put(voisin, art);
                }
            }
        }

        int indexArrivee = listeNoeuds.indexOf(arrivee);
        if (indexArrivee == -1 || listeCouts.get(indexArrivee) == Double.MAX_VALUE) {
            return Double.MAX_VALUE;
        }

        String courant = arrivee;
        double dureeReelle = 0.0;

        while (courant != null && !courant.equals(depart)) {
            Arete areteDuChemin = areteUtilisee.get(courant);
            if (areteDuChemin != null) {
                areteDuChemin.setCouleur(new Color(255, 236, 0));
                dureeReelle += areteDuChemin.getDuree();
            }
            courant = predecesseurs.get(courant);
        }

        Sommet sommetDepart = liste_sommet.get(depart);
        Sommet sommetArrivee = liste_sommet.get(arrivee);

        if (sommetDepart != null) {
            sommetDepart.setCouleur(new Color(0, 200, 0));
        }
        if (sommetArrivee != null) {
            sommetArrivee.setCouleur(new Color(200, 0, 0));
        }

        return dureeReelle;
    }
}

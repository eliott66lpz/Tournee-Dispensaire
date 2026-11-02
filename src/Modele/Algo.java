package Modele;

import java.util.*;
import org.graphstream.graph.*;

/**
 * Algorithmes permettant de calculer et optimiser des temps de trajets entre
 * des centres
 *
 * @author eliot
 */
public class Algo {

    public static double meilleurTemps = 0;
    public static List<String> meilleureTourneeCamion1 = new ArrayList<String>();
    public static List<String> meilleureTourneeCamion2 = new ArrayList<String>();
    public static double tempsC1 = 0;
    public static double tempsC2 = 0;

    /**
     * Optimise la tournée des camions en calculant le meilleur itinéraire pour
     * visiter les nœuds du graphe souhaiter. 
     * Les cibles peuvent être filtrées selon leur type et triées par leur numéro identifiant. La méthode utilise
     * l'algorithme glouton à un ou deux camions selon le choix de l'utilisateur.
     * @param gr L'instance du graphe de la bibliothèque GraphStream utilisé pour le calcul.
     * @param model Le modèle de données de l'application représentant le graphe.
     * @param nbCamions Le nombre de camions disponibles pour la tournée (1 ou 2)
     * @param typeFiltre Le filtre appliqué aux types de sommets à visiter (ex:
     * "TOUS", "M", "O", "N").
     */
    public static void optimiserTournee(Graph gr, Graphe model, int nbCamions, String typeFiltre) {
        meilleurTemps = 0;
        meilleureTourneeCamion1 = new ArrayList<String>();
        meilleureTourneeCamion2 = new ArrayList<String>();
        tempsC1 = 0;
        tempsC2 = 0;

        if (gr.getNode("S1") == null) {
            System.out.println("[Algo] ERREUR : Le noeud S1 est absent !");
            return;
        }

        List<Node> cibles = new ArrayList<Node>();
        for (Node n : gr.getNodeSet()) {
            String id = n.getId();
            if (id.equals("S1")) {
                continue;
            }

            String type = obtenirType(n);
            if (typeFiltre.equalsIgnoreCase("TOUS") || type.equalsIgnoreCase(typeFiltre)) {
                cibles.add(n);
            }
        }

        if (cibles.isEmpty()) {
            return;
        }

        for (int i = 0; i < cibles.size() - 1; i++) {
            int indexMin = i;

            for (int j = i + 1; j < cibles.size(); j++) {
                int numJ = extraireNumero(cibles.get(j).getId());
                int numMin = extraireNumero(cibles.get(indexMin).getId());

                if (numJ < numMin) {
                    indexMin = j;
                }
            }

            Node temporaire = cibles.get(i);
            cibles.set(i, cibles.get(indexMin));
            cibles.set(indexMin, temporaire);
        }

        if (nbCamions == 1) {
            gloutonUnCamion(gr, cibles);
        } else {
            gloutonDeuxCamions(gr, cibles);
        }
    }

    /**
     * Extrait la valeur présente dans l'identifiant d'un sommet. 
     * Par exemple, pour l'identifiant "S12", la méthode renvoie l'entier 12.
     * En cas d'erreur, la valeur maximale d'un entier est retournée.
     * @param id L'identifiant du sommet (ex: "S1", "S12").
     * @return Le numéro extrait sous forme d'entier, ou Integer.MAX_VALUE en
     * cas d'erreur.
     */
    private static int extraireNumero(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * Calcule la tournée optimale pour un seul camion en utilisant l'algorithme
     * glouton. Le camion part du centre ("S1") et visite successivement le
     * noeud le plus proche en temps, jusqu'à ce que tous les noeuds soient
     * traitées. Les résultats du temps total et du parcours sont ensuite
     * sauvegardés dans les variables globales associées.
     * @param gr L'instance du graphe GraphStream contenant les arêtes.
     * @param cibles La liste des sommets à visiter durant la tournée.
     */
    public static void gloutonUnCamion(Graph gr, List<Node> cibles) {
        List<String> tournee = new ArrayList<String>();
        List<Node> restants = new ArrayList<Node>(cibles);
        String pos = "S1";
        double tempsCumule = 0;

        while (!restants.isEmpty()) {
            Node plusProche = null;
            double tempsMin = Double.MAX_VALUE;

            for (Node n : restants) {
                double t = obtenirDureeChemin(gr, pos, n.getId());
                if (t < tempsMin) {
                    tempsMin = t;
                    plusProche = n;
                }
            }

            if (plusProche == null || tempsMin == Double.MAX_VALUE) {
                break;
            }

            tempsCumule += tempsMin;
            tournee.add(plusProche.getId());
            restants.remove(plusProche);
            pos = plusProche.getId();
        }

        meilleureTourneeCamion1 = tournee;
        tempsC1 = tempsCumule;
        meilleurTemps = tempsC1;
    }

    /**
     * Calcule la tournée optimale répartie sur deux camions en utilisant
     * l'algorithme glouton. Les deux camions partent du centre ("S1"). À chaque
     * étape, la méthode attribue le sommet restant le plus proche au camion
     * ayant accumulé le moins de temps de trajet actuel. Le temps final retenu
     * correspond au maximum des temps des deux camions.
     * @param gr L'instance du graphe GraphStream servant de support pour les
     * calculs.
     * @param cibles La liste des sommets à distribuer et à visiter.
     */
    public static void gloutonDeuxCamions(Graph gr, List<Node> cibles) {
        List<String> t1 = new ArrayList<String>(), t2 = new ArrayList<String>();
        List<Node> restants = new ArrayList<Node>(cibles);

        String posC1 = "S1", posC2 = "S1";
        double cumulC1 = 0, cumulC2 = 0;

        while (!restants.isEmpty()) {
            Node meilleur = null;
            double minCout = Double.MAX_VALUE;
            int camion = 1;

            for (Node n : restants) {
                if (cumulC1 <= cumulC2) {
                    double d1 = obtenirDureeChemin(gr, posC1, n.getId());
                    if (d1 < minCout) {
                        minCout = d1;
                        meilleur = n;
                        camion = 1;
                    }
                } else {
                    double d2 = obtenirDureeChemin(gr, posC2, n.getId());
                    if (d2 < minCout) {
                        minCout = d2;
                        meilleur = n;
                        camion = 2;
                    }
                }
            }

            if (meilleur == null || minCout == Double.MAX_VALUE) {
                break;
            }

            if (camion == 1) {
                t1.add(meilleur.getId());
                cumulC1 += minCout;
                posC1 = meilleur.getId();
            } else {
                t2.add(meilleur.getId());
                cumulC2 += minCout;
                posC2 = meilleur.getId();
            }
            restants.remove(meilleur);
        }

        meilleureTourneeCamion1 = t1;
        meilleureTourneeCamion2 = t2;

        tempsC1 = cumulC1;
        tempsC2 = cumulC2;
        meilleurTemps = Math.max(tempsC1, tempsC2);
    }

    /**
     * Calcule la durée minimale du chemin le plus court entre un sommet de
     * départ et un sommet d'arrivée en utilisant l'algorithme de Dijkstra. La
     * fonction parcourt le graphe et extrait la durée associée à chaque arête
     * pour déterminer le coût optimal vers la destination.
     * @param gr L'instance du graphe GraphStream contenant les nœuds et les
     * arêtes.
     * @param dep L'identifiant du sommet de départ (ex: "S1").
     * @param arr L'identifiant du sommet d'arrivée.
     * @return La durée totale minimale sous forme de double, ou 9999.0 si le
     * sommet est inaccessible.
     */
    private static double obtenirDureeChemin(Graph gr, String dep, String arr) {
        if (dep.equals(arr)) {
            return 0.0;
        }

        Map<String, Double> distances = new HashMap<String, Double>();
        List<String> visites = new ArrayList<String>();

        for (Node n : gr.getNodeSet()) {
            distances.put(n.getId(), Double.MAX_VALUE);
        }
        distances.put(dep, 0.0);

        while (true) {
            String u = null;
            double distU = Double.MAX_VALUE;
            for (String id : distances.keySet()) {
                if (!visites.contains(id) && distances.get(id) < distU) {
                    distU = distances.get(id);
                    u = id;
                }
            }

            if (u == null || u.equals(arr)) {
                break;
            }

            visites.add(u);

            Node noeudU = gr.getNode(u);
            if (noeudU == null) {
                continue;
            }

            Iterator<Edge> edgeIterator = noeudU.getEdgeIterator();
            while (edgeIterator.hasNext()) {
                Edge e = edgeIterator.next();
                Node voisinNode = e.getOpposite(noeudU);
                String voisin = voisinNode.getId();

                double poidsArc = extraireDureeArete(e);
                if (poidsArc > 0 && poidsArc < 9999) {
                    double nouvelleDist = distU + poidsArc;
                    if (nouvelleDist < distances.get(voisin)) {
                        distances.put(voisin, nouvelleDist);
                    }
                }
            }
        }

        if (distances.containsKey(arr)) {
            return distances.get(arr);
        } else {
            return 9999.0;
        }
    }

    /**
     * Extrait la durée associé à une arête du graphe. La méthode recherche
     * l'attribut "duree" stocké dans l'arête. Si l'attribut est une chaîne
     * brute ou un ensemble de données séparées par des virgules, elle tente
     * d'isoler et de parser la valeur numérique correspondante.
     * @param e L'arête de GraphStream dont on souhaite obtenir la durée.
     * @return La durée extraite sous forme de double, ou 9999.0 par défaut si
     * l'arête est invalide.
     */
    private static double extraireDureeArete(Edge e) {
        if (e == null) {
            return 9999.0;
        }

        Object d = e.getAttribute("duree");
        if (d instanceof Number) {
            return ((Number) d).doubleValue();
        }

        Object w = e.getAttribute("weight");
        if (w instanceof Number) {
            return ((Number) w).doubleValue();
        }

        if (d != null) {
            try {
                String str = d.toString().trim();
                String[] pieces = str.split(",");
                if (pieces.length == 3) {
                    return Double.parseDouble(pieces[2].trim());
                } else if (pieces.length == 1) {
                    return Double.parseDouble(str);
                }
            } catch (Exception ex) {
            }
        }
        return 9999.0;
    }

    /**
     * Détermine le type d'un sommet en analysant sa couleur. Permet
     * d'identifier s'il s'agit d'un centre ou point d'intérêt de type Maternité
     * (M), Bloc Opératoire (O) ou Centre de nutrition (N) selon le code couleur
     * hexadécimal associé.
     * @param n Le noeud GraphStream dont on extrait l'attribut de style
     * graphique.
     * @return Une chaîne de caractères représentant le type du sommet ("M", "O"
     * ou "N").
     */
    public static String obtenirType(Node n) {
        String style = (String) n.getAttribute("ui.style");
        if (style != null) {
            if (style.contains("#E982B0")) {
                return "M";
            }
            if (style.contains("white")) {
                return "O";
            }
            if (style.contains("#87F36C")) {
                return "N";
            }
        }
        return "M";
    }

    //          ALGO 2-OPT 
    /**
     * Lance l'optimisation des tournées en utilisant l'algorithme 2-opt. Cette
     * méthode réinitialise les résultats, vérifie que le centre de départ
     * existe, récupère la liste des centres médicaux filtrés, puis lance le
     * calcul pour 1 ou 2 camions.
     * @param gr Le graphique GraphStream utilisé pour manipuler le réseau.
     * @param model Le modèle de données du graphe
     * @param nbCamions Le nombre de camions à utiliser pour la tournée (1 ou
     * @param typeFiltre Le type de centres médicaux à cibler (par exemple "M"
     * ou "N").
     */
    public static void optimiserTourneeDeuxOpt(Graph gr, Graphe model, int nbCamions, String typeFiltre) {
        meilleurTemps = 0;
        meilleureTourneeCamion1 = new ArrayList<String>();
        meilleureTourneeCamion2 = new ArrayList<String>();
        tempsC1 = 0;
        tempsC2 = 0;

        if (gr.getNode("S1") == null) {
            System.out.println("[Algo] ERREUR : Le noeud S1 est absent !");
            return;
        }

        List<Node> cibles = obtenirCiblesTriees(gr, typeFiltre);

        if (cibles.isEmpty()) {
            return;
        }

        if (nbCamions == 1) {
            deuxOptUnCamion(gr, cibles);
        } else {
            deuxOptDeuxCamions(gr, cibles);
        }
    }

    /**
     * Récupère et trie les sommets du graphe en fonction du filtre sélectionné.
     *
     * @param gr Le graphe contenant tous les nœuds du réseau.
     * @param typeFiltre Le type de sommet recherché (par exemple "M", "N" ou
     * "TOUS").
     * @return Une liste de nœuds filtrés et triés par numéro.
     */
    private static List<Node> obtenirCiblesTriees(Graph gr, String typeFiltre) {
        List<Node> cibles = new ArrayList<Node>();
        for (Node n : gr.getNodeSet()) {
            String id = n.getId();
            if (id.equals("S1")) {
                continue;
            }

            String type = obtenirType(n);
            if (typeFiltre.equalsIgnoreCase("TOUS") || type.equalsIgnoreCase(typeFiltre)) {
                cibles.add(n);
            }
        }

        Collections.sort(cibles, new Comparator<Node>() {
            @Override
            public int compare(Node a, Node b) {
                int na = extraireNumero(a.getId());
                int nb = extraireNumero(b.getId());
                return Integer.compare(na, nb);
            }
        });

        return cibles;
    }

    /**
     * Calcule la tournée optimale pour un seul camion. Cette méthode génère
     * d'abord un itinéraire initial avec l'algorithme glouton, l'améliore
     * ensuite grâce à la méthode 2-opt, puis met à jour la durée totale du
     * trajet.
     * @param gr Le graphe représentant le réseau de centres.
     * @param cibles La liste des nœuds que le camion doit visiter.
     */
    public static void deuxOptUnCamion(Graph gr, List<Node> cibles) {
        gloutonUnCamion(gr, cibles);

        List<String> tourneeAmelioree = deuxOpt(gr, meilleureTourneeCamion1);

        meilleureTourneeCamion1 = tourneeAmelioree;
        tempsC1 = calculerTempsTotal(gr, tourneeAmelioree);
        meilleurTemps = tempsC1;
    }

    /**
     * Calcule et optimise les tournées pour deux camions. Cette méthode génère
     * un itinéraire initial pour chaque véhicule avec l'algorithme glouton,
     * améliore séparément les deux parcours avec la méthode 2-opt, puis calcule
     * les durées totales en retenant le temps de trajet du camion le plus lent.
     * @param gr Le graphe représentant le réseau de centres.
     * @param cibles La liste des nœuds à visiter et à répartir entre les deux
     * camions.
     */
    public static void deuxOptDeuxCamions(Graph gr, List<Node> cibles) {
        gloutonDeuxCamions(gr, cibles);

        List<String> t1Ameliore = deuxOpt(gr, meilleureTourneeCamion1);
        List<String> t2Ameliore = deuxOpt(gr, meilleureTourneeCamion2);

        meilleureTourneeCamion1 = t1Ameliore;
        meilleureTourneeCamion2 = t2Ameliore;

        tempsC1 = calculerTempsTotal(gr, t1Ameliore);
        tempsC2 = calculerTempsTotal(gr, t2Ameliore);
        meilleurTemps = Math.max(tempsC1, tempsC2);
    }

    /**
     * Applique l'algorithme d'optimisation 2-opt sur un parcours existant.
     * Cette méthode ajoute temporairement le point de départ, calcule à
     * l'avance les temps de trajet pour aller plus vite, puis inverse le
     * parcours dès qu'elle trouve un chemin plus court.
     * @param gr Le graphe représentant le réseau complet.
     * @param tourneeInitiale La liste des sommets du parcours avant
     * optimisation.
     * @return La liste des sommets du nouveau parcours optimisé.
     */
    public static List<String> deuxOpt(Graph gr, List<String> tourneeInitiale) {
        List<String> chemin = new ArrayList<String>();
        chemin.add("S1");
        chemin.addAll(tourneeInitiale);

        int n = chemin.size();
        if (n < 3) {
            return new ArrayList<String>(tourneeInitiale);
        }

        Map<String, Map<String, Double>> distances = construireMatriceDistances(gr, chemin);

        boolean amelioration = true;
        while (amelioration) {
            amelioration = false;

            for (int i = 0; i < n - 2; i++) {
                for (int j = i + 2; j < n - 1; j++) {
                    String a = chemin.get(i);
                    String b = chemin.get(i + 1);
                    String c = chemin.get(j);
                    String d = chemin.get(j + 1);

                    double coutActuel = distances.get(a).get(b) + distances.get(c).get(d);
                    double coutNouveau = distances.get(a).get(c) + distances.get(b).get(d);

                    if (coutNouveau < coutActuel - 1e-9) {
                        Collections.reverse(chemin.subList(i + 1, j + 1));
                        amelioration = true;
                    }
                }
            }
        }

        chemin.remove(0);

        return chemin;
    }

    /**
     * Calcule et stocke à l'avance les temps de trajet entre tous les sommets
     * de la tournée. Cette matrice permet d'éviter de recalculer plusieurs fois
     * les mêmes trajets pendant l'exécution de l'algorithme 2-opt.
     * @param gr Le graphe représentant le réseau complet.
     * @param noeuds La liste des identifiants des sommets à analyser.
     * @return Une table à double entrée contenant les durées de trajet entre
     * chaque paire de sommets.
     */
    private static Map<String, Map<String, Double>> construireMatriceDistances(Graph gr, List<String> noeuds) {
        Map<String, Map<String, Double>> matrice = new HashMap<String, Map<String, Double>>();

        for (String depart : noeuds) {
            Map<String, Double> ligne = new HashMap<String, Double>();
            for (String arrivee : noeuds) {
                if (!depart.equals(arrivee)) {
                    ligne.put(arrivee, obtenirDureeChemin(gr, depart, arrivee));
                }
            }
            matrice.put(depart, ligne);
        }

        return matrice;
    }

    /**
     * Calcule la durée totale d'une tournée en partant du sommet d'origine. La
     * méthode cumule les temps de trajet entre chaque étape successive pour
     * obtenir le coût temporel global du parcours.
     * @param gr Le graphe représentant le réseau de centres.
     * @param tournee La liste ordonnée des étapes de la tournée.
     * @return La somme des durées de tous les trajets du parcours.
     */
    private static double calculerTempsTotal(Graph gr, List<String> tournee) {
        double total = 0;
        String pos = "S1";
        for (String etape : tournee) {
            total += obtenirDureeChemin(gr, pos, etape);
            pos = etape;
        }
        return total;
    }
}

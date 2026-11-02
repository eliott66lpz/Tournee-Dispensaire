package Modele;

import java.awt.Color;
import java.util.HashMap;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;

/**
 * Affichage du réseau medical. 
 * Permet de stocker, manipuler et donner un style aux sommets et aux aretes du graphe.
 * @author eliott
 */
public class Graphe {

    private HashMap<String, Sommet> liste_sommets = new HashMap<>();
    private HashMap<String, Arete> liste_arete = new HashMap<>();

    /**
     * Donne accès à la liste complète de toutes les sommets du réseau.
     * @return le dictionnaire contenant tous les sommets triées par leur nom
     */
    public HashMap<String, Sommet> getListe_sommets() {
        return liste_sommets;
    }

    /**
     * Génère un graphe de façon totalement aléatoire.
     * Cette méthode crée le nombre de centres demandé, leur donne un type au hasard (maternité, bloc
     * ou nutrition) avec une couleur, puis crée des arêtes entre eux
     * avec des distances, des durées et des fiabilités aléatoires.
     * @param nb le nombre de sommet à créer dans le réseau
     * @return le dessin du graphe généré prêt à être affiché par GraphStream
     */
    public Graph Alea(int nb) {
        String type;
        String numero;
        Graph gr = new SingleGraph("Test");

        for (int i = 0; i < nb; i++) {
            numero = "S" + i;
            int gen = (int) (1 + Math.random() * 5);
            if (gen <= 3) {
                type = "M";
            } else if (gen == 4) {
                type = "O";
            } else {
                type = "N";
            }

            Sommet nouveau_som = new Sommet(numero, type);
            liste_sommets.put(numero, nouveau_som);

            Node n = gr.addNode(numero);

            n.setAttribute("ui.label", numero);

            if (type.equals("M")) {
                n.setAttribute("ui.style", "fill-color: #E982B0; size: 21px;");
            } else if (type.equals("O")) {
                n.setAttribute("ui.style", "fill-color: #325AC8; size: 21px;");
            } else if (type.equals("N")) {
                n.setAttribute("ui.style", "fill-color: #28965A; size: 21px;");
            }
        }

        int distance;
        double fiabilite;
        int duree;

        for (Sommet s_dep : liste_sommets.values()) {
            for (Sommet s_arr : liste_sommets.values()) {

                if (s_dep.equals(s_arr)) {
                    continue;
                }

                int proba_route = (int) (1 + Math.random() * 5);
                if (proba_route == 1) {

                    if (gr.getNode(s_dep.getNumero()).hasEdgeToward(s_arr.getNumero())) {
                        continue;
                    }

                    distance = (int) (10 + Math.random() * 40);
                    fiabilite = (int) (1 + Math.random() * 10);
                    duree = (int) (10 + Math.random() * 110);

                    String arete_id = s_dep.getNumero() + " " + s_arr.getNumero();

                    Arete nouv_art = new Arete(distance, (int) fiabilite, duree, s_dep, s_arr);

                    liste_arete.put(arete_id, nouv_art);

                    Edge nouvelle_arete = gr.addEdge(arete_id, s_dep.getNumero(), s_arr.getNumero());
                    nouvelle_arete.setAttribute("duree", (double) duree);
                    nouvelle_arete.setAttribute("fiabilite", (double) fiabilite);
                }
            }
        }

        return gr;
    }

    /**
     * Renvoie une chaîne de caractères contenant les informations détaillées
     * d'un sommet. 
     * Cette méthode cherche le sommet par son numéro pour afficher son identifiant ainsi que son type de structure médicale
     * (maternité, bloc opératoire ou nutrition).
     * @param sommet le numéro ou l'identifiant du centre à rechercher
     * @return un texte contenant le nom et le type du centre, ou un message d'erreur s'il n'existe pas
     */
    public String info_Sommet(String sommet) {
        String info = "Sommet non trouve";
        for (Sommet s : liste_sommets.values()) {
            if (s.getNumero().equals(sommet)) {
                info = "Infos sur le Sommet : " + s.getNumero() + "\n";
                info = info + "Type de Sommet : " + s.getType();
                break;
            }
        }
        return info;
    }

    /**
     * Renvoie une chaîne de caractères contenant les informations détaillées
     * d'une arête.
     * Cette méthode cherche la liaison entre deux centres pour regrouper ses caractéristiques : les villes de départ/arrivée, la
     * distance en kilomètres, le niveau de risque (fiabilité) et la durée du trajet.
     * @param s_dep l'identifiant du centre de départ recherché
     * @param s_arr l'identifiant du centre d'arrivée recherché
     * @return un texte contenant toutes les statistiques de la route, ou un message d'erreur si elle n'existe pas
     */
    public String info_Arete(String s_dep, String s_arr) {
        String info = "Arete non trouve";
        for (Arete s : liste_arete.values()) {
            String depId = s.getSommet_dep().getNumero();
            String arrId = s.getSommet_arr().getNumero();

            boolean sensNormal = depId.equals(s_dep) && arrId.equals(s_arr);
            boolean sensInverse = depId.equals(s_arr) && arrId.equals(s_dep);

            if (sensNormal || sensInverse) {
                info = "Infos sur l'Arete : " + s_dep + " " + s_arr + "\n";
                info += "Sommet de depart : " + depId + "\n";
                info += "Sommet d'arrivee : " + arrId + "\n";
                info += "Distance entre les deux Sommets : " + s.getDistance() + "\n";
                info += "Risque entre les deux Sommets : " + s.getFiabilite() + "\n";
                info += "Duree entre les deux Sommets : " + s.getDuree() + "\n";
                break;
            }
        }
        return info;
    }

    /**
     * Crée et ajoute un nouveau sommet dans le graphe.
     * Cette méthode instancie une structure de type Sommet avec son identifiant et sa catégorie
     * médicale, puis l'enregistre dans le dictionnaire des sommets.
     * @param id le numéro ou l'identifiant unique à attribuer au centre
     * @param type le type de structure médicale (par exemple "M", "O" ou "N")
     */
    public void ajouterSommet(String id, String type) {
        Sommet s = new Sommet(id, type);
        this.liste_sommets.put(id, s);
    }

    /**
     * Recherche et renvoie un sommet à partir de son identifiant. 
     * Cette méthode permet de récupérer l'objet Sommet complet associé au numéro fourni.
     * @param id le numéro ou l'identifiant du sommet à récupérer
     * @return l'objet Sommet correspondant, ou null s'il n'existe pas dans le
     * graphe
     */
    public Sommet getSommet(String id) {
        return this.liste_sommets.get(id);
    }

    /**
     * Crée et ajoute une nouvelle arête reliant deux centres du réseau.
     * Cette méthode récupère d'abord les structures des villes de départ et d'arrivée, puis instancie la route avec ses statistiques (distance,
     * fiabilité, durée) avant de l'enregistrer dans le dictionnaire des arêtes.
     * @param distance la distance en kilomètres entre les deux centres
     * @param fiabilite le niveau de fiabilité de la route
     * @param duree le temps de trajet estimé en minutes
     * @param dep l'identifiant du centre de départ
     * @param arr l'identifiant du centre d'arrivée
     */
    public void ajouterArete(int distance, int fiabilite, int duree, String dep, String arr) {
        // Récupération des objets Sommet correspondants stockés dans la Map
        Sommet s_dep = this.liste_sommets.get(dep);
        Sommet s_arr = this.liste_sommets.get(arr);

        if (s_dep != null && s_arr != null) {
            Arete r = new Arete(distance, fiabilite, duree, s_dep, s_arr);
            String id = dep + " " + arr;
            this.liste_arete.put(id, r);
        }
    }

    /**
     * Construit et configure l'affichage visuel complet du graphe.
     * Cette méthode parcourt tous les centres enregistrés pour dessiner les sommets avec leurs étiquettes et leurs couleurs associées, 
     * puis trace les arêtes qui les relient en y intégrant leurs caractéristiques de durée et de fiabilité.
     * @return le modèle Graph de GraphStream entièrement configuré pour l'affichage visuel
     */
    public Graph genererGraphe() {
        Graph gr = new SingleGraph("Graphe");

        for (Sommet s : liste_sommets.values()) {
            gr.addNode(s.getNumero());

            org.graphstream.graph.Node n = gr.getNode(s.getNumero());
            n.setAttribute("ui.label", s.getNumero());

            if (s.getType().equals("M")) {
                n.setAttribute("ui.style", "fill-color: #E982B0; size: 21px;");
            } else if (s.getType().equals("O")) {
                n.setAttribute("ui.style", "fill-color: #325AC8; size: 21px;");
            } else if (s.getType().equals("N")) {
                n.setAttribute("ui.style", "fill-color: #28965A; size: 21px;");
            }
        }

        for (Arete art : liste_arete.values()) {
            String idDep = art.getSommet_dep().getNumero();
            String idArr = art.getSommet_arr().getNumero();
            String name = idDep + " " + idArr;

            if (gr.getEdge(name) == null && gr.getEdge(idArr + " " + idDep) == null) {
                org.graphstream.graph.Edge e = gr.addEdge(name, idDep, idArr);
                e.setAttribute("duree", (double) art.getDuree());
                e.setAttribute("fiabilite", (double) art.getFiabilite());
            }
        }
        return gr;
    }

    /**
     * Compte et renvoie le nombre total de maternités présentes dans le réseau.
     * Cette méthode parcourt l'ensemble des sommets et incrémente le
     * compteur à chaque fois qu'elle trouve un sommet de type "M".
     * @return le nombre total de maternités enregistrées
     */
    public int cptM() {
        int cpt = 0;
        for (Sommet s : liste_sommets.values()) {
            if (s.getType().equals("M")) {
                cpt = cpt + 1;
            }
        }
        return cpt;
    }

    /**
     * Compte et renvoie le nombre total de blocs opératoires présents dans le
     * graphe. 
     * Cette méthode parcourt l'ensemble des sommets et
     * incrémente le compteur à chaque fois qu'elle trouve un sommet de type "O".
     * @return le nombre total de blocs opératoires enregistrés
     */
    public int cptO() {
        int cpt = 0;
        for (Sommet s : liste_sommets.values()) {
            if (s.getType().equals("O")) {
                cpt = cpt + 1;
            }
        }
        return cpt;
    }

    /**
     * Compte et renvoie le nombre total de centre de nutrition présents dans le
     * graphe. 
     * Cette méthode parcourt l'ensemble des sommets et incrémente le
     * compteur à chaque fois qu'elle trouve un sommet de type "N".
     * @return le nombre total de centre de nutrition enregistrés
     */
    public int cptN() {
        int cpt = 0;
        for (Sommet s : liste_sommets.values()) {
            if (s.getType().equals("N")) {
                cpt = cpt + 1;
            }
        }
        return cpt;
    }

    /**
     * Contrôle et met en valeur visuellement les routes en fonction d'un seuil
     * de fiabilité. 
     * Cette méthode parcourt toutes les arêtes du réseau : si la
     * fiabilité d'une route est inférieure au seuil demandé, elle est colorée
     * en rouge pour signaler un danger. Sinon, elle conserve ou reprend sa
     * couleur grise de base.
     * @param fiab le seuil de fiabilité critique en dessous duquel la
     * route devient non fiable
     * @param gr le modèle de dessin du graphe de GraphStream dont les styles
     * visuels seront modifiés
     */
    public void arete_fia(int fiab, Graph gr) {
        for (Arete a : liste_arete.values()) {

            String dep = a.getSommet_dep().getNumero();
            String arr = a.getSommet_arr().getNumero();

            org.graphstream.graph.Edge edge = gr.getEdge(dep + " " + arr);

            if (edge == null) {
                edge = gr.getEdge(arr + " " + dep);
            }

            if (edge != null) {
                if (a.getFiabilite() < fiab) {
                    a.setCouleur(Color.RED);

                    edge.setAttribute("ui.style", "fill-color: #ff0000; size: 2px;");
                } else {
                    a.setCouleur(new Color(170, 170, 170));

                    edge.setAttribute("ui.style", "fill-color: #aaaaaa; size: 2px;");
                }
            }
        }
    }

    /**
     * Recherche et renvoie une arête reliant deux centres spécifiques,
     * peu importe le sens du trajet. 
     * Cette méthode parcourt toutes les liaisons disponibles et vérifie si l'une d'elles correspond aux deux villes
     * fournies en paramètres, que ce soit dans le sens aller ou retour.
     * @param dep l'identifiant de la première ville
     * @param arr l'identifiant de la seconde ville
     * @return l'objet Arete correspondant trouvé, ou null si aucune route
     * directe n'existe entre ces centres
     */
    public Arete trouver_art(String dep, String arr) {
        for (Arete a : this.liste_arete.values()) {
            String idDep = a.getSommet_dep().getNumero();
            String idArr = a.getSommet_arr().getNumero();

            boolean sensNormal = idDep.equals(dep) && idArr.equals(arr);
            boolean sensInverse = idDep.equals(arr) && idArr.equals(dep);

            if (sensNormal || sensInverse) {
                return a;
            }
        }
        return null;
    }

    /**
     * Donne accès à la liste complète de toutes les arêtes du réseau.
     * @return le dictionnaire contenant toutes les routes triées par leur
     * identifiant unique
     */
    public HashMap<String, Arete> getListe_arete() {
        return liste_arete;
    }

    /**
     * Recherche une route directe en respectant strictement le sens de départ
     * et d'arrivée fourni.
     * @param dep l'identifiant du centre de départ précis
     * @param arr l'identifiant du centre d'arrivée précis
     * @return l'objet Arete correspondant si elle existe dans ce sens, ou null sinon
     */
    public Arete getArete(String dep, String arr) {
        Arete a = null;
        for (Arete art : liste_arete.values()) {
            if (art.getSommet_dep().getNumero().equals(dep) && art.getSommet_arr().getNumero().equals(arr)) {
                a = art;
                break;
            }
        }
        return a;
    }

    /**
     * Compte le nombre d'arêtes dont la fiabilité est strictement inférieure à un certain seuil.
     * Cette méthode permet d'identifier et de comptabiliser les routes initiales jugées non sécurisées dans le graphe.
     * @param seuil La valeur limite de fiabilité en dessous de laquelle une arête est comptée.
     * @param gr L'instance du graphe contenant la liste des arêtes à analyser.
     * @return Le nombre d'arêtes dont la fiabilité est inférieure au seuil spécifié.
     */
    public int routeIni(int seuil, Graphe gr) {
        int cpt = 0;
        for (Arete n : gr.getListe_arete().values()) {
            if (n.getFiabilite() < seuil) {
                cpt++;
            }
        }
        return cpt;
    }
}
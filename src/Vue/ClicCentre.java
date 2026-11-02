/**
 * vuedhdh
 */
package Vue;

import Modele.Sommet;
import Modele.Graphe;
import Modele.Dijkstra;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Fenêtre modale des détails d'un centre
 *
 * @author eliot
 */
public class ClicCentre extends JDialog {

    private JPanel panelHaut, panelTitres, panelProches, panelCentral;
    private JLabel lblId, lblType, lblMatProche, lblBlocProche, lblNutProche;

    private JTable tableDistances, tableDurees;
    private DefaultTableModel modeleDistances, modeleDurees;

    private JScrollPane scrollDistances, scrollDurees;

    /**
     * Classe interne représentant le résultat de calcul vers le sommet le plus
     * proche. Regroupe l'identifiant du sommet ainsi que les coûts associés
     */
    public static class ResultatProche {

        String id;
        double valeur;
        double valeurEstimee;

        /**
         * Constructeur de la classe interne ResultatProche. Stocke les
         * informations de distance ou de durée (brute et estimée) vers un
         * sommet cible.
         *
         * @param id L'identifiant du sommet.
         * @param valeur La valeur brute calculée (ex: distance ou durée).
         * @param valeur_e La valeur estimée prenant en compte la fiabilité.
         */
        ResultatProche(String id, double valeur, double valeur_e) {
            this.id = id;
            this.valeur = valeur;
            this.valeurEstimee = valeur_e;
        }
    }

    /**
     * Constructeur de la fenetre modale affichant les details d'un centre
     * medical selectionner. Configure l'affichage, initialise et organise les
     * composants pour présenter les structures les plus proches ainsi que les
     * tableaux des distances et des durees vers les autres centres.
     *
     * @param parent la fenetre principale JFrame parente pour la gestion de la
     * modalité
     * @param idCentreActuel l'identifiant du centre dont on souhaite afficher
     * les informations
     * @param gr le graphe contenant les donnees du reseau medical
     */
    public ClicCentre(JFrame parent, String idCentreActuel, Graphe gr) {
        super(parent, "Détails du Centre - " + idCentreActuel, true);

        setSize(800, 580);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 15));
        getContentPane().setBackground(new Color(22, 27, 41));

        initComposants(idCentreActuel, gr);

        panelHaut.add(panelTitres, BorderLayout.NORTH);

        panelProches.add(lblMatProche);
        panelProches.add(lblBlocProche);
        panelProches.add(lblNutProche);
        panelHaut.add(panelProches, BorderLayout.CENTER);

        add(panelHaut, BorderLayout.NORTH);

        panelCentral.add(scrollDistances);
        panelCentral.add(scrollDurees);

        add(panelCentral, BorderLayout.CENTER);

        dist_centre(idCentreActuel, gr);
        duree_centre(idCentreActuel, gr);

        toFront();
        requestFocus();
    }

    /**
     * Initialise et configure l'ensemble de l'IHM de la fenetre. Crée les
     * polices, les panneaux de mise en forme, les étiquettes d'information sur
     * le centre interrogé et calcule les structures les plus proches
     * (maternite, bloc opé, nutrition).
     *
     * @param idCentreActuel l'identifiant du centre sélectionné
     * @param gr le graphe contenant les informations du réseau medical
     */
    private void initComposants(String idCentreActuel, Graphe gr) {

        panelHaut = new JPanel(new BorderLayout(0, 12));
        panelHaut.setOpaque(false);
        panelHaut.setBorder(BorderFactory.createEmptyBorder(15, 20, 0, 20));

        panelTitres = new JPanel(new GridLayout(3, 1, 4, 4));
        panelTitres.setOpaque(false);

        lblId = new JLabel("CENTRE INTERROGÉ : " + idCentreActuel);
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblId.setForeground(Color.WHITE);

        String typeCentre = "INCONNU";
        Color couleurType = new Color(110, 125, 150);
        Sommet centreAct = gr.getSommet(idCentreActuel);
        if (centreAct != null) {
            switch (centreAct.getType()) {
                case "M":
                    typeCentre = "MATERNITÉ";
                    couleurType = new Color(233, 130, 176);
                    break;
                case "O":
                    typeCentre = "BLOC OPÉRATOIRE";
                    couleurType = new Color(50, 90, 200);
                    break;
                case "N":
                    typeCentre = "CENTRE NUTRITION";
                    couleurType = new Color(40, 150, 90);
                    break;
            }
        }

        lblType = new JLabel("Type de structure : " + typeCentre);
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblType.setForeground(couleurType);

        JLabel lblSousTitre = new JLabel("Centres les plus proches :");
        lblSousTitre.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSousTitre.setForeground(new Color(110, 125, 150));

        panelTitres = new JPanel(new GridLayout(2, 1, 4, 6));
        panelTitres.setOpaque(false);

        JPanel ligneId = new JPanel(new BorderLayout(12, 0));
        ligneId.setOpaque(false);
        ligneId.add(lblId, BorderLayout.WEST);

        JLabel badgeType = new JLabel("  " + typeCentre + "  ");
        badgeType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badgeType.setForeground(Color.WHITE);
        badgeType.setBackground(couleurType);
        badgeType.setOpaque(true);
        badgeType.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        ligneId.add(badgeType, BorderLayout.EAST);

        panelTitres.add(ligneId);
        panelTitres.add(lblSousTitre);

        panelProches = new JPanel(new GridLayout(3, 1, 6, 6));
        panelProches.setBackground(new Color(35, 42, 62));
        panelProches.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 65, 90), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));

        String idMat = "Aucun";
        String idOpe = "Aucun";
        String idNut = "Aucun";

        if (centreAct != null) {
            ResultatProche rMat = duree_min_mat(centreAct, gr);
            ResultatProche rOpe = duree_min_ope(centreAct, gr);
            ResultatProche rNut = duree_min_nut(centreAct, gr);

            if (rMat.valeur != Double.MAX_VALUE) {
                idMat = rMat.id + " ( Durée brute : " + (int) rMat.valeur + " min)  ||  " + rMat.id + " ( Durée fiabilité : " + (int) rMat.valeurEstimee + " min)";
            }
            if (rOpe.valeur != Double.MAX_VALUE) {
                idOpe = rOpe.id + " ( Durée brute : " + (int) rOpe.valeur + " min)  ||  " + rOpe.id + " ( Durée fiabilité : " + (int) rOpe.valeurEstimee + " min)";
            }
            if (rNut.valeur != Double.MAX_VALUE) {
                idNut = rNut.id + " ( Durée brute : " + (int) rNut.valeur + " min)  ||  " + rNut.id + " ( Durée fiabilité : " + (int) rNut.valeurEstimee + " min)";
            }
        }

        lblMatProche = new JLabel("<html><b><font color='#E5E9F4'>Maternité la plus proche :</font></b>    " + idMat + "</html>");
        lblMatProche.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMatProche.setForeground(Color.WHITE);

        lblBlocProche = new JLabel("<html><b><font color='#E5E9F4'>Bloc Opératoire le plus proche :</font></b>    " + idOpe + "</html>");
        lblBlocProche.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBlocProche.setForeground(Color.WHITE);

        lblNutProche = new JLabel("<html><b><font color='#E5E9F4'>Centre Nutrition le plus proche :</font></b>    " + idNut + "</html>");
        lblNutProche.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNutProche.setForeground(Color.WHITE);

        panelCentral = new JPanel(new GridLayout(1, 2, 20, 0));
        panelCentral.setOpaque(false);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        String[] entetesDist = {"Type de structure", "ID", "Distance"};
        modeleDistances = new DefaultTableModel(entetesDist, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDistances = new JTable(modeleDistances);
        configurerDesignTable(tableDistances);

        scrollDistances = new JScrollPane(tableDistances);
        scrollDistances.getViewport().setBackground(new Color(35, 42, 62));

        TitledBorder titreDist = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 65, 90), 1),
                "DISTANCES AUX AUTRES CENTRES",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                Color.WHITE
        );
        scrollDistances.setBorder(titreDist);
        scrollDistances.setBackground(new Color(22, 27, 41));

        String[] entetesDur = {"Type de structure", "ID", "Durée"};
        modeleDurees = new DefaultTableModel(entetesDur, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableDurees = new JTable(modeleDurees);
        configurerDesignTable(tableDurees);

        scrollDurees = new JScrollPane(tableDurees);
        scrollDurees.getViewport().setBackground(new Color(35, 42, 62));

        TitledBorder titreDuree = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(50, 65, 90), 1),
                "DURÉES MINIMALES ACCESSIBLES",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 12),
                Color.WHITE
        );
        scrollDurees.setBorder(titreDuree);
        scrollDurees.setBackground(new Color(22, 27, 41));
    }

    /**
     * Remplit le tableau des distances directes entre le centre actuel et ses
     * voisins. Parcourt les aretes du graphe pour identifier les sommets
     * adjacents, determine leur type, puis applique un HTML pour donner un
     * style aux données.
     *
     * @param idCentreActuel l'identifiant du centre selectionne
     * @param gr le graphe contenant les donnees du reseau medical
     */
    public void dist_centre(String idCentreActuel, Graphe gr) {
        modeleDistances.setRowCount(0);
        HashMap<String, Sommet> liste_sommet = gr.getListe_sommets();
        String styleBase = "style='font-family:Monospaced; font-size:13px;'";

        for (Sommet voisin : liste_sommet.values()) {
            if (!voisin.getType().equals("M")) {
                continue;
            }
            String id = voisin.getNumero();
            if (id.equals(idCentreActuel)) {
                continue;
            }
            double distance = Dijkstra.dijkstraGraphe(idCentreActuel, id, gr, false);
            if (distance == Double.MAX_VALUE) {
                continue;
            }
            String idStylise = "<html><span style='font-family:Monospaced; font-size:13px; color:#FFFFFF;'>" + id + "</span></html>";
            String distStylise = "<html><span " + styleBase + "><font color='#FFFFFF'><b>" + (int) distance + "</b> km</font></span></html>";
            modeleDistances.addRow(new Object[]{"MATERNITÉ", idStylise, distStylise});
        }

        for (Sommet voisin : liste_sommet.values()) {
            if (!voisin.getType().equals("O")) {
                continue;
            }
            String id = voisin.getNumero();
            if (id.equals(idCentreActuel)) {
                continue;
            }
            double distance = Dijkstra.dijkstraGraphe(idCentreActuel, id, gr, false);
            if (distance == Double.MAX_VALUE) {
                continue;
            }
            String idStylise = "<html><span style='font-family:Monospaced; font-size:13px; color:#FFFFFF;'>" + id + "</span></html>";
            String distStylise = "<html><span " + styleBase + "><font color='#FFFFFF'><b>" + (int) distance + "</b> km</font></span></html>";
            modeleDistances.addRow(new Object[]{"BLOC OPÉ", idStylise, distStylise});
        }

        for (Sommet voisin : liste_sommet.values()) {
            if (!voisin.getType().equals("N")) {
                continue;
            }
            String id = voisin.getNumero();
            if (id.equals(idCentreActuel)) {
                continue;
            }
            double distance = Dijkstra.dijkstraGraphe(idCentreActuel, id, gr, false);
            if (distance == Double.MAX_VALUE) {
                continue;
            }
            String idStylise = "<html><span style='font-family:Monospaced; font-size:13px; color:#FFFFFF;'>" + id + "</span></html>";
            String distStylise = "<html><span " + styleBase + "><font color='#FFFFFF'><b>" + (int) distance + "</b> km</font></span></html>";
            modeleDistances.addRow(new Object[]{"NUTRITION", idStylise, distStylise});
        }
    }

    /**
     * Remplit le tableau des durées de trajet directes entre le centre actuel
     * et ses voisins. Parcourt les aretes du graphe pour identifier les sommets
     * adjacents, determine leur type, puis applique un HTML pour donner un
     * style aux données.
     *
     * @param idCentreActuel l'identifiant du centre selectionne
     * @param gr le graphe contenant les donnees du reseau medical
     */
    public void duree_centre(String idCentreActuel, Graphe gr) {
        modeleDurees.setRowCount(0);
        HashMap<String, Sommet> liste_sommet = gr.getListe_sommets();
        String styleBase = "style='font-family:Monospaced; font-size:13px;'";

        for (Sommet voisin : liste_sommet.values()) {
            if (!voisin.getType().equals("M")) {
                continue;
            }
            String id = voisin.getNumero();
            if (id.equals(idCentreActuel)) {
                continue;
            }
            double duree = Dijkstra.dijkstraGraphe(idCentreActuel, id, gr, true);
            if (duree == Double.MAX_VALUE) {
                continue;
            }
            String idStylise = "<html><span style='font-family:Monospaced; font-size:13px; color:#FFFFFF;'>" + id + "</span></html>";
            String dureeStylise = "<html><span " + styleBase + "><font color='#FFFFFF'><b>" + (int) duree + "</b> min</font></span></html>";
            modeleDurees.addRow(new Object[]{"MATERNITÉ", idStylise, dureeStylise});
        }

        for (Sommet voisin : liste_sommet.values()) {
            if (!voisin.getType().equals("O")) {
                continue;
            }
            String id = voisin.getNumero();
            if (id.equals(idCentreActuel)) {
                continue;
            }
            double duree = Dijkstra.dijkstraGraphe(idCentreActuel, id, gr, true);
            if (duree == Double.MAX_VALUE) {
                continue;
            }
            String idStylise = "<html><span style='font-family:Monospaced; font-size:13px; color:#FFFFFF;'>" + id + "</span></html>";
            String dureeStylise = "<html><span " + styleBase + "><font color='#FFFFFF'><b>" + (int) duree + "</b> min</font></span></html>";
            modeleDurees.addRow(new Object[]{"BLOC OPÉ", idStylise, dureeStylise});
        }

        for (Sommet voisin : liste_sommet.values()) {
            if (!voisin.getType().equals("N")) {
                continue;
            }
            String id = voisin.getNumero();
            if (id.equals(idCentreActuel)) {
                continue;
            }
            double duree = Dijkstra.dijkstraGraphe(idCentreActuel, id, gr, true);
            if (duree == Double.MAX_VALUE) {
                continue;
            }
            String idStylise = "<html><span style='font-family:Monospaced; font-size:13px; color:#FFFFFF;'>" + id + "</span></html>";
            String dureeStylise = "<html><span " + styleBase + "><font color='#FFFFFF'><b>" + (int) duree + "</b> min</font></span></html>";
            modeleDurees.addRow(new Object[]{"NUTRITION", idStylise, dureeStylise});
        }
    }

    /**
     * Recherche et identifie la maternite la plus proche d'un centre donner en
     * fonction de la distance. Parcourt les aretes adjacentes au centre actuel
     * pour trouver les voisins de type "M" (Maternite) et retourne
     * l'identifiant de celle qui présente la distance minimale directe.
     *
     * @param centreActuel l'objet Sommet representant le centre interroge
     * @param gr le graphe contenant les donnees du reseau medical
     * @return l'identifiant de la maternite la plus proche, ou "Aucune" si
     * aucune n'est connecté directement
     */
    public ResultatProche duree_min_mat(Sommet centreActuel, Graphe gr) {
        String id = centreActuel.getNumero();
        double distanceMin = Double.MAX_VALUE;
        double dureeMinSecurisee = Double.MAX_VALUE;
        String idMatProche = "Aucune";

        for (Sommet s : gr.getListe_sommets().values()) {
            if (s.getType().equals("M") && !s.getNumero().equals(id)) {
                double dist = Dijkstra.dijkstraGraphe(id, s.getNumero(), gr, true);

                double distEstimee = Dijkstra.dijkstraGrapheEstimeeColor(id, s.getNumero(), gr);

                if (dist < distanceMin && distEstimee != Double.MAX_VALUE) {
                    distanceMin = dist;
                    dureeMinSecurisee = distEstimee;
                    idMatProche = s.getNumero();
                }
            }
        }
        return new ResultatProche(idMatProche, distanceMin, dureeMinSecurisee);
    }

    /**
     * Recherche et identifie le bloc opératoire le plus proche d'un centre
     * donne en fonction de la distance. Parcourt les aretes adjacentes au
     * centre actuel pour trouver les voisins de type "O" (Bloc Operatoire) et
     * retourne l'identifiant de celui qui presente la distance minimale
     * directe.
     *
     * @param centreActuel l'objet Sommet representant le centre interroge
     * @param gr le graphe contenant les donnees du reseau medical
     * @return l'identifiant du bloc operatoire le plus proche, ou "Aucune" si
     * aucun n'est connecté directement
     */
    public ResultatProche duree_min_ope(Sommet centreActuel, Graphe gr) {
        String id = centreActuel.getNumero();
        double distanceMin = Double.MAX_VALUE;
        double dureeMinSecurisee = Double.MAX_VALUE;
        String idOpeProche = "Aucune";

        for (Sommet s : gr.getListe_sommets().values()) {
            if (s.getType().equals("O") && !s.getNumero().equals(id)) {
                double dist = Dijkstra.dijkstraGraphe(id, s.getNumero(), gr, true);
                // On utilise la méthode Color pour récupérer la VRAIE durée réelle en minutes
                double distEstimee = Dijkstra.dijkstraGrapheEstimeeColor(id, s.getNumero(), gr);

                if (dist < distanceMin && distEstimee != Double.MAX_VALUE) {
                    distanceMin = dist;
                    dureeMinSecurisee = distEstimee;
                    idOpeProche = s.getNumero();
                }
            }
        }
        return new ResultatProche(idOpeProche, distanceMin, dureeMinSecurisee);
    }

    /**
     * Recherche et identifie le centre de nutrition le plus proche d'un centre
     * donner en fonction de la distance. Parcourt les aretes adjacentes au
     * centre actuel pour trouver les voisins de type "N" (Centre de Nutrition)
     * et retourne l'identifiant de celui qui presente la distance minimale
     * directe.
     *
     * @param centreActuel l'objet Sommet representant le centre interroger
     * @param gr le graphe contenant les donnees du reseau medical
     * @return l'identifiant du centre de nutrition le plus proche, ou "Aucune"
     * si aucun n'est connecté directement
     */
    public ResultatProche duree_min_nut(Sommet centreActuel, Graphe gr) {
        String id = centreActuel.getNumero();
        double distanceMin = Double.MAX_VALUE;
        double dureeMinSecurisee = Double.MAX_VALUE;
        String idNutProche = "Aucune";
        
        for (Sommet s : gr.getListe_sommets().values()) {
            if (s.getType().equals("N") && !s.getNumero().equals(id)) {
                double dist = Dijkstra.dijkstraGraphe(id, s.getNumero(), gr, true);
                // On utilise la méthode Color pour récupérer la VRAIE durée réelle en minutes
                double distEstimee = Dijkstra.dijkstraGrapheEstimeeColor(id, s.getNumero(), gr);
                
                if (dist < distanceMin && distEstimee != Double.MAX_VALUE) {
                    distanceMin = dist;
                    dureeMinSecurisee = distEstimee;
                    idNutProche = s.getNumero();
                }
            }
        }
        return new ResultatProche(idNutProche, distanceMin, dureeMinSecurisee);
    }

    /**
     * Applique une charte graphique sombre et personnalisée a un composant
     * JTable. Configure les couleurs de fond, de texte, des bordures de la
     * grille et des lignes selectionnées, ajuste la hauteur des lignes. Centre
     * également l'alignement du contenu de toutes les colonnes du tableau.
     *
     * @param table l'objet JTable dont le design visuel doit etre configurer
     */
    private void configurerDesignTable(JTable table) {
        table.setBackground(new Color(35, 42, 62));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(50, 65, 90));
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setSelectionBackground(new Color(60, 80, 120));
        table.setSelectionForeground(Color.WHITE);

        if (table.getParent() != null) {
            table.getParent().setBackground(new Color(35, 42, 62));
        }

        table.getTableHeader().setBackground(new Color(14, 19, 31));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        centerRenderer.setBackground(new Color(35, 42, 62));
        centerRenderer.setForeground(Color.WHITE);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

}

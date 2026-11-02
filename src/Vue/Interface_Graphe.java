package Vue;

import Modele.Sommet;
import Modele.Graphe;
import Modele.Dijkstra;
import Modele.Arete;
import Modele.Algo;
import Modele.OuvertureFichier;
import Modele.GenereCSV;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.swingViewer.ViewPanel;

/**
 * Gère l'affichage visuel et l'interface utilisateur principale du centre de
 * commandement (IHM).
 *
 * @author eliott
 */
public class Interface_Graphe extends JFrame {

    private String nomFichierGraphe = "grapheEval1";
    private String premierNoeudClique;
    private JButton ok, btnChercherArete;
    private JLabel nb_m, nb_o, nb_n, txt_fia, lbl_fia, titreStats,
            lblChercherArete, lblDepart, lblArrivee, nb_rts_ini,
            a_fia, a_duree, a_distance, a_fia_r, a_duree_r, a_distance_r;
    private JPanel conteneur_ouest, panneauGlobal;
    private JTextField text, txtDepart, txtArrivee;
    private Graphe gr;

    private JLabel lblTitreItineraire, lblItiDepart, lblItiArrivee;
    private JTextField txtItiDepart, txtItiArrivee;
    private JButton btnCalculerIti;
    private JTextArea zoneResultatIti;

    private JLabel lblTitreTournee;
    private JComboBox comboCamions, comboType;
    private JButton btnLancerTournee;
    private JTextArea zoneResultatTournee;

    private Graph graphe_visual;

    /**
     * Constructeur de la fenêtre principale de l'application (Interface_Graphe).
     * Configure le rendu graphique, charge le réseau de centres médicaux (depuis un fichier ou de manière aléatoire)
     * et construit l'interface homme-machine avec la carte interactive GraphStream.
     * * @param depuisFichier Indique si le graphe doit être chargé depuis un fichier (true) ou généré aléatoirement (false).
     * @param nbSommets     Nombre de sommets à générer en cas de création aléatoire.
     * @param cheminFichier Chemin d'accès du fichier contenant les attributs du graphe à charger.
     */
    public Interface_Graphe(boolean depuisFichier, int nbSommets, String cheminFichier) {
        super("SAE IHM - Centre de Commandement");

        System.setProperty("sun.java2d.uiScale", "1.0");
        System.setProperty("org.graphstream.ui.renderer",
                "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        System.setProperty("org.graphstream.ui", "swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.gr = new Graphe();

        if (depuisFichier) {
            OuvertureFichier of = new OuvertureFichier();
            of.chargerAttributs(cheminFichier, gr);
            graphe_visual = gr.genererGraphe();
            
            try {
                File f = new File(cheminFichier);
                String name = f.getName();
                if (name.contains(".")) {
                    name = name.substring(0, name.lastIndexOf("."));
                }
                this.nomFichierGraphe = name;
            } catch (Exception ex) {
                this.nomFichierGraphe = "grapheEval1";
            }
        } else {
            graphe_visual = gr.Alea(nbSommets);
            this.nomFichierGraphe = "grapheAlea";
        }

        initComposants(gr, graphe_visual);

        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(10, 15, 30));

        conteneur_ouest = new JPanel(new GridBagLayout());
        conteneur_ouest.setPreferredSize(new Dimension(440, 900));
        conteneur_ouest.setBackground(new Color(22, 27, 41));
        conteneur_ouest.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 5, 6, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        titreStats = new JLabel("SYNTHESE DU DISTRICT");
        titreStats.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titreStats.setForeground(new Color(110, 125, 150));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        conteneur_ouest.add(titreStats, gbc);

        JPanel panelBadges = new JPanel(new GridBagLayout());
        panelBadges.setOpaque(false);
        GridBagConstraints gbcB = new GridBagConstraints();
        gbcB.insets = new Insets(0, 4, 0, 4);
        gbcB.weightx = 1.0;
        gbcB.fill = GridBagConstraints.BOTH;
        panelBadges.add(nb_m, gbcB);
        panelBadges.add(nb_o, gbcB);
        panelBadges.add(nb_n, gbcB);

        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 5, 12, 5);
        conteneur_ouest.add(panelBadges, gbc);

        gbc.insets = new Insets(4, 5, 2, 5);
        gbc.gridy = 2;
        conteneur_ouest.add(lbl_fia, gbc);

        JPanel ligne_saisie = new JPanel(new BorderLayout(12, 0));
        ligne_saisie.setOpaque(false);
        ligne_saisie.add(txt_fia, BorderLayout.WEST);
        ligne_saisie.add(text, BorderLayout.CENTER);
        ligne_saisie.add(ok, BorderLayout.EAST);

        gbc.gridy = 3;
        gbc.insets = new Insets(2, 5, 8, 5);
        conteneur_ouest.add(ligne_saisie, gbc);

        nb_rts_ini = new JLabel("Nombre de Routes inutilisables : ");
        nb_rts_ini.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nb_rts_ini.setForeground(new Color(170, 185, 210));
        gbc.gridy = 4;
        gbc.insets = new Insets(2, 5, 12, 5);
        conteneur_ouest.add(nb_rts_ini, gbc);

        JSeparator separateur1 = new JSeparator();
        separateur1.setForeground(new Color(255, 255, 255, 40));
        gbc.gridy = 5;
        gbc.insets = new Insets(8, 5, 14, 5);
        conteneur_ouest.add(separateur1, gbc);

        JLabel lblTitreArete = new JLabel("RECHERCHER UNE ARÊTE");
        lblTitreArete.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitreArete.setForeground(new Color(110, 125, 150));
        gbc.gridy = 6;
        gbc.insets = new Insets(4, 5, 5, 5);
        conteneur_ouest.add(lblTitreArete, gbc);

        JPanel ligneRecherche = new JPanel(new GridBagLayout());
        ligneRecherche.setOpaque(false);
        GridBagConstraints gbcR = new GridBagConstraints();
        gbcR.insets = new Insets(0, 2, 0, 2);
        gbcR.fill = GridBagConstraints.HORIZONTAL;

        gbcR.gridx = 0;
        gbcR.weightx = 0.0;
        ligneRecherche.add(lblDepart, gbcR);

        gbcR.gridx = 1;
        gbcR.weightx = 0.4;
        ligneRecherche.add(txtDepart, gbcR);

        gbcR.gridx = 2;
        gbcR.weightx = 0.0;
        ligneRecherche.add(lblArrivee, gbcR);

        gbcR.gridx = 3;
        gbcR.weightx = 0.4;
        ligneRecherche.add(txtArrivee, gbcR);

        gbcR.gridx = 4;
        gbcR.weightx = 0.2;
        gbcR.insets = new Insets(0, 8, 0, 0);
        ligneRecherche.add(btnChercherArete, gbcR);

        gbc.gridy = 7;
        gbc.insets = new Insets(2, 5, 6, 5);
        conteneur_ouest.add(ligneRecherche, gbc);

        JPanel reponse_rech = new JPanel(new GridBagLayout());
        reponse_rech.setOpaque(false);
        GridBagConstraints gbcA = new GridBagConstraints();
        gbcA.insets = new Insets(4, 0, 4, 0);
        gbcA.fill = GridBagConstraints.HORIZONTAL;

        gbcA.gridx = 0;
        gbcA.gridy = 0;
        gbcA.weightx = 1.0;
        gbcA.anchor = GridBagConstraints.WEST;
        reponse_rech.add(a_fia, gbcA);

        gbcA.gridx = 1;
        gbcA.gridy = 0;
        gbcA.weightx = 0.0;
        gbcA.anchor = GridBagConstraints.EAST;
        reponse_rech.add(a_fia_r, gbcA);

        gbcA.gridx = 0;
        gbcA.gridy = 1;
        gbcA.anchor = GridBagConstraints.WEST;
        reponse_rech.add(a_duree, gbcA);

        gbcA.gridx = 1;
        gbcA.gridy = 1;
        gbcA.anchor = GridBagConstraints.EAST;
        reponse_rech.add(a_duree_r, gbcA);

        gbcA.gridx = 0;
        gbcA.gridy = 2;
        gbcA.anchor = GridBagConstraints.WEST;
        reponse_rech.add(a_distance, gbcA);

        gbcA.gridx = 1;
        gbcA.gridy = 2;
        gbcA.weightx = 0.0;
        gbcA.anchor = GridBagConstraints.EAST;
        reponse_rech.add(a_distance_r, gbcA);

        gbc.gridy = 8;
        gbc.insets = new Insets(2, 5, 12, 5);
        conteneur_ouest.add(reponse_rech, gbc);

        JSeparator separateur2 = new JSeparator();
        separateur2.setForeground(new Color(255, 255, 255, 40));
        gbc.gridy = 9;
        gbc.insets = new Insets(8, 5, 14, 5);
        conteneur_ouest.add(separateur2, gbc);

        lblTitreItineraire = new JLabel("PLANIFICATION D'ITINÉRAIRE (DIJKSTRA)");
        lblTitreItineraire.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitreItineraire.setForeground(new Color(110, 125, 150));
        gbc.gridy = 10;
        gbc.insets = new Insets(4, 5, 5, 5);
        conteneur_ouest.add(lblTitreItineraire, gbc);

        JPanel ligneItiSaisie = new JPanel(new GridBagLayout());
        ligneItiSaisie.setOpaque(false);
        GridBagConstraints gbcI = new GridBagConstraints();
        gbcI.insets = new Insets(0, 2, 0, 2);
        gbcI.fill = GridBagConstraints.HORIZONTAL;

        gbcI.gridx = 0;
        gbcI.weightx = 0.0;
        ligneItiSaisie.add(lblItiDepart, gbcI);

        gbcI.gridx = 1;
        gbcI.weightx = 0.5;
        ligneItiSaisie.add(txtItiDepart, gbcI);

        gbcI.gridx = 2;
        gbcI.weightx = 0.0;
        ligneItiSaisie.add(lblItiArrivee, gbcI);

        gbcI.gridx = 3;
        gbcI.weightx = 0.5;
        ligneItiSaisie.add(txtItiArrivee, gbcI);

        gbcI.gridx = 4;
        gbcI.weightx = 0.4;
        gbcI.insets = new Insets(0, 12, 0, 0);
        ligneItiSaisie.add(btnCalculerIti, gbcI);

        gbc.gridy = 11;
        gbc.insets = new Insets(5, 5, 8, 5);
        conteneur_ouest.add(ligneItiSaisie, gbc);

        JScrollPane scrollResultat = new JScrollPane(zoneResultatIti);
        scrollResultat.setBorder(BorderFactory.createLineBorder(new Color(50, 65, 90), 1));

        gbc.gridy = 12;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        gbc.insets = new Insets(2, 5, 12, 5);
        conteneur_ouest.add(scrollResultat, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;

        JSeparator separateur3 = new JSeparator();
        separateur3.setForeground(new Color(255, 255, 255, 40));
        gbc.gridy = 13;
        gbc.insets = new Insets(8, 5, 14, 5);
        conteneur_ouest.add(separateur3, gbc);

        gbc.gridy = 14;
        gbc.insets = new Insets(4, 5, 5, 5);
        conteneur_ouest.add(lblTitreTournee, gbc);

        JPanel ligneTournee = new JPanel(new GridBagLayout());
        ligneTournee.setOpaque(false);
        GridBagConstraints gbcT = new GridBagConstraints();
        gbcT.insets = new Insets(0, 3, 0, 3);
        gbcT.fill = GridBagConstraints.HORIZONTAL;

        gbcT.gridx = 0;
        gbcT.weightx = 0.35;
        ligneTournee.add(comboCamions, gbcT);

        gbcT.gridx = 1;
        gbcT.weightx = 0.30;
        ligneTournee.add(comboType, gbcT);

        gbcT.gridx = 2;
        gbcT.weightx = 0.35;
        gbcT.insets = new Insets(0, 6, 0, 0);
        ligneTournee.add(btnLancerTournee, gbcT);

        gbc.gridy = 15;
        gbc.insets = new Insets(2, 5, 8, 5);
        conteneur_ouest.add(ligneTournee, gbc);

        JScrollPane scrollTournee = new JScrollPane(zoneResultatTournee);
        scrollTournee.setBorder(BorderFactory.createLineBorder(new Color(50, 65, 90), 1));

        gbc.gridy = 16;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.7;
        gbc.insets = new Insets(2, 5, 10, 5);
        conteneur_ouest.add(scrollTournee, gbc);

        JPanel panneau_carte = new JPanel(new BorderLayout());
        panneau_carte.setBackground(new Color(5, 19, 38));

        graphe_visual.setAttribute("ui.stylesheet",
                "graph { fill-color: #051326; } "
                + "node { fill-color: #ffffff; size: 22px; } "
                + "edge { fill-color: #aaaaaa; size: 2px; }");

        Viewer viewer = new Viewer(graphe_visual, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        viewer.enableAutoLayout();

        ViewPanel viewPanel = viewer.addDefaultView(false);

        viewPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            private String centreDepart = null;

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e) && !SwingUtilities.isRightMouseButton(e)) {
                    return;
                }

                org.graphstream.ui.graphicGraph.GraphicGraph gg = viewer.getGraphicGraph();
                java.awt.GraphicsConfiguration gc = viewPanel.getGraphicsConfiguration();
                java.awt.geom.AffineTransform tx = gc.getDefaultTransform();

                float abscisse = (float) (e.getX() * tx.getScaleX());
                float ordonnee = (float) (e.getY() * tx.getScaleY());

                org.graphstream.ui.geom.Point3 gu = viewPanel.getCamera().transformPxToGu(abscisse, ordonnee);
                org.graphstream.ui.graphicGraph.GraphicNode plusProche = null;
                double distanceMin = Double.MAX_VALUE;

                for (org.graphstream.graph.Node n : gg) {
                    org.graphstream.ui.graphicGraph.GraphicNode gn = (org.graphstream.ui.graphicGraph.GraphicNode) n;
                    double distance = Math.hypot(gn.getX() - gu.x, gn.getY() - gu.y);
                    if (distance < distanceMin) {
                        distanceMin = distance;
                        plusProche = gn;
                    }
                }

                if (plusProche != null && distanceMin <= 1.5) {
                    String idNoeudActuel = plusProche.getId();
                    ClicCentre cc = new ClicCentre(Interface_Graphe.this, idNoeudActuel, gr);
                    cc.setVisible(true);
                }
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) {
                    return;
                }

                org.graphstream.ui.graphicGraph.GraphicGraph gg = viewer.getGraphicGraph();
                java.awt.GraphicsConfiguration gc = viewPanel.getGraphicsConfiguration();
                java.awt.geom.AffineTransform tx = gc.getDefaultTransform();

                float abscisse = (float) (e.getX() * tx.getScaleX());
                float ordonnee = (float) (e.getY() * tx.getScaleY());

                org.graphstream.ui.geom.Point3 gu = viewPanel.getCamera().transformPxToGu(abscisse, ordonnee);
                org.graphstream.ui.graphicGraph.GraphicNode plusProche = null;
                double distanceMin = Double.MAX_VALUE;
                for (org.graphstream.graph.Node n : gg) {
                    org.graphstream.ui.graphicGraph.GraphicNode gn = (org.graphstream.ui.graphicGraph.GraphicNode) n;
                    double distance = Math.hypot(gn.getX() - gu.x, gn.getY() - gu.y);
                    if (distance < distanceMin) {
                        distanceMin = distance;
                        plusProche = gn;
                    }
                }

                if (plusProche != null && distanceMin <= 1.2) {
                    centreDepart = plusProche.getId();
                } else {
                    centreDepart = null;
                }
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e) || centreDepart == null) {
                    return;
                }

                org.graphstream.ui.graphicGraph.GraphicGraph gg = viewer.getGraphicGraph();
                java.awt.GraphicsConfiguration gc = viewPanel.getGraphicsConfiguration();
                java.awt.geom.AffineTransform tx = gc.getDefaultTransform();

                float abscisse = (float) (e.getX() * tx.getScaleX());
                float ordonnee = (float) (e.getY() * tx.getScaleY());

                org.graphstream.ui.geom.Point3 gu = viewPanel.getCamera().transformPxToGu(abscisse, ordonnee);
                org.graphstream.ui.graphicGraph.GraphicNode plusProche = null;
                double distanceMin = Double.MAX_VALUE;
                for (org.graphstream.graph.Node n : gg) {
                    org.graphstream.ui.graphicGraph.GraphicNode gn = (org.graphstream.ui.graphicGraph.GraphicNode) n;
                    double distance = Math.hypot(gn.getX() - gu.x, gn.getY() - gu.y);
                    if (distance < distanceMin) {
                        distanceMin = distance;
                        plusProche = gn;
                    }
                }

                if (plusProche != null && distanceMin <= 1.2) {
                    String centreArrivee = plusProche.getId();

                    if (centreArrivee.equals(centreDepart)) {
                        ClicCentre cc = new ClicCentre(Interface_Graphe.this, centreDepart, gr);
                        cc.setVisible(true);
                    } else {
                        txtItiDepart.setText(centreDepart);
                        txtItiArrivee.setText(centreArrivee);
                        btnCalculerIti.doClick();
                    }
                }
                centreDepart = null;
            }
        });

        panneau_carte.add(viewPanel, BorderLayout.CENTER);

        panneauGlobal = new JPanel(new BorderLayout());
        panneauGlobal.setPreferredSize(new Dimension(1280, 900));
        panneauGlobal.add(conteneur_ouest, BorderLayout.WEST);
        panneauGlobal.add(panneau_carte, BorderLayout.CENTER);

        this.setContentPane(panneauGlobal);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        new javax.swing.Timer(1500, e -> viewer.disableAutoLayout()).start();
    }

    /**
     * Initialise tous les composants graphiques de l'interface et configure leurs actions.
     * * @param gr Le modèle de données du graphe.
     * @param graphe_visual Le graphe visuel GraphStream à mettre à jour.
     */
    public void initComposants(Graphe gr, Graph graphe_visual) {
        nb_m = new JLabel("<html><center><font size='3' color='#FFFFFF'>Maternités</font><br>"
                + "<b><font size='6' color='white'>" + gr.cptM() + "</font></b></center></html>", JLabel.CENTER);
        nb_m.setBackground(new Color(233, 130, 176));
        nb_m.setOpaque(true);
        nb_m.setPreferredSize(new Dimension(110, 75));
        nb_m.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        nb_o = new JLabel("<html><center><font size='3' color='#E0E5FF'>Blocs Opé</font><br>"
                + "<b><font size='6' color='white'>" + gr.cptO() + "</font></b></center></html>", JLabel.CENTER);
        nb_o.setBackground(new Color(50, 90, 200));
        nb_o.setOpaque(true);
        nb_o.setPreferredSize(new Dimension(110, 75));
        nb_o.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        nb_n = new JLabel("<html><center><font size='3' color='#E0FFE5'>Nutrition</font><br>"
                + "<b><font size='6' color='white'>" + gr.cptN() + "</font></b></center></html>", JLabel.CENTER);
        nb_n.setBackground(new Color(40, 150, 90));
        nb_n.setOpaque(true);
        nb_n.setPreferredSize(new Dimension(110, 75));
        nb_n.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        lbl_fia = new JLabel("ROUTES NON FIABLES");
        lbl_fia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl_fia.setForeground(Color.WHITE);

        txt_fia = new JLabel("Seuil de fiabilité ( entre 0% et 100% ) < ");
        txt_fia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt_fia.setForeground(new Color(200, 210, 230));

        text = createStyledTextField(4);
        nb_rts_ini = new JLabel("Nombre de Routes inutilisables : ");

        ok = createStyledButton("OK");
        ok.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        ok.addActionListener(e -> {
            try {
                int seuil = Integer.parseInt(text.getText());
                gr.arete_fia(seuil, graphe_visual);
                nb_rts_ini.setText("Nombre de Routes inutilisables : " + gr.routeIni(seuil, gr));
            } catch (NumberFormatException ex) {
            }
        });

        lblDepart = new JLabel("De : ");
        lblDepart.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDepart.setForeground(new Color(200, 210, 230));

        txtDepart = createStyledTextField(4);

        lblArrivee = new JLabel(" À : ");
        lblArrivee.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblArrivee.setForeground(new Color(200, 210, 230));

        txtArrivee = createStyledTextField(4);

        btnChercherArete = createStyledButton("OK");
        btnChercherArete.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        a_fia = new JLabel("Fiabilité (en %)");
        a_duree = new JLabel("Durée (en min)");
        a_distance = new JLabel("Distance (en km)");
        a_fia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        a_duree.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        a_distance.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        a_fia.setForeground(new Color(200, 210, 230));
        a_duree.setForeground(new Color(200, 210, 230));
        a_distance.setForeground(new Color(200, 210, 230));

        a_fia_r = createStyledResultLabel("-");
        a_duree_r = createStyledResultLabel("-");
        a_distance_r = createStyledResultLabel("-");

        btnChercherArete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s_dep = txtDepart.getText().trim();
                String s_arr = txtArrivee.getText().trim();
                Arete a = gr.trouver_art(s_dep, s_arr);
                if (a == null) {
                    a_fia_r.setText("Inexistante");
                    a_duree_r.setText("Inexistante");
                    a_distance_r.setText("Inexistante");
                } else {
                    a_fia_r.setText("" + a.getFiabilite());
                    a_duree_r.setText("" + a.getDuree());
                    a_distance_r.setText("" + a.getDistance());
                }
            }
        });

        lblItiDepart = new JLabel("De : ");
        lblItiDepart.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblItiDepart.setForeground(new Color(200, 210, 230));

        txtItiDepart = createStyledTextField(4);

        lblItiArrivee = new JLabel(" À : ");
        lblItiArrivee.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblItiArrivee.setForeground(new Color(200, 210, 230));

        txtItiArrivee = createStyledTextField(4);

        btnCalculerIti = createStyledButton("CALCULER");
        btnCalculerIti.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        zoneResultatIti = new JTextArea("Les itinéraires s'afficheront ici");
        zoneResultatIti.setFont(new Font("Monospaced", Font.PLAIN, 13));
        zoneResultatIti.setBackground(new Color(35, 42, 62));
        zoneResultatIti.setForeground(new Color(135, 243, 108));
        zoneResultatIti.setEditable(false);
        zoneResultatIti.setLineWrap(true);
        zoneResultatIti.setWrapStyleWord(true);
        zoneResultatIti.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        btnCalculerIti.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dep = txtItiDepart.getText().trim();
                String arr = txtItiArrivee.getText().trim();

                for (Arete art : gr.getListe_arete().values()) {
                    art.setCouleur(new Color(170, 170, 170));
                }
                for (org.graphstream.graph.Edge edge : graphe_visual.getEachEdge()) {
                    edge.setAttribute("ui.style", "fill-color: #aaaaaa; size: 2px;");
                }
                for (Sommet som : gr.getListe_sommets().values()) {
                    som.setCouleur(null);
                }

                int duree = (int) Dijkstra.dijkstraGraphe(dep, arr, gr, true);
                double dureeEstimee = Dijkstra.dijkstraGrapheEstimeeColor(dep, arr, gr);

                for (Arete art : gr.getListe_arete().values()) {
                    Color c = art.getCouleur();
                    if (c != null && c.getRed() == 255 && c.getGreen() == 236 && c.getBlue() == 0) {
                        String idDep = art.getSommet_dep().getNumero();
                        String idArr = art.getSommet_arr().getNumero();
                        org.graphstream.graph.Edge edge = graphe_visual.getEdge(idDep + " " + idArr);
                        if (edge == null) {
                            edge = graphe_visual.getEdge(idArr + " " + idDep);
                        }
                        if (edge != null) {
                            edge.setAttribute("ui.style", "fill-color: #FFEC00; size: 3px;");
                        }
                    }
                }

                for (Sommet som : gr.getListe_sommets().values()) {
                    org.graphstream.graph.Node node = graphe_visual.getNode(som.getNumero());
                    if (node == null) continue;

                    Color cheminCouleur = som.getCouleur();
                    if (cheminCouleur != null && cheminCouleur.getRed() == 0 && cheminCouleur.getGreen() == 200 && cheminCouleur.getBlue() == 0) {
                        node.setAttribute("ui.style", "fill-color: #00C800; size: 28px;");
                    } else if (cheminCouleur != null && cheminCouleur.getRed() == 200 && cheminCouleur.getGreen() == 0 && cheminCouleur.getBlue() == 0) {
                        node.setAttribute("ui.style", "fill-color: #C80000; size: 28px;");
                    } else {
                        String style;
                        style = switch (som.getType()) {
                            case "M" -> "fill-color: #E982B0; size: 21px;";
                            case "O" -> "fill-color: #325AC8; size: 21px;";
                            case "N" -> "fill-color: #28965A; size: 21px;";
                            default -> "fill-color: #ffffff; size: 21px;";
                        };
                        node.setAttribute("ui.style", style);
                    }
                }

                if (duree >= 1000000) {
                    zoneResultatIti.setText("Aucun itinéraire trouvé entre " + dep + " et " + arr);
                } else {
                    zoneResultatIti.setText("Durée brute : " + duree + " min\n" + "Durée avec fiabilité : " + (int) dureeEstimee + " min");
                }
            }
        });

        lblTitreTournee = new JLabel("OPTIMISATION DE TOURNÉE");
        lblTitreTournee.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitreTournee.setForeground(new Color(110, 125, 150));

        comboCamions = new JComboBox();
        comboCamions.addItem("1 camion");
        comboCamions.addItem("2 camion");
        comboCamions.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboCamions.setBackground(new Color(35, 42, 62));
        comboCamions.setForeground(Color.WHITE);

        comboType = new JComboBox();
        comboType.addItem("TOUS");
        comboType.addItem("M");
        comboType.addItem("O");
        comboType.addItem("N");
        comboType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboType.setBackground(new Color(35, 42, 62));
        comboType.setForeground(Color.WHITE);

        btnLancerTournee = createStyledButton("LANCER");
        btnLancerTournee.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));

        zoneResultatTournee = new JTextArea("Les tournées s'afficheront ici");
        zoneResultatTournee.setFont(new Font("Monospaced", Font.PLAIN, 13));
        zoneResultatTournee.setBackground(new Color(35, 42, 62));
        zoneResultatTournee.setForeground(new Color(135, 243, 108));
        zoneResultatTournee.setEditable(false);
        zoneResultatTournee.setLineWrap(true);
        zoneResultatTournee.setWrapStyleWord(true);
        zoneResultatTournee.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        btnLancerTournee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int nbCamions = comboCamions.getSelectedIndex() + 1;
                String typeFiltre = (String) comboType.getSelectedItem();

                for (Arete art : gr.getListe_arete().values()) {
                    art.setCouleur(new Color(170, 170, 170));
                }
                for (org.graphstream.graph.Edge edge : graphe_visual.getEachEdge()) {
                    edge.setAttribute("ui.style", "fill-color: #aaaaaa; size: 2px;");
                }

                Algo.optimiserTourneeDeuxOpt(graphe_visual, gr, nbCamions, typeFiltre);

                 
                GenereCSV generateurCsv = new GenereCSV();
                try {
                    if (nbCamions == 1) {
                        double dureeOpti = Algo.meilleurTemps;
                        List<Node> noeudsGraphe = new ArrayList<>(graphe_visual.getNodeSet());
                        noeudsGraphe.remove(graphe_visual.getNode("S1"));

                        Algo.gloutonUnCamion(graphe_visual, noeudsGraphe);
                        
                        double dureeGlouton = Algo.meilleurTemps;

                        generateurCsv.ajouterLigne1Camion(nomFichierGraphe, dureeGlouton, dureeOpti, Algo.meilleureTourneeCamion1);
                        generateurCsv.ecrireFichier1Camion("resultats1camionEquipe5Groupe1");
                    } else {
                        generateurCsv.ajouterLigne2Camions(nomFichierGraphe, Algo.tempsC1, Algo.tempsC2, Algo.meilleureTourneeCamion1, Algo.meilleureTourneeCamion2);
                        generateurCsv.ecrireFichier2Camions("resultats2camionsEquipe5Groupe1");
                    }
                } catch (java.io.IOException ex) {
                    JOptionPane.showMessageDialog(null,"Erreur d'écriture du fichier CSV ", "Erreur d'écriture du fichier CSV ", JOptionPane.ERROR_MESSAGE);
                    
                }

                colorerTournee(Algo.meilleureTourneeCamion1, "#00D4FF", gr, graphe_visual);
                if (nbCamions == 2) {
                    colorerTournee(Algo.meilleureTourneeCamion2, "#FF8C00", gr, graphe_visual);
                }

                StringBuilder sb = new StringBuilder();
                if (Algo.meilleureTourneeCamion1.isEmpty() && Algo.meilleureTourneeCamion2.isEmpty()) {
                    sb.append("Aucun centre à visiter pour le filtre : ").append(typeFiltre);
                } else if (nbCamions == 1) {
                    sb.append("Camion 1 :\nS1");
                    for (String etape : Algo.meilleureTourneeCamion1) {
                        sb.append(" -> ").append(etape);
                    }
                    sb.append("\nDuree totale : ").append((int) Algo.tempsC1).append(" min");
                } else {
                    sb.append("Camion 1 :\n");
                    if (Algo.meilleureTourneeCamion1.isEmpty()) {
                        sb.append("(aucune etape)");
                    } else {
                        sb.append("S1");
                        for (String etape : Algo.meilleureTourneeCamion1) {
                            sb.append(" -> ").append(etape);
                        }
                    }
                    sb.append("\nDuree : ").append((int) Algo.tempsC1).append(" min\n\nCamion 2 :\n");
                    if (Algo.meilleureTourneeCamion2.isEmpty()) {
                        sb.append("(aucune etape)");
                    } else {
                        sb.append("S1");
                        for (String etape : Algo.meilleureTourneeCamion2) {
                            sb.append(" -> ").append(etape);
                        }
                    }
                    sb.append("\nDuree : ").append((int) Algo.tempsC2).append(" min\n\nTemps total : ").append((int) Algo.meilleurTemps).append(" min");
                }

                zoneResultatTournee.setText(sb.toString());
                zoneResultatTournee.setCaretPosition(0);
            }
        });
    }

    /**
     * Colore visuellement le chemin d'une tournée sur le graphe GraphStream.
     * @param tournee Liste des sommets à visiter dans l'ordre.
     * @param couleurHex Code couleur hexadécimal à appliquer aux arêtes.
     * @param gr Le modèle de données du graphe.
     * @param graphe_visual Le graphe visuel GraphStream à mettre à jour.
     */
    private void colorerTournee(java.util.List<String> tournee, String couleurHex, Graphe gr, Graph graphe_visual) {
        if (tournee == null || tournee.isEmpty()) return;

        String dep = "S1";
        for (String arr : tournee) {
            Dijkstra.dijkstraGraphe(dep, arr, gr, true);
            for (Arete art : gr.getListe_arete().values()) {
                Color c = art.getCouleur();
                boolean estDansChemin = (c.getRed() == 255 && c.getGreen() == 236 && c.getBlue() == 0);
                if (estDansChemin) {
                    String idDep = art.getSommet_dep().getNumero();
                    String idArr = art.getSommet_arr().getNumero();
                    org.graphstream.graph.Edge edge = graphe_visual.getEdge(idDep + " " + idArr);
                    if (edge == null) {
                        edge = graphe_visual.getEdge(idArr + " " + idDep);
                    }
                    if (edge != null) {
                        edge.setAttribute("ui.style", "fill-color: " + couleurHex + "; size: 3px;");
                    }
                }
            }
            for (Arete art : gr.getListe_arete().values()) {
                art.setCouleur(new Color(170, 170, 170));
            }
            dep = arr;
        }
    }
    
    
    /**
     * Retourne le type du sommet cliqué
     * @param s
     * @return 
     */
    public String getTypeClic(String s) {
        HashMap<String, Sommet> list_sommets = gr.getListe_sommets();
        for (Sommet som : list_sommets.values()) {
            if (som.getNumero().equals(s)) {
                return som.getType();
            }
        }
        return null;
    }
    
    /**
     * Crée et stylise un champ de saisie de texte (JTextField).
     * @param columns Le nombre de colonnes pour définir la largeur du champ.
     * @return Le champ de texte configuré et stylisé.
     */
    private JTextField createStyledTextField(int columns) {
        JTextField tf = new JTextField(columns);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBackground(new Color(35, 42, 62));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(35, 42, 62), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return tf;
    }

    /**
     * Crée et stylise un bouton personnalisé (JButton).
     * @param text Le texte à afficher sur le bouton.
     * @return Le bouton configuré et stylisé.
     */
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(72, 81, 88));
        btn.setFocusPainted(false);
        return btn;
    }

    /**
     * Crée et stylise un libellé (JLabel) destiné à afficher un résultat.
     * @param text Le texte initial à afficher dans le libellé.
     * @return Le libellé de résultat configuré et stylisé.
     */
    private JLabel createStyledResultLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.WHITE);
        lbl.setBackground(new Color(35, 42, 62));
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 12));
        return lbl;
    }
}
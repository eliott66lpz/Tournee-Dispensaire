package Vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Permet d'afficher la fenetre de choix du type de graphe que ce soit de
 * générer un graphe ou d'en importer un
 *
 * @author eliot
 */
public class FirstPage extends JFrame {

    private JButton generer, importer, btnFermer, btnParcourir;
    private JLabel titre, message_imp, message_gen;
    private JTextField txt;
    private String cheminFichierSelectionne = "";

    /**
     * Ouvre la fenêtre d'accueil permettant de choisir ou de générer un graphe.
     */
    public FirstPage() {
        super("Choisir le Graphe");

        initComposant();

        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        System.setProperty("org.graphstream.ui", "swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panneauHaut = new JPanel(new BorderLayout());
        panneauHaut.setBackground(new Color(19, 23, 34));
        panneauHaut.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
        panneauHaut.add(titre, BorderLayout.CENTER);

        JPanel panneauBas = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        panneauBas.setPreferredSize(new Dimension(650, 100));
        panneauBas.setBackground(new Color(19, 23, 34));
        panneauBas.add(generer);
        panneauBas.add(importer);

        JPanel panneauGlobal = new JPanel(new BorderLayout());
        panneauGlobal.setBackground(new Color(19, 23, 34));
        panneauGlobal.add(panneauHaut, BorderLayout.NORTH);
        panneauGlobal.add(panneauBas, BorderLayout.CENTER);

        this.setContentPane(panneauGlobal);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * Initialise et configure les composants textuels et les boutons de la page
     * d'accueil. Cette méthode crée les boutons "Générer", "Importer" et
     * "Parcourir" avec leur style, définit le titre de l'application et
     * configure les actions lors d'un clic.
     */
    public void initComposant() {
        generer = new JButton("GÉNÉRER UN GRAPHE ALÉATOIRE");
        styliserBouton(generer);

        generer.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("Clic détecté sur Générer !");
                ouvrirFenetreModaleGenerer();
            }
        });

        importer = new JButton("IMPORTER UN GRAPHE");
        styliserBouton(importer);

        importer.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                System.out.println("Clic détecté sur Importer !");
                ouvrirFenetreModaleImport();
            }
        });

        titre = new JLabel("BIENVENUE DANS LE CENTRE DE COMMANDE", SwingConstants.CENTER);
        titre.setForeground(new Color(130, 145, 177));
        titre.setFont(new Font("Segoe UI", Font.BOLD, 15));

        message_imp = new JLabel("SÉLECTIONNEZ LE FICHIER DU GRAPHE : ");
        message_imp.setForeground(Color.WHITE);
        message_imp.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txt = new JTextField(4);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBackground(new Color(35, 42, 62));
        txt.setForeground(Color.WHITE);
        txt.setCaretColor(Color.WHITE);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 75, 100), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));

        btnParcourir = new JButton("PARCOURIR...");
        styliserBouton(btnParcourir);
        btnParcourir.setPreferredSize(new Dimension(140, 32));
    }

    /**
     * Ouvre une fenêtre modale pour sélectionner et importer un fichier de
     * graphe au format CSV. Cette méthode configure un explorateur de fichiers
     * (JFileChooser) ciblé sur le dossier "data", récupère le chemin du fichier
     * sélectionné et lance l'interface d'affichage correspondante au clic sur
     * OK.
     */
    private void ouvrirFenetreModaleImport() {
        JDialog modale = new JDialog(this, "Importer un Graphe", true);
        modale.setSize(450, 200);
        modale.setLocationRelativeTo(this);
        modale.setResizable(false);

        JPanel conteneurModale = new JPanel(new BorderLayout());
        conteneurModale.setBackground(new Color(19, 23, 34));
        conteneurModale.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        JPanel panneauSaisie = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panneauSaisie.setBackground(new Color(19, 23, 34));

        panneauSaisie.add(message_imp);
        panneauSaisie.add(btnParcourir);
        conteneurModale.add(panneauSaisie, BorderLayout.CENTER);

        btnParcourir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser explorateur = new JFileChooser();

                explorateur.setCurrentDirectory(new File("SAE_Code_Final\\src\\Data"));
                
                FileNameExtensionFilter filtre = new FileNameExtensionFilter("Fichiers CSV (*.csv)", "csv");
                explorateur.setFileFilter(filtre);

                int resultat = explorateur.showOpenDialog(modale);
                if (resultat == JFileChooser.APPROVE_OPTION) {
                    File fichierSelectionne = explorateur.getSelectedFile();
                    cheminFichierSelectionne = fichierSelectionne.getAbsolutePath();

                    btnParcourir.setText(fichierSelectionne.getName());
                }
            }
        });

        btnFermer = new JButton("OK");
        styliserBouton(btnFermer);
        btnFermer.setPreferredSize(new Dimension(100, 30));

        btnFermer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                if (!cheminFichierSelectionne.isEmpty()) {
                    System.out.println("Fichier sélectionné envoyé à l'IHM : " + cheminFichierSelectionne);

                    Interface_Graphe ii = new Interface_Graphe(true, 0, cheminFichierSelectionne);

                    modale.dispose();
                    dispose();
                }
            }
        });

        JPanel panneauBoutonFermer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panneauBoutonFermer.setBackground(new Color(19, 23, 34));
        panneauBoutonFermer.add(btnFermer);
        conteneurModale.add(panneauBoutonFermer, BorderLayout.SOUTH);

        modale.setContentPane(conteneurModale);
        modale.setVisible(true);
    }

    /**
     * Ouvre une fenêtre modale pour configurer la génération automatique d'un
     * graphe. Cette méthode permet à l'utilisateur de saisir le nombre de
     * sommets souhaité, convertit la saisie, puis lance l'interface du graphe
     * généré aléatoirement au clic sur OK selon le nombre choisi par
     * l'utilisateur.
     */
    private void ouvrirFenetreModaleGenerer() {
        JDialog modale = new JDialog(this, "Générer un Graphe", true);
        modale.setSize(450, 200);
        modale.setLocationRelativeTo(this);
        modale.setResizable(false);

        JPanel conteneurModale = new JPanel(new BorderLayout());
        conteneurModale.setBackground(new Color(19, 23, 34));
        conteneurModale.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));

        JPanel panneauSaisie = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panneauSaisie.setBackground(new Color(19, 23, 34));

        message_gen = new JLabel("SÉLECTIONNEZ LE NOMBRE DE SOMMETS : ");
        message_gen.setForeground(Color.WHITE);
        message_gen.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panneauSaisie.add(message_gen);
        panneauSaisie.add(txt);
        conteneurModale.add(panneauSaisie, BorderLayout.CENTER);

        btnFermer = new JButton("OK");
        styliserBouton(btnFermer);
        btnFermer.setPreferredSize(new Dimension(100, 30));

        btnFermer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                String saisie = txt.getText().trim();
                if (!saisie.isEmpty()) {
                    try {
                        int nbSommets = Integer.parseInt(saisie);

                        Interface_Graphe ig = new Interface_Graphe(false, nbSommets, null);

                        modale.dispose();
                        dispose();
                    } catch (NumberFormatException nfe) {
                        System.out.println("Veuillez entrer un nombre valide.");
                    }
                }
            }
        });

        JPanel panneauBoutonFermer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panneauBoutonFermer.setBackground(new Color(19, 23, 34));
        panneauBoutonFermer.add(btnFermer);
        conteneurModale.add(panneauBoutonFermer, BorderLayout.SOUTH);

        modale.setContentPane(conteneurModale);
        modale.setVisible(true);
    }

    /**
     * Applique un style visuel au bouton.
     *
     * @param bouton le bouton Swing à qui est donner le style
     */
    private void styliserBouton(JButton bouton) {
        bouton.setBackground(new Color(42, 47, 61));
        bouton.setForeground(Color.WHITE);
        bouton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bouton.setPreferredSize(new Dimension(260, 35));

        bouton.setFocusPainted(false);
        bouton.setBorder(new LineBorder(new Color(60, 68, 88), 1));

        bouton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bouton.setBackground(new Color(55, 62, 80));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                bouton.setBackground(new Color(42, 47, 61));
            }
        });
    }
}
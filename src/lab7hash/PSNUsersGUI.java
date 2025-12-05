/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab7hash;

/**
 *
 * @author User
 */
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class PSNUsersGUI {
    private PSNUsers psnUsers;
        private JFrame frame;

        public PSNUsersGUI(String filename) {
        psnUsers = new PSNUsers(filename); 
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("PSN Manager - Control de Usuarios y Trofeos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(" Agregar Usuario", createAddUserPanel());
        tabbedPane.addTab(" Desactivar Usuario", createDeactivateUserPanel());
        tabbedPane.addTab(" Agregar Trofeo", createAddTrophyPanel());
        tabbedPane.addTab(" Info de Jugador", createPlayerInfoPanel());

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
        
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                psnUsers.closeFiles(); 
                System.exit(0);
            }
        });
    }

    private JPanel createAddUserPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField usernameField = new JTextField(20);
        JButton addButton = new JButton("Agregar Usuario");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("")); 
        panel.add(addButton);

        addButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "El username no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (psnUsers.addUser(username)) {
                JOptionPane.showMessageDialog(panel, "Usuario '" + username + "' agregado con éxito.");
                usernameField.setText("");
            } else {
                JOptionPane.showMessageDialog(panel, "Error: Usuario ya existe o fallo de archivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }
    
    private JPanel createDeactivateUserPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField usernameField = new JTextField(20);
        JButton deactivateButton = new JButton("Desactivar Usuario");

        panel.add(new JLabel("Username a Desactivar:"));
        panel.add(usernameField);
        panel.add(new JLabel("")); 
        panel.add(deactivateButton);

        deactivateButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "El username no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (psnUsers.deactivateUser(username)) {
                JOptionPane.showMessageDialog(panel, "Usuario '" + username + "' desactivado y eliminado de HashTable.");
                usernameField.setText("");
            } else {
                JOptionPane.showMessageDialog(panel, "Error: Usuario no encontrado o inactivo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private JPanel createAddTrophyPanel() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        
        JTextField usernameField = new JTextField(20);
        JTextField gameField = new JTextField(20);
        JTextField nameField = new JTextField(20);
        JComboBox<Trophy> typeCombo = new JComboBox<>(Trophy.values());
        JTextField imagePathField = new JTextField("trophy_sample.png", 20); // Sugerencia de archivo
        JButton selectImageButton = new JButton("Seleccionar Imagen");
        JButton addButton = new JButton("Agregar Trofeo");

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Juego:"));
        panel.add(gameField);
        panel.add(new JLabel("Nombre del Trofeo:"));
        panel.add(nameField);
        panel.add(new JLabel("Tipo de Trofer o:"));
        panel.add(typeCombo);
        
        panel.add(new JLabel("Ruta de Imagen:"));
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imagePathField, BorderLayout.CENTER);
        imagePanel.add(selectImageButton, BorderLayout.EAST);
        panel.add(imagePanel);
        
        panel.add(new JLabel("")); 
        panel.add(addButton);

        selectImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showOpenDialog(frame);
            if (option == JFileChooser.APPROVE_OPTION) {
                imagePathField.setText(fileChooser.getSelectedFile().getPath());
            }
        });

        addButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String game = gameField.getText().trim();
            String name = nameField.getText().trim();
            Trophy type = (Trophy) typeCombo.getSelectedItem();
            String imagePath = imagePathField.getText().trim();

            if (username.isEmpty() || game.isEmpty() || name.isEmpty() || imagePath.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
                
                if (psnUsers.addTrophieTo(username, game, name, type, imageBytes)) {
                    JOptionPane.showMessageDialog(panel, " Trofeo agregado con éxito a '" + username + "'. Puntos actualizados.");
                    Arrays.asList(usernameField, gameField, nameField).forEach(f -> f.setText(""));
                } else {
                    JOptionPane.showMessageDialog(panel, "Error: Usuario no encontrado o fallo al escribir el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(panel, "Error al leer el archivo de imagen: " + ioException.getMessage(), "Error de Archivo", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }
    
    private JPanel createPlayerInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JTextField usernameField = new JTextField(20);
        JButton searchButton = new JButton("Buscar Info");

        // Panel donde se mostrará texto + imágenes
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));

        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        inputPanel.add(new JLabel("Username a buscar:"));
        inputPanel.add(usernameField);
        inputPanel.add(searchButton);

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "El username no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            resultsPanel.removeAll(); // Limpiar panel anterior

            String info = psnUsers.playerInfo(username);

            JLabel infoLabel = new JLabel("<html>" + info.replace("\n", "<br>") + "</html>");
            infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            resultsPanel.add(infoLabel, BorderLayout.WEST);
            resultsPanel.add(Box.createVerticalStrut(20));

            java.util.ArrayList<PSNUsers.TrophyInfo> trophies = psnUsers.getTrophiesOf(username);

            for (PSNUsers.TrophyInfo t : trophies) {

                JPanel trophyPanel = new JPanel(new BorderLayout(10, 10));

                // Convertir bytes → ImageIcon
                ImageIcon icon = new ImageIcon(t.image);
                Image scaled = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaled);

                JLabel imgLabel = new JLabel(icon);

                JLabel txtLabel = new JLabel("<html>"
                        + "<b>" + t.type + "</b> — " + t.game + "<br>"
                        + t.desc + "<br>"
                        + "<i>" + t.date + "</i>"
                        + "</html>");

                trophyPanel.add(imgLabel, BorderLayout.WEST);
                trophyPanel.add(txtLabel, BorderLayout.CENTER);

                resultsPanel.add(trophyPanel);
                resultsPanel.add(Box.createVerticalStrut(10));
            }

            // Refrescar GUI
            resultsPanel.revalidate();
            resultsPanel.repaint();
        });

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PSNUsersGUI("users.psn");
        });
    }
}

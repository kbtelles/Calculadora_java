
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Calculadora extends JFrame {

    private String operacion = "";
    private double resultado = 0;
    private boolean nuevaOperacion = true;
    private FileWriter bitacora;

    private JTextField pantalla;
    private JMenuItem itemNuevo, itemHistorial, itemManual;

    public Calculadora() {
        setTitle("Calculadora Java");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        initComponents();
        crearBitacora();
        pantalla.setText("0");
    }

    private void crearBitacora() {
        try {
            bitacora = new FileWriter("bitacoraCalculadora.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void botonPresionado(String texto) {
        Toolkit.getDefaultToolkit().beep();
        try {
            bitacora.write(texto + "\n");
            bitacora.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if ("0123456789.".contains(texto)) {
            if (nuevaOperacion) {
                pantalla.setText(texto);
            } else {
                pantalla.setText(pantalla.getText() + texto);
            }
            nuevaOperacion = false;
        } else if ("+-*/".contains(texto)) {
            operacion = texto;
            resultado = Double.parseDouble(pantalla.getText());
            nuevaOperacion = true;
        } else if ("=".equals(texto)) {
            double valorActual = Double.parseDouble(pantalla.getText());
            switch (operacion) {
                case "+": resultado += valorActual; break;
                case "-": resultado -= valorActual; break;
                case "*": resultado *= valorActual; break;
                case "/": resultado /= valorActual; break;
            }
            pantalla.setText(String.valueOf(resultado));
            nuevaOperacion = true;
        }
    }

    private void initComponents() {
        pantalla = new JTextField();
        pantalla.setBounds(50, 20, 330, 40);
        pantalla.setEditable(false);
        pantalla.setHorizontalAlignment(JTextField.RIGHT);
        add(pantalla);

        String[] botones = {
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "0", ".", "=", "+"
        };

        int x = 50, y = 80;
        for (int i = 0; i < botones.length; i++) {
            String texto = botones[i];
            JButton btn = new JButton(texto);
            btn.setBounds(x, y, 70, 30);
            add(btn);
            btn.addActionListener(e -> botonPresionado(texto));
            x += 80;
            if ((i + 1) % 4 == 0) {
                x = 50;
                y += 40;
            }
        }

        JMenuBar barra = new JMenuBar();
        JMenu menuOpciones = new JMenu("Opciones");
        itemNuevo = new JMenuItem("Nuevo");
        itemHistorial = new JMenuItem("Historial");
        menuOpciones.add(itemNuevo);
        menuOpciones.add(itemHistorial);

        JMenu menuAyuda = new JMenu("Ayuda");
        itemManual = new JMenuItem("Manual de Usuario");
        menuAyuda.add(itemManual);

        barra.add(menuOpciones);
        barra.add(menuAyuda);
        setJMenuBar(barra);

        itemNuevo.addActionListener(e -> {
            pantalla.setText("0");
            resultado = 0;
            operacion = "";
            nuevaOperacion = true;
            try {
                bitacora.write("Nuevo\n");
                bitacora.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        itemHistorial.addActionListener(e -> {
            try {
                BufferedReader lector = new BufferedReader(new FileReader("bitacoraCalculadora.txt"));
                StringBuilder contenido = new StringBuilder();
                String linea;
                while ((linea = lector.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }
                lector.close();
                JTextArea areaTexto = new JTextArea(contenido.toString());
                areaTexto.setEditable(false);
                JScrollPane scroll = new JScrollPane(areaTexto);
                scroll.setPreferredSize(new Dimension(400, 300));
                JOptionPane.showMessageDialog(null, scroll, "Historial de Operaciones", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        itemManual.addActionListener(e -> {
            String manual = "MANUAL DE USUARIO:\n\n"
                          + "- Usa los botones o teclado numérico para ingresar números.\n"
                          + "- Usa +, -, *, / para realizar operaciones.\n"
                          + "- Presiona = para obtener el resultado.\n"
                          + "- Opción 'Nuevo' reinicia la calculadora.\n"
                          + "- Opción 'Historial' muestra las operaciones realizadas.\n"
                          + "- Todo se guarda en bitacoraCalculadora.txt.";
            JOptionPane.showMessageDialog(null, manual, "Manual de Usuario", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Calculadora().setVisible(true));
    }
}

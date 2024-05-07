import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class HorasLibresUNABApp extends JFrame implements ActionListener {
    private JTextField campoNombre, campoHorasLibres, campoNuevoEvento;
    private JComboBox<String> comboEvento, comboCarrera;
    private JComboBox<Integer> comboSemestres;
    private JButton botonRegistrar, botonExportar, botonVerificar, botonAgregarEvento;
    private JTextArea areaResultado;
    private int horasAcumuladas = 0;
    private HashMap<String, Integer> eventosConocidos;
    private HashMap<String, Integer> nombresConocidos;

    public HorasLibresUNABApp() {

        setTitle("Registro de Horas Libres UNAB");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(148, 0, 211));

        eventosConocidos = new HashMap<>();
        nombresConocidos = new HashMap<>();

        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem itemExportar = new JMenuItem("Exportar a Archivo");
        itemExportar.addActionListener(this);
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.addActionListener(e -> System.exit(0));
        menuArchivo.add(itemExportar);
        menuArchivo.add(itemSalir);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);

        JPanel panel = new JPanel(new GridLayout(8, 2));
        panel.setBackground(new Color(148, 0, 211));

        panel.add(new JLabel("Nombre del Estudiante:"));
        campoNombre = new JTextField();
        panel.add(campoNombre);

        panel.add(new JLabel("Horas Libres:"));
        campoHorasLibres = new JTextField();
        panel.add(campoHorasLibres);

        panel.add(new JLabel("Evento (si conocido):"));
        comboEvento = new JComboBox<>();
        panel.add(comboEvento);

        panel.add(new JLabel("Nuevo Evento:"));
        campoNuevoEvento = new JTextField();
        panel.add(campoNuevoEvento);

        panel.add(new JLabel("Carrera:"));
        comboCarrera = new JComboBox<>(
                new String[] { "Ing. de Sistemas", "Derecho", "Biomedica", "Ing. Industrial", "Otra" });
        panel.add(comboCarrera);

        panel.add(new JLabel("Semestres:"));
        comboSemestres = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        panel.add(comboSemestres);

        botonAgregarEvento = new JButton("Agregar Evento");
        botonAgregarEvento.addActionListener(this);
        panel.add(botonAgregarEvento);

        botonRegistrar = new JButton("Registrar");
        botonRegistrar.addActionListener(this);
        panel.add(botonRegistrar);

        botonExportar = new JButton("Exportar a Archivo");
        botonExportar.addActionListener(this);
        panel.add(botonExportar);

        botonVerificar = new JButton("Verificar Graduación");
        botonVerificar.addActionListener(this);
        panel.add(botonVerificar);

        areaResultado = new JTextArea();
        areaResultado.setEditable(false);
        areaResultado.setBackground(new Color(255, 255, 204));
        JScrollPane scrollPane = new JScrollPane(areaResultado);

        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == botonAgregarEvento) {
            agregarNuevoEvento();
        } else if (e.getSource() == botonRegistrar) {
            registrarHorasLibres();
        } else if (e.getSource() == botonExportar) {
            exportarDatos();
        } else if (e.getSource() == botonVerificar) {
            verificarGraduacion();
        }
    }

    private void agregarNuevoEvento() {
        String nuevoEvento = campoNuevoEvento.getText();
        if (!nuevoEvento.isEmpty()) {
            eventosConocidos.put(nuevoEvento, 0);
            comboEvento.addItem(nuevoEvento);
            campoNuevoEvento.setText("");
        }
    }

    private void registrarHorasLibres() {
        String nombre = campoNombre.getText();
        int horasNuevas = Integer.parseInt(campoHorasLibres.getText());
        String evento = (String) comboEvento.getSelectedItem();
        String carrera = (String) comboCarrera.getSelectedItem();
        int semestres = (int) comboSemestres.getSelectedItem();

        if (!nombresConocidos.containsKey(nombre)) {
            nombresConocidos.put(nombre, 0);
        }
        int horasAcumuladasPorEstudiante = nombresConocidos.get(nombre);

        if (!eventosConocidos.containsKey(evento)) {
            eventosConocidos.put(evento, 0);
            comboEvento.addItem(evento);
        }

        eventosConocidos.put(evento, eventosConocidos.get(evento) + horasNuevas);
        nombresConocidos.put(nombre, horasAcumuladasPorEstudiante + horasNuevas);

        areaResultado.append("Horas Libres Registradas:\n");
        areaResultado.append("Nombre: " + nombre + "\n");
        areaResultado.append("Horas Nuevas: " + horasNuevas + "\n");
        areaResultado.append("Horas Acumuladas: " + nombresConocidos.get(nombre) + "\n");
        areaResultado.append("Evento: " + evento + "\n");
        areaResultado.append("Carrera: " + carrera + "\n");
        areaResultado.append("Semestres: " + semestres + "\n\n");

        limpiarCampos();
    }

    private void exportarDatos() {
        File archivo = new File("horas_libres.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write(areaResultado.getText());
            writer.flush();
            areaResultado.append("Datos exportados a horas_libres.txt\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void verificarGraduacion() {
        int horasMinimas = 90;

        for (String nombre : nombresConocidos.keySet()) {
            int horasAcumuladasPorEstudiante = nombresConocidos.get(nombre);
            int horasFaltantes = horasMinimas - horasAcumuladasPorEstudiante;

            if (horasFaltantes <= 0) {
                areaResultado.append("El estudiante " + nombre + " puede graduarse.\n");
            } else {
                areaResultado.append("Faltan " + horasFaltantes + " horas libres para que " + nombre + " se gradúe.\n");
            }
        }

        areaResultado.append("\n");
    }

    private void limpiarCampos() {
        campoNombre.setText("");
        campoHorasLibres.setText("");
        comboEvento.setSelectedIndex(0);
        comboCarrera.setSelectedIndex(0);
        comboSemestres.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HorasLibresUNABApp());
    }
}

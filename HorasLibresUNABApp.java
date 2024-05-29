import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;

public class HorasLibresUNABApp extends JFrame implements ActionListener {
    private JTextField campoHorasLibres, campoNuevoEvento, campoUsuario;
    private JPasswordField campoContrasena;
    private JComboBox<String> comboEvento, comboCarrera;
    private JComboBox<Integer> comboSemestres;
    private JButton botonRegistrar, botonExportar, botonVerificar, botonAgregarEvento;
    private JButton botonPromedioDia, botonPromedioMes, botonPromedioAnio;
    private JButton botonIniciarSesion, botonRegistrarse;
    private JTextArea areaResultado;
    private HashMap<String, Integer> eventosConocidos;
    private HashMap<String, String> usuariosConocidos;
    private HashMap<String, Integer> horasPorUsuario;
    private String nombreActual;
    private JPanel panelLogin;

    private static final String ARCHIVO_USUARIOS = "usuarios.txt";
    private static final String ARCHIVO_EVENTOS = "eventos.txt";
    private static final String ARCHIVO_HORAS = "horas.txt";

    public HorasLibresUNABApp() {
        setTitle("Registro de Horas Libres UNAB");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(148, 0, 211));

        eventosConocidos = new HashMap<>();
        usuariosConocidos = new HashMap<>();
        horasPorUsuario = new HashMap<>();

        cargarDatos();

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

        JPanel panel = new JPanel(new GridLayout(7, 2));
        panel.setBackground(new Color(148, 0, 211));

        panel.add(new JLabel("Horas Libres:"));
        campoHorasLibres = new JTextField();
        panel.add(campoHorasLibres);

        panel.add(new JLabel("Evento (si conocido):"));
        comboEvento = new JComboBox<>(eventosConocidos.keySet().toArray(new String[0]));
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

        botonPromedioDia = new JButton("Horas Promedio por Día");
        botonPromedioDia.addActionListener(this);
        botonPromedioMes = new JButton("Horas Promedio por Mes");
        botonPromedioMes.addActionListener(this);
        botonPromedioAnio = new JButton("Horas Promedio por Año");
        botonPromedioAnio.addActionListener(this);

        JPanel panelBotones = new JPanel();
        panelBotones.add(botonPromedioDia);
        panelBotones.add(botonPromedioMes);
        panelBotones.add(botonPromedioAnio);

        panel.add(panelBotones);

        add(panel, BorderLayout.NORTH);

        areaResultado = new JTextArea();
        areaResultado.setEditable(false);
        areaResultado.setBackground(new Color(255, 255, 204));
        JScrollPane scrollPane = new JScrollPane(areaResultado);

        add(scrollPane, BorderLayout.CENTER);

        panelLogin = new JPanel(new FlowLayout());
        panelLogin.setBackground(new Color(148, 0, 211));

        panelLogin.add(new JLabel("Usuario:"));
        campoUsuario = new JTextField(15);
        panelLogin.add(campoUsuario);
        panelLogin.add(new JLabel("Contraseña:"));
        campoContrasena = new JPasswordField(15);
        panelLogin.add(campoContrasena);

        botonIniciarSesion = new JButton("Iniciar Sesión");
        botonIniciarSesion.addActionListener(this);
        panelLogin.add(botonIniciarSesion);

        botonRegistrarse = new JButton("Registrarse");
        botonRegistrarse.addActionListener(this);
        panelLogin.add(botonRegistrarse);

        add(panelLogin, BorderLayout.SOUTH);

        setVisible(true);
        mostrarPantallaInicioSesion();
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
        } else if (e.getSource() == botonPromedioDia) {
            calcularHorasPromedioDia();
        } else if (e.getSource() == botonPromedioMes) {
            calcularHorasPromedioMes();
        } else if (e.getSource() == botonPromedioAnio) {
            calcularHorasPromedioAnio();
        } else if (e.getSource() == botonIniciarSesion) {
            iniciarSesion();
        } else if (e.getSource() == botonRegistrarse) {
            registrarUsuario();
        }
    }

    private void agregarNuevoEvento() {
        String nuevoEvento = campoNuevoEvento.getText();
        if (!nuevoEvento.isEmpty()) {
            eventosConocidos.put(nuevoEvento, 0);
            comboEvento.addItem(nuevoEvento);
            campoNuevoEvento.setText("");
            guardarEventos();
        }
    }

    private void registrarHorasLibres() {
        try {
            int horasNuevas = Integer.parseInt(campoHorasLibres.getText());
            String evento;
            int horasNuevas = Integer.parseInt(campoHorasLibres.getText());
            String eventoSeleccionado = (String) comboEvento.getSelectedItem();
            String carreraSeleccionada = (String) comboCarrera.getSelectedItem();
            int semestreSeleccionado = (int) comboSemestres.getSelectedItem();
            if (eventoSeleccionado == null || eventoSeleccionado.isEmpty()) {
                eventoSeleccionado = campoNuevoEvento.getText();
                eventosConocidos.put(eventoSeleccionado, 0);
                comboEvento.addItem(eventoSeleccionado);
                campoNuevoEvento.setText("");
                guardarEventos();
            }
            if (eventoSeleccionado != null) {
                horasPorUsuario.putIfAbsent(nombreActual, 0);
                horasPorUsuario.put(nombreActual, horasPorUsuario.get(nombreActual) + horasNuevas);
                areaResultado.append("Horas registradas para " + nombreActual + ": " + horasNuevas + " en " +
                        eventoSeleccionado + "\n");
                guardarHoras();
            }
            campoHorasLibres.setText("");
            comboEvento.setSelectedIndex(0);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un número válido para las horas libres.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarDatos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("exportacion_horas.txt"))) {
            for (String usuario : horasPorUsuario.keySet()) {
                writer.println("Usuario: " + usuario);
                writer.println("Horas totales: " + horasPorUsuario.get(usuario));
                writer.println("------------------------");
            }
            JOptionPane.showMessageDialog(this, "Datos exportados correctamente.", "Exportación Exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al exportar los datos.", "Error de Exportación",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verificarGraduacion() {
        if (horasPorUsuario.containsKey(nombreActual) && horasPorUsuario.get(nombreActual) >= 2000) {
            JOptionPane.showMessageDialog(this,
                    "Felicidades, " + nombreActual + ", has alcanzado las 2000 horas necesarias para graduarte!",
                    "Graduación Confirmada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Aún no has alcanzado las 2000 horas necesarias para graduarte, " + nombreActual + ".",
                    "Horas Insuficientes", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void calcularHorasPromedioDia() {
        int horasTotales = horasPorUsuario.getOrDefault(nombreActual, 0);
        double promedioDia = horasTotales / 365.0;
        JOptionPane.showMessageDialog(this, "El promedio de horas por día es: " + promedioDia, "Promedio por Día",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void calcularHorasPromedioMes() {
        int horasTotales = horasPorUsuario.getOrDefault(nombreActual, 0);
        double promedioMes = horasTotales / 12.0;
        JOptionPane.showMessageDialog(this, "El promedio de horas por mes es: " + promedioMes, "Promedio por Mes",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void calcularHorasPromedioAnio() {
        int horasTotales = horasPorUsuario.getOrDefault(nombreActual, 0);
        double promedioAnio = horasTotales;
        JOptionPane.showMessageDialog(this, "El promedio de horas por año es: " + promedioAnio, "Promedio por Año",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void iniciarSesion() {
        String usuario = campoUsuario.getText();
        String contrasena = new String(campoContrasena.getPassword());
        if (usuariosConocidos.containsKey(usuario) && usuariosConocidos.get(usuario).equals(contrasena)) {
            nombreActual = usuario;
            mostrarPantallaPrincipal();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Inicio de Sesión",
                    JOptionPane.ERROR_MESSAGE);
        }
        campoUsuario.setText("");
        campoContrasena.setText("");
    }

    private void registrarUsuario() {
        String usuario = campoUsuario.getText();
        String contrasena = new String(campoContrasena.getPassword());
        if (usuariosConocidos.containsKey(usuario)) {
            JOptionPane.showMessageDialog(this, "El usuario ya existe.", "Error de Registro",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            usuariosConocidos.put(usuario, contrasena);
            guardarUsuarios();
            JOptionPane.showMessageDialog(this, "Usuario registrado correctamente.", "Registro Exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
        campoUsuario.setText("");
        campoContrasena.setText("");
    }

    private void cargarDatos() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_EVENTOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    eventosConocidos.put(partes[0], Integer.parseInt(partes[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar los eventos: " + e.getMessage());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    usuariosConocidos.put(partes[0], partes[1]);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar los usuarios: " + e.getMessage());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_HORAS))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length == 2) {
                    horasPorUsuario.put(partes[0], Integer.parseInt(partes[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar las horas: " + e.getMessage());
        }
    }

    private void guardarEventos() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_EVENTOS))) {
            for (String evento : eventosConocidos.keySet()) {
                writer.println(evento + "," + eventosConocidos.get(evento));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar eventos: " + e.getMessage());
        }
    }

    private void guardarUsuarios() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_USUARIOS))) {
            for (String usuario : usuariosConocidos.keySet()) {
                writer.println(usuario + "," + usuariosConocidos.get(usuario));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    private void guardarHoras() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_HORAS))) {
            for (String usuario : horasPorUsuario.keySet()) {
                writer.println(usuario + "," + horasPorUsuario.get(usuario));
            }
        } catch (IOException e) {
            System.err.println("Error al guardar las horas: " + e.getMessage());
        }
    }

    private void mostrarPantallaInicioSesion() {
        panelLogin.setVisible(true);
        areaResultado.setText("");
    }

    private void mostrarPantallaPrincipal() {
        panelLogin.setVisible(false);
        areaResultado.setText("Bienvenido, " + nombreActual + "!\n\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HorasLibresUNABApp());
    }
}

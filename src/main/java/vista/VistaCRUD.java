package vista;

import controlador.TorreDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VistaCRUD extends JFrame {

    private JTable tablaTorres;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtRango, txtCosto, txtDanio, txtCadencia;
    private JButton btnCrear, btnActualizar, btnEliminar, btnRefrescar;
    private TorreDAO controlador;

    public VistaCRUD() {
        controlador = new TorreDAO();
        setTitle("Panel de Administración - CRUD Torres");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tabla central
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Rango", "Costo", "Daño", "Cadencia"}, 0);
        tablaTorres = new JTable(modeloTabla);
        add(new JScrollPane(tablaTorres), BorderLayout.CENTER);

        // Formulario izquierdo
        JPanel pnlForm = new JPanel(new GridLayout(6, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Datos de la Torre"));
        pnlForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        pnlForm.add(txtNombre);
        pnlForm.add(new JLabel("Rango:"));
        txtRango = new JTextField();
        pnlForm.add(txtRango);
        pnlForm.add(new JLabel("Costo:"));
        txtCosto = new JTextField();
        pnlForm.add(txtCosto);
        pnlForm.add(new JLabel("Daño:"));
        txtDanio = new JTextField();
        pnlForm.add(txtDanio);
        pnlForm.add(new JLabel("Cadencia (ms):"));
        txtCadencia = new JTextField();
        pnlForm.add(txtCadencia);

        add(pnlForm, BorderLayout.WEST);

        // Botonera inferior
        JPanel pnlBotones = new JPanel();
        btnCrear = new JButton("Añadir (Create)");
        btnActualizar = new JButton("Modificar (Update)");
        btnEliminar = new JButton("Eliminar (Delete)");
        btnRefrescar = new JButton("Listar (Read)");

        pnlBotones.add(btnCrear);
        pnlBotones.add(btnRefrescar);
        pnlBotones.add(btnActualizar);
        pnlBotones.add(btnEliminar);
        add(pnlBotones, BorderLayout.SOUTH);

        // Eventos de los botones
        btnRefrescar.addActionListener(e -> cargarDatos());
        btnCrear.addActionListener(e -> {
            if (controlador.crear(txtNombre.getText(), Integer.parseInt(txtRango.getText()), Integer.parseInt(txtCosto.getText()), Integer.parseInt(txtDanio.getText()), Long.parseLong(txtCadencia.getText()), "128,128,128")) {
                JOptionPane.showMessageDialog(this, "Torre agregada.");
                cargarDatos();
            }
        });
        btnActualizar.addActionListener(e -> {
            int fila = tablaTorres.getSelectedRow();
            if (fila >= 0) {
                int id = (int) modeloTabla.getValueAt(fila, 0);
                controlador.actualizar(id, Integer.parseInt(txtRango.getText()), Integer.parseInt(txtCosto.getText()), Integer.parseInt(txtDanio.getText()), Long.parseLong(txtCadencia.getText()));
                cargarDatos();
            }
        });
        btnEliminar.addActionListener(e -> {
            int fila = tablaTorres.getSelectedRow();
            if (fila >= 0) {
                int id = (int) modeloTabla.getValueAt(fila, 0);
                controlador.eliminar(id);
                cargarDatos();
            }
        });

        cargarDatos();
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        List<Object[]> torres = controlador.leerTodas();
        for (Object[] fila : torres) {
            modeloTabla.addRow(fila);
        }
    }
}

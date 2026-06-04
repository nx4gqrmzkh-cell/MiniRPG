package vista;

import controlador.TorreDAO;
import Monos.Tower;
import interfaces.IVistaCRUD;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VistaCRUD extends JFrame implements IVistaCRUD {

    private JTable tablaTorres;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtRango, txtCosto, txtDanio, txtCadencia;
    private JButton btnCrear, btnActualizar, btnEliminar, btnRefrescar;
    private TorreDAO dao;

    public VistaCRUD() {
        dao = new TorreDAO();
        setTitle("Panel de Control - CRUD con Interfaces");
        setSize(750, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Construcción de la tabla
        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Rango", "Costo", "Daño", "Cadencia"}, 0);
        tablaTorres = new JTable(modeloTabla);
        add(new JScrollPane(tablaTorres), BorderLayout.CENTER);

        // Formulario lateral
        JPanel pnlForm = new JPanel(new GridLayout(5, 2, 5, 5));
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
        btnCrear = new JButton("Registrar");
        btnRefrescar = new JButton("Listar");
        btnActualizar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");
        pnlBotones.add(btnCrear);
        pnlBotones.add(btnRefrescar);
        pnlBotones.add(btnActualizar);
        pnlBotones.add(btnEliminar);
        add(pnlBotones, BorderLayout.SOUTH);

        // Enlace de Eventos utilizando excepciones controladas
        btnRefrescar.addActionListener(e -> refrescarTabla());

        btnCrear.addActionListener(e -> {
            try {
                Tower nuevaTorre = new Tower(
                        txtNombre.getText(),
                        Integer.parseInt(txtRango.getText()),
                        Integer.parseInt(txtCosto.getText()),
                        Integer.parseInt(txtDanio.getText()),
                        Long.parseLong(txtCadencia.getText()),
                        Color.GRAY, ""
                );
                if (dao.crear(nuevaTorre)) {
                    mostrarMensaje("Torre guardada en SQL con éxito.", false);
                    refrescarTabla();
                    limpiarFormulario();
                }
            } catch (Exception ex) {
                mostrarMensaje("Error al registrar: " + ex.getMessage(), true);
            }
        });

        btnEliminar.addActionListener(e -> {
            int id = getFilaSeleccionadaId();
            if (id != -1) {
                try {
                    if (dao.eliminar(id)) {
                        mostrarMensaje("Registro eliminado de SQL.", false);
                        refrescarTabla();
                    }
                } catch (Exception ex) {
                    mostrarMensaje("No se pudo eliminar.", true);
                }
            }
        });

        refrescarTabla();
    }

    // ========================================================
    // IMPLEMENTACIÓN OBLIGATORIA DE MÉTODOS DE LA INTERFAZ
    // ========================================================
    @Override
    public void refrescarTabla() {
        modeloTabla.setRowCount(0);
        try {
            List<Object[]> data = dao.leerTodos();
            for (Object[] r : data) {
                modeloTabla.addRow(r);
            }
        } catch (Exception e) {
            mostrarMensaje("Error al conectar a la BD: " + e.getMessage(), true);
        }
    }

    @Override
    public void limpiarFormulario() {
        txtNombre.setText("");
        txtRango.setText("");
        txtCosto.setText("");
        txtDanio.setText("");
        txtCadencia.setText("");
    }

    @Override
    public void mostrarMensaje(String mensaje, boolean esError) {
        int tipo = esError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(this, mensaje, "Notificación Sistema", tipo);
    }

    @Override
    public int getFilaSeleccionadaId() {
        int fila = tablaTorres.getSelectedRow();
        if (fila == -1) {
            mostrarMensaje("Por favor, selecciona una fila de la tabla primero.", true);
            return -1;
        }
        return (int) modeloTabla.getValueAt(fila, 0);
    }
}

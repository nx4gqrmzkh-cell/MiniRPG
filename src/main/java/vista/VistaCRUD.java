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
    private TorreDAO dao;

    public VistaCRUD() {
        dao = new TorreDAO();
        setTitle("Configuración General del Servidor - CRUD Torres");
        setSize(750, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre", "Rango", "Costo", "Daño", "Cadencia"}, 0);
        tablaTorres = new JTable(modeloTabla);
        add(new JScrollPane(tablaTorres), BorderLayout.CENTER);

        JPanel pnlForm = new JPanel(new GridLayout(5, 2, 5, 5));
        pnlForm.setBorder(BorderFactory.createTitledBorder("Propiedades de Entidad"));
        pnlForm.add(new JLabel("Nombre:")); txtNombre = new JTextField(); pnlForm.add(txtNombre);
        pnlForm.add(new JLabel("Rango:")); txtRango = new JTextField(); pnlForm.add(txtRango);
        pnlForm.add(new JLabel("Costo:")); txtCosto = new JTextField(); pnlForm.add(txtCosto);
        pnlForm.add(new JLabel("Daño:")); txtDanio = new JTextField(); pnlForm.add(txtDanio);
        pnlForm.add(new JLabel("Cadencia (ms):")); txtCadencia = new JTextField(); pnlForm.add(txtCadencia);
        add(pnlForm, BorderLayout.WEST);

        JPanel pnlBotones = new JPanel();
        btnCrear = new JButton("Registrar");
        btnRefrescar = new JButton("Consultar");
        btnActualizar = new JButton("Modificar");
        btnEliminar = new JButton("Eliminar");

        pnlBotones.add(btnCrear); pnlBotones.add(btnRefrescar); 
        pnlBotones.add(btnActualizar); pnlBotones.add(btnEliminar);
        add(pnlBotones, BorderLayout.SOUTH);

        btnRefrescar.addActionListener(e -> cargar());
        btnCrear.addActionListener(e -> {
            if(dao.crear(txtNombre.getText(), Integer.parseInt(txtRango.getText()), Integer.parseInt(txtCosto.getText()), Integer.parseInt(txtDanio.getText()), Long.parseLong(txtCadencia.getText()), "128,128,128")) {
                cargar();
            }
        });
        btnActualizar.addActionListener(e -> {
            int fila = tablaTorres.getSelectedRow();
            if (fila >= 0) {
                int id = (int) modeloTabla.getValueAt(fila, 0);
                dao.actualizar(id, Integer.parseInt(txtRango.getText()), Integer.parseInt(txtCosto.getText()), Integer.parseInt(txtDanio.getText()), Long.parseLong(txtCadencia.getText()));
                cargar();
            }
        });
        btnEliminar.addActionListener(e -> {
            int fila = tablaTorres.getSelectedRow();
            if (fila >= 0) {
                int id = (int) modeloTabla.getValueAt(fila, 0);
                dao.eliminar(id);
                cargar();
            }
        });
        cargar();
    }

    private void cargar() {
        modeloTabla.setRowCount(0);
        List<Object[]> data = dao.leerTodas();
        for (Object[] r : data) { modeloTabla.addRow(r); }
    }
}
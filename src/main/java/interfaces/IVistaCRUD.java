package interfaces;
/**
 * Interfaz fundamental para aplicar el patrón MVC (Modelo-Vista-Controlador).
 * Define el contrato que cualquier ventana gráfica (JFrame o JPanel) que administre
 * los datos de la base de datos debe cumplir obligatoriamente para ser interactiva.
 */
public interface IVistaCRUD {

    /**
     * Carga y actualiza de forma asíncrona o directa los registros de la base
     * de datos para mostrarlos reflejados en la JTable visual del juego.
     */
    void refrescarTabla();

    /**
     * Limpia todas las cajas de texto (JTextFields) del formulario de edición
     * para facilitar la inserción de un nuevo registro.
     */
    void limpiarFormulario();

    /**
     * Muestra mensajes emergentes gráficos (usando JOptionPane) para informar
     * al usuario si la operación en la base de datos fue exitosa o falló.
     *
     * * @param mensaje Texto explicativo que se mostrará en pantalla.
     * @param esError true si se trata de una excepción/fallo, false si es
     * informativo.
     */
    void mostrarMensaje(String mensaje, boolean esError);

    /**
     * Recupera el identificador único (ID de la clave primaria SQL) de la fila
     * que el usuario ha seleccionado con el ratón en la tabla gráfica.
     *
     * * @return El ID numérico entero del registro seleccionado, o -1 si no
     * hay selección.
     */
    int getFilaSeleccionadaId();
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GamePane;

/**
 *
 * @author Usuario
 */
public gamepane() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(34, 139, 34)); // Verde césped
        setFocusable(true);

        // === VINCULACIÓN SQL ACTIVA ===
        System.out.println("[DB Inicialización] Sincronizando datos de configuración desde SQL...");
        sql.sql1.cargarTorresDesdeBD(); 
        
        // Comprobación de carga dinámica de un enemigo (Ejemplo: BLUE)
        java.util.Map<String, Object> datosBlue = sql.sql1.obtenerEstadisticasBloon("BLUE");
        if (!datosBlue.isEmpty()) {
            System.out.println("[DB Inicialización] Sincronizado enemigo 'BLUE' - Capas: " 
                    + datosBlue.get("layers") + " | Velocidad: " + datosBlue.get("speed"));
        }
        // ==============================

        initPath();
        waveManager = new WaveManager(this);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mousePos = e.getPoint();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });
    }

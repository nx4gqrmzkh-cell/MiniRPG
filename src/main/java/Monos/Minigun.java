/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Monos;

/**
 *
 * @author Usuario
 */
public class Minigun extends Monos {

    public Minigun() {
        super(950, 10);
    }

    

    public void realizarMejora(boolean mejora) {
        if (mejora) {
            super.setDps(super.getDps()+10);
        }
    }

}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Monos;

/**
 *
 * @author Juan
 */
public class Boomeran extends Monos {

    public Boomeran(int coste, int dps) {
        super(500, 5);
    }

    public void realizarMejora(boolean mejora) {
        if (mejora) {
            super.setDps(super.getDps() + 5);
        }
    }
}

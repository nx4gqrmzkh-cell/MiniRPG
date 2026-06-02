/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Monos;

/**
 *
 * @author Usuario
 */
public class Dardero extends Monos{

    public Dardero(int coste, int dps) {
        super(250, 1);
    }






    public void realizarMejora(boolean mejora) {
        if (mejora) {
            super.setDps(super.getDps()+1);
        }
    }
}

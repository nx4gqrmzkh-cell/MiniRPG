
package Monos;
public class Dardero extends Monos{

    public void realizarMejora(boolean mejora) {
        if (mejora) {
            super.setDps(super.getDps() + 1);
        }
    }
}

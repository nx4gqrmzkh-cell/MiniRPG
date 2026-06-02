package Monos;

public class Dardero extends Monos {

    public Dardero(int coste, int dps) {
        super(200, 1);
    }

    public void realizarMejora(boolean mejora) {
        if (mejora) {
            super.setDps(super.getDps() + 1);
        }
    }
}

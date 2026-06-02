
te, int dps) {
        super(250, 1);
    }






    public void realizarMejora(boolean mejora) {
        if (mejora) {
            super.setDps(super.getDps()+1);
        }
    }
}

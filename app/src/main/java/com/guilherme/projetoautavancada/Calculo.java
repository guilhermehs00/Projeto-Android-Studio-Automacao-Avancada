package com.guilherme.projetoautavancada;

public class Calculo {

    public Calculo(){

    }
    public static double[] Media(double d1[][]){
        double Media_F1 = (d1[0][0] + d1[1][0] + d1[2][0] + d1[3][0] + d1[4][0] + d1[5][0] + d1[6][0] + d1[7][0] + d1[8][0] + d1[9][0])/10;
        double Media_F2 = (d1[0][1] + d1[1][1] + d1[2][1] + d1[3][1] + d1[4][1] + d1[5][1] + d1[6][1] + d1[7][1] + d1[8][1] + d1[9][1])/10;
        double Media_F3 = (d1[0][2] + d1[1][2] + d1[2][2] + d1[3][2] + d1[4][2] + d1[5][2] + d1[6][2] + d1[7][2] + d1[8][2] + d1[9][2])/10;
        double Media_F4 = (d1[0][3] + d1[1][3] + d1[2][3] + d1[3][3] + d1[4][3] + d1[5][3] + d1[6][3] + d1[7][3] + d1[8][3] + d1[9][3])/10;
        double Media_F5 = (d1[0][4] + d1[1][4] + d1[2][4] + d1[3][4] + d1[4][4] + d1[5][4] + d1[6][4] + d1[7][4] + d1[8][4] + d1[9][4])/10;
        double[] D = new double[] {Media_F1, Media_F2, Media_F3, Media_F4, Media_F5};
        return D;
    }

    public static double[] DesvioPadrao(double d1[][], double D[]){
        double Desvio_F1 = Math.sqrt(((Math.pow((d1[0][0] - D[0]), 2)) + (Math.pow((d1[1][0] - D[0]), 2)) + (Math.pow((d1[2][0] - D[0]), 2))+
                (Math.pow((d1[3][0] - D[0]), 2)) + (Math.pow((d1[4][0] - D[0]), 2)) + (Math.pow((d1[5][0] - D[0]), 2)) +
                (Math.pow((d1[6][0] - D[0]), 2)) + (Math.pow((d1[7][0] - D[0]), 2)) + (Math.pow((d1[8][0] - D[0]), 2)) + (Math.pow((d1[9][0] - D[0]), 2)) ) / 10);

        double Desvio_F2 = Math.sqrt(((Math.pow((d1[0][1] - D[1]), 2)) + (Math.pow((d1[1][1] - D[1]), 2)) + (Math.pow((d1[2][1] - D[1]), 2))+
                (Math.pow((d1[3][1] - D[1]), 2)) + (Math.pow((d1[4][1] - D[1]), 2)) + (Math.pow((d1[5][1] - D[1]), 2)) +
                (Math.pow((d1[6][1] - D[1]), 2)) + (Math.pow((d1[7][1] - D[1]), 2)) + (Math.pow((d1[8][1] - D[1]), 2)) + (Math.pow((d1[9][1] - D[1]), 2)) ) / 10);

        double Desvio_F3 = Math.sqrt(((Math.pow((d1[0][2] - D[2]), 2)) + (Math.pow((d1[1][2] - D[2]), 2)) + (Math.pow((d1[2][2] - D[2]), 2))+
                (Math.pow((d1[3][2] - D[2]), 2)) + (Math.pow((d1[4][2] - D[2]), 2)) + (Math.pow((d1[5][2] - D[2]), 2)) +
                (Math.pow((d1[6][2] - D[2]), 2)) + (Math.pow((d1[7][2] - D[2]), 2)) + (Math.pow((d1[8][2] - D[2]), 2)) + (Math.pow((d1[9][2] - D[2]), 2)) ) / 10);

        double Desvio_F4 = Math.sqrt(((Math.pow((d1[0][3] - D[3]), 2)) + (Math.pow((d1[1][3] - D[3]), 2)) + (Math.pow((d1[2][3] - D[3]), 2))+
                (Math.pow((d1[3][3] - D[3]), 2)) + (Math.pow((d1[4][3] - D[3]), 2)) + (Math.pow((d1[5][3] - D[3]), 2))+
                (Math.pow((d1[6][3] - D[3]), 2)) + (Math.pow((d1[7][3] - D[3]), 2)) + (Math.pow((d1[8][3] - D[3]), 2))+ (Math.pow((d1[9][3] - D[3]), 2)) ) / 10);

        double Desvio_F5 = Math.sqrt(((Math.pow((d1[0][4] - D[4]), 2)) + (Math.pow((d1[1][4] - D[4]), 2)) + (Math.pow((d1[2][4] - D[4]), 2))+
                (Math.pow((d1[3][4] - D[4]), 2)) + (Math.pow((d1[4][4] - D[4]), 2)) + (Math.pow((d1[5][4] - D[4]), 2)) +
                (Math.pow((d1[6][4] - D[4]), 2)) + (Math.pow((d1[7][4] - D[4]), 2)) + (Math.pow((d1[8][4] - D[4]), 2)) + (Math.pow((d1[9][4] - D[4]), 2)) ) / 10);

        double[] Sigma = new double[] {Desvio_F1, Desvio_F2, Desvio_F3, Desvio_F4, Desvio_F5};
        return Sigma;
    }

    public static double[] Variancia(double Sigma[]){
        double[] variancia = new double[] { Math.pow(Sigma[0], 2), Math.pow(Sigma[1], 2), Math.pow(Sigma[2], 2), Math.pow(Sigma[3], 2), Math.pow(Sigma[4], 2) };
        return variancia;
    }
    public static double[] Bias(double[] medias, double valorEstimado) {
        double[] bias = new double[medias.length];
        for (int i = 0; i < medias.length; i++) {
            bias[i] = medias[i] - valorEstimado;
        }
        return bias;
    }

    public static double[] Precisao(double[] desviosPadroes) {
        double[] precisao = new double[desviosPadroes.length];
        for (int i = 0; i < desviosPadroes.length; i++) {
            precisao[i] = 2 * desviosPadroes[i];
        }
        return precisao;
    }

    public static double[] Incerteza(double[] bias, double[] precisao) {
        double[] incerteza = new double[bias.length];
        for (int i = 0; i < bias.length; i++) {
            incerteza[i] = Math.sqrt(Math.pow(bias[i], 2) + Math.pow(precisao[i], 2));
        }
        return incerteza;
    }

    public static double Media(double[] medias){
        double Valor_estimado = (medias[0] + medias[1] + medias[2] + medias[3] + medias[4])/5;
        return Valor_estimado;
    }
}

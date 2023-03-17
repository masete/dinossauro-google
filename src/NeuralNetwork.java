import java.io.*;

public class NeuralNetwork {

    static final double TAXA_APRENDIZADO = 0.1;
    static final double TAXA_PESO_INICIAL = 1.0;
    static final int BIAS = 1;

    public static void main(String[] args) {
        // TODO: Implement main logic
    }

    static class Neuronio {
        double[] Peso;
        double Erro;
        double Saida;
        int QuantidadeLigacoes;
    }

    static class Camada {
        Neuronio[] Neuronios;
        int QuantidadeNeuronios;
    }

    static class RedeNeural {
        Camada CamadaEntrada;
        Camada[] CamadaEscondida;
        Camada CamadaSaida;
        int QuantidadeEscondidas;
    }

    static double relu(double X) {
        if (X < 0) {
            return 0;
        } else {
            return X;
        }
    }

    static double reluDx(double X) {
        if (X < 0) {
            return 0;
        } else {
            return 1;
        }
    }

    //METODO INIT
    static RedeNeural RNA_CarregarRede(String fileName) {
        int i, j, k;

        try (DataInputStream dis = new DataInputStream(new FileInputStream(fileName))) {
            int QtdEscondida = dis.readInt();
            int QtdNeuroEntrada = dis.readInt();
            int QtdNeuroEscondida = dis.readInt();
            int QtdNeuroSaida = dis.readInt();

            RedeNeural Temp = RNA_CriarRedeNeural(QtdEscondida, QtdNeuroEntrada, QtdNeuroEscondida, QtdNeuroSaida);

            for (k = 0; k < Temp.QuantidadeEscondidas; k++) {
                for (i = 0; i < Temp.CamadaEscondida[k].QuantidadeNeuronios; i++) {
                    for (j = 0; j < Temp.CamadaEscondida[k].Neuronios[i].QuantidadeLigacoes; j++) {
                        Temp.CamadaEscondida[k].Neuronios[i].Peso[j] = dis.readDouble();
                    }
                }
            }
            for (i = 0; i < Temp.CamadaSaida.QuantidadeNeuronios; i++) {
                for (j = 0; j < Temp.CamadaSaida.Neuronios[i].QuantidadeLigacoes; j++) {
                    Temp.CamadaSaida.Neuronios[i].Peso[j] = dis.readDouble();
                }
            }

            return Temp;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static void RNA_CopiarVetorParaCamadas(RedeNeural Rede, double[] Vetor) {
        int j, k, l;

        j = 0;

        for (int i = 0; i < Rede.QuantidadeEscondidas; i++) {
            for (k = 0; k < Rede.CamadaEscondida[i].QuantidadeNeuronios; k++) {
                for (l = 0; l < Rede.CamadaEscondida[i].Neuronios[k].QuantidadeLigacoes; l++) {
                    Rede.CamadaEscondida[i].Neuronios[k].Peso[l] = Vetor[j];
                    j++;
                }
            }
        }

        for (k = 0; k < Rede.CamadaSaida.QuantidadeNeuronios; k++) {
            for (l = 0; l < Rede.CamadaSaida.Neuronios[k].QuantidadeLigacoes; l++) {
                Rede.CamadaSaida.Neuronios[k].Peso[l] = Vetor[j];
                j++;
            }
        }
    }

    static void RNA_CopiarParaEntrada(RedeNeural Rede, double[] VetorEntrada) {
        int i;

        for (i = 0; i < Rede.CamadaEntrada.QuantidadeNeuronios - BIAS; i++) {
            Rede.CamadaEntrada.Neuronios[i].Saida = VetorEntrada[i];
        }
    }

    static int RNA_QuantidadePesos(RedeNeural Rede) {
        int Soma = 0;
        for (int i = 0; i < Rede.QuantidadeEscondidas; i++) {
            for (int j = 0; j < Rede.CamadaEscondida[i].QuantidadeNeuronios; j++) {
                Soma = Soma + Rede.CamadaEscondida[i].Neuronios[j].QuantidadeLigacoes;
            }
        }

        for (int i = 0; i < Rede.CamadaSaida.QuantidadeNeuronios; i++) {
            Soma = Soma + Rede.CamadaSaida.Neuronios[i].QuantidadeLigacoes;
        }
        return Soma;
    }

    static void RNA_CopiarDaSaida(RedeNeural Rede, double[] VetorSaida) {
        int i;

        for (i = 0; i < Rede.CamadaSaida.QuantidadeNeuronios; i++) {
            VetorSaida[i] = Rede.CamadaSaida.Neuronios[i].Saida;
        }
    }


    static void RNA_CalcularSaida(RedeNeural Rede) {
        int i, j, k;
        double Somatorio;

        for (i = 0; i < Rede.CamadaEscondida[0].QuantidadeNeuronios - BIAS; i++) {
            Somatorio = 0;
            for (j = 0; j < Rede.CamadaEntrada.QuantidadeNeuronios; j++) {
                Somatorio = Somatorio + Rede.CamadaEntrada.Neuronios[j].Saida * Rede.CamadaEscondida[0].Neuronios[i].Peso[j];
            }
            Rede.CamadaEscondida[0].Neuronios[i].Saida = relu(Somatorio);
        }

        for (k = 1; k < Rede.QuantidadeEscondidas; k++) {
            for (i = 0; i < Rede.CamadaEscondida[k].QuantidadeNeuronios - BIAS; i++) {
                Somatorio = 0;
                for (j = 0; j < Rede.CamadaEscondida[k - 1].QuantidadeNeuronios; j++) {
                    Somatorio = Somatorio + Rede.CamadaEscondida[k - 1].Neuronios[j].Saida * Rede.CamadaEscondida[k].Neuronios[i].Peso[j];
                }
                Rede.CamadaEscondida[k].Neuronios[i].Saida = relu(Somatorio);
            }
        }

        for (i = 0; i < Rede.CamadaSaida.QuantidadeNeuronios; i++) {
            Somatorio = 0;
            for (j = 0; j < Rede.CamadaEscondida[k - 1].QuantidadeNeuronios; j++) {
                Somatorio = Somatorio + Rede.CamadaEscondida[k - 1].Neuronios[j].Saida * Rede.CamadaSaida.Neuronios[i].Peso[j];
            }
            Rede.CamadaSaida.Neuronios[i].Saida = relu(Somatorio);
        }
    }

    static void RNA_CriarNeuronio(Neuronio Neuron, int QuantidadeLigacoes) {
        int i;

        Neuron.QuantidadeLigacoes = QuantidadeLigacoes;
        Neuron.Peso = new double[QuantidadeLigacoes];

        for (i = 0; i < QuantidadeLigacoes; i++) {
            Neuron.Peso[i] = (int) (Math.random() * 2000) - 1000;
        }

        Neuron.Erro = 0;
        Neuron.Saida = 1;
    }

    static RedeNeural RNA_CriarRedeNeural(int QuantidadeEscondidas, int QtdNeuroniosEntrada, int QtdNeuroniosEscondida, int QtdNeuroniosSaida) {
        int i, j;

        QtdNeuroniosEntrada = QtdNeuroniosEntrada + BIAS;
        QtdNeuroniosEscondida = QtdNeuroniosEscondida + BIAS;

        RedeNeural Rede = new RedeNeural();

        Rede.CamadaEntrada.QuantidadeNeuronios = QtdNeuroniosEntrada;
        Rede.CamadaEntrada.Neuronios = new Neuronio[QtdNeuroniosEntrada];

        for (i = 0; i < QtdNeuroniosEntrada; i++) {
            Rede.CamadaEntrada.Neuronios[i].Saida = 1.0;
        }

        Rede.QuantidadeEscondidas = QuantidadeEscondidas;
        Rede.CamadaEscondida = new Camada[QuantidadeEscondidas];

        for (i = 0; i < QuantidadeEscondidas; i++) {
            Rede.CamadaEscondida[i].QuantidadeNeuronios = QtdNeuroniosEscondida;
            Rede.CamadaEscondida[i].Neuronios = new Neuronio[QtdNeuroniosEscondida];

            for (j = 0; j < QtdNeuroniosEscondida; j++) {
                if (i == 0) {
                    RNA_CriarNeuronio(Rede.CamadaEscondida[i].Neuronios[j], QtdNeuroniosEntrada);
                } else {
                    RNA_CriarNeuronio(Rede.CamadaEscondida[i].Neuronios[j], QtdNeuroniosEscondida);
                }
            }
        }

        Rede.CamadaSaida.QuantidadeNeuronios = QtdNeuroniosSaida;
        Rede.CamadaSaida.Neuronios = new Neuronio[QtdNeuroniosSaida];

        for (j = 0; j < QtdNeuroniosSaida; j++) {
            RNA_CriarNeuronio(Rede.CamadaSaida.Neuronios[j], QtdNeuroniosEscondida);
        }

        return Rede;
    }

    static RedeNeural RNA_DestruirRedeNeural(RedeNeural Rede) {
        // Em Java, a coleta de lixo gerencia a liberação de memória, então este método não é necessário.
        return null;
    }


    static void RNA_SalvarRede(RedeNeural Temp, String fileName) {
        int i, j, k;

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName))) {
            dos.writeInt(Temp.QuantidadeEscondidas);
            dos.writeInt(Temp.CamadaEntrada.QuantidadeNeuronios);
            dos.writeInt(Temp.CamadaEscondida[0].QuantidadeNeuronios);
            dos.writeInt(Temp.CamadaSaida.QuantidadeNeuronios);

            for (k = 0; k < Temp.QuantidadeEscondidas; k++) {
                for (i = 0; i < Temp.CamadaEscondida[k].QuantidadeNeuronios; i++) {
                    for (j = 0; j < Temp.CamadaEscondida[k].Neuronios[i].QuantidadeLigacoes; j++) {
                        dos.writeDouble(Temp.CamadaEscondida[k].Neuronios[i].Peso[j]);
                    }
                }
            }

            for (i = 0; i < Temp.CamadaSaida.QuantidadeNeuronios; i++) {
                for (j = 0; j < Temp.CamadaSaida.Neuronios[i].QuantidadeLigacoes; j++) {
                    dos.writeDouble(Temp.CamadaSaida.Neuronios[i].Peso[j]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


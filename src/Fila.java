
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Predicate;

public class Fila<E> {

    private Celula<E> frente;
    private Celula<E> tras;

    public Fila() {
        Celula<E> sentinela = new Celula<>();

        frente = sentinela;
        tras = sentinela;
    }

    public void enfileirar(E item) {

        Celula<E> nova = new Celula<>(item, null);

        tras.setProximo(nova);
        tras = nova;
    }

    public E desenfileirar() {

        if (frente == tras) {
            throw new NoSuchElementException("Fila vazia");
        }

        Celula<E> celulaRemovida = frente.getProximo();

        E item = celulaRemovida.getItem();

        frente.setProximo(celulaRemovida.getProximo());

        if (celulaRemovida == tras) {
            tras = frente;
        }

        return item;
    }



    public double calcularValorMedio(Function<E, Double> extrator, int quantidade){

        double soma = 0;
        int contador = 0;

        Celula<E> aux = frente.getProximo();

        while(contador < quantidade){
            if (this.frente == this.tras){
                throw new IllegalArgumentException("erro");
            }
            E item = aux.getItem();

            double valor = extrator.apply(item);

            soma+=valor;
            aux = aux.getProximo();
            contador++;


        }


        return soma;
    }

    public Fila<E> filtrar(Predicate<E> condicional, int quantidade){
        if(this.frente == this.tras){
            throw new IllegalArgumentException("erro");

        }
        Fila<E> novaFila = new Fila<>();
        int contador = 0;
        Celula<E> aux = frente.getProximo();
        while(contador < quantidade){

            E item = aux.getItem();
            if(condicional.test(item) == true){
                novaFila.enfileirar(item);
            }
            contador++;
            aux = aux.getProximo();
        }

        return novaFila;

    }
}

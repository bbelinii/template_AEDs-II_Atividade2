import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class ABB<K, V> implements IMapeamento<K, V>{

	// atributos
	private long comparacoes;
	private long inicio;
	private long termino;
	private No<K, V> raiz;
	private Comparator<K> comparador;

	/**
	 * Construtor da classe.
	 * @param comparador o comparador a ser utilizado para organizar os elementos da árvore.
	 */
	public ABB(Comparator<K> comparador) {
		this.comparador = comparador;
		this.raiz = null;
		this.comparacoes = 0;
	}

	@SuppressWarnings("unchecked")
	public ABB() {
		this((a, b) -> ((Comparable<? super K>) a).compareTo(b));
	}

	public <T> ABB(ABB<T, V> origem, Function<V, K> extratorDeChave) {
		this();
		copiar(origem.raiz, extratorDeChave);
	}

	private <T> void copiar(No<T, V> atual, Function<V, K> extratorDeChave) {
		if (atual == null) {
			return;
		}
		copiar(atual.getEsquerda(), extratorDeChave);
		V item = atual.getItem();
		inserir(extratorDeChave.apply(item), item);
		copiar(atual.getDireita(), extratorDeChave);
	}

	@Override
	public long getComparacoes() {
		return comparacoes;
	}

	@Override
	public double getTempo() {
		return (termino - inicio) / 1_000_000.0;
	}

	@Override
	public int inserir(K chave, V valor){
		comparacoes = 0;
		inicio = System.nanoTime();
		try {
			raiz = inserir(raiz, chave, valor);
			return (int) comparacoes;
		} finally {
			termino = System.nanoTime();
		}
	}

	private No<K, V> inserir(No<K, V> atual, K chave, V valor){
		if (atual == null ){
			return new No<>(chave, valor);
		}

		int resultado = comparador.compare(chave, atual.getChave());
		comparacoes++;

		if(resultado < 0){
			atual.setEsquerda(inserir(atual.getEsquerda(), chave, valor));
		}
		else if (resultado > 0){
			atual.setDireita(inserir(atual.getDireita(), chave, valor));
		}else{
			throw new IllegalArgumentException("O item já foi inserido anteriormente na árvore.");
		}
		return atual;
	}

	@Override
	public V pesquisar(K chave){
		comparacoes = 0;
		inicio = System.nanoTime();
		try {
			return pesquisar(raiz, chave);
		} finally {
			termino = System.nanoTime();
		}
	}

	private V pesquisar(No<K, V> atual, K chave){
		if(atual == null){
			throw new NoSuchElementException("nao localizado");
		}

		int resultado = comparador.compare(chave, atual.getChave());
		comparacoes++;

		if (resultado == 0){
			return atual.getItem();
		}
		else if(resultado > 0){
			return pesquisar(atual.getDireita(), chave);
		}
		else{
			return pesquisar(atual.getEsquerda(), chave);
		}
	}

	public void excluir(K chave){
		remover(chave);
	}

	@Override
	public V remover(K chave){
		comparacoes = 0;
		inicio = System.nanoTime();
		try {
			V item;
			try {
				item = pesquisar(raiz, chave);
			} catch (NoSuchElementException e) {
				throw new NoSuchElementException("O item a ser removido não foi localizado na árvore!");
			}
			raiz = excluir(raiz, chave);
			return item;
		} finally {
			termino = System.nanoTime();
		}
	}

	private No<K, V> excluir(No<K, V> atual, K chave){
		if (atual == null){
			throw new NoSuchElementException("O item a ser removido não foi localizado na árvore!");
		}

		int resultado = comparador.compare(chave, atual.getChave());
		comparacoes++;

		if(resultado < 0){
			atual.setEsquerda(excluir(atual.getEsquerda(), chave));
		}
		else if(resultado > 0){
			atual.setDireita(excluir(atual.getDireita(), chave));
		}
		else{
			if(atual.getEsquerda() == null){
				return atual.getDireita();
			}
			if (atual.getDireita() == null) {
				return atual.getEsquerda();
			}
			No<K, V> antessesor = atual.getEsquerda();

			while(antessesor.getDireita() != null){
				antessesor = antessesor.getDireita();
			}

			atual.setChave(antessesor.getChave());
			atual.setItem(antessesor.getItem());

			atual.setEsquerda(excluir(atual.getEsquerda(), antessesor.getChave()));
		}
		return atual;
	}

	@Override
	public int tamanho() {
		return tamanho(raiz);
	}

	private int tamanho(No<K, V> atual) {
		if (atual == null) {
			return 0;
		}
		return 1 + tamanho(atual.getEsquerda()) + tamanho(atual.getDireita());
	}

	public boolean vazia() {
		return raiz == null;
	}

	@Override
	public String percorrer() {
		StringBuilder texto = new StringBuilder();
		percorrer(raiz, texto);
		return texto.toString();
	}

	private void percorrer(No<K, V> atual, StringBuilder texto) {
		if (atual == null) {
			return;
		}
		percorrer(atual.getEsquerda(), texto);
		texto.append(atual.getItem()).append("\n");
		percorrer(atual.getDireita(), texto);
	}

	public String caminhamentoEmOrdem() {
		StringBuilder texto = new StringBuilder();
		caminhamentoEmOrdem(raiz, texto);
		return texto.toString();
	}

	private void caminhamentoEmOrdem(No<K, V> atual, StringBuilder texto) {
		if (atual == null) {
			return;
		}
		caminhamentoEmOrdem(atual.getEsquerda(), texto);
		if (texto.length() > 0) {
			texto.append(" - ");
		}
		texto.append(atual.getChave());
		caminhamentoEmOrdem(atual.getDireita(), texto);
	}

	@Override
	public String toString() {
		return percorrer();
	}

	public Lista<V> recortar(K chaveInicio, K chaveFinal){
		comparacoes = 0;
		inicio = System.nanoTime();
		try {
			Lista<V> lista1 = new Lista<>();
			recortar(raiz, chaveInicio, chaveFinal, lista1);
			return lista1;
		} finally {
			termino = System.nanoTime();
		}
	}

	private void recortar(No<K, V> atual, K chaveInicio, K chaveFinal, Lista<V> lista1){
		if (atual == null){
			return;
		}

		int resultadoinicio = comparador.compare(atual.getChave(), chaveInicio);
		int resultadofinal = comparador.compare(atual.getChave(), chaveFinal);
		comparacoes += 2;

		if(resultadoinicio > 0){
			recortar(atual.getEsquerda(), chaveInicio, chaveFinal, lista1);
		}

		if (resultadoinicio >= 0 && resultadofinal <=0){
			lista1.inserir(atual.getItem());
		}

		if(resultadofinal < 0){
			recortar(atual.getDireita(), chaveInicio, chaveFinal, lista1);
		}
	}
}

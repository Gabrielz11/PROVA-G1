package server;

/**
 * Representa o estado atual do leilão de forma thread-safe.
 */
public class AuctionState {
    private String itemLeilao = "Notebook Gamer Pro";
    private double maiorLance = 1000.0;
    private String vencedorAtual = "Nenhum";
    private boolean leilaoAtivo = true;

    public synchronized String getItemLeilao() { return itemLeilao; }
    public synchronized double getMaiorLance() { return maiorLance; }
    public synchronized String getVencedorAtual() { return vencedorAtual; }
    public synchronized boolean isLeilaoAtivo() { return leilaoAtivo; }

    public synchronized boolean processarLance(double valor, String usuario) {
        if (valor > maiorLance && leilaoAtivo) {
            maiorLance = valor;
            vencedorAtual = usuario;
            return true;
        }
        return false;
    }
}

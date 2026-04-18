package server;

/**
 * Representa o estado atual do leilão de forma thread-safe.
 */
public class AuctionState {
    private String itemLeilao;
    private double maiorLance;
    private String vencedorAtual = "Nenhum";
    private boolean leilaoAtivo = true;

    public AuctionState(String item, double lanceInicial) {
        this.itemLeilao = item;
        this.maiorLance = lanceInicial;
    }

    public synchronized String getItemLeilao() { return itemLeilao; }
    public synchronized double getMaiorLance() { return maiorLance; }
    public synchronized String getVencedorAtual() { return vencedorAtual; }
    public synchronized boolean isLeilaoAtivo() { return leilaoAtivo; }

    public synchronized void encerrarLeilao() {
        this.leilaoAtivo = false;
    }

    public synchronized boolean processarLance(double valor, String usuario) {
        if (valor > maiorLance && leilaoAtivo) {
            maiorLance = valor;
            vencedorAtual = usuario;
            return true;
        }
        return false;
    }
}

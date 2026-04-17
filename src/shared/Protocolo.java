package shared;

/**
 * Define as constantes de comunicação para garantir que Cliente e Servidor
 * falem a mesma "língua" sem erros de digitação.
 */
public class Protocolo {
    // Comandos do Cliente -> Servidor
    public static final String LANCE = "LANCE:";
    public static final String SAIR = "SAIR";
    public static final String LOGIN = "AUTH:";

    // Comandos do Servidor -> Cliente
    public static final String AUTH_REQ = "AUTH_REQ";
    public static final String AUTH_SUCCESS = "AUTH_SUCCESS";
    public static final String AUTH_FAIL = "AUTH_FAIL";
    public static final String STATUS = "STATUS:";
    public static final String NOVO_LANCE = "NOVO_LANCE:";
    public static final String ERRO = "ERRO:";
    public static final String BEM_VINDO = "BEM_VINDO:";
}

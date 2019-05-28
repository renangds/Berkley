public enum Messages {
    EXIT(0), BERKLEY(1), ELEICAO(2), DAEMON(3),
    ELEICAO_CHAMADO_RESPOSTA(4);

    private int value;

    Messages(int value){
        this.value = value;
    }

    public String getValue(){
        return Integer.toString(this.value);
    }

}

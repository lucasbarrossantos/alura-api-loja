package br.com.alura.microservice.loja.controller.dto;

import java.util.List;

public class CompraDTO {

    private List<ItemCompra> itens;
    private EnderecoDTO endereco;

    public List<ItemCompra> getItens() {
        return itens;
    }

    public void setItens(List<ItemCompra> itens) {
        this.itens = itens;
    }

    public EnderecoDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDTO endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return "CompraDTO{" +
                "itens=" + itens +
                ", endereco=" + endereco +
                '}';
    }
}

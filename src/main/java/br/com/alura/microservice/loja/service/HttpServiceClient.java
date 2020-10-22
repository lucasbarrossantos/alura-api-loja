package br.com.alura.microservice.loja.service;

import br.com.alura.microservice.loja.client.FornecedorClient;
import br.com.alura.microservice.loja.controller.dto.CompraDTO;
import br.com.alura.microservice.loja.controller.dto.InfoFornecedorDTO;
import br.com.alura.microservice.loja.controller.dto.InfoPedidoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpServiceClient {

    private final Logger log = LoggerFactory.getLogger(HttpServiceClient.class);

    @Autowired
    private FornecedorClient fornecedorClient;

    public InfoPedidoDTO realizarPedidoParaFornecedor(CompraDTO compraDTO) throws Exception {
        try {
            log.info("Enviando dados para realização de um pedido");
            return fornecedorClient.realizarPedido(compraDTO.getItens());
        } catch (Exception error) {
            log.error("Servidor indisponível! ");
            throw new Exception("Servidor indisponível! ");
        }
    }

    public void tratarEnderecoDoFornecedor(CompraDTO compraDTO) throws Exception {
        try {
            log.info("Buscando informações do fornecedor");
            InfoFornecedorDTO info = fornecedorClient.getInfoPorEstado(compraDTO.getEndereco().getEstado());

            if (info != null)
                System.out.println("Endereço retornado: " + info.getEndereco());

        } catch (Exception error) {
            log.error("Servidor indisponível! ");
            throw new Exception("Servidor indisponível! ");
        }
    }

}

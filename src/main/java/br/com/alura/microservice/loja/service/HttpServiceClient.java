package br.com.alura.microservice.loja.service;

import br.com.alura.microservice.loja.client.FornecedorClient;
import br.com.alura.microservice.loja.client.TransportadorClient;
import br.com.alura.microservice.loja.controller.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class HttpServiceClient {

    private final Logger log = LoggerFactory.getLogger(HttpServiceClient.class);

    @Autowired
    private FornecedorClient fornecedorClient;

    @Autowired
    private TransportadorClient transportadorClient;

    public InfoPedidoDTO realizarPedidoParaFornecedor(CompraDTO compraDTO) throws Exception {
        try {
            log.info("Enviando dados para realização de um pedido");
            return fornecedorClient.realizarPedido(compraDTO.getItens());
        } catch (Exception error) {
            log.error("Servidor indisponível! ");
            throw new Exception("Servidor indisponível! ");
        }
    }

    public InfoFornecedorDTO tratarEnderecoDoFornecedor(CompraDTO compraDTO) throws Exception {
        try {
            log.info("Buscando informações do fornecedor");
            InfoFornecedorDTO info = fornecedorClient.getInfoPorEstado(compraDTO.getEndereco().getEstado());

            if (info != null) {
                System.out.println("Endereço retornado: " + info.getEndereco());
                return info;
            }

        } catch (Exception error) {
            log.error("Servidor indisponível! ");
            throw new Exception("Servidor indisponível! ");
        }

        return null;
    }

    public VoucherDTO reservarEntregaParaTransportador(InfoEntregaDTO infoEntregaDTO) {
        log.info("Enviando dados de reserva para a transportadora");
        return transportadorClient.reservarEntrega(infoEntregaDTO);
    }
}

package br.com.alura.microservice.loja.service;

import br.com.alura.microservice.loja.controller.dto.CompraDTO;
import br.com.alura.microservice.loja.controller.dto.InfoPedidoDTO;
import br.com.alura.microservice.loja.model.Compra;
import br.com.alura.microservice.loja.repository.CompraRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CompraService {
    private final Logger log = LoggerFactory.getLogger(CompraService.class);

    @Autowired
    private HttpServiceClient httpServiceClient;

    @Autowired
    private CompraRepository compraRepository;

    @HystrixCommand(threadPoolKey = "getByIdThreadPool")
    public Compra getById(Long compraId) throws Exception {
        log.info("Realizando consulta de compra por id: {}", compraId);

        return compraRepository.findById(compraId)
                .orElseThrow(() -> {
                    log.error("Não foi possível encontrar uma compra com o id: {}", compraId);
                    return new Exception("Não foi possível encontrar uma compra com o id: " + compraId);
                });
    }

    @HystrixCommand(fallbackMethod = "realizarCompraFallback", threadPoolKey = "realizaCompraThreadPool")
    public Compra realizarCompra(CompraDTO compraDTO) throws Exception {
        log.info("Iniciando processo de realizar compra...");
        httpServiceClient.tratarEnderecoDoFornecedor(compraDTO);
        InfoPedidoDTO dadosPedido = httpServiceClient.realizarPedidoParaFornecedor(compraDTO);
        log.info("Compra realizada com sucesso!");
        return tratarRetornoPedido(compraDTO, dadosPedido);
    }

    public Compra realizarCompraFallback(CompraDTO compra) {
        Compra compraFallback = new Compra();
        compraFallback.setEnderecoDestino(compra.getEndereco().toString());
        return compraFallback;
    }

    private Compra tratarRetornoPedido(CompraDTO compraDTO, InfoPedidoDTO dadosPedido) {
        if (dadosPedido != null)
            return montarCompraEfetuada(compraDTO, dadosPedido);

        return null;
    }

    private Compra montarCompraEfetuada(CompraDTO compraDTO, InfoPedidoDTO pedido) {
        log.info("Montando objeto de Compra");
        Compra compra = new Compra();
        compra.setPedidoId(pedido.getId());
        compra.setTempoDePreparo(pedido.getTempoDePreparo());
        compra.setEnderecoDestino(compraDTO.getEndereco().toString());
        compra = compraRepository.saveAndFlush(compra);
        return compra;
    }
}

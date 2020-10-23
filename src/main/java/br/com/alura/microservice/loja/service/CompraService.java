package br.com.alura.microservice.loja.service;

import br.com.alura.microservice.loja.controller.dto.*;
import br.com.alura.microservice.loja.model.Compra;
import br.com.alura.microservice.loja.repository.CompraRepository;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
        InfoFornecedorDTO infoFornecedor = httpServiceClient.tratarEnderecoDoFornecedor(compraDTO);
        InfoPedidoDTO dadosPedido = httpServiceClient.realizarPedidoParaFornecedor(compraDTO);

        InfoEntregaDTO entregaDTO = montarDadosParaEntrega(compraDTO, infoFornecedor, dadosPedido);
        VoucherDTO voucher = httpServiceClient.reservarEntregaParaTransportador(entregaDTO);

        log.info("Compra realizada com sucesso!");
        return tratarRetornoPedido(compraDTO, dadosPedido, voucher);
    }

    private InfoEntregaDTO montarDadosParaEntrega(CompraDTO compraDTO, InfoFornecedorDTO infoFornecedor, InfoPedidoDTO dadosPedido) {
        log.info("Montando dados para entrega");
        InfoEntregaDTO entregaDTO = new InfoEntregaDTO();
        entregaDTO.setPedidoId(dadosPedido.getId());
        entregaDTO.setDataParaEntrega(LocalDate.now().plusDays(dadosPedido.getTempoDePreparo()));
        entregaDTO.setEnderecoOrigem(infoFornecedor.getEndereco());
        entregaDTO.setEnderecoDestino(compraDTO.getEndereco().toString());
        log.info("Dados montados para entrega.");
        return entregaDTO;
    }

    public Compra realizarCompraFallback(CompraDTO compra) {
        log.warn("realizarCompraFallback() ... ");
        Compra compraFallback = new Compra();
        compraFallback.setEnderecoDestino(compra.getEndereco().toString());
        return compraFallback;
    }

    private Compra tratarRetornoPedido(CompraDTO compraDTO, InfoPedidoDTO dadosPedido, VoucherDTO voucher) {
        if (dadosPedido != null)
            return montarCompraEfetuada(compraDTO, dadosPedido, voucher);

        return null;
    }

    private Compra montarCompraEfetuada(CompraDTO compraDTO, InfoPedidoDTO pedido, VoucherDTO voucher) {
        log.info("Montando objeto de Compra");
        Compra compra = new Compra();
        compra.setPedidoId(pedido.getId());
        compra.setTempoDePreparo(pedido.getTempoDePreparo());
        compra.setEnderecoDestino(compraDTO.getEndereco().toString());
        compra.setDataParaEntrega(voucher.getPrevisaoParaEntrega());
        compra.setVoucher(voucher.getNumero());
        compra = compraRepository.saveAndFlush(compra);
        return compra;
    }
}

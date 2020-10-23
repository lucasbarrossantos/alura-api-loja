package br.com.alura.microservice.loja.client;

import br.com.alura.microservice.loja.controller.dto.InfoEntregaDTO;
import br.com.alura.microservice.loja.controller.dto.VoucherDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("transportador")
public interface TransportadorClient {

    @PostMapping("/entrega")
    VoucherDTO reservarEntrega(@RequestBody InfoEntregaDTO infoEntregaDTO);

}

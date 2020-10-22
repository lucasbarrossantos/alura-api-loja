package br.com.alura.microservice.loja.controller;

import br.com.alura.microservice.loja.controller.dto.CompraDTO;
import br.com.alura.microservice.loja.model.Compra;
import br.com.alura.microservice.loja.repository.CompraRepository;
import br.com.alura.microservice.loja.service.CompraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/compra")
public class CompraController {

    private final Logger log = LoggerFactory.getLogger(CompraController.class);

    @Autowired
    private CompraService compraService;


    @GetMapping("/{compraId}")
    public Compra getById(@PathVariable Long compraId) throws Exception {
        log.info("Recebido id: {} para consulta de compra", compraId);
        Compra compra = compraService.getById(compraId);
        log.info("Compra encontrada: {}", compra.toString());
        return compra;
    }

    @PostMapping
    public Compra realizarCompra(@RequestBody CompraDTO compraDTO) throws Exception {
        log.info("Recebido dados da compra: {}", compraDTO.toString());
        return compraService.realizarCompra(compraDTO);
    }

}

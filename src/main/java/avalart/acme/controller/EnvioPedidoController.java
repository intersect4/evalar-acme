package avalart.acme.controller;

import avalart.acme.dto.EnviarPedidoRequest;
import avalart.acme.dto.EnviarPedidoResponse;
import avalart.acme.dto.PedidoRespuestaData;
import avalart.acme.service.EnvioPedidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EnvioPedidoController {

    private final EnvioPedidoService envioPedidoService;

    public EnvioPedidoController(EnvioPedidoService envioPedidoService) {
        this.envioPedidoService = envioPedidoService;
    }

    @PostMapping("/enviar-pedido")
    public ResponseEntity<EnviarPedidoResponse> enviarPedido(
            @Valid @RequestBody EnviarPedidoRequest request) {
        PedidoRespuestaData respuesta = envioPedidoService.enviarPedido(request.enviarPedido());
        return ResponseEntity.ok(new EnviarPedidoResponse(respuesta));
    }
}

package avalart.acme.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record EnviarPedidoRequest(
        @Valid @NotNull PedidoData enviarPedido
) {
}

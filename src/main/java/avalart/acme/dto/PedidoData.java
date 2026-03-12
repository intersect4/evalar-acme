package avalart.acme.dto;

import jakarta.validation.constraints.NotBlank;

public record PedidoData(
        @NotBlank String numPedido,
        @NotBlank String cantidadPedido,
        @NotBlank String codigoEAN,
        @NotBlank String nombreProducto,
        @NotBlank String numDocumento,
        @NotBlank String direccion
) {
}

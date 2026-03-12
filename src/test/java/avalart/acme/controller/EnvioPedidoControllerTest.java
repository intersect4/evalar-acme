package avalart.acme.controller;

import avalart.acme.dto.PedidoRespuestaData;
import avalart.acme.service.EnvioPedidoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnvioPedidoController.class)
class EnvioPedidoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnvioPedidoService envioPedidoService;

    @Test
    void testEnviarPedidoEndpoint() throws Exception {
        when(envioPedidoService.enviarPedido(any()))
                .thenReturn(new PedidoRespuestaData("80375472", "Entregado exitosamente al cliente"));

        String jsonRequest = """
                {
                    "enviarPedido": {
                        "numPedido": "75630275",
                        "cantidadPedido": "1",
                        "codigoEAN": "00110000765191002104587",
                        "nombreProducto": "Armario INVAL",
                        "numDocumento": "1113987400",
                        "direccion": "CR 72B 45 12 APT 301"
                    }
                }
                """;

        mockMvc.perform(post("/api/enviar-pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enviarPedidoRespuesta.codigoEnvio").value("80375472"))
                .andExpect(jsonPath("$.enviarPedidoRespuesta.estado").value("Entregado exitosamente al cliente"));
    }

    @Test
    void testEnviarPedidoBadRequest() throws Exception {
        String invalidRequest = """
                {
                    "enviarPedido": {
                        "numPedido": ""
                    }
                }
                """;

        mockMvc.perform(post("/api/enviar-pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEnviarPedidoSinBody() throws Exception {
        mockMvc.perform(post("/api/enviar-pedido")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}

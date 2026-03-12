package avalart.acme.service;

import avalart.acme.dto.PedidoData;
import avalart.acme.dto.PedidoRespuestaData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvioPedidoServiceTest {

    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock
    private RestClient.RequestBodySpec requestBodySpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    private EnvioPedidoService service;

    private static final String SOAP_ENDPOINT = "https://run.mocky.io/v3/test";

    @BeforeEach
    void setUp() {
        service = new EnvioPedidoService(restClient, SOAP_ENDPOINT);
    }

    private PedidoData samplePedido() {
        return new PedidoData(
                "75630275", "1", "00110000765191002104587",
                "Armario INVAL", "1113987400", "CR 72B 45 12 APT 301"
        );
    }

    @Test
    void testBuildSoapXml() {
        String xml = service.buildSoapXml(samplePedido());

        assertThat(xml).contains("<pedido>75630275</pedido>");
        assertThat(xml).contains("<Cantidad>1</Cantidad>");
        assertThat(xml).contains("<EAN>00110000765191002104587</EAN>");
        assertThat(xml).contains("<Producto>Armario INVAL</Producto>");
        assertThat(xml).contains("<Cedula>1113987400</Cedula>");
        assertThat(xml).contains("<Direccion>CR 72B 45 12 APT 301</Direccion>");
        assertThat(xml).contains("soapenv:Envelope");
        assertThat(xml).contains("EnvioPedidoRequest");
    }

    @Test
    void testParseSoapResponse() {
        String soapResponse = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                    xmlns:env="http://WSDLs/EnvioPedidos/EnvioPedidosAcme">
                  <soapenv:Header/>
                  <soapenv:Body>
                    <env:EnvioPedidoAcmeResponse>
                      <EnvioPedidoResponse>
                        <Codigo>80375472</Codigo>
                        <Mensaje>Entregado exitosamente al cliente</Mensaje>
                      </EnvioPedidoResponse>
                    </env:EnvioPedidoAcmeResponse>
                  </soapenv:Body>
                </soapenv:Envelope>
                """;

        PedidoRespuestaData result = service.parseSoapResponse(soapResponse);

        assertThat(result.codigoEnvio()).isEqualTo("80375472");
        assertThat(result.estado()).isEqualTo("Entregado exitosamente al cliente");
    }

    @Test
    void testEnviarPedido() {
        String soapResponse = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                    xmlns:env="http://WSDLs/EnvioPedidos/EnvioPedidosAcme">
                  <soapenv:Header/>
                  <soapenv:Body>
                    <env:EnvioPedidoAcmeResponse>
                      <EnvioPedidoResponse>
                        <Codigo>80375472</Codigo>
                        <Mensaje>Entregado exitosamente al cliente</Mensaje>
                      </EnvioPedidoResponse>
                    </env:EnvioPedidoAcmeResponse>
                  </soapenv:Body>
                </soapenv:Envelope>
                """;

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(eq(SOAP_ENDPOINT))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.TEXT_XML)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn(soapResponse);

        PedidoRespuestaData result = service.enviarPedido(samplePedido());

        assertThat(result.codigoEnvio()).isEqualTo("80375472");
        assertThat(result.estado()).isEqualTo("Entregado exitosamente al cliente");
    }
}

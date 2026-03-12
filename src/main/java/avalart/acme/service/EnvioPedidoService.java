package avalart.acme.service;

import avalart.acme.dto.PedidoData;
import avalart.acme.dto.PedidoRespuestaData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;

@Service
public class EnvioPedidoService {

    private final RestClient restClient;
    private final String soapEndpointUrl;

    public EnvioPedidoService(RestClient restClient,
                              @Value("${soap.endpoint.url}") String soapEndpointUrl) {
        this.restClient = restClient;
        this.soapEndpointUrl = soapEndpointUrl;
    }

    public PedidoRespuestaData enviarPedido(PedidoData pedido) {
        String soapXml = buildSoapXml(pedido);

        String xmlResponse = restClient.post()
                .uri(soapEndpointUrl)
                .contentType(MediaType.TEXT_XML)
                .body(soapXml)
                .retrieve()
                .body(String.class);

        return parseSoapResponse(xmlResponse);
    }

    String buildSoapXml(PedidoData pedido) {
        return """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" \
                xmlns:env="http://WSDLs/EnvioPedidos/EnvioPedidosAcme">
                  <soapenv:Header/>
                  <soapenv:Body>
                    <env:EnvioPedidoAcme>
                      <EnvioPedidoRequest>
                        <pedido>%s</pedido>
                        <Cantidad>%s</Cantidad>
                        <EAN>%s</EAN>
                        <Producto>%s</Producto>
                        <Cedula>%s</Cedula>
                        <Direccion>%s</Direccion>
                      </EnvioPedidoRequest>
                    </env:EnvioPedidoAcme>
                  </soapenv:Body>
                </soapenv:Envelope>
                """.formatted(
                pedido.numPedido(),
                pedido.cantidadPedido(),
                pedido.codigoEAN(),
                pedido.nombreProducto(),
                pedido.numDocumento(),
                pedido.direccion()
        );
    }

    PedidoRespuestaData parseSoapResponse(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(xml)));

            String codigo = doc.getElementsByTagName("Codigo").item(0).getTextContent();
            String mensaje = doc.getElementsByTagName("Mensaje").item(0).getTextContent();

            return new PedidoRespuestaData(codigo, mensaje);
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear respuesta SOAP", e);
        }
    }
}

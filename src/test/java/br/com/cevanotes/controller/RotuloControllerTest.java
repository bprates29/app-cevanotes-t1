package br.com.cevanotes.controller;

import br.com.cevanotes.dto.RotuloDTO;
import br.com.cevanotes.model.Rotulo;
import br.com.cevanotes.service.RotuloService;
import br.com.cevanotes.utils.RotuloFixures;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import io.javalin.testtools.JavalinTest;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static br.com.cevanotes.utils.RotuloFixures.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RotuloControllerTest {

    @Mock
    private RotuloService rotuloService;

    @InjectMocks
    private RotuloController rotuloController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deveListarTodosOsRotulos() {
        Rotulo rotulo1 = new Rotulo(1, "Heineken", "Larger", 5.0, "Heineken", LocalDate.now());
        Rotulo rotulo2 = new Rotulo(2, "Stells", "Pilsen", 4.0, "Bier", LocalDate.now());

        List<Rotulo> rotulos = List.of(rotulo1, rotulo2);

        when(rotuloService.listar()).thenReturn(rotulos);

        JavalinTest.test(criarAppComRotas(), (server, client) -> {
            var response = client.get("/rotulos");
            assertEquals(200, response.code());

            assertNotNull(response.body());
            List<Rotulo> responseRotulos = objectMapper.readValue(
                    response.body().string(),
                    new TypeReference<List<Rotulo>>() {
                    }
            );

            assertEquals(2, responseRotulos.size());
            assertEquals("Heineken", responseRotulos.get(0).getNome());
            assertEquals("Stells", responseRotulos.get(1).getNome());
        });

        Mockito.verify(rotuloService).listar();
    }

    @NotNull
    private Javalin criarAppComRotas() {
        Javalin app = Javalin.create();
        rotuloController.registrarRotas(app);
        return app;
    }

    @Test
    void deveRetornarArrayVazioQuandoNaoHouverRotulos() {
        when(rotuloService.listar()).thenReturn(List.of());

        JavalinTest.test(criarAppComRotas(), (server, client) -> {
            var response = client.get("/rotulos");
            assertEquals(200, response.code());

            assertNotNull(response.body());
            List<Rotulo> responseRotulos = objectMapper.readValue(
                    response.body().string(),
                    new TypeReference<List<Rotulo>>() {
                    }
            );

            assertEquals(0, responseRotulos.size());
            assertTrue(responseRotulos.isEmpty());
        });
    }

    @Test
    void deveBuscarRotuloPorId() {
        var rotulo = buildRotulo();

        when(rotuloService.buscarPorId(ID)).thenReturn(rotulo);

        JavalinTest.test(criarAppComRotas(), (server, client) -> {
            var response = client.get("/rotulos/1");
            assertEquals(200, response.code());

            Rotulo responseRotulo = objectMapper.readValue(
                    response.body().string(),
                    new TypeReference<Rotulo>() {
                    }
            );

            assertEquals(ID, responseRotulo.getId());
            assertEquals(NOME_ROTULO, responseRotulo.getNome());
            assertEquals(ESTILO_ROTULO, responseRotulo.getEstilo());
            assertEquals(TEOR_ALCOOLICO_ROTULO, responseRotulo.getTeorAlcoolico());
        });

        verify(rotuloService).buscarPorId(ID);
    }

    @Test
    void deveRetornar404AoBuscarRotuloInexistente() {
        when(rotuloService.buscarPorId(999)).thenThrow(new NotFoundResponse(("Rótulo não encontrado")));

        JavalinTest.test(criarAppComRotas(), (server, client) -> {
            var response = client.get("/rotulos/999");
            assertEquals(404, response.code());
        });

        verify(rotuloService).buscarPorId(999);
    }

    @Test
    void deveRetornar400ComIdInvalido() {
        JavalinTest.test(criarAppComRotas(), (server, client) -> {
            var response = client.get("/rotulos/abc");
            assertEquals(400, response.code());
            assert response.body() != null;
            assertEquals("ID inválido. Use um número inteiro!", response.body().string());
        });
    }

    private RotuloDTO criarRotuloDTO(String nome, String estilo, double teor, String cervejaria) {
        return new RotuloDTO(nome, estilo, teor, cervejaria);
    }

    @Test
    void deveCriarUmNovoRotulo() {
        var dto = criarRotuloDTO("Corona", "Lager", 4.5, "Corona");
        var rotuloSalvo = new Rotulo(3, "Corona", "Lager", 4.5, "Corona", LocalDate.now());

        when(rotuloService.salvar(dto)).thenReturn(rotuloSalvo);

        JavalinTest.test(criarAppComRotas(), (server, client) -> {
            String jsonBody = objectMapper.writeValueAsString(dto);
            var response = client.post("/rotulos", jsonBody);

            assertEquals(201, response.code());

            Rotulo responseRotulo = objectMapper.readValue(response.body().string(), Rotulo.class);

            assertEquals(3, responseRotulo.getId());
            assertEquals("Corona", responseRotulo.getNome());
            assertEquals("Lager", responseRotulo.getEstilo());
            assertEquals(4.5, responseRotulo.getTeorAlcoolico());
        });

        verify(rotuloService).salvar(any(RotuloDTO.class));
    }

    static Stream<Arguments> dadosInvalidosParaCriacaoDeRotulo() {
        return Stream.of(
                Arguments.of("", "Estilo", 4.5, "Cervejaris", "Nome Obrigatório"),
                Arguments.of("Nome", "", 4.5, "Cervejaris", "Estilo é obrigatório!"),
                Arguments.of("Nome", "Estilo", -1, "Cervejaris", "Teor alcoolico deve ser positivo")
                );
    }

    @ParameterizedTest
    @MethodSource("dadosInvalidosParaCriacaoDeRotulo")
    void deveRetornar400AoCriarRotuloComNomeVazio(String nome, String estilo, double teor, String cervejaria, String menssagemErro) {
        var dto = criarRotuloDTO(nome, estilo, teor, cervejaria);

        JavalinTest.test(criarAppComRotas(), (server, client) -> {
            String jsonBody = objectMapper.writeValueAsString(dto);
            var response = client.post("/rotulos", jsonBody);

            assertEquals(400, response.code());
            assert response.body() != null;
            assertTrue(response.body().string().contains(menssagemErro));
        });

        verifyNoInteractions(rotuloService);
    }

    @Test
    void deveAtualiazrRotulo() {
        var dto = criarRotuloDTO("fake", "fake", 4.5, "corona");
        var rotuloAtulizado = buildRotulo();

        when(rotuloService.atualizar(ID, dto)).thenReturn(rotuloAtulizado);

        JavalinTest.test(criarAppComRotas(), (server, client) -> {
            String jsonBody = objectMapper.writeValueAsString(dto);
            var response = client.put("/rotulos/"+ID, jsonBody);

            assertEquals(200, response.code());
            assert response.body() != null;
            Rotulo responseRotulo = objectMapper.readValue(
                    response.body().string(),
                    Rotulo.class
            );

            assertEquals(NOME_ROTULO, responseRotulo.getNome());
            assertEquals(ID, responseRotulo.getId());
        });

        verify(rotuloService).atualizar(anyInt(), any(RotuloDTO.class));
    }

    @Test
    void deveDeletarRotulo() {
        doNothing().when(rotuloService).deletar(1);

        JavalinTest.test(criarAppComRotas(), (server, client) -> {
            var response = client.delete("/rotulos/1");
            assertEquals(204, response.code());
            assert response.body() != null;
            assertEquals("", response.body().string());
        });

        verify(rotuloService).deletar(anyInt());
    }

    @Test
    void deveRetornar400AoDeletarRotuloInexistente() {

        doThrow(new NotFoundResponse("Rotulo não existente")).when(rotuloService).deletar(999);

        JavalinTest.test(criarAppComRotas(), (server, client) -> {
            var response = client.delete("/rotulos/999");
            assertEquals(404, response.code());
        });

        verify(rotuloService).deletar(anyInt());
    }


}
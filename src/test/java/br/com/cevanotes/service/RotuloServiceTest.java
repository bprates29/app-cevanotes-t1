package br.com.cevanotes.service;

import br.com.cevanotes.dto.RotuloDTO;
import br.com.cevanotes.model.Rotulo;
import br.com.cevanotes.repository.RotuloRepository;
import io.javalin.http.NotFoundResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RotuloServiceTest {

    @Mock
    RotuloRepository repository; // será injetado no service

    @InjectMocks
    RotuloService service; // terá o mock injetado automaticametne

    @Test
    void testSalvarRotulo() {
        RotuloDTO dto = new RotuloDTO();
        dto.setNome("IPA puro Malte");
        dto.setEstilo("IPA");
        dto.setTeorAlcoolico(7.5);
        dto.setCervejaria("Cervejaria Alpha");

        Rotulo rotuloSalvo = new Rotulo();
        rotuloSalvo.setId(1);
        rotuloSalvo.setNome(dto.getNome());
        rotuloSalvo.setEstilo(dto.getEstilo());
        rotuloSalvo.setTeorAlcoolico(dto.getTeorAlcoolico());
        rotuloSalvo.setCervejaria(dto.getCervejaria());
        rotuloSalvo.setDataCadastro(LocalDate.now());

        when(repository.insert(rotuloSalvo)).thenReturn(1);
        when(repository.findById(1)).thenReturn(Optional.of(rotuloSalvo));

        Rotulo result = service.salvar(dto);

        assertEquals(dto.getNome(), result.getNome());
        assertEquals(dto.getEstilo(), result.getEstilo());
        assertEquals(dto.getTeorAlcoolico(), result.getTeorAlcoolico());
        assertEquals(dto.getCervejaria(), result.getCervejaria());
    }

    @Test
    void testAtualizarRotuloExistente () {
        RotuloDTO dto = new RotuloDTO();
        dto.setNome("IPA puro Malte");
        dto.setEstilo("IPA");
        dto.setTeorAlcoolico(7.5);
        dto.setCervejaria("Cervejaria Alpha");

        Rotulo existente = new Rotulo();
        existente.setId(2);
        existente.setNome("Antigo");
        existente.setEstilo("Larger");
        existente.setTeorAlcoolico(4.5);
        existente.setCervejaria("Farrapos");
        existente.setDataCadastro(LocalDate.now());

        Rotulo expectedRotulo = new Rotulo();
        expectedRotulo.setId(2);
        expectedRotulo.setNome(dto.getNome());
        expectedRotulo.setEstilo(dto.getEstilo());
        expectedRotulo.setTeorAlcoolico(dto.getTeorAlcoolico());
        expectedRotulo.setCervejaria(dto.getCervejaria());
        expectedRotulo.setDataCadastro(LocalDate.now());

        when(repository.findById(2)).thenReturn(Optional.of(existente));
        doNothing().when(repository).update(expectedRotulo);

        Rotulo atualizado = service.atualizar(2, dto);

        assertEquals(expectedRotulo, atualizado);
    }

    public static Stream<Arguments> fornecerCasosParaNaoAtualizados() {
        return Stream.of(
                Arguments.of("Todos os campos vazios", "", "", 0.0, ""),
                Arguments.of("Todos os ampos nulos", null, null, 0.0, null),
                Arguments.of("Campos mistos vazios/nulos", "", null, 0.0, ""),
                Arguments.of("Campos com apenas espaços", "    ", "    ", 0.0, "     ")
        );
    }

    @ParameterizedTest(name = "Cenário {index}: {0}")
    @MethodSource("fornecerCasosParaNaoAtualizados")
    void testNaoAtualizarCamposQuandoEntradasInvalidas (
            String cenario, String nome, String estilo, double teorAlcoolico, String cervejaria
    ) {
        RotuloDTO dto = new RotuloDTO();
        dto.setNome(nome);
        dto.setEstilo(estilo);
        dto.setTeorAlcoolico(teorAlcoolico);
        dto.setCervejaria(cervejaria);

        Rotulo existente = new Rotulo();
        existente.setId(2);
        existente.setNome("Antigo");
        existente.setEstilo("Larger");
        existente.setTeorAlcoolico(4.5);
        existente.setCervejaria("Farrapos");
        existente.setDataCadastro(LocalDate.now());

        Rotulo expectedRotulo = new Rotulo();
        expectedRotulo.setId(2);
        expectedRotulo.setNome(existente.getNome());
        expectedRotulo.setEstilo(existente.getEstilo());
        expectedRotulo.setTeorAlcoolico(existente.getTeorAlcoolico());
        expectedRotulo.setCervejaria(existente.getCervejaria());
        expectedRotulo.setDataCadastro(LocalDate.now());

        when(repository.findById(2)).thenReturn(Optional.of(existente));
        doNothing().when(repository).update(expectedRotulo);

        Rotulo atualizado = service.atualizar(2, dto);

        assertEquals(expectedRotulo, atualizado);
    }

    @Test
    void testDarErroQuandoRotuloNaoExistente() {
        RotuloDTO dto = new RotuloDTO();
        dto.setNome("Fake");
        dto.setEstilo("Fake");
        dto.setTeorAlcoolico(1);
        dto.setCervejaria("fake");

        when(repository.findById(999)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundResponse.class, () ->
                service.atualizar(999, dto));

        assertEquals("Rotulo Não encontrado!", exception.getMessage());
    }

    @Test
    void testListarRotulos() {

        List<Rotulo> rotuloEsperados = List.of(
                new Rotulo(1, "IPA", "American IPA", 6.3, "Cervejaria x", LocalDate.now()),
                new Rotulo(2, "Stout", "American Stout", 8.3, "Cervejaria y", LocalDate.now())
        );

        when(repository.findAll()).thenReturn(rotuloEsperados);

        var resultado = service.listar();

        assertEquals(rotuloEsperados, resultado);

        verify(repository).findAll();
    }

    @Test
    void testBuscarPorIdExistente() {
        var idExistente = 1;
        var rotuloEsperado = new Rotulo(idExistente, "IPA", "American IPA", 6.3, "Cervejaria x", LocalDate.now());

        when(repository.findById(idExistente)).thenReturn(Optional.of(rotuloEsperado));

        var resultado = service.buscarPorId(idExistente);

        assertEquals(rotuloEsperado, resultado);

        verify(repository,times(1)).findById(anyInt()); // times(1) é opcional porque o padrao é 1
    }

    @Test
    void testBuscarPorIdNaoExistente() {
        var idInexistente = 999;

        when(repository.findById(idInexistente)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundResponse.class, () ->
                service.buscarPorId(idInexistente));

        assertEquals("Rotulo Não encontrado!", exception.getMessage());

        verify(repository).findById(anyInt()); // times(1) é opcional porque o padrao é 1
    }

    @Test
    void testDeletarRotulos() {
        var expectedId = 1;

        doNothing().when(repository).delete(expectedId);

        service.deletar(expectedId);

        verify(repository).delete(anyInt());
    }

}
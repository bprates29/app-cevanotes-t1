package br.com.cevanotes.service;

import br.com.cevanotes.dto.RotuloDTO;
import br.com.cevanotes.model.Rotulo;
import br.com.cevanotes.repository.RotuloRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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

        Rotulo novoRotulo = new Rotulo();
        novoRotulo.setId(2);
        novoRotulo.setNome(dto.getNome());
        novoRotulo.setEstilo(dto.getEstilo());
        novoRotulo.setTeorAlcoolico(dto.getTeorAlcoolico());
        novoRotulo.setCervejaria(dto.getCervejaria());
        novoRotulo.setDataCadastro(LocalDate.now());

        when(repository.findById(2)).thenReturn(Optional.of(existente));
        doNothing().when(repository).update(novoRotulo);

        Rotulo atulizado = service.atualizar(2, dto);

        assertEquals(dto.getNome(), atulizado.getNome());
        assertEquals(dto.getEstilo(), atulizado.getEstilo());
        assertEquals(dto.getTeorAlcoolico(), atulizado.getTeorAlcoolico());
        assertEquals(dto.getCervejaria(), atulizado.getCervejaria());
    }

}
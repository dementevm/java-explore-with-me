package ru.practicum.ewm.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.exception.ObjectNotFoundException;

import java.util.List;

@Service
@AllArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;

    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepository.findAll(pageable)
                .stream()
                .map(compilationMapper::toCompilationDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilationsByPinned(boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return compilationRepository.findByPinned(pinned, pageable)
                .stream()
                .map(compilationMapper::toCompilationDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CompilationDto getCompilationById(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new ObjectNotFoundException("Compilation with id %d not found".formatted(compilationId)));
        return compilationMapper.toCompilationDto(compilation);
    }

    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation saved = compilationRepository.save(compilationMapper.toCompilation(newCompilationDto));
        return compilationMapper.toCompilationDto(saved);
    }

    @Transactional
    public void deleteCompilationById(Long compilationId) {
        if (!compilationRepository.existsById(compilationId)) {
            throw new ObjectNotFoundException("Compilation with id %d not found".formatted(compilationId));
        }
        compilationRepository.deleteById(compilationId);
    }

    @Transactional
    public CompilationDto updateCompilation(Long compilationId, UpdateCompilationDto dto) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new ObjectNotFoundException("Compilation with id %d not found".formatted(compilationId)));

        compilationMapper.updateCompilationFromDto(dto, compilation);

        Compilation saved = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(saved);
    }
}

package io.github.oseongryu.api.redis.service;

import io.github.oseongryu.api.redis.domain.Info;
import io.github.oseongryu.api.redis.domain.InfoRepository;
import io.github.oseongryu.api.redis.dto.InfoDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InfoService {

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<InfoDto.Response> select(InfoDto.Save dto) {
        List<Info> result = infoRepository.findAll();
        List<InfoDto.Response> content = result.stream()
                .map(this::toResponse).collect(Collectors.toList());
        return content;
    }

    public InfoDto.Response insert(InfoDto.Save dto) throws IOException {

        Info model = modelMapper.map(dto, Info.class);
        Info result = infoRepository.save(model);
        return toResponse(result);
    }

    private InfoDto.Response toResponse(Info entity) {
        return modelMapper.map(entity, InfoDto.Response.class);
    }
}

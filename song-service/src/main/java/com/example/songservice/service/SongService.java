package com.example.songservice.service;

import com.example.songservice.model.Song;
import com.example.songservice.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;

    public Song saveSong(Song song) {
        try {
            return songRepository.save(song);
        } catch (Exception e) {
            log.error("Exception occurred while saving song: ", e);
            throw e;
        }
    }

    public Song getSongById(Integer id) {
        return songRepository.findById(id).orElse(null);
    }

    public void deleteSongs(List<Integer> ids) {
        ids.forEach(songRepository::deleteById);
    }
}

package com.example.songservice.service;

import com.example.songservice.model.Song;
import com.example.songservice.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;

    public Song saveSong(Song song) {
        return songRepository.save(song);
    }

    public Song getSongById(Integer id) {
        return songRepository.findById(id).orElse(null);
    }

    public void deleteSongs(List<Integer> ids) {
        ids.forEach(songRepository::deleteById);
    }
}

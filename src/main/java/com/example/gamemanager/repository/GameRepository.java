package com.example.gamemanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.gamemanager.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByTitleContainingIgnoreCaseOrPublisherContainingIgnoreCase(String keyword1, String keyword2);
    List<Game> findByGenreIgnoreCase(String genre);
    List<Game> findByGenreIgnoreCaseAndTitleContainingIgnoreCaseOrPublisherContainingIgnoreCase(
        String genre, String keyword1, String keyword2);
}

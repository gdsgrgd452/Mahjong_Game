package com.example.mahjong_game.service;

import com.example.mahjong_game.exception.GetFromDatabaseFailedException;
import com.example.mahjong_game.exception.TileCreationFailedException;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Chow;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.repository.ChowRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChowService {

    private final ChowRepository chowRepository; //The chow repository for interacting with the database
    public ChowService(ChowRepository chowRepository) { //Initialises the user Repository
        this.chowRepository = chowRepository;
    }

    public Chow createChow() {
        try {
            Chow chow = new Chow();
            chow = chowRepository.save(chow);
            return chow;
        } catch (Exception e) {
            throw new TileCreationFailedException("Failed to create chow: ", e);
        }
    }

    public void deleteChow(Integer chowId) {
        chowRepository.delete(findChowById(chowId));
    }

    public Chow findChowById(Integer chowId) {
        return chowRepository.findById(chowId).orElseThrow(() -> new GetFromDatabaseFailedException("Chow not found with Id: " + chowId));
    }

    public Chow addTileToChow(Integer chowId, Tile tile) {
        Chow chow = findChowById(chowId);
        chow.addTile(tile);
        chowRepository.save(chow);
        return chow;
    }

    public void removeTileFromChow(Integer chowId, Tile tile) {
        Chow chow = findChowById(chowId);
        chow.removeTile(tile);
        chowRepository.save(chow);
    }

    public boolean lookInPlayerForChowWithSameTiles(Player player, List<Tile> chowTiles) {
        List<Chow> existingChowsWithPlayer = chowRepository.findAllByPlayer(player);
        for (Chow existingChow : existingChowsWithPlayer) {
            boolean matchFound = true;
            List<Tile> existingTiles = existingChow.getTiles();
            for (int i = 0; i < 3; i++) {
                if (existingTiles.get(i).getTileId() != chowTiles.get(i).getTileId()) {
                    matchFound = false;
                    break; // This chow does not match as any of the elements are diff
                }
            }
            if (matchFound) {
                return true; // Found duplicate chow
            }
        }
        return false;
    }

    public Chow saveChow(Integer chowId) {
        try {
            Chow chow = findChowById(chowId);
            chow = chowRepository.save(chow);
            return chow;
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to save an existing chow", e);
        }
    }
}

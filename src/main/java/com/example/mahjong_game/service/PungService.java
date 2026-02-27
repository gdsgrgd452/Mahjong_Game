package com.example.mahjong_game.service;

import com.example.mahjong_game.exception.GetFromDatabaseFailedException;
import com.example.mahjong_game.exception.TileCreationFailedException;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Pung;
import com.example.mahjong_game.model.tiles.Tile;
import com.example.mahjong_game.repository.PungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PungService {

    @Autowired
    PlayerService playerService;

    private final PungRepository pungRepository; //The pung repository for interacting with the database
    public PungService(PungRepository pungRepository) { //Initialises the user Repository
        this.pungRepository = pungRepository;
    }

    public Pung createPung() {
        try {
            Pung pung = new Pung();
            pung = pungRepository.save(pung);
            return pung;
        } catch (Exception e) {
            throw new TileCreationFailedException("Failed to create pung: ", e);
        }
    }

    public void deletePung(Integer pungId) {
        pungRepository.delete(findPungById(pungId));
    }

    public Pung findPungById(Integer pungId) {
        return pungRepository.findById(pungId).orElseThrow(() -> new GetFromDatabaseFailedException("Pung not found with Id: " + pungId));
    }

    public Pung addTileToPung(Integer pungId, Tile tile) {
        Pung pung = findPungById(pungId);
        pung.addTile(tile);
        pungRepository.save(pung);
        return pung;
    }

    public void removePungFromPlayer(Pung pung) {
        Player player = pung.getPlayer();
        if (player != null) {
            playerService.removePungFromPlayer(player.getPlayerId(), pung);
        }
        pung.setPlayer(null);
        pungRepository.save(pung);
    }

    public void removeTileFromPung(Integer pungId, Tile tile) {
        Pung pung = findPungById(pungId);
        pung.removeTile(tile);
        pungRepository.save(pung);
    }

    public boolean lookInPlayerForPungWithSameTiles(Player player, List<Tile> pungTiles) {
        List<Pung> existingPungsWithPlayer = pungRepository.findAllByPlayer(player);
        for (Pung existingPung : existingPungsWithPlayer) {
            boolean matchFound = true;
            List<Tile> existingTiles = existingPung.getTiles();
            for (int i = 0; i < 3; i++) {
                if (existingTiles.get(i).getTileId() != pungTiles.get(i).getTileId()) {
                    matchFound = false;
                    break; // This pung does not match as any of the elements are diff
                }
            }
            if (matchFound) {
                return true; // Found duplicate pung
            }
        }
        return false;
    }

    public Pung savePung(Integer pungId) {
        try {
            Pung pung = findPungById(pungId);
            pung = pungRepository.save(pung);
            return pung;
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to save an existing pung", e);
        }
    }
}

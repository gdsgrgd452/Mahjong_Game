package com.example.mahjong_game.service;

import com.example.mahjong_game.exception.ActionFailedException;
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

    private final PlayerService playerService;
    private final PungRepository pungRepository; //The pung repository for interacting with the database

    @Autowired
    public PungService(PlayerService playerService, PungRepository pungRepository) { //Initialises the user Repository
        this.playerService = playerService;
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
        try {
            pungRepository.delete(findPungById(pungId));
        } catch (Exception e) {
            throw new ActionFailedException("Failed to delete pung with ID: " + pungId, e);
        }
    }

    public Pung findPungById(Integer pungId) {
        try {
            return pungRepository.findById(pungId).orElseThrow(() -> new GetFromDatabaseFailedException("Pung not found with Id: " + pungId));
        } catch (GetFromDatabaseFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to find pung with ID: " + pungId, e);
        }
    }

    public Pung addTileToPung(Integer pungId, Tile tile) {
        try {
            Pung pung = findPungById(pungId);
            pung.addTile(tile);
            pungRepository.save(pung);
            return pung;
        } catch (Exception e) {
            throw new ActionFailedException("Failed to add tile to pung", e);
        }
    }

    public void removePungFromPlayer(Pung pung) {
        try {
            Player player = pung.getPlayer();
            if (player != null) {
                playerService.removePungFromPlayer(player.getPlayerId(), pung);
            }
            pung.setPlayer(null);
            pungRepository.save(pung);
        } catch (Exception e) {
            throw new ActionFailedException("Failed to remove pung from player", e);
        }
    }

    public void removeTileFromPung(Integer pungId, Tile tile) {
        try {
            Pung pung = findPungById(pungId);
            pung.removeTile(tile);
            pungRepository.save(pung);
        } catch (Exception e) {
            throw new ActionFailedException("Failed to remove tile from pung", e);
        }
    }

    public boolean lookInPlayerForPungWithSameTiles(Player player, List<Tile> pungTiles) {
        try {
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
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to look for duplicate pungs for player: " + player.getUsername(), e);
        }
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

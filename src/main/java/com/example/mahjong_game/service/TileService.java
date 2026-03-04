package com.example.mahjong_game.service;

import com.example.mahjong_game.exception.ActionFailedException;
import com.example.mahjong_game.exception.GameCreationFailedException;
import com.example.mahjong_game.exception.GetFromDatabaseFailedException;
import com.example.mahjong_game.exception.TileCreationFailedException;
import com.example.mahjong_game.model.Game;
import com.example.mahjong_game.model.Player;
import com.example.mahjong_game.model.actions.Chow;
import com.example.mahjong_game.model.actions.Pung;
import com.example.mahjong_game.model.tiles.*;
import com.example.mahjong_game.repository.TileRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TileService {
    private final TileRepository tileRepository;
    private final PlayerService playerService;
    private final PungService pungService;
    private final ChowService chowService;

    public TileService(TileRepository tileRepository, PlayerService playerService, PungService pungService, ChowService chowService) { //Initialises the tile Repository
        this.tileRepository = tileRepository;
        this.playerService = playerService;
        this.pungService = pungService;
        this.chowService = chowService;
    }

    /**
     * Creates a tile of a certain type
     * @deprecated should add something to set path for image
     */
    public Tile createTile(String type, String suit, Integer number, Game game) {
        try {
            Tile tile = null;
            switch(type) {
                case "Suited":
                    SuitedTile suited = new SuitedTile();
                    suited.setSuit(suit);
                    suited.setNumber(number);
                    tile = suited;
                    break;

                case "Honor":
                    HonorTile honor = new HonorTile();
                    honor.setSuit(suit);
                    tile = honor;
                    break;

                case "Flower":
                    FlowerTile flower = new FlowerTile();
                    flower.setSuit(suit);
                    flower.setNumber(number);
                    tile = flower;
                    break;

                default:
                    throw new TileCreationFailedException("Unknown tile type: " + type);
            }
            tile.setGame(game);
            tileRepository.save(tile);
            return tile;

        } catch (Exception e) {
            throw new TileCreationFailedException("Failed to create tile of type: " + type, e);
        }
    }

    public void saveTile(Tile tile) {
        try {
            tileRepository.save(tile);
        } catch (Exception e) {
            throw new GameCreationFailedException("Failed to save an existing tile", e);
        }
    }

    public void placeTile(Tile tile) {
        try {
            tile.setPlaced(true);
            tileRepository.save(tile);
        } catch (Exception e) {
            throw new TileCreationFailedException("Failed to place tile: ", e);
        }
    }

    public void removeFromPung(Tile tile) {
        try {
            Pung pung = tile.getPung();
            if (pung != null) {
                pungService.removeTileFromPung(pung.getPungId(), tile);
            }
            tile.setPung(null);
            tileRepository.save(tile);
        } catch (Exception e) {
            throw new ActionFailedException("Failed to remove tile from pung", e);
        }
    }

    public void removeFromChow(Tile tile) {
        try {
            Chow chow = tile.getChow();
            if (chow != null) {
                chowService.removeTileFromChow(chow.getChowId(), tile);
            }
            tile.setChow(null);
            tileRepository.save(tile);
        } catch (Exception e) {
            throw new ActionFailedException("Failed to remove tile from chow", e);
        }
    }

    public void discardTile(Tile tile) {
        try {
            Player player = tile.getPlayer();
            tile.setDiscarded(true);
            if (player != null) {
                playerService.removeTileFromHand(player.getPlayerId(), tile);
            }
            tile.setPlayer(null);
            tile.setJustDiscarded(true);
            tileRepository.save(tile);
        } catch (Exception e) {
            throw new TileCreationFailedException("Failed to discard tile: ", e);
        }
    }

    public Tile findTileById(Integer tileId) {
        try {
            return tileRepository.findById(tileId).orElseThrow(() -> new GetFromDatabaseFailedException("Tile not found with ID: " + tileId));
        } catch (GetFromDatabaseFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to find tile by ID: " + tileId, e);
        }
    }

    public List<Tile> findAllTilesByGame(Game game) {
        try {
            return tileRepository.getAllByGame(game);
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to get all tiles from game id: ", e);
        }
    }

    public List<Tile> getLiveTiles(Game game) {
        try {
            return tileRepository.findAllByGameAndPlayerIsNullAndDiscardedAndJustDiscarded(game, false, false);
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to get un-owned tiles from game id: ", e);
        }
    }

    public List<Tile> getDiscardedTiles(Game game) {
        try {
            return tileRepository.findAllByGameAndPlayerIsNullAndDiscardedAndJustDiscarded(game, true, false);
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to get un-owned and discarded tiles from game id: ", e);
        }
    }

    public Tile getJustDiscardedTile(Game game) {
        try {
            return tileRepository.findFirstByGameAndPlayerIsNullAndDiscardedAndJustDiscarded(game, true, true);
        } catch (Exception e) {
            throw new GetFromDatabaseFailedException("Failed to get un-owned and just discarded tiles from game id: ", e);
        }
    }
}


